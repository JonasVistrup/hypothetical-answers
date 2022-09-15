

import java.util.ArrayList;
import java.util.List;

public class Substitution {

    private List<Sub> subs;

    public Substitution(){
        this.subs = new ArrayList<>();
    }

    public Substitution(Variable from, Term to){
        this.subs = new ArrayList<>();
        this.subs.add(new Sub(from, to));
    }

    public boolean isInSupport(Variable var){
        for(Sub sub: subs){
            if(sub.from.equals(var)) return true;
        }
        return false;
    }

    public List<Sub> subs(){
        return subs;
    }

    /**
     * Finds the substitution for variable var.
     * @param var   The variable for which the substitution should be found.
     * @return Substitution for variable var. Null if var is not in the support of the substitution.
     */
    public Term getSubstitution(Variable var){
        for(Sub sub: subs){
            if(sub.from.equals(var)) return sub.to;
        }
        return null;
    }

    public static Substitution composition(Substitution subs1,Substitution subs2){
        Substitution resultingSubs = new Substitution();

        for(Sub sub: subs1.subs){
            Sub new_sub = new Sub(sub.from, sub.to.applySub(subs2));
            if (!new_sub.from.equals(new_sub.to)){
                resultingSubs.subs.add(new_sub);
            }
        }
        for(Sub sub: subs2.subs){
            if(!resultingSubs.isInSupport(sub.from)){
                resultingSubs.subs.add(sub);
            }
        }
        return resultingSubs;
    }

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

    public String toString(Atom relevantQuery) {
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

    private static boolean relevantSub(Sub s, Atom relevantQuery) {
        for(Term t: relevantQuery.args()){
            if(t.equals(s.from())) return true;
        }
        return relevantQuery.temporal().variable().equals(s.from());
    }

    public static final class Sub {
        private final Variable from;
        private final Term to;

        public Sub(Variable from, Term to){
            this.from = from;
            this.to = to;
        }

        public Variable from(){
            return from;
        }

        public Term to(){
            return to;
        }
        @Override
        public String toString() {
            return from.name()+"/"+to.name();
        }
    }
}
