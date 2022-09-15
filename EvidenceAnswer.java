import java.util.List;
import java.util.Objects;

public final class EvidenceAnswer {
    private final Substitution substitution;
    private final List<Atom> Evidence;
    private final List<Atom> PremiseConstant;
    private final List<Atom> PremiseVariable;

    EvidenceAnswer(Substitution substitution, List<Atom> Evidence, List<Atom> PremiseConstant, List<Atom> PremiseVariable) {
        this.substitution = substitution;
        this.Evidence = Evidence;
        this.PremiseConstant = PremiseConstant;
        this.PremiseVariable = PremiseVariable;
    }

    public Substitution substitution() {
        return substitution;
    }

    public List<Atom> Evidence(){
        return Evidence;
    }
    public List<Atom> PremiseConstant(){
        return PremiseConstant;
    }
    public List<Atom> PremiseVariable(){
        return PremiseVariable;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        EvidenceAnswer that = (EvidenceAnswer) obj;
        return Objects.equals(this.substitution, that.substitution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(substitution);
    }

    @Override
    public String toString() {
        return "EvidenceAnswer[" +
                "substitution=" + substitution + ']';
    }

}
