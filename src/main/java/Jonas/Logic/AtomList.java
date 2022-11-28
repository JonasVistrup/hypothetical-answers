package Jonas.Logic;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A list of atom
 */
public class AtomList extends ArrayList<Atom>{

    private AtomList constantTime = null;
    private AtomList variableTime = null;
    private AtomList smallestConstant = null;
    private AtomList smallestVariable = null;

    private boolean organized = false;

    /**
     * Constructs a list of atoms based upon the list of atoms given.
     * @param atoms list of atom this list is based upon.
     */
    public AtomList(ArrayList<Atom> atoms){
        super();
        super.addAll(atoms);
        organize();
    }

    /**
     * Constructs a list of atoms with atom as a single element in the list.
     * @param atom the single atom added
     */
    public AtomList(Atom atom){
        super();
        super.add(atom);
        organize();
    }

    @Override
    public boolean add(Atom atom) {
        organized = false;
        return super.add(atom);
    }

    @Override
    public boolean addAll(Collection<? extends Atom> c) {
        organized = false;
        return super.addAll(c);
    }

    @Override
    public void add(int index, Atom element) {
        organized = false;
        super.add(index, element);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Atom> c) {
        organized = false;
        return super.addAll(index, c);
    }

    /**
     * Must only be called if all instances are constant or all instances are variables.
     * Sorts the list and returns the smallest atoms.
     * @return the smallest atoms in this list.
     */
    public AtomList getMin(){
        Collections.sort(this);

        AtomList min = new AtomList();
        if(this.isEmpty()){
            return min;
        }
        int smallestTime = this.get(0).temporal.tConstant;
        for(Atom a: this){
            if(a.temporal.tConstant>smallestTime){
                break;
            }
            min.add(a);
        }
        return min;
    }

    private void organize(){
        if(organized){
            return;
        }

        constantTime = new AtomList();
        variableTime = new AtomList();
        for(Atom a: this){
            if(a.temporal.tVar == null) constantTime.add(a);
            else variableTime.add(a);
        }

        smallestConstant = constantTime.getMin();
        smallestVariable = variableTime.getMin();

        organized = true;
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

    public AtomList constantTime(){
        organize();

        return constantTime;
    }

    public AtomList smallestConstant(){
        organize();

        return smallestConstant;
    }

    public AtomList smallestVariable(){
        organize();

        return smallestVariable;
    }


    public AtomList variableTime(){
        organize();

        return variableTime;
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

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof AtomList)){
            return false;
        }
        AtomList other = (AtomList) o;
        this.organize();
        other.organize();

        return sameLists(this.constantTime, other.constantTime) && sameLists(this.variableTime, other.variableTime);
    }

    private static boolean sameLists(AtomList one, AtomList two){
        if(one.size() != two.size()) return false;

        for(int i = 0; i<one.size(); i++){
            if(!one.get(i).equals(two.get(i))){
                return false;
            }
        }
        return true;
    }

    public JSONArray toJSONArray() {
        JSONArray arr = new JSONArray();
        for(Atom a: this){
            arr.put(a.toJSONObject());
        }
        return arr;
    }
}