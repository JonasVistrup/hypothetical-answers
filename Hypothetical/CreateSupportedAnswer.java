import java.util.ArrayList;
import java.util.List;

public class CreateSupportedAnswer {
    public static List<SupportedAnswer> create(PreprocessingAnswer pa, Program dataStream, int time) {
        if (!pa.smallestConstant.isEmpty() && pa.smallestConstant.get(0).temporal.tConstant < time) {
            return new ArrayList<>();
        }


        List<SupportedAnswer> constantResults = new ArrayList<>();


        if (!pa.smallestConstant.isEmpty() && pa.smallestConstant.get(0).temporal.tConstant == time) {
            List<Substitution> answers = SLDResolution.findSubstitutions(dataStream, pa.smallestConstant);

            if (answers.isEmpty()) {
                return constantResults;
            }

            AtomList leftoverPremise = pa.constantPremise.without(pa.smallestConstant).plus(pa.temporalPremise);

            for (Substitution answer : answers) {
                Substitution substitution = Substitution.composition(pa.substitution, answer);
                AtomList evidence = pa.smallestConstant.applySub(answer);
                AtomList premise = leftoverPremise.applySub(answer);

                constantResults.add(new SupportedAnswer(substitution, evidence, premise));
            }
        }


        List<SupportedAnswer> results = new ArrayList<>();


        for (SupportedAnswer sa : constantResults) {
            results.add(sa);

            List<Substitution> answers = SLDResolution.findSubstitutions(dataStream, sa.smallestTemporal);

            AtomList leftoverPremise = sa.temporalPremise.without(sa.smallestTemporal).plus(sa.constantPremise);
            for (Substitution answer : answers) {
                Substitution substitution = Substitution.composition(sa.substitution, answer);
                AtomList evidence = sa.smallestTemporal.applySub(answer);
                AtomList premise = leftoverPremise.applySub(answer);
                results.add(new SupportedAnswer(substitution, evidence, premise));
            }
        }


        //Temporal aspect
        List<Substitution> answers = SLDResolution.findSubstitutions(dataStream, pa.smallestTemporal);
        AtomList leftoverPremise = pa.temporalPremise.without(pa.smallestTemporal).plus(pa.constantPremise);
        for (Substitution answer : answers) {
            Substitution substitution = Substitution.composition(pa.substitution, answer);
            AtomList evidence = pa.smallestTemporal.applySub(answer);
            AtomList premise = leftoverPremise.applySub(answer);
            results.add(new SupportedAnswer(substitution, evidence, premise));
        }

        return results;
    }
}
