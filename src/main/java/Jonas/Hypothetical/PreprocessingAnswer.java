package Jonas.Hypothetical;

import java.util.Collections;
import Jonas.Logic.Atom;
import Jonas.Logic.AtomList;
import Jonas.Logic.Substitution;

/**
 * A non-supported answer
 */
public class PreprocessingAnswer {

    /**
     * Substitutions which have been applied to the evidence and premises.
     */
    public final Substitution substitution;
    /**
     * Sorted list of premises without a temporal variable.
     */
    public final AtomList constantPremise; // Sorted list of premises with no temporal variable
    /**
     * List of premises with the smallest time constant and without a temporal variable.
     */
    public final AtomList smallestConstant;
    /**
     * Sorted list of premises with a temporal variable.
     */
    public final AtomList temporalPremise; // Sorted list of premises with temporal variable
    /**
     * List of premises with the smallest time constant and with a temporal variable.
     */
    public final AtomList smallestTemporal;

    /**
     * Constructs an hypothetical answer.
     * @param substitution substitution applied on the premise
     * @param premise premises of the hypothetical answer
     */
    public PreprocessingAnswer(Substitution substitution, AtomList premise) {
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

    /**
     * Create a list of the atoms with the smallest temporal constant from a list of atoms given.
     * @param list the list of atoms given.
     * @return list of atoms with the smallest temporal constant.
     */
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


    /**
     * Returns a string representation of this.
     * @return string representation.
     */
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

    /**
     * Returns a string representation of this, where only substitutions relating to an atom is show.
     * @param relevantQuery the atom for which only relevant substitutions is showed.
     * @return string representation.
     */
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


    /**
     * Returns a string representation of this, where only substitutions relating to a list of atoms is show.
     * @param relevantQuery the list of atoms for which only relevant substitutions is showed.
     * @return string representation.
     */
    public String toString(AtomList relevantQuery) {
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
