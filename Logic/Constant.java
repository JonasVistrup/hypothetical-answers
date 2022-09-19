public class Constant implements Term, TermInstance{

    String id;
    public Constant(String id){
        this.id = id;
    }

    public TermInstance getVariant(int version){
        return this;
    }

    @Override
    public String toString() {
        return id;
    }
}
