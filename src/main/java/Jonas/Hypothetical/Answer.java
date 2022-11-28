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

public class Answer implements Comparable<Answer>{

    public final Substitution substitution;
    public final AtomList evidence;
    public final AtomList premise;

    public Answer(Substitution substitution, AtomList evidence, AtomList premise){
        this.substitution = substitution;
        this.evidence = evidence;
        this.premise = premise;
    }


    public Set<Answer> update(Program dataSlice, int time){
        //Constant part.
        Set<Answer> result = new HashSet<>();

        if(!this.premise.smallestConstant().isEmpty() && this.premise.smallestConstant().get(0).temporal.tConstant == time){
            AtomList constants = this.premise.smallestConstant();
            List<Substitution> answers = SLDResolution.findSubstitutions(dataSlice, constants);

            for(Substitution answer: answers){
                Substitution sub = this.substitution.add(answer);
                AtomList newEvidence = this.evidence.plus(constants).applySub(answer);
                AtomList newPremise = this.premise.without(constants).applySub(answer);

                result.add(new Answer(sub, newEvidence, newPremise));
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
                Substitution sub = this.substitution.add(answer);
                AtomList newEvidence = this.evidence.plus(smallestPremise).applySub(answer);
                AtomList newPremise = this.premise.without(smallestPremise).applySub(answer);

                result.add(new Answer(sub, newEvidence, newPremise));
            }
        }

        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Answer)){
            return false;
        }
        Answer other = (Answer) obj;
        return this.evidence.equals(other.evidence) && this.premise.equals(other.premise);
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

    @Override
    public int compareTo(Answer o) {
        return this.toString().compareTo(o.toString());
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("substitutions", this.substitution.toJSONArray());
        o.put("evidence",this.evidence.toJSONArray());
        o.put("premise",this.premise.toJSONArray());

        return o;
    }

    public JSONObject toJSONObject(AtomList relevantQuery) {
        JSONObject o = new JSONObject();
        o.put("substitutions", this.substitution.toJSONArray(relevantQuery));
        o.put("evidence",this.evidence.toJSONArray());
        o.put("premise",this.premise.toJSONArray());

        return o;
    }
}
