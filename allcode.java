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
        private boolean isEmpty;

        public boolean isLeafNode(){
            return this.isLeaf;
        }

        public boolean isEmptyNode(){
            return this.isEmpty;
        }

        public void setLeftLabel(String leftLabel){
            this.leftChildLabel = leftLabel;
        }

        public void setRightLabel(String rightLabel){
            this.rightChildLabel = rightLabel;
        }

        public Node getRightChild(){
            return this.rightChild;
        }
        public Node getLeftChild(){
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

    class EmptyNode extends Node{
        private byte[]SHA256;
        private boolean isEmpty;
        private boolean isLeaf;

        public EmptyNode(){
            this.isEmpty = true;
            this.SHA256 = new byte[0];
            this.isLeaf = false;
        }

        public boolean isEmptyNode(){
            return this.isEmpty;
        }

        public boolean isLeafNode(){
            return this.isLeaf;
        }

        public byte[] getSHA(){
            return this.SHA256;
        }
    }

    class LeafNode extends Node {
        
        private String str;
        private byte[] SHA256;        
        private boolean isLeaf;
        private boolean isEmpty;
        
        public LeafNode(String str) throws NoSuchAlgorithmException{
            this.str = str;
            this.SHA256 = getSHA(str);
            this.isLeaf = true;
            this.isEmpty = false;
        }


        public boolean isLeafNode(){
            return this.isLeaf;
        }

        public boolean isEmptyNode(){
            return false;
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
        private boolean isEmpty;

        public boolean isLeafNode(){
            return this.isLeaf;
        }

        public InnerNode(Node leftChild, Node rightChild) throws NoSuchAlgorithmException{
            this.leftChild = leftChild;
            // this.leftChildLabel = leftChild.getSHAString();
            this.rightChild = rightChild;
            // this.rightChildLabel = rightChild.getSHAString();
            this.isLeaf = false;
            this.isEmpty = false;
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

        //Used for testing invalid hash 
        public void setSHA(byte[] newSHA){
            this.SHA256 = newSHA;
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
        //System.out.println("Creating tree with strings: " + keys.toString());
        //ArrayList<String> addedToList = new ArrayList<>();
        boolean oddLeafs = false;
        int n = keys.size();
        if(n%2 != 0){
            oddLeafs = true;
            n = n-1;
        } 
        
        int i = 0;
        System.out.println("Generating tree with: " + keys.size() + " keys");
        //Generate first level of inner nodes from the leaf nodes
        for(i = 0; i < n-1; i+=2){
            LeafNode l = new LeafNode(keys.get(i));
            LeafNode r = new LeafNode(keys.get(i+1));
            // addedToList.add(keys.get(i));
            // addedToList.add(keys.get(i+1));
            InnerNode parent = new InnerNode(l, r);
            q.addLast(parent);
        } //Case with odd leaf nodes, we use the last one twice
        if(oddLeafs){
            LeafNode l = new LeafNode(keys.get(n));
            EmptyNode r = new EmptyNode();
            InnerNode parent = new InnerNode(l, r);
            q.addLast(parent);
        }

        System.out.println("Added " + q.size() + " leaf nodes");


        //Continue taking children and adding parent at higher level until we reach a single root node
        while(q.size() != 1){
            boolean isOdd = false;
            if(q.size() % 2 != 0 && q.size() != 0){
                n = q.size()-1;
                isOdd = true;
                System.out.println("Odd tree level, level size: " + q.size());
            } else {
                n = q.size();
            }
            LinkedList<InnerNode> tempList = new LinkedList<>();
            for(i = 0; i < q.size()-1; i+=2){
                tempList.add(new InnerNode(q.get(i), q.get(i+1)));
            }
            if(isOdd && q.size() != 0){
                tempList.add(new InnerNode(q.get(n), new EmptyNode()));
            }
            q = tempList;
        }
        System.out.println("Root SHA: " + q.get(0).getSHAString());

        //Return root node
        return q.get(0);
    }

    //Finds the maximum string value in a node subtree
    public String findMax(InnerNode node){

        while(!node.getRightChild().isLeafNode()){
            //System.out.println("Empty Node: " + node.getRightChild().isEmptyNode());
            if(node.getRightChild().isEmptyNode() && !node.getLeftChild().isLeafNode()){
                System.out.println("Encounteded empty node");
                node = (InnerNode)node.getLeftChild();
            } else if(node.getRightChild().isEmptyNode() && node.getLeftChild().isLeafNode()){
                //Case where left child is leafNode but odd strings so right child is empty
                return ((LeafNode)node.getLeftChild()).getString();
            }
            else {
                node = (InnerNode)node.getRightChild();
            }
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
            if(!curr.getLeftChild().isLeafNode()  && !curr.getLeftChild().isEmptyNode()){
                q.addFirst((InnerNode)curr.getLeftChild());
                String edgeLabel = findMax((InnerNode)curr.getLeftChild());
                curr.setLeftLabel(edgeLabel);

            } else {
                String edgeLabel = ((LeafNode)curr.getLeftChild()).getString();
                curr.setLeftLabel(edgeLabel);

            }
            if(!curr.getRightChild().isLeafNode() && !curr.getRightChild().isEmptyNode()){
                q.addFirst((InnerNode)curr.getRightChild());
                String edgeLabel = findMin((InnerNode)curr.getRightChild());
                curr.setRightLabel(edgeLabel);

            } else {
                if(!curr.getRightChild().isEmptyNode()){
                    String edgeLabel = ((LeafNode)curr.getRightChild()).getString();
                    curr.setRightLabel(edgeLabel);
                }


            }
            
        }
        

        }

    //Traverses search itteratively with BFS to get nodes in print order 
    //If statements in order to cast InnerNode to LeafNode 
    public String printTree(InnerNode node, String fileName){
        String retString = "";
        boolean justString = false;
        if(fileName == null){
            justString = true;
        }

        StringBuilder outString = new StringBuilder();
        try {

            //Generate output file name
            PrintWriter out = null;
            if(!justString){
                String[] nameParts = fileName.split("\\.");
                fileName = nameParts[0] + ".out.txt";
                fileName = fileName.replace("/", "");
                out = new PrintWriter(new FileWriter(fileName, true), true);
            }
             
        
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
                    boolean rightIsEmpty = false;
                    if(curr.getRightChild().isEmptyNode()) rightIsEmpty = true;
                    InnerNode leftChild = (InnerNode)curr.getLeftChild();
                    InnerNode rightChild = null;
                    String printStr = "";
                    if(!rightIsEmpty) {
                        rightChild = (InnerNode)curr.getRightChild();
                        printStr = Integer.toString(p)+ "\n"  + Integer.toString((2*p)) + "\n" + curr.getLeftChildLabel() + "\n" + curr.getSHAString() + "\n" +  curr.getRightChildLabel() + "\n" + Integer.toString((2*p+1)) + "\n\n";
                    } else {
                        printStr = Integer.toString(p)+ "\n"  + Integer.toString((2*p)) + "\n" + curr.getLeftChildLabel() + "\n" + curr.getSHAString() + "\n" +  "Empty Node Label" + "\n" + Integer.toString((2*p+1)) + "\n\n";
                    }
                    if(!justString) out.write(printStr);
                    outString.append(printStr);
                    q.addFirst((InnerNode)curr.getLeftChild());
                    if(!rightIsEmpty) q.addFirst((InnerNode)curr.getRightChild());
                } else {
                    boolean rightIsEmpty = false;
                    if(curr.getRightChild().isEmptyNode()) rightIsEmpty = true;
                    LeafNode leftChild = (LeafNode)curr.getLeftChild();
                    LeafNode rightChild = null;
                    String printStr = "";
                    if(!rightIsEmpty){
                        rightChild = (LeafNode)curr.getRightChild();
                        printStr = Integer.toString(p)+ "\n"  + Integer.toString((2*p)) + "\n" + curr.getLeftChildLabel() + "\n" + curr.getSHAString() + "\n" + curr.getRightChildLabel() + "\n" + Integer.toString((2*p+1)) + "\n\n";
                    } else {
                        printStr = Integer.toString(p)+ "\n"  + Integer.toString((2*p)) + "\n" + curr.getLeftChildLabel() + "\n" + curr.getSHAString() + "\n" + "Empty Node Label" + "\n" + Integer.toString((2*p+1)) + "\n\n";
                    }
                    if(!justString) out.write(printStr);
                    outString.append(printStr);
                    q_leafs.add((LeafNode)curr.getLeftChild());
                    if(!rightIsEmpty) q_leafs.add((LeafNode)curr.getRightChild());

                }
            }

            for(LeafNode curr: q_leafs){
                p++;
                String printStr = Integer.toString(p) + "\n" + curr.getString() + "\n" + curr.getSHAString() + "\n\n";
                if(!justString) out.write(printStr);
                outString.append(printStr);
            }
            if(!justString) {
                retString = out.toString();
                out.close();
            }
            return outString.toString();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return outString.toString();
        
    }

    public static void main(String[] args) {
        
    

     
    }
}

package cse297;
import java.io.*;  
import java.util.*;
import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;
import java.lang.Cloneable;
import java.lang.Integer; 
import java.nio.file.Files;
import java.nio.file.Paths;

import cse297.Block;
import cse297.Tree.*;


public class Validator implements java.io.Serializable {
    
    Map<String,Block> indexStructure;
    public Validator(){

    }

    public String toHexString(byte[] hash)  { 
        BigInteger number = new BigInteger(1, hash);  
        StringBuilder hexString = new StringBuilder(number.toString(16));  
        while (hexString.length() < 32)  
            hexString.insert(0, '0');   
        return hexString.toString();  
    } 

    public byte[] getSHAFromNodes(byte[] one, byte[] two) throws NoSuchAlgorithmException {  
        ByteArrayOutputStream outputStream = null;
        MessageDigest md = null;
        byte[] jointHash = null;
        try{
            outputStream = new ByteArrayOutputStream();
            outputStream.write(one);
            outputStream.write(two);
            jointHash = outputStream.toByteArray();
            md = MessageDigest.getInstance("SHA-256");   
        } catch(Exception e){
            e.printStackTrace();  
        }
        
        return md.digest(jointHash);  
    } 

    public byte[] getSHA(String input) throws NoSuchAlgorithmException {  
        MessageDigest  md = MessageDigest.getInstance("SHA-256");  
        return md.digest(input.getBytes(StandardCharsets.UTF_8));  
    } 

    // Validates each block in the block chain and calculates the hash of the previous block and compares it to the value stored for previous hash 
    // Verifies the correctness of each blocks Merkle tree 
    public boolean validateBlockChain(ArrayList<Block> blockChain) throws NoSuchAlgorithmException{
        boolean valid = true;
        if(blockChain.size() == 0) return false;
        Block block = blockChain.get(0);
        if(!block.getPrevHash().equals("0")  || !(block.getRootNode().getSHAString().equals(block.getRootHash())) || !validateBlock(block)){
            return false;
        }
        for(int i = 1; i < blockChain.size(); i++){
            block = blockChain.get(i);
            if (!block.getPrevHash().equals(blockChain.get(i-1).calculateBlockHash())  || !(block.getRootNode().getSHAString().equals(block.getRootHash())) || !validateBlock(block)) {
                return false;
            }
        }
       return true;
    }

    // Validates the blocks merkle tree by calling the recurssive checkMerkleRoot method to 
    // ensure that the root hash can be calculated by recussively hashing all the sibbling nodes
    // off the merkel tree.
    public boolean validateBlock(Block block) throws NoSuchAlgorithmException {

        boolean rootValid = this.checkMerkleRoot(block.getRootNode());
        return rootValid;

    }

    public boolean checkMerkleRootHelper(InnerNode node) throws NoSuchAlgorithmException {

        Node leftChild = node.getLeftChild();
        Node rightChild = node.getRightChild();
        if(!leftChild.isLeafNode() && !rightChild.isLeafNode()){
            byte[] currentSHA = node.getSHA();
            byte[] childrenSHA = getSHAFromNodes(leftChild.getSHA(), rightChild.getSHA());
            return Arrays.equals(currentSHA, childrenSHA) && checkMerkleRootHelper((InnerNode)leftChild) && checkMerkleRootHelper((InnerNode)leftChild);
        } else if(leftChild.isLeafNode() && !rightChild.isLeafNode()){ //Left child is leafNode
            byte[] currentSHA = node.getSHA();
            byte[] childrenSHA = getSHAFromNodes(leftChild.getSHA(), rightChild.getSHA());
            return Arrays.equals(currentSHA, childrenSHA) && checkMerkleRootHelper((InnerNode)leftChild);
        } else if(!leftChild.isLeafNode() && rightChild.isLeafNode()){ //Right child is the leafNode
            byte[] currentSHA = node.getSHA();
            byte[] childrenSHA = getSHAFromNodes(leftChild.getSHA(), rightChild.getSHA());
            return Arrays.equals(currentSHA, childrenSHA) && checkMerkleRootHelper((InnerNode)rightChild);
        } else {  //BOTH LEAF NODES
            if(leftChild == null || rightChild == null) return false;
            byte[] currentSHA = node.getSHA();
            byte[] childrenSHA = getSHAFromNodes(leftChild.getSHA(), rightChild.getSHA());
            return Arrays.equals(currentSHA, childrenSHA);
        }

        // return false;
    }

    public boolean checkMerkleRoot(Node root) throws NoSuchAlgorithmException {
        InnerNode leftChild = (InnerNode)root.getLeftChild();
        InnerNode rightChild = (InnerNode)root.getRightChild();
        if(root.getLeftChild() != null && root.getRightChild() != null){
            byte[] currentSHA = root.getSHA();
            byte[] childrenSHA = getSHAFromNodes(leftChild.getSHA(), rightChild.getSHA());
            String currSHAStr = new String(currentSHA, StandardCharsets.UTF_8);
            String childSHAStr = new String(childrenSHA, StandardCharsets.UTF_8);
            return Arrays.equals(currentSHA, childrenSHA) && checkMerkleRootHelper(leftChild) && checkMerkleRootHelper(leftChild);
        }
        return false;
    }


    
    // Uses method in block class to change the root merkle has to one which is invalid 
    public void generateBadBlockchain(ArrayList<Block> BadBlockChain) throws NoSuchAlgorithmException{
        String s = "rdlkhregtht34t";
        for (int i = 0; i < BadBlockChain.size();i++){
            BadBlockChain.get(i).setRootHash(s+i);
        }        
    }


    //Uses HashMap to map all transaction strings to the most recent block in which they occur 
    //Returns this HashMap as the indexing structure
    public Map<String,Block> generateIndexStructure(ArrayList<Block> blocks){
        Map<String,Block> map = new HashMap<String, Block>();
        for(Block b: blocks){
            List<List<String>> blockInfo = b.getTransactions(b);
            for(List<String> stringAndHash: blockInfo){
                map.put(stringAndHash.get(0), b);
                // System.out.println(stringAndHash.get(0));
            }
        }
        this.indexStructure = map;
        return map;
    }

    //When adding a block to the blockchain, updates the index structure to make sure that the transaction Strings
    //map to the most recently added block.
    public void updateIndexStructure(Block block){
        List<List<String>> blockInfo = block.getTransactions(block);
        for(List<String> stringAndHash: blockInfo){
            this.indexStructure.put(stringAndHash.get(0), block);
        }
    }


    //inchain method returns the Merkle Proof as an array of SHA-256 byte arrays
    //If the tree does not contain verifiable path the membership result will print out "false" 
    public ArrayList<byte[]> inchain(String string, ArrayList<Block> blockChain, boolean inChain) throws NoSuchAlgorithmException{
        
        Block block = this.indexStructure.get(string);
        ArrayList<byte[]> path = locateTransaction(string, block);
        boolean result = verifyTransactionPath(path);
        System.out.println("Proof of membership result: " + result);
        return path;
    }

    //Validates the result of the Merkle Proof, recalculating the hashes to ensure the result matches the merkel root 
    //This validates that the path from the root to the leaf node as well as all their sibblings hash to same value as tree root
    public boolean verifyTransactionPath(ArrayList<byte[]> path) throws NoSuchAlgorithmException{

        for(int i = 0; i < path.size() -2; i+=2){

            byte[] sibblingHASH = getSHAFromNodes(path.get(i+1), path.get(i));
            if(!Arrays.equals(path.get(i+2), sibblingHASH)) {
                return false;
            }
        }
        return true;
    }


    //Traverses the Merkle Tree to find the path from root to the leaf node of the transaction
    //Returns an array of the Hashes as well as each sibbling node.
    public ArrayList<byte[]> locateTransaction(String string, Block b){
        InnerNode root = b.getRootNode();
        ArrayList<byte[]> path = new ArrayList<>();

        if(root == null) return path;

        InnerNode curr = root;
        path.add(curr.getSHA());
        while(!curr.isLeafNode()){
            
            //Base Case
            if(curr.getLeftChild().isLeafNode() && curr.getRightChild().isLeafNode()){
                String lString = ((LeafNode)curr.getLeftChild()).getString();
                String rString = ((LeafNode)curr.getRightChild()).getString();
                if(string.equals(lString)){
                    path.add(curr.getRightChild().getSHA());
                    //This is target we found
                    path.add(curr.getLeftChild().getSHA());
                } else if(string.equals(rString)){
                    path.add(curr.getLeftChild().getSHA());
                    //This is target we found
                    path.add(curr.getRightChild().getSHA());

                }
                break;


            } else if(curr.getLeftChild().isLeafNode() && curr.getRightChild().isEmptyNode()){
                String lString = ((LeafNode)curr.getLeftChild()).getString();
                if(string.equals(lString)){
                    path.add(curr.getRightChild().getSHA());
                    path.add(curr.getLeftChild().getSHA());

                }
                break;
            } 

            String lLabel = curr.getLeftChildLabel();
            if(string.compareTo(lLabel) > 0) { //String is greater than left label, traverse right side of tree
                if(!curr.getRightChild().isEmptyNode()){
                    path.add(curr.getLeftChild().getSHA());
                    path.add(curr.getRightChild().getSHA());
                    curr = (InnerNode)curr.getRightChild();
                    
                } else {
                    path.add(curr.getRightChild().getSHA());
                    path.add(curr.getLeftChild().getSHA());
                    curr = (InnerNode)curr.getLeftChild();
                    
                }
            } else{
                path.add(curr.getRightChild().getSHA());
                path.add(curr.getLeftChild().getSHA());
                curr = (InnerNode)curr.getLeftChild();
                
            }
        }

        
        Collections.reverse(path);
        return path;

    }



    




    public static void main (String[] args) throws NoSuchAlgorithmException{
        // System.out.println("Test");

        FileInputStream fis = null;
        String fileName;
        ArrayList<Block> blockChain = new ArrayList<>();;
        Validator validate = new Validator();
        try {
            
            Scanner myObj = new Scanner(System.in);
            System.out.println("\nPlease enter file of serialized blockchain");
            fileName = myObj.nextLine();
            fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            blockChain = (ArrayList<Block>)ois.readObject();
            ois.close();
            fis.close();
        } catch(Exception e) {
            e.printStackTrace();
        }

        boolean result = false;
        boolean stringResult = false;
        ArrayList<byte[]> hashProof = null;
        String toVerify = "";
        validate.generateIndexStructure(blockChain);
        result = validate.validateBlockChain(blockChain);
        
        System.out.println("\n### Validating Blockchain\n");

        System.out.println("Blockchain Verified: " + result);

        toVerify = "zulr6clwo7d1if8aylw6";
        System.out.println("Verifying Transaction String: "  + toVerify);
        hashProof = validate.inchain("zulr6clwo7d1if8aylw6", blockChain, true);
        System.out.println("Hash List Proof of Membership");
        for(byte[] hash: hashProof) System.out.print(validate.toHexString(hash) + ", ");
        
        System.out.println("\n\n### Invalidating Blockchain");

        System.out.println("\n### Validating Invalidated Blockchain\n");

        validate.generateBadBlockchain(blockChain);
        result = validate.validateBlockChain(blockChain);

        System.out.println("Blockchain Verified: " + result);

        toVerify = "zulr6clwo7d1if8aylw6";
        System.out.println("Verifying Transaction String: "  + toVerify);
        hashProof = validate.inchain("zulr6clwo7d1if8aylw6", blockChain, true);
        System.out.println("Hash List Proof of Membership");
        for(byte[] hash: hashProof) System.out.print(validate.toHexString(hash) + ", ");
        System.out.println("\n");


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

    public String getPrevHash(){
        return this.prevHash;
    }

    //Used for testing invalid blocks 
    public void setRootHash(String newHash) throws NoSuchAlgorithmException{
        this.root.setSHA(getSHA(newHash));
        this.rootHash = toHexString(this.root.getSHA());
    }


    public String getHash(){
        return this.rootHash;
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
        int attemptedNonce = rand.nextInt();
        String byteString = attemptedNonce + this.rootHash;
        byte[] guess = getSHA(byteString);
        BigInteger guessNumber = new BigInteger(guess);
        BigInteger targetNumber = new BigInteger(target);
        do{ 
            attemptedNonce = rand.nextInt();
            byteString = attemptedNonce + this.rootHash;
            guess = getSHA(byteString); 
            guessNumber = new BigInteger(guess);
            System.out.println("Found nonce: " + attemptedNonce);
        } while(guessNumber.compareTo(targetNumber) == 1); 
        this.nonce = attemptedNonce;
        long time=System.currentTimeMillis()/1000;
        this.timeStamp = (int)time;
        return true;
    }
    

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException 
    {  
        // Static getInstance method is called with hashing SHA  
        MessageDigest md = MessageDigest.getInstance("SHA-256");  
        return md.digest(input.getBytes(StandardCharsets.UTF_8));  
    } 

    

    //Calcualtes hash of contents of block 
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
        System.out.println("Error shouldnt get to here");
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
        // System.out.println(blocks);

        for(Block b : blocks){
            // System.out.println(b);
            String retString = "";
            InnerNode node = b.getRootNode();
            try {
                String inputFileName = b.getFileName();
                String treeOutput = b.getTree().printTree(node, null);

                // System.out.println(inputFileName);
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
                writer.write(treeOutput);

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

    public List<List<String>> getTransactions(Block block){
        // System.out.println(blocks);
        List<List<String>> leafTransactionStrings = new ArrayList<List<String>>();
        InnerNode node = block.getRootNode();

        LinkedList<InnerNode> q = new LinkedList<>();
        ArrayList<LeafNode> q_leafs = new ArrayList<>();

        int p = 0;
        if (node == null) {
            return leafTransactionStrings;
        }
        
        q.add(node);
        while (!q.isEmpty()) {
            InnerNode curr = (InnerNode)q.pollLast();
            if(!curr.getLeftChild().isLeafNode() && !curr.getRightChild().isLeafNode()){
                boolean rightIsEmpty = false;
                InnerNode rightChild = null;
                if(curr.getRightChild().isEmptyNode()){
                    rightIsEmpty = true;
                }
                InnerNode leftChild = (InnerNode)curr.getLeftChild();
                if(!rightIsEmpty){
                    rightChild = (InnerNode)curr.getRightChild();
                    q.addFirst((InnerNode)curr.getRightChild());
                } 
                q.addFirst((InnerNode)curr.getLeftChild());
            
            } else if(!curr.getLeftChild().isLeafNode() && curr.getRightChild().isEmptyNode()){
                InnerNode leftChild = (InnerNode)curr.getLeftChild();
                q.addFirst(leftChild);
            } else if(curr.getLeftChild().isLeafNode() && curr.getRightChild().isEmptyNode()){
                LeafNode leftChild = (LeafNode)curr.getLeftChild();
                q_leafs.add(leftChild);
            } 
            else {
                LeafNode leftChild = (LeafNode)curr.getLeftChild();
                LeafNode rightChild = (LeafNode)curr.getRightChild();
                q_leafs.add(rightChild);
                q_leafs.add(leftChild);

            }    
                                
        }
        ArrayList<String> leafStrings = new ArrayList<>();
        for(LeafNode curr: q_leafs){
            ArrayList<String> stringAndHash = new ArrayList<>();
            stringAndHash.add(curr.getString());
            leafStrings.add(curr.getString());
            // System.out.println(stringAndHash.add(curr.getString()));
            stringAndHash.add(curr.getSHAString());
            leafTransactionStrings.add(stringAndHash);
        }
        // System.out.println("Leaf Strings:" + leafStrings.toString());   


        return leafTransactionStrings;
    }

    public static void main(String[] args) {
        // System.out.println("Test");
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
            System.out.println("Storing: " + fileNames.length +" files");
            if(fileNames.length > 0) blocks.add(0, new Block(zero.toString(), zero.toString(), firstTarget, 10, fileNames[0]));
            for(int i = 1; i < fileNames.length; i++){
                blocks.add(new Block(blocks.get(i-1).calculateBlockHash() , zero.toString(), firstTarget, 10, fileNames[i]));

            }
            for (int i = 0; i < blocks.size(); i ++) { 

            }
            // System.out.println(blocks);
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
