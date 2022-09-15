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
        variables.add(new Variable("T1"));
        variables.add(new Variable("T"));
        Atom head = new Atom(predicates.get(0), new Temporal(variables.get(3), 1), constants.get(0), variables.get(0));
        Atom body1 = new Atom(predicates.get(1), new Temporal(variables.get(3), 0), variables.get(0));
        Atom body2 = new Atom(predicates.get(1), new Temporal(variables.get(3), -1), constants.get(1));
        Atom query = new Atom(predicates.get(0), new Temporal(variables.get(4), 0), constants.get(0), constants.get(2));
        Atom head2 = new Atom(predicates.get(0), new Temporal(variables.get(3), -3), constants.get(0), constants.get(2));
        clauses.add(new Clause(head,body1, body2));
        clauses.add(new Clause(head2));


        System.out.println("\n----------TEST1-----------\n");
        System.out.println(clauses.get(0));
        System.out.println(clauses.get(1)+"\n");

        Program p = new Program(clauses);
        System.out.println("P is IDB:"+p.isIDB(predicates.get(0))+", Q is IDB:"+p.isIDB(predicates.get(1))+"\n");
        System.out.println("Query: "+query);
        List<HypAnswer> answers = SLDResolution.preprocess(query, p);
        for(HypAnswer answer: answers){
            System.out.println(answer.toString(query));
        }

        System.out.println("\n----------TEST2-----------\n");
        System.out.println(clauses.get(0));
        System.out.println(clauses.get(1)+"\n");

        query = new Atom(predicates.get(0), new Temporal(variables.get(4), 0), variables.get(2), variables.get(2));

        System.out.println("Query: "+query);

        answers = SLDResolution.preprocess(query, p);
        for(HypAnswer answer: answers){
            System.out.println(answer.toString(query));
        }


    }
}
