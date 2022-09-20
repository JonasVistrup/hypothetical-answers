import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Atom {
    Predicate predicate;
    List<Term> args;
    Temporal temporal;
    Map<Integer, AtomInstance> instances;

    Atom(Predicate predicate, List<Term> args, Temporal temporal){
        if(predicate.nArgs != args.size()) throw new IllegalArgumentException("Number of arguments does not match predicate");
        this.predicate = predicate;
        this.args = args;
        this.temporal = temporal;
        this.instances = new HashMap<>();
    }

    public AtomInstance getInstance(int version){
        if(this.instances.containsKey(version)){
            return this.instances.get(version);
        }else{
            AtomInstance instance = new AtomInstance(this, version);
            this.instances.put(version, instance);
            return instance;
        }
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
}
