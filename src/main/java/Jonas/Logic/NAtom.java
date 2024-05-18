package Jonas.Logic;

import Jonas.Hypothetical.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NAtom extends Atom{
    private Atom query;
    private Atom atom;

    public NAtom(PredicateInterface predicate, List<Term> args, Temporal temporal, Atom query){
        super(predicate,args,temporal);
        this.atom = new Atom(predicate,args,temporal);
        this.query = query;

    }
    public NAtom(NAtom parent, int version){
        super(parent, version);
        this.query = parent.getQuery();
    }

    @Override
    public boolean negated(){ return true;}
    @Override
    public Atom getQuery() {
        return query;
    }

    public NAtom getInstance(int version){
        if(this.instances.containsKey(version)){
            return (NAtom) this.instances.get(version);
        }else{
            NAtom instance = new NAtom(this, version);
            this.instances.put(version, instance);
            return instance;
        }
    }

    public void setQuery(Atom a){
        this.query = a;
    }

    public NAtom applySub(Substitution substitution){
        List<Term> new_terms = new ArrayList<>();
        for(Term t: args){
            new_terms.add(t.applySub(substitution));
        }
        return new NAtom(this.predicate, new_terms, (Temporal) this.temporal.applySub(substitution), this.query);
    }

    public Atom getAtom(){
        return atom;
    }

    @Override
    public String toString() {
        return "~"+super.toString();
    }
}
