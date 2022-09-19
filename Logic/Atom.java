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
}
