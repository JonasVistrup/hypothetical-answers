public record Predicate(int numberOfArgs, String name){
    @Override
    public String toString() {
        return name;
    }
}
