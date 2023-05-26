package Jonas.Logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * A class which builds programs using string versions of its clauses.
 */
public class ProgramBuilder {
    private final Map<String, PredicateInterface> predicates = new HashMap<>();
    private final Map<String, Term> terms = new HashMap<>();
    private final Map<String, Variable> temporalVariables = new HashMap<>();

    private final ArrayList<Clause> clauses = new ArrayList<>();

    public ProgramBuilder(){
        addFunctionPredicates();
    }

    private void addFunctionPredicates(){
        try{
            Scanner input = new Scanner(new File("FunctionPredicates"));
            while(input.hasNextLine()){
                addUDPredicate(input.nextLine());
            }
            input.close();
        }catch (FileNotFoundException e) {
            throw new IllegalStateException("file FunctionPredicates can not be found.");
        }catch (ClassNotFoundException e){
            throw new IllegalArgumentException("Class not found.");
        }catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e){
            throw new IllegalArgumentException("Constructor not found.");
        }
    }


    private void addUDPredicate(String functionClassString) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class functionClass = Class.forName(functionClassString);
        if(UserDefinedPredicate.class.isAssignableFrom(functionClass)){
            Constructor ct = functionClass.getConstructor(new Class[0]);
            UserDefinedPredicate fp = (UserDefinedPredicate) ct.newInstance(new Object[0]);
            this.predicates.put(fp.id(),fp);
        }else{
            throw new IllegalArgumentException("Class "+functionClass.getName() + " does not implement UserDefinedPredicate.");
        }
    }

    /**
     * Returns a program consisting of the current clauses.
     * @return program made from the added clauses
     */
    public Program getProgram(){
        return new Program(clauses);
    }

    /**
     * Returns the number of clauses added.
     * @return number of clauses added.
     */
    public int size(){
        return clauses.size();
    }


    /**
     * Adds a clause to the Logic.ProgramBuilder based upon the string representation of the clause given in the format HEAD{@literal <}-BODY.
     * @param representation string representation of the clause
     */
    public void addClause(String representation) {
        representation = representation.replaceAll(" ", "");
        String[] parts = representation.split("<-");
        if (parts.length == 1 && parts[0].equals(representation)){
            String [] temp_parts = representation.split("->");
            if (temp_parts.length == 1 && temp_parts[0].equals(representation)) {
                throw new IllegalArgumentException("The clause should consist of a \"head<-body\" or  \"body->head\"");
            }
            parts = new String[temp_parts.length];
            parts[0] = temp_parts[temp_parts.length-1];
            if(parts.length > 1) {
                parts[1] = temp_parts[0];
            }
        }
        if (parts.length >= 3) {
            throw new IllegalArgumentException("The clause should consist of a \"head<-body\" or  \"body->head\"");
        }
        Atom head = parseAtom(parts[0]);
        if(parts.length==1){
            clauses.add(new Clause(head, new AtomList()));
            return;
        }



        parts[1] = parts[1].replaceAll("\\),", ")<-");
        String[] strBody = parts[1].split("<-");

        AtomList body = new AtomList();
        for (String s : strBody) {
            body.add(parseAtom(s));
        }
        if(head.predicate instanceof UserDefinedPredicate){
            throw new IllegalArgumentException("UserDefinedPredicates can not be defined by clauses.");
        }

        ((Predicate)head.predicate).IDB = true;
        clauses.add(new Clause(head, body));
    }



    /**
     * Returns an atom based upon the string representation given.
     * @param atomRep string representation
     * @return atom which the string representation corresponds to
     */
    public Atom parseAtom(String atomRep) {
        atomRep = atomRep.replaceAll(" ", "");
        String[] strArguments;
        if (atomRep.length() == 0) {
            throw new IllegalArgumentException("Atom representation must not be empty");
        }
        String[] parts = atomRep.split("\\(");
        int numberOfArgs;
        if (parts.length >= 2) {
            if (!parts[1].contains(")"))
                throw new IllegalArgumentException("( must end with a )");
            strArguments = parts[1].split("\\)");
            strArguments = strArguments[0].split(",");
            numberOfArgs = strArguments.length - 1;
        } else {
            throw new IllegalArgumentException("All predicates must contain a temporal argument");
        }

        PredicateInterface p = getPredicate(parts[0], numberOfArgs);


        // Get terms arguments
        List<Term> arguments = new ArrayList<>(numberOfArgs);
        for (int i = 0; i<numberOfArgs; i++) {
            arguments.add(getTerm(strArguments[i]));
        }


        if(p instanceof Predicate) {

            // Get temporal argument
            Temporal temporal = getTemporal(strArguments[numberOfArgs]);
            return new Atom(p,arguments, temporal);

        }else{
            arguments.add(getTerm(strArguments[numberOfArgs]));
            return new SpecialAtom(p,arguments, null);
        }

    }

    private Temporal getTemporal(String strTemporal) {
        String[] temporalParts;
        int constantMultiplier;
        if (strTemporal.contains("+")) {
            temporalParts = strTemporal.split("\\+");
            constantMultiplier = 1;

        } else if (strTemporal.contains("-")) {
            temporalParts = strTemporal.split("-");
            constantMultiplier = -1;

        } else {
            if(isInteger(strTemporal)){
                return new Temporal(null, Integer.parseInt(strTemporal));
            }else{
                return new Temporal(getTemporalVariable(strTemporal), 0);
            }
        }

        if(temporalParts.length>2) throw new IllegalArgumentException("Logic.Temporal argument must only contain a single + or a single -");
        if(!isInteger(temporalParts[1])) throw new IllegalArgumentException("Logic.Temporal constant is not a number");
        return new Temporal(getTemporalVariable(temporalParts[0]), Integer.parseInt(temporalParts[1])*constantMultiplier);
    }

    private Variable getTemporalVariable(String tempVar){
        if(temporalVariables.containsKey(tempVar)){
            return temporalVariables.get(tempVar);
        }else{
             if(!tempVar.toUpperCase().equals(tempVar)){
                throw new IllegalArgumentException("Logic.Temporal variables must be uppercase");
            }

            Variable v = new Variable(tempVar);
            temporalVariables.put(tempVar, v);
            return v;
        }
    }

    private boolean isInteger(String s){
        try {
            Integer.parseInt(s);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private Term getTerm(String name) {
        if (terms.containsKey(name)) {
            return terms.get(name);
        } else {
            if (name.toLowerCase().equals(name)) {
                Constant constant = new Constant(name);
                terms.put(name, constant);
                return constant;
            } else if(name.substring(0,1).toUpperCase().equals(name.substring(0,1))) {
                Variable var = new Variable(name);
                terms.put(name, var);
                return var;
            } else if(isInteger(name)) {
                Constant constant = new Constant(name);
                terms.put(name, constant);
                return constant;
            }else{
                throw new IllegalArgumentException("Terms must either be all uppercase for variables or all lowercase for constants");
            }
        }
    }

    private PredicateInterface getPredicate(String name, int numberOfArgs) {
        if (numberOfArgs < 0) {
            throw new IllegalArgumentException("Only a non-negative amount of arguments allowed");
        }
        PredicateInterface res;
        if (predicates.containsKey(name)) {
            res = (PredicateInterface) predicates.get(name);
            if ((res instanceof Predicate && res.nArgs() != numberOfArgs) || (res instanceof UserDefinedPredicate && res.nArgs() != numberOfArgs + 1)) {
                throw new IllegalArgumentException("Logic.Predicate " + name + " contains an inconsistent of arguments");
            }
        } else {
            res = new Predicate(name, numberOfArgs);
            predicates.put(name, res);
        }
        return res;
    }

}
