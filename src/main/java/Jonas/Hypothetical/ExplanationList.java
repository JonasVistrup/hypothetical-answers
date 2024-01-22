package Jonas.Hypothetical;

import Jonas.Logic.*;

import java.util.List;
import java.util.ArrayList;

public class ExplanationList {
    List<Clause> clauseUsed;

    ExplanationList(List<Clause> clauseUsed){
        this.clauseUsed = clauseUsed;
    }

    ExplanationList(String clauseListString, ProgramBuilder pb){
        this.clauseUsed = new ArrayList<>();
        for(String representation: clauseListString.split(";")){
            this.clauseUsed.add(getClauseFromString(representation, pb));
        }
    }

    public ExplanationList applySub(Substitution substitution){
        List<Clause> substitutedClauses = new ArrayList<>();
        for(Clause c: this.clauseUsed){
            substitutedClauses.add(c.applySub(substitution));
        }
        return new ExplanationList(substitutedClauses);
    }

    private Clause getClauseFromString(String representation, ProgramBuilder pb){
        representation = representation.replaceAll(" ", "");
        String[] parts = representation.split("<-");
        if (parts.length == 1 && parts[0].equals(representation)){
            String [] temp_parts = representation.split("->");
            if (temp_parts.length == 1 && temp_parts[0].equals(representation)) {
                throw new IllegalArgumentException("The clause should consist of a \"head<-body\" or  \"body->head\"");
            }
            parts = new String[temp_parts.length];
            parts[0] = temp_parts[temp_parts.length-1];
            if(parts.length > 1) {
                parts[1] = temp_parts[0];
            }
        }
        if (parts.length >= 3) {
            throw new IllegalArgumentException("The clause should consist of a \"head<-body\" or  \"body->head\"");
        }
        Atom head = pb.parseAtom(parts[0]);
        if(parts.length==1){
            return new Clause(head, new AtomList());
        }



        parts[1] = parts[1].replaceAll("\\),", ")<-");
        String[] strBody = parts[1].split("<-");

        AtomList body = new AtomList();
        for (String s : strBody) {
            body.add(pb.parseAtom(s));
        }
        if(head.predicate instanceof UserDefinedPredicate){
            throw new IllegalArgumentException("UserDefinedPredicates can not be defined by clauses.");
        }

        ((Predicate)head.predicate).IDB = true;
        return new Clause(head, body);
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(Clause c: this.clauseUsed){
            builder.append(c.toString()).append(";");
        }
        return builder.deleteCharAt(builder.length()-1).toString();
    }
}
