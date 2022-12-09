package Jonas.Logic;

/**
 * A logical predicate.
 */
public class Predicate implements PredicateInterface{


    /**
     * String representation of predicate.
     */
    private final String id;
    /**
     * Number of non-temporal arguments of the predicate.
     */
    final int nArgs;

    /**
     * Indicator for wheter or not this predicate is an intensional (true) or extensional (false).
     */
    public boolean IDB;

    /**
     * Constructs a predicate with signature id and nArgs number of non-temporal arguments.
     * @param id    String representation of predicate
     * @param nArgs number of non-temporal arguments
     */
    public Predicate(String id, int nArgs){
        this.id = id;
        this.nArgs = nArgs;
        this.IDB = false;
    }

    public int nArgs(){
        return nArgs;
    }

    public boolean IDB(){
        return IDB;
    }

    /**
     * Returns the String representation of this predicate.
     * @return id
     */
    @Override
    public String toString() {
        return id;
    }

}
