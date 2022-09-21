public class Sub {
    public final Variable from;
    public final Term to;

    public Sub(Variable from, Term to){
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return from.toString()+"/"+to.toString();
    }
}
