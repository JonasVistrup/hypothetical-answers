public class Unify {
    static Substitution findMGU(Atom selectedAtom, Atom head) {

        if(head.predicate() != selectedAtom.predicate()){
            return null;
        }
        Substitution sub = new Substitution();
        for(int i = 0; i<head.args().length; i++){
            if(head.args()[i] != selectedAtom.args()[i]){
                Substitution unifier = unify(selectedAtom.args()[i], head.args()[i]);
                if(unifier == null){
                    return null;
                }else{
                    sub = Substitution.composition(sub, unifier);
                    selectedAtom = selectedAtom.applySub(unifier);
                }
            }
        }
        Substitution temp_unifier = unifyTemporal(selectedAtom.temporal(), head.temporal());
        if(temp_unifier == null){
            return null;
        }else{
            sub = Substitution.composition(sub, temp_unifier);
        }
        return sub;
    }

    private static Substitution unifyTemporal(Temporal one, Temporal two){
        if(one.variable() == null && two.variable() == null){
            if(one.constant() != two.constant()){
                return null;
            }else{
                return new Substitution();
            }
        }else if(one.variable() == null && two.variable() != null){
            if(one.constant()- two.constant() < 0) return null;
            return new Substitution(two.variable(), new Temporal(null, one.constant()- two.constant()));
        }else if(one.variable() != null && two.variable() == null){
            if(two.constant() - one.constant() < 0) return null;
            return new Substitution(one.variable(), new Temporal(null, two.constant()- one.constant()));
        }else{
            return new Substitution(two.variable(), new Temporal(one.variable(), one.constant() - two.constant()));
        }
    }

    private static Substitution unify(Term one, Term two){
        if(one instanceof Variable){
            return new Substitution((Variable) one, two); //No matter if two is a constant or a variable, we choose to sub one with two
        }else{
            if(two instanceof Variable){
                return new Substitution((Variable) two, one);
            }else{
                assert !one.equals(two);
                return null;
            }
        }
    }
}
