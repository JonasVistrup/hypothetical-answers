/**
 * Class representing the un-initialize predicate.
 * @param numberOfArgs Number of non-temporal arguments.
 * @param name  Name of predicate which will be printed.
 */
public record Predicate(int numberOfArgs, String name){
    @Override
    public String toString() {
        return name;
    }
}
