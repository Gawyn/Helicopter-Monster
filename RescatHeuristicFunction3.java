//~--- non-JDK imports --------------------------------------------------------

import aima.search.framework.HeuristicFunction;

//~--- JDK imports ------------------------------------------------------------

import java.util.LinkedList;

/**
 * @author Cristian Planas | Pere Joan Martorell
 *
 */

public class RescatHeuristicFunction3 implements HeuristicFunction {
    public double getHeuristicValue(Object state) {
        int         p     = 0;
        RescatBoard board = (RescatBoard) state;
        boolean     prio;

        prio = false;

        int                               nh         = board.getnh();
        int                               ng         = board.getngrups();
        double                            tempsprio1 = 0;
        double                            tempsprio2 = 0;
        double                            auxtemps   = 0;
        double                            auxtemps2  = 0;
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
                    desti = (Integer) auxlist2.get(k);

                    if (board.esPrio(desti)) {
                        prio = true;
                    }

                    auxtemps2 = ((dist[origen][desti] / 100) * 60) + auxtemps2 + 5;
                    origen = desti;
                }

                // Sumem el temps fins a la base
                desti     = new Integer(ng);
                auxtemps2 = ((dist[origen][desti] / 100) * 60) + 10;

                if (prio) {
                    tempsprio1 += auxtemps2;
                } else {
                    tempsprio2 += auxtemps2;
                }

                auxtemps2 = 0;
                prio      = false;
                origen = desti;
            }
        }

        // System.out.println("-------------------------");
        return (p * tempsprio1) + ((100 - p) * tempsprio2);
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
