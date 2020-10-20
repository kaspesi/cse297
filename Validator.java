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

import cse297.Block;
import cse297.Tree.*;

public class Validator implements java.io.Serializable {
    
    public static boolean validateBlock(Block block){

        System.out.println("Root hash: " + block.getRootHash());
        return true;

    }

    public static boolean inchain(String string, ArrayList<Block> blockChain){
        
        for (int i = 0; i < blockChain.size(); i++){
            System.out.println("Root Hash: " + blockChain.get(i).getRootHash());
        }
        return true;

    }


    public static void main (String[] args){
        // System.out.println("Test");
        FileInputStream fis = null;
        String fileName;
        ArrayList<Block> blocks = new ArrayList<>();
        try {
            Scanner myObj = new Scanner(System.in);
            System.out.println("Please enter file of serialized blockchain");
            fileName = myObj.nextLine();
            fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            blocks = (ArrayList<Block>)ois.readObject();
            System.out.println("\nDeserialized Data:\n");
            for(int i = 0; i < blocks.size();i++){
                System.out.println("Block " + (i+1) + ": " + blocks.get(i));  
                validateBlock(blocks.get(i));
                System.out.println();
            }
            System.out.println();
            String string  = "T7SCG4jK0PbC7iwB7oVe";
            inchain(string, blocks);

            ois.close();
            fis.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
