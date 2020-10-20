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
    
    public Validator(){

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
            System.out.println("LeftNode TreeNode? " + leftChild.isLeafNode());
            System.out.println("RightNode TreeNode? " + rightChild.isLeafNode());
            byte[] currentSHA = node.getSHA();
            byte[] childrenSHA = getSHAFromNodes(leftChild.getSHA(), rightChild.getSHA());
            return Arrays.equals(currentSHA, childrenSHA) && checkMerkleRootHelper((InnerNode)leftChild);
        } else if(!leftChild.isLeafNode() && rightChild.isLeafNode()){ //Right child is the leafNode
            System.out.println("LeftNode TreeNode? " + leftChild.isLeafNode());
            System.out.println("RightNode TreeNode? " + rightChild.isLeafNode());
            byte[] currentSHA = node.getSHA();
            byte[] childrenSHA = getSHAFromNodes(leftChild.getSHA(), rightChild.getSHA());
            return Arrays.equals(currentSHA, childrenSHA) && checkMerkleRootHelper((InnerNode)rightChild);
        } else {  //BOTH LEAF NODES
            if(leftChild == null || rightChild == null) return false;
            System.out.println("LeftNode TreeNode? " + leftChild.isLeafNode());
            System.out.println("RightNode TreeNode? " + rightChild.isLeafNode());
            byte[] currentSHA = node.getSHA();
            byte[] childrenSHA = getSHAFromNodes(leftChild.getSHA(), rightChild.getSHA());
            return Arrays.equals(currentSHA, childrenSHA);
        }

        return false;
    }

    public boolean checkMerkleRoot(Node root) throws NoSuchAlgorithmException {
        InnerNode leftChild = (InnerNode)root.getLeftChild();
        InnerNode rightChild = (InnerNode)root.getRightChild();
        if(root.getLeftChild() != null && root.getRightChild() != null){
            byte[] currentSHA = root.getSHA();
            byte[] childrenSHA = getSHAFromNodes(leftChild.getSHA(), rightChild.getSHA());
            return Arrays.equals(currentSHA, childrenSHA) && checkMerkleRootHelper(leftChild) && checkMerkleRootHelper(leftChild);
        }
        return false;
    }

    public static boolean inchain(String string, ArrayList<Block> blockChain){
        
        for (int i = 0; i < blockChain.size(); i++){
            System.out.println("Searching in Root Hash: " + blockChain.get(i).getRootHash());
            blockSearch(string,blockChain.get(i));
        }
        return true;

    }

    public static boolean blockSearch(String string, Block block){
        System.out.println("Searching for term: " + string);
        System.out.println("Tree: " + block.getTree());


            
        return true;

    }

    public static ArrayList<Block> generateBadBlockchain(ArrayList<Block> BadBlockChain){
        
        for (int i = 0; i < BadBlockChain.size();i++){
            System.out.println("BBC " + (i+1) + " " + BadBlockChain.get(i));

        }
        return BadBlockChain;
        
    }


    public static void main (String[] args){
        // System.out.println("Test");
        FileInputStream fis = null;
        String fileName;
        ArrayList<Block> blocks;
        Validator validate = new Validator();
        try {
            Scanner myObj = new Scanner(System.in);
            System.out.println("Please enter file of serialized blockchain");
            fileName = myObj.nextLine();
            fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            blocks = (ArrayList<Block>)ois.readObject();
            System.out.println("\nDeserialized Data:\n");
            for(int i = 0; i < blocks.size();i++){
                System.out.println("Block " + (i) + ": " + blocks.get(i));  
                boolean valid = validate.validateBlock(blocks.get(i));
                System.out.println("Block result: " + valid);
                System.out.println();
            }
            System.out.println();
            String string  = "T7SCG4jK0PbC7iwB7oVe";
            ArrayList<Block> badBlockchain = new ArrayList<Block>();
            badBlockchain = (ArrayList<Block>)blocks.clone();

            generateBadBlockchain(badBlockchain);

            // inchain(string, blocks);
            

            ois.close();
            fis.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
