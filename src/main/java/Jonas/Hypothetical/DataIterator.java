package Jonas.Hypothetical;

import Jonas.Logic.AtomList;
import Jonas.Logic.Program;

import java.util.Iterator;

public class DataIterator implements Iterable<Program> {
    private static int MAX_NUMBER_OF_FACTS = 100000;
    int time;
    DBConnection db;
    Iterator<String> iterator;
    HypotheticalReasoner h;

    public DataIterator(int time, DBConnection db, HypotheticalReasoner h){
        this.time = time;
        this.db = db;
        this.iterator = db.getData(time);
        this.h = h;
    }


    @Override
    public Iterator<Program> iterator() {
        return new Iterator<Program>() {
            Iterator<String> dataIterator = iterator;

            @Override
            public boolean hasNext() {
                return dataIterator.hasNext();
            }

            @Override
            public Program next() {
                StringBuilder builder = new StringBuilder();
                for(int i = 0; i<MAX_NUMBER_OF_FACTS && dataIterator.hasNext(); i++){
                    builder.append(dataIterator.next());
                    builder.append(",");
                }
                if(builder.length() == 0) return (new AtomList()).toProgram();
                builder.deleteCharAt(builder.length()-1);
                return h.stringToAtomList(builder.toString()).toProgram();
            }
        };
    }
}
