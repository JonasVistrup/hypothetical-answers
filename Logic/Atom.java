import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Atom implements Comparable<Atom>{
    Predicate predicate;
    List<Term> args;
    Temporal temporal;
    Map<Integer, Atom> instances;

    Atom(Predicate predicate, List<Term> args, Temporal temporal){
        if(predicate.nArgs != args.size()) throw new IllegalArgumentException("Number of arguments does not match predicate");
        this.predicate = predicate;
        this.args = args;
        this.temporal = temporal;
        this.instances = new HashMap<>();
    }

    Atom(Atom parent, int version){
        this.predicate = parent.predicate;
        this.args = new ArrayList<>();
        for(Term t: parent.args){
            this.args.add(t.getVariant(version));
        }
        this.temporal = (Temporal) parent.temporal.getVariant(version);
        this.instances = new HashMap<>();
    }



    public Atom getInstance(int version){
        if(this.instances.containsKey(version)){
            return this.instances.get(version);
        }else{
            Atom instance = new Atom(this, version);
            this.instances.put(version, instance);
            return instance;
        }
    }

    public Atom applySub(Substitution substitution){
        List<Term> new_terms = new ArrayList<>();
        for(Term t: args){
            new_terms.add(t.applySub(substitution));
        }
        return new Atom(this.predicate, new_terms, (Temporal) this.temporal.applySub(substitution));
    }



    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(predicate.toString());
        builder.append('(');
        for(Term t: args){
            builder.append(t.toString());
            builder.append(',');
        }
        if(temporal.tVar != null) {
            builder.append(temporal.tVar.toString());
            if (temporal.tConstant > 0) {
                builder.append("+");
                builder.append(temporal.tConstant);
            } else if (temporal.tConstant < 0) {
                builder.append(temporal.tConstant);
            }
        }else{
            builder.append(temporal.tConstant);
        }

        builder.append(')');
        return builder.toString();
    }

    @Override
    public int compareTo(@NotNull Atom o) {
        return this.temporal.compareTo(o.temporal);
    }
}
