import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class HypAnswer {
    private final Substitution substitution;
    private final List<Atom> premise;
    private final List<Atom> premiseConstant = new ArrayList<>(); //Sorted lists
    private final List<Atom> premiseTemporal = new ArrayList<>(); //Sorted lists
    public HypAnswer(Substitution substitution, List<Atom> edbAtoms){
        this.substitution = substitution;
        this.premise = new ArrayList<>(edbAtoms);

        //TODO differentiate between atoms with contants and atoms with temporal variables. The ladder should also be sorted.
        for (Atom edbAtom : edbAtoms) {
            if (edbAtom.temporal() == null) {
                premiseConstant.add(edbAtom);
            } else {
                premiseTemporal.add(edbAtom);
            }
        }
        Collections.sort(premiseConstant);
        Collections.sort(premiseTemporal);


    }

    public List<Atom> getConstantsForTime(int time){
        if(time<0) throw new IllegalArgumentException("Time must be a non-negative number");
        List<Atom> constantsForTime = new ArrayList<>();
        for(Atom a: premiseConstant){
            if(time==a.temporal().constant()){
                constantsForTime.add(a);
            }
            if(time<a.temporal().constant()){
                return constantsForTime;
            }
        }
        return constantsForTime;
    }



    public Substitution substitution(){
        return substitution;
    }

    public List<Atom> premiseConstant(){
        return premiseConstant;
    }

    public List<Atom> premise(){
        return premise;
    }

    public List<Atom> premiseTemporal(){
        return premiseTemporal;
    }

    public List<Atom> getSmallestTemporal(){
        List<Atom> smallestTemporal = new ArrayList<>();
        if(premiseTemporal.isEmpty()){
            return smallestTemporal;
        }

        int smallestConstant = premiseTemporal.get(0).temporal().constant();
        for(Atom atom: premiseTemporal){
            if(atom.temporal().constant() != smallestConstant){
                return smallestTemporal;
            }else{
                smallestTemporal.add(atom);
            }
        }
        return smallestTemporal;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(substitution.toString());
        builder.append(",{");
        for(Atom a: premise){
            builder.append(a.toString());
            builder.append(",");
        }
        if(premise.isEmpty()){
            builder.append("-"); // Hacky add of a character to the end for deletion
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append("}]");
        return builder.toString();
    }

    public String toString(Atom relevantQuery) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(substitution.toString(relevantQuery));
        builder.append(",{");
        for(Atom a: premise){
            builder.append(a.toString());
            builder.append(",");
        }
        if(premise.isEmpty()){
            builder.append("-"); // Hacky add of a character to the end for deletion
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append("}]");
        return builder.toString();
    }
}
