package cse297;
import java.io.*;  
import java.util.*;
import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;
import cse297.Tree.*;

public class Block{

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
        this.nonce = rand.nextInt();
        String byteString = this.nonce + this.rootHash;
        byte[] guess = getSHA(byteString);
        System.out.println("Mining Attempt");
        BigInteger guessNumber = new BigInteger(guess);
        BigInteger targetNumber = new BigInteger(target);
        while(guessNumber.compareTo(targetNumber) == 1){ 
            this.nonce = rand.nextInt();
            byteString = nonce + this.rootHash;
            guess = getSHA(byteString); 
            guessNumber = new BigInteger(guess);
            System.out.println("Mining Attempt");
            System.out.println("Guess: " + guessNumber.toString());
        } 
        System.out.println("Target" + targetNumber.toString());
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
            //String retval = new String(md.digest(input.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
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
        for(Block b : blocks){
            System.out.println("BEGINING BLOCK");
            System.out.println("BEGIN HEADER");
            String[] headerInfo = b.getHeaderInfo();
            System.out.println(headerInfo[0] + "\n" + headerInfo[1] + "\n" + headerInfo[2] + "\n" + headerInfo[3] + "\n" + headerInfo[4]);
            System.out.println("BEGIN HEADER");
            InnerNode root = b.getRootNode();
            if(printTree) b.getTree().printTree(root, b.getFileName());
            System.out.println("END BLOCK\n");
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
                // String prevHash = blocks.get(i-1).getRootHash();
                blocks.add(i, new Block(blocks.get(i-1).calculateBlockHash() , zero.toString(), firstTarget, 10, fileNames[i]));
            }

            b.printBlocks(blocks, false);
           
        } catch(Exception e){
            e.printStackTrace();
        }
            
    }
}
