

public record Atom(Predicate predicate, Term... args) {

    public Atom {
        if(args.length != predicate.numberOfArgs()){
            throw new IllegalArgumentException("The number of terms must match the number of args in the predicate!");
        }
    }

    public Atom applySub(Substitution substitution) {
        Term[] subbed_args = new Term[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            subbed_args[i] = this.args[i].applySub(substitution);
        }
        return new Atom(predicate, subbed_args);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(predicate.toString());
        builder.append('(');
        for(Term t: args){
            builder.append(t.toString());
            builder.append(',');
        }
        builder.delete(builder.length()-1, builder.length());
        builder.append(')');
        return builder.toString();
    }
}
