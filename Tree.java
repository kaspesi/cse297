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
            HashSet<Integer> emptyNodes = new HashSet<>();
            int emptyNodesAbove = 0;
            int p = 0;
            if (node == null) {
                return " ";
            }
            q.add(node);
            while (!q.isEmpty()) {
                p++;
                if(emptyNodes.contains(p)) {
                    String printStr = "";
                    printStr = Integer.toString(p)+ "\nEmpty Node\n\n";
                    System.out.println(printStr);
                    if(!justString) out.write(printStr);
                    outString.append(printStr);
                    emptyNodesAbove++;
                    continue;
                }
                InnerNode curr = (InnerNode)q.pollLast();
                if(!curr.getLeftChild().isLeafNode() && !curr.getRightChild().isLeafNode()){
                    boolean rightIsEmpty = false;
                    if(curr.getRightChild().isEmptyNode()) rightIsEmpty = true;
                    InnerNode leftChild = (InnerNode)curr.getLeftChild();
                    InnerNode rightChild = null;
                    String printStr = "";
                    if(!rightIsEmpty) {
                        rightChild = (InnerNode)curr.getRightChild();
                        printStr = Integer.toString(p)+ "\n"  + Integer.toString((2*p - 2*emptyNodesAbove)) + "\n" + curr.getLeftChildLabel() + "\n" + curr.getSHAString() + "\n" +  curr.getRightChildLabel() + "\n" + Integer.toString((2*p+1-2*emptyNodesAbove)) + "\n\n";
                    } else {
                        printStr = Integer.toString(p)+ "\n"  + Integer.toString((2*p-2*emptyNodesAbove)) + "\n" + curr.getLeftChildLabel() + "\n" + curr.getSHAString() + "\n" +  "Empty Node Label" + "\n" + Integer.toString((2*p+1 - 2*emptyNodesAbove)) + "\n\n";
                        emptyNodes.add(2*p+1 - 2*emptyNodesAbove);
                    }
                    System.out.println(printStr);
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
                        printStr = Integer.toString(p)+ "\n"  + Integer.toString((2*p - 2*emptyNodesAbove)) + "\n" + curr.getLeftChildLabel() + "\n" + curr.getSHAString() + "\n" + curr.getRightChildLabel() + "\n" + Integer.toString((2*p+1 - 2*emptyNodesAbove)) + "\n\n";
                    } else {
                        printStr = Integer.toString(p)+ "\n"  + Integer.toString((2*p - 2*emptyNodesAbove)) + "\n" + curr.getLeftChildLabel() + "\n" + curr.getSHAString() + "\n" + "Empty Node Label" + "\n" + Integer.toString(2*p+1 - 2*emptyNodesAbove) + "\n\n";
                        emptyNodes.add(2*p+1 - 2*emptyNodesAbove);
                    }
                    System.out.println(printStr);
                    if(!justString) out.write(printStr);
                    outString.append(printStr);
                    q_leafs.add((LeafNode)curr.getLeftChild());
                    if(!rightIsEmpty) q_leafs.add((LeafNode)curr.getRightChild());

                }
            }

            for(LeafNode curr: q_leafs){
                p++;
                if(emptyNodes.contains(p)) { 
                    String printStr = "";
                    printStr = Integer.toString(p)+ "\nEmpty Node\n\n";
                    System.out.println(printStr);
                    if(!justString) out.write(printStr);
                    outString.append(printStr);
                    p++;
                }
                String printStr = Integer.toString(p) + "\n" + curr.getString() + "\n" + curr.getSHAString() + "\n\n";
                if(!justString) out.write(printStr);
                outString.append(printStr);
                System.out.println(printStr);
            }
            //If the last leaf node is an empty node
            p++;
            if(emptyNodes.contains(p)) { 
                String printStr = "";
                printStr = Integer.toString(p)+ "\nEmpty Node\n\n";
                System.out.println(printStr);
                if(!justString) out.write(printStr);
                outString.append(printStr);
                p++;
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
        //If try catch fails
        return "";
        
    }

    public static void main(String[] args) {
        
    

     
    }
}