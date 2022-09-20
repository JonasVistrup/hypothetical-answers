public class Predicate {
    String id;
    int nArgs;

    public boolean IDB;

    /**
     * Constructor for predicate class.
     * @param id    String represenation of predicate
     * @param nArgs Number of non-temporal arguments
     */
    public Predicate(String id, int nArgs){
        this.id = id;
        this.nArgs = nArgs;
        this.IDB = false;
    }

    @Override
    public String toString() {
        return id;
    }

    public boolean isIDB(){
        return IDB;
    }
}
