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
        private List<HAnswer> hAnswers;
        private List<EAnswer> eAnswers;

        private AtomList query;

        private int time;

        public HypotheticalReasoner() {
                pBuilder = new ProgramBuilder();
                this.query = null;
                this.hAnswers = null;
                this.eAnswers = null;
                this.time = -1;
        }

        public HypotheticalReasoner(String filepath){
                this();
                try {
                        Scanner input = new Scanner(new File(filepath));
                        while(input.hasNextLine()){
                                addClause(input.nextLine());
                        }
                        input.close();

                } catch (FileNotFoundException e) {
                        throw new IllegalArgumentException("Filepath is not a valid path");
                }

        }

        public void addClause(String representation){
                pBuilder.addClause(representation);
                this.query = null;
                this.hAnswers = null;
                this.eAnswers = null;
                this.time = -1;
        }

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

        public void nextTime(String dataSliceRep){
                AtomList dataSlice = stringToAtomList(dataSliceRep);
                for(Atom a: dataSlice){
                        if(a.temporal.tVar != null) throw new IllegalArgumentException("Temporal aspect of "+a.toString()+" is not initiated");
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


        public AtomList getQuery(){
                return this.query;
        }

        public List<HAnswer> hypotheticalAnswers(){
                if(this.hAnswers == null) throw new IllegalStateException("The Reasoner must be queried before hypothetical answers are generated.");
                return hAnswers;
        }

        public List<EAnswer> evidenceAnswers(){
                if(this.hAnswers == null) throw new IllegalStateException("The Reasoner must be queried before evidence answers are generated.");
              return this.eAnswers;
        }


        @Override
        public String toString() {
                StringBuilder b = new StringBuilder();
                if(pBuilder.size()>0) b.append("Program:\n");
                b.append(pBuilder.getProgram().toString());

                if(hAnswers == null){
                        return b.toString();
                }
                b.append("\n");
                b.append("Hypothetical Answers:\n");
                for(HAnswer hAnswer: hAnswers){
                        b.append("\t").append(hAnswer.toString(this.query)).append("\n");
                }
                b.append("\n");

                b.append("Evidence Answers:\n");
                for(EAnswer eAnswer: eAnswers){
                        b.append("\t").append(eAnswer.toString(this.query)).append("\n");
                }
                b.append("\n");

                b.append("Answers:\n");
                for(EAnswer eAnswer: eAnswers){
                        if(eAnswer.constantPremise.isEmpty() && eAnswer.temporalPremise.isEmpty()) {
                                b.append("\t").append(eAnswer.substitution.toString(this.query)).append("\n");
                        }
                }

                return b.toString();
        }
}
