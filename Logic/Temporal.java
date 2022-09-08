public class Temporal implements Term{

    private Variable variable; // Time variable
    private int constant; // Added constant

    public Temporal(Variable variable, int constant){
        this.variable = variable;
        this.constant = constant;
    }

    @Override
    public Term applySub(Substitution substitution) {
        if(!substitution.isInSupport(variable)){
            return this;
        }
        Term res_term = variable.applySub(substitution);
        assert res_term instanceof Temporal;
        Temporal res = (Temporal) res_term;
        return new Temporal(res.variable, res.constant+this.constant);
    }

    public Variable variable() {
        return variable;
    }

    @Override
    public String name() {
        return variable.name();
    }

    public int constant(){
        return this.constant;
    }
}
