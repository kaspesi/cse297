package cse297;
import java.io.*;  
import java.util.*;
import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;
import java.lang.Integer; 
import java.nio.file.Files;
import java.nio.file.Paths;
import cse297.Tree.*;

public class Block implements java.io.Serializable{

    private String prevHash;
    private String rootHash;
    private int timeStamp;
    private byte[] target;
    private int nonce;
    private InnerNode root; 
    private String fileName; 
    private Tree tree;
    
    
    public Block (String prevHash, String rootHash, byte[] target, int nonce, String fileName) throws NoSuchAlgorithmException {
        
        
        this.prevHash = prevHash;
        this.fileName = fileName;
        this.target = target;
        this.nonce = nonce;
        long time=System.currentTimeMillis()/1000;
        this.timeStamp = (int)time;
        this.tree = new Tree(fileName);
        this.root = this.tree.getRoot();
        this.rootHash = toHexString(this.root.getSHA());
        this.mineBlock();
    }

    public String[] getHeaderInfo(){
        String[] headerInfo = new String[5];
        headerInfo[0] = this.prevHash;
        headerInfo[1] = this.rootHash;
        headerInfo[2] = (new Integer(this.timeStamp)).toString();
        headerInfo[3] = toHexString(this.target);
        headerInfo[4] = (new Integer(this.nonce)).toString();
        return headerInfo;
    }
    

    public Block() {}

    public String getRootHash(){
        return this.rootHash;
    }

    public InnerNode getRootNode(){
        return this.root;
    }

    public String getFileName(){
        return this.fileName;
    }

    public Tree getTree(){
        return this.tree;
    }

    public String toHexString(byte[] hash)  { 
        BigInteger number = new BigInteger(1, hash);  
        StringBuilder hexString = new StringBuilder(number.toString(16));  
        while (hexString.length() < 32)  
            hexString.insert(0, '0');   
        return hexString.toString();  
    } 
    
    public boolean mineBlock() throws NoSuchAlgorithmException{
        Random rand = new Random();
        String byteString = this.nonce + this.rootHash;
        byte[] guess = getSHA(byteString);
        BigInteger guessNumber = new BigInteger(guess);
        BigInteger targetNumber = new BigInteger(target);
        do{ 
            this.nonce = rand.nextInt();
            byteString = nonce + this.rootHash;
            guess = getSHA(byteString); 
            guessNumber = new BigInteger(guess);
        } while(guessNumber.compareTo(targetNumber) == 1); 
        return true;
    }
    

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException 
    {  
        // Static getInstance method is called with hashing SHA  
        MessageDigest md = MessageDigest.getInstance("SHA-256");  
        return md.digest(input.getBytes(StandardCharsets.UTF_8));  
    } 

    public String calculateBlockHash() throws NoSuchAlgorithmException{
        String stringTarget = new String(target, StandardCharsets.UTF_8);
        String input = prevHash + rootHash + Long.toString(timeStamp) + stringTarget + Integer.toString(nonce);
        MessageDigest md = null;
        byte[] bytes = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            String retval = toHexString(md.digest(input.getBytes(StandardCharsets.UTF_8)));
            return retval;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return " ";
    }
    public String[] parseFileNames(String fileSequence) {
        String[] fileNames = fileSequence.split(" ");
        return fileNames;
    }

    private static byte[] intToByteArray(final int i) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(i);
        dos.flush();
        return bos.toByteArray();
    }

    public String printBlocks(ArrayList<Block> blocks, boolean printTree){
        System.out.println(blocks);
        for(Block b : blocks){
            System.out.println(b);
            String retString = "";
            InnerNode node = b.getRootNode();
            try {
                String inputFileName = b.getFileName();
                System.out.println(inputFileName);
                String[] nameParts = inputFileName.split("\\.");
                inputFileName = nameParts[0] + ".block.out";
                inputFileName = inputFileName.replace("/", "");
                File myOut = new File(inputFileName);
                BufferedWriter writer = new BufferedWriter(new FileWriter(myOut.getName()));
                myOut.createNewFile();
                    writer.newLine();
                    writer.write("BEGIN BLOCK");
                    writer.newLine();
                    writer.write("BEGIN HEADER");
                    writer.newLine();
                    String[] headerInfo = b.getHeaderInfo();
                    writer.write(headerInfo[0]);
                    writer.newLine();
                    writer.write(headerInfo[1]);
                    writer.newLine();
                    writer.write(headerInfo[2]);
                    writer.newLine();
                    writer.write(headerInfo[3]);
                    writer.newLine();
                    writer.write(headerInfo[4]);
                    writer.newLine();
                    writer.write("END HEADER");
                    writer.newLine();
                    writer.newLine();

                    LinkedList<InnerNode> q = new LinkedList<>();
                    ArrayList<LeafNode> q_leafs = new ArrayList<>();

                    int p = 0;
                    if (node == null) {
                        return " ";
                    }
                    
                    q.add(node);
                    while (!q.isEmpty()) {
                        p++;
                        InnerNode curr = (InnerNode)q.pollLast();
                        if(!curr.getLeftChild().isLeafNode() && !curr.getRightChild().isLeafNode()){
                            InnerNode leftChild = (InnerNode)curr.getLeftChild();
                            InnerNode rightChild = (InnerNode)curr.getRightChild();
                            writer.write(Integer.toString(p));
                            writer.newLine();
                            writer.write(Integer.toString((2*p)));
                            writer.newLine();
                            writer.write(curr.getLeftChildLabel());
                            writer.newLine();
                            writer.write(curr.getLeftChildLabel());
                            writer.newLine();
                            writer.write(curr.getSHAString());
                            writer.newLine();
                            writer.write(curr.getRightChildLabel());
                            writer.newLine();
                            writer.write(Integer.toString((2*p+1)));
                            writer.newLine();
                            writer.newLine();
                            q.addFirst((InnerNode)curr.getLeftChild());
                            q.addFirst((InnerNode)curr.getRightChild());
                        } else {
                            LeafNode leftChild = (LeafNode)curr.getLeftChild();
                            LeafNode rightChild = (LeafNode)curr.getRightChild();
                            writer.write(Integer.toString(p));
                            writer.newLine();
                            writer.write(Integer.toString((2*p)));
                            writer.newLine();
                            writer.write(curr.getLeftChildLabel());
                            writer.newLine();
                            writer.write(curr.getLeftChildLabel());
                            writer.newLine();
                            writer.write(curr.getSHAString());
                            writer.newLine();
                            writer.write(curr.getRightChildLabel());
                            writer.newLine();
                            writer.write(Integer.toString((2*p+1)));
                            writer.newLine();
                            writer.newLine();
                            q_leafs.add((LeafNode)curr.getLeftChild());
                            q_leafs.add((LeafNode)curr.getRightChild());
                        }                        
                    }

                    for(LeafNode curr: q_leafs){
                        p++;
                        writer.write(Integer.toString(p));
                        writer.newLine();
                        writer.write(curr.getString());
                        writer.newLine();
                        writer.write(curr.getSHAString());
                        writer.newLine();
                        writer.newLine();
                    }
                    writer.write("END BLOCK");
                    retString = writer.toString();
                    writer.close();

            } catch (IOException e) {
                System.out.println("An error has occured creating out file.");
                e.printStackTrace();
            }
               
        }

    return "";
    }

    public static void main(String[] args) {
        System.out.println("Test");
        byte[] firstTarget = null;
        try {
            firstTarget = intToByteArray(2 ^ 256 - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Integer zero = new Integer(0);
        Block b = new Block();
        FileInputStream fis = null;
        BufferedReader reader = null;
        File file = null;
        String currentDirectory = null;
        String[] fileNames;
        ArrayList<Block> blocks = new ArrayList<>();

        try {
            Scanner myObj = new Scanner(System.in);
            System.out.println("Please enter file sequence");
            fileNames = b.parseFileNames(myObj.nextLine());
            if(fileNames.length > 0) blocks.add(0, new Block(zero.toString(), zero.toString(), firstTarget, 10, fileNames[0]));
            for(int i = 1; i < fileNames.length; i++){
                blocks.add(i, new Block(blocks.get(i-1).calculateBlockHash() , zero.toString(), firstTarget, 10, fileNames[i]));
            }
            System.out.println(blocks);
            b.printBlocks(blocks, false);
            FileOutputStream fos = new FileOutputStream("serializedBlocks");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(blocks);
            oos.close();
            fos.close();
           
        } catch(Exception e){
            e.printStackTrace();
        }
            
    }
}
package cse297;

import java.io.*;  
import java.util.*;
import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;
import java.lang.Integer;  


//Sources Cited:
//https://www.geeksforgeeks.org/sha-256-hash-in-java/



public class Tree implements java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    InnerNode root;
    public Tree(String fileName){
        fileName = this.formatFileName(fileName);
        ArrayList<String> strings = new ArrayList<>();
        FileInputStream fis = null;
        BufferedReader reader = null;
        File file = null;
        String currentDirectory = null;
        fileName = this.formatFileName(fileName);
        try{
            currentDirectory = System.getProperty("user.dir");
            file=new File(currentDirectory + fileName);   
            fis=new FileInputStream(file);    
            reader = new BufferedReader(new InputStreamReader(fis));
            
            String line = reader.readLine();
            while(line != null){
                strings.add(line);
                //System.out.println(line);
                line = reader.readLine();
            }   
            Collections.sort(strings);
            this.root = this.generateMerkleTree(strings);
            this.generatePatriciaEdges(root);
            //this.printTree(root, fileName);
        }
        catch(Exception e)  
        {  
            e.printStackTrace();  
        }  
    }

    public InnerNode getRoot(){
        return this.root;
    }

    class Node implements java.io.Serializable {

        private Node leftChild;
        private String leftChildLabel;
        private Node rightChild;
        private String rightChildLabel;
        private byte[] SHA256;
        private boolean isLeaf;

        public boolean isLeafNode(){
            return this.isLeaf;
        }

        public void setLeftLabel(String leftLabel){
            this.leftChildLabel = leftLabel;
        }

        public void setRightLabel(String rightLabel){
            this.rightChildLabel = rightLabel;
        }

        private Node getRightChild(){
            return this.rightChild;
        }
        private Node getLeftChild(){
            return this.rightChild;
        }

        public String getSHAString(){
            return toHexString(SHA256);
        };
        public byte[] getSHA(){
            return SHA256;
        };

        public byte[] getSHA(String input) throws NoSuchAlgorithmException {  
            MessageDigest  md = MessageDigest.getInstance("SHA-256");  ; 
            return md.digest(input.getBytes(StandardCharsets.UTF_8));  
        } 

        
        public String toHexString(byte[] hash)  { 
            BigInteger number = new BigInteger(1, hash);  
            StringBuilder hexString = new StringBuilder(number.toString(16));  
            while (hexString.length() < 32)  
                hexString.insert(0, '0');   
            return hexString.toString();  
        } 
        
    }

    class LeafNode extends Node {
        
        private String str;
        private byte[] SHA256;        
        private boolean isLeaf;
        
        public LeafNode(String str) throws NoSuchAlgorithmException{
            this.str = str;
            this.SHA256 = getSHA(str);
            this.isLeaf = true;
        }


        public boolean isLeafNode(){
            return this.isLeaf;
        }
        

        public String getString(){
            return this.str;
        }

        public byte[] getSHA(){
            return this.SHA256;
        }

        public String getSHAString(){
            return toHexString(this.SHA256);
        }

    }


    class InnerNode extends Node {
        private Node leftChild;
        private String leftChildLabel;
        private Node rightChild;
        private String rightChildLabel;
        private byte[] SHA256;
        private boolean isLeaf;

        public boolean isLeafNode(){
            return this.isLeaf;
        }

        public InnerNode(Node leftChild, Node rightChild) throws NoSuchAlgorithmException{
            this.leftChild = leftChild;
            // this.leftChildLabel = leftChild.getSHAString();
            this.rightChild = rightChild;
            // this.rightChildLabel = rightChild.getSHAString();
            this.isLeaf = false;
            this.SHA256 = getSHAChildren(leftChild, rightChild);
        }

        public void setLeftLabel(String leftLabel){
            this.leftChildLabel = leftLabel;
        }

        public void setRightLabel(String rightLabel){
            this.rightChildLabel = rightLabel;
        }

        public byte[] getSHA(){
            return this.SHA256;
        }

        public Node getLeftChild(){
            return this.leftChild;
        }

        public Node getRightChild(){
            return this.rightChild;
        }

        public String getLeftChildLabel(){
            return this.leftChildLabel;
        }

        public String getRightChildLabel(){
            return this.rightChildLabel;
        }

        public String getSHAString(){
            return toHexString(this.SHA256);
        }

        public byte[] getSHAChildren(Node one, Node two) throws NoSuchAlgorithmException {  
            ByteArrayOutputStream outputStream = null;
            MessageDigest md = null;
            byte[] jointHash = null;
            byte[] oneHash = null;
            byte[] twoHash = null;
            try{
                oneHash = one.getSHA();
                twoHash = two.getSHA();
                outputStream = new ByteArrayOutputStream();
                outputStream.write(oneHash);
                outputStream.write(twoHash);
                jointHash = outputStream.toByteArray();
                md = MessageDigest.getInstance("SHA-256");   
            } catch(Exception e){
                e.printStackTrace();  
            }
            
            return md.digest(jointHash);  
        } 

    }

    public String formatFileName(String fileName){
        if(fileName.contains(".txt")){
            return "/" + fileName;
        } else {
            return "/" + fileName + ".txt";

        }
    }


    public InnerNode generateMerkleTree(ArrayList<String> keys) throws NoSuchAlgorithmException{
        
        LinkedList<InnerNode> q = new LinkedList<>();
        boolean oddLeafs = false;
        int n = keys.size();
        if(n%2 != 0){
            oddLeafs = true;
        }
        int i = 0;
        //Generate first level of inner nodes from the leaf nodes
        for(i = 0; i < n-1; i+=2){
            LeafNode l = new LeafNode(keys.get(i));
            LeafNode r = new LeafNode(keys.get(i+1));
            InnerNode parent = new InnerNode(l, r);
            q.addLast(parent);
        } //Case with odd leaf nodes, we use the last one twice
        if(oddLeafs){
            LeafNode l = new LeafNode(keys.get(n-1));
            LeafNode r = new LeafNode(keys.get(n-1));
            InnerNode parent = new InnerNode(l, r);
            q.addLast(parent);
        }


        //Continue taking children and adding parent at higher level until we reach a single root node
        while(q.size() != 1){

            LinkedList<InnerNode> tempList = new LinkedList<>();
            for(i = 0; i < q.size()-1; i+=2){
                tempList.add(new InnerNode(q.get(i), q.get(i+1)));
            }
            q = tempList;
        }

        //Return root node
        return q.get(0);
    }

    //Finds the maximum string value in a node subtree
    public String findMax(InnerNode node){

        while(!node.getRightChild().isLeafNode()){
            node = (InnerNode)node.getRightChild();
        }
        String retStr = ((LeafNode)node.getRightChild()).getString();
        return retStr;
    }

    //Finds the minimum string value in a node subtree
    public String findMin(InnerNode node){

        while(!node.getLeftChild().isLeafNode()){
            node = (InnerNode)node.getLeftChild();
        }
        String retStr = ((LeafNode)node.getLeftChild()).getString();
        return retStr;
    }

    //Generates edges with range x.leftChildLabel = to maximum of left child subarray
    //and x.rightChildLabel = minimum of right child subarray 
    public void generatePatriciaEdges(InnerNode node){
        
        //Queue for BFS itteration of tree
        LinkedList<InnerNode> q = new LinkedList<>();
        if (node == null) {
            return;
        }

        q.add(node);
        while (!q.isEmpty()) {

            InnerNode curr = (InnerNode)q.pollLast();
            if(!curr.getLeftChild().isLeafNode()){
                q.addFirst((InnerNode)curr.getLeftChild());
                String edgeLabel = findMax((InnerNode)curr.getLeftChild());
                curr.setLeftLabel(edgeLabel);

            } else {
                String edgeLabel = ((LeafNode)curr.getLeftChild()).getString();
                curr.setLeftLabel(edgeLabel);

            }
            if(!curr.getRightChild().isLeafNode()){
                q.addFirst((InnerNode)curr.getRightChild());
                String edgeLabel = findMin((InnerNode)curr.getRightChild());
                curr.setRightLabel(edgeLabel);

            } else {
                String edgeLabel = ((LeafNode)curr.getRightChild()).getString();
                curr.setRightLabel(edgeLabel);


            }
            
        }
        

        }

    //Traverses search itteratively with BFS to get nodes in print order 
    //If statements in order to cast InnerNode to LeafNode 
    public String printTree(InnerNode node, String fileName){
        String retString = "";
        try {

            //Generate output file name
            String[] nameParts = fileName.split("\\.");
            fileName = nameParts[0] + ".out.txt";
            fileName = fileName.replace("/", "");
            PrintWriter out = new PrintWriter(new FileWriter(fileName, true), true);
        
            LinkedList<InnerNode> q = new LinkedList<>();
            ArrayList<LeafNode> q_leafs = new ArrayList<>();
            int p = 0;
            if (node == null) {
                return " ";
            }
            
            q.add(node);
            while (!q.isEmpty()) {
                p++;
                InnerNode curr = (InnerNode)q.pollLast();
                if(!curr.getLeftChild().isLeafNode() && !curr.getRightChild().isLeafNode()){
                    InnerNode leftChild = (InnerNode)curr.getLeftChild();
                    InnerNode rightChild = (InnerNode)curr.getRightChild();
                    String printStr = Integer.toString(p)+ "\n"  + Integer.toString((2*p)) + "\n" + curr.getLeftChildLabel() + "\n" + curr.getSHAString() + "\n" +  curr.getRightChildLabel() + "\n" + Integer.toString((2*p+1)) + "\n\n";
                    System.out.println(printStr);
                    out.write(printStr);
                    q.addFirst((InnerNode)curr.getLeftChild());
                    q.addFirst((InnerNode)curr.getRightChild());
                    

                } else {
                    LeafNode leftChild = (LeafNode)curr.getLeftChild();
                    LeafNode rightChild = (LeafNode)curr.getRightChild();
                    String printStr = Integer.toString(p)+ "\n"  + Integer.toString((2*p)) + "\n" + curr.getLeftChildLabel() + "\n" + curr.getSHAString() + "\n" + curr.getRightChildLabel() + "\n" + Integer.toString((2*p+1)) + "\n\n";
                    System.out.println(printStr);
                    out.write(printStr);
                    q_leafs.add((LeafNode)curr.getLeftChild());
                    q_leafs.add((LeafNode)curr.getRightChild());

                }
                
                
            }

            for(LeafNode curr: q_leafs){
                p++;
                String printStr = Integer.toString(p) + "\n" + curr.getString() + "\n" + curr.getSHAString() + "\n\n";

                System.out.println(printStr);
                out.write(printStr);
            }
            retString = out.toString();
            out.close();

            return " ";
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return retString;
        
    }

    public static void main(String[] args) {
        
    

     
    }
}