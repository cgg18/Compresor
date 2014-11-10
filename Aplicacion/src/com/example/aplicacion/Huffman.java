package com.example.aplicacion;


import java.io.FileNotFoundException;

/*************************************************************************
 *  Codigo basado en http://algs4.cs.princeton.edu/55compression/
 *
 *************************************************************************/

public class Huffman {

    // alphabet size of extended ASCII
    private static final int R = 256;
    BinaryIn In;
    BinaryOut Out;
    String[] codeTable;
    int[] freq;
    
    public Huffman(){
    
    }
    
    // Huffman trie node
    private  class Node implements Comparable<Node> {
        private final char ch;
        private final int freq;
        private final Node left, right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch    = ch;
            this.freq  = freq;
            this.left  = left;
            this.right = right;
        }

        // is the node a leaf node?
        private boolean isLeaf() {
            assert (left == null && right == null) || (left != null && right != null);
            return (left == null && right == null);
        }

        // compare, based on frequency
        public int compareTo(Node that) {
            return this.freq - that.freq;
        }
    }

/**********************
 * Compress
 * @param fileIn
 * @param fileOut
 * @throws FileNotFoundException
 */
    // compress bytes from standard input and write to standard output
    public  void compress(String fileIn, String fileOut) throws FileNotFoundException{
    	In = new BinaryIn(fileIn);
        Out = new BinaryOut(fileOut);
        
        
        // read the input
        String s = In.readString();
        char[] input = s.toCharArray();

        // tabulate frequency counts
        freq = new int[R];
        for (int i = 0; i < input.length; i++)
            freq[input[i]]++;

        // build Huffman trie
        Node root = buildTrie(freq);

        // build code table
        codeTable = new String[R];
        buildCode(codeTable, root, "");

        // print trie for decoder
        writeTrie(root);

        // print number of bytes in original uncompressed message
        Out.write(input.length);
        
        // use Huffman code to encode input
        for (int i = 0; i < input.length; i++) {
            String code = codeTable[input[i]];
            for (int j = 0; j < code.length(); j++) {
                if (code.charAt(j) == '0') {
                    Out.write(false);
                }
                else if (code.charAt(j) == '1') {
                    Out.write(true);
                }
                else throw new IllegalStateException("Illegal state");
            }
        }

        // close output stream
        Out.close();
        
       
    }
    
/************
 * Imprime la codificacion
 */
    public void printCodeTableAndFrequency(){
    	long total=0;
    	float prob[]= new float [R];
    	
    	System.out.println("\n\nCaracter  (Frecuencia - Probabilidad) \t Codificacion\n------------------------------------------------------");
    	
    	 for (int i=0;i<R;i++){
         	if (codeTable[i]!=null){
         		total+=freq[i];
         	}
         }
    	
    	 for (int i=0;i<R;i++){
          	if (codeTable[i]!=null){
          		prob[i]=(float) freq[i]/ (float) total;
         		System.out.println(i+"\t ("+freq[i]+" - "+prob[i]+")   "+codeTable[i]);
         	    
          	}
          	else   prob[i]=0;
          }
    	 
    	 System.out.println("\nEntropia:       "+entropia(prob));    
    	 System.out.println("Longitud Media: "+longitudMedia(prob)+"\n");    
    }
    
 /**********
  *    
  * @param probabilidades
  * @return
  */
    public float longitudMedia(float probabilidades[]) {
  		float l = 0;
  		
  		for (int i = 0; i < R; i++) {
  			if (probabilidades[i]!=0) l += probabilidades[i] * codeTable[i].length();
  		}
  		return l;
  	}
    
 /***************
  * Entropia   
  * @param probabilidades
  * @return
  */
    public float entropia(float probabilidades[]) {
		float h = 0;
		
		for (int i = 0; i < R; i++) {
			if (probabilidades[i]!=0) h += probabilidades[i] * logBase2(1.0/probabilidades[i]);
		}
		return h;
	}
    
    /*******
	 * Calculo logartimo base 2 mediante cambio de base
	 * @param num
	 * @return
	 */
	private  double logBase2(double num) {
		return logCambioBase(num,2);
	}

	private  double logCambioBase(double num, int b) {
		return Math.log(num)/Math.log(b);
	}
/*****************
 *   build the Huffman trie given frequencies   
 * @param freq
 * @return
 */
   
    private  Node buildTrie(int[] freq) {

        // initialze priority queue with singleton trees
        MinPQ<Node> pq = new MinPQ<Node>();
        for (char i = 0; i < R; i++)
            if (freq[i] > 0)
                pq.insert(new Node(i, freq[i], null, null));

        // merge two smallest trees
        while (pq.size() > 1) {
            Node left  = pq.delMin();
            Node right = pq.delMin();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            pq.insert(parent);
        }
        return pq.delMin();
    }

/************
 *   write bitstring-encoded trie to standard output
 * @param x
 */
    private  void writeTrie(Node x) {
        if (x.isLeaf()) {
            Out.write(true);
            Out.write(x.ch, 8);
            return;
        }
        Out.write(false);
        writeTrie(x.left);
        writeTrie(x.right);
    }

    /*******
     * make a lookup table from symbols and their encodings
     *
     * @param st
     * @param x
     * @param s
     */
    
    private  void buildCode(String[] st, Node x, String s) {
        if (!x.isLeaf()) {
            buildCode(st, x.left,  s + '0');
            buildCode(st, x.right, s + '1');
        }
        else {
            st[x.ch] = s;
        }
    }

/*******************
 * Expand
 * @param fileIn
 * @param fileOut
 * @throws FileNotFoundException
 */
    // expand Huffman-encoded input from standard input and write to standard output
    public  void expand(String fileIn, String fileOut) throws FileNotFoundException{
    	In = new BinaryIn(fileIn);
        Out = new BinaryOut(fileOut);
        
        // read in Huffman trie from input stream
        Node root = readTrie(); 

        // number of bytes to write
        int length = In.readInt();

        // decode using the Huffman trie
        for (int i = 0; i < length; i++) {
            Node x = root;
            while (!x.isLeaf()) {
                boolean bit = In.readBoolean();
                if (bit) x = x.right;
                else     x = x.left;
            }
            Out.write(x.ch, 8);
        }
        Out.close();
    }

/*************
 * 
 * @return
 */
    private  Node readTrie() {
        boolean isLeaf = In.readBoolean();
        if (isLeaf) {
            return new Node(In.readChar(), -1, null, null);
        }
        else {
            return new Node('\0', -1, readTrie(), readTrie());
        }
    }


 
}
