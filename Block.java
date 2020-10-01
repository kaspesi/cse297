package cse297;
import java.io.*;  
import java.util.*;
import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;
import java.lang.Integer; 
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


    }
    
}