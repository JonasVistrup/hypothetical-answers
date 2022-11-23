public class Main {
    public static void main(String[] args){
        test2();
    }


    private static void test1(){
        HypotheticalReasoner h = new HypotheticalReasoner();
        h.addClause("Flag(X,T)<-Temp(X,high,T)");
        h.addClause("Cool(X,T+1)<-Flag(X,T),Flag(X,T+1)");
        h.addClause("Shdn(X,T+1)<-Cool(X,T),Flag(X,T+1)");
        h.addClause("Malf(X,T-2)<-Shdn(X,T)");
        System.out.println(h);
        System.out.println("------------------------------------------------\n");

        h.query("HotTopic(X,T)");
        System.out.println(h);
        System.out.println("------------------------------------------------\n");

        h.nextTime("Temp(wt25,high,0),Trendy(Paris,0),Temp(wt24,low,0)");
        System.out.println(h);
        System.out.println("------------------------------------------------\n");

        h.nextTime("Temp(wt25,high,1)");
        System.out.println(h);
        System.out.println("------------------------------------------------\n");

        h.nextTime("Temp(wt25,high,2),Temp(wt12,low,2)");
        System.out.println(h);
        System.out.println("------------------------------------------------\n");

        h.nextTime("P(wt25,3)");
        System.out.println(h);
        System.out.println("------------------------------------------------\n");
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
