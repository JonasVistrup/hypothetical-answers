import java.util.ArrayList;
import java.util.List;



/**
 * A set of substitutions.
 */
public class Substitution {

    private final List<Sub> subs; //TODO: convert to hashmap for speedup

    /**
     * Constructs an empty set of substitutions.
     */
    public Substitution(){
        this.subs = new ArrayList<>();
    }

    /**
     * Constructs a set of substitutions with a single sub.
     * @param from variable which to substitute.
     * @param to term to substitute the variable into.
     */
    public Substitution(Variable from, Term to){
        this.subs = new ArrayList<>();
        this.subs.add(new Sub(from, to));
    }

    /**
     * Creates a new substitution based upon two substitutions.
     * @param subs1 the first substitution.
     * @param subs2 the second substitution.
     * @return the composition of the two substitutions.
     */
    public static Substitution composition(Substitution subs1, Substitution subs2){
        Substitution resultingSubs = new Substitution();

        for(Sub sub: subs1.subs){
            Sub new_sub = new Sub(sub.from, sub.to.applySub(subs2));
            if (new_sub.from != new_sub.to){
                resultingSubs.subs.add(new_sub);
            }
        }
        for(Sub sub: subs2.subs){
            Term to = resultingSubs.getSubstitution(sub.from);
            if(to == null){
                resultingSubs.subs.add(sub);
            }
        }
        return resultingSubs;
    }


    /**
     * Returns the term that a variable is substituted into. Null if the variable is not substituted.
     * @param var variable for which its substitution is searched for.
     * @return the term var is substituted into or null if no substitution of var is in this list of substitutions.
     */
    public Term getSubstitution(Variable var){
        for(Sub sub: subs){
            if(sub.from == var) return sub.to;
        }
        return null;
    }


    /** Returns a string representation of the substitution.
     * @return string representation of the substitution.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for(Sub s: subs){
            builder.append("(");
            builder.append(s.toString());
            builder.append(")");
            builder.append(",");
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append("}");
        return builder.toString();
    }


    /**Returns a string representation of the all substitutions relevant to the atom.
     * @param relevantQuery atom for which the representation is relevant for.
     * @return string representation.
     */
    public String toString(Atom relevantQuery) {
        AtomList relevantList = new AtomList(relevantQuery);

        StringBuilder builder = new StringBuilder();
        builder.append("{");

        for(Sub s: subs){
            if(!relevantSub(s, relevantList)) continue;
            builder.append("(");
            builder.append(s.toString());
            builder.append(")");
            builder.append(",");
        }
        if(builder.length()>1) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("}");
        return builder.toString();
    }

    /**Returns a string representation of the all substitutions relevant to an atom in a list of atoms.
     * @param relevantQuery list of atom for which the representation is relevant for.
     * @return string representation.
     */
    public String toString(AtomList relevantQuery) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");

        for(Sub s: subs){
            if(!relevantSub(s, relevantQuery)) continue;
            builder.append("(");
            builder.append(s.toString());
            builder.append(")");
            builder.append(",");
        }
        if(builder.length()>1) {
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("}");
        return builder.toString();
    }


    /**
     * Returns whether the sub would affect any of the atoms in the query.
     * @param s the substitution
     * @param relevantQuery the list of atoms
     * @return true iff relevantQuery contains the variable which is substituted in s.
     */
    private static boolean relevantSub(Sub s, AtomList relevantQuery) {
        for(Atom a: relevantQuery) {
            for (Term t : a.args) {
                if (t == s.from) return true;
            }
            if(a.temporal.tVar == s.from) return true;
        }
        return false;
    }

}