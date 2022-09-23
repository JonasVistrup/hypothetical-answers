import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Clause {
    Atom head;
    AtomList body;

    Map<Integer, Clause> instances;

    Clause(Atom head, AtomList body){
        this.head = head;
        this.body = body;

        this.instances = new HashMap<>();
    }

    public Clause(Clause clause, int version){
        this.head = clause.head.getInstance(version);
        this.body = new AtomList();
        for(Atom a: clause.body){
            this.body.add(a.getInstance(version));
        }
    }

    public Clause getInstance(int version){
        if(this.instances.containsKey(version)){
            return this.instances.get(version);
        }else{
            Clause instance = new Clause(this, version);
            this.instances.put(version, instance);
            return instance;
        }
    }



    @Override
    public String toString() {
        if (body.isEmpty()) {
            return head.toString() + "<-";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(head.toString());
        builder.append("<-");
        for (Atom atom : body) {
            builder.append(atom.toString());
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
