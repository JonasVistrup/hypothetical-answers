import org.jetbrains.annotations.NotNull;

public class TemporalInstance implements TermInstance, Comparable<TemporalInstance> {

    VariableInstance tVarInst;
    int constant;
    int version;

    TemporalInstance(VariableInstance tVarInst, int constant,  int version){
        this.tVarInst = tVarInst;
        this.constant = constant;
        this.version = version;
    }

    @Override
    public TermInstance applySub(Substitution substitution) {
        TermInstance to_term = substitution.getSubstitution(this.tVarInst);
        if(to_term != null){
            assert to_term instanceof TemporalInstance;
            TemporalInstance to = (TemporalInstance) to_term;
            return new TemporalInstance(to.tVarInst,to.constant+this.constant , to.version);
        }
        return this;
    }
    @Override
    public String toString() {
        if(this.tVarInst == null) {
            return "" + this.constant;
        }

        StringBuilder b = new StringBuilder(this.tVarInst.toString());
        if(this.constant>=0){
            b.append("+");
        }
        b.append(this.constant);
        return b.toString();
    }

    @Override
    public int compareTo(@NotNull TemporalInstance o) {
        if(this.tVarInst != o.tVarInst) throw new IllegalArgumentException("Not comparable");
        return this.constant - o.constant;
    }
}
