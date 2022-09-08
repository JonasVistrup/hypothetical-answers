public class Variable implements Term{

    public String name;
    public Variable(String name){
        this.name = name;
    }
    @Override
    public Term applySub(Substitution substitution) {
        for(Substitution.Sub sub: substitution.subs()){
            if(sub.from().equals(this)) {
                return sub.to();
            }
        }
        return this;
    }
    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return name;
    }
}
