import java.util.List;

public class Program{
    List<Clause> clauses;

    Program(List<Clause> clauses) {
        this.clauses = clauses;
    }

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