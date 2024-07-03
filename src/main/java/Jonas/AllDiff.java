package Jonas;

import Jonas.Logic.Constant;
import Jonas.Logic.UserDefinedPredicate;
import Jonas.Logic.Term;

import java.util.List;

public class AllDiff implements UserDefinedPredicate {
    @Override
    public int nArgs() {
        return 4;
    }

    @Override
    public boolean run(List<Constant> constantList) {
        assert constantList.size() == nArgs();
        Constant c0 = constantList.get(0);
        Constant c1 = constantList.get(1);
        Constant c2 = constantList.get(2);
        Constant c3 = constantList.get(3);
        return c1 != c0 && c2 != c0 && c3 != c0 && c1 != c2 && c1 != c3 && c2 != c3;
    }

    @Override
    public String toString(List<Term> terms) {
        assert terms.size() == nArgs();
        return "AllDiff("+terms.get(0).toString() + ", " +terms.get(1).toString() + ", "+terms.get(2).toString() + ", " + terms.get(3).toString() + ")";
    }

    @Override
    public String id(){
        return "AllDiff";
    }
}
