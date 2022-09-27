import java.util.ArrayList;
import java.util.List;

public class UpdateAnswer {


    public static List<EvidenceAnswer> update(List<HypotheticalAnswer> hAnswers, List<EvidenceAnswer> eAnswers, AtomList dataStream, int time){
        List<EvidenceAnswer> updated_EAnswers = getEAnswersFromHAnswers(hAnswers, dataStream, time);
        updated_EAnswers.addAll(updateEAnswers(eAnswers, dataStream, time));
        return updated_EAnswers;
    }

    public static List<EvidenceAnswer> getEAnswersFromHAnswers(List<HypotheticalAnswer> hAnswers, AtomList dataStream, int time){
        Program p = dataStream.toProgram();
        List<EvidenceAnswer> eAnswers = new ArrayList<>();
        for(HypotheticalAnswer hAnswer: hAnswers){
            eAnswers.addAll(getEAnswersFromHAnswer(hAnswer, p, time));
        }
        return eAnswers;
    }
    private static List<EvidenceAnswer> getEAnswersFromHAnswer(HypotheticalAnswer hAnswer, Program p, int time){  //TODO use time for early rejection on contantPremise with timeConstant less then current time.
        List<EvidenceAnswer> eAnswers = new ArrayList<>();

        AtomList minConstantPremise = hAnswer.smallestConstant;

        List<Substitution> constantAnswerList = SLDResolution.findSubstitutions(p, minConstantPremise);

        if(!minConstantPremise.isEmpty()) {
            for (Substitution constantAnswer : constantAnswerList) {
                Substitution sub = Substitution.composition(hAnswer.substitution, constantAnswer);
                eAnswers.add(new EvidenceAnswer(sub, minConstantPremise, hAnswer.constantPremise.without(minConstantPremise).applySub(constantAnswer), hAnswer.temporalPremise.applySub(constantAnswer)));
            }
        }

        if(hAnswer.temporalPremise.isEmpty()){
            return eAnswers;
        }

        AtomList minTemporalPremise = hAnswer.smallestTemporal;
        for(Substitution constantAnswer: constantAnswerList){
            AtomList minTemporalSubstituted = minTemporalPremise.applySub(constantAnswer);

            List<Substitution> temporalAnswerList = SLDResolution.findSubstitutions(p, minTemporalSubstituted);

            for(Substitution answer: temporalAnswerList){
                Substitution sub = Substitution.composition(hAnswer.substitution, constantAnswer);
                sub = Substitution.composition(sub, answer);
                AtomList evidence = minConstantPremise.applySub(sub).plus(minTemporalSubstituted.applySub(answer));
                AtomList constantRemainingPremise = hAnswer.constantPremise.without(minConstantPremise).applySub(sub);
                AtomList temporalRemainingPremise = hAnswer.temporalPremise.without(minTemporalPremise).applySub(sub);



                eAnswers.add(new EvidenceAnswer(sub, evidence, constantRemainingPremise, temporalRemainingPremise));
            }
        }
        return eAnswers;
    }



    public static List<EvidenceAnswer> updateEAnswers(List<EvidenceAnswer> eAnswers, AtomList dataStream, int time){
        Program p = dataStream.toProgram();
        List<EvidenceAnswer> result = new ArrayList<>();
        for(EvidenceAnswer eAnswer: eAnswers){
            result.addAll(updateEAnswer(eAnswer, p, time));
        }
        return result;
    }

    private static List<EvidenceAnswer> updateEAnswer(EvidenceAnswer eAnswer, Program p, int time){
        List<EvidenceAnswer> eAnswers = new ArrayList<>();

        AtomList minConstantPremise = eAnswer.smallestConstant;
        List<Substitution> constantAnswerList = SLDResolution.findSubstitutions(p, minConstantPremise);


        for (Substitution constantAnswer : constantAnswerList) {
            Substitution sub = Substitution.composition(eAnswer.substitution, constantAnswer);
            eAnswers.add(new EvidenceAnswer(sub, eAnswer.evidence.plus(minConstantPremise).applySub(sub), eAnswer.constantPremise.without(minConstantPremise).applySub(constantAnswer), eAnswer.temporalPremise.applySub(constantAnswer)));
        }


        if(eAnswer.temporalPremise.isEmpty()){
            return eAnswers;
        }

        AtomList minTemporalPremise = eAnswer.smallestTemporal;
        for(Substitution constantAnswer: constantAnswerList){
            AtomList minTemporalSubstituted = minTemporalPremise.applySub(constantAnswer);

            List<Substitution> temporalAnswerList = SLDResolution.findSubstitutions(p, minTemporalSubstituted);

            for(Substitution answer: temporalAnswerList){
                Substitution sub = Substitution.composition(eAnswer.substitution, constantAnswer);
                sub = Substitution.composition(sub, answer);
                AtomList evidence = minConstantPremise.applySub(sub).plus(minTemporalSubstituted.applySub(answer));
                AtomList constantRemainingPremise = eAnswer.constantPremise.without(minConstantPremise).applySub(constantAnswer).applySub(answer);
                AtomList temporalRemainingPremise = eAnswer.temporalPremise.without(minTemporalPremise).applySub(constantAnswer).applySub(answer);



                eAnswers.add(new EvidenceAnswer(sub, eAnswer.evidence.plus(evidence), constantRemainingPremise, temporalRemainingPremise));
            }
        }
        return eAnswers;
    }
}
