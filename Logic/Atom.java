

public record Atom(Predicate predicate, Temporal temporal, Term... args) {

    public Atom {
        if(args.length != predicate.numberOfArgs()){
            throw new IllegalArgumentException("The number of terms must match the number of args in the predicate.");
        }
        if(temporal.variable() == null && temporal.constant()<0){
            throw new IllegalArgumentException("TimeConstant must be positive if no TimeVariable is given.");
        }
    }

    public Atom applySub(Substitution substitution) {
        Term[] subbed_args = new Term[this.args.length];
        for (int i = 0; i < this.args.length; i++) {
            subbed_args[i] = this.args[i].applySub(substitution);
        }
        return new Atom(predicate, (Temporal) this.temporal.applySub(substitution), subbed_args);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(predicate.toString());
        builder.append('(');
        if(temporal.variable() != null) {
            builder.append(temporal.variable().name);
            if (temporal.constant() > 0) {
                builder.append("+");
                builder.append(temporal.constant());
            } else if (temporal.constant() < 0) {
                builder.append(temporal.constant());
            }
        }else{
            builder.append(temporal.constant());
        }
        builder.append(",");
        for(Term t: args){
            builder.append(t.toString());
            builder.append(',');
        }
        builder.delete(builder.length()-1, builder.length());
        builder.append(')');
        return builder.toString();
    }
}
