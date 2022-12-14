package Jonas.Logic;

import org.json.JSONObject;

/**
 * A single substitution.
 */
public class Sub {
    /**
     * Logic.Variable that will be substituted.
     */
    public final Variable from;
    /**
     * Logic.Term that the variable from will be substituted into.
     */
    public final Term to;

    /**
     * Constructs a single substitution from a variable to a term.
     * @param from the variable.
     * @param to the term.
     */
    public Sub(Variable from, Term to){
        this.from = from;
        this.to = to;
    }

    /** Returns a string representation of the substitution.
     * @return string representation of the substitution.
     */
    @Override
    public String toString() {
        return from.toString()+"/"+to.toString();
    }

    /**
     * @return JSON object representing this sub.
     */
    public JSONObject toJSONObject(){
        JSONObject o = new JSONObject();
        o.put("from", from.toString());
        o.put("to",to.toString());
        return o;
    }
}
