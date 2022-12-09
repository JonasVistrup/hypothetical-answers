package Jonas.Logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FunctionAtom extends Atom{

    private final Map<Integer, FunctionAtom> instances;

    FunctionAtom(PredicateInterface predicate, List<Term> args, Temporal temporal) {
        super(predicate, args, temporal);

        if(!(predicate instanceof FunctionPredicate)) throw new IllegalArgumentException("Predicate for a FunctionAtom must be a FunctionPredicate");

        this.instances = new HashMap<>();
    }

    FunctionAtom(FunctionAtom parent, int version){
        super(parent, version);

        this.instances = new HashMap<>();
    }


    @Override
    public Atom getInstance(int version){
        if(this.instances.containsKey(version)){
            return this.instances.get(version);
        }else{
            FunctionAtom instance = new FunctionAtom(this, version);
            this.instances.put(version, instance);
            return instance;
        }
    }

    @Override
    public Atom applySub(Substitution substitution){
        List<Term> new_terms = new ArrayList<>();
        for(Term t: args){
            new_terms.add(t.applySub(substitution));
        }
        return new FunctionAtom(this.predicate, new_terms, this.temporal);
    }

    @Override
    public String toString() {
        FunctionPredicate fPredicate = (FunctionPredicate) this.predicate;
        return fPredicate.toString(this.args);
    }

    public boolean isground(){
        for(Term t: args){
            if(t instanceof Variable) return false;
        }
        return true;
    }

    public boolean run(){
        assert isground();

        FunctionPredicate fPredicate = (FunctionPredicate) this.predicate;
        List<Constant> constants = this.args.stream().map(x ->(Constant) x).collect(Collectors.toList());
        return fPredicate.run(constants);
    }



}
