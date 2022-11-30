package Jonas.Hypothetical;

import Jonas.Logic.AtomList;
import Jonas.Logic.Program;
import Jonas.Logic.Substitution;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Representation of a query.
 */
public class Query {
    /**
     * Answers that resulted from the preprocessing.
     */
    public List<Answer> preprocessingAnswers;
    /**
     * Answers that have been updated up to the current time.
     */
    public List<Answer> supportedAnswers;
    /**
     * Complete answers for the query. I.e. answers with no premise.
     */
    public List<Answer> answers; // TODO fill this as supportedAnswers are proven.
    public AtomList queriedAtoms;

    /**
     * Constructs a new query given the preprocessed answers.
     * @param queriedAtoms Atoms to query.
     * @param preprocessingAnswers Preprocessed answers found by the modified SLD-resolution.
     */
    public Query(AtomList queriedAtoms, List<Answer> preprocessingAnswers){
        this.queriedAtoms = queriedAtoms;
        this.supportedAnswers = preprocessingAnswers;
        this.preprocessingAnswers = preprocessingAnswers;
    }

    /**
     * @param obj other object.
     * @return true iff this and obj are querying the same atoms.
     */
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Query)){
            return false;
        }
        Query other = (Query) obj;
        return other.queriedAtoms.equals(this.queriedAtoms);
    }

    /**
     * @return list of all complete answers. I.e. answers without a premise.
     */
    public List<Answer> getProvedAnswers(){
        List<Answer> result = new ArrayList<>();
        for(Answer a: supportedAnswers){
            if(a.premise.isEmpty()) result.add(a);
        }
        return result;
    }


    /**
     * @return a copy of this query.
     */
    public Query copy(){
        return new Query(queriedAtoms, new ArrayList<>(supportedAnswers));
    }

    /**
     * @return String representation of this query.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(queriedAtoms.toString()).append(":");
        if(supportedAnswers.isEmpty()){
            builder.append(" NO ANSWERS");
        }else{
            builder.append("\n");
        }
        for(Answer a: supportedAnswers){
            builder.append("\t");
            builder.append(a.toString(queriedAtoms));
            builder.append("\n");
        }
        return builder.toString();
    }

    /**
     * @return JSON object of the current status of this query.
     */
    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("query", this.queriedAtoms.toString());
        for(Answer a: this.supportedAnswers){
            o.accumulate("answers", a.toJSONObject(this.queriedAtoms));
        }
        return o;
    }

    /**
     * Updates the answers of the query using the dataSliceProgram.
     * @param dataSliceProgram a program consisting only of facts. Every atom in the data slice at the current time is added as a single fact.
     * @param time current time.
     */
    public void update(Program dataSliceProgram, int time) {
        Set<Answer> answerSet = new HashSet<>();
        for(Answer a: this.supportedAnswers){
            answerSet.addAll(a.update(dataSliceProgram, time));
        }
        this.supportedAnswers = new ArrayList<>(answerSet);
    }
}
