import java.util.ArrayList;
import java.util.List;
//TODO
public class SLDResolution {
    public static List<HypAnswer> preprocess(Atom query, Program program) {
        List<HypAnswer> answers = new ArrayList<>();

        List<Atom> goal = new ArrayList<>();
        goal.add(query);

        sldInOrderTraversal(answers, goal, program, new Substitution());

        return answers;
    }

    private static void sldInOrderTraversal(List<HypAnswer> answers, List<Atom> goal, Program program, Substitution sub) {
        if (isFinished(goal, program)) {
            answers.add(new HypAnswer(sub, goal));
        } else {
            Atom selectedAtom = select(goal, program);
            assert program.isIDB(selectedAtom.predicate());
            for (Clause c : program.clauses()) {
                Substitution unifier = findMGU(c.head(), selectedAtom);
                if(unifier != null){
                    List<Atom> new_goal = new ArrayList<>();
                    for(Atom atom: goal){
                        if(atom != selectedAtom){
                            new_goal.add(atom.applySub(unifier));
                        }
                    }
                    for(Atom atom: c.body()){
                        new_goal.add(atom.applySub(unifier));
                    }
                    sldInOrderTraversal(answers, new_goal, program, Substitution.composition(sub, unifier));
                }
            }
        }
    }

    private static Substitution findMGU(Atom head, Atom selectedAtom) {
        if(head.predicate() != selectedAtom.predicate()){
            return null;
        }
        Substitution sub = new Substitution();
        for(int i = 0; i<head.args().length; i++){
            if(head.args()[i] != selectedAtom.args()[i]){
                Substitution unifier = unify(head.args()[i], selectedAtom.args()[i]);
                if(unifier == null){
                    return null;
                }else{
                    sub = Substitution.composition(sub, unifier);
                }
            }
        }

        return sub;
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

    private static Atom select(List<Atom> goal, Program program) {
        for(int i = goal.size()-1; i>0; i--){
            if(program.isIDB(goal.get(i).predicate())){
                return goal.get(i);
            }
        }
        assert program.isIDB(goal.get(0).predicate());
        return goal.get(goal.size()-1);
    }


    private static boolean isFinished(List<Atom> goal, Program program) {
        for(Atom atom : goal) {
            if(program.isIDB(atom.predicate())){
                return false;
            }
        }
        return true;
    }
}
