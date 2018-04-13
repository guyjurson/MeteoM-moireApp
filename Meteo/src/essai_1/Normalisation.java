package essai_1;

import java.io.*;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;

/**
 * Created by Charles on 13/04/2018.
 */
public class Normalisation {

    public static void main(String[] args) throws IOException {
       normaliserVariable("inconnu_16");
       normaliserVariable("marseille_1416");
       normaliserVariable("nancy_1416");
       normaliserVariable("All");
    }

    public static void normaliserVariable(String file) throws IOException {
        File f = new File(file+".data.txt");
        File f2 = new File(file+"Normalized.data.txt");

        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        String [] res=null;
        ArrayList<Double> l1 = new ArrayList<Double>();
        ArrayList<Double> l2 = new ArrayList<Double>();
        ArrayList<Double> l3 = new ArrayList<Double>();
        ArrayList<Double> l4 = new ArrayList<Double>();

        while (line != null)
        {
            //System.out.println (line);
            String [] tab = line.split(",");
            //System.out.println (tab[0]);
            if (Double.isNaN(Double.parseDouble(tab[0])) || Double.isNaN(Double.parseDouble(tab[1])) || Double.isNaN(Double.parseDouble(tab[2])) || Double.isNaN(Double.parseDouble(tab[3])) ){
            }else{
                //System.out.println(tab[0]+" // "+tab[1]+" // "+tab[2]+" // "+tab[3]);
                l1.add(Double.parseDouble(tab[0]));
                l2.add(Double.parseDouble(tab[1]));
                l3.add(Double.parseDouble(tab[2]));
                l4.add(Double.parseDouble(tab[3]));
            }

            line = br.readLine();
        }
        br.close();
        fr.close();

        double moy1 = faireMoyenne(l1);
        double moy2 = faireMoyenne(l2);
        double moy3 = faireMoyenne(l3);
        double moy4 = faireMoyenne(l4);

        double ec1 = faireEcartType(l1,moy1);
        double ec2 = faireEcartType(l2,moy2);
        double ec3 = faireEcartType(l3,moy3);
        double ec4 = faireEcartType(l4,moy4);
        FileWriter fw = new FileWriter (f2);
        fr = new FileReader(f);
        br = new BufferedReader(fr);
        line = br.readLine();
        while (line != null)
        {
            //System.out.println (line);
            String [] tab = line.split(",");
            if (Double.isNaN(Double.parseDouble(tab[0])) || Double.isNaN(Double.parseDouble(tab[1])) || Double.isNaN(Double.parseDouble(tab[2])) || Double.isNaN(Double.parseDouble(tab[3])) ){
            }else {
                double val1 = (Double.parseDouble(tab[0]) - moy1) / ec1;
                double val2 = (Double.parseDouble(tab[1]) - moy2) / ec2;
                double val3 = (Double.parseDouble(tab[2]) - moy3) / ec3;
                double val4 = (Double.parseDouble(tab[3]) - moy4) / ec4;
                String st = val1 + "," + val2 + "," + val3 + "," + val4 + "\n";
                fw.write(st);
            }
            line = br.readLine();
        }
        fw.close();
        br.close();
        fr.close();


    }


    public static double faireMoyenne(ArrayList<Double> l){
        double total = 0;
        for (int i = 0; i<l.size();i++){
            total+=l.get(i);
        }
        return total/l.size();
    }

    public static double faireEcartType(ArrayList<Double> l, double m){
        double variance =0;
        for (int i = 0; i<l.size();i++){
            variance+=Math.pow(l.get(i)-m, 2);
        }
        variance=variance/l.size();
        double res =Math.sqrt(variance);
        return res;
    }











}
