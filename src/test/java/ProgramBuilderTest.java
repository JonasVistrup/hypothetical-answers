import Jonas.Hypothetical.HypotheticalReasoner;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProgramBuilderTest {

    @Test
    void reverse() {
        HypotheticalReasoner h = new HypotheticalReasoner("TempProgramReverse");
        assertEquals("Program:\nFlag(X,T)<-Temp(X,high,T)\nCool(X,T+1)<-Flag(X,T),Flag(X,T+1)\nShdn(X,T+1)<-Cool(X,T),Flag(X,T+1)\nMalf(X,T-2)<-Shdn(X,T)\n", h.toString());
    }

}