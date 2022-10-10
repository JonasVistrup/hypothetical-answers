import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UpdateAnswerTest {

    ProgramBuilder pB;

    @BeforeEach
    void setUp(){
        pB = new ProgramBuilder();
    }

    @Test
    @DisplayName("Evidence Test")
    void evidenceTest(){
        pB.addClause("P(T)<-Q(0),Q(1),R(T)");
        Program p = pB.getProgram();
        Atom query = pB.parseAtom("P(T)");
        List<PreprocessingAnswer> answerList = ModifiedSLDResolution.preprocess(p, new AtomList(query));
        AtomList T0 = new AtomList(pB.parseAtom("Q(0)"));
        AtomList T1 = new AtomList(pB.parseAtom("Q(1)"));
        T1.add(pB.parseAtom("R(1)"));

        List<SupportedAnswer> S0 = UpdateAnswer.update(answerList, new ArrayList<>(), T0, 0);
        List<SupportedAnswer> S1 = UpdateAnswer.update(answerList, S0, T1, 1);
        List<SupportedAnswer> S2 = UpdateAnswer.update(answerList, S1, new AtomList(), 1);

        assertEquals(1, S0.size());
        assertEquals(2, S1.size());
        assertEquals("[{},{Q(0)},{Q(1),R(T)}]",S0.get(0).toString(query));
        assertEquals("[{},{Q(0),Q(1)},{R(T)}]",S1.get(0).toString(query));
        assertEquals("[{(T/1)},{Q(0),Q(1),R(1)},{}]",S1.get(1).toString(query));
        assertEquals(2, S2.size());
        assertEquals("[{},{Q(0),Q(1)},{R(T)}]",S2.get(0).toString(query));
        assertEquals("[{(T/1)},{Q(0),Q(1),R(1)},{}]",S2.get(1).toString(query));

    }




}