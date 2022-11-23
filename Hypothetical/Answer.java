package Hypothetical;

import Logic.*;
import SLD.SLDResolution;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Answer implements Comparable<Answer>{

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
        nextAnswers.addAll(stepBC(dataSlice, time));

        return nextAnswers;
    }

    public boolean shouldBePreserved(int time){
        AtomList mPlus = premise.positive().smallestConstant();
        AtomList mMinus = premise.negative().smallestConstant();

        return (mPlus.isEmpty() || mPlus.get(0).temporal.tConstant>time) && (mMinus.isEmpty() || mMinus.get(0).temporal.tConstant>time);
    }


    private Set<Answer> stepA(Program dataSlice, int time){
        //Constant part.
        Set<Answer> result = new HashSet<>();

        if(!this.premise.positive().smallestConstant().isEmpty() && this.premise.positive().smallestConstant().get(0).temporal.tConstant == time){
            AtomList constants = this.premise.positive().smallestConstant();
            List<Substitution> answers = SLDResolution.findSubstitutions(dataSlice, constants);

            for(Substitution answer: answers){
                Substitution sub = this.substitution.add(answer);
                LiteralList newEvidence = new LiteralList(this.evidence.positive().plus(constants), this.evidence.negative()).applySub(answer);
                LiteralList newPremise = new LiteralList(this.premise.positive().without(constants), this.evidence.negative()).applySub(answer);

                result.add(new Answer(sub, newEvidence, newPremise));
            }
        }

        //Constant and Temporal part.
        if(!this.premise.positive().smallestVariable().isEmpty()){
            AtomList smallestPremise = this.premise.positive().smallestConstant().isEmpty() || this.premise.positive().smallestConstant().get(0).temporal.tConstant!= time?
                    this.premise.positive().smallestVariable() : this.premise.positive().smallestConstant().plus(this.premise.positive().smallestVariable());

            List<Substitution> answers = SLDResolution.findSubstitutions(dataSlice, smallestPremise); //TODO can be optimized. The constants do not need to be solved twice.

            for(Substitution answer: answers){
                Substitution sub = this.substitution.add(answer);
                LiteralList newEvidence = new LiteralList(this.evidence.positive().plus(smallestPremise), this.evidence.negative()).applySub(answer);
                LiteralList newPremise = new LiteralList(this.premise.positive().without(smallestPremise), this.evidence.negative()).applySub(answer);

                result.add(new Answer(sub, newEvidence, newPremise));
            }
        }

        return result;
    }
    private Set<Answer> stepBC(Program dataSlice, int time) {
        Set<Answer> result = new HashSet<>();

        Substitution answerSub = this.substitution;
        LiteralList answerEvidence = this.evidence;
        LiteralList answerPremise = this.premise;

        if(!this.premise.positive().variableTime().isEmpty() || !this.premise.negative().variableTime().isEmpty()){
            Variable t;
            if(!this.premise.positive().variableTime().isEmpty()){
                t = this.premise.positive().variableTime().get(0).temporal.tVar;
            }else{
                t = this.premise.negative().variableTime().get(0).temporal.tVar;
            }

            Substitution tSub = new Substitution(t, new Temporal(null, time));  //This means that all rules must start with P(...,T)<- and not P(...,T+1)<-

            try {
                answerSub = answerSub.add(tSub);
                answerEvidence = answerEvidence.applySub(tSub);
                answerPremise = answerPremise.applySub(tSub);
            }catch (IllegalArgumentException e){ // Negative temporal initiations
                return result;
            }

        }

        if(!answerPremise.isEmpty() &&(answerPremise.positive().smallestConstant().isEmpty() || answerPremise.positive().smallestConstant().get(0).temporal.tConstant>time)){
            result.add(new Answer(answerSub, answerEvidence, answerPremise));
        }

        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Answer)){
            return false;
        }
        Answer other = (Answer) obj;
        return this.evidence.equals(other.evidence) && this.premise.equals(other.premise);
    }



    /**
     * Returns a string representation of this, where only substitutions relating to an atom is show.
     * @param relevantQuery the atom for which only relevant substitutions is showed.
     * @return string representation.
     */
    public String toString(Atom relevantQuery) {
        return toString(new AtomList(relevantQuery));
    }


    /**
     * Returns a string representation of this, where only substitutions relating to a list of atoms is show.
     * @param relevantQuery the list of atoms for which only relevant substitutions is showed.
     * @return string representation.
     */
    public String toString(AtomList relevantQuery) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(substitution.toString(relevantQuery));
        builder.append(",{");
        for(Atom a: this.evidence.positive().constantTime()){
            builder.append(a.toString());
            builder.append(",");
        }
        for(Atom a: this.evidence.negative().constantTime()){
            builder.append("-");
            builder.append(a.toString());
            builder.append(",");
        }
        for(Atom a: this.evidence.positive().variableTime()){
            builder.append(a.toString());
            builder.append(",");
        }
        for(Atom a: this.evidence.negative().variableTime()){
            builder.append("-");
            builder.append(a.toString());
            builder.append(",");
        }
        if(!this.evidence.isEmpty()){
            builder.deleteCharAt(builder.length()-1);
        }
        builder.append("}");
        builder.append(",{");
        for(Atom a: this.premise.positive().constantTime()){
            builder.append(a.toString());
            builder.append(",");
        }
        for(Atom a: this.premise.negative().constantTime()){
            builder.append("-");
            builder.append(a.toString());
            builder.append(",");
        }
        for(Atom a: this.premise.positive().variableTime()){
            builder.append(a.toString());
            builder.append(",");
        }
        for(Atom a: this.premise.negative().variableTime()){
            builder.append("-");
            builder.append(a.toString());
            builder.append(",");
        }
        if(!this.premise.isEmpty()){
            builder.deleteCharAt(builder.length()-1);
        }
        builder.append("}]");
        return builder.toString();
    }

    /**
     * Returns a string representation of this.
     * @return string representation.
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append(substitution.toString());
        builder.append(",{");
        for(Atom a: this.evidence.positive()){
            builder.append(a.toString());
            builder.append(",");
        }
        for(Atom a: this.evidence.negative()){
            builder.append("-");
            builder.append(a.toString());
            builder.append(",");
        }
        if(!this.evidence.isEmpty()){
            builder.deleteCharAt(builder.length()-1);
        }
        builder.append("}");
        builder.append(",{");
        for(Atom a: this.premise.positive()){
            builder.append(a.toString());
            builder.append(",");
        }
        for(Atom a: this.premise.negative()){
            builder.append("-");
            builder.append(a.toString());
            builder.append(",");
        }
        if(!this.premise.isEmpty()){
            builder.deleteCharAt(builder.length()-1);
        }
        builder.append("}]");
        return builder.toString();
    }

    @Override
    public int compareTo(Answer o) {
        return this.toString().compareTo(o.toString());
    }
}
