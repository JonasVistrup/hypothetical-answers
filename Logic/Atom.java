import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Atom implements Comparable<Atom>{
    /**
     * Predicate of the atom.
     */
    final Predicate predicate;
    /**
     * Non-temporal arguments of the atom.
     */
    final List<Term> args;
    /**
     * Temporal argument of the atom.
     */
    final Temporal temporal;
    /**
     * Map of different variants of this atom.
     */
    private final Map<Integer, Atom> instances;

    /**
     * Constructs an atom.
     * @param predicate predicate of atom.
     * @param args non-temporal arguments of atom.
     * @param temporal temporal argument of atom.
     */
    Atom(Predicate predicate, List<Term> args, Temporal temporal){
        if(predicate.nArgs != args.size()) throw new IllegalArgumentException("Number of arguments does not match predicate");
        this.predicate = predicate;
        this.args = args;
        this.temporal = temporal;
        this.instances = new HashMap<>();
    }

    /**
     * Constructs a variant of an atom.
     * @param parent original atom for which this is a variant
     * @param version version of the variant
     */
    Atom(Atom parent, int version){
        this.predicate = parent.predicate;
        this.args = new ArrayList<>();
        for(Term t: parent.args){
            this.args.add(t.getVariant(version));
        }
        this.temporal = (Temporal) parent.temporal.getVariant(version);
        this.instances = new HashMap<>();
    }


    /**
     * Returns a variant of the atom.
     * @param version which variant that should be returned
     * @return a variant of the atom
     */
    public Atom getInstance(int version){
        if(this.instances.containsKey(version)){
            return this.instances.get(version);
        }else{
            Atom instance = new Atom(this, version);
            this.instances.put(version, instance);
            return instance;
        }
    }

    /**
     * Applies a substitution on every term in the atom, returning the resulting atom.
     * @param substitution applied substitution
     * @return new atom with substituted terms
     */
    public Atom applySub(Substitution substitution){
        List<Term> new_terms = new ArrayList<>();
        for(Term t: args){
            new_terms.add(t.applySub(substitution));
        }
        return new Atom(this.predicate, new_terms, (Temporal) this.temporal.applySub(substitution));
    }


    /**
     * Returns a String representation of this atom.
     * @return representation of this atom.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(predicate.toString());
        builder.append('(');
        for(Term t: args){
            builder.append(t.toString());
            builder.append(',');
        }
        builder.append(temporal);

        builder.append(')');
        return builder.toString();
    }

    /**
     * Compares the temporal aspects of atom o and this atom. See Temporal.compareTo for details.
     * @param o the object to be compared
     * @return a negative, 0 or a positive number
     */
    @Override
    public int compareTo(@NotNull Atom o) {
        return this.temporal.compareTo(o.temporal);
    }
}
