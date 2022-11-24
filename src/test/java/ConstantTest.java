import Jonas.Logic.Constant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstantTest {
    @Test
    @DisplayName("Constant toString check")
    void toStringTest(){
        assertEquals("a", new Constant("a").toString(), "Should print its id");
        assertEquals("HEY ", new Constant("HEY ").toString(), "Should print its id");
        assertEquals("\n\t {pp12 |\n}", new Constant("\n\t {pp12 |\n}").toString(), "Should print its id");
    }

    @Test
    @DisplayName("Ensure that getVariant returns itself")
    void getVariantTest(){
        Constant c = new Constant("a");
        assertEquals(c, c.getVariant(0));
    }
}
