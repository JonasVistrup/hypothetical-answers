import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Clause {
    Atom head;
    List<Atom> body;

    Map<Integer, ClauseInstance> instances;

    Clause(Atom head, List<Atom> body){
        this.head = head;
        this.body = body;

        this.instances = new HashMap<>();
    }

    public ClauseInstance getInstance(int version){
        if(this.instances.containsKey(version)){
            return this.instances.get(version);
        }else{
            ClauseInstance instance = new ClauseInstance(this, version);
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
