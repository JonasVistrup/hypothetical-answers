package Jonas.SLD;

import Jonas.Logic.*;

import java.util.ArrayList;
import java.util.List;


/**
 * A class for performing SLD Resolution.
 */
public class SLDResolution {
    /**
     * Finds and returns a list of all valid substitutions, which makes the query true in the program.
     * @param program the program.
     * @param query a list of atoms
     * @return all valid substitutions.
     */
    public static List<Substitution> findSubstitutions(Program program, AtomList query){ //TODO might add the same substitution multiple times (SHOULD BE SET INSTEAD OF LIST?)
        List<Substitution> answers = new ArrayList<>();

        inOrderTraversal(answers, query, new Substitution(), program, 1);

        return answers;
    }

    /**
     * Recursively performs depth first search of the SLD-tree.
     * @param answers the list of valid substitutions found so far.
     * @param goal the current list of atoms to try to unify with the program.
     * @param sub the current substitution applied to the goal.
     * @param program the program.
     * @param level the current level of the SLD-tree.
     */
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
     * Selects an atom in the goal.
     * @param goal a list of atoms.
     * @param program the program.
     * @return The last atom in goal
     */
    private static Atom selectAtom(AtomList goal, Program program) {
        assert !goal.isEmpty();
        return goal.get(goal.size()-1);
    }

}


