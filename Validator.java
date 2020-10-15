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

public class Validator implements java.io.Serializable {
    
    public static void main (String[] args){
        System.out.println("Test");
        FileInputStream fis = null;
        BufferedReader reader = null;
        File file = null;
        String currentDirectory = null;
        String fileName;
        ArrayList<Block> blocks = new ArrayList<>();
        try {
            Scanner myObj = new Scanner(System.in);
            System.out.println("Please enter file of Serialized blockchain");
            fileName = myObj.nextLine();


            fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj = ois.readObject();
            System.out.println(obj);
            ois.close();
            fis.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
