package Logic;

import java.util.HashMap;
import java.util.Map;

/**
 * A logical clause.
 */
public class Clause {
    /**
     * Head of the clause.
     */
    public Atom head;
    /**
     * Body of the clause.
     */
    public AtomList body;

    /**
     * Map of different variants of this clause.
     */
    public Map<Integer, Clause> instances;

    /**
     * @param head head of the clause
     * @param body body of the clause
     */
    Clause(Atom head, AtomList body){
        this.head = head;
        this.body = body;

        this.instances = new HashMap<>();
    }

    /**
     * Constructs a variant of a clause.
     * @param clause original clause for which this is a variant
     * @param version version of the variant
     */
    public Clause(Clause clause, int version){
        this.head = clause.head.getInstance(version);
        this.body = new AtomList();
        for(Atom a: clause.body){
            this.body.add(a.getInstance(version));
        }
    }

    /**
     * Returns a variant of this clause.
     * @param version which variant that should be returned
     * @return a variant of this atom
     */
    public Clause getInstance(int version){
        if(this.instances.containsKey(version)){
            return this.instances.get(version);
        }else{
            Clause instance = new Clause(this, version);
            this.instances.put(version, instance);
            return instance;
        }
    }


    /** Returns a string representation of this clause in the form of HEAD{@literal <}-BODY.
     * @return string representation of clause
     */
    @Override
    public String toString() {
        if (body.isEmpty()) {
            return head.toString() + "<-";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(head.toString());
        builder.append("<-");
        for (Atom atom : body) {
            builder.append(atom.toString());
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
