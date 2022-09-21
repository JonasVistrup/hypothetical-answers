import java.util.ArrayList;
import java.util.List;

public class ClauseInstance {

    AtomInstance head;
    AtomList body;

    ClauseInstance(Clause original, int version){
        this.head = original.head.getInstance(version);
        this.body = new AtomList();
        for(Atom a: original.body){
            this.body.add(a.getInstance(version));
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
        for (AtomInstance atom : body) {
            builder.append(atom.toString());
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
