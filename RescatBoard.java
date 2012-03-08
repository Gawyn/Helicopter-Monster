/*!
 * Representació de l'estat del problema de Rescat de grups
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

/**
 * @author Cristian Planas | Pere Joan Martorell
 *
 */

public class RescatBoard {

  private static final int area = 50;
  private static int hcap = 15;
  private static final int hspeed = 100;
  private static final int hmaxgroups = 3;
  private static final int load_time = 5;
  private static final int base_time = 10;

  public class ginfo {
    boolean prio;
    int npers;
    int pos_x;
    int pos_y;
    int id;
    public ginfo(boolean p, int np, int x, int y, int ident) {
      id = ident;
      prio = p;
      npers = np;
      pos_x=x;
      pos_y=y;
    }
    public int getid(){
        return id;
    }
  }
  /// Nombre de grups a rescatar
  private int ngroups;
  /// Nombre d'helicopters de rescat
  private int nhelic;
  
  /// Array de grups amb camps <prioritat, numpersones>
  private static ginfo [] grups;
  /// Array de sortides dels helicopters
  private LinkedList<LinkedList<Integer>> [] sortides;
  /// Distancias entre grups inclosa la base
  private static double [][] dist;
  private LinkedList<Integer>[] persones;     //Llista de nombres de persones per cada sortida dels H

  /*!\brief Genera una instancia del problema de Rescat
   *
   * Crea una nova instancia del problema de rescat de grups amb ng grups i nh helicopters
   * 
   * @param [in] ng Nombre de grups
   * @param [in] nh Nombre d'helicopters
   */
  public RescatBoard(int ng, int nh) {
    Random myRandom=new Random();
    int d;
    persones = new LinkedList[nh];
    sortides = new LinkedList[nh];
    for(int i=0; i<nh; i++){
        sortides[i] = new LinkedList<LinkedList<Integer>>();
    }

    ngroups = ng;
    nhelic=nh;
    dist= new double[ng+1][ng+1];
    grups = new ginfo [ng+1];
    int impx=0;
    int impy=0;
    //Això és per generar grups aleatoris.
    for (int i=0; i<ng; i++){
        boolean b=false;
        int aux = myRandom.nextInt(3);
        if(aux==2) b=true;
        impx=myRandom.nextInt(50);
        impy=myRandom.nextInt(50);
        /*
        System.out.print("Grup:"+i+"=>"+impx+","+impy);
        System.out.println();
        */
        grups[i]=new ginfo(b,myRandom.nextInt(11)+1,impx,impy,i);
    }
    grups[ng]=new ginfo(false,0,0,0,ng);
    //Aquest bucle serveix pel càlcul de distàncies.
    for (int i=0; i<ng+1; i++){
        for (int j=0; j<ng+1; j++){
         if(i==j) dist[i][j]=Double.MAX_VALUE;
         else{
             double aux_x=grups[i].pos_x-grups[j].pos_x;
             double aux_y=grups[i].pos_y-grups[j].pos_y;
             dist[i][j]=java.lang.Math.hypot(aux_x, aux_y);
             /*
             System.out.print("Distancia de:"+i+" a "+j+" és de "+dist[i][j]);
             System.out.println();
              */
         }
        }
    }
    //SolucioAleatoria();
    //SolucioProximitat();
    SolucioBEST();
    comptaPersones();
    //System.out.print(toStringPersones());
    //ShowSortides();
  }


  public RescatBoard(int ng, int nh, LinkedList<LinkedList<Integer>>[] s, ginfo[] g, double d[][], LinkedList<Integer>[] p) {
    sortides = s;
    ngroups = ng;
    nhelic=nh;
    dist= d;
    grups = g;
    persones = p;
  }

  public void ShowSortides(){
      double printdist;
      Integer origen= new Integer(ngroups);
      Integer desti;
      printdist=0;
      for(int i=0;i<nhelic;i++){
          LinkedList auxlist = sortides[i];
          System.out.print("Helic."+i+":");
          for(int j=0; j<auxlist.size(); j++){
              //S'ha de mirar que aquesta llista tingui algun element
              
              LinkedList auxlist2 = (LinkedList) auxlist.get(j);
              for(int k=0; k<auxlist2.size();k++){
                  desti=(Integer)auxlist2.get(k);
                  printdist=printdist+dist[origen][desti];
                  origen=desti;
                  System.out.print(" => "+auxlist2.get(k)+"("+grups[(Integer)auxlist2.get(k)].npers+")");
              }
              desti= new Integer(ngroups);
              System.out.print(" => Base");
              printdist=printdist+dist[origen][desti];
              origen=desti;
          }
          
          System.out.println();
           
          printdist=0;
      }
  }

  public String toStringPersones(){
    String out = "";
    for(int i=0; i<nhelic; i++){
      LinkedList s = persones[i];
      out += "Sortides H["+i+"]: ";
      for(int j=0; j<s.size(); j++){
        out += s.get(j)+";";
      }
      out += "\n";
    }
    return out;
  }
  private void SolucioAleatoria(){
    int[] numsortida=new int[nhelic];
      for(int i=0;i<nhelic;i++){
          numsortida[i]=1;
      }
      int[] personeshel=new int[nhelic];
      int[] grupshel=new int[nhelic];
      for(int i=0;i<nhelic;i++){
          personeshel[i]=0;
      }
      Random myRandom=new Random();
      int auxng=ngroups;
      Integer auxint;
      int random; //Random serà el grup escollit a l'atzar per ser recollit
      int randomhel;
      ArrayList<ginfo> copiagrups;
      copiagrups = new ArrayList<ginfo>(Arrays.asList(grups));
      while(auxng>0){
          random=myRandom.nextInt(auxng);
          randomhel=myRandom.nextInt(nhelic);
          if(copiagrups.get(random).npers>(hcap-personeshel[randomhel]) || grupshel[randomhel]==3){
                personeshel[randomhel]=0;
                grupshel[randomhel]=0;
                numsortida[randomhel]++;
          }
          else{
            if(sortides[randomhel].size()<numsortida[randomhel]){
                sortides[randomhel].add(new LinkedList());
            }
            auxint = new Integer(copiagrups.get(random).id);
            sortides[randomhel].getLast().add(auxint);
            personeshel[randomhel]=personeshel[randomhel]+copiagrups.get(random).npers;
            copiagrups.remove(random);
            auxng--;
            grupshel[randomhel]++;
          }
      }
  }

  private void SolucioProximitat(){
      boolean[] visitats=new boolean[ngroups];
      for(int i=0;i<ngroups;i++){
        visitats[i]=false;
      }
      int[] numsortida=new int[nhelic];
      for(int i=0;i<nhelic;i++){
          numsortida[i]=1;
      }
      int[] posiciohel=new int[nhelic];
      for(int i=0;i<nhelic;i++){
          posiciohel[i]=ngroups;
      }
      int[] personeshel=new int[nhelic];
      int[] grupshel=new int[nhelic];
      for(int i=0;i<nhelic;i++){
          personeshel[i]=0;
      }
      double auxdist= Double.MAX_VALUE;
      int auxng=ngroups;
      Integer auxint;
      int grupproper=0; //Grupproper serà el grup més proper a l'actual helicopter.
      int hel=0;
      while(auxng>0){
          //Busca el grup mes proper a l'actual helicopter
          auxdist=Double.MAX_VALUE;
          for(int i=0;i<ngroups;i++){
              if(dist[posiciohel[hel]][i]<auxdist && visitats[i]==false){
                  auxdist=dist[posiciohel[hel]][i];
                  grupproper=i;
              }
          }
          posiciohel[hel]=grupproper;
          //Comprova restricció de hcap pers màxim i si és així tornem a la base
          if(grups[grupproper].npers>(hcap-personeshel[hel]) || grupshel[hel]==3){
                personeshel[hel]=0;
                grupshel[hel]=0;
                posiciohel[hel]=ngroups;
                numsortida[hel]++;
          }
          else{
            if(sortides[hel].size()<numsortida[hel]){
                sortides[hel].add(new LinkedList());
            }
            auxint = new Integer(grupproper);
            sortides[hel].getLast().add(auxint);
            personeshel[hel]=personeshel[hel]+grups[grupproper].npers;
            visitats[grupproper]=true;
            auxng--;
            grupshel[hel]++;
            //Comprovem la restricció de 3 grups màxim per helicopter
          }
          //El pròxim viatge l'assignarem a un altre helicopter
          hel++;
          hel = hel % nhelic;

      }
  }

    private void SolucioBEST(){
      boolean[] visitats=new boolean[ngroups];
      for(int i=0;i<ngroups;i++){
        visitats[i]=false;
      }
      int[] numsortida=new int[nhelic];
      for(int i=0;i<nhelic;i++){
          numsortida[i]=1;
      }
      double[] tempshel=new double[nhelic];
      for(int i=0;i<nhelic;i++){
          tempshel[i]=0;
      }
      int[] posiciohel=new int[nhelic];
      for(int i=0;i<nhelic;i++){
          posiciohel[i]=ngroups;
      }
      int[] personeshel=new int[nhelic];
      int[] grupshel=new int[nhelic];
      for(int i=0;i<nhelic;i++){
          personeshel[i]=0;
      }
      double auxdist= Double.MAX_VALUE;
      int auxng=ngroups;
      Integer auxint;
      int grupproper=0; //Grupproper serà el grup més proper a l'actual helicopter.
      int hel=0;
      while(auxng>0){
          grupproper=-1;
          //Busca el grup mes proper a l'actual helicopter
          auxdist=Double.MAX_VALUE;
              for(int i=0;i<ngroups;i++){
                  if(dist[posiciohel[hel]][i]<auxdist && visitats[i]==false && grups[i].npers<=(hcap-personeshel[hel])){
                      //System.out.println("Agafat");
                      auxdist=dist[posiciohel[hel]][i];
                      grupproper=i;
                  }
              }
          //Si no hi ha cap grup al qual poguem anar a buscar, tornem a la base
          if(grupproper==-1){
                tempshel[hel]+=(dist[ngroups][posiciohel[hel]]/100)+10;
                personeshel[hel]=0;
                grupshel[hel]=0;
                posiciohel[hel]=ngroups;
                numsortida[hel]++;
          }
          //Comprova restricció de hcap pers màxim i si és així tornem a la base
          else{
            if(sortides[hel].size()<numsortida[hel]){
                sortides[hel].add(new LinkedList());
            }
            tempshel[hel]+=(dist[ngroups][posiciohel[hel]]/100)+5;
            auxint = new Integer(grupproper);
            sortides[hel].getLast().add(auxint);
            personeshel[hel]=personeshel[hel]+grups[grupproper].npers;
            visitats[grupproper]=true;
            auxng--;
            grupshel[hel]++;
            posiciohel[hel]=grupproper;
            if(grupshel[hel]==3){
                tempshel[hel]+=(dist[ngroups][posiciohel[hel]]/100)+10;
                personeshel[hel]=0;
                grupshel[hel]=0;
                posiciohel[hel]=ngroups;
                numsortida[hel]++;
            }
            //Comprovem la restricció de 3 grups màxim per helicopter
          }
          //El pròxim viatge l'assignarem a un altre helicopter
          hel=0;
          for(int i=1;i<nhelic;i++){
            if(tempshel[i]<tempshel[hel])hel=i;
          }
      }
  }
  
  public int getnh(){
      return nhelic;
  }
    public int getngrups(){
      return ngroups;
  }
  public double[][] getdistancies(){
      return dist;
  }
  public LinkedList<LinkedList<Integer>> [] getsortides(){
      return sortides;
  }

  public ginfo [] getgrups(){return(grups);}

  public String toStringSortides()
  {
      String out = "";
      double printdist;
      Integer origen= new Integer(ngroups);
      Integer desti;
      printdist=0;
      for(int i=0;i<nhelic;i++){
          LinkedList auxlist = sortides[i];
          out += "Helic."+i+":";
          for(int j=0; j<auxlist.size(); j++){
              //S'ha de mirar que aquesta llista tingui algun element

              LinkedList auxlist2 = (LinkedList) auxlist.get(j);
              for(int k=0; k<auxlist2.size();k++){
                  desti=(Integer)auxlist2.get(k);
                  printdist=printdist+dist[origen][desti];
                  origen=desti;
                  out += " => "+auxlist2.get(k)+"("+grups[(Integer)auxlist2.get(k)].npers+")";
              }
              desti= new Integer(ngroups);
              out += " => Base";
              printdist=printdist+dist[origen][desti];
              origen=desti;
          }
          out += "\n";

          printdist=0;
      }
      return out;
  }

  private void comptaPersones(){
      Integer origen= new Integer(ngroups);
      Integer desti;
      int p = 0;
      for(int i=0;i<nhelic;i++){
          LinkedList auxlist = sortides[i]; //Llista de sortides de l'h [i]
          persones[i] = new LinkedList<Integer>();
          for(int j=0; j<auxlist.size(); j++){
              //S'ha de mirar que aquesta llista tingui algun element
              LinkedList auxlist2 = (LinkedList) auxlist.get(j); //Sortida [j] de l'h [i]
              for(int k=0; k<auxlist2.size();k++){  //Recorregut per la sortida [j]
                  desti=(Integer)auxlist2.get(k); //Grup [k] de la sortida [j]
                  origen=desti;
                  p += grups[(Integer)auxlist2.get(k)].npers;
              }
              persones[i].add(new Integer(p));
              p = 0;
              desti= new Integer(ngroups);
              origen=desti;
          }
      }
  }

  public LinkedList<LinkedList<Integer>> [] copiaS()
  {

    LinkedList<LinkedList<Integer>>[] copiallista = new LinkedList[nhelic];
      for(int i=0; i<nhelic; i++)
      {
        LinkedList<LinkedList<Integer>> auxsortides1 = sortides[i]; //auxsortides1 té una llista de sortides
        copiallista[i] = new LinkedList();
        for(int j=0; j<auxsortides1.size(); j++)
        {
          int as1 = auxsortides1.get(j).size();
          copiallista[i].add(new LinkedList());
          for(int k=0; k<as1; k++)
          {
             Integer grupaux = new Integer(auxsortides1.get(j).get(k)); //grupaux conté el grup k de la sortida j
             copiallista[i].get(j).add(grupaux);  //Afegim un grup a la sortida j del helicopter i
          }
        }
      }
      return copiallista;
    }

    public LinkedList<Integer>[] copiaP()
    {

      LinkedList<Integer>[] copiallista = new LinkedList[nhelic];
      for(int i=0; i<nhelic; i++)
      {
        LinkedList<Integer> auxpers1 = persones[i]; //auxpers1 té una llista de "persones per sortida"
        copiallista[i] = new LinkedList();
        for(int j=0; j<auxpers1.size(); j++)
        {
          copiallista[i].add(auxpers1.get(j));
        }
      }
      return copiallista;
    }


    public void move(int nh1, int s1, int g1, int nh2, int s2, int g2){
        int actualp;
        LinkedList<Integer> auxlist1 = sortides[nh1].get(s1);   //auxlist1 conté la sortida origen [s1]
        int aux1 = auxlist1.get(g1);   //aux1 conté el grup [g1] de la sortida origen
        if(nh1==nh2 && s1==s2) g2++;
        if(sortides[nh2].size()<=s2){  //Cas en que creem una sortida nova
            sortides[nh2].add(new LinkedList<Integer>());
            if(s1!=s2 || nh1!=nh2) persones[nh2].add(new Integer(grups[aux1].npers));
        }
        else if(s1!=s2 || nh1!=nh2) //Si estem dintre la mateixa sortida no cal actualitzar el comptador de persones
        { //Cas en que estem en una sortida ja existent
          //Actualitzem el comptador de persones de la sortida desti
          actualp = persones[nh2].get(s2).intValue();
          actualp += grups[aux1].npers;
          persones[nh2].set(s2, new Integer(actualp));  //Actualitzem el nombre de persones per la sortida [s2]
        }

        sortides[nh2].get(s2).add(g2, aux1);  //Afegim el grup a la posicio destí

        if(s1!=s2 || nh1!=nh2){
          //Actualitzem el comptador de persones de la sortida origen
          actualp = persones[nh1].get(s1).intValue();
          actualp -= grups[aux1].npers;
          persones[nh1].set(s1, new Integer(actualp));
        }
      

        sortides[nh1].get(s1).remove(g1);
        if(sortides[nh1].get(s1).size()==0){
            sortides[nh1].remove(s1);
            persones[nh1].remove(s1); //Esborrem el comptador que fa referencia a la sortida [s1]
        }
    }

   public void swap(int nh1, int s1, int g1, int nh2, int s2, int g2){
        LinkedList<Integer> auxlist = sortides[nh1].get(s1);
        int aux1 = auxlist.get(g1);
        LinkedList<Integer> auxlist2 = sortides[nh2].get(s2);
        int aux2 = auxlist2.get(g2);
        int grup1 = grups[aux1].npers;
        int grup2 = grups[aux2].npers;
        sortides[nh1].get(s1).set(g1, aux2);
        sortides[nh2].get(s2).set(g2, aux1);
        int pers1 = persones[nh1].get(s1)-grup1+grup2; //Comptem el nou nombre de persones a l'origen
        persones[nh1].set(s1,new Integer(pers1));      //Actualitzem el valor de l'origen
        int pers2 = persones[nh2].get(s2)+grup1-grup2; //Comptem el nou nombre de persones al destí
        persones[nh2].set(s2,new Integer(pers2));      //Idem anterior

    }

    public boolean computPersones(int ho, int so, int go, int hd, int sd){
      LinkedList<Integer> auxlist1 = sortides[ho].get(so);   //auxlist1 conté la sortida origen [so]
      int aux1 = auxlist1.get(go);   //aux1 conté el grup [go] de la sortida origen
      boolean b=true;
      int parcial1 = grups[aux1].npers;
      int parcial2 = 0;
      if(sd < persones[hd].size()){
        parcial2 = persones[hd].get(sd);
      }
      if(ho==hd && so==sd)return false;
      else return (parcial1 + parcial2) <= hcap;
    }

        public boolean computSwap(int ho, int so, int go, int hd, int sd, int gd){
          LinkedList<Integer> auxlist1 = sortides[ho].get(so);   //auxlist1 conté la sortida origen [so]
          LinkedList<Integer> auxlist2 = sortides[hd].get(sd);
          int aux1 = auxlist1.get(go);   //aux1 conté el grup [go] de la sortida origen
          int aux2 = auxlist2.get(gd);
          int grup1 = grups[aux1].npers;
          int grup2 = grups[aux2].npers;

          int total1=persones[ho].get(so)-grup1+grup2;
          int total2=persones[hd].get(sd)+grup1-grup2;
          if(total1<=hcap && total2<=hcap) return true;
          else return false;
        }
        public boolean esPrio(int x){
            return grups[x].prio;
        }

  public double getTempsDarrer() {
       int ng=ngroups;
       int nh=nhelic;
       double tempsmaxim=0;
       double auxtemps=0;
       int n=0;
       Integer origen= new Integer(ng);
       int desti=0;
       LinkedList auxlist;
       LinkedList auxlist2;
       for (int i=0;i<nh;i++){
           auxlist= sortides[i];
           n=sortides[i].size();
           origen=ng;
           //System.out.println("-------------------");
           //System.out.println(" Sortides Helic. "+i);
           //System.out.println("-------------------");

           for(int j=0;j<n;j++){
               //Agafem una sortida "j" del helicopter "i"
               auxlist2 = (LinkedList) auxlist.get(j);
               for (int k=0;k<auxlist2.size();k++){
                    desti=(Integer)auxlist2.get(k);
                    auxtemps=((dist[origen][desti]/100)*60)+auxtemps+5;

                    origen=desti;
               }
               //Sumem el temps fins a la base
               desti= new Integer(ng);
               auxtemps=((dist[origen][desti]/100)*60)+auxtemps+10;
               origen = desti;
           }

           tempsmaxim = java.lang.Math.max(auxtemps,tempsmaxim);
           auxtemps=0;
       }
       //System.out.println("-------------------------");
       return tempsmaxim;
      }

    public double getTempsTotal() {
       int ng=ngroups;
       int nh=nhelic;
       double tempstotal=0;
       double auxtemps=0;
       int n=0;
       Integer origen= new Integer(ng);
       int desti=0;
       LinkedList auxlist;
       LinkedList auxlist2;
       for (int i=0;i<nh;i++){
           auxlist= sortides[i];
           n=sortides[i].size();
           origen=ng;
           //System.out.println("-------------------");
           //System.out.println(" Sortides Helic. "+i);
           //System.out.println("-------------------");

           for(int j=0;j<n;j++){
               //Agafem una sortida "j" del helicopter "i"
               auxlist2 = (LinkedList) auxlist.get(j);
               for (int k=0;k<auxlist2.size();k++){
                    desti=(Integer)auxlist2.get(k);
                    auxtemps=((dist[origen][desti]/100)*60)+auxtemps+5;
        
                    origen=desti;
               }
               //Sumem el temps fins a la base
               desti= new Integer(ng);
               auxtemps=((dist[origen][desti]/100)*60)+auxtemps+10;
               origen = desti;
           }

           tempstotal +=auxtemps;
           auxtemps=0;
       }
       return tempstotal;
      }
}
