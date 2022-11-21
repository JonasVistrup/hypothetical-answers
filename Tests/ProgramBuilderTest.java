import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProgramBuilderTest {

    @Test
    void reverse() {
        HypotheticalReasoner h = new HypotheticalReasoner("TempProgramReverse");
        assertEquals("Program:\nFlag(X,T)<-Temp(X,high,T)\nCool(X,T+1)<-Flag(X,T),Flag(X,T+1)\nShdn(X,T+1)<-Cool(X,T),Flag(X,T+1)\nMalf(X,T-2)<-Shdn(X,T)\n", h.toString());
    }

    @Test
    void negation() {
        HypotheticalReasoner h = new HypotheticalReasoner("TempProgramNegated");
        String programString = "Program:\n" +
                "GVS(X,T)<-GCM(X,T),GBOL(X,T)\n" +
                "ST(X,us,T+1)<-BCA(X,T+2)\n" +
                "ST(X,us,T+1)<--GVS(X,T),-ST(X,us,T)\n" +
                "ST(X,ic,T+1)<-ST(X,us,T),-GVS(X,T)\n" +
                "Risk(X,T)<-ST(X,ic,T+2)\n";
        assertEquals(programString, h.toString());
    }
}