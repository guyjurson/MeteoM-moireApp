package apprentissage;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Kohonen {
	
	//Variables fixées
	
	//Nombre de noeud total
	//Pour avoir une carte carré, il faut que le nombre de noeud soit un carré
	static final int NbNoe=16;
	//Valeur initial du rayon de voisinage
	//Plus il est elevé, plus un neurone entraine de voisin
	static final double R=1.5;
	//Taux d'apprentissage
	//Il diminuera avec le temps
	//Plus il est grand, plus le neurone se rapproche de la donnée
	static double alpha=0.999;
	
	//Nombre d'iteration pour l'apprentissage
	static final int tMax=500*NbNoe;
	//Nombre de données
	//La longueur est fixé pour contenir exactement toutes les données
	static final int nbJ=1463;
	
	//Chemin vers le répertoire des fichiers
	//Les fichiers crées seront mis dans ce repertoire
	static final String chemin = "C:/Users/Guy Ju/Documents/Fac/M1/MémoireApp/";

	//Carte auto-organisatrice
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//Ouvre les fichiers contenant les données de chaque ville
		double[][] donnees=ouvertFichier();
		//On récupère la longueur des fichiers qui se trouve sur la dernière ligne du tableau
		//longF1 est la longueur du fichier 1 (ici Nancy), etc
		int longF1=(int)donnees[nbJ][0];
		int longF2=(int)donnees[nbJ][1];
		
		//Apprend avec les données
		double[][][] carte= apprentissage(donnees);
		
		//Test les données
		test(carte, donnees, longF1, longF2);
		
		//Lance les chaines de markov
		Markov.main(args);
		Markov3Jours.main(args);
	}
	
	//Ouvre tous les fichiers, les concatène et centre-réduit
	public static double[][] ouvertFichier() throws NumberFormatException, IOException{
		//Compte le nombre de données (de jours)
		int i = 1;
		//Somme de chaque colonne
		//Utilisé pour la moyenne et l'écart type
		double[] somme= {0,0,0,0};
		
		//Contient les données
		//La derniere ligne contient la longueur de chaque fichier
		//La première case de la derniere ligne est la longueur du fichier 1 et ainsi de suite
		double[][] donnees = new double[nbJ+1][4];
		//Contiendra chaque ligne
		String chaine;
		//Boolean qui vérifie si sur la ligne contient un NaN
		//Dans le cas la, on ne tient pas compte de la ligne
		boolean ajout;
		
		//On lit Nancy
		try{
			//On ouvre le fichier
			BufferedReader fichier_source = new BufferedReader(new FileReader(chemin+"nancy_1416.data"));
			//Pour chaque ligne
			while((chaine = fichier_source.readLine())!= null){
				//On vérifie si elle contient un NaN
				ajout=true;
				for(int k=0;k<chaine.split(",").length;k++){
					if(chaine.split(",")[k].equals("NaN")){
						ajout=false;
					}
				}
				//Si elle n'en contient pas, on ajoute la ligne au tableau de donnée
				if(ajout){
					//On transforme la ligne qui est en String en double
					double[] enNb =new double[4];
					String[] enLt = chaine.split(",");
					//On transforme en double
					//On ajoute chaque colonne à la somme totale
					for(int k=0;k<4;k++){
						enNb[k]=Double.parseDouble(enLt[k]);
						somme[k]+=enNb[k];
					}
					//On ajoute la ligne au tableau
					donnees[i] = enNb;
					i++;
				}
			}
			fichier_source.close();                 
		}
		catch (FileNotFoundException e){
			System.out.println("Le fichier 1 est introuvable !");
		}
		//On ajoute à la dernière ligne la longueur du fichier
		donnees[nbJ][0]=i;
		
		//Même principe que pour Nancy
		try{
			BufferedReader fichier_source = new BufferedReader(new FileReader(chemin+"marseille_1416.data"));
			while((chaine = fichier_source.readLine())!= null){
				ajout=true;
				for(int k=0;k<chaine.split(",").length;k++){
					if(chaine.split(",")[k].equals("NaN")){
						ajout=false;
					}
				}
				if(ajout){
					double[] enNb =new double[4];
					String[] enLt = chaine.split(",");
					for(int k=0;k<4;k++){
						enNb[k]=Double.parseDouble(enLt[k]);
						somme[k]+=enNb[k];
					}
					donnees[i] = enNb;
					i++;
				}
			}
			fichier_source.close();                 
		}
		catch (FileNotFoundException e){
			System.out.println("Le fichier 2 est introuvable !");
		}
		donnees[nbJ][1]=i;
		
		//Meme principe avec les données inconnues
		try{
			BufferedReader fichier_source = new BufferedReader(new FileReader(chemin+"inconnu_16.data"));
			while((chaine = fichier_source.readLine())!= null){
				ajout=true;
				for(int k=0;k<chaine.split(",").length;k++){
					if(chaine.split(",")[k].equals("NaN")){
						ajout=false;
					}
				}
				if(ajout){
					double[] enNb =new double[4];
					String[] enLt = chaine.split(",");
					for(int k=0;k<4;k++){
						enNb[k]=Double.parseDouble(enLt[k]);
						somme[k]+=enNb[k];
					}
					donnees[i] = enNb;
					i++;
				}
			}
			fichier_source.close();                 
		}
		catch (FileNotFoundException e){
			System.out.println("Le fichier 3 est introuvable !");
		}
		
		//On calcul les moyennes de chaque colonne
		for(int k=0;k<4;k++){
			somme[k]=somme[k]/i;
		}
		
		//Calcul de l'ecart type
		double[] ecartT= {0,0,0,0};
		for(int k=0;k<nbJ;k++){
			for(int j=0;j<4;j++){
				ecartT[j]+= Math.pow(donnees[k][j]-somme[j],2);
			}
		}
		for(int k=0;k<4;k++){
			ecartT[k]=ecartT[k]/i;
			ecartT[k]= Math.sqrt(ecartT[k]);
		}
		
		//Centre-réduit
		for(int k=0;k<nbJ;k++){
			for(int j=0;j<4;j++){
				donnees[k][j]= (donnees[k][j]-somme[j])/ecartT[j];
			}
		}
		//On retourne les données concaténées et centrées reduites
		return donnees;
	}

	//Les neurones apprennent avec les données
	public static double[][][] apprentissage(double[][]donnees){
		//Grille qui contient les neurones
		//Comme c'est un tableau carré, la longueur et la largeur correspondent à la sqrt du nombre total de neurone
		int longueurCot= (int) Math.sqrt(NbNoe);
		//La 3eme dimension correspond aux poids du neurone
		double[][][] grilleNoe= new double[longueurCot][longueurCot][4];
		
		//On crée les neurones aléatoirement en prenant les valeurs des poids entre -1 et 1
		for( int i=0; i<longueurCot; i++){
			for( int j=0; j<longueurCot; j++){
				for( int k=0; k<4; k++){
					//Chaque case prend un nombre aléatoire entre -1 et 1
					grilleNoe[i][j][k]=(Math.random()*2)-1;
				}
			}
		}
		
		//On commence l'apprentissage
		
		//Nombre d'itération
		int t=1;
		double[] test;
		//On boucle
		while(t<tMax){
			//On choisi une donnée au hasard dans le tableau
			int r=(int)(Math.random()*nbJ);
			test= donnees[r];
			
			//On cherche le neurone vainqueur
			
			//On initialise la distance minimale avec une valeur très haute
			double min=500;
			//On retient l'abscisse et l'ordonné du vainqueur
			int xV=-1 , yV=-1;
			//Pour chaque neurone
			for( int i=0; i<longueurCot; i++){
				for( int j=0; j<longueurCot; j++){
					//On calcul la distance euclidienne
					double distance=Math.sqrt(Math.pow(grilleNoe[i][j][0]-test[0],2)
							+Math.pow(grilleNoe[i][j][1]-test[1],2)
							+Math.pow(grilleNoe[i][j][2]-test[2],2)
							+Math.pow(grilleNoe[i][j][3]-test[3],2));
					//Si il est plus proche que le min, on le retient
					if(distance<min){
						min=distance;
						xV=i;
						yV=j;
					}
				}
			}
			
			//Modification des poids
			
			//coefficient de voisinage
			//Diminue avec le temps
			double coefVoi= R* Math.exp(-t/tMax);
			
			//taux d'apprentissage
			//On le laisse très haut pour les 1000 premiers passages
			//Puis il diminue avec le temps
			//définit l'amplitude du déplacement 
			double alph;
			if(t>1000){
				alph= alpha*Math.exp(-t/tMax);
			} else {
				alph=0.999;
			}
			//On calcul la modification du poids pour chaque neurone
			for( int i=0; i<longueurCot; i++){
				for( int j=0; j<longueurCot; j++){
					//Fonction de voisinage
					//en fonction de la distance avec le neurone vainqueur, le neurone en question est + ou - deplacé
					double foncVois= Math.exp(
							-(Math.abs(xV-i)+Math.abs(yV-j))
							/(2*Math.pow(coefVoi, 2)));
					//On modifie chaque poids
					for( int k=0; k<4;k++){
						grilleNoe[i][j][k]= grilleNoe[i][j][k]+
								foncVois*
								alph*
								(test[k]-grilleNoe[i][j][k]);
					}
				}
			}
			t++;
		}
		//On retourne la carte une fois qu'elle a appris
		return grilleNoe;
	}

	//Attribue chaque donnée à un neurone
	public static void test(double[][][] carte, double[][]donnees, int l1, int l2) throws IOException{
		int longueurCot= (int) Math.sqrt(NbNoe);
		//Fichier dans lesquels on ecrit les resultats
		File na=new File(chemin+"nancy.txt");
		na.createNewFile();
		FileWriter naw=new FileWriter(na);
		File ma=new File(chemin+"marseille.txt");
		ma.createNewFile();
		FileWriter maw=new FileWriter(ma);
		File in=new File(chemin+"inconnu.txt"); 
		in.createNewFile();
		FileWriter inw=new FileWriter(in);
		//On cherche le neurone le plus proche
		for(int k=0; k<nbJ;k++){
			//Comme au dessus, on initialise la distance min, l'abscisse et l'ordonné
			double min=500;
			int xV=-1 , yV=-1;
			for( int i=0; i<longueurCot; i++){
				for( int j=0; j<longueurCot; j++){
					//On calcul la distance euclidienne
					double distance=Math.sqrt(Math.pow(carte[i][j][0]-donnees[k][0],2)
							+Math.pow(carte[i][j][1]-donnees[k][1],2)
							+Math.pow(carte[i][j][2]-donnees[k][2],2)
							+Math.pow(carte[i][j][3]-donnees[k][3],2));
					//Si il est plus proche, on le retient
					if(distance<min){
						min=distance;
						xV=i;
						yV=j;
					}
				}
			}
			int nbNeurVainq=(xV*3)+yV;
			//On redivise les données par leur villes
			//Et on les écris dans des fichiers textes
			if(k<l1){
				naw.write(nbNeurVainq+"");
				naw.write("\n");
			} else if(k<l2){
				maw.write(nbNeurVainq+"");
				maw.write("\n");
			} else {
				inw.write(nbNeurVainq+"");
				inw.write("\n");
			}
		}
		naw.close();
		maw.close();
		inw.close();
	}
}
