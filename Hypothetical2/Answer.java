import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Answer {

    public final Substitution substitution;
    public final LiteralList evidence;
    public final LiteralList premise;

    public Answer(Substitution substitution, LiteralList evidence, LiteralList premise){
        this.substitution = substitution;
        this.evidence = evidence;
        this.premise = premise;
    }

    public Set<Answer> getNextAnswers(Program dataSlice, int time){
        Set<Answer> nextAnswers = new HashSet<>();

        nextAnswers.addAll(stepA(dataSlice, time));
        nextAnswers.addAll(stepB(dataSlice, time));
        nextAnswers.addAll(stepC(dataSlice, time));
    }




    private Set<Answer> stepA(Program dataSlice, int time){

    }
    private Set<Answer> stepB(Program dataSlice, int time) {
    }
    private Set<Answer> stepC(Program dataSlice, int time) {
    }


    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Answer other)){
            return false;
        }
        return this.evidence.equals(other.evidence) && this.premise.equals(other.premise);
    }
}
