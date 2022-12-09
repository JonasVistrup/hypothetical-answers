package Jonas.Logic;


import java.util.List;

public interface UserDefinedPredicate extends PredicateInterface {
    public boolean run(List<Constant> constantList);

    public String toString(List<Term> terms);
}
