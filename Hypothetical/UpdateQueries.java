import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpdateQueries {
    public static List<Query> update(List<Query> queries, AtomList dataSlice, int time, List<Constant> constants){
        List<Query> result = new ArrayList<>();
        Program p = dataSlice.toProgram();

        for(Query q: queries){
            //q.queriedAtom;
            Set<Answer> answerSet = new HashSet<>();
            for(Answer answer: q.answers){
                answerSet.addAll(answer.getNextAnswers(p, time));
            }

            result.add(new Query(q.queriedAtom, new ArrayList<>(answerSet)));
        }


        // X
        X(result, constants, time);

        for(int i=0; i<queries.size(); i++){ // TODO Encapsulate
            for(Answer a: queries.get(i).answers){
                if(a.shouldBePreserved(time)){
                    result.get(i).answers.add(a);
                }
            }
        }

        return result;
    }


    private static void X(List<Query> currentQueries, List<Constant> constants, int time){
        boolean changed = false;


        List<Query> nextQueries = deepCopyQueries(currentQueries);

        //Step A
        for(Query q: currentQueries){
            List<Answer> proved = q.getProvedAnswers();
            for(Answer a: proved){
                changed = changed || removeProved(nextQueries, q.queriedAtom.applySub(a.substitution), constants);
            }
        }

        //Step B
        for(Query q: currentQueries){
            changed = changed || addFalseToEvidence(nextQueries, q, time);
        }


        if(changed){
            X(nextQueries,constants, time);
        }
    }

    private static List<Query> deepCopyQueries(List<Query> queries) {
        List<Query> deepCopy = new ArrayList<>();
        for(Query q: queries){
            deepCopy.add(q.copy());
        }
        return deepCopy;
    }

    private static boolean addFalseToEvidence(List<Query> nextQueries, Query q, int time){
        boolean changed = false;
        List<Answer> toRemove = new ArrayList<>();
        List<Answer> toAdd = new ArrayList<>();
        for(Answer a: q.answers) {
            for (Atom negated : a.premise.negative()) {
                if (negated.temporal.tVar == null && negated.temporal.tConstant == time) {
                    for (Query qq : nextQueries) {
                        Substitution mgu = Unify.findMGU(negated, qq.queriedAtom);
                        if (mgu == null) continue;

                        boolean provenFalse = true;
                        for (Answer aa : qq.answers) {
                            if (Unify.findMGU(negated, qq.queriedAtom.applySub(aa.substitution)) != null) {
                                provenFalse = false;
                                break;
                            }
                        }
                        if (provenFalse) {
                            toRemove.add(a);
                            LiteralList evidence = new LiteralList(a.evidence.positive(), a.evidence.negative().plus(new AtomList(negated)));
                            LiteralList premise = new LiteralList(a.premise.positive(), a.premise.negative().without(new AtomList(negated)));
                            toAdd.add(new Answer(a.substitution, evidence, premise));
                            changed = true;
                        }
                    }
                }
            }
        }
        for(Answer a: toRemove){
            q.answers.remove(a);
        }
        q.answers.addAll(toAdd);
        return changed;
    }

    private static boolean removeProved(List<Query> queries, Atom proved, List<Constant> constants){
        boolean changed = false;
        for(Query q: queries){
            List<Answer> newAnswers = new ArrayList<>();
            for(Answer a: q.answers){
                List<Answer> updatedAnswers = findUpdatedAnswers(a, proved, constants);
                if(updatedAnswers.size() != 1 || updatedAnswers.get(0) != a){
                    changed = true;
                }
                newAnswers.addAll(updatedAnswers);
            }
            q.answers = newAnswers;
        }


        return changed;
    }

    private static List<Answer> findUpdatedAnswers(Answer a, Atom proved, List<Constant> constants) {
        List<Answer> updatedAnswers = new ArrayList<>();

        for(Atom negatedAtom: a.premise.negative()){
            Substitution unify = Unify.findMGU(proved, negatedAtom);
            if(unify != null) {
                List<Sub> disprovedSubs = getDisprovedSubs(proved, negatedAtom);
                if(!disprovedSubs.isEmpty()){
                    List<Substitution> substitutions = new ArrayList<>();
                    substitutions.add(new Substitution()); //Add empty substitution to list.
                    List<Substitution> possibleSubstitutions = getInverseSubs(substitutions, disprovedSubs, constants);

                    for(Substitution sub: possibleSubstitutions){
                        updatedAnswers.add(new Answer(a.substitution.add(sub), a.evidence.applySub(sub), a.premise.applySub(sub)));
                    }
                }

                return updatedAnswers;
            }
        }

        updatedAnswers.add(a);
        return updatedAnswers;
    }


    private static List<Substitution> getInverseSubs(List<Substitution> substitutions, List<Sub> disprovedSubs, List<Constant> constants) {
        if(disprovedSubs.isEmpty()){
            return substitutions;
        }

        Sub next = disprovedSubs.remove(0); //Should be the last index for speed
        List<Substitution> nextSubstitutions = new ArrayList<>();

        for(Substitution substitution: substitutions){
            for (Constant c : constants) {
                if (c != next.to) {
                    nextSubstitutions.add(substitution.add(new Substitution(next.from, c)));
                }
            }
        }

        return getInverseSubs(nextSubstitutions, disprovedSubs, constants);
    }


    private static List<Sub> getDisprovedSubs(Atom proved, Atom negatedAtom) {
        List<Sub> disprovedSub = new ArrayList<>();
        for(int i = 0; i<proved.args.size(); i++){
            Term t1 = proved.args.get(i);
            Term t2 = negatedAtom.args.get(i);

            if(t1 instanceof Constant && t2 instanceof Variable){
                disprovedSub.add(new Sub((Variable) t2, t1));
            }
        }
        return disprovedSub;
    }
}
