package core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.StringTokenizer;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

public class Knn {
	// Store all users and their music collections
	static HashMap<Integer, Integer> hashData[];
	// Store all test data
	static HashSet<Integer> test[];
	static int k = 10;
	static boolean weighted = true;
	static int metric = 3;
	static int qu, qa;
	static int max, count;
	static PrintWriter output;
	static double totalP10;
	static double[] vectorA;
	//static double[][] sims;
	
	/**
	 * read training data from file
	 * @param fileName
	 */
	public static void readTrainingData(String fileName){
		String line = "";
		count = 0;
		max = 0;
		try {
			BufferedReader input = new BufferedReader(new FileReader(fileName));
			while ((line = input.readLine()) != null){
				count++;
//				System.out.println("COUNT==" + count);
				int start = line.indexOf('-') + 1;
				line = line.substring(start + 1);
				hashData[count] = new HashMap<Integer, Integer>();
				StringTokenizer token = new StringTokenizer(line, " ");
				while (token.hasMoreElements()) {
					String cur = token.nextToken();
					int div = cur.indexOf(':');
					int musicId = Integer.parseInt(cur.substring(0,  div));
					int numPlayed = Integer.parseInt(cur.substring(div + 1));
					hashData[count].put(musicId, numPlayed);
					if (musicId > max) {
						max = musicId;
					}
				}
//				System.out.println(line);
			}
			input.close();
			System.out.println("TOTAL USER=" + count);
			System.out.println("MAX ID=" + max);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	/**
	 * read test data from file
	 * @param fileName
	 */
	public static void readTestData(String fileName){
		try {
			BufferedReader input = new BufferedReader(new FileReader(fileName));
			for (int i = 1; i <= count; i++){
				String line = input.readLine();
				test[i] = new HashSet<Integer>();
				int start = line.indexOf('-') + 1;
				line = line.substring(start + 1);
				StringTokenizer token = new StringTokenizer(line, " ");
				while (token.hasMoreElements()) {
					test[i].add(Integer.parseInt(token.nextToken()));
				}
			}
			input.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Get inverse Euclidean distance of two vectors
	 * @param vectori
	 * @param vectorj
	 * @return
	 */
	public static double getEuclideanDist(double[] vectori, double[] vectorj){
		double res = 0;
		for (int i = 1; i <= max; i++) {
			res += Math.pow((vectori[i] - vectorj[i]), 2);
		}
		res = (double) 1.0 / Math.sqrt(res);
		return res;
	}
	
	/**
	 * Get dot product of two vectors
	 * @param vectori
	 * @param vectorj
	 * @return
	 */
	public static double getDotDist(double[] vectori, double[] vectorj){
		double res = 0;
		for (int i = 1; i <= max; i++) {
			res += vectori[i] * vectorj[i];
		}
		return res;
	}
	
	/**
	 * Get cosine distance of two vectors, using the colt library
	 * @param vectori
	 * @param vectorj
	 * @return
	 */
	public static double getCosineDist(double[] vectori, double[] vectorj){
		DoubleMatrix1D a = new DenseDoubleMatrix1D(vectori);
		DoubleMatrix1D b = new DenseDoubleMatrix1D(vectorj);
		return a.zDotProduct(b)/Math.sqrt(a.zDotProduct(a)*b.zDotProduct(b));
	}
	
	/**
	 * Get the distance of two vectors, using the specified method
	 * @param indexi
	 * @param indexj
	 * @return
	 */
	public static double getDist(int indexi, int indexj){
		// Convert two user' music collections to vectors
		double[] vectori = new double[max + 1];
		Arrays.fill(vectori, 0);
		double[] vectorj = new double[max + 1];
		Arrays.fill(vectorj, 0);
		if (indexi == 0) {
			vectori = vectorA.clone();
		}
		else{
			Iterator<Entry<Integer, Integer>> it = hashData[indexi].entrySet().iterator();
			while (it.hasNext()) {
		        Map.Entry<Integer, Integer> pairs = (Map.Entry<Integer, Integer>) it.next();
		        vectori[(Integer) pairs.getKey()] = (Integer) pairs.getValue();
		    }
		}
		
		Iterator<Entry<Integer, Integer>> it2 = hashData[indexj].entrySet().iterator();
		while (it2.hasNext()) {
	        Map.Entry<Integer, Integer> pairs = (Map.Entry<Integer, Integer>) it2.next();
	        vectorj[(Integer) pairs.getKey()] = (Integer) pairs.getValue();
	    }
		
		switch (metric) {
			case 1: return getEuclideanDist(vectori, vectorj);
			case 2: return getDotDist(vectori, vectorj);
			case 3: return getCosineDist(vectori, vectorj);
			default: return -1;
		}
	}
	/**
	 * Retrieve the top 10 songs for a specific user
	 * @param userId
	 */
	public static void queryUser(int userId) {
		// First find the top k neighbor of the user
		double[] res = new double[k];
		int[] resId = new int[k];
		Arrays.fill(res, 0.0);
		Arrays.fill(resId, 0);
		for (int id = 1; id <= count; id++) {
			if (id == userId) {
				continue;
			}
			double sim = 0;
			
			// In my test I used some prefetched data
//			if (sims[userId][id] != -1) {
//				sim = sims[userId][id];
//			}
//			else if (sims[id][userId] != -1) {
//				sim = sims[id][userId];
//			}
//			else{
//				sim = getDist(userId, id);
//				sims[id][userId] = sim;
//				sims[userId][id] = sim;
//			}
			
			// Based on the distance between two users' music collections
			sim = getDist(userId, id);
			if (sim != 0) {
				int find = -1;
				for (int i = 0; i < k; i++){
					if (sim > res[i]) {
						find = i;
						break;
					}
				}
				if (find != -1){
					for (int i = k - 1; i > find; i--){
						res[i] = res[i - 1];
						resId[i] = resId[i - 1];
					}
					res[find] = sim;
					resId[find] = id;
				}
			}
		}
		
		// Print the user information
//		for (int i = 0; i < k; i++){
//			System.out.println(resId[i] + " " + res[i]);
//		}
		
		// Add the possible music of these k users to a set
		HashSet<Integer> musicSet = new HashSet<Integer>();
		for (int i = 0; i < k; i++) {
			int id = resId[i];
			if (id != 0) {
				Iterator<Entry<Integer, Integer>> it = hashData[id].entrySet().iterator();
				while (it.hasNext()) {
			        Map.Entry<Integer, Integer> pairs = (Map.Entry<Integer, Integer>) it.next();
			        int mid = pairs.getKey();
			        if (!musicSet.contains(mid)) {
			        	musicSet.add(mid);
			        }
			    }
			}
		}
		
		// Retrieve the top 10 music from this set
		double[] score = new double[10];
		int[] musicId = new int[10];
		Arrays.fill(score, 0.0);
		Arrays.fill(musicId, 0);
		
		Iterator<Integer> it = musicSet.iterator();
		while (it.hasNext()) {
	        int id =  it.next();
	        if (hashData[userId].containsKey(id)) {
				// If the music is already in query user's collection, find the next one
				continue;
			}
			else{
				double value = 0;
				if (!weighted) {
					// Unweighted
					for (int j = 0; j < k; j++){
						if (resId[j] != 0) {
							HashMap<Integer, Integer> hm = hashData[resId[j]];
							if (hm.containsKey(id)){
								value += hm.get(id);
							}
						}
					}
					value /= k;
				}
				else {
					// Weighted
					double divide = 0;
					for (int j = 0; j < k; j++){
						if (resId[j] != 0) {
							HashMap<Integer, Integer> hm = hashData[resId[j]];
							if (hm.containsKey(id)){
								value += hm.get(id) * res[j];
							}
						}
						divide += res[j];
					}
					value /= divide;
				}
				
				// find the top 10 similiar ones
				int mfind = -1;
				for (int ii = 0; ii < 10; ii++){
					if (value > score[ii]) {
						mfind = ii;
						break;
					}
				}
				if (mfind != -1){
					for (int ii = 10 - 1; ii > mfind; ii--){
						score[ii] = score[ii - 1];
						musicId[ii] = musicId[ii - 1];
					}
					score[mfind] = value;
					musicId[mfind] = id;
				}
			}
	    }
		
		// Computer the precision@10
        if (userId != 0) {
            double precision = 0;
            for (int i = 0; i < 10; i++){
            	System.out.println(musicId[i] + " " + score[i]);
                if (test[userId].contains(musicId[i])) {
                    precision++;
                }
            }
            precision /= 10.0;
            totalP10 += precision;
            output.println("For user," + userId + ", P@10 = " + precision);
            System.out.println("For this user, P@10 = " + precision);
        }
        else{
            for (int i = 0; i < 10; i++){
                output.println(musicId[i] + " " + score[i]);
            }
        }
	}
	
	/**
	 * Query an artist, in this case "Metallica"
	 */
	public static void queryArtist(){
		vectorA = new double[max + 1];
		Arrays.fill(vectorA, 0);
		//data[0] = new ArrayList<MusicTuple>();
        hashData[0] = new HashMap<Integer, Integer>();
		try {
			BufferedReader input = new BufferedReader(new FileReader("song_mapping.txt"));
			String line = "";
			while ((line = input.readLine()) != null) {
				if (line.endsWith("Metallica")) {
                    String temp = line.substring(0, line.indexOf('	'));
					System.out.println(temp);
                    int id = Integer.parseInt(temp);
                    //data[0].add(new MusicTuple(id, 1));
                    hashData[0].put(id, 1);
					vectorA[id] = 1;
                    
				}
			}
			input.close();
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
		queryUser(0);
        
        
	}
	
	 /** Process query for num of users
	  * 
	  * @param num
	  */
	public static void processQuery(int num){
        System.out.println("k = " + k);
		System.out.println("metric = " + metric);
        System.out.println("weighted ? " + weighted);
		totalP10 = 0.0;
		for (int i = 1; i <= num; i++){
			queryUser(i);
		}
		totalP10 /= num;
		output.println("Average P@10 =" + totalP10);
//		queryUser(1);
	}
	
	/**
	 * Get the two baselines
	 */
	public static void getBaseline() {
		// Random
		int[] random = new int[10];
		System.out.println("Random:");
		for (int i = 0; i < 10; i++) {
			random[i] = (int) (Math.random() * 74594);
			System.out.println(random[i]);
		}
		
		double total_precision = 0;
		for (int id = 1; id <= count; id++) {
			double precision = 0;
			for (int i = 0; i < 10; i++) {
				if (test[id].contains(random[i])) {
					precision += 0.1;
				}
			}
			total_precision += precision;
		}
		total_precision /= count;
		System.out.println("Random baseline: Average P@10 = " + total_precision);
		
		// Popularity-based
		System.out.println("Popularity-based:");
		int[] musicCount = new int[max + 1];
		Arrays.fill(musicCount, 0);
		for (int id = 1; id <= count; id++) {
			Iterator<Entry<Integer, Integer>> it = hashData[id].entrySet().iterator();
			while (it.hasNext()) {
		        Map.Entry<Integer, Integer> pairs = (Map.Entry<Integer, Integer>) it.next();
		        musicCount[(Integer) pairs.getKey()] = (Integer) pairs.getValue();
		    }
		}
		
		int[] topCount = new int[10];
		int[] topId = new int[10];
		
		for (int sid = 1; sid <= max; sid++) {
			int find = -1;
			for (int i = 0; i < 10; i++){
				if (musicCount[sid] > topCount[i]) {
					find = i;
					break;
				}
			}
			if (find != -1){
				for (int i = 10 - 1; i > find; i--){
					topCount[i] = topCount[i - 1];
					topId[i] = topId[i - 1];
				}
				topCount[find] = musicCount[sid];
				topId[find] = sid;
			}
		}
        
        for (int i = 0; i < 10; i++) {
			System.out.println(topId[i]);
		}
		
		total_precision = 0;
		for (int id = 1; id <= count; id++) {
			double precision = 0;
			for (int i = 0; i < 10; i++) {
				if (test[id].contains(topId[i])) {
					precision += 0.1;
				}
			}
			total_precision += precision;
		}
		total_precision /= count;
		System.out.println("Popularity-based baseline: Average P@10 = " + total_precision);
	}
	
	/**
	 * Read query and set parameters
	 */
	public static void readQuery(){
		Scanner input = new Scanner(System.in);
		System.out.println("Input k: the number of neighbors to consider:");
		k = input.nextInt();
		System.out.println("Input whether to use weighted or unweighted version (y/n):");
		weighted =  input.next().equals("y") ? true : false;
		System.out.println("Choose similarity metric (1:Euclidean 2:Dot 3:Cosine):");
		metric = input.nextInt();
		System.out.println("Would you like to do a user query or artist query (u/a):");
		String temp = input.next();
		if(temp.equals("u")){
			System.out.println("Input user ID, 0 to compute average of all users:");
			qu = input.nextInt();
			if (qu == 0) {
				processQuery(count);
			}
			else {
				queryUser(qu);
			}
		}
		else{
			queryArtist();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws FileNotFoundException{
		// Some code for reading prefetched data
//		sims = new double[3325][3325];
//		for(int i = 1; i <= 3323; i++) {
//			for (int j = 1; j <= 3323; j++) {
//				sims[i][j] = -1;
//			}
//		}
//		BufferedReader reader = new BufferedReader(new FileReader("cosine-sim.txt"));
//		String line = "";
//		try {
//			while ((line = reader.readLine()) != null) {
//				String str[] = line.split(" ");
//				sims[Integer.parseInt(str[0])][Integer.parseInt(str[1])] = Double.parseDouble(str[2]);
//			}
//		} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//		}
//		System.out.println("READ DONE");
		
		hashData = new HashMap[3325];
		// Save results to a file
		output = new PrintWriter("result.txt");
		output.println("START TIME: " + new java.util.Date().toString());
		readTrainingData("user_train.txt");
		test = new HashSet[count + 1];
		readTestData("user_test.txt");
        //queryArtist();
        //getBaseline();
		readQuery();
		
		output.println("FINISH TIME: " + new java.util.Date().toString());
		output.close();
	}
}
