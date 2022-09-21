import java.util.ArrayList;
import java.util.List;

public class AtomList extends ArrayList<Atom>{
    public AtomList(ArrayList<Atom> atoms){
        super();
        super.addAll(atoms);
    }
    public AtomList(){
        super();
    }

    public AtomList applySub(Substitution substitution){
        AtomList atomList = new AtomList();
        for(Atom a: this){
            atomList.add(a.applySub(substitution));
        }
        return atomList;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for(Atom a: this){
            b.append(a.toString());
            b.append(",");
        }
        if(!isEmpty()){
            b.deleteCharAt(b.length()-1);
        }
        return b.toString();
    }

    public Program toProgram() {
        List<Clause> clauses = new ArrayList<>();
        for(Atom a: this){
            clauses.add(new Clause(a, new AtomList()));
        }
        return new Program(clauses);
    }
}
