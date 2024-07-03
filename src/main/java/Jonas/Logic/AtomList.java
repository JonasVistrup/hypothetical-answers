package Jonas.Logic;

import org.json.JSONArray;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A list of atom
 */
public class AtomList extends ArrayList<Atom>{

    private AtomList constantTime = null;
    private AtomList variableTime = null;
    private AtomList smallestConstant = null;
    private AtomList smallestVariable = null;

    private AtomList negatedAtoms = null;
    private AtomList userDefinedAtoms = null;

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
     * @param atom the single atom added.
     */
    public AtomList(Atom atom){
        super();
        super.add(atom);
        organize();
    }

    /**
     * Adds an atom to this list.
     * @param atom atom whose presence in this collection is to be ensured
     * @return true iff this list is changed by this operation.
     */
    @Override
    public boolean add(Atom atom) {
        organized = false;
        return super.add(atom);
    }

    /**
     * Adds a collection to this list.
     * @param c collection containing elements to be added to this collection.
     * @return true iff this list is changed by this operation.
     */
    @Override
    public boolean addAll(Collection<? extends Atom> c) {
        organized = false;
        return super.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        organized = false;
        return super.removeAll(c);
    }

    /**
     * Adds an atom at an index.
     * @param index   index at which the specified atom is to be inserted.
     * @param element atom to be inserted.
     */
    @Override
    public void add(int index, Atom element) {
        organized = false;
        super.add(index, element);
    }

    /**
     * Adds a collections starting at a certain index.
     * @param index index at which to insert the first element from the
     *              specified collection.
     * @param c     collection containing elements to be added to this list.
     * @return true iff this list is changed by this operation.
     */
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
        userDefinedAtoms = new AtomList();
        negatedAtoms = new AtomList();
        for(Atom a: this){
            if(a instanceof SpecialAtom) {
                userDefinedAtoms.add(a);
            }else {
                if (a.negated()){
                    negatedAtoms.add(a);
                }else {
                    if (a.temporal.tVar == null) constantTime.add(a);
                    else variableTime.add(a);
                }
            }
        }
        Collections.sort(negatedAtoms);
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

    public AtomList functionAtoms(){
        organize();

        return this.userDefinedAtoms;
    }

    /**
     * @return a sorted list of the atoms in this list with no temporal variable.
     */
    public AtomList constantTime(){
        organize();

        return constantTime;
    }

    /**
     * @return a sorted list of the atoms in this list with no temporal variable and the smallest temporal constant.
     */
    public AtomList smallestConstant(){
        organize();

        return smallestConstant;
    }

    public AtomList negated(){
        organize();

        return negatedAtoms;
    }

    public AtomList M_minus(int time){
        organize();
        AtomList a = new AtomList();
        for(int i = 0; i < negatedAtoms.size(); i++){
            Atom current = negatedAtoms.get(i);
            if(current.temporal.tConstant > time) break;
            a.add(current);
        }
        return a;
    }
    public AtomList M_plus(int time){
        AtomList smallest = smallestConstant();
        if(!smallest.isEmpty()){
            Atom a = smallest.get(0);
            if(a.temporal.tVar != null) throw new IllegalArgumentException("M_plus is called on atoms with uninstated time variable");
            if(a.temporal.tConstant < time) throw new IllegalArgumentException("M_plus is called with positive atoms before current time");
            if(a.temporal.tConstant == time) return smallest;
        }
        return new AtomList();
    }

    /**
     * @return a sorted list of the atoms in this with a temporal variable and the smallest temporal constant.
     */
    public AtomList smallestVariable(){
        organize();

        return smallestVariable;
    }


    /**
     * @return a sorted list of atoms in this list with a temporal variable.
     */
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
        boolean allGround = true;
        for(Atom a: this){
            if(!a.isGround()) allGround = false;
            clauses.add(new Clause(a, new AtomList()));
        }
        HashMap<PredicateInterface, HashMap<Term, List<Clause>>> h = new HashMap<>();

        if(!allGround || this.size() == 0){
            return new Program(clauses);
        }

        for(Clause clause: clauses){
            if(!h.containsKey(clause.head.predicate)){
                HashMap<Term, List<Clause>> hh = new HashMap<>();
                hh.put(null, new ArrayList<>());
                h.put(clause.head.predicate, hh);
            }

            HashMap<Term, List<Clause>> innerMap = h.get(clause.head.predicate);
            if(clause.head.args.size() == 0){
                innerMap.get(null).add(clause);
            }else {
                if(!innerMap.containsKey(clause.head.args.get(0))){
                    innerMap.put(clause.head.args.get(0), new ArrayList<>());
                }
                innerMap.get(clause.head.args.get(0)).add(clause);
            }
        }
        return new Program(clauses, new Selector() {
            HashMap<PredicateInterface, HashMap<Term, List<Clause>>> map = h;
            @Override
            public List<Clause> getClausesFor(Atom a) {
                if(!h.containsKey(a.predicate)) return new ArrayList<>();
                HashMap<Term, List<Clause>> innerMap = h.get(a.predicate);

                if(a.args.size() == 0) return innerMap.get(null);
                if(a.args.get(0) instanceof Variable){
                    return innerMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
                }
                if(innerMap.containsKey(a.args.get(0))){
                    return innerMap.get(a.args.get(0));
                }else{
                    return new ArrayList<>();
                }
            }
        });
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

    /**
     * @param o the object to be compared for equality with this list
     * @return true iff the two list contains the same atoms.
     */
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


    public List<SpecialAtom> groundFAtoms(){
        List<SpecialAtom> fpWithOnlyConstants = new ArrayList<>();
        for(Atom a: this.functionAtoms()){
            SpecialAtom fa = (SpecialAtom) a;
            if(fa.isground()) fpWithOnlyConstants.add(fa);
        }
        return fpWithOnlyConstants;
    }



    /**
     * @return JSONArray of the JSON representation of the atoms in this list.
     */
    public JSONArray toJSONArray() {
        JSONArray arr = new JSONArray();
        for(Atom a: this){
            arr.put(a.toJSONObject());
        }
        return arr;
    }

}