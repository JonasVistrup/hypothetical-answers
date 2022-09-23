import java.util.ArrayList;
import java.util.List;

public class ModifiedSLDResolution {
    public static List<HAnswer> preprocess(Program program, AtomList query){
        List<HAnswer> hAnswers = new ArrayList<>();
        inOrderTraversal(hAnswers, query, new Substitution(), program, 1);

        return hAnswers;
    }

    private static void inOrderTraversal(List<HAnswer> hAnswers, AtomList goal, Substitution sub, Program program, int level){
        if(isFinished(goal)){
            hAnswers.add(new HAnswer(sub, goal));
            return;
        }

        Atom selected = selectAtom(goal, program);
        for(Clause clause: program.clauses){
            Clause clauseInstance = clause.getInstance(level);
            Substitution unifier = Unify.findMGU(selected, clauseInstance.head);
            if(unifier != null){
                AtomList new_goal = new AtomList(goal);

                new_goal.remove(selected);
                new_goal = new_goal.applySub(unifier);

                new_goal.addAll(clauseInstance.body.applySub(unifier));

                Substitution new_sub = Substitution.composition(sub, unifier);

                inOrderTraversal(hAnswers, new_goal, new_sub, program, level+1);
            }
        }
    }

    /**
     *
     * @param goal
     * @param program
     * @return The last IDB atom in goal
     */
    private static Atom selectAtom(AtomList goal, Program program) {
        for(int i = goal.size()-1; i>0; i--){
            if(goal.get(i).predicate.IDB){
                return goal.get(i);
            }
        }

        assert goal.get(0).predicate.IDB;
        return goal.get(0);
    }

    private static boolean isFinished(AtomList goal) {
        for(Atom a: goal){
            if(a.predicate.IDB){
                return false;
            }
        }
        return true;
    }
}
