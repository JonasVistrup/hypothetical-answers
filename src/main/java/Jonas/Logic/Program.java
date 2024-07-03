package Jonas.Logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A set of logical clauses called a program.
 */
public class Program{
    /**
     * List of program clauses.
     */
    public final List<Clause> clauses;
    public final Selector selector;

    /**
     * Constructs a program given a list of clauses
     * @param clauses clauses of the program
     */
    public Program(List<Clause> clauses) {
        this.clauses = new ArrayList<>(clauses);
        this.selector = null;
    }

    public Program(List<Clause> clauses, Selector selector) {
        this.clauses = new ArrayList<>(clauses);
        this.selector = selector;
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

    public List<Clause> getClausesFor(Atom selected) {
        if(selector == null) return clauses;
        return selector.getClausesFor(selected);
    }
}