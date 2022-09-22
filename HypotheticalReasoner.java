import java.util.List;

public class HypotheticalReasoner {
        private ProgramBuilder activeBuilder;
        private List<HAnswer> hAnswers;

        private AtomList query;

        private int time = -1;

        public HypotheticalReasoner() {
                activeBuilder = new ProgramBuilder();
                hAnswers = null;
                query = null;
        }

        public void addClause(String representation){
                activeBuilder.addClause(representation);
        }

        public void query(String atomsRep){
                atomsRep = atomsRep.replaceAll(" ", "");
                atomsRep = atomsRep.replaceAll("\\),", " ");
                String[] atomRepList = atomsRep.split(" ");
                this.query = new AtomList();
                for(String atom: atomRepList){
                        this.query.add(activeBuilder.parseAtom(atom));
                }

                this.hAnswers = ModifiedSLDResolution.preprocess(activeBuilder.getProgram(), this.query);
                this.time = 0;
        }


        public AtomList getQuery(){
                return this.query;
        }

        public List<HAnswer> getHypotheticalAnswers(){
                if(hAnswers == null) throw new IllegalStateException("The Reasoner must be queried before hypothetical answers are generated.");
                return hAnswers;
        }


        @Override //TODO
        public String toString() {
                return activeBuilder.getProgram().toString();
        }
}
