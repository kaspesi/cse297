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
