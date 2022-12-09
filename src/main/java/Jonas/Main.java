package Jonas;

import Jonas.Hypothetical.HypotheticalReasoner;

public class Main {
    public static void main(String[] args){
        test1();
    }


    private static void test1(){
        HypotheticalReasoner h = new HypotheticalReasoner("SofiaProgram");
        h.query("Trending(Topic,Region,T)");
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
