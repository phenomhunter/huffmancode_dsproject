package prj02;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashSet;

import HashTable.*;
import List.*;
import SortedList.*;
import Tree.*;


/**
 * The Huffman Encoding Algorithm
 *
 * This is a data compression algorithm designed by David A. Huffman and published in 1952
 *
 * What it does is it takes a string and by constructing a special binary tree with the frequencies of each character.
 * This tree generates special prefix codes that make the size of each string encoded a lot smaller, thus saving space.
 *
 * @author Fernando J. Bermudez Medina (Template)
 * @author A. ElSaid (Review)
 * @author Orlando G. Mercado Tellado 802-19-0377 (Implementation)
 * @version 2.0
 * @since 10/16/2021
 */
public class HuffmanCoding {
	/** Runs the program
	 * 
	 * @param args the command line arguments.
	 * */
	public static void main(String[] args) {
		HuffmanEncodedResult();
	}

	/* This method just runs all the main methods developed or the algorithm */
	private static void HuffmanEncodedResult() {
		String data = load_data("input1.txt"); //You can create other test input files and add them to the inputData Folder

		/*If input string is not empty we can encode the text using our algorithm*/
		if(!data.isEmpty()) {
			Map<String, Integer> fD = compute_fd(data);
			BTNode<Integer,String> huffmanRoot = huffman_tree(fD);
			Map<String,String> encodedHuffman = huffman_code(huffmanRoot);
			String output = encode(encodedHuffman, data);
			process_results(fD, encodedHuffman,data,output);
		} else {
			System.out.println("Input Data Is Empty! Try Again with a File that has data inside!");
		}

	}

	/**
	 * Receives a file named in parameter inputFile (including its path),
	 * and returns a single string with the contents.
	 *
	 * @param inputFile name of the file to be processed in the path inputData/
	 * @return String with the information to be processed
	 */
	public static String load_data(String inputFile) {
		BufferedReader in = null;
		String line = "";

		try {
			/*We create a new reader that accepts UTF-8 encoding and extract the input string from the file, and we return it*/
			in = new BufferedReader(new InputStreamReader(new FileInputStream("inputData/" + inputFile), "UTF-8"));

			/*If input file is empty just return an empty string, if not just extract the data*/
			String extracted = in.readLine();
			if(extracted != null)
				line = extracted;

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

		}
		return line;
	}

	/**
	 * Receives a string and returns a map with the symbol frequency distribution.
	 *
	 * @param inputString the string whose characters(symbol) are going to be counted and stored in a map.
	 * @return Map with symbols as keys and frequency as values of each key.
	 */
	public static Map<String, Integer> compute_fd(String inputString) {
		/* HashFunction used to initialized the HashTableSC*/
		HashFunction<String> hash = new SimpleHashFunction<String>();
		Map<String, Integer> symFreq = new HashTableSC<String, Integer>(inputString.length(),hash); //HashTableSC initialized with inputString's length to avoid worst case scenario bugs. Worst case: Every symbol is different.
		for(int i = 0; i < inputString.length(); i++)
		{
			String symbol = inputString.substring(i, i+1); //Symbol in the string
			if(!symFreq.containsKey(symbol)) 
			{
				symFreq.put(symbol, 1);
			}
			else //Symbol is already in map
			{
				Integer symVal = symFreq.remove(symbol); //Save the symbol's old value
				symFreq.put(symbol, symVal+1); //Add the symbol with its new value
			}
		}
		return symFreq; // Map with symbol frequency distribution.
	}

	/**
	 * Receives a map with the frequency distribution and returns a root node of the created Huffman tree.
	 *
	 * @param fD the map with the frequency-symbol distribution.
	 * @return the root of the created Huffman tree.
	 */
	public static BTNode<Integer, String> huffman_tree(Map<String, Integer> fD) {

		BTNode<Integer,String> rootNode = null;
		/* Sorted linked list to fill*/
		AbstractSortedList<BTNode<Integer, String>> sortedList = new SortedLinkedList<BTNode<Integer, String>>();
		/* Fill the sorted linked list with the fD values*/
		for(String s : fD.getKeys())
		{
			/* Create BTNode and add it to the sorted linked list*/
			BTNode<Integer, String> btNode = new BTNode<Integer, String>(fD.get(s), s);
			sortedList.add(btNode);
		}
		/* Case in which the sorted linked list consist of only 1 element*/
		if(sortedList.size() == 1)
		{
			rootNode = sortedList.get(0);
		}
		
		/* Continuously gets the first two nodes of the sortedList and creates the Huffman tree*/
		for(int i = 0; sortedList.size() != 1;)
		{
			/* Compare the first two nodes, the smallest will go to the left, the biggest will go to the right*/
			int smaOrBig = sortedList.get(i).compareTo(sortedList.get(i+1));
			/* First node is the smallest of the two*/
			if(smaOrBig < 0)
			{
				/* Get the keys and values of the first two nodes*/
				Integer fNodeKey = sortedList.get(i).getKey();
				Integer sNodeKey = sortedList.get(i+1).getKey();
				String fNodeValue = sortedList.get(i).getValue();
				String sNodeValue = sortedList.get(i+1).getValue();
				/* Combine both nodes into a bigger node*/
				rootNode = new BTNode<Integer, String>(fNodeKey+sNodeKey, fNodeValue+sNodeValue);
				/* Create parent-child relationship*/
				BTNode<Integer, String> first = sortedList.get(i);
				BTNode<Integer, String> second = sortedList.get(i+1);
				first.setParent(rootNode);
				second.setParent(rootNode);
				rootNode.setLeftChild(first);
				rootNode.setRightChild(second);
				/* Erased both elements from the list and add combine node to the list*/
				sortedList.removeIndex(i);
				sortedList.removeIndex(i);
				sortedList.add(rootNode);
			}
			/* First node is the biggest of the two*/
			else
			{
				/* Get the keys and values of the first two nodes*/
				Integer fNodeKey = sortedList.get(i).getKey();
				Integer sNodeKey = sortedList.get(i+1).getKey();
				String fNodeValue = sortedList.get(i).getValue();
				String sNodeValue = sortedList.get(i+1).getValue();
				/* Combine both nodes into a bigger node*/
				rootNode = new BTNode<Integer, String>(fNodeKey+sNodeKey, sNodeValue+fNodeValue);
				/* Create parent-child relationship*/
				BTNode<Integer, String> first = sortedList.get(i);
				BTNode<Integer, String> second = sortedList.get(i+1);
				first.setParent(rootNode);
				second.setParent(rootNode);
				rootNode.setLeftChild(second);
				rootNode.setRightChild(first);
				/* Erased both elements from the list and add combine node to the list*/
				sortedList.removeIndex(i);
				sortedList.removeIndex(i);
				sortedList.add(rootNode);
			}
			
		}
			
		return rootNode;
	}

	/**
	 * Receives the root of a Huffman tree and returns a mapping of every symbol to its corresponding Huffman code.
	 *
	 * @param huffmanRoot the root of the Huffman tree
	 * @return a map with the symbols as keys and the Huffman code as the value of each key
	 */
	public static Map<String, String> huffman_code(BTNode<Integer,String> huffmanRoot) {
		/* Simple hash function to initialize the hash table*/
		HashFunction<String> hash = new SimpleHashFunction<String>();
		/* Map that will hold the symbols and their corresponding Huffman codes*/
		Map<String, String> symCode = new HashTableSC<String, String>(huffmanRoot.getValue().length(), hash);
		/* Traverse each character in the root's string and add to the map*/
		for(char c : huffmanRoot.getValue().toCharArray())
		{
			/* Symbol whose Huffman code will be created*/
			String symbol = String.valueOf(c);
			/* Store the Huffman code string that the auxiliary function will return*/
			String huffCode = huffman_codeAux(huffmanRoot, symbol);
			/* Add the symbol and its corresponding Huffman code to the map*/
			symCode.put(symbol, huffCode);
			
		}
		return symCode;
	}

	/**
	 * Receives the Huffman code map and the input string and returns the encoded string.
	 *
	 * @param encodingMap the map containing the symbols and their corresponding Huffman code.
	 * @param inputString the string to encode.
	 * @return the encoded string.
	 */
	public static String encode(Map<String, String> encodingMap, String inputString) {
		/* Encoded string*/
		String eStr = "";
		/* Traverse the inputString*/
		for(char symbol : inputString.toCharArray())
		{
			/* Concat the symbol's Huffman code to the encoded string*/
			eStr += encodingMap.get(String.valueOf(symbol));
		}
		/* Return the encoded string*/
		return eStr; 
	}

	/**
	 * Receives the frequency distribution map, the Huffman Prefix Code HashTable, the input string,
	 * and the output string, and prints the results to the screen (per specifications).
	 *
	 * Output Includes: symbol, frequency and code.
	 * Also includes how many bits has the original and encoded string, plus how much space was saved using this encoding algorithm
	 *
	 * @param fD Frequency Distribution of all the characters in input string
	 * @param encodedHuffman Prefix Code Map
	 * @param inputData text string from the input file
	 * @param output processed encoded string
	 */
	public static void process_results(Map<String, Integer> fD, Map<String, String> encodedHuffman, String inputData, String output) {
		/*To get the bytes of the input string, we just get the bytes of the original string with string.getBytes().length*/
		int inputBytes = inputData.getBytes().length;

		/**
		 * For the bytes of the encoded one, it's not so easy.
		 *
		 * Here we have to get the bytes the same way we got the bytes for the original one but we divide it by 8,
		 * because 1 byte = 8 bits and our huffman code is in bits (0,1), not bytes.
		 *
		 * This is because we want to calculate how many bytes we saved by counting how many bits we generated with the encoding
		 */
		DecimalFormat d = new DecimalFormat("##.##");
		double outputBytes = Math.ceil((float) output.getBytes().length / 8);

		/**
		 * to calculate how much space we saved we just take the percentage.
		 * the number of encoded bytes divided by the number of original bytes will give us how much space we "chopped off"
		 *
		 * So we have to subtract that "chopped off" percentage to the total (which is 100%)
		 * and that's the difference in space required
		 */
		String savings =  d.format(100 - (( (float) (outputBytes / (float)inputBytes) ) * 100));


		/**
		 * Finally we just output our results to the console
		 * with a more visual pleasing version of both our Hash Tables in decreasing order by frequency.
		 *
		 * Notice that when the output is shown, the characters with the highest frequency have the lowest amount of bits.
		 *
		 * This means the encoding worked and we saved space!
		 */
		System.out.println("Symbol\t" + "Frequency   " + "Code");
		System.out.println("------\t" + "---------   " + "----");

		SortedList<BTNode<Integer,String>> sortedList = new SortedLinkedList<BTNode<Integer,String>>();

		/* To print the table in decreasing order by frequency, we do the same thing we did when we built the tree
		 * We add each key with it's frequency in a node into a SortedList, this way we get the frequencies in ascending order*/
		for (String key : fD.getKeys()) {
			BTNode<Integer,String> node = new BTNode<Integer,String>(fD.get(key),key);
			sortedList.add(node);
		}

		/**
		 * Since we have the frequencies in ascending order,
		 * we just traverse the list backwards and start printing the nodes key (character) and value (frequency)
		 * and find the same key in our prefix code "Lookup Table" we made earlier on in huffman_code().
		 *
		 * That way we get the table in decreasing order by frequency
		 * */
		for (int i = sortedList.size() - 1; i >= 0; i--) {
			BTNode<Integer,String> node = sortedList.get(i);
			System.out.println(node.getValue() + "\t" + node.getKey() + "\t    " + encodedHuffman.get(node.getValue()));
		}

		System.out.println("\nOriginal String: \n" + inputData);
		System.out.println("Encoded String: \n" + output);
		System.out.println("Decoded String: \n" + decodeHuff(output, encodedHuffman) + "\n");
		System.out.println("The original string requires " + inputBytes + " bytes.");
		System.out.println("The encoded string requires " + (int) outputBytes + " bytes.");
		System.out.println("Difference in space requiered is " + savings + "%.");
	}


	/*************************************************************************************
	 ** ADD ANY AUXILIARY METHOD YOU WISH TO IMPLEMENT TO FACILITATE YOUR SOLUTION HERE **
	 *************************************************************************************/

	/**
	 * Auxiliary Method that decodes the generated string by the Huffman Coding Algorithm
	 *
	 * Used for output Purposes
	 *
	 * @param output Encoded String
	 * @param lookupTable the map with symbol-Huffman code elements.
	 * @return The decoded String, this should be the original input string parsed from the input file
	 */
	public static String decodeHuff(String output, Map<String, String> lookupTable) {
		String result = "";
		int start = 0;
		List<String>  prefixCodes = lookupTable.getValues();
		List<String> symbols = lookupTable.getKeys();

		/*looping through output until a prefix code is found on map and
		 * adding the symbol that the code that represents it to result */
		for(int i = 0; i <= output.length();i++){

			String searched = output.substring(start, i);

			int index = prefixCodes.firstIndex(searched);

			if(index >= 0) { //Found it
				result= result + symbols.get(index);
				start = i;
			}
		}
		return result;
	}
	
	/**
	 * Auxiliary Method that returns the Huffman code of a symbol as a string.
	 * 
	 * @param btNode root of the Huffman tree.
	 * @param letter the letter whose Huffman code will be created.
	 * @return a string containing the Huffman code of the given letter.
	 * */
	public static String huffman_codeAux(BTNode<Integer, String> btNode, String letter)
	{
		/* if both children are null, return*/
		if(btNode.getLeftChild() == null && btNode.getRightChild() == null)
		{
			return "";
		}
		/* if left child symbol contains letter*/
		else if(btNode.getLeftChild().getValue().contains(letter))
		{
			/* Concat "0" to the string and enter left child*/
			return "0" + huffman_codeAux(btNode.getLeftChild(), letter);
		}
		/* left child symbol does not contain letter*/
		else
		{
			/* Concat "1" to the string and enter the right child*/
			return "1" + huffman_codeAux(btNode.getRightChild(), letter);
			
		}
	}


}
