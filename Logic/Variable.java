import java.util.HashMap;
import java.util.Map;

public class Variable implements Term{

    String id;
    Map<Integer, Variable> variants;
    public Variable(String id){
        this.id = id;
        this.variants = new HashMap<>();
    }

    public Term getVariant(int version){
        if(this.variants.containsKey(version)){
            return this.variants.get(version);
        }else{
            Variable instance = new Variable(this.id);
            this.variants.put(version, instance);
            return instance;
        }
    }

    @Override
    public Term applySub(Substitution substitution) {
        Term t = substitution.getSubstitution(this);
        if(t != null){
            return t;
        }
        return this;
    }



    @Override
    public String toString() {
        return id;
    }


}
