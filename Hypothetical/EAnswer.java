public class EAnswer {

    public final Substitution substitution;
    public final AtomList evidence;
    public final AtomList constantPremise; // Sorted list of premises with no temporal variable
    public final AtomList smallestConstant;
    public final AtomList temporalPremise; // Sorted list of premises with temporal variable
    public final AtomList smallestTemporal;


    public EAnswer(Substitution substitution, AtomList evidence, AtomList constantPremise, AtomList temporalPremise) {
        this.substitution = substitution;
        this.evidence = evidence;
        this.constantPremise = constantPremise;
        this.temporalPremise = temporalPremise;

        smallestConstant = HAnswer.findMin(constantPremise);
        smallestTemporal = HAnswer.findMin(temporalPremise);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(substitution.toString());
        builder.append(",{");
        for(Atom a: evidence){
            builder.append(a.toString());
            builder.append(",");
        }
        if(!evidence.isEmpty()){
            builder.deleteCharAt(builder.length()-1);
        }
        builder.append("},{");
        for(Atom a: constantPremise){
            builder.append(a.toString());
            builder.append(",");
        }

        for(Atom a: temporalPremise){
            builder.append(a.toString());
            builder.append(",");
        }
        if(!constantPremise.isEmpty() || !temporalPremise.isEmpty()){
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
        for(Atom a: evidence){
            builder.append(a.toString());
            builder.append(",");
        }
        if(!evidence.isEmpty()){
            builder.deleteCharAt(builder.length()-1);
        }
        builder.append("},{");
        for(Atom a: constantPremise){
            builder.append(a.toString());
            builder.append(",");
        }

        for(Atom a: temporalPremise){
            builder.append(a.toString());
            builder.append(",");
        }
        if(!constantPremise.isEmpty() || !temporalPremise.isEmpty()){
            builder.deleteCharAt(builder.length()-1);
        }

        builder.append("}]");
        return builder.toString();
    }
}
