import java.util.ArrayList;

public class LiteralList{
    private AtomList positive;
    private AtomList negative;


    public LiteralList(AtomList positive, AtomList negative){
        positive = new AtomList(positive);
        negative = new AtomList(negative);
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


    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof LiteralList other)){
            return false;
        }
        return this.positive.equals(other.positive) && this.negative.equals(other.negative);
    }

    public LiteralList applySub(Substitution sub){
        return new LiteralList(this.positive.applySub(sub), this.negative.applySub(sub));
    }
}
