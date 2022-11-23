package Hypothetical;

import Logic.*;

import java.util.ArrayList;
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

    public List<Answer> getProvedAnswers(){
        List<Answer> result = new ArrayList<>();
        for(Answer a: answers){
            if(a.premise.isEmpty()) result.add(a);
        }
        return result;
    }


    public Query copy(){
        return new Query(queriedAtom, new ArrayList<>(answers));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(queriedAtom.toString()).append(":");
        if(answers.isEmpty()){
            builder.append(" NO ANSWERS");
        }else{
            builder.append("\n");
        }
        for(Answer a: answers){
            builder.append("\t");
            builder.append(a.toString(queriedAtom));
            builder.append("\n");
        }
        return builder.toString();
    }
}
