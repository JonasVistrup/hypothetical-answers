import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//TODO
public class NormalSLDResolution {
    public static List<Substitution> SLDResolution(Goal query, Program program) {
        List<Substitution> answers = new ArrayList<>();

        sldInOrderTraversal(answers, query, program, new Substitution());

        return answers;
    }

    private static void sldInOrderTraversal(List<Substitution> answers, Goal goal, Program program, Substitution sub) {
        if (goal.atoms().length==0) {
            answers.add(sub);
        } else {
            Atom selectedAtom = select(goal, program);

            for (Clause c : program.clauses()) {
                Substitution unifier = Unify.findMGU(selectedAtom, c.head());
                if(unifier != null){
                    Goal new_goal = goal.remove(selectedAtom);
                    new_goal = new_goal.add(c.body());
                    new_goal = new_goal.applySub(unifier);

                    sldInOrderTraversal(answers, new_goal, program, Substitution.composition(sub, unifier));
                }
            }
        }
    }

    private static Atom select(Goal goal, Program program) {
        return goal.atoms()[goal.atoms().length-1];
    }


}
