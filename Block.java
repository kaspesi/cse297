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
            } else {
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
        System.out.println("Leaf Strings:" + leafStrings.toString());   


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
