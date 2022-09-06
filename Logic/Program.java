import java.util.ArrayList;
import java.util.List;

//TODO
public class Program {

    private final List<Clause> clauses;
    private final List<Predicate> IDB;

    public Program(List<Clause> clauses){
        this.clauses = clauses;
        this.IDB = new ArrayList<>();
        for(Clause c: clauses){
            if(!IDB.contains(c.head().predicate())){
                IDB.add(c.head().predicate());
            }
        }

    }
    public boolean isIDB(Predicate predicate){
        return IDB.contains(predicate);
    }

    public List<Clause> clauses() {
        return clauses;
    }
}
