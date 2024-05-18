package Jonas.NegationStuff;

import Jonas.Hypothetical.Answer;
import Jonas.Hypothetical.DBConnection;
import Jonas.Hypothetical.HypotheticalReasoner;
import Jonas.Hypothetical.Query;
import Jonas.Logic.*;
import Jonas.SLD.ModifiedSLDResolution;
import Jonas.SLD.SLDResolution;
import Jonas.SLD.Unify;
import Jonas.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
/*
 * This version requires all atoms in rules must contain temporal variable, which must be the same for all atoms in a rule.
 * All rules have to be safe. This has to be a requirements because we don't know all
 */
public class NegatedHypothetical {
    private final ProgramBuilder pBuilder;
    private List<Atom> queries;
    private int time;

    private HashMap<Long, Set<Answer>> P;
    private HashMap<Long, Set<Answer>> S;

    public NegatedHypothetical(){
        this.pBuilder = new ProgramBuilder();
        this.queries = new ArrayList<>();
        this.time = -1;
        this.S = new HashMap<>();
        this.P = new HashMap<>();
    }

    public NegatedHypothetical(String programPath){
        this();
        try {
            Scanner input = new Scanner(new File(programPath));
            while(input.hasNextLine()){
                addClause(input.nextLine());
            }
            input.close();

        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("Filepath is not a valid path");
        }
    }

    public void addClause(String representation){
        pBuilder.addClause(representation);
        this.queries = new ArrayList<>();
        this.time = -1;
    }

    public void query(String atomsRep){
        this.time = -1;
        atomsRep = atomsRep.replaceAll(" ", "");
        atomsRep = atomsRep.replaceAll("\\),", " ");
        String[] atomRepList = atomsRep.split(" ");
        if(atomRepList.length != 1) throw new IllegalArgumentException("Query must only contain a single atom");

        Atom query = pBuilder.parseAtom(atomRepList[0]);

        preprocess(query);
    }

    public void addQuery(Atom query){
        this.queries.add(query);
    }

    public int inQueries(Atom query){ //Check if a superset atom exist e.g. for p(A) does p(x) exists in Q
        return this.queries.indexOf(query);
    }

    public void preprocess(Atom query){
        this.addQuery(query);
        Set<Answer> preprocessingAnswers = new HashSet<>(ModifiedSLDResolution.preprocess(pBuilder.getProgram(), query));
        for(Answer answer: preprocessingAnswers){
            for(Atom n_atom: answer.premise.negated()){
                NAtom nAtom = (NAtom) n_atom;
                int index = inQueries(nAtom.getAtom());
                if(index == -1){
                    preprocess(nAtom.getAtom());
                    nAtom.setQuery(nAtom.getAtom());
                }else{
                    nAtom.setQuery(queries.get(index));
                }
            }
        }
        P.put(query.id(),preprocessingAnswers);
        S.put(query.id(),new HashSet<>());
    }

    public void nextTime(String dataSliceRep){
        if(queries.isEmpty()) throw  new IllegalStateException("query must be called before time slices can be added");

        AtomList dataSlice = pBuilder.stringToAtomList(dataSliceRep);

        this.time = getTime_andCheckFormat(dataSlice);

        Program dataSliceProgram = dataSlice.toProgram();

        HashMap<Long, Set<Answer>> S_next = new HashMap<>();
        for(Atom query: queries){
            S_next.put(query.id(), updateS(query, dataSliceProgram));
        }
        HashMap<Long, Set<Answer>> S_prev;
        do {
            S_prev = (HashMap<Long, Set<Answer>>) S_next.clone();
            step3a(S_next);
            step3b(S_next);
        }while (!S_next.equals(S_prev));

        this.S = S_next;
    }

    private void step3b(HashMap<Long, Set<Answer>> s_next) {
        for(Atom query: queries){
            s_next.put(query.id(),step3bQ(query,s_next));
        }
    }
    private Set<Answer> step3bQ(Atom query, HashMap<Long, Set<Answer>> s_next){
        Set<Answer> sq_next = new HashSet<>();
        for(Answer a: s_next.get(query.id())) {
            AtomList M = a.premise.M_minus(time);
            if (!mProven(M,s_next)) sq_next.add(a);

        }
        return sq_next;
    }

    private boolean mProven(AtomList M, HashMap<Long, Set<Answer>> s_next){
        for (Atom nL : M) {
            if(atomProven(nL,s_next.get(nL.getQuery().id()))) return true;
        }
        return false;
    }


    private boolean atomProven(Atom nL, Set<Answer> nLQAnswers){
        for(Answer a: nLQAnswers){
            if(a.premise.isEmpty() && Unify.isUnifiableDownAtomList(a.resultingQueriedAtoms, new AtomList(nL))){
                return true;
            }
        }
        return false;
    }

    private int getTime_andCheckFormat(AtomList dataslice){
        int currentTime = this.time + 1;
        if(!dataslice.isEmpty() && this.time == -1){
            currentTime = dataslice.get(0).temporal.tConstant;
        }
        for(Atom a: dataslice){
            if(a.temporal.tVar != null) throw new IllegalArgumentException("Logic.Temporal aspect of "+a.toString()+" is not initiated");
            if(a.temporal.tConstant != currentTime)  throw new IllegalArgumentException("Time constant of "+a.toString()+" is not equal to current time: "+currentTime);
        }
        return currentTime;
    }

    private Set<Answer> updateS(Atom query, Program dataSliceProgram){
        Set<Answer> Sq_next = step1(query);
        Sq_next = step2(query, Sq_next, dataSliceProgram);

        return Sq_next;
    }

    private void step3a(HashMap<Long, Set<Answer>> s_next) {
        for(Atom query: queries){
            s_next.put(query.id(),step3aQ(query,s_next));
        }
    }

    private Set<Answer> step3aQ(Atom query, HashMap<Long, Set<Answer>> s_next){
        Set<Answer> sq_next = new HashSet<>();
        for(Answer a: s_next.get(query.id())) {
            AtomList M = a.premise.M_minus(time);
            boolean negationProved = false;
            for (Atom nL : M) {
                Set<Answer> nLQAnswers = s_next.get(nL.getQuery().id());
                if(!atomStilPossible(nL,nLQAnswers)) {
                    sq_next.add(new Answer(a.resultingQueriedAtoms, a.substitution, a.evidence.plus(new AtomList(nL)), a.premise.without(new AtomList(nL)), a.clausesUsed));
                    negationProved = true;
                }
            }
            if(!negationProved) sq_next.add(a);
        }
        return sq_next;
    }

    private boolean atomStilPossible(Atom query,Set<Answer> nLQAnswers){
        for(Answer a: nLQAnswers){
            if(Unify.findMGUAtomList(new AtomList(query),a.resultingQueriedAtoms) != null){
                return true;
            }
        }
        return false;
    }


    private Set<Answer> step1(Atom query){
        Set<Answer> Sq_next = new HashSet<>();
        for(Answer answer: P.get(query.id())){
            Substitution start = getTimeSubForEarliestAtom(answer);
            Sq_next.add(answer.applySub(start));
        }
        return Sq_next;
    }

    private Substitution getTimeSubForEarliestAtom(Answer answer) {
        AtomList query = answer.resultingQueriedAtoms;
        Temporal min = query.get(0).temporal;
        for(int i = 1; i<query.size(); i++){
            Temporal current = query.get(i).temporal;
            if(current.compareTo(min) < 0) min = current;
        }

        for(int i = 0; i<answer.premise.size(); i++){
            Temporal current = answer.premise.get(i).temporal;
            if(current.compareTo(min) < 0) min = current;
        }

        if(min.tVar == null) return new Substitution();
        return new Substitution(min.tVar, new Temporal(null, this.time - min.tConstant));
    }

    private Set<Answer> step2(Atom query, Set<Answer> Sq_prelim, Program dataSliceProgram) {
        Set<Answer> Sq_next = new HashSet<>();
        for(Answer answer: Utils.concat(this.S.get(query.id()),Sq_prelim)){
            AtomList M = answer.premise.M_plus(time);
            List<Substitution> unifiers = SLDResolution.findSubstitutions(dataSliceProgram,M);
            AtomList newEvidence = answer.evidence.plus(M);
            AtomList newPremise = answer.premise.without(M);
            for(Substitution sub: unifiers){
                Sq_next.add(new Answer(answer.resultingQueriedAtoms.applySub(sub),answer.substitution.add(sub),newEvidence.applySub(sub), newPremise.applySub(sub), answer.clausesUsed.applySub(sub)));
            }
        }

        return Sq_next;
    }


    public List<Answer> supportedAnswers() {
        return new ArrayList<>(this.S.get(queries.get(0).id()));
    }

    public Atom getQuery() {
        return queries.get(0);
    }

    public List<Answer> preprocessAnswers() {
        return new ArrayList<>(this.P.get(queries.get(0).id()));
    }
}
