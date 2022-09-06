import java.util.ArrayList;
import java.util.List;


public class HypAnswer {
    private final Substitution substitution;
    private final List<Atom> premise = new ArrayList<>();
    public HypAnswer(Substitution substitution, List<Atom> edbAtoms){
        this.substitution = substitution;
        premise.addAll(edbAtoms);

    }

    public Substitution substitution(){
        return substitution;
    }

    public List<Atom> premise(){
        return premise;
    }
}
