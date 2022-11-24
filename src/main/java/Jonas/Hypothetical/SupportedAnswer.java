package Jonas.Hypothetical;

import Jonas.Logic.Atom;
import Jonas.Logic.AtomList;
import Jonas.Logic.Substitution;

/**
 * A supported answer.
 */
public class SupportedAnswer {

    /**
     * Substitutions which have been applied to the evidence and premises.
     */
    public final Substitution substitution;
    /**
     * List of evidence.
     */
    public final AtomList evidence;
    /**
     * Sorted list of premises without a temporal variable.
     */
    public final AtomList constantPremise;
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
     * Constructs an evidence answer.
     * @param substitution substitution applied on the evidence and premise
     * @param evidence evidence obtained for the query
     * @param constantPremise premises without a temporal variable.
     * @param temporalPremise premises with a temporal variable.
     */
    public SupportedAnswer(Substitution substitution, AtomList evidence, AtomList constantPremise, AtomList temporalPremise) {

        this.substitution = substitution;
        this.evidence = evidence;

        AtomList premise = constantPremise.plus(temporalPremise);
        this.constantPremise = new AtomList();
        this.temporalPremise = new AtomList();

        for(Atom a: premise){
            if (a.temporal.tVar == null) {
                this.constantPremise.add(a);
            }else{
                this.temporalPremise.add(a);
            }
        }
        this.smallestConstant = PreprocessingAnswer.findMin(this.constantPremise);
        this.smallestTemporal = PreprocessingAnswer.findMin(this.temporalPremise);
    }

    public SupportedAnswer(Substitution substitution, AtomList evidence, AtomList premise) {

        this.substitution = substitution;
        this.evidence = evidence;

        this.constantPremise = new AtomList();
        this.temporalPremise = new AtomList();

        for(Atom a: premise){
            if (a.temporal.tVar == null) {
                this.constantPremise.add(a);
            }else{
                this.temporalPremise.add(a);
            }
        }
        this.smallestConstant = PreprocessingAnswer.findMin(this.constantPremise);
        this.smallestTemporal = PreprocessingAnswer.findMin(this.temporalPremise);
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
