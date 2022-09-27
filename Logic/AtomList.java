import java.util.ArrayList;
import java.util.List;

/**
 * A list of atom
 */
public class AtomList extends ArrayList<Atom>{

    /**
     * Constructs a list of atoms based upon the list of atoms given.
     * @param atoms list of atom this list is based upon.
     */
    public AtomList(ArrayList<Atom> atoms){
        super();
        super.addAll(atoms);
    }

    /**
     * Constructs a list of atoms with atom as a single element in the list.
     * @param atom the single atom added
     */
    public AtomList(Atom atom){
        super();
        super.add(atom);
    }

    /**
     * Constructs an empty list of atoms.
     */
    public AtomList(){
        super();
    }

    /**
     * Applies the substitution on every term in every atom of the list, returning the resulting list.
     * @param substitution applied substitution
     * @return list with applied substitutions
     */
    public AtomList applySub(Substitution substitution){
        AtomList atomList = new AtomList();
        for(Atom a: this){
            atomList.add(a.applySub(substitution));
        }
        return atomList;
    }

    /**
     * Returns a comma separated string of the atoms in the list.
     * @return string representation of this list
     */
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

    /**
     * Returns a program consisting of a fact for each of the atoms in this list.
     * @return resulting program
     */
    public Program toProgram() {
        List<Clause> clauses = new ArrayList<>();
        for(Atom a: this){
            clauses.add(new Clause(a, new AtomList()));
        }
        return new Program(clauses);
    }

    /**
     * Returns a new list consisting of all elements in this list that is not in the other list.
     * @param other list to remove from this
     * @return this list \ other
     */
    //TODO: is this slow?
    public AtomList without(AtomList other){
        AtomList atomList = (AtomList) this.clone();
        atomList.removeAll(other);
        return atomList;
    }

    /**
     * Returns a new list consisting of all elements in this list and all elements in other
     * @param other list to be added
     * @return this list + other list
     */
    public AtomList plus(AtomList other){
        AtomList atomList = (AtomList) this.clone();
        atomList.addAll(other);
        return atomList;
    }
}
