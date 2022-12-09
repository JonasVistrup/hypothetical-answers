import Jonas.Hypothetical.HypotheticalReasoner;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProgramBuilderTest {

    @Test
    void reverse() {
        HypotheticalReasoner h = new HypotheticalReasoner("TempProgramReverse");
        assertEquals("Program:\nFlag(X,T)<-Temp(X,high,T)\nCool(X,T+1)<-Flag(X,T),Flag(X,T+1)\nShdn(X,T+1)<-Cool(X,T),Flag(X,T+1)\nMalf(X,T-2)<-Shdn(X,T)\n", h.toString());
    }

    @Test
    void predicateTest(){
        HypotheticalReasoner h = new HypotheticalReasoner();
        h.addClause("GTU(Topic,T)<-GTD(Topic,Pop1,T),GTD(Topic,Pop2,T-1),GTD(Topic,Pop3,T-2),>(Pop1,Pop2),>(Pop2,Pop3),>(Pop3,5)");
        h.query("GTU(Topic,T)");
        h.nextTime("GTD(christmas, 10, 0)");
        h.nextTime("GTD(christmas, 15, 1)");
    }



}