public class Predicate {
    String id;
    int nArgs;

    /**
     * Constructor for predicate class.
     * @param id    String represenation of predicate
     * @param nArgs Number of non-temporal arguments
     */
    public Predicate(String id, int nArgs){
        this.id = id;
        this.nArgs = nArgs;
    }

    @Override
    public String toString() {
        return id;
    }
}
