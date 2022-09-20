import java.util.ArrayList;
import java.util.List;

public class ClauseInstance {
    Clause original;
    int version;
    AtomInstance head;
    List<AtomInstance> body;

    ClauseInstance(Clause original, int version){
        this.original = original;
        this.version = version;

        this.head = original.head.getInstance(version);
        this.body = new ArrayList<>();
        for(Atom a: original.body){
            this.body.add(a.getInstance(version));
        }
    }



    @Override
    public String toString() {
        return original.toString();
    }
}
