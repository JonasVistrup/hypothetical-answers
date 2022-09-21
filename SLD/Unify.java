public class Unify {
    static Substitution findMGU(AtomInstance selectedAtom, AtomInstance head) {

        if(head.predicate != selectedAtom.predicate){
            return null;
        }
        Substitution sub = new Substitution();
        for(int i = 0; i<selectedAtom.argsInst.size(); i++){
            TermInstance selectInstance = selectedAtom.argsInst.get(i);
            TermInstance headInstance = head.argsInst.get(i);

            if(headInstance != selectInstance){
                Substitution unifier = unify(selectInstance, headInstance);
                if(unifier == null){
                    return null;
                }else{
                    sub = Substitution.composition(sub, unifier);
                    selectedAtom = selectedAtom.applySub(unifier);
                }
            }
        }
        Substitution temp_unifier = unifyTemporal(selectedAtom.temporal, head.temporal);
        if(temp_unifier == null){
            return null;
        }else{
            sub = Substitution.composition(sub, temp_unifier);
        }
        return sub;
    }

    private static Substitution unifyTemporal(TemporalInstance one, TemporalInstance two){
        if(one.tVarInst == null && two.tVarInst == null){
            if(one.constant != two.constant){
                return null;
            }else{
                return new Substitution();
            }
        }else if(one.tVarInst == null){
            if(one.constant - two.constant < 0) return null;
            return new Substitution(two.tVarInst, new TemporalInstance(null, one.constant- two.constant));
        }else if(two.tVarInst == null){
            if(two.constant - one.constant < 0) return null;
            return new Substitution(one.tVarInst, new TemporalInstance(null, two.constant- one.constant));
        }else{
            return new Substitution(two.tVarInst, new TemporalInstance(one.tVarInst, one.constant - two.constant));
        }
    }

    private static Substitution unify(TermInstance one, TermInstance two){
        if(two instanceof VariableInstance){
            return new Substitution((VariableInstance) two, one); //No matter if two is a constant or a variable, we choose to sub two with one
        }else{
            if(one instanceof VariableInstance){
                return new Substitution((VariableInstance) one, two);
            }else{
                assert !one.equals(two);
                return null;
            }
        }
    }
}
