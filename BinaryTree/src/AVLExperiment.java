//Yachyaa Toefy`
//16 March 2022
//Assignment 2 - AVL trees
//Used Hussein Sulemans AVLTree.java programme

import java.util.*;
import java.io.*;

public class AVLExperiment
{
   List<String> a = new ArrayList<>();
   AVLTree<String> tree = new AVLTree<>();
   
   
	public static void main (String[] args)
	{
	  AVLExperiment instance = new AVLExperiment();
	  instance.fillArray();
	  instance.randomise();
	}
	  
//Reads and inserts each line of vaccination.csv into an ArrayList<>	  
	public void fillArray()
	{
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(
				"/home/yachyaa/CSCassignments/A2/src/vaccinations.csv"));
			String line = reader.readLine();
			while (line != null) {
				a.add(line);
				line = reader.readLine();
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				}
	}
	
	
//Swaps 2 indexes in a Array
	public void swap ( String[] array, int i, int j){
		array[i] = array[j];
		}
	

// Swaps item at "i" in ArrayList with a random number in range 0-9919
	public void randomise()
	{
		Random rand = new Random();
		for (int i = 0; i<=9918; i++){
			
			//swaps item at "i" with a Randomly generated number in ranged 0-9919
			Collections.swap(a, i, rand.nextInt(9918));
			
			//inserts items in the randomised array ito the AVL tree in differn intervals of i
			if (i == 495 || i == 991 || i == 1485 || i == 1982 || i == 2475 || 
				i == 2973 || i == 3465 || i == 3964 || i == 4455 ||
				i == 4955 || i == 5450 || i == 5946 || i == 6440 || i == 6937 ||
				i == 7430 || i == 7928 || i == 8420 || i == 8919 || 
				i == 9410 || i == 9918)
				{	
				for (int j = 0; j<=9918; j++){
					tree.insert(a.get(j));
					}
					
			//output for each AVL with different levels of randomisation
				
				System.out.println(i + " Randomisations");
				
				System.out.println("Insertion: " + tree.insertCounter);
				
				tree.find("notFound");
				int worst = tree.findCounter;
				System.out.println("Find Wost Case: " + worst);
				
				tree.find(tree.root.data);
				int best = tree.findCounter - worst - 1;
				System.out.println ("Find Best Case: " + best);
				
				tree.find("Canada,2022-01-05,266746");
				int av = tree.findCounter - worst - best;
				System.out.println("Find Average Case: " +  av + "\n");
				
				tree.clearCounters();
				tree.clearTree(tree.root);
				}
			}
	}

}
