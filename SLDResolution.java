import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//TODO
public class SLDResolution {
    public static List<HypAnswer> preprocess(Atom query, Program program) {
        List<HypAnswer> answers = new ArrayList<>();

        sldInOrderTraversal(answers, new Goal(query), program, new Substitution());

        return answers;
    }

    private static void sldInOrderTraversal(List<HypAnswer> answers, Goal goal, Program program, Substitution sub) {
        System.out.println("Goal: "+goal.toString());
        if (isFinished(goal, program)) {
            answers.add(new HypAnswer(sub, Arrays.asList(goal.atoms())));
        } else {
            Atom selectedAtom = select(goal, program);
            assert program.isIDB(selectedAtom.predicate());
            for (Clause c : program.clauses()) {
                System.out.println("\nSELECTED CLAUSE:"+c);
                Substitution unifier = Unify.findMGU(selectedAtom, c.head());
                System.out.println("Selected ATOM:"+selectedAtom+ "; Selected HEAD:"+c.head() + "; RESULT:"+unifier);
                if(unifier != null){
                    Goal new_goal = goal.remove(selectedAtom);
                    System.out.println("Body:");
                    for(Atom a: c.body()){
                        System.out.print("\t"+a);
                    }
                    System.out.println("");
                    new_goal = new_goal.add(c.body());
                    new_goal = new_goal.applySub(unifier);
                    if(!new_goal.containsNegativeTime()){
                        sldInOrderTraversal(answers, new_goal, program, Substitution.composition(sub, unifier));
                    }
                }
            }
        }
    }

    private static Atom select(Goal goal, Program program) {
        for(int i = goal.atoms().length-1; i>0; i--){
            if(program.isIDB(goal.atoms()[i].predicate())){
                return goal.atoms()[i];
            }
        }
        assert program.isIDB(goal.atoms()[0].predicate());
        return goal.atoms()[goal.atoms().length-1];
    }


    private static boolean isFinished(Goal goal, Program program) {
        for(Atom atom : goal.atoms()) {
            if(program.isIDB(atom.predicate())){
                return false;
            }
        }
        return true;
    }
}
