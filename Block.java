package cse297;
import java.io.*;  
import java.util.*;
import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;
import java.lang.Integer; 
<<<<<<< HEAD
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;

public class Block{

    private String prevHash;
    private String rootHash;
    private int timestamp;
    private int target;
    private int nonce;
    
    public Block (Block block, String rootHash, int target, int nonce) {
=======
import cse297.Tree.*;



public class Block{


    public static void main(String[] args) {

        String fileNamesInput = ""; 
        String[] fileNames = null;
        Tree test = new Tree("/test.txt");
        InnerNode root = test.getRoot();
        String test1 = test.printTree(root, "test.txt");
        System.out.println(test1);
        try{

            //Get user input 
            Scanner myObj = new Scanner(System.in); 
            System.out.println("Please enter the sequence of file names");
            fileNamesInput = myObj.nextLine(); 



        }
        catch(Exception e)  
        {  
            e.printStackTrace();  
        }  

>>>>>>> 968ab1431bdfdec08595e7fe13222025c6fba833

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
}