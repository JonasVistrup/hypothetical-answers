import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import Hypothetical.*;
import Logic.*;
import SLD.*;

public class HypotheticalReasoner {
        private final ProgramBuilder pBuilder;
        private Query query;
        private List<Answer> hypothetical;
        private List<Query> queries;
        private int time;

        /**
         * Constructs a reasoner with an empty program.
         */
        public HypotheticalReasoner() {
                pBuilder = new ProgramBuilder();
                this.query = null;
                this.queries = null;
                this.time = -1;
                this.hypothetical = new ArrayList<>();
        }

        /**
         * Constructs a reasoner with a program specified by the file given.
         * @param programPath filepath of a string version of the program.
         */
        public HypotheticalReasoner(String programPath){
                this();
                try {
                        Scanner input = new Scanner(new File(programPath));
                        while(input.hasNextLine()){
                                addClause(input.nextLine());
                        }
                        input.close();

                } catch (FileNotFoundException e) {
                        throw new IllegalArgumentException("Filepath is not a valid path");
                }

        }

        /**
         * Adds a clause to the program.
         * @param representation string representation of clause.
         */
        public void addClause(String representation){
                pBuilder.addClause(representation);
                this.query = null;
                this.queries = null;
                this.time = -1;
        }

        /**
         * Queries the program and creates hypothetical answers.
         * @param atomsRep String representation of atoms to query.
         */
        public void query(String atomsRep){
                atomsRep = atomsRep.replaceAll(" ", "");
                Atom queriedAtom = pBuilder.parseAtom(atomsRep);

                Set<Atom> queried = new HashSet<>();
                List<Atom> atomsToQuery = new ArrayList<>();

                this.queries = new ArrayList<>();
                this.time = 0;

                queried.add(queriedAtom);
                atomsToQuery.add(queriedAtom);

                while(!atomsToQuery.isEmpty()){
                        Atom next = atomsToQuery.remove(0); // Would be faster if last atom is taken.
                        List<Answer> nextPreprocessAnswers = ModifiedSLDResolution.preprocess(pBuilder.getProgram(), new AtomList(next));
                        Query nextQ = new Query(next, nextPreprocessAnswers);
                        addNewAtomsToQuery(queried, atomsToQuery, nextQ);
                        queries.add(nextQ);
                }

                this.query = queries.get(0);
                this.hypothetical = this.query.answers;
        }

        private void addNewAtomsToQuery(Set<Atom> queries, List<Atom> atomsToQuery, Query query){ //TODO make one list instead of a list + a set.
                for(Answer answer: query.answers){
                        for(Atom atom: answer.premise.negative()){
                                Atom variant = atom.getInstance(-1);
                                if(variant.temporal.tVar != null){ // Set time to T
                                        variant = new Atom(variant.predicate, variant.args, new Temporal(variant.temporal.tVar, 0));
                                }
                                System.out.println(variant);
                                if(!queries.contains(variant)){
                                        queries.add(variant);
                                        atomsToQuery.add(variant);
                                }
                        }
                }
        }

        /**
         * Updates the Reasoner with
         * @param dataSliceRep String representation of the atoms arriving in at the next time. All atoms must be initated to the current time.
         */
        public void nextTime(String dataSliceRep){
                if(query == null) throw  new IllegalStateException("query must be called before time slices can be added");
                AtomList dataSlice = stringToAtomList(dataSliceRep);
                for(Atom a: dataSlice){
                        if(a.temporal.tVar != null) throw new IllegalArgumentException("Temporal aspect of "+a.toString()+" is not initiated");
                        if(a.temporal.tConstant != this.time) throw new IllegalArgumentException("Time constant of "+a.toString()+" is not equal to current time: "+this.time);
                }

                queries = UpdateQueries.update(queries, dataSlice, time, pBuilder.getConstants());
                this.query = queries.get(0);

                this.time = this.time + 1;
        }


        private AtomList stringToAtomList(String stringRep){
                AtomList atomList = new AtomList();

                stringRep = stringRep.replaceAll(" ", "");
                if(stringRep.isEmpty()){
                        return atomList;
                }

                stringRep = stringRep.replaceAll("\\),", ") ");
                String[] atomRepList = stringRep.split(" ");

                for(String atomRep: atomRepList){
                        atomList.add(pBuilder.parseAtom(atomRep));
                }
                return atomList;
        }


        /**Returns the query of the program.
         * @return The query of the program.
         */
        public Atom getQuery(){
                return this.query.queriedAtom;
        }

        /**
         * Returns the hypothetical answers generated during preprocessing.
         * @throws IllegalStateException if the reasoner has not been queried yet
         * @return list of hypothetical answers generated during preprocessing
         */
        public List<Answer> hypotheticalAnswers(){
                if(this.query == null) throw new IllegalStateException("The Reasoner must be queried before hypothetical answers are generated.");
                List<Answer> res = new ArrayList<>();
                for(Answer answer: query.answers){
                        if(answer.evidence.isEmpty()){
                                res.add(answer);
                        }
                }
                Collections.sort(res);
                return res;
        }

        public void addConstant(String representation){
                representation = representation.replaceAll(" ","");
                String[] constantStrings = representation.split(",");
                for(String constant: constantStrings){
                        pBuilder.addConstant(constant);
                }
        }

        /**
         * Returns the supported answers of the current time.
         * @throws IllegalStateException if the reasoner has not been queried yet
         * @return list of the current supported answers
         */
        public List<Answer> evidenceAnswers(){
                if(this.query == null) throw new IllegalStateException("The Reasoner must be queried before evidence answers are generated.");
                List<Answer> res = new ArrayList<>();
                for(Answer answer: query.answers){
                        if(!answer.evidence.isEmpty()){
                                res.add(answer);
                        }
                }
                Collections.sort(res);
                return res;
        }


        /**
         * Gives a string of the reasoner which provides an overview of the program, hypothetical answers, supported answers and concluded answers.
         * @return string copy of the reasoner
         */
        @Override
        public String toString() {
                StringBuilder b = new StringBuilder();
                if(pBuilder.size()>0) b.append("Program:\n");
                b.append(pBuilder.getProgram().toString());

                if(query == null){
                        return b.toString();
                }
                b.append("\n");
                b.append("Hypothetical Answers:\n");
                for(Answer answer: hypothetical){
                        b.append("\t").append(answer.toString(this.query.queriedAtom)).append("\n");
                }
                b.append("\n");

                b.append("Evidence Answers:\n");
                for(Answer answer: evidenceAnswers()){
                        b.append("\t").append(answer.toString(this.query.queriedAtom)).append("\n");
                }
                b.append("\n");

                b.append("Answers:\n");
                for(Answer answer: evidenceAnswers()){
                        if(answer.premise.isEmpty()) {
                                b.append("\t").append(answer.substitution.toString(this.query.queriedAtom)).append("\n");
                        }
                }

                return b.toString();
        }

}
