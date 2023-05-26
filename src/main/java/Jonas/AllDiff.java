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
        return constantList.get(1) == c0 && constantList.get(2) == c0 && constantList.get(3) == c0;
    }

    @Override
    public String toString(List<Term> terms) {
        assert terms.size() == nArgs();
        return "AllDiff("+terms.get(0).toString() + ", " +terms.get(1).toString() + ", "+terms.get(2).toString() + ", " + terms.get(3).toString() + ")";
    }


    @Override
    public boolean IDB() {
        return false;
    }

    @Override
    public String id(){
        return "AllDiff";
    }
}
