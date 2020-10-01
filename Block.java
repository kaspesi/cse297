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
import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;
import cse297.Tree.*;

public class Block{

    private String prevHash;
    private String rootHash;
    private int timestamp;
    private int target;
    private int nonce;
    
    public Block () {
        long time=System.currentTimeMillis()/1000;
        this.timestamp = (int)time;
    }

    public Block (Block block, String rootHash, int target, int nonce) {
        long time=System.currentTimeMillis()/1000;
        this.timestamp = (int)time;
    }

    
    public static boolean mineBlock(Block block) {
        Random rand = new Random();
        Block.nonce = rand.nextInt();
        String byteString = Block.nonce + Block.rootHash;
        byte[] guess = getSHA(byteString);
        System.out.println("Mining Attempt");
        while(Byte.compare(guess,Block.target) > 0){ 
            Block.nonce = rand.nextInt();
            byteString = Block.nonce + Block.rootHash;
            guess = getSHA(byteString); 
            System.out.println("Mining Attempt");
        } 
        return true;
    }
    

    public static byte[] getSHA(String input) throws NoSuchAlgorithmException 
    {  
        // Static getInstance method is called with hashing SHA  
        MessageDigest md = MessageDigest.getInstance("SHA-256");  
  
        // digest() method called  
        // to calculate message digest of an input  
        // and return array of byte 
        return md.digest(input.getBytes(StandardCharsets.UTF_8));  
    } 


    public String[] parseFileNames(String fileSequence) {
        String[] fileNames = fileSequence.split("\\.");
        return fileNames;
    }

    public static void main(String [] args){
        Block b = new Block();  
        FileInputStream fis = null;
        BufferedReader reader = null;
        File file = null;
        String currentDirectory = null;
        String[] fileNames;
        ArrayList<Block> blocks = new ArrayList<>();

        try{
            Scanner myObj = new Scanner(System.in); 
            System.out.println("Please enter file sequence");
            fileNames = b.parseFileNames(myObj.nextLine()); 
            blocks.add(0, new Block(0, 0, ))
            for(int i = 1; i < fileNames.length; i++){

            }
           
        } catch(Exception e){
            e.printStackTrace();
        }
            
    }
}
