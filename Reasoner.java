import java.util.List;
//TODO
public class Reasoner {
    private Program program;
    private Atom query;
    private int time;

    private List<HypAnswer> hypAnswers;

    /**
     * Constructor.
     * @param program   A program created by a program constructor.
     * @param query     A query for which the OLD_interfaced_based.Reasoner give (hypothetical) answers.
     */
    public Reasoner(Program program, Atom query){
        this.program = program;
        this.query = query;
        this.time = -1;

        assert program.isIDB(query.predicate());

        hypAnswers = SLDResolution.preprocess(this.query, this.program);
    }

    /** TODO Complete this.
     * Changing the query resets the time and therefore also the timestream.
     * @param query The new query for the program.
     */
    public void changeQuery(Atom query) {
        assert program.isIDB(query.predicate());
        this.query = query;
        this.time = -1;

    }

    public HypAnswer nextTime(List<Atom> data_slice) {
        return null;
    }
}

