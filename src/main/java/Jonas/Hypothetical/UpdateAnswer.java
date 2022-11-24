package Jonas.Hypothetical;

import java.util.ArrayList;
import java.util.List;
import Jonas.Logic.AtomList;
import Jonas.Logic.Program;
import Jonas.SLD.SLDResolution;
import Jonas.Logic.Substitution;


/**
 * Class of static functions used to update evidence based upon new information.
 */
 public class UpdateAnswer {


    /**
     * Updates the evidence based upon the dataStream given.
     * @param hAnswers hypothetical answers gathered from preprocessing.
     * @param eAnswers evidence for at the previous time
     * @param dataStream facts at the current time
     * @param time current time (Currently not used)
     * @return list of evidence for the current time
     */
    public static List<SupportedAnswer> update(List<PreprocessingAnswer> hAnswers, List<SupportedAnswer> eAnswers, AtomList dataStream, int time){
        Program p = dataStream.toProgram();

        List<SupportedAnswer> updated_EAnswers = getEAnswersFromHAnswers(hAnswers, p, time);
        updated_EAnswers.addAll(updateEAnswers(eAnswers, p, time));
        return updated_EAnswers;
    }

    private static List<SupportedAnswer> getEAnswersFromHAnswers(List<PreprocessingAnswer> hAnswers, Program p, int time){
        List<SupportedAnswer> eAnswers = new ArrayList<>();
        for(PreprocessingAnswer hAnswer: hAnswers){
            eAnswers.addAll(CreateSupportedAnswer.create(hAnswer, p, time));
        }
        return eAnswers;
    }
    private static List<SupportedAnswer> getEAnswersFromHAnswer(PreprocessingAnswer hAnswer, Program p, int time){  //TODO use time for early rejection on contantPremise with timeConstant less then current time.
        List<SupportedAnswer> eAnswers = new ArrayList<>();

        AtomList minConstantPremise = hAnswer.smallestConstant;

        List<Substitution> constantAnswerList = SLDResolution.findSubstitutions(p, minConstantPremise);

        if(!minConstantPremise.isEmpty()) {
            for (Substitution constantAnswer : constantAnswerList) {
                Substitution sub = Substitution.composition(hAnswer.substitution, constantAnswer);
                eAnswers.add(new SupportedAnswer(sub, minConstantPremise, hAnswer.constantPremise.without(minConstantPremise).applySub(constantAnswer), hAnswer.temporalPremise.applySub(constantAnswer)));
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



                eAnswers.add(new SupportedAnswer(sub, evidence, constantRemainingPremise, temporalRemainingPremise));
            }
        }
        return eAnswers;
    }



    private static List<SupportedAnswer> updateEAnswers(List<SupportedAnswer> eAnswers, Program p, int time){
        List<SupportedAnswer> result = new ArrayList<>();
        for(SupportedAnswer eAnswer: eAnswers){
            result.addAll(UpdateSupportedAnswer.update(eAnswer, p, time));
        }
        return result;
    }

    private static List<SupportedAnswer> updateEAnswer(SupportedAnswer eAnswer, Program p, int time){
        List<SupportedAnswer> eAnswers = new ArrayList<>();

        AtomList minConstantPremise = eAnswer.smallestConstant;
        List<Substitution> constantAnswerList = SLDResolution.findSubstitutions(p, minConstantPremise);


        for (Substitution constantAnswer : constantAnswerList) {
            Substitution sub = Substitution.composition(eAnswer.substitution, constantAnswer);
            eAnswers.add(new SupportedAnswer(sub, eAnswer.evidence.plus(minConstantPremise).applySub(sub), eAnswer.constantPremise.without(minConstantPremise).applySub(constantAnswer), eAnswer.temporalPremise.applySub(constantAnswer)));
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



                eAnswers.add(new SupportedAnswer(sub, eAnswer.evidence.plus(evidence), constantRemainingPremise, temporalRemainingPremise));
            }
        }
        return eAnswers;
    }
}
