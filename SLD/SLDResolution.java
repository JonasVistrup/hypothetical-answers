import java.util.ArrayList;
import java.util.List;

public class SLDResolution {
    public static List<Substitution> findSubstitutions(Program program, AtomList query){
        List<Substitution> answers = new ArrayList<>();

        inOrderTraversal(answers, query, new Substitution(), program, 1);

        return answers;
    }

    private static void inOrderTraversal(List<Substitution> answers, AtomList goal, Substitution sub, Program program, int level){
        if(goal.isEmpty()){
            answers.add(sub);
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

                inOrderTraversal(answers, new_goal, new_sub, program, level+1);
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
        assert !goal.isEmpty();
        return goal.get(goal.size()-1);
    }

}


