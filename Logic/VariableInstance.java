public class VariableInstance implements TermInstance{
    int version;
    String id;

    public VariableInstance(String id, int version){
        this.id = id;
        this.version = version;
    }


    @Override
    public TermInstance applySub(Substitution substitution) {
        TermInstance t = substitution.getSubstitution(this);
        if(t != null){
            return t;
        }
        return this;
    }

    @Override
    public String toString() {
        return id;
    }
}
