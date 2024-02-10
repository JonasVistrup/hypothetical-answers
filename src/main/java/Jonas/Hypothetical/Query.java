package Jonas.Hypothetical;

import Jonas.Logic.AtomList;
import Jonas.Logic.Program;
import Jonas.Logic.Substitution;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Representation of a query.
 */
public class Query {

    public ArrayList<Answer> answers = new ArrayList<>();
    public ArrayList<Answer> hypAnswers;

    private static int MAX_ANSWER_SIZE = 100000;

    public AtomList queriedAtoms;
    public int index;
    public DBConnection db;
    private static int numberOfQueries = 0;

    /**
     * Constructs a new query given the preprocessed answers.
     * @param queriedAtoms Atoms to query.
     */
    public Query(AtomList queriedAtoms, DBConnection db, ArrayList<Answer> hypAnswers){
        this.index = numberOfQueries++;
        this.queriedAtoms = queriedAtoms;
        this.db = db;
        this.hypAnswers = hypAnswers; //TODO check if hypanswers has no premise and should be answers
    }

    public Query(AtomList queriedAtoms, int index, DBConnection db, ArrayList<Answer> hypAnswers, ArrayList<Answer> answers){
        this.index = index;
        this.queriedAtoms = queriedAtoms;
        this.db = db;

        this.hypAnswers = hypAnswers;
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
        return new Query(queriedAtoms, this.index, this.db, (ArrayList<Answer>) this.hypAnswers.clone(), (ArrayList<Answer>) this.answers.clone());
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

    private List<Answer> getAllAnswers(){ //TODO make it return all answers
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
        ArrayList<Answer> nextHypAnswers = new ArrayList<>();
        for(Answer a: this.hypAnswers){
            //System.out.println(a.toString(this.queriedAtoms));
            for(Answer aa: a.update(dataSliceProgram,time)){
                if(aa.premise.isEmpty()) this.answers.add(aa);
                else nextHypAnswers.add(aa);
            }
        }
        this.hypAnswers = nextHypAnswers;
        if(answers.size() > MAX_ANSWER_SIZE){
            db.uploadAnswers(this.answers,this);
            this.answers = new ArrayList<>();
        }

        /*Iterator<Answer> iterator = db.getHypotheticalAnswers(this);
        for (Iterator<Answer> it = iterator; it.hasNext(); ) {
            Answer a = it.next();
            //System.out.println(a);
            for(Answer aa: a.update(dataSliceProgram, time)){
                db.addAnswer(aa,this);
            }
        }*/
        //System.out.println();
    }

    public void update2(DataIterator data, int time) {

        //Step 2: Fetch Datachunk
        //Step 3: Update TBK and rest, add results to rest
        //Step 4: Go back to step 2 until all datachunk have been used
        //Step 5: Remove TBK, and set rest as the new hypanswers

        //Step 1: Split to TBK and rest
        ArrayList<Answer> toBeKilled = new ArrayList<>();
        ArrayList<Answer> iterHypAnswers = new ArrayList<>();
        ArrayList<Answer> nextHypAnswers = new ArrayList<>();
        for(Answer a: this.hypAnswers){
            //System.out.println(a.toString(this.queriedAtoms));
            if(!a.premise.smallestConstant().isEmpty() && a.premise.smallestConstant().get(0).temporal.tConstant == time){
                toBeKilled.add(a);
            }else if(a.premise.smallestVariable().isEmpty() && a.premise.smallestConstant().get(0).temporal.tConstant>time) {
                nextHypAnswers.add(a);
            }else{
                iterHypAnswers.add(a);
            }
        }

        for(Program p: data){
            ArrayList<Answer> temp = new ArrayList<>();
            for(Answer a: iterHypAnswers){
                for(Answer aa: a.partialUpdate(p,time)){
                    if(aa.premise.isEmpty()) this.answers.add(aa);
                    else if (aa.premise.smallestVariable().isEmpty() && aa.premise.smallestConstant().get(0).temporal.tConstant>time){
                        nextHypAnswers.add(aa);
                    }else{
                        temp.add(aa);
                    }
                }
            }
            iterHypAnswers.addAll(temp);

            for(Answer a: toBeKilled){
                for(Answer aa: a.partialUpdate(p,time)){
                    if(aa.premise.isEmpty()) this.answers.add(aa);
                    else if (aa.premise.smallestVariable().isEmpty() && aa.premise.smallestConstant().get(0).temporal.tConstant>time){
                        nextHypAnswers.add(aa);
                    }else{
                        iterHypAnswers.add(aa);
                    }
                }
            }
        }

        this.hypAnswers = nextHypAnswers;
        this.hypAnswers.addAll(iterHypAnswers);
        if(answers.size() > MAX_ANSWER_SIZE){
            db.uploadAnswers(this.answers,this);
            this.answers = new ArrayList<>();
        }

        //System.out.println();

    }
}
