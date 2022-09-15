import java.util.List;

public record Clause(Atom head, Atom... body) {
    @Override
    public String toString() {
        if(body.length == 0){
            return head.toString() + "<-";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(head.toString());
        builder.append("<-");
        for(Atom atom: body){
            builder.append(atom.toString());
            builder.append(",");
        }
        builder.deleteCharAt(builder.length()-1);
        return builder.toString();
    }
}
