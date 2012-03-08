//~--- non-JDK imports --------------------------------------------------------

import aima.search.framework.HeuristicFunction;

//~--- JDK imports ------------------------------------------------------------

import java.util.LinkedList;

/**
 * @author Cristian Planas | Pere Joan Martorell
 *
 */

public class RescatHeuristicFunction2 implements HeuristicFunction {
    public double getHeuristicValue(Object state) {
        RescatBoard board = (RescatBoard) state;
        boolean     prio;

        prio = false;

        int                               nh           = board.getnh();
        int                               ng           = board.getngrups();
        double                            tempsmaxim   = 0;
        double                            tempstotal   = 0;
        double                            auxtemps     = 0;
        double                            auxtemps2    = 0;
        double                            auxtemps3    = 0;
        double                            auxtempsreal = 0;
        double[][]                        dist         = board.getdistancies();
        int                               n            = 0;
        Integer                           origen       = new Integer(ng);
        int                               desti        = 0;
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

            for (int j = n - 1; j >= 0; j--) {

                // Agafem una sortida "j" del helicopter "i"
                auxlist2 = (LinkedList) auxlist.get(j);

                for (int k = 0; k < auxlist2.size(); k++) {
                    desti = (Integer) auxlist2.get(k);

                    if (board.esPrio(desti)) {
                        prio = true;
                    }

                    if (prio = true) {
                        auxtemps2 = ((dist[origen][desti] / 100) * 60) + auxtemps2 + 5;
                    } else {
                        auxtemps3 = ((dist[origen][desti] / 100) * 60) + auxtemps2 + 5;
                    }

                    origen = desti;
                }

                // Sumem el temps fins a la base
                desti = new Integer(ng);

                if (prio = true) {
                    auxtemps2 = ((dist[origen][desti] / 100) * 60) + 10;
                } else {
                    auxtemps3 = ((dist[origen][desti] / 100) * 60) + 10;
                }

                auxtempsreal = auxtempsreal + auxtemps2 + auxtemps3;
                auxtemps2    = auxtemps2 * 3;
                auxtemps     = auxtemps2 + auxtemps + auxtemps3;
                auxtemps2    = 0;
                auxtemps3    = 0;
                prio         = false;

                origen = desti;
            }

            tempsmaxim   = java.lang.Math.max(auxtempsreal, tempsmaxim);
            auxtempsreal = 0;
            tempstotal   += auxtemps;
            auxtemps     = 0;
            auxtempsreal = 0;
        }

        return (tempstotal) + tempsmaxim * nh;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
