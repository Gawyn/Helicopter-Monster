//~--- non-JDK imports --------------------------------------------------------

import aima.search.framework.HeuristicFunction;

//~--- JDK imports ------------------------------------------------------------

import java.util.LinkedList;

/**
 * @author Cristian Planas | Pere Joan Martorell
 *
 */

public class RescatHeuristicFunction1 implements HeuristicFunction {

    public double getHeuristicValue(Object state) {
        RescatBoard                       board      = (RescatBoard) state;
        int                               nh         = board.getnh();
        int                               ng         = board.getngrups();
        double                            tempsmaxim = 0;
        double                            tempstotal = 0;
        double                            auxtemps   = 0;
        double[][]                        dist       = board.getdistancies();
        int                               n          = 0;
        Integer                           origen     = new Integer(ng);
        int                               desti      = 0;
        LinkedList                        auxlist;
        LinkedList                        auxlist2;
        LinkedList<LinkedList<Integer>>[] sortides = board.getsortides();

        for (int i = 0; i < nh; i++) {
            auxlist = sortides[i];
            n       = sortides[i].size();
            origen  = ng;

            // System.out.println("-------------------");
            // System.out.println(" Sortides Helic. "+i);
            // System.out.println("-------------------");

            for (int j = 0; j < n; j++) {

                // Agafem una sortida "j" del helicopter "i"
                auxlist2 = (LinkedList) auxlist.get(j);

                for (int k = 0; k < auxlist2.size(); k++) {
                    desti    = (Integer) auxlist2.get(k);
                    auxtemps = ((dist[origen][desti] / 100) * 60) + auxtemps + 5;
                    origen = desti;
                }

                // Sumem el temps fins a la base
                desti    = new Integer(ng);
                auxtemps = ((dist[origen][desti] / 100) * 60) + auxtemps + 10;

                // System.out.println("Temps acumulat entre "+origen+" i "+desti+" és "+auxtemps);
                origen = desti;
            }

            tempstotal += auxtemps;
            tempsmaxim = java.lang.Math.max(auxtemps, tempsmaxim);
            auxtemps   = 0;
        }

        return tempstotal + (tempsmaxim * nh);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
