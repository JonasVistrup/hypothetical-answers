public class TemporalInstance implements TermInstance {
    Temporal original;
    int version;

    TemporalInstance(Temporal original, int version){
        this.original = original;
        this.version = version;
    }




    @Override
    public String toString() {
        return original.toString();
    }
}
