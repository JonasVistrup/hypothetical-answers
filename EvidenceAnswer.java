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

    //TODO equal
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        EvidenceAnswer that = (EvidenceAnswer) obj;
        if(!substitution.equals(that.substitution)) return false;

        return Objects.equals(this.substitution, that.substitution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(substitution);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(substitution.toString());
        builder.append(",{");
        for(Atom a: Evidence){
            builder.append(a.toString());
            builder.append(",");
        }
        if(!Evidence.isEmpty()){
            builder.deleteCharAt(builder.length()-1);
        }
        builder.append("},{");
        for(Atom a: PremiseConstant){
            builder.append(a.toString());
            builder.append(",");
        }

        for(Atom a: PremiseVariable){
            builder.append(a.toString());
            builder.append(",");
        }
        if(!PremiseConstant.isEmpty() || !PremiseVariable.isEmpty()){
            builder.deleteCharAt(builder.length()-1);
        }

        builder.append("}]");
        return builder.toString();
    }

    public String toString(Atom releventQuery) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(substitution.toString(releventQuery));
        builder.append(",{");
        for(Atom a: Evidence){
            builder.append(a.toString());
            builder.append(",");
        }
        if(!Evidence.isEmpty()){
            builder.deleteCharAt(builder.length()-1);
        }
        builder.append("},{");
        for(Atom a: PremiseConstant){
            builder.append(a.toString());
            builder.append(",");
        }

        for(Atom a: PremiseVariable){
            builder.append(a.toString());
            builder.append(",");
        }
        if(!PremiseConstant.isEmpty() || !PremiseVariable.isEmpty()){
            builder.deleteCharAt(builder.length()-1);
        }

        builder.append("}]");
        return builder.toString();
    }


}
