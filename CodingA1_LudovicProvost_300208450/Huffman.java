
import java.io.*;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Stack;

//import net.datastructures.*;

/**
 * Class Huffman that provides huffman compression encoding and decoding of files
 * @author Lucia Moura 2021
 *
 */

public class Huffman {

	/**
	 *
	 * Inner class Huffman Node to Store a node of Huffman Tree
	 *
	 */
	private class HuffmanTreeNode implements Comparable<HuffmanTreeNode> {
	    private int character;      // character being represented by this node (applicable to leaves)
	    private int count;          // frequency for the subtree rooted at node
	    private HuffmanTreeNode left;  // left/0  subtree (NULL if empty)
	    private HuffmanTreeNode right; // right/1 subtree subtree (NULL if empty)
	    public HuffmanTreeNode(int c, int ct, HuffmanTreeNode leftNode, HuffmanTreeNode rightNode) {
	    	character = c;
	    	count = ct;
	    	left = leftNode;
	    	right = rightNode;
	    }
	    public int getChar() { return character;}
	    public Integer getCount() { return count; }
	    public HuffmanTreeNode getLeft() { return left;}
	    public HuffmanTreeNode getRight() { return right;}
		public boolean isLeaf() { return left==null ; } // since huffman tree is full; if leaf=null so must be right
		public int compareTo(HuffmanTreeNode other) {
	    	return this.getCount().compareTo(other.getCount());
		}
	}

	/**
	 *
	 * Auxiliary class to write bits to an OutputStream
	 * Since files output one byte at a time, a buffer is used to group each output of 8-bits
	 * Method close should be invoked to flush half filed buckets by padding extra 0's
	 */
	private class OutBitStream {
		OutputStream out;
		int buffer;
		int buffCount;
		public OutBitStream(OutputStream output) { // associates this to an OutputStream
			out = output;
			buffer=0;
			buffCount=0;
		}
		public void writeBit(int i) throws IOException { // write one bit to Output Stream (using byte buffer)
		    buffer=buffer<<1;
		    buffer=buffer+i;
		    buffCount++;
		    if (buffCount==8) {
		    	out.write(buffer);
		    	//System.out.println("buffer="+buffer);
		    	buffCount=0;
		    	buffer=0;
		    }
		}

		public void close() throws IOException { // close output file, flushing half filled byte
			if (buffCount>0) { //flush the remaining bits by padding 0's
				buffer=buffer<<(8-buffCount);
				out.write(buffer);
			}
			out.close();
		}

 	}

	/**
	 *
	 * Auxiliary class to read bits from a file
	 * Since we must read one byte at a time, a buffer is used to group each input of 8-bits
	 *
	 */
	private class InBitStream {
		InputStream in;
		int buffer;    // stores a byte read from input stream
		int buffCount; // number of bits already read from buffer
		public InBitStream(InputStream input) { // associates this to an input stream
			in = input;
			buffer=0;
			buffCount=8;
		}
		public int readBit() throws IOException { // read one bit to Output Stream (using byte buffer)
			if (buffCount==8) { // current buffer has already been read must bring next byte
				buffCount=0;
				buffer=in.read(); // read next byte
				if (buffer==-1) return -1; // indicates stream ended
			}
			int aux=128>>buffCount; // shifts 1000000 buffcount times so aux has a 1 is in position of bit to read
			//System.out.println("aux="+aux+"buffer="+buffer);
			buffCount++;
			if ((aux&buffer)>0) return 1; // this checks whether bit buffcount of buffer is 1
			else return 0;

		}

	}

	/**
	 * Builds a frequency table indicating the frequency of each character/byte in the input stream
	 * @param input is a file where to get the frequency of each character/byte
	 * @return freqTable a frequency table must be an ArrayList<Integer? such that freqTable.get(i) = number of times character i appears in file
	 *                   and such that freqTable.get(256) = 1 (adding special character representing"end-of-file")
	 * @throws IOException indicating errors reading input stream
	 */

	private ArrayList<Integer> buildFrequencyTable(InputStream input) throws IOException{
		ArrayList<Integer> freqTable= new ArrayList<>(257); // declare frequency table
		for (int i=0; i<257;i++) freqTable.add(i,0); // initialize frequency values with 0
		int freq;
		while ((freq = input.read()) != -1) {
			freqTable.set(freq, freqTable.get(freq)+1);
		}
		freqTable.set(256,1);
		return freqTable; // return computer frequency table
	}

	/**
	 * Create Huffman tree using the given frequency table; the method requires a heap priority queue to run in O(nlogn) where n is the characters with nonzero frequency
	 * @param freqTable the frequency table for characters 0..255 plus 256 = "end-of-file" with same specs are return value of buildFrequencyTable
	 * @return root of the Huffman tree build by this method
	 */
	private HuffmanTreeNode buildEncodingTree(ArrayList<Integer> freqTable) {

		PriorityQueue<HuffmanTreeNode> P = new PriorityQueue<>();
		for (int i = 0; i < freqTable.size(); i++) {
			if (freqTable.get(i) != 0) {
				HuffmanTreeNode T = new HuffmanTreeNode(i, freqTable.get(i), null, null);
				P.add(T);
			}
		}

		HuffmanTreeNode T;
		while (P.size() > 1) {
			HuffmanTreeNode e1 = P.remove();
			HuffmanTreeNode e2 = P.remove();
			T = new HuffmanTreeNode(0, e1.getCount() + e2.getCount(), e1, e2);
			P.add(T);
		}

		return P.remove();
	}


	/**
	 *
	 * @param encodingTreeRoot - input parameter storing the root of the Huffman tree
	 * @return an ArrayList<String> of length 257 where code.get(i) returns a String of 0-1 corresponding to each character in a Huffman tree
	 *                                                  code.get(i) returns null if i is not a leaf of the Huffman tree
	 */
	private ArrayList<String> buildEncodingTable(HuffmanTreeNode encodingTreeRoot) {
		ArrayList<String> code= new ArrayList<>(257);
		for (int i=0;i<257;i++) code.add(i,null);

		Stack<String> stringStack = new Stack<>();
		Stack<HuffmanTreeNode> nodeStack = new Stack<>();
		stringStack.push("");
		nodeStack.push(encodingTreeRoot);

		while (!nodeStack.empty()) {
			String s = stringStack.pop();
			HuffmanTreeNode node = nodeStack.pop();
			if (node.isLeaf()) {
				code.set(node.getChar(), s);
			}
			if (node.getLeft() != null) {
				stringStack.push(s+0);
				nodeStack.push(node.getLeft());
			}
			if (node.getRight() != null) {
				stringStack.push(s+1);
				nodeStack.push(node.getRight());
			}
		}

		return code;
	}

	/**
	 * Encodes an input using encoding Table that stores the Huffman code for each character
	 * @param input - input parameter, a file to be encoded using Huffman encoding
	 * @param encodingTable - input parameter, a table containing the Huffman code for each character
	 * @param output - output parameter - file where the encoded bits will be written to.
	 * @throws IOException indicates I/O errors for input/output streams
	 */
	private void encodeData(InputStream input, ArrayList<String> encodingTable, OutputStream output) throws IOException {
		OutBitStream bitStream = new OutBitStream(output); // uses bitStream to output bit by bit
		input = new BufferedInputStream(input);
		ArrayList<Integer> freqTable = buildFrequencyTable(input);

		int in;
		input.mark(Integer.MAX_VALUE);

		ObjectOutputStream codedOutput = new ObjectOutputStream(output);

		codedOutput.writeObject(freqTable);

		input.reset();

		while ((in = input.read()) != -1) {
			for (char c : encodingTable.get(in).toCharArray()) {
				bitStream.writeBit(Character.getNumericValue(c));
			}
		}
		bitStream.close(); // close bit stream; flushing what is in the bit buffer to output file
	}

	/**
	 * Decodes an encoded input using encoding tree, writing decoded file to output
	 * @param input  input parameter a stream where header has already been read from
	 * @param encodingTreeRoot input parameter contains the root of the Huffman tree
	 * @param output output parameter where the decoded bytes will be written to
	 * @throws IOException indicates I/O errors for input/output streams
	 */
	private void decodeData(ObjectInputStream input, HuffmanTreeNode encodingTreeRoot, FileOutputStream output) throws IOException {
		InBitStream inputBitStream= new InBitStream(input); // associates a bit stream to read bits from file
		int in;

		HuffmanTreeNode node = encodingTreeRoot;

		while ((in = inputBitStream.readBit()) != -1) {

			if (node.isLeaf()) {
				node = encodingTreeRoot;
				output.write(node.getChar());
				continue;
			}
			if (in == 0) {
				node = node.getLeft();
			}
			if (in == 1) {
				node = node.getRight();
			}
		}
		output.close();
    }

//	/**
//	 * Method that implements Huffman encoding on plain input into encoded output
//	 * @param input - this is the file to be encoded (compressed)
//	 * @param codedOutput - this is the Huffman encoded file corresponding to input
//	 * @throws IOException indicates problems with input/output streams
//	 */
	public void encode(String inputFileName, String outputFileName) throws IOException {
		System.out.println("\nEncoding "+inputFileName+ " " + outputFileName);

		// prepare input and output files streams
		FileInputStream input = new FileInputStream(inputFileName);
		FileInputStream copyinput = new FileInputStream(inputFileName); // create copy to read input twice
		FileOutputStream out = new FileOutputStream(outputFileName);
 		ObjectOutputStream codedOutput= new ObjectOutputStream(out); // use ObjectOutputStream to print objects to file

		ArrayList<Integer> freqTable= buildFrequencyTable(input); // build frequencies from input
		System.out.println("FrequencyTable is="+freqTable);
		HuffmanTreeNode root= buildEncodingTree(freqTable); // build tree using frequencies
		ArrayList<String> codes= buildEncodingTable(root);  // buildcodes for each character in file
		System.out.println("EncodingTable is="+codes);
		codedOutput.writeObject(freqTable); //write header with frequency table
		encodeData(copyinput,codes,codedOutput); // write the Huffman encoding of each character in file
	}

//    /**
//     * Method that implements Huffman decoding on encoded input into a plain output
//     * @param codedInput  - this is a file encoded (compressed) via the encode algorithm of this class
//     * @param output      - this is the output where we must write the decoded file  (should original encoded file)
//     * @throws IOException - indicates problems with input/output streams
//     * @throws ClassNotFoundException - handles case where the file does not contain correct object at header
//     */
	public void decode (String inputFileName, String outputFileName) throws IOException, ClassNotFoundException {
		System.out.println("\nDecoding "+inputFileName+ " " + outputFileName);
		// prepare input and output file streams
		FileInputStream in = new FileInputStream(inputFileName);
 		ObjectInputStream codedInput= new ObjectInputStream(in);
 		FileOutputStream output = new FileOutputStream(outputFileName);

		ArrayList<Integer> freqTable = (ArrayList<Integer>) codedInput.readObject(); //read header with frequency table
		System.out.println("FrequencyTable is="+freqTable);
		HuffmanTreeNode root= buildEncodingTree(freqTable);
		decodeData(codedInput, root, output);
	}
}

