package Jonas;

import Jonas.Logic.Constant;
import Jonas.Logic.UserDefinedPredicate;
import Jonas.Logic.Term;

import java.util.List;

public class Less implements UserDefinedPredicate {
    @Override
    public int nArgs() {
        return 2;
    }

    @Override
    public boolean run(List<Constant> constantList) {
        assert constantList.size() == nArgs();

        try{
            int one = Integer.parseInt(constantList.get(0).toString());
            int two = Integer.parseInt(constantList.get(1).toString());

            return one > two;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String toString(List<Term> terms) {
        assert terms.size() == nArgs();
        return terms.get(0).toString() + "<" + terms.get(1).toString();
    }


    @Override
    public boolean IDB() {
        return false;
    }

    @Override
    public String id(){
        return "Less";
    }
}
