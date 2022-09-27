import java.util.HashMap;
import java.util.Map;

/**
 * A logical variable.
 */
public class Variable implements Term{

    /**
     * String representation of variable.
     */
    private final String id;
    /**
     * Map of different variants of this variable.
     */
    private final Map<Integer, Variable> variants;

    /**
     * Constructs a new variable.
     * @param id String representation of variable
     */
    public Variable(String id){
        this.id = id;
        this.variants = new HashMap<>();
    }

    /**
     * Returns a variant of the variable.
     * @param version which variant that should be returned
     * @return a variant of the variable
     */
    public Term getVariant(int version){
        if(this.variants.containsKey(version)){
            return this.variants.get(version);
        }else{
            Variable instance = new Variable(this.id);
            this.variants.put(version, instance);
            return instance;
        }
    }

    /**
     * Returns the result of applying the substitution to this variable.
     * @param substitution substitution to apply
     * @return resulting variable
     */
    @Override
    public Term applySub(Substitution substitution) {
        Term t = substitution.getSubstitution(this);
        if(t != null){
            return t;
        }
        return this;
    }


    /**
     * String representation of the variable.
     * @return id
     */
    @Override
    public String toString() {
        return id;
    }


}
