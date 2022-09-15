import java.util.List;
import java.util.Objects;

public final class Clause {
    private final Atom head;
    private final Atom[] body;

    Clause(Atom head, Atom... body) {
        this.head = head;
        this.body = body;
    }

    public Atom head() {
        return head;
    }

    public Atom[] body() {
        return body;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Clause that = (Clause) obj;
        return Objects.equals(this.head, that.head) &&
                Objects.equals(this.body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, body);
    }


    @Override
    public String toString() {
        if (body.length == 0) {
            return head.toString() + "<-";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(head.toString());
        builder.append("<-");
        for (Atom atom : body) {
            builder.append(atom.toString());
            builder.append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
