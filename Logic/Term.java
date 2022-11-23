package Logic;

/**
 * A logical term.
 */
public interface Term {
    /**
     * Returns a variant of the term.
     * @param version which variant that should be returned.
     * @return a variant of the term
     */
    public Term getVariant(int version);

    /**
     * Returns the result of applying the substitution to this term.
     * @param substitution substitution to apply
     * @return resulting term
     */
    public Term applySub(Substitution substitution);

}
