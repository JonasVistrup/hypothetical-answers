import java.util.ArrayList;
import java.util.List;

/**
 * A tester class
 */
public class Main {
    public static void main(String[] args){
        ProgramBuilder.addClause("Flag(X1,T1)<-Temp(X1, high, T1)");
        ProgramBuilder.addClause("Cool(X2,T2+1)<-Flag(X2,T2),Flag(X2,T2+1)");
        ProgramBuilder.addClause("Shdn(X3,T3+1)<-Cool(X3,T3),Flag(X3,T3+1)");
        ProgramBuilder.addClause("Malf(X4,T4-2)<-Shdn(X4,T4)");

        Program p = ProgramBuilder.getProgram();
        System.out.println("Temp is IDB:"+p.isIDB(ProgramBuilder.predicates.get("Temp")));
        Atom query = ProgramBuilder.parseAtom("Malf(X,T)");
        Reasoner r = new Reasoner(p,query);

        System.out.println(p);

        System.out.println("Query:"+query);
        System.out.println("Hyp Answers:");
        for(HypAnswer a: r.hypAnswers()){
            System.out.println(a.toString());
        }


    }

    public static void oldHorribleTest(){
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
