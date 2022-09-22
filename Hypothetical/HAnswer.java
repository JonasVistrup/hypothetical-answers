import java.util.Collections;

public class HAnswer {
    public final Substitution substitution;
    public final AtomList constantPremise; // Sorted list of premises with no temporal variable
    public final AtomList smallestConstant;
    public final AtomList temporalPremise; // Sorted list of premises with temporal variable
    public final AtomList smallestTemporal;

    public HAnswer(Substitution substitution, AtomList premise) {
        this.substitution = substitution;
        this.constantPremise = new AtomList();
        this.temporalPremise = new AtomList();
        for(Atom a: premise) {
            if (a.temporal.tVar == null) {
                constantPremise.add(a);
            }else{
                temporalPremise.add(a);
            }
        }
        Collections.sort(constantPremise);
        Collections.sort(temporalPremise);

        smallestConstant = findMin(constantPremise);
        smallestTemporal = findMin(temporalPremise);
    }

    public static AtomList findMin(AtomList list){
        AtomList min = new AtomList();
        if(list.isEmpty()){
            return min;
        }
        int smallestTime = list.get(0).temporal.tConstant;
        for(Atom a: list){
            if(a.temporal.tConstant>smallestTime){
                break;
            }
            min.add(a);
        }
        return min;
    }



    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(substitution.toString());
        builder.append(",{");
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

    public String toString(Atom relevantQuery) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(substitution.toString(relevantQuery));
        builder.append(",{");
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
