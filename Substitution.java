

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
        return super.toString();
    }

    private record Sub(Variable from, Term to) {
    }
}
