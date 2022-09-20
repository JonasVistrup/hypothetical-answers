import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramBuilder {
    private final Map<String, Predicate> predicates = new HashMap<>();
    private final Map<String, Term> terms = new HashMap<>();
    private final Map<String, Variable> temporalVariables = new HashMap<>();

    private static final ArrayList<Clause> clauses = new ArrayList<>();


    public Program getProgram(){
        return new Program(clauses);
    }



    public void addClause(String representation) {
        representation = representation.replaceAll(" ", "");
        String[] parts = representation.split("<-");
        if (parts.length < 1 || parts.length >= 3) {
            throw new IllegalArgumentException("The clause should consist of a \"head<-body\"");
        }
        Atom head = parseAtom(parts[0]);
        if(parts.length==1){
            clauses.add(new Clause(head, new ArrayList<>()));
            return;
        }



        parts[1] = parts[1].replaceAll("\\),", ")<-");
        String[] strBody = parts[1].split("<-");

        List<Atom> body = new ArrayList<>(strBody.length);
        for (String s : strBody) {
            body.add(parseAtom(s));
        }
        head.predicate.IDB = true;
        clauses.add(new Clause(head, body));
    }

    public Atom parseAtom(String atomRep) {
        atomRep = atomRep.replaceAll(" ", "");
        String[] strArguments;
        if (atomRep.length() == 0) {
            throw new IllegalArgumentException("Atom representation must not be empty");
        }
        String[] parts = atomRep.split("\\(");
        int numberOfArgs = 0;
        if (parts.length >= 2) {
            if (!parts[1].contains(")"))
                throw new IllegalArgumentException("( must end with a )");
            strArguments = parts[1].split("\\)");
            strArguments = strArguments[0].split(",");
            numberOfArgs = strArguments.length - 1;
        } else {
            throw new IllegalArgumentException("All predicates must contain a temporal argument");
        }

        Predicate p = getPredicate(parts[0], numberOfArgs);


        // Get terms arguments
        List<Term> arguments = new ArrayList<>(numberOfArgs);
        for (String s: strArguments) {
            arguments.add(getTerm(s));
        }

        // Get temporal argument
        Temporal temporal = getTemporal(strArguments[numberOfArgs]);
        return new Atom(p,arguments, temporal);
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

        if(temporalParts.length>2) throw new IllegalArgumentException("Temporal argument must only contain a single + or a single -");
        if(!isInteger(temporalParts[1])) throw new IllegalArgumentException("Temporal constant is not a number");
        return new Temporal(getTemporalVariable(temporalParts[0]), Integer.parseInt(temporalParts[1])*constantMultiplier);
    }

    private Variable getTemporalVariable(String tempVar){
        if(temporalVariables.containsKey(tempVar)){
            return temporalVariables.get(tempVar);
        }else{
            if(!tempVar.toUpperCase().equals(tempVar)) throw new IllegalArgumentException("Temporal variables must be uppercase");

            Variable v = new Variable(tempVar);
            temporalVariables.put(tempVar, v);
            return v;
        }
    }

    private boolean isInteger(String s){
        try {
            int i = Integer.parseInt(s);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    private Term getTerm(String name) {
        if (terms.containsKey(name)) {
            return terms.get(name);
        } else {
            if (name.toUpperCase().equals(name)) {
                Variable var = new Variable(name);
                terms.put(name, var);
                return var;
            } else if (name.toLowerCase().equals(name)) {
                Constant constant = new Constant(name);
                terms.put(name, constant);
                return constant;
            } else {
                throw new IllegalArgumentException("Terms must either be all uppercase for variables or all lowercase for constants");
            }
        }
    }

    private Predicate getPredicate(String name, int numberOfArgs) {
        if (numberOfArgs < 0) {
            throw new IllegalArgumentException("Only a non-negative amount of arguments allowed");
        }
        Predicate res;
        if (predicates.containsKey(name)) {
            res = predicates.get(name);
            if (res.nArgs != numberOfArgs) {
                throw new IllegalArgumentException("Predicate " + name + " contains an inconsistent of arguments");
            }
        } else {
            res = new Predicate(name, numberOfArgs);
            predicates.put(name, res);
        }
        return res;
    }

}
