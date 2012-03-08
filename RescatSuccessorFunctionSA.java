//~--- non-JDK imports --------------------------------------------------------

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author Cristian Planas | Pere Joan Martorell
 *
 */

public class RescatSuccessorFunctionSA implements SuccessorFunction {
    public List getSuccessors(Object aState) {
        ArrayList                         retVal   = new ArrayList();
        RescatBoard                       board    = (RescatBoard) aState;
        RescatHeuristicFunction1          RHF      = new RescatHeuristicFunction1();
        Random                            myRandom = new Random();
        boolean                           comput;
        boolean                           sortCap = true;
        String                            S;
        int                               h1, s1, g1;
        int                               h2, s2, g2;
        int                               op;
        int                               nh       = board.getnh();
        int                               ng       = board.getngrups();
        LinkedList<LinkedList<Integer>>[] sortides = board.copiaS();
        LinkedList<Integer>[]             persones = board.copiaP();

        // Ens estalviem generar tots els successors escollint un operadors i 2 grups a l'atzar
        op = myRandom.nextInt(2);    // op pren valor 0|1

        do {
            do {
                h1 = myRandom.nextInt(nh);
            } while (sortides[h1].size() == 0);

            s1 = myRandom.nextInt(sortides[h1].size());
            g1 = myRandom.nextInt(sortides[h1].get(s1).size());
            h2 = myRandom.nextInt(nh);

            if (op == 1) {    // Cas Move
                s2 = myRandom.nextInt(sortides[h2].size() + 1);

                if (s2 < sortides[h2].size()) {
                    g2      = myRandom.nextInt(sortides[h2].get(s2).size());
                    sortCap = (sortides[h2].get(s2).size() < 3);
                } else {
                    g2 = 0;
                }

                comput = board.computPersones(h1, s1, g1, h2, s2);
                comput = comput && sortCap;
            }
            else {          // Cas Swap
                do {
                    h2 = myRandom.nextInt(nh);
                } while (sortides[h2].size() == 0);

                s2     = myRandom.nextInt(sortides[h2].size());
                g2     = myRandom.nextInt(sortides[h2].get(s2).size());
                comput = board.computSwap(h1, s1, g1, h2, s2, g2);
            }
        } while (((h1 == h2) && (s1 == s2) && (g1 == g2)) || (!comput));

        /*
         * Ens assegurem que no intercanvien un grup per ell mateix i
         * que no superem el max de pers per sortida ni la restr. de 3
         * grups per sortida (nomes ho comprovem pel cas Move)
         */

        RescatBoard newBoard = new RescatBoard(ng, nh, sortides, board.getgrups(), board.getdistancies(), persones);

        if (op == 1) {
            newBoard.move(h1, s1, g1, h2, s2, g2);

            double v = RHF.getHeuristicValue(newBoard);

            S = new String("Moviment del grup(" + g1 + ") de la sortida(" + s1 + ") del h(" + h1 + ") al grup(" + g2
                           + ") de la sortida(" + s2 + ") del h(" + h2 + ") | Coste(" + v + ")");
        } else {
            newBoard.swap(h1, s1, g1, h2, s2, g2);

            double v = RHF.getHeuristicValue(newBoard);

            S = new String("Intercanvi del grup(" + g1 + ") de la sortida(" + s1 + ") del h(" + h1 + ") amb el grup("
                           + g2 + ") de la sortida(" + s2 + ") del h(" + h2 + ") | Coste(" + v + ")");
        }

        S += "\n" + newBoard.toStringSortides();
        S += newBoard.toStringPersones();
        retVal.add(new Successor(S, newBoard));

        return retVal;
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
