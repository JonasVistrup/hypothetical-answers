import java.util.Collections;

public class HAnswer {
    public final Substitution substitution;
    public final AtomList constantPremise; // Sorted list of premises with no temporal variable
    public final AtomList temporalPremise; // Sorted list of premises with temporal variable

    public HAnswer(Substitution substitution, AtomList premise) {
        this.substitution = substitution;
        this.constantPremise = new AtomList();
        this.temporalPremise = new AtomList();
        for(AtomInstance a: premise) {
            if (a.temporal.tVarInst == null) {
                constantPremise.add(a);
            }else{
                temporalPremise.add(a);
            }
        }
        Collections.sort(constantPremise);
        Collections.sort(temporalPremise);
    }



    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(substitution.toString());
        builder.append(",{");
        for(AtomInstance a: constantPremise){
            builder.append(a.toString());
            builder.append(",");
        }
        for(AtomInstance a: temporalPremise){
            builder.append(a.toString());
            builder.append(",");
        }
        if(!constantPremise.isEmpty() || !temporalPremise.isEmpty()){
            builder.deleteCharAt(builder.length()-1);
        }

        builder.append("}]");
        return builder.toString();
    }

    public String toString(AtomInstance relevantQuery) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(substitution.toString(relevantQuery));
        builder.append(",{");
        for(AtomInstance a: constantPremise){
            builder.append(a.toString());
            builder.append(",");
        }
        for(AtomInstance a: temporalPremise){
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
