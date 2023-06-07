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
    private String c = "C:/Users/vistrup/Desktop/db/mydb.db";
    private int nextQuery;

    public DBConnection(String connectionURL, ProgramBuilder pb){
        nextQuery = 0;
        this.pb = pb;
        try {
            // db parameters
            String url = "jdbc:sqlite:"+connectionURL;
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            conn.prepareStatement("DELETE FROM queries WHERE true;").execute();
            conn.prepareStatement("DELETE FROM answers WHERE true;").execute();
            conn.prepareStatement("DELETE FROM hypotheticalanswers WHERE true;").execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addQuery(Query query){
        try {
            conn.prepareStatement(format("INSERT INTO queries VALUES (%d, %s);", nextQuery, query.queriedAtoms.toString())).execute();
            nextQuery++;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addAnswer(Answer answer, Query query){
        assert answer.premise.isEmpty();
        try {
            conn.prepareStatement(format("INSERT INTO answers VALUES (%d, %s, %s);", query.index, answer.resultingQueriedAtoms.toString(), answer.evidence.toString())).execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void addHypotheticalAnswer(Answer hypoAnswer, Query query){
        assert !hypoAnswer.premise.isEmpty();
        try {
            conn.prepareStatement(format("INSERT INTO answers VALUES (%d, %s, %s, %s);", query.index, hypoAnswer.resultingQueriedAtoms.toString(), hypoAnswer.premise.toString(), hypoAnswer.evidence.toString())).execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Answer> getAnswers(Query query){
        List<Answer> result = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(format("SELECT * FROM answers WHERE query=%d;",query.index));
            while(resultSet.next()){
                AtomList resultingQuery = pb.parseAtomList(resultSet.getString(2));
                AtomList evidence       = pb.parseAtomList(resultSet.getString(3));
                Substitution sub        = Unify.findMGUAtomList(query.queriedAtoms, resultingQuery);
                result.add(new Answer(resultingQuery, sub, evidence, new AtomList()));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }

     public Iterator<Answer> getHypotheticalAnswers(Query query){
         try {
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(format("SELECT * FROM hypotheticalanswers WHERE query=%d;",query.index));
             return new Iterator<Answer>() {
                 @Override
                 public boolean hasNext() {
                     try {
                         return resultSet.isLast();
                     } catch (SQLException e) {
                         throw new RuntimeException(e);
                     }
                 }

                 @Override
                 public Answer next() {
                     try {
                         resultSet.next();
                         AtomList resultingQuery = pb.parseAtomList(resultSet.getString(2));
                         AtomList premise        = pb.parseAtomList(resultSet.getString(3));
                         AtomList evidence       = pb.parseAtomList(resultSet.getString(4));
                         Substitution sub        = Unify.findMGUAtomList(query.queriedAtoms, resultingQuery);
                         return new Answer(resultingQuery, sub, evidence, premise);
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

}
