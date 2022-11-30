package Jonas.Hypothetical;

import Jonas.Logic.Atom;
import Jonas.Logic.AtomList;
import Jonas.Logic.Program;
import Jonas.Logic.Substitution;
import Jonas.SLD.SLDResolution;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Representation of an (incomplete) answer.
 */
public class Answer implements Comparable<Answer>{

    /**
     *
     */
    public final AtomList resultingQueriedAtoms;
    /**
     * Substitutions applied on the atoms in evidence and premise.
     */
    public final Substitution substitution;
    /**
     * List of atoms which is evidence for this answer.
     */
    public final AtomList evidence;
    /**
     * List of atoms which has not yet been seen, and need to occur for this answer to be correct.
     */
    public final AtomList premise;

    /**
     * Constructs an answer with substitution, evidence and premise.
     * @param substitution This answers substitution.
     * @param evidence This answers evidence.
     * @param premise This answers premise.
     */
    public Answer(AtomList resultingQueriedAtoms, Substitution substitution, AtomList evidence, AtomList premise){
        this.resultingQueriedAtoms = resultingQueriedAtoms;
        this.substitution = substitution;
        this.evidence = evidence;
        this.premise = premise;
    }


    /**
     * Updates the premises of this answer to get all possible new answers.
     * @param dataSlice a program consisting only of facts. Every atom in the data slice at the current time is added as a single fact.
     * @param time time of the dataSlice.
     * @return The set of all possible answers derived from the dataSlice and this Answer's premise.
     */
    public Set<Answer> update(Program dataSlice, int time){
        //Constant part.
        Set<Answer> result = new HashSet<>();

        if(!this.premise.smallestConstant().isEmpty() && this.premise.smallestConstant().get(0).temporal.tConstant == time){
            AtomList constants = this.premise.smallestConstant();
            List<Substitution> answers = SLDResolution.findSubstitutions(dataSlice, constants);

            for(Substitution answer: answers){
                AtomList queriedAtoms = this.resultingQueriedAtoms.applySub(answer);
                Substitution sub = this.substitution.add(answer);
                AtomList newEvidence = this.evidence.plus(constants).applySub(answer);
                AtomList newPremise = this.premise.without(constants).applySub(answer);

                result.add(new Answer(queriedAtoms, sub, newEvidence, newPremise));
            }
        }else{
            result.add(this);
        }

        //Constant and Temporal part.
        if(!this.premise.smallestVariable().isEmpty()){
            AtomList smallestPremise = this.premise.smallestConstant().isEmpty() || this.premise.smallestConstant().get(0).temporal.tConstant!= time?
                    this.premise.smallestVariable() : this.premise.smallestConstant().plus(this.premise.smallestVariable());

            List<Substitution> answers = SLDResolution.findSubstitutions(dataSlice, smallestPremise); //TODO can be optimized. The constants do not need to be solved twice.

            for(Substitution answer: answers){
                AtomList queriedAtoms = this.resultingQueriedAtoms.applySub(answer);
                Substitution sub = this.substitution.add(answer);
                AtomList newEvidence = this.evidence.plus(smallestPremise).applySub(answer);
                AtomList newPremise = this.premise.without(smallestPremise).applySub(answer);

                result.add(new Answer(queriedAtoms, sub, newEvidence, newPremise));
            }
        }

        return result;
    }


    /**
     * An equals function. If the queriedAtoms, evidence and premise for this and obj are equal then they are the same Answer.
     * @param obj other object.
     * @return returns whether this answer is equal to obj.
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Answer)){
            return false;
        }
        Answer other = (Answer) obj;
        return this.resultingQueriedAtoms.equals(other.resultingQueriedAtoms) && this.evidence.equals(other.evidence) && this.premise.equals(other.premise);
    }



    /**
     * Returns a string representation of this, where only substitutions relating to an atom is show.
     * @param relevantQuery the atom for which only relevant substitutions is showed.
     * @return string representation.
     */
    public String toString(Atom relevantQuery) {
        return toString(new AtomList(relevantQuery));
    }


    /**
     * Returns a string representation of this, where only substitutions relating to a list of atoms is show.
     * @param relevantQuery the list of atoms for which only relevant substitutions is showed.
     * @return string representation.
     */
    public String toString(AtomList relevantQuery) {
        return "[" + substitution.toString(relevantQuery)+","+evidenceString()+","+premiseString()+"]";
    }

    private String evidenceString(){
        StringBuilder builder = new StringBuilder("{");
        for(Atom a: this.evidence.constantTime()){
            builder.append(a.toString());
            builder.append(",");
        }
        for(Atom a: this.evidence.variableTime()){
            builder.append(a.toString());
            builder.append(",");
        }
        if(!this.evidence.isEmpty()){
            builder.deleteCharAt(builder.length()-1);
        }
        builder.append("}");
        return builder.toString();
    }

    private String premiseString(){
        StringBuilder builder = new StringBuilder("{");
        for(Atom a: this.premise.constantTime()){
            builder.append(a.toString());
            builder.append(",");
        }
        for(Atom a: this.premise.variableTime()){
            builder.append(a.toString());
            builder.append(",");
        }
        if(!this.premise.isEmpty()){
            builder.deleteCharAt(builder.length()-1);
        }
        builder.append("}");
        return builder.toString();
    }

    /**
     * Returns a string representation of this.
     * @return string representation.
     */
    public String toString() {
        return "[" + substitution.toString()+","+evidenceString()+","+premiseString()+"]";
    }

    /**
     * Compares to answers, creating a complete ordering of all answer.
     * @param o the object to be compared.
     * @return -1 if this is less than o, o if they are lexicographically equal, 1 if this is greater than o.
     */
    @Override
    public int compareTo(Answer o) {
        return this.toString().compareTo(o.toString());
    }

    /**
     * @return a JSONObject of this.
     */
    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("substitutions", this.substitution.toJSONArray());
        o.put("evidence",this.evidence.toJSONArray());
        o.put("premise",this.premise.toJSONArray());

        return o;
    }

    /**
     * @param relevantQuery list of relevant atoms.
     * @return a JSONObject of this, but only includes substitutions that effects atoms in for relevantQuery.
     */
    public JSONObject toJSONObject(AtomList relevantQuery) {
        JSONObject o = new JSONObject();
        o.put("substitutions", this.substitution.toJSONArray(relevantQuery));
        o.put("evidence",this.evidence.toJSONArray());
        o.put("premise",this.premise.toJSONArray());

        return o;
    }
}
