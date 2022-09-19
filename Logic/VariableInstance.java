public class VariableInstance implements TermInstance{
    int version;
    Variable original;

    public VariableInstance(Variable original, int version){
        this.original = original;
        this.version = version;
    }



    @Override
    public String toString() {
        return original.toString();
    }
}
