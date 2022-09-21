import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModifiedSLDResolutionTest {
    ProgramBuilder pB;

    @BeforeEach
    void setUp(){
        pB = new ProgramBuilder();
    }


    @Test
    @DisplayName("Simple prepocessing test")
    void testPreprocessing1(){
        pB.addClause("A(T)<-B(T)");
        Program p = pB.getProgram();
        Atom query = pB.parseAtom("A(3)");
        AtomList queryList = new AtomList();
        queryList.add(query.getInstance(0));
        List<HAnswer> answerListGiven = ModifiedSLDResolution.preprocess(p, queryList);
        assertEquals(answerListGiven.size(), 1, "Only one possible unification");
        HAnswer answer = answerListGiven.get(0);
        assertEquals(0, answer.temporalPremise.size(), "Premise contains only constants");
        assertEquals(1,answer.constantPremise.size(), "Premise contains one constant premise");
        assertEquals("[{(T/3)},{B(3)}]",answer.toString() , "Only one possible unification");
    }

    @Test
    @DisplayName("Simple prepocessing test with two atoms in query")
    void testPreprocessing2(){
        pB.addClause("A(T)<-B(T)");
        Program p = pB.getProgram();
        AtomList queryList = new AtomList();
        queryList.add(pB.parseAtom("A(3)").getInstance(0));
        queryList.add(pB.parseAtom("A(4)").getInstance(0));
        List<HAnswer> answerListGiven = ModifiedSLDResolution.preprocess(p, queryList);
        assertEquals(answerListGiven.size(), 1, "Only one possible unification");
        HAnswer answer = answerListGiven.get(0);
        assertEquals(0, answer.temporalPremise.size(), "Premise contains only constants");
        assertEquals(2,answer.constantPremise.size(), "Premise contains one constant premise");
        assertEquals("[{(T/4),(T/3)},{B(3),B(4)}]",answer.toString() , "Only one possible unification");
    }

    @Test
    @DisplayName("Complex processing test")
    void testPreprocessing3(){
        pB.addClause("Flag(X,T)<-Temp(X,high,T)");
        pB.addClause("Cool(X,T+1)<-Flag(X,T),Flag(X,T+1)");
        pB.addClause("Shdn(X,T+1)<-Cool(X,T),Flag(X,T+1)");
        pB.addClause("Malf(X,T-2)<-Shdn(X,T)");
        Program p = pB.getProgram();
        AtomList queryList = new AtomList();
        AtomInstance query = pB.parseAtom("Malf(X,T)").getInstance(0);
        queryList.add(query);
        List<HAnswer> answerListGiven = ModifiedSLDResolution.preprocess(p, queryList);
        assertEquals(answerListGiven.size(), 1, "Only one possible unification");
        HAnswer answer = answerListGiven.get(0);
        assertEquals(3, answer.temporalPremise.size(), "Premise contains 3 variable premises");
        assertEquals(0,answer.constantPremise.size(), "Premise contains no constant premise");
        assertEquals("[{},{Temp(X,high,T),Temp(X,high,T+1),Temp(X,high,T+2)}]",answer.toString(query) , "Only one possible unification");
    }
}