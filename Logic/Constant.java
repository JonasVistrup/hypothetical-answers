package Logic;

import Logic.Term;

/**
 * A logical constant
 */
public class Constant implements Term{

    /**
     * String representation of this constant.
     */
    private final String id;

    /**
     * Constructs a constant.
     * @param id constant's String representation
     */
    public Constant(String id){
        this.id = id;
    }

    /**
     * Any variant is also itself, since constants do not allow instantiation.
     * @param version which variant that should be returned (is ignored)
     * @return itself
     */
    public Term getVariant(int version){
        return this;
    }

    /**
     * String representation of this constant.
     * @return id
     */
    @Override
    public String toString() {
        return id;
    }

    /**
     * Returns the result of applying the substitution to this term (which is always itself, since only variables can be substituted).
     * @param substitution substitution to apply
     * @return itself
     */
    @Override
    public Term applySub(Substitution substitution) {
        return this;
    }
}
