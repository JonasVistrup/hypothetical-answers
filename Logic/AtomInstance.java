import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AtomInstance implements Comparable<AtomInstance> {
    Predicate predicate;
    int version;
    List<TermInstance> argsInst;
    TemporalInstance temporal;

    AtomInstance(Atom original, int version){
        this.predicate = original.predicate;
        this.argsInst = new ArrayList<>();
        for(Term t: original.args){
            this.argsInst.add(t.getVariant(version));
        }
        this.temporal = (TemporalInstance) original.temporal.getVariant(version);
        this.version = version;
    }

    AtomInstance(Predicate predicate, List<TermInstance> argsInst, TemporalInstance temporal, int version){
        this.predicate = predicate;
        this.argsInst = argsInst;
        this.temporal = temporal;
        this.version = version;
    }

    public AtomInstance applySub(Substitution substitution){
        List<TermInstance> new_terms = new ArrayList<>();
        for(TermInstance t: argsInst){
            new_terms.add(t.applySub(substitution));
        }
        return new AtomInstance(this.predicate, new_terms, (TemporalInstance) this.temporal.applySub(substitution), this.version);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(predicate.toString());
        builder.append('(');
        for(TermInstance t: argsInst){
            builder.append(t.toString());
            builder.append(',');
        }
        if(temporal.tVarInst != null) {
            builder.append(temporal.tVarInst.toString());
            if (temporal.constant > 0) {
                builder.append("+");
                builder.append(temporal.constant);
            } else if (temporal.constant < 0) {
                builder.append(temporal.constant);
            }
        }else{
            builder.append(temporal.constant);
        }

        builder.append(')');
        return builder.toString();
    }


    @Override
    public int compareTo(@NotNull AtomInstance o) {
        return this.temporal.compareTo(o.temporal);
    }
}
