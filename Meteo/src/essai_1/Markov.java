package essai_1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Markov {
	//Nombre de type de jour
	final static int type=10;
	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		double nancy[][]= apprentissage("nancy");
		double marseille[][]= apprentissage("marseille");
		double probNancy=test(nancy);
		double probMars= test(marseille);
		if( probNancy < probMars){
			System.out.println("Les données inconnues sont probablement de Marseille");
		} else {
			System.out.println("Les données inconnues sont probablement de Nancy");
		}
	}
	
	public static double[][] apprentissage(String ville) throws FileNotFoundException{
		//Matrice qui contient le nombre de transition entre 2 jours
		//Matrice[Jour1][Jour2]
		double matrice [][]= new double[type][type+1];
		//On initialise la matrice à 0
		for( int i = 0; i< type; ++i){
			for( int j = 0; j<type; ++j){
			      matrice[i][j]=0;
			}
		}
		//On ouvre le fichier qui contient les types de jours
		Scanner scan = new Scanner(new File("C:/Users/Guy Ju/Documents/Fac/M1/MémoireApp/"+ville+".txt"));
		int jPrec= scan.nextInt();
		int jAct;
		//Compte le nombre de jour
		int compt= 1;
		//On remplie la matrice avec les successions de jours
		while (scan.hasNextInt()){
			jAct=scan.nextInt();
			matrice[jPrec][jAct]+=1;
			jPrec=jAct;
			compt+=1;
		}
		float comptCol;
		//On transforme les comptes en proba
		for( int i = 0; i< type; ++i){
			comptCol=0;
			for( int j = 0; j<type; ++j){
			      comptCol+=matrice[i][j];
			}
			//La dernière colonne contient le nombre de fois où le type est présent
			//On utilise le Laplacian smoothing pour ne pas avoir de proba de 0
			matrice[i][type]=(comptCol+1)/(compt+type);
			for( int j = 0; j<type; ++j){
			      matrice[i][j]=(matrice[i][j]+1)/(comptCol+type);
			}
		}
		scan.close();
		return matrice;
	}
	
	public static double test(double[][] ville) throws FileNotFoundException{
		//On ouvre le fichier avec les données inconnues
		Scanner scan = new Scanner(new File("C:/Users/Guy Ju/Documents/Fac/M1/MémoireApp/inconnu.txt"));
		//Contiendra la proba 
		double proba=1;
		int jPrec= scan.nextInt();
		proba*=ville[jPrec][type];
		int jAct;
		//On calcul 
		while (scan.hasNextInt()){
			jAct=scan.nextInt();
			proba*=ville[jPrec][jAct];
			jPrec=jAct;
			proba*=ville[jPrec][type];
		}
		scan.close();
		return proba;
	}

}
