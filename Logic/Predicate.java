import java.util.Objects;

/**
 * Class representing the un-initialize predicate.
 */
public final class Predicate {
    private final int numberOfArgs;
    private final String name;

    /**
     *
     * @param numberOfArgs Number of non-temporal arguments.
     * @param name Name of predicate which will be printed.
     */
    Predicate(int numberOfArgs, String name) {
        this.numberOfArgs = numberOfArgs;
        this.name = name;
    }

    public int numberOfArgs() {
        return numberOfArgs;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Predicate that = (Predicate) obj;
        return this.numberOfArgs == that.numberOfArgs &&
                Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfArgs, name);
    }

    @Override
    public String toString() {
        return name;
    }
}
