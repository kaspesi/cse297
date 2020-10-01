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

public class Block{

    private String prevHash;
    private String rootHash;
    private int timeStamp;
    private byte[] target;
    private int nonce;
    private InnerNode root; 
    private String fileName; 
    
    public Block (String prevHash, String rootHash, byte[] target, int nonce, String fileName) throws NoSuchAlgorithmException {
        this.prevHash = prevHash;
        this.rootHash = rootHash;
        this.fileName = fileName;
        this.target = target;
        this.nonce = nonce;
        long time=System.currentTimeMillis()/1000;
        this.timeStamp = (int)time;
        Tree mTree = new Tree(fileName);
        this.root = mTree.getRoot();
        this.mineBlock();
    }

    public Block() {}

    
    public boolean mineBlock() throws NoSuchAlgorithmException{
        Random rand = new Random();
        nonce = rand.nextInt();
        String byteString = nonce + rootHash;
        byte[] guess = getSHA(byteString);
        System.out.println("Mining Attempt");
        BigInteger guessNumber = new BigInteger(guess);
        BigInteger targetNumber = new BigInteger(target);
        while(guessNumber.compareTo(targetNumber) == 1){ 
            this.nonce = rand.nextInt();
            byteString = nonce + rootHash;
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
            String retval = new String(md.digest(input.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
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
                blocks.add(0, new Block(zero.toString(), zero.toString(), firstTarget, 10, fileNames[i]));
            }
           
        } catch(Exception e){
            e.printStackTrace();
        }
            
    }
}
