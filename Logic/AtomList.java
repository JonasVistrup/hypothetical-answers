import java.util.ArrayList;

public class AtomList extends ArrayList<AtomInstance>{
    public AtomList(ArrayList<AtomInstance> atoms){
        super();
        super.addAll(atoms);
    }
    public AtomList(){
        super();
    }

    public AtomList applySub(Substitution substitution){
        AtomList atomList = new AtomList();
        for(AtomInstance a: this){
            atomList.add(a.applySub(substitution));
        }
        return atomList;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for(AtomInstance a: this){
            b.append(a.toString());
            b.append(",");
        }
        if(!isEmpty()){
            b.deleteCharAt(b.length()-1);
        }
        return b.toString();
    }
}
