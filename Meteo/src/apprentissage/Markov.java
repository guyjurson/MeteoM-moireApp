package apprentissage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Markov {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//Lance l'apprentissage pour les deux villes
		double nancy[][]= apprentissage("nancy");
		double marseille[][]= apprentissage("marseille");
		//Test la proba de chacune
		double probNancy=test(nancy);
		double probMars= test(marseille);
		//Donne la ville avec la plus grosse proba
		if( probNancy < probMars){
			System.out.println("Les données inconnues sont probablement de Marseille");
		} else {
			System.out.println("Les données inconnues sont probablement de Nancy");
		}
	}
	
	public static double[][] apprentissage(String ville) throws FileNotFoundException{
		//Matrice qui contient le nombre de transition entre 2 jours
		//Matrice[Jour1][Jour2]
		//La dimension correspond aux nombre de type de jour (donc au nombre de neurone)
		double matrice [][]= new double[Kohonen.NbNoe][Kohonen.NbNoe+1];
		//On initialise la matrice à 0
		for( int i = 0; i< Kohonen.NbNoe; ++i){
			for( int j = 0; j<Kohonen.NbNoe; ++j){
			      matrice[i][j]=0;
			}
		}
		//On ouvre le fichier qui contient les types de jours
		Scanner scan = new Scanner(new File(Kohonen.chemin+ville+".txt"));
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
		for( int i = 0; i< Kohonen.NbNoe; ++i){
			comptCol=0;
			for( int j = 0; j<Kohonen.NbNoe; ++j){
			      comptCol+=matrice[i][j];
			}
			//La dernière colonne contient le nombre de fois où le type est présent
			//On utilise le Laplacian smoothing pour ne pas avoir de proba de 0
			matrice[i][Kohonen.NbNoe]=(comptCol+1)/(compt+Kohonen.NbNoe);
			for( int j = 0; j<Kohonen.NbNoe; ++j){
			      matrice[i][j]=(matrice[i][j]+1)/(comptCol+Kohonen.NbNoe);
			}
		}
		scan.close();
		return matrice;
	}
	
	public static double test(double[][] ville) throws FileNotFoundException{
		//On ouvre le fichier avec les données inconnues
		Scanner scan = new Scanner(new File(Kohonen.chemin+"inconnu.txt"));
		//Contiendra la proba 
		double proba=1;
		int jPrec= scan.nextInt();
		proba*=ville[jPrec][Kohonen.NbNoe];
		int jAct;
		//On calcul 
		while (scan.hasNextInt()){
			jAct=scan.nextInt();
			proba*=ville[jPrec][jAct];
			jPrec=jAct;
			proba*=ville[jPrec][Kohonen.NbNoe];
		}
		scan.close();
		return proba;
	}

}
