import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A temporal aspect of predicates, containing both the temporal variable and temporal constant.
 */
public class Temporal implements Term, Comparable<Temporal>{


    /**
     * Temporal variable. May be null in the case of the temporal aspect only containing a constant.
     */
    final Variable tVar;
    /**
     * Temporal constant.
     */
    final int tConstant;

    /**
     * Map of different variants of this temporal.
     */
    private final Map<Integer, Temporal> variants;

    /**
     * Constructs a temporal.
     * @param tVar temporal variable
     * @param tConstant temporal constant
     */
    public Temporal(Variable tVar, int tConstant){
        if(tVar==null && tConstant<0) throw new IllegalArgumentException("Temporal constant must be at least 0");

        this.tVar = tVar;
        this.tConstant = tConstant;
        this.variants = new HashMap<>();
    }


    /**
     * Returns a variant of the temporal.
     * @param version which variant that should be returned
     * @return a variant of the temporal
     */
    public Term getVariant(int version){
        if(tVar == null){
            return this;
        }

        if(this.variants.containsKey(version)){
            return this.variants.get(version);
        }else{
            Temporal instance = new Temporal((Variable) tVar.getVariant(version), this.tConstant);
            this.variants.put(version, instance);
            return instance;
        }
    }

    /**
     * Returns the result of applying the substitution to this temporal.
     * @param substitution substitution to apply
     * @return resulting temporal
     */
    @Override
    public Term applySub(Substitution substitution) {
        Term to_term = substitution.getSubstitution(this.tVar);
        if(to_term != null){
            assert to_term instanceof Temporal;
            Temporal to = (Temporal) to_term;
            return new Temporal(to.tVar,to.tConstant+this.tConstant);
        }
        return this;
    }




    /**
     * String representation of temporal.
     * @return combined representation of both the temporal variable and temporal constant.
     */
    @Override
    public String toString() {
        if(tVar == null) {
            return "" + tConstant;
        }

        StringBuilder b = new StringBuilder(tVar.toString());
        if(tConstant>0){
            b.append("+");
        }else if(tConstant == 0){
            return b.toString();
        }

        b.append(tConstant);
        return b.toString();
    }

    /**
     * Compares temporal o to this temporal.
     * @throws IllegalArgumentException if the temporal variables are different, making the objects un-comparable
     * @param o the object to be compared.
     * @return a negative number if the o is larger than this temporal, 0 if they are equal, and a positive number otherwise
     */
    @Override
    public int compareTo(@NotNull Temporal o) {
        if(this.tVar != o.tVar) throw new IllegalArgumentException("Not comparable");
        return this.tConstant - o.tConstant;
    }
}
