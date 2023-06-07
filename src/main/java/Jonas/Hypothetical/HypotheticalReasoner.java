package Jonas.Hypothetical;

import Jonas.Logic.*;
import Jonas.SLD.ModifiedSLDResolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import org.json.JSONObject;
/**
 * Class which allows the user to define a logical system of define temporal clauses.
 * That logical system can then be queried, creating hypothetical answer based upon some premise.
 * A stream of data can then be given such that the hypothetical answer now become supported evidence answers.
 */
public class HypotheticalReasoner {
        private final ProgramBuilder pBuilder;
        private List<Query> queries;
        private int time;
        private

        /**
         * Constructs a reasoner with an empty program.
         */
        public HypotheticalReasoner() {
                this.pBuilder = new ProgramBuilder();
                this.queries = new ArrayList<>();
                this.time = -1;
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
         * Constructs a reasoner with a program specified by the file given, and a file of queries.
         * @param programPath filepath of a string version of the program.
         * @param queriesPath filepath of a string version of the queries.
         */
        public HypotheticalReasoner(String programPath, String queriesPath){
                this(programPath);
                queryFromFile(queriesPath);
        }

        /**
         * Adds a clause to the program.
         * @param representation string representation of clause.
         */
        public void addClause(String representation){
                pBuilder.addClause(representation);
                this.queries = new ArrayList<>();
                this.time = -1;
        }

        /**
         * Queries the program and creates hypothetical answers.
         * @param filename name of the file containing queries in text format.
         */
        public void queryFromFile(String filename){
                try {
                        Scanner input = new Scanner(new File(filename));
                        while(input.hasNextLine()){
                                query(input.nextLine());
                        }
                        input.close();

                } catch (FileNotFoundException e) {
                        throw new IllegalArgumentException("Filepath is not a valid path");
                }
        }


        /**
         * Queries the program and creates hypothetical answers.
         * @param atomsRep String representation of atoms to query.
         */
        public void query(String atomsRep){
                atomsRep = atomsRep.replaceAll(" ", "");
                atomsRep = atomsRep.replaceAll("\\),", " ");
                String[] atomRepList = atomsRep.split(" ");
                AtomList query = new AtomList();
                for(String atom: atomRepList){
                        query.add(pBuilder.parseAtom(atom));
                }

                List<Answer> preprocessingAnswers = ModifiedSLDResolution.preprocess(pBuilder.getProgram(), query);
                this.queries.add(new Query(query, preprocessingAnswers,this.queries.size()));
        }

        /**
         * Updates the Reasoner with
         * @param dataSliceRep String representation of the atoms arriving in at the next time. All atoms must be initiated to the current time.
         */
        public void nextTime(String dataSliceRep){
                if(queries.isEmpty()) throw  new IllegalStateException("query must be called before time slices can be added");

                AtomList dataSlice = stringToAtomList(dataSliceRep);
                for(Atom a: dataSlice){
                        if(a.temporal.tVar != null) throw new IllegalArgumentException("Logic.Temporal aspect of "+a.toString()+" is not initiated");
                        if(this.time == -1){
                                this.time = a.temporal.tConstant;
                        }
                        if(a.temporal.tConstant != this.time) throw new IllegalArgumentException("Time constant of "+a.toString()+" is not equal to current time: "+this.time);
                }

                Program dataSliceProgram = dataSlice.toProgram();
                for(Query q: queries){
                        q.update(dataSliceProgram, this.time);

                }
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
        public AtomList getQuery(){
                return this.queries.get(0).queriedAtoms;
        }

        /**
         * Returns the hypothetical answers generated during preprocessing.
         * @throws IllegalStateException if the reasoner has not been queried yet
         * @return list of hypothetical answers generated during preprocessing
         */
        public List<Answer> preprocessingAnswers(){
                if(this.queries.isEmpty()) throw new IllegalStateException("The Reasoner must be queried before hypothetical answers are generated.");
                List<Answer> list = queries.get(0).preprocessingAnswers;
                Collections.sort(list);
                return list;
        }

        /**
         * Returns the supported answers of the current time.
         * @throws IllegalStateException if the reasoner has not been queried yet.
         * @return list of the current supported answers.
         */
        public List<Answer> supportedAnswers(){
                if(this.queries.isEmpty()) throw new IllegalStateException("The Reasoner must be queried before evidence answers are generated.");
                Query query = queries.get(0);
                List<Answer> res = new ArrayList<>();
                for(Answer answer: query.supportedAnswers){
                        if(!answer.evidence.isEmpty()){
                                res.add(answer);
                        }
                }
                Collections.sort(res);
                return res;
        }

        /**
         * Returns the answers of the current time.
         * @throws IllegalStateException if the reasoner has not been queried yet.
         * @return list of the current answers.
         */
        public List<Answer> answers(){
                if(this.queries.isEmpty()) throw new IllegalStateException("The Reasoner must be queried before evidence answers are generated.");
                Query query = queries.get(0);
                List<Answer> res = new ArrayList<>();
                for(Answer answer: query.supportedAnswers){
                        if(answer.premise.isEmpty()){
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

                if(this.queries.isEmpty()){
                        return b.toString();
                }
                Query query = queries.get(0);
                b.append("\n");
                b.append("Preprocessing Answers:\n");
                for(Answer answer: preprocessingAnswers()){
                        b.append("\t").append(answer.toString(query.queriedAtoms)).append("\n");
                }
                b.append("\n");

                b.append("Supported Answers:\n");
                for(Answer answer: supportedAnswers()){
                        b.append("\t").append(answer.toString(query.queriedAtoms)).append("\n");
                }
                b.append("\n");

                b.append("Answers:\n");
                for(Answer answer: supportedAnswers()){
                        if(answer.premise.isEmpty()) {
                                b.append("\t").append(answer.substitution.toString(query.queriedAtoms)).append("\n");
                        }
                }

                return b.toString();
        }


        /**
         * Creates a JSON object of the current state.
         * @return JSON object of the current state of the Hypothetical Reasoner.
         */
        public JSONObject toJSONObject(){
                JSONObject o = new JSONObject();
                for(Query q: queries){
                        o.put("Queries",q.toJSONObject());
                }
                return o;
        }

        /**
         * Creates a JSON object of the current state.
         * @return String representation of JSON object.
         */
        public String toJSON(){
                return toJSONObject().toString();
        }



}
