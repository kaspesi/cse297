import java.io.*;  
import java.util.*;
import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  


//Sources Cited:
//https://www.geeksforgeeks.org/sha-256-hash-in-java/



public class Tree {



    class Node {

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

    public static String formatFileName(String fileName){
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
            System.out.println("Odd keys");
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

        System.out.println("Queue is now of length: " + q.size());

        //Continue taking children and adding parent at higher level until we reach a single root node
        while(q.size() != 1){

            LinkedList<InnerNode> tempList = new LinkedList<>();
            for(i = 0; i < q.size()-1; i+=2){
                tempList.add(new InnerNode(q.get(i), q.get(i+1)));
            }
            q = tempList;
        }

        System.out.println("Queue is now of length: " + q.size());
        //Return root node
        return q.get(0);
    }

    public Node findMax(Node node){

        return null;
    }

    public void generatePatriciaEdges(InnerNode node){
        
        LinkedList<InnerNode> q = new LinkedList<>();
        if (node == null) {
            return;
        }

        q.add(node);
        while (!q.isEmpty()) {

            InnerNode curr = (InnerNode)q.pollLast();
            //System.out.println(curr.isLeafNode());
            if(!curr.getRightChild().isLeafNode()){
                q.addFirst((InnerNode)curr.getLeftChild());
                System.out.println("InnerNOde");

            } else {
                System.out.println("LeafNode");
            }
            if(!curr.getRightChild().isLeafNode()){
                q.addFirst((InnerNode)curr.getLeftChild());
                System.out.println("InnerNode");

            } else {
                System.out.println("LeafNode");
            }
            
        }
        

        }

    public String printTree(Node root){
        
        return "";
    }

    public static void main(String[] args) {
        
        String fileName = ""; 
        ArrayList<String> strings = new ArrayList<>();
        FileInputStream fis = null;
        BufferedReader reader = null;
        File file = null;
        String currentDirectory = null;
        Tree obj = new Tree();

        try{
            Scanner myObj = new Scanner(System.in); 
            System.out.println("Please enter input file name");
            fileName = formatFileName(myObj.nextLine()); 
            currentDirectory = System.getProperty("user.dir");
            file=new File(currentDirectory + fileName);   
            fis=new FileInputStream(file);    
            reader = new BufferedReader(new InputStreamReader(fis));
            System.out.println("file content: ");  
            
            String line = reader.readLine();
            while(line != null){
                strings.add(line);
                line = reader.readLine();
            }   
            Collections.sort(strings);
            InnerNode root = obj.generateMerkleTree(strings);
            System.out.println("Root HASH: " + root.getSHAString());
            obj.generatePatriciaEdges(root);

        }
        catch(Exception e)  
        {  
            e.printStackTrace();  
        }  

        for(String s: strings){
            System.out.println(s);
        }
     
    }
}