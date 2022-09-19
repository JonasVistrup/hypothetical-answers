import java.util.ArrayList;
import java.util.List;

public class AtomInstance {
    Atom original;
    int version;

    List<TermInstance> argsInst;
    TemporalInstance temporal;

    AtomInstance(Atom original, int version){
        this.original = original;
        this.version = version;

        this.argsInst = new ArrayList<>();
        for(Term t: original.args){
            this.argsInst.add(t.getVariant(version));
        }
        this.temporal = (TemporalInstance) original.temporal.getVariant(version);
    }


    @Override
    public String toString() {
        return original.toString();
    }
}
