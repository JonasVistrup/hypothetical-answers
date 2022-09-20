public class EAnswer {

    private final Substitution substitution;
    private final AtomList evidence;
    private final AtomList constantPremise; // Sorted list of premises with no temporal variable
    private final AtomList temporalPremise; // Sorted list of premises with temporal variable

    public EAnswer(Substitution substitution, AtomList evidence, AtomList constantPremise, AtomList temporalPremise) {
        this.substitution = substitution;
        this.evidence = evidence;
        this.constantPremise = constantPremise;
        this.temporalPremise = temporalPremise;
    }
}
