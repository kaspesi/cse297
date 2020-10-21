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
// import jdk.internal.jimage.ImageReader.Node;

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
        // byte[] oneHash = null;
        // byte[] twoHash = null;
        try{
            // oneHash = one.getSHA();
            // twoHash = two.getSHA();
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
        MessageDigest  md = MessageDigest.getInstance("SHA-256");  ; 
        return md.digest(input.getBytes(StandardCharsets.UTF_8));  
    } 


    public boolean validateBlockChain(ArrayList<Block> blockChain) throws NoSuchAlgorithmException{
       // serializedBlocks
        boolean valid = true;
        int c = 0;
        do {
            Block block = blockChain.get(c);
            System.out.println("Checking Block: " + block);
            if(block.getPrevHash().equals("0")  && (block.getRootNode().getSHAString().equals(block.getRootHash()))){
                System.out.println(block + " is valid");
                System.out.println();
                c++;
            } else if (block.getPrevHash().equals(blockChain.get(c-1).calculateBlockHash())  && (block.getRootNode().getSHAString().equals(block.getRootHash()))){
                System.out.println(block + " is valid");
                System.out.println();
                c++;
            } else {
                System.out.println("Check 1");
                System.out.println("block.getPrevHash(): \t\t\t\t" + block.getPrevHash());
                System.out.println("blockChain.get(c-1).calculateBlockHash(): \t" + blockChain.get(c-1).calculateBlockHash());
                System.out.println("Check 2");
                System.out.println("block.getRootNode().getSHAString(): \t\t" + block.getRootNode().getSHAString());
                System.out.println("block.getRootHash: \t\t\t\t" + block.getRootHash());
                System.out.println(block + " is not valid");
                System.out.println();
                return false;
            }
        }while(c < blockChain.size());

       return valid;
       
    }

    public boolean validateBlock(Block block) throws NoSuchAlgorithmException {

        System.out.println("Root hash: " + block.getRootHash());

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
            // System.out.println("LeftNode TreeNode? " + leftChild.isLeafNode());
            // System.out.println("RightNode TreeNode? " + rightChild.isLeafNode());
            byte[] currentSHA = node.getSHA();
            byte[] childrenSHA = getSHAFromNodes(leftChild.getSHA(), rightChild.getSHA());
            return Arrays.equals(currentSHA, childrenSHA) && checkMerkleRootHelper((InnerNode)leftChild);
        } else if(!leftChild.isLeafNode() && rightChild.isLeafNode()){ //Right child is the leafNode
            // System.out.println("LeftNode TreeNode? " + leftChild.isLeafNode());
            // System.out.println("RightNode TreeNode? " + rightChild.isLeafNode());
            byte[] currentSHA = node.getSHA();
            byte[] childrenSHA = getSHAFromNodes(leftChild.getSHA(), rightChild.getSHA());
            return Arrays.equals(currentSHA, childrenSHA) && checkMerkleRootHelper((InnerNode)rightChild);
        } else {  //BOTH LEAF NODES
            if(leftChild == null || rightChild == null) return false;
            // System.out.println("LeftNode TreeNode? " + leftChild.isLeafNode());
            // System.out.println("RightNode TreeNode? " + rightChild.isLeafNode());
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
            System.out.println("RootSHA: " + currSHAStr);
            System.out.println("ChildrenSHA: " + childSHAStr);
            return Arrays.equals(currentSHA, childrenSHA) && checkMerkleRootHelper(leftChild) && checkMerkleRootHelper(leftChild);
        }
        return false;
    }


    

    public static void generateBadBlockchain(ArrayList<Block> BadBlockChain) throws NoSuchAlgorithmException{
        String s = "rdlkhregtht34t";
        System.out.println("Generating Bad Blockchain\n");
        for (int i = 0; i < BadBlockChain.size();i++){
            System.out.println("Old Hash: " + BadBlockChain.get(i).getRootHash());
            BadBlockChain.get(i).setRootHash(s+i);
            System.out.println("New Hash: " + BadBlockChain.get(i).getRootHash());
        }
        System.out.println();
        
        try{
            FileOutputStream fos = new FileOutputStream("badBlocks");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(BadBlockChain);
            oos.close();
            fos.close();
        }  catch(Exception e) {
            e.printStackTrace();
        }
        //return BadBlockChain;
        
    }


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

    //When adding a block to structure
    public void updateIndexStructure(Block block){
        List<List<String>> blockInfo = block.getTransactions(block);
        for(List<String> stringAndHash: blockInfo){
            this.indexStructure.put(stringAndHash.get(0), block);
        }
    }

    public boolean inchain(String string, ArrayList<Block> blockChain, boolean inChain) throws NoSuchAlgorithmException{
        
        Block block = this.indexStructure.get(string);
        // List<List<String>> blockInfo = block.getTransactions(block);
        // for(List<String> stringAndHash: blockInfo){
        //     System.out.println(stringAndHash.get(0));
        // }
        ArrayList<byte[]> path = locateTransaction(string, block);
        boolean result = verifyTransactionPath(path);
        System.out.println("Verifying Transaction Path Result :" + result);

        // System.out.println(block.getRootHash());

        return inChain;
    }

    public boolean verifyTransactionPath(ArrayList<byte[]> path) throws NoSuchAlgorithmException{
        System.out.println(path.size());
        for(byte[] hash: path){
            System.out.println(toHexString(hash));
        }
        System.out.println("\n\n");
        for(int i = 0; i < path.size() -2; i+=2){
            System.out.println(toHexString(path.get(i)));
            System.out.println(toHexString(path.get(i+1)));

            byte[] sibblingHASH = getSHAFromNodes(path.get(i+1), path.get(i));
            // byte[] sibblingHASH2 = getSHAFromNodes(path.get(i), path.get(i+1));

            // if(!Arrays.equals(path.get(i+2), sibblingHASH) && !Arrays.equals(path.get(i+2), sibblingHASH2)) {
            if(!Arrays.equals(path.get(i+2), sibblingHASH)) {
                System.out.println("\n\n"+toHexString(path.get(i)));
                System.out.println(toHexString(path.get(i+1)));
                System.out.println(toHexString(sibblingHASH));
                // System.out.println(toHexString(sibblingHASH2));

                System.out.println("not equal to:");
                System.out.println(toHexString(path.get(i+2)));
                return false;
            }
            System.out.println(toHexString(sibblingHASH));
            System.out.println(toHexString(path.get(i+2)));
            System.out.println("\n\n");

        }

        return true;
    }


    public ArrayList<byte[]> locateTransaction(String string, Block b){
        InnerNode root = b.getRootNode();
        ArrayList<byte[]> path = new ArrayList<>();

        if(root == null) return path;

        // Stack<InnerNode> stack = new Stack<InnerNode>();
        InnerNode curr = root;
        path.add(curr.getSHA());
        while(!curr.isLeafNode()){
            
            //Base Case
            if(curr.getLeftChild().isLeafNode() && curr.getRightChild().isLeafNode()){
                String lString = ((LeafNode)curr.getLeftChild()).getString();
                String rString = ((LeafNode)curr.getRightChild()).getString();
                if(string.equals(lString)){
                    System.out.println("Checking for target" + string);
                    System.out.println("Found target: " + lString + " as left leafNode");
                    path.add(curr.getRightChild().getSHA());
                    //This is target we found
                    path.add(curr.getLeftChild().getSHA());
                } else if(string.equals(rString)){
                    System.out.println("Found target: " + rString + " as right leafNode");
                    path.add(curr.getLeftChild().getSHA());
                    //This is target we found
                    path.add(curr.getRightChild().getSHA());

                }
                break;


            } else if(curr.getLeftChild().isLeafNode() && curr.getRightChild().isEmptyNode()){
                String lString = ((LeafNode)curr.getLeftChild()).getString();
                if(string.equals(lString)){
                    System.out.println("Checking for target" + string);
                    System.out.println("Found target: " + lString + " as left leafNode");
                    path.add(curr.getRightChild().getSHA());
                    //This is target we found
                    path.add(curr.getLeftChild().getSHA());

                }
                break;
            } 

            String lLabel = curr.getLeftChildLabel();
            // String rLabel = curr.getRightChildLabel();
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
                
                // System.out.println("Exiting traverse");
                // return "";
            }
        }

        // System.out.println(path.toString());
        
        Collections.reverse(path);
        return path;

    }



    




    public static void main (String[] args) throws NoSuchAlgorithmException{
        // System.out.println("Test");

        FileInputStream fis = null;
        String fileName;
        ArrayList<Block> blocks = new ArrayList<>();;
        Validator validate = new Validator();
        try {
            
            Scanner myObj = new Scanner(System.in);
            System.out.println("Please enter file of serialized blockchain");
            fileName = myObj.nextLine();
            fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            blocks = (ArrayList<Block>)ois.readObject();
            // generateBadBlockchain(blocks);
            System.out.println("\nDeserialized Data:\n");
            for(int i = 0; i < blocks.size();i++){
                System.out.println("Block " + (i) + ": " + blocks.get(i));  
                //boolean valid = validate.validateBlock(blocks.get(i));
                System.out.println("Block result: " + valid);
                System.out.println();
            }
            System.out.println();
            // ArrayList<Block> badBlockchain = new ArrayList<Block>();
            // badBlockchain = (ArrayList<Block>)blocks.clone();

            // generateBadBlockchain(badBlockchain);

            

            ois.close();
            fis.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        validate.validateBlockChain(blocks);
        validate.generateIndexStructure(blocks);
        validate.validateBlockChain(blocks);
        validate.inchain("zulr6clwo7d1if8aylw6", blocks, true);

    }

}
