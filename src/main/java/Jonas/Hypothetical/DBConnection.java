package Jonas.Hypothetical;

import Jonas.Logic.AtomList;
import Jonas.Logic.ProgramBuilder;
import Jonas.Logic.Substitution;
import Jonas.SLD.Unify;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.String.format;

public class DBConnection {
    private Connection conn;
    private ProgramBuilder pb;
    private String c = "C:/Users/vistrup/Desktop/mydb.db";

    private boolean first = true;

    private PreparedStatement dataInserter;

    public DBConnection(String connectionURL, ProgramBuilder pb){
        if(connectionURL == null) connectionURL = c;
        this.pb = pb;
        try {
            // db parameters
            String url = "jdbc:sqlite:"+connectionURL;
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            //System.out.println("Connection to SQLite has been established.");

            conn.prepareStatement("CREATE TABLE data(" +
                    "time int, data varchar(65535)" +
                    ");").execute();
            conn.prepareStatement("CREATE TABLE answers(" +
                    "queryid int, query varchar(65535), " +
                    "evidence varchar(65535), " +
                    "clauses varchar(65535)" +
                    ");").execute();
            conn.prepareStatement("CREATE TABLE hypanswers(" +
                    "queryid int, " +
                    "query varchar(65535), " +
                    "premise varchar(65535), " +
                    "evidence varchar(65535), " +
                    "clauses varchar(65535)" +
                    ");").execute();
            conn.prepareStatement("CREATE TABLE hyp2answers(" +
                    "queryid int, " +
                    "query varchar(65535), " +
                    "premise varchar(65535), " +
                    "evidence varchar(65535), " +
                    "clauses varchar(65535)" +
                    ");").execute();
            conn.prepareStatement("DELETE FROM answers WHERE true;").execute();
            conn.prepareStatement("DELETE FROM hypanswers WHERE true;").execute();
            conn.prepareStatement("DELETE FROM hyp2answers WHERE true;").execute();

        } catch (SQLException e) {
            //System.out.println(e.getMessage());
            try {
                conn.prepareStatement("DELETE FROM data WHERE true;").execute();
                conn.prepareStatement("DELETE FROM answers WHERE true;").execute();
                conn.prepareStatement("DELETE FROM hypanswers WHERE true;").execute();
                conn.prepareStatement("DELETE FROM hyp2answers WHERE true;").execute();
            }catch (SQLException e2) {
                System.out.println(e2.getMessage());
            }
        }
        try {
            dataInserter = conn.prepareStatement("INSERT INTO data VALUES (?,?);");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteHypanswer(Answer answer, Query query){
        try {
            conn.prepareStatement(format("DELETE FROM hypanswers WHERE queryid=%d AND query='%s' AND premise='%s' AND evidence='%s' AND clauses='%s';", query.index, answer.resultingQueriedAtoms.toString(), answer.premise.toString(), answer.evidence.toString(), answer.clausesUsed.toString())).execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void insertData(int time, String data){ //TODO use upload partly to increase speed
        try {
            dataInserter.setInt(1,time);
            dataInserter.setString(2,data);
            dataInserter.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void uploadData(ArrayList<String> data, int time) {
        if (data.isEmpty()) return;
        for (int i = 0; i < data.size(); i += 1000) {
            insertDataPartial(data, i, i + 1000, time);
            //System.out.println("i="+i);
        }
    }
    public void insertDataPartial(ArrayList<String> data, int from, int to, int time){
        try {
            Statement uploadStatement = conn.createStatement();

            StringBuilder statement = new StringBuilder("INSERT INTO data VALUES");
            for(int i=from; i<to && i<data.size(); i++){
                statement.append(format(" (%d, '%s'),", time, data.get(i)));
            }
            statement.deleteCharAt(statement.length()-1);
            statement.append(";");
            uploadStatement.executeLargeUpdate(statement.toString());
        } catch (SQLException e) {
            System.out.println("TOO MUCH DATA");
            for(String a: data){
                insertData(time,a);
            }

        }
    }

    public void wipeDB(){
        try {
            conn.prepareStatement("DELETE FROM data WHERE true;").execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Iterator<String> getData(int time){
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(format("SELECT data FROM data WHERE time=%d;",time));

            if(resultSet.getString(1) == null){
                return new Iterator<String>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public String next() {
                        return null;
                    }
                };
            }
            String first = resultSet.getString(1);
            resultSet.next();
            return new Iterator<String>() {
                String next = first;
                @Override
                public boolean hasNext() {
                    try {
                        return !resultSet.isAfterLast();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public String next() {
                    try {
                        String answer = next;
                        resultSet.next();
                        if(resultSet.getString(1) != null) {
                            next = resultSet.getString(1);
                        }
                        return answer;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void addQuery(Query query){
        try {
            conn.prepareStatement(format("INSERT INTO queries VALUES (%d, '%s');", query.index, query.queriedAtoms.toString())).execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addAnswer(Answer answer, Query query){
        if(answer.premise.isEmpty()) addDoneAnswer(answer,query);
        else addHypotheticalAnswer(answer,query);
    }

    private void addDoneAnswer(Answer answer, Query query){
        assert answer.premise.isEmpty();
        try {
            conn.prepareStatement(format("INSERT INTO answers VALUES (%d, '%s', '%s', '%s');", query.index, answer.resultingQueriedAtoms.toString(), answer.evidence.toString(), answer.clausesUsed.toString())).execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addHypotheticalAnswer(Answer hypoAnswer, Query query){
        assert !hypoAnswer.premise.isEmpty();
        String table = first? "hypanswers":"hyp2answers";
        String command = format("INSERT INTO %s VALUES (%d, '%s', '%s', '%s', '%s');", table, query.index, hypoAnswer.resultingQueriedAtoms.toString(), hypoAnswer.premise.toString(), hypoAnswer.evidence.toString(), hypoAnswer.clausesUsed.toString());

        try {
            conn.createStatement().execute(command);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Answer> getAnswers(Query query){
        List<Answer> result = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(format("SELECT * FROM answers WHERE queryid=%d;",query.index));
            while(resultSet.next()){
                AtomList resultingQuery = pb.parseAtomList(resultSet.getString(2));
                AtomList evidence       = pb.parseAtomList(resultSet.getString(3));
                ExplanationList list    = new ExplanationList(resultSet.getString(4), pb);
                Substitution sub        = Unify.findMGUAtomList(query.queriedAtoms, resultingQuery);
                result.add(new Answer(resultingQuery, sub, evidence, new AtomList(), list));
            }
            return result;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    private Answer resultSetToAnswer(ResultSet resultSet, Query query){
        try {
            AtomList resultingQuery = pb.parseAtomList(resultSet.getString(2));
            AtomList premise        = pb.parseAtomList(resultSet.getString(3));
            AtomList evidence       = pb.parseAtomList(resultSet.getString(4));
            Substitution sub        = Unify.findMGUAtomList(query.queriedAtoms, resultingQuery);
            ExplanationList list    = new ExplanationList(resultSet.getString(5), pb);

            return new Answer(resultingQuery, sub, evidence, premise, list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

     public Iterator<Answer> getHypotheticalAnswers(Query query){
         try {
             Statement statement = conn.createStatement();
             String table = first? "hyp2answers":"hypanswers";
             ResultSet resultSet = statement.executeQuery(format("SELECT * FROM %s WHERE queryid=%d;",table, query.index));

             if(resultSet.getString(2) == null){
                 return new Iterator<Answer>() {
                     @Override
                     public boolean hasNext() {
                         return false;
                     }

                     @Override
                     public Answer next() {
                         return null;
                     }
                 };
             }
             Answer first = resultSetToAnswer(resultSet,query);
             resultSet.next();
             return new Iterator<Answer>() {
                 Answer next = first;
                 @Override
                 public boolean hasNext() {
                     try {
                         return !resultSet.isAfterLast();
                     } catch (SQLException e) {
                         throw new RuntimeException(e);
                     }
                 }

                 @Override
                 public Answer next() {
                     try {
                         Answer answer = next;
                         resultSet.next();
                         if(resultSet.getString(2) != null) {
                             next = resultSetToAnswer(resultSet, query);
                         }
                         return answer;
                     } catch (SQLException e) {
                         throw new RuntimeException(e);
                     }
                 }
             };
         } catch (SQLException e) {
             System.out.println(e.getMessage());
         }
         return null;
     }

     public void wipeHyp(){
         String table = first? "hyp2answers":"hypanswers";
         try {
             conn.prepareStatement(format("DELETE FROM %s WHERE true;",table)).execute();
             switchHyp();
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }
     }

     public void switchHyp(){
        first = !first;
     }

    public List<Answer> getNonSupportedAnswers(Query query){
        List<Answer> result = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            String table = first? "hyp2answers":"hypanswers";
            ResultSet resultSet = statement.executeQuery(format("SELECT * FROM %s WHERE queryid=%d AND WHERE evidence='';",table,query.index));
            while(resultSet.next()){
                AtomList resultingQuery = pb.parseAtomList(resultSet.getString(2));
                AtomList premise        = pb.parseAtomList(resultSet.getString(3));
                AtomList evidence       = pb.parseAtomList(resultSet.getString(4));
                Substitution sub        = Unify.findMGUAtomList(query.queriedAtoms, resultingQuery);
                ExplanationList list    = new ExplanationList(resultSet.getString(5), pb);
                result.add(new Answer(resultingQuery, sub, evidence, premise, list));
            }
            return result;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    public List<Answer> getSupportedAnswers(Query query){
        List<Answer> result = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            String table = first? "hyp2answers":"hypanswers";
            ResultSet resultSet = statement.executeQuery(format("SELECT * FROM %s WHERE queryid=%d AND WHERE evidence!='';",table,query.index));
            while(resultSet.next()){
                AtomList resultingQuery = pb.parseAtomList(resultSet.getString(2));
                AtomList premise        = pb.parseAtomList(resultSet.getString(3));
                AtomList evidence       = pb.parseAtomList(resultSet.getString(4));
                Substitution sub        = Unify.findMGUAtomList(query.queriedAtoms, resultingQuery);
                ExplanationList list    = new ExplanationList(resultSet.getString(5), pb);
                result.add(new Answer(resultingQuery, sub, evidence, premise, list));
            }
            return result;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    public void uploadAnswers(ArrayList<Answer> answers, Query query) {
        if(answers.isEmpty()) return;
        for(int i=0; i<answers.size(); i+=1000){
            uploadPartly(answers,query,i,i+1000);
            //System.out.println("i="+i);
        }

        /*try {
            Statement uploadStatement = conn.createStatement();

            StringBuilder statement = new StringBuilder("INSERT INTO answers VALUES");
            for(Answer a: answers){
                statement.append(format(" (%d, '%s', '%s', '%s'),", query.index, a.resultingQueriedAtoms.toString(), a.evidence.toString(), a.clausesUsed.toString()));
            }
            statement.deleteCharAt(statement.length()-1);
            statement.append(";");
            uploadStatement.executeLargeUpdate(statement.toString());
        } catch (SQLException e) {
            for(Answer a: answers){
                addDoneAnswer(a,query);
            }

        }*/
    }

    public void uploadPartly(ArrayList<Answer> answers, Query query, int from, int to){
        try {
            Statement uploadStatement = conn.createStatement();

            StringBuilder statement = new StringBuilder("INSERT INTO answers VALUES");
            for(int i=from; i<to && i<answers.size(); i++){
                Answer a = answers.get(i);
                statement.append(format(" (%d, '%s', '%s', '%s'),", query.index, a.resultingQueriedAtoms.toString(), a.evidence.toString(), a.clausesUsed.toString()));
            }
            statement.deleteCharAt(statement.length()-1);
            statement.append(";");
            uploadStatement.executeLargeUpdate(statement.toString());
        } catch (SQLException e) {
            for(Answer a: answers){
                addDoneAnswer(a,query);
            }

        }
    }
}
