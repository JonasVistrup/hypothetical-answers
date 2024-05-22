import Jonas.Hypothetical.Answer;
import Jonas.Hypothetical.HypotheticalReasoner;
import Jonas.NegationStuff.NegatedHypothetical;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NHypotheticalReasonerTest {

    NegatedHypothetical h;

    @BeforeEach
    void setUp() {
    }


    @Test
    @DisplayName("Simple Test")
    void evidenceTest1() {
        h = new NegatedHypothetical();
        h.addClause("P(T)<-Q(T-1),Q(T),R(T)");
        h.query("P(T)");
        h.nextTime("Q(0)");
        List<Answer> S0 = h.supportedAnswers();
        h.nextTime("Q(1),R(1)");
        List<Answer> S1 = h.supportedAnswers();
        h.nextTime("");
        List<Answer> S2 = h.supportedAnswers();


        assertEquals(1, S0.size());
        assertEquals(2, S1.size());
        assertEquals("[{(T/1)},{Q(0)},{Q(1),R(1)}]", S0.get(0).toString(h.getQuery()));

        if (S1.get(0).evidence.size() == 3) {
            assertEquals("[{(T/1)},{Q(0),Q(1)},{R(1)}]", S1.get(1).toString(h.getQuery()));
            assertEquals("[{(T/1)},{Q(0),Q(1),R(1)},{}]", S1.get(0).toString(h.getQuery()));
        }
    }


    @Test
    @DisplayName("Simple Negation Test")
    void negationTestSimple() {
        h = new NegatedHypothetical();
        h.addClause("P(T)<-Q(T+1),~R(T)");
        h.addClause("R(T)<-S(T-100)");
        h.query("P(T)");
        List<Answer> P = h.preprocessAnswers();
        System.out.println(P);

        h.nextTime("Q(0)");
        List<Answer> S0 = h.supportedAnswers();
        System.out.println(S0);

        h.nextTime("S(1),Q(1)");
        List<Answer> S1 = h.supportedAnswers();
        System.out.println(S1);


    }

    @Test
    @DisplayName("Simple Negation Test2")
    void negationTestSimple2() {
        h = new NegatedHypothetical();
        h.addClause("P(T)<-Q(T+1),~R(T)");
        h.addClause("R(T)<-~L(T+1)");
        h.addClause("L(T)<-S(T-1)");
        h.query("P(T)");
        List<Answer> P = h.preprocessAnswers();
        System.out.println(P);

        h.nextTime("Q(0)");
        List<Answer> S0 = h.supportedAnswers();
        System.out.println(S0);

        h.nextTime("S(1),Q(1)");
        List<Answer> S1 = h.supportedAnswers();
        System.out.println(S1);


    }

    @Test
    @DisplayName("Simple Negation Test3")
    void negationTestSimple3() {
        h = new NegatedHypothetical();
        h.addClause("P(T)<-Q(T+1),~R(X,T)");
        h.addClause("R(a,T)<-S(T)");
        h.addClause("R(b,T)<-S(T+1)");
        h.query("P(T)");
        List<Answer> P = h.preprocessAnswers();
        System.out.println(P);

        h.nextTime("Q(0)");
        List<Answer> S0 = h.supportedAnswers();
        System.out.println();
        for(Long key: h.S.keySet()){
            System.out.println("\t"+h.S.get(key));
        }

        h.nextTime("Q(1)");
        List<Answer> S1 = h.supportedAnswers();
        System.out.println();
        for(Long key: h.S.keySet()){
            System.out.println("\t"+h.S.get(key));
        }


    }

}