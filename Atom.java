public record Atom(Predicate predicate, Term... args) {

    public Atom {
        assert predicate.numberOfArgs() == args.length;
    }

    public Atom applySub(Substitution substitution) {
        Term[] subbed_args = new Term[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            subbed_args[i] = this.args[i].applySub(substitution);
        }
        return new Atom(predicate, subbed_args);
    }
}
