package Logic;

import java.util.ArrayList;

public class LiteralList{
    private AtomList positive;
    private AtomList negative;


    public LiteralList(){
        positive = new AtomList();
        negative = new AtomList();
    }

    public LiteralList(LiteralList copy){
        this.positive = (AtomList) copy.positive.clone();
        this.negative = (AtomList) copy.negative.clone();
    }

    public LiteralList(AtomList positive, AtomList negative){
        this.positive = new AtomList(positive);
        this.negative = new AtomList(negative);
    }

    public int size(){
        return positive.size() + negative.size();
    }


    public void add(Atom a, boolean isPositive){
        if(isPositive) positive.add(a);
        else negative.add(a);
    }

    public AtomList positive(){
        return positive;
    }
    public AtomList negative(){
        return negative;
    }

    public boolean isEmpty(){
        return positive.isEmpty() && negative.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LiteralList)){
            return false;
        }
        LiteralList other =  (LiteralList) obj;
        return this.positive.equals(other.positive) && this.negative.equals(other.negative);
    }

    public LiteralList applySub(Substitution sub){
        return new LiteralList(this.positive.applySub(sub), this.negative.applySub(sub));
    }
}
