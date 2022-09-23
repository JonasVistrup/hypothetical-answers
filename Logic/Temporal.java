import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Temporal implements Term, Comparable<Temporal>{

    Variable tVar;
    int tConstant;
    Map<Integer, Temporal> variants;

    public Temporal(Variable tVar, int tConstant){
        if(tVar==null && tConstant<0) throw new IllegalArgumentException("Temporal constant must be at least 0");

        this.tVar = tVar;
        this.tConstant = tConstant;
        this.variants = new HashMap<>();
    }

    public Term getVariant(int version){
        if(tVar == null){
            return this;
        }

        if(this.variants.containsKey(version)){
            return this.variants.get(version);
        }else{
            Temporal instance = new Temporal((Variable) tVar.getVariant(version), this.tConstant);
            this.variants.put(version, instance);
            return instance;
        }
    }

    @Override
    public Term applySub(Substitution substitution) {
        Term to_term = substitution.getSubstitution(this.tVar);
        if(to_term != null){
            assert to_term instanceof Temporal;
            Temporal to = (Temporal) to_term;
            return new Temporal(to.tVar,to.tConstant+this.tConstant);
        }
        return this;
    }




    @Override
    public String toString() {
        if(tVar == null) {
            return "" + tConstant;
        }

        StringBuilder b = new StringBuilder(tVar.toString());
        if(tConstant>=0){
            b.append("+");
        }
        b.append(tConstant);
        return b.toString();
    }

    @Override
    public int compareTo(@NotNull Temporal o) {
        if(this.tVar != o.tVar) throw new IllegalArgumentException("Not comparable");
        return this.tConstant - o.tConstant;
    }
}
