import java.util.ArrayList;
import java.util.List;

/**
 * A tester class
 */
public class Main {
    public static void main(String[] args){
        ArrayList<Predicate> predicates = new ArrayList<>();
        ArrayList<Constant> constants = new ArrayList<>();
        ArrayList<Variable> variables = new ArrayList<>();
        ArrayList<Clause> clauses = new ArrayList<>();
        predicates.add(new Predicate(2, "P"));
        predicates.add(new Predicate(1, "Q"));
        constants.add(new Constant("a"));
        constants.add(new Constant("b"));
        constants.add(new Constant("c"));
        variables.add(new Variable("x"));
        variables.add(new Variable("y"));
        variables.add(new Variable("z"));
        Atom head = new Atom(predicates.get(0), constants.get(0), variables.get(0));
        Atom head2 = new Atom(predicates.get(1), constants.get(1));
        Atom head3 = new Atom(predicates.get(1), constants.get(2));

        clauses.add(new Clause(head,head2, head3));
        System.out.println(clauses.get(0));

        Program p = new Program(clauses);
        System.out.println("P is IDB:"+p.isIDB(predicates.get(0))+", Q is IDB:"+p.isIDB(predicates.get(1)));

        List<HypAnswer> answers = SLDResolution.preprocess(head, p);
        System.out.println("SIZE:"+answers.size());
        for(HypAnswer answer: answers){
            System.out.println(answer.premise().get(0));
            System.out.println(answer.premise().get(1));
        }


    }
}
