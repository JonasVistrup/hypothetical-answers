package Jonas;

import Jonas.Hypothetical.Answer;
import Jonas.Hypothetical.HypotheticalReasoner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Main {
    public static void main(String[] args) throws IOException {
        testDataLength(100,5);
      }

    private static void testDataLength(int maxLength, int numberOfIterations) {
        ArrayList<Long> executionTimes = new ArrayList<>();
        for(int i = 10; i<=maxLength; i+= 10){
           executionTimes.add(testDataInner(i, numberOfIterations));
        }
        System.out.println(executionTimes);
    }

    public static void connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:C:/Users/vistrup/Desktop/db/mydb.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
            //conn.prepareStatement("CREATE TABLE queries(ID int PRIMARY KEY, varchar(255));")
            conn.prepareStatement("CREATE TABLE answers(queryid int, query varchar(65535), evidence varchar(65535), clauses varchar(65535));").execute();
            conn.prepareStatement("CREATE TABLE hypanswers(queryid int, query varchar(65535), premise varchar(65535), evidence varchar(65535), clauses varchar(65535));").execute();
            conn.prepareStatement("INSERT INTO answers VALUES (2,\"Lead(Topic,Region,T)\", \"wee\", \"Lee\");").execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private static long testDataInner(int dataLength, int numberOfIterations){
        System.out.println("Length="+dataLength);
        HypotheticalReasoner h = new HypotheticalReasoner("LeadProgramSmall");
        h.query("Lead(Topic,Region,T)");
        long sumOfExecutionTimes = 0L;
        for(int j=0; j<numberOfIterations; j++){
            sumOfExecutionTimes += testDataInnerst(h, dataLength, j);
        }
        long time = sumOfExecutionTimes/numberOfIterations;
        System.out.println(time);
        //List<Answer> answerList = h.answers();
        //System.out.println(h.answers());
        return time;
    }

    private static long testDataInnerst(HypotheticalReasoner h, int dataLength, int time){
        String data = generateLongData(dataLength,time);
        long startTime = System.nanoTime();
        h.nextTime(data);
        long endTime = System.nanoTime();
        return (endTime-startTime);
    }

    private static String generateLongData(int amount, int time) {
        if(amount == 0) return "";
        StringBuilder builder = new StringBuilder(generateTrendingTopic("topic0",time)).append(",").append(generatePopularity("topic0",time,time));
        for(int i = 1; i<amount; i++){
            builder.append(",").append(generateTrendingTopic("topic"+i,time)).append(",");
            builder.append(generatePopularity("topic"+i,time,time));
        }
        return builder.toString();
    }

    private static String generateTrendingTopic(String topic, int time){
        return "DailyTrend("+topic+",dk,"+time+")";
    }
    private static String generatePopularity(String topic, int pop, int time){
        return "Popularity("+topic+",dk,"+pop+","+time+")";
    }

    private static void testRuleLength(int maxLength, int numberOfIterations) throws IOException {

        ArrayList<Long> executionTimes = new ArrayList<>();
        for(int i = 0; i<=maxLength; i+=1000){
            System.out.println("Length="+i);
            long sumOfExecutionTimes = 0L;
            FileWriter fileWriter = new FileWriter(new File("LengthyProgram"), false);
            String rule = generateLongRule(i);
            fileWriter.write(rule);
            fileWriter.flush();
            for(int j=0; j<numberOfIterations; j++){
                long startTime = System.nanoTime();
                HypotheticalReasoner h = new HypotheticalReasoner("LengthyProgram");
                h.query("Lead(Topic,Region,T)");
                long endTime = System.nanoTime();

                sumOfExecutionTimes += (endTime-startTime);
            }
            executionTimes.add((sumOfExecutionTimes/numberOfIterations));
        }

        System.out.println(executionTimes);
    }

    private static String simpleDataSlice(int time){
        return "DailyTrend(News, Denmark, " + time + ")";
    }
    private static String generateLongRule(int length){
        StringBuilder builder = new StringBuilder("Lead(Topic, Region, T) <- DailyTrend(Topic, Region, T)");
        for(int i = 1; i<length; i++){
            builder.append(", DailyTrend(Topic, Region, T+").append(i).append(")");
        }
        return builder.toString();
    }
    private static void test1(){
        HypotheticalReasoner h = new HypotheticalReasoner("SofiaProgramOld");
        h.query("Lead(Topic,Region,T)");
        h.nextTime("GTD(christmas, capital, 10, 0),Tweets(christmas, capital, 60, 0)");
        h.nextTime("GTD(christmas, capital, 6, 1),Tweets(christmas, capital, 40, 1)");
        h.nextTime("GTD(christmas, capital, 10, 2),Tweets(christmas, capital, 51, 2)");
        h.nextTime("GTD(christmas, capital, 15, 3),Tweets(christmas, capital, 60, 3)");
        h.nextTime("GTD(christmas, capital, 25, 4),Tweets(christmas, capital, 70, 4)");
        System.out.println(h);
    }

    private static void test2(){
        HypotheticalReasoner h = new HypotheticalReasoner("TempProgram");
        System.out.println(h);
        System.out.println("------------------------------------------------\n");

        h.query("Malf(X,T)");
        System.out.println(h);
        System.out.println("------------------------------------------------\n");

        h.nextTime("Temp(wt25,high,0),Temp(wt12,high,0)");
        System.out.println(h);
        System.out.println("------------------------------------------------\n");

        h.nextTime("Temp(wt25,high,1),Temp(wt12,high,1)");
        System.out.println(h);
        System.out.println("------------------------------------------------\n");

        h.nextTime("Temp(wt25,high,2),Temp(wt12,high,2)");
        System.out.println(h);
        System.out.println("------------------------------------------------\n");

        h.nextTime("P(wt25,3),Temp(wt12,high,3)");
        System.out.println(h);
        System.out.println("------------------------------------------------\n");
    }

}
