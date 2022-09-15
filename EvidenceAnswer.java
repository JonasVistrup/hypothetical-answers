import java.util.List;

public record EvidenceAnswer(Substitution substitution, List<Atom> Evidence, List<Atom> PremiseConstant, List<Atom> PremiseVariable) {

}
