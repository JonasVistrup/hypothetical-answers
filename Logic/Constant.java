public class Constant implements Term{

    String id;
    public Constant(String id){
        this.id = id;
    }

    public Term getVariant(int version){
        return this;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public Term applySub(Substitution substitution) {
        return this;
    }
}
