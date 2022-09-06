public class Variable implements Term{

    public String name;
    public Variable(String name){
        this.name = name;
    }
    @Override
    public Term applySub(Substitution substitution) {
        return null;
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
