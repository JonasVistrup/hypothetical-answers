package Jonas.SLD;


import Jonas.Logic.*;
/**
 * A class for unified two atoms.
 */
public class Unify {

    public static Substitution findMGUAtomList(AtomList list1, AtomList list2) {
        if(list1.size() != list2.size()) return null;
        Substitution result = new Substitution();
        for(int i = 0; i<list1.size(); i++){
            result.add(findMGU(list1.get(i),list2.get(i)));
            list1 = list1.applySub(result);
            list2 = list2.applySub(result);
        }
        return result;
    }


    /**
     * Finds a most general unifier between selectedAtom and head if one exist, otherwise returns 0.
     * @param selectedAtom atom which variables are only substituted if absolutely necessary.
     * @param head atom which variables are substituted whenever possible.
     * @return a substitution which is a most general unifier.
     */
    static Substitution findMGU(Atom selectedAtom, Atom head) {

        if(head.predicate != selectedAtom.predicate){
            return null;
        }
        Substitution sub = new Substitution();
        for(int i = 0; i<selectedAtom.args.size(); i++){
            Term selectInstance = selectedAtom.args.get(i);
            Term headInstance = head.args.get(i);

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

    private static Substitution unifyTemporal(Temporal one, Temporal two){
        if(one.tVar == null && two.tVar == null){
            if(one.tConstant != two.tConstant){
                return null;
            }else{
                return new Substitution();
            }
        }else if(one.tVar == null){
            if(one.tConstant - two.tConstant < 0) return null;
            return new Substitution(two.tVar, new Temporal(null, one.tConstant- two.tConstant));
        }else if(two.tVar == null){
            if(two.tConstant - one.tConstant < 0) return null;
            return new Substitution(one.tVar, new Temporal(null, two.tConstant- one.tConstant));
        }else{
            return new Substitution(two.tVar, new Temporal(one.tVar, one.tConstant - two.tConstant));
        }
    }

    private static Substitution unify(Term one, Term two){
        if(two instanceof Variable){
            return new Substitution((Variable) two, one); //No matter if two is a constant or a variable, we choose to sub two with one
        }else{
            if(one instanceof Variable){
                return new Substitution((Variable) one, two);
            }else{
                assert !one.equals(two);
                return null;
            }
        }
    }


}
