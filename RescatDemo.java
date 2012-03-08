//~--- non-JDK imports --------------------------------------------------------

import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;


//~--- JDK imports ------------------------------------------------------------

import java.io.*;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * @author Cristian Planas | Pere Joan Martorell
 *
 */

public class RescatDemo {

    static int demo;
    static int h;
    static int g;
    static boolean trace;
    static int hf;

    public static void main(String[] args) throws Exception {
        System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("┃                         Selecció de Demostracions                            ┃");
        System.out.println("┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫");
        System.out.println("┃ Demostració 1: Hill Climbing amb Heurístic 1                                 ┃");
        System.out.println("┃ Demostració 2: Hill Climbing amb Heurístic 2                                 ┃");
        System.out.println("┃ Demostració 3: Simulated Annealing amb Heurístic 1                           ┃");
        System.out.println("┃ Demostració 4: Simulated Annealing amb Heurístic 2                           ┃");
        System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");

        
        // Executar HC HF1 
        // Executar HC HF2 
        // idem SA H1 
        // Idem SA HF 
        // G: [1,100]
        // H: [1,10]
        // Print Temps_total, Elapsed, Steps
        // Vols veure el trace?


        demo = -1;
        h = -1;
        g = -1;
        trace = false;
        String t = "";
        int s = -1;
        int n = -1;



        while ((demo > 4) || (demo < 1)) {
            System.out.println("◷ Introdueix el valor de la demostració a realitzar (entre 1 i 4)");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            demo = Integer.parseInt(in.readLine());
        }

        while ((h > 10) || (h < 1)) {
            System.out.println("◶ Introdueix el nombre d'helicòpters de rescat (entre 1 i 10)");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            h = Integer.parseInt(in.readLine());
        }

        while ((g > 100) || (g < 1)) {
            System.out.println("◵ Introdueix el nombre de grups a rescatar (entre 1 i 100)");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            g = Integer.parseInt(in.readLine());
        }
        while (s != 0 && n != 0) {
            System.out.println("◴ Voleu veure el rastre de moviments de l'algoritme? (S/N)");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            t = in.readLine();
            s = t.compareToIgnoreCase("S");
            n = t.compareToIgnoreCase("N");
        }
        if (s==0) trace = true;

        RescatBoard              RB  = new RescatBoard(g, h);

        System.out.println("\n+--------------------+------------+------------------+------------------+------------------+----------+");
        System.out.println("|       Demostració  |    HF      |   Temps_total    |    Suma_temps    |      Elapsed     |  Steps   |");
        System.out.println("+--------------------+------------+------------------+------------------+------------------+----------+");

        switch (demo) {
          case 1:
            hf=1;
            RHillClimbingSearch(RB);
            break;
          case 2:
            hf = 2;
            RHillClimbingSearch(RB);
            break;
          case 3:
            hf = 1;
            RSimulatedAnnealingSearch(RB);
            break;
          case 4:
            hf = 2;
            RSimulatedAnnealingSearch(RB);
            break;
        }

        //RescatHeuristicFunction2 rhf = new RescatHeuristicFunction2();
       
    }

    private static void RHillClimbingSearch(RescatBoard RB) {

        // System.out.println("\n#######################");
        // System.out.println("# Rescat HillClimbing #");
        // System.out.println("#######################");
        long ini, fi;

        try {
            Problem problem;
            if(hf == 1){
              problem = new Problem(RB, new RescatSuccessorFunction(), new RescatGoalTest(),
                                          new RescatHeuristicFunction1());
            }
            else{
              problem = new Problem(RB, new RescatSuccessorFunction(), new RescatGoalTest(),
                                          new RescatHeuristicFunction2());
                                          }

            ini = System.currentTimeMillis();

            Search search = new HillClimbingSearch();

            ini = System.currentTimeMillis();

            SearchAgent agent = new SearchAgent(problem, search);

            fi = System.currentTimeMillis();

            aima.search.framework.HeuristicFunction HF;
            if(hf==1){
               HF     = new RescatHeuristicFunction1();
            }
            else{
               HF     = new RescatHeuristicFunction2();

            }
            RescatBoard              rescat = ((RescatBoard) ((HillClimbingSearch) search).getLastSearchState());
            double                   hvalue     = HF.getHeuristicValue(rescat);

            // int nodes = Integer.parseInt( (String) agent.getInstrumentation().get("nodesExpanded"));
            int steps = agent.getActions().size();

            double tempsdarrer = RB.getTempsDarrer();

            double tempstotal = RB.getTempsTotal();

            String dem = "HC("+g+","+h+") HF"+hf;

            System.out.format("| %18s | %10.2f | %16f | %16f | %16d | %8d |\n", dem, hvalue, tempsdarrer, tempstotal, fi-ini, steps);
            System.out.println("+--------------------+------------+------------------+------------------+------------------+----------+");

            if(trace){
              System.out.print("\nPremeu una tecla per mostrar el rastre de moviments..");
              BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
              in.readLine();
              String S;
              S = "\n" + RB.toStringSortides();
              S += RB.toStringPersones();
              System.out.println(S);
              printActions(agent.getActions());
              printInstrumentation(agent.getInstrumentation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void RSimulatedAnnealingSearch(RescatBoard RB) {

        // System.out.println("\n##############################");
        // System.out.println("# Rescat Simulated Annealing #");
        // System.out.println("##############################");
        long ini, fi;

        try {

            Problem problem;
            if(hf == 1){
              problem = new Problem(RB, new RescatSuccessorFunctionSA(), new RescatGoalTest(),
                                          new RescatHeuristicFunction1());
            }
            else{
              problem = new Problem(RB, new RescatSuccessorFunctionSA(), new RescatGoalTest(),
                                          new RescatHeuristicFunction2());
            }

            
            SimulatedAnnealingSearch search = new SimulatedAnnealingSearch(4000, 140, 30, 0.008);

            // search.traceOn();
            ini = System.currentTimeMillis();

            SearchAgent agent = new SearchAgent(problem, search);

            fi = System.currentTimeMillis();

            aima.search.framework.HeuristicFunction HF;
            if(hf==1){
               HF     = new RescatHeuristicFunction1();
            }
            else{
               HF     = new RescatHeuristicFunction2();
            }
            RescatBoard              rescat = ((RescatBoard) ((SimulatedAnnealingSearch) search).getLastSearchState());
            double                   hvalue     = HF.getHeuristicValue(rescat);
            int                      steps  = agent.getActions().size();

            double tempsdarrer = RB.getTempsDarrer();

            double tempstotal = RB.getTempsTotal();
            String dem = "SA("+g+","+h+") HF"+hf;

            System.out.format("| %18s | %10.2f | %16f | %16f | %16d | %8d |\n", dem, hvalue, tempsdarrer, tempstotal, fi-ini, steps);
            System.out.println("+--------------------+------------+------------------+------------------+------------------+----------+");
            if(trace){
              System.out.print("\nPremeu una tecla per mostrar el rastre de moviments..");
              BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
              in.readLine();
              String S;
              S = "\n" + RB.toStringSortides();
              S += RB.toStringPersones();
              System.out.println(S);
              printActions(agent.getActions());
              printInstrumentation(agent.getInstrumentation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printInstrumentation(Properties properties) {
        Iterator keys = properties.keySet().iterator();

        while (keys.hasNext()) {
            String key      = (String) keys.next();
            String property = properties.getProperty(key);

            System.out.println(key + " : " + property);
        }
    }

    private static void printActions(List actions) {
        for (int i = 0; i < actions.size(); i++) {
            String action = (String) actions.get(i);

            System.out.println(action);
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
