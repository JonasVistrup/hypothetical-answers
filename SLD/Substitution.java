import java.util.ArrayList;
import java.util.List;

public class Substitution {

    private final List<Sub> subs; //TODO: convert to hashmap for speedup

    public Substitution(){
        this.subs = new ArrayList<>();
    }

    public Substitution(Variable from, Term to){
        this.subs = new ArrayList<>();
        this.subs.add(new Sub(from, to));
    }

    public List<Sub> subs(){
        return subs;
    }

    public static Substitution composition(Substitution subs1, Substitution subs2){
        Substitution resultingSubs = new Substitution();

        for(Sub sub: subs1.subs){
            Sub new_sub = new Sub(sub.from, sub.to.applySub(subs2));
            if (new_sub.from != new_sub.to){
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


    public boolean isInSupport(Variable var){
        for(Sub sub: subs){
            if(sub.from == var) return true;
        }
        return false;
    }

    public Term getSubstitution(Variable var){
        for(Sub sub: subs){
            if(sub.from == var) return sub.to;
        }
        return null;
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
        for(Term t: relevantQuery.args){
            if(t == s.from) return true;
        }
        return relevantQuery.temporal.tVar == s.from;
    }

}