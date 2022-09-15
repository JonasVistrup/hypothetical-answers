import java.util.Arrays;
import java.util.Objects;

public class Atom implements Comparable<Atom> {

    private Predicate predicate;
    private Temporal temporal;
    private Term[] args;

    public Atom(Predicate predicate, Temporal temporal, Term... args) {
        this.predicate = predicate;
        this.temporal = temporal;
        this.args = args;
        if(args.length != predicate.numberOfArgs()){
            throw new IllegalArgumentException("The number of terms must match the number of args in the predicate.");
        }
        if(temporal.variable() == null && temporal.constant()<0){
            throw new IllegalArgumentException("TimeConstant must be positive if no Temporal variable is given.");
        }
    }


    public Predicate predicate(){
        return predicate;
    }

    public Temporal temporal(){
        return temporal;
    }

    public Term[] args(){
        return args;
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
        for(Term t: args){
            builder.append(t.toString());
            builder.append(',');
        }
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

        builder.append(')');
        return builder.toString();
    }

    //TODO rewrite
    @Override
    public int compareTo(Atom o) {
        if((o.temporal.variable() == null && this.temporal.variable() == null) || (o.temporal.variable() != null && this.temporal.variable() != null)){
            return this.temporal.constant() - o.temporal.constant();
        }else{
            throw new IllegalArgumentException("Cannot compare temporal variable to constant");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Atom atom = (Atom) o;
        if(!Objects.equals(predicate, atom.predicate)) return false;
        if(!Objects.equals(temporal.variable(), atom.temporal().variable())) return false; //TODO is this needed??
        if(temporal.constant() != atom.temporal.constant()) return false;
        if(args.length != atom.args.length) return false;
        for(int i = 0; i<args.length; i++){
            if(!Objects.equals(args[i], atom.args[i]) && !(args[i] instanceof Variable && atom.args[i] instanceof Variable))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(predicate, temporal);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }
}
