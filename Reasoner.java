import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO
public class Reasoner {
    private Program program;
    private Atom query;
    private int time;

    private List<HypAnswer> hypAnswers;

    /**
     * Constructor.
     *
     * @param program A program created by a program constructor.
     * @param query   A query for which the OLD_interfaced_based.Reasoner give (hypothetical) answers.
     */
    public Reasoner(Program program, Atom query) {
        this.program = program;
        this.query = query;
        this.time = -1;

        assert program.isIDB(query.predicate());

        this.hypAnswers = SLDResolution.preprocess(this.query, this.program);
    }

    /**
     * //TODO Complete this.
     * Changing the query resets the time and therefore also the timestream.
     *
     * @param query The new query for the program.
     */
    public void changeQuery(Atom query) {
        assert program.isIDB(query.predicate());
        this.query = query;
        this.time = -1;

    }

    //TODO
    public HypAnswer nextTime(List<Atom> dataSlice) {
        if (!legalSlice(dataSlice)) {
            throw new IllegalArgumentException("Dataslice is not allowed at time: " + this.time + ".");
        }
        this.time += 1;
        //Check constant, and then find variables
        for (HypAnswer hypAnswer : hypAnswers) {
            List<EvidenceAnswer> eAnswer = generateEvidenceFromHyp(hypAnswer, dataSlice);
            if (eAnswer == null) continue;
            //TODO
        }
        return null;
    }

    //TODO
    private List<EvidenceAnswer> generateEvidenceFromHyp(HypAnswer hypAnswer, List<Atom> dataSlice) {
        if (!hypAnswer.premiseConstant().isEmpty() && hypAnswer.premiseConstant().get(0).temporal().constant() < this.time) {
            return null; //Time constant is less than current time and cannot be satisfied
        }
        List<EvidenceAnswer> evidenceAnswers = new ArrayList<>();

        ArrayList<Atom> M = new ArrayList<>(hypAnswer.getConstantsForTime(this.time));
        Program dataSliceProgram = dataSliceToProgram(dataSlice);
        List<Substitution> constantSolutions = NormalSLDResolution.SLDResolution(new Goal(M.toArray(new Atom[0])), dataSliceProgram);
        if(constantSolutions.isEmpty()){
            return null;
        }
        List<Atom> premiseConstantWithoutM = hypAnswer.premiseConstant().stream().filter((x)->!M.contains(x)).toList();
        List<Atom> smallestPremiseTemporal = hypAnswer.getSmallestTemporal();
        for(Substitution s: constantSolutions){
            List<Atom> MSubbed = applySubToList(M, s);

            List<Atom> premiseConstantSubbed = applySubToList(premiseConstantWithoutM, s);
            List<Atom> premiseTemporalSubbed = applySubToList(hypAnswer.premiseTemporal(),s);

            evidenceAnswers.add(new EvidenceAnswer(s, MSubbed, premiseConstantSubbed, premiseTemporalSubbed)); // TODO use arraylist instead of list for clone feature

            List<Atom> smallestTSubbed = applySubToList(smallestPremiseTemporal, s);
            List<Substitution> solutions = NormalSLDResolution.SLDResolution(new Goal(smallestTSubbed.toArray(new Atom[0])), dataSliceProgram); // Prove that you can solve constants before Temporal without loss of solution.
            List<Atom> evidencePreSubbed = new ArrayList<>(MSubbed);
            evidencePreSubbed.addAll(smallestTSubbed);
            List<Atom> premiseVarWithoutE = hypAnswer.premiseTemporal().stream().filter((x)->!smallestPremiseTemporal.contains(x)).toList();
            for(Substitution ss: solutions){
                evidenceAnswers.add(new EvidenceAnswer(s, applySubToList(evidencePreSubbed, ss), applySubToList(premiseConstantSubbed,s),applySubToList(premiseVarWithoutE, Substitution.composition(s, ss)) ));
            }
        }

        return evidenceAnswers;
    }

    private static List<Atom> applySubToList(List<Atom> atoms, Substitution substitution){
        List<Atom> subbedAtoms = new ArrayList<>();
        for(Atom a: atoms){
            subbedAtoms.add(a.applySub(substitution));
        }
        return subbedAtoms;
    }

    private static Program dataSliceToProgram(List<Atom> dataSlice) {
        ArrayList<Clause> clauses = new ArrayList<>();
        for(Atom a: dataSlice){
            clauses.add(new Clause(a));
        }
        return new Program(clauses);
    }

    public boolean legalSlice(List<Atom> dataSlice) {
        return true;
    }
}

