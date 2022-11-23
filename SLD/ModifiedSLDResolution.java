package SLD;

import Hypothetical.PreprocessingAnswer;
import Logic.*;

import java.util.ArrayList;
import java.util.List;


/**
 * A class with functions for performing the preprocessing of the hypothetical answer.
 */
public class ModifiedSLDResolution {
    /**
     * Returns the hypothetical answers generated by a query of a program.
     * @param program the program.
     * @param query the query.
     * @return hypothetical answers.
     */
    public static List<PreprocessingAnswer> preprocess(Program program, AtomList query){
        List<PreprocessingAnswer> hAnswers = new ArrayList<>();
        inOrderTraversal(hAnswers, query, new Substitution(), program, 1);

        return hAnswers;
    }

    /**
     * Recursively performs depth first search of the SLD-tree, but stops whenever a goal which only contains atoms with EDB predicates.
     * @param hAnswers the list of hypothetical answer found so far.
     * @param goal the current list of atoms to try to unify with the program.
     * @param sub the current substitution applied to the goal.
     * @param program the program.
     * @param level the current level of the SLD-tree.
     */
    private static void inOrderTraversal(List<PreprocessingAnswer> hAnswers, AtomList goal, Substitution sub, Program program, int level){
        if(isFinished(goal)){
            hAnswers.add(new PreprocessingAnswer(sub, goal));
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
     * Selects an IDB atom from the goal.
     * @param goal list of atoms.
     * @param program the program.
     * @return the last IDB atom in goal.
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

    /**
     * Returns whether the goal consists of only EDB atoms.
     * @param goal a list of atoms
     * @return true iff all atoms in goal is EDB.
     */
    private static boolean isFinished(AtomList goal) {
        for(Atom a: goal){
            if(a.predicate.IDB){
                return false;
            }
        }
        return true;
    }
}
