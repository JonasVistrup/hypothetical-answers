import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HypotheticalReasonerTest {

    HypotheticalReasoner h;

    @BeforeEach
    void setUp(){

    }

    @Test
    @DisplayName("Simple Test")
    void evidenceTest(){
        h = new HypotheticalReasoner();
        h.addClause("P(T)<-Q(0),Q(1),R(T)");
        h.query("P(T)");
        h.nextTime("Q(0)");
        List<EvidenceAnswer> S0 = h.evidenceAnswers();
        h.nextTime("Q(1),R(1)");
        List<EvidenceAnswer> S1 = h.evidenceAnswers();
        h.nextTime("");
        List<EvidenceAnswer> S2 = h.evidenceAnswers();


        assertEquals(1, S0.size());
        assertEquals(2, S1.size());
        assertEquals("[{},{Q(0)},{Q(1),R(T)}]",S0.get(0).toString(h.getQuery()));
        assertEquals("[{},{Q(0),Q(1)},{R(T)}]",S1.get(0).toString(h.getQuery()));
        assertEquals("[{(T/1)},{Q(0),Q(1),R(1)},{}]",S1.get(1).toString(h.getQuery()));
        assertEquals(2, S2.size());
        assertEquals("[{},{Q(0),Q(1)},{R(T)}]",S2.get(0).toString(h.getQuery()));
        assertEquals("[{(T/1)},{Q(0),Q(1),R(1)},{}]",S2.get(1).toString(h.getQuery()));
        assertEquals("Program:\nP(T)<-Q(0),Q(1),R(T)\n\nHypothetical Answers:\n\t[{},{Q(0),Q(1),R(T)}]\n\nEvidence Answers:\n\t[{},{Q(0),Q(1)},{R(T)}]\n\t[{(T/1)},{Q(0),Q(1),R(1)},{}]\n\nAnswers:\n\t{(T/1)}\n", h.toString());

    }

    @Test
    @DisplayName("Test from Paper")
    void PaperTest(){
        h = new HypotheticalReasoner();
        h.addClause("Flag(X,T)<-Temp(X,high,T)");
        h.addClause("Cool(X,T+1)<-Flag(X,T),Flag(X,T+1)");
        h.addClause("Shdn(X,T+1)<-Cool(X,T),Flag(X,T+1)");
        h.addClause("Malf(X,T-2)<-Shdn(X,T)");

        String pString = "Program:\nFlag(X,T)<-Temp(X,high,T)\nCool(X,T+1)<-Flag(X,T),Flag(X,T+1)\nShdn(X,T+1)<-Cool(X,T),Flag(X,T+1)\nMalf(X,T-2)<-Shdn(X,T)\n";
        assertEquals(pString, h.toString());

        h.query("Malf(X,T)");
        assertEquals(pString+"\nHypothetical Answers:\n\t[{},{Temp(X,high,T),Temp(X,high,T+1),Temp(X,high,T+2)}]\n\n"+"Evidence Answers:\n\n"+"Answers:\n", h.toString());

        h.nextTime("Temp(wt25,high, 0)");
        assertEquals(pString+"\nHypothetical Answers:\n\t[{},{Temp(X,high,T),Temp(X,high,T+1),Temp(X,high,T+2)}]\n\n"+"Evidence Answers:\n\t[{(X/wt25),(T/0)},{Temp(wt25,high,0)},{Temp(wt25,high,1),Temp(wt25,high,2)}]\n\n"+"Answers:\n", h.toString());


    }

}