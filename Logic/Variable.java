import java.util.HashMap;
import java.util.Map;

public class Variable implements Term{

    String id;
    Map<Integer, VariableInstance> variants;
    public Variable(String id){
        this.id = id;
        this.variants = new HashMap<>();
    }

    public TermInstance getVariant(int version){
        if(this.variants.containsKey(version)){
            return this.variants.get(version);
        }else{
            VariableInstance instance = new VariableInstance(this, version);
            this.variants.put(version, instance);
            return instance;
        }
    }


    @Override
    public String toString() {
        return id;
    }
}
