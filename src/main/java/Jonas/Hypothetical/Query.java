package Jonas.Hypothetical;

import Jonas.Logic.AtomList;
import Jonas.Logic.Substitution;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Query {
    public List<Answer> preprocessingAnswers;
    public List<Answer> supportedAnswers;
    public List<Answer> answers; // TODO fill this as supportedAnswers are proven.
    public AtomList queriedAtoms;

    public Query(AtomList queriedAtoms, List<Answer> preprocessingAnswers){
        this.queriedAtoms = queriedAtoms;
        this.supportedAnswers = preprocessingAnswers;
        this.preprocessingAnswers = preprocessingAnswers;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Query)){
            return false;
        }
        Query other = (Query) obj;
        return other.queriedAtoms.equals(this.queriedAtoms);
    }

    public List<Answer> getProvedAnswers(){
        List<Answer> result = new ArrayList<>();
        for(Answer a: supportedAnswers){
            if(a.premise.isEmpty()) result.add(a);
        }
        return result;
    }


    public Query copy(){
        return new Query(queriedAtoms, new ArrayList<>(supportedAnswers));
    }

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

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("query", this.queriedAtoms.toString());
        for(Answer a: this.supportedAnswers){
            o.accumulate("answers", a.toJSONObject(this.queriedAtoms));
        }
        return o;
    }
}
