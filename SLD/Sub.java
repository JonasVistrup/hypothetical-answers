public class Sub {
    public final VariableInstance from;
    public final TermInstance to;

    public Sub(VariableInstance from, TermInstance to){
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return from.toString()+"/"+to.toString();
    }
}
