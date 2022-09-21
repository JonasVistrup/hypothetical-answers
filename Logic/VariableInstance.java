public class VariableInstance implements TermInstance{
    String id;

    public VariableInstance(String id){
        this.id = id;
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
