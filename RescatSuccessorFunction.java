//~--- non-JDK imports --------------------------------------------------------

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Pere Joan Martorell, Cristian Planas
 *
 */
public class RescatSuccessorFunction implements SuccessorFunction {
    public List getSuccessors(Object aState) {
        ArrayList                retVal = new ArrayList();
        RescatBoard              board  = (RescatBoard) aState;
        RescatHeuristicFunction1 RHF    = new RescatHeuristicFunction1();
        int                      nh     = board.getnh();
        int                      ng     = board.getngrups();
        boolean                  comput;
        int                      hds  = 0;
        int                      sds  = 0;
        int                      saux = 0;

        LinkedList<LinkedList<Integer>>[] sortides = board.copiaS();
        LinkedList<Integer>[]             persones = board.copiaP();
        LinkedList<Integer>               auxlist;
        LinkedList<Integer>               auxlist2;
        LinkedList<LinkedList<Integer>>[] auxsortides = new LinkedList[nh];

        for (int ho = 0; ho < nh; ho++) {
            for (int so = 0; so < sortides[ho].size(); so++) {
                auxlist2 = sortides[ho].get(so);

                for (int go = 0; go < auxlist2.size(); go++) {
                    for (int hd = 0; hd < nh; hd++) {
                        for (int sd = 0; sd < sortides[hd].size(); sd++) {
                            auxlist = sortides[hd].get(sd);
                            comput  = board.computPersones(ho, so, go, hd, sd);

                            if (((auxlist.size() < 3) && comput) || ((so == sd) && (ho == hd))) {
                                int gd = 0;

                                RescatBoard newBoard = new RescatBoard(ng, nh, sortides, board.getgrups(),
                                                           board.getdistancies(), persones);

                                newBoard.move(ho, so, go, hd, sd, gd);

                                double v = RHF.getHeuristicValue(newBoard);
                                String S = new String("Moviment del grup(" + go + ") de la sortida(" + so + ") del h("
                                                      + ho + ") al grup (" + gd + ") de la sortida(" + sd + ") del h("
                                                      + hd + ") | Coste(" + v + ")");

                                S += "\n" + newBoard.toStringSortides();
                                S += newBoard.toStringPersones();
                                retVal.add(new Successor(S, newBoard));
                                sortides = board.copiaS();
                                persones = board.copiaP();
                            }

                            if (sd + 1 == sortides[hd].size()) {
                                int gd = 0;

                                RescatBoard newBoard = new RescatBoard(ng, nh, sortides, board.getgrups(),
                                                           board.getdistancies(), persones);

                                newBoard.move(ho, so, go, hd, sd + 1, gd);

                                double v = RHF.getHeuristicValue(newBoard);
                                String S = new String("Moviment del grup(" + go + ") de la sortida(" + so + ") del h("
                                                      + ho + ") al grup(" + gd + ") de la sortida(" + sd + 1
                                                      + ") del h(" + hd + ") | Coste(" + v + ")");

                                S += "\n" + newBoard.toStringSortides();
                                S += newBoard.toStringPersones();
                                retVal.add(new Successor(S, newBoard));
                                sortides = board.copiaS();
                                persones = board.copiaP();
                            }
                        }
                    }
                }
            }
        }

        for (int ho = 0; ho < nh; ho++) {
            for (int so = 0; so < sortides[ho].size(); so++) {
                auxlist2 = sortides[ho].get(so);
                for (int go = 0; go < auxlist2.size(); go++) {
                    for (int hd = ho; hd < nh; hd++) {
                        hds = hd;
                        if (hd == ho) {
                            saux = so;
                        } else {
                            saux = 0;
                        }
                        for (int sd = saux; sd < sortides[hd].size(); sd++) {
                            sds     = sd;
                            auxlist = sortides[hd].get(sd);
                            for (int gd = 0; gd < auxlist.size(); gd++) {
                                comput = board.computSwap(ho, so, go, hds, sds, gd);

                                if (comput) {
                                    RescatBoard newBoard = new RescatBoard(ng, nh, sortides, board.getgrups(),
                                                               board.getdistancies(), persones);

                                    newBoard.swap(ho, so, go, hds, sds, gd);

                                    double v = RHF.getHeuristicValue(newBoard);
                                    String S = new String("Intercanvi del grup(" + go + ") de la sortida(" + so
                                                          + ") del h(" + ho + ") amb el grup(" + gd
                                                          + ") de la sortida(" + sds + ") del h(" + hds + ") | Coste("
                                                          + v + ")");

                                    S += "\n" + newBoard.toStringSortides();
                                    S += newBoard.toStringPersones();
                                    retVal.add(new Successor(S, newBoard));

                                    sortides = board.copiaS();
                                    persones = board.copiaP();
                                }
                            }
                        }
                    }
                }
            }
        }

        return retVal;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
