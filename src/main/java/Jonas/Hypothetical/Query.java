package Jonas.Hypothetical;

import Jonas.Logic.AtomList;
import Jonas.Logic.Program;
import Jonas.Logic.Substitution;
import org.json.JSONObject;

import java.util.*;

/**
 * Representation of a query.
 */
public class Query {

    public List<Answer> answers;
    public List<Answer> hypAnswers;

    public AtomList queriedAtoms;
    public int index;
    public DBConnection db;
    private static int numberOfQueries = 0;

    /**
     * Constructs a new query given the preprocessed answers.
     * @param queriedAtoms Atoms to query.
     */
    public Query(AtomList queriedAtoms, DBConnection db){
        this.index = numberOfQueries++;
        this.queriedAtoms = queriedAtoms;
        this.db = db;
    }

    public Query(AtomList queriedAtoms, int index, DBConnection db){
        this.index = index;
        this.queriedAtoms = queriedAtoms;
        this.db = db;
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
        return new ArrayList<>(this.db.getAnswers(this));
    }


    /**
     * @return a copy of this query.
     */
    public Query copy(){
        return new Query(queriedAtoms, this.index, this.db);
    }

    /**
     * @return String representation of this query.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(queriedAtoms.toString()).append(":");
        List<Answer> answers = getAllAnswers();

        if(answers.isEmpty()){
            builder.append(" NO ANSWERS");
        }else{
            builder.append("\n");
        }

        for(Answer a: answers){
            builder.append("\t");
            builder.append(a.toString(queriedAtoms));
            builder.append("\n");
        }
        return builder.toString();
    }

    private List<Answer> getAllAnswers(){
        List<Answer> answers = new ArrayList<>(db.getAnswers(this));
        for (Iterator<Answer> it = db.getHypotheticalAnswers(this); it.hasNext();) {
            answers.add(it.next());
        }
        return answers;
    }

    /**
     * @return JSON object of the current status of this query.
     */
    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("query", this.queriedAtoms.toString());
        for(Answer a: getAllAnswers()){
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
        Iterator<Answer> iterator = db.getHypotheticalAnswers(this);
        for (Iterator<Answer> it = iterator; it.hasNext(); ) {
            Answer a = it.next();
            //System.out.println(a);
            for(Answer aa: a.update(dataSliceProgram, time)){
                db.addAnswer(aa,this);
            }
        }
        //System.out.println();
    }
}
