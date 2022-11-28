package Jonas.Logic;


import java.util.List;

public interface FunctionPredicate extends PredicateInterface {
    public int nArgs();
    public boolean run(List<Constant> constantList);
}
