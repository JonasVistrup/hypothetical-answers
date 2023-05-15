import Jonas.Hypothetical.Answer;
import Jonas.Hypothetical.HypotheticalReasoner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HypotheticalReasonerTest {

    HypotheticalReasoner h;

    @BeforeEach
    void setUp() {

    }


    @Test
    @DisplayName("Simple Test")
    void evidenceTest1() {
        h = new HypotheticalReasoner();
        h.addClause("P(T)<-Q(0),Q(1),R(T)");
        h.query("P(T)");
        h.nextTime("Q(0)");
        List<Answer> S0 = h.supportedAnswers();
        h.nextTime("Q(1),R(1)");
        List<Answer> S1 = h.supportedAnswers();
        h.nextTime("");
        List<Answer> S2 = h.supportedAnswers();


        assertEquals(1, S0.size());
        assertEquals(2, S1.size());
        assertEquals("[{},{Q(0)},{Q(1),R(T)}]", S0.get(0).toString(h.getQuery()));

        if (S1.get(0).evidence.size() == 3) {
            assertEquals("[{},{Q(0),Q(1)},{R(T)}]", S1.get(1).toString(h.getQuery()));
            assertEquals("[{(T/1)},{Q(0),Q(1),R(1)},{}]", S1.get(0).toString(h.getQuery()));
        } else {
            assertEquals("[{},{Q(0),Q(1)},{R(T)}]", S1.get(0).toString(h.getQuery()));
            assertEquals("[{(T/1)},{Q(0),Q(1),R(1)},{}]", S1.get(1).toString(h.getQuery()));
        }

        assertEquals(2, S2.size());
        if (S2.get(0).evidence.size() == 3) {
            assertEquals("[{},{Q(0),Q(1)},{R(T)}]", S2.get(1).toString(h.getQuery()));
            assertEquals("[{(T/1)},{Q(0),Q(1),R(1)},{}]", S2.get(0).toString(h.getQuery()));
        } else {
            assertEquals("[{},{Q(0),Q(1)},{R(T)}]", S2.get(0).toString(h.getQuery()));
            assertEquals("[{(T/1)},{Q(0),Q(1),R(1)},{}]", S2.get(1).toString(h.getQuery()));
        }
        assertEquals("Program:\nP(T)<-Q(0),Q(1),R(T)\n\nPreprocessing Answers:\n\t[{},{},{Q(0),Q(1),R(T)}]\n\nSupported Answers:\n\t[{(T/1)},{Q(0),Q(1),R(1)},{}]\n\t[{},{Q(0),Q(1)},{R(T)}]\n\nAnswers:\n\t{(T/1)}\n", h.toString());

    }


    @Test
    @DisplayName("Simple Test with propagation")
    void evidenceTest2() {
        h = new HypotheticalReasoner();
        h.addClause("P(T)<-Q(0),Q(2),R(T)");
        h.query("P(T)");
        h.nextTime("Q(0)");
        List<Answer> S0 = h.supportedAnswers();
        List<Answer> H = h.preprocessingAnswers();
        h.nextTime("Q(1),R(1)");
        List<Answer> S1 = h.supportedAnswers();
        h.nextTime("Q(2)");
        List<Answer> S2 = h.supportedAnswers();


        assertEquals(1, S0.size());
        //assertEquals(2, S1.size());
        assertEquals("[{},{Q(0)},{Q(2),R(T)}]", S0.get(0).toString(h.getQuery()));
        assertEquals("[{(T/1)},{Q(0),R(1)},{Q(2)}]", S1.get(0).toString(h.getQuery()));
        assertEquals(2, S2.size());
        assertEquals("[{(T/1)},{Q(0),R(1),Q(2)},{}]", S2.get(0).toString(h.getQuery()));
        assertEquals("Program:\nP(T)<-Q(0),Q(2),R(T)\n\nPreprocessing Answers:\n\t[{},{},{Q(0),Q(2),R(T)}]\n\nSupported Answers:\n\t[{(T/1)},{Q(0),R(1),Q(2)},{}]\n\t[{},{Q(0),Q(2)},{R(T)}]\n\nAnswers:\n\t{(T/1)}\n", h.toString());

    }

    @Test
    @DisplayName("Test from Paper")
    void PaperTest() {
        h = new HypotheticalReasoner();
        h.addClause("Flag(X,T)<-Temp(X,high,T)");
        h.addClause("Cool(X,T+1)<-Flag(X,T),Flag(X,T+1)");
        h.addClause("Shdn(X,T+1)<-Cool(X,T),Flag(X,T+1)");
        h.addClause("Malf(X,T-2)<-Shdn(X,T)");

        String pString = "Program:\nFlag(X,T)<-Temp(X,high,T)\nCool(X,T+1)<-Flag(X,T),Flag(X,T+1)\nShdn(X,T+1)<-Cool(X,T),Flag(X,T+1)\nMalf(X,T-2)<-Shdn(X,T)\n";
        assertEquals(pString, h.toString());

        h.query("Malf(X,T)");
        assertEquals(pString + "\nPreprocessing Answers:\n\t[{},{},{Temp(X,high,T),Temp(X,high,T+1),Temp(X,high,T+2)}]\n\n" + "Supported Answers:\n\n" + "Answers:\n", h.toString());

        h.nextTime("Temp(wt25,high, 0)");
        assertEquals(pString + "\nPreprocessing Answers:\n\t[{},{},{Temp(X,high,T),Temp(X,high,T+1),Temp(X,high,T+2)}]\n\n" + "Supported Answers:\n\t[{(X/wt25),(T/0)},{Temp(wt25,high,0)},{Temp(wt25,high,1),Temp(wt25,high,2)}]\n\n" + "Answers:\n", h.toString());


    }

    @Test
    @DisplayName("Old Sofia Test")
    void sofiaProgram(){
        HypotheticalReasoner h = new HypotheticalReasoner("SofiaProgramOld");
        h.query("Trending(Topic,Region,T)");
        h.nextTime("GTD(christmas, capital, 10, 0),Tweets(christmas, capital, 60, 0)");
        h.nextTime("GTD(christmas, capital, 6, 1),Tweets(christmas, capital, 40, 1)");
        h.nextTime("GTD(christmas, capital, 10, 2),Tweets(christmas, capital, 51, 2)");
        h.nextTime("GTD(christmas, capital, 15, 3),Tweets(christmas, capital, 60, 3)");
        h.nextTime("GTD(christmas, capital, 25, 4),Tweets(christmas, capital, 70, 4)");
    }
}