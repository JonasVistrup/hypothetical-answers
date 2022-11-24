package Jonas.Hypothetical;

import java.util.ArrayList;
import java.util.List;
import Jonas.Logic.AtomList;
import Jonas.Logic.Program;
import Jonas.SLD.SLDResolution;
import Jonas.Logic.Substitution;

public class UpdateSupportedAnswer {
    public static List<SupportedAnswer> update(SupportedAnswer sa, Program dataStream, int time) {
        List<SupportedAnswer> constantResults = new ArrayList<>();

        if (!sa.smallestConstant.isEmpty() && sa.smallestConstant.get(0).temporal.tConstant == time) {

            List<Substitution> answers = SLDResolution.findSubstitutions(dataStream, sa.smallestConstant);

            if (answers.isEmpty()) {
                return constantResults;
            }

            AtomList leftoverPremise = sa.constantPremise.without(sa.smallestConstant).plus(sa.temporalPremise);
            for (Substitution answer : answers) {
                Substitution substitution = Substitution.composition(sa.substitution, answer);
                AtomList evidence = sa.evidence.plus(sa.smallestConstant).applySub(answer);
                AtomList premise = leftoverPremise.applySub(answer);

                constantResults.add(new SupportedAnswer(substitution, evidence, premise));
            }
        }

        List<SupportedAnswer> results = new ArrayList<>();


        for (SupportedAnswer sAnswer : constantResults) {
            results.add(sAnswer);

            List<Substitution> answers = SLDResolution.findSubstitutions(dataStream, sAnswer.smallestTemporal);

            AtomList leftoverPremise = sAnswer.temporalPremise.without(sAnswer.smallestTemporal).plus(sAnswer.constantPremise);
            for (Substitution answer : answers) {
                Substitution substitution = Substitution.composition(sAnswer.substitution, answer);
                AtomList evidence = sa.evidence.plus(sAnswer.smallestTemporal).applySub(answer);
                AtomList premise = leftoverPremise.applySub(answer);
                results.add(new SupportedAnswer(substitution, evidence, premise));
            }
        }


        //Logic.Temporal aspect
        List<Substitution> answers = SLDResolution.findSubstitutions(dataStream, sa.smallestTemporal);
        AtomList leftoverPremise = sa.temporalPremise.without(sa.smallestTemporal).plus(sa.constantPremise);
        for (Substitution answer : answers) {
            Substitution substitution = Substitution.composition(sa.substitution, answer);
            AtomList evidence = sa.evidence.plus(sa.smallestTemporal).applySub(answer);
            AtomList premise = leftoverPremise.applySub(answer);
            results.add(new SupportedAnswer(substitution, evidence, premise));
        }

        return results;
    }
}
