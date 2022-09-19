import java.util.Map;

public class Temporal implements Term{

    Variable tVar;
    int tConstant;
    Map<Integer, TemporalInstance> variants;

    public Temporal(Variable tVar, int tConstant){
        if(tVar==null && tConstant<0) throw new IllegalArgumentException("Temporal constant must be at least 0");

        this.tVar = tVar;
        this.tConstant = tConstant;
    }

    public TermInstance getVariant(int version){
        if(this.variants.containsKey(version)){
            return this.variants.get(version);
        }else{
            TemporalInstance instance = new TemporalInstance(this, version);
            this.variants.put(version, instance);
            return instance;
        }
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
}
