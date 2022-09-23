public interface Term {
    Term getVariant(int version);
    public Term applySub(Substitution substitution);

}
