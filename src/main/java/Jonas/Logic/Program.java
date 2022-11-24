package Jonas.Logic;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of logical clauses called a program.
 */
public class Program{
    /**
     * List of program clauses.
     */
    public final List<Clause> clauses;

    /**
     * Constructs a program given a list of clauses
     * @param clauses clauses of the program
     */
    public Program(List<Clause> clauses) {
        this.clauses = new ArrayList<>(clauses);
    }

    /**
     * Returns a string representation of this program consisting of every clause in the program.
     * @return string representation of this program.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Clause c : clauses) {
            builder.append(c.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}