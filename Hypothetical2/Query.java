import java.util.List;

public class Query {
    public List<Answer> answers;
    public Atom queriedAtom;

    public Query(Atom queriedAtom, List<Answer> preprocessingAnswers){
        this.queriedAtom = queriedAtom;
        this.answers = preprocessingAnswers;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Query)){
            return false;
        }
        Query other = (Query) obj;
        return other.queriedAtom.equals(this.queriedAtom);
    }
}
