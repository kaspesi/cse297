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
    private int timeStamp;
    private byte[] target;
    private int nonce;
    
    public Block (String prevHash, String rootHash, int timeStamp, byte[] target, int nonce) {
        this.prevHash = prevHash;
        this.rootHash = rootHash;
        this.timeStamp = timeStamp;
        this.target = target;
        this.nonce = nonce;
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

    public String calculateBlockHash() {
        String stringTarget = new String(target, StandardCharsets.UTF_8);
        String input = prevHash + rootHash + Long.toString(timeStamp) + stringTarget + Integer.toString(nonce);
        MessageDigest md = null;
        byte[] bytes = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            String retval = new String(md.digest(input.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            return retval;
        } catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
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



        try{
            Scanner myObj = new Scanner(System.in); 
            System.out.println("Please enter file sequence");
            fileNames = b.parseFileNames(myObj.nextLine()); 
           
        } catch(Exception e){
            e.printStackTrace();
        }
            
    }
}
