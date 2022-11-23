import Hypothetical.*;
import Logic.*;
import SLD.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



/**
 * Class which allows the user to define a logical system of define temporal clauses.
 * That logical system can then be queried, creating hypothetical answer based upon some premise.
 * A stream of data can then be given such that the hypothetical answer now become supported evidence answers.
 */
public class HypotheticalReasoner {
        private final ProgramBuilder pBuilder;
        private List<PreprocessingAnswer> hAnswers;
        private List<SupportedAnswer> eAnswers;

        private AtomList query;

        private int time;

        /**
         * Constructs a reasoner with an empty program.
         */
        public HypotheticalReasoner() {
                pBuilder = new ProgramBuilder();
                this.query = null;
                this.hAnswers = null;
                this.eAnswers = null;
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
         * Adds a clause to the program.
         * @param representation string representation of clause.
         */
        public void addClause(String representation){
                pBuilder.addClause(representation);
                this.query = null;
                this.hAnswers = null;
                this.eAnswers = null;
                this.time = -1;
        }

        /**
         * Queries the program and creates hypothetical answers.
         * @param atomsRep String representation of atoms to query.
         */
        public void query(String atomsRep){
                atomsRep = atomsRep.replaceAll(" ", "");
                atomsRep = atomsRep.replaceAll("\\),", " ");
                String[] atomRepList = atomsRep.split(" ");
                this.query = new AtomList();
                for(String atom: atomRepList){
                        this.query.add(pBuilder.parseAtom(atom));
                }

                this.hAnswers = ModifiedSLDResolution.preprocess(pBuilder.getProgram(), this.query);
                this.eAnswers = new ArrayList<>();
                this.time = 0;
        }

        /**
         * Updates the Reasoner with
         * @param dataSliceRep String representation of the atoms arriving in at the next time. All atoms must be initiated to the current time.
         */
        public void nextTime(String dataSliceRep){
                if(hAnswers == null) throw  new IllegalStateException("query must be called before time slices can be added");
                AtomList dataSlice = stringToAtomList(dataSliceRep);
                for(Atom a: dataSlice){
                        if(a.temporal.tVar != null) throw new IllegalArgumentException("Logic.Temporal aspect of "+a.toString()+" is not initiated");
                        if(a.temporal.tConstant != this.time) throw new IllegalArgumentException("Time constant of "+a.toString()+" is not equal to current time: "+this.time);
                }
                eAnswers = UpdateAnswer.update(hAnswers, eAnswers, dataSlice, this.time);
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
                return this.query;
        }

        /**
         * Returns the hypothetical answers generated during preprocessing.
         * @throws IllegalStateException if the reasoner has not been queried yet
         * @return list of hypothetical answers generated during preprocessing
         */
        public List<PreprocessingAnswer> hypotheticalAnswers(){
                if(this.hAnswers == null) throw new IllegalStateException("The Reasoner must be queried before hypothetical answers are generated.");
                return hAnswers;
        }

        /**
         * Returns the supported answers of the current time.
         * @throws IllegalStateException if the reasoner has not been queried yet
         * @return list of the current supported answers
         */
        public List<SupportedAnswer> evidenceAnswers(){
                if(this.hAnswers == null) throw new IllegalStateException("The Reasoner must be queried before evidence answers are generated.");
              return this.eAnswers;
        }


        /**
         * Gives a string of the reasoner which provides an overview of the program, hypothetical answers, supported answers and concluded answers.
         * @return string copy of the reasoner
         */
        @Override
        public String toString() {
                StringBuilder b = new StringBuilder();
                if(pBuilder.size()>0) b.append("Logic.Program:\n");
                b.append(pBuilder.getProgram().toString());

                if(hAnswers == null){
                        return b.toString();
                }
                b.append("\n");
                b.append("Hypothetical Answers:\n");
                for(PreprocessingAnswer hAnswer: hAnswers){
                        b.append("\t").append(hAnswer.toString(this.query)).append("\n");
                }
                b.append("\n");

                b.append("Evidence Answers:\n");
                for(SupportedAnswer eAnswer: eAnswers){
                        b.append("\t").append(eAnswer.toString(this.query)).append("\n");
                }
                b.append("\n");

                b.append("Answers:\n");
                for(SupportedAnswer eAnswer: eAnswers){
                        if(eAnswer.constantPremise.isEmpty() && eAnswer.temporalPremise.isEmpty()) {
                                b.append("\t").append(eAnswer.substitution.toString(this.query)).append("\n");
                        }
                }

                return b.toString();
        }
}
