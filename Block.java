import java.io.*;  
import java.util.*;
import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;
import java.lang.Integer; 


public class Block{


    public static void main(String[] args) {

        String fileNamesInput = ""; 
        String[] fileNames = null;
        ArrayList<String> strings = new ArrayList<>();
        FileInputStream fis = null;
        BufferedReader reader = null;
        File file = null;
        String currentDirectory = null;
        Tree obj = new Tree();

        try{

            //Get user input 
            Scanner myObj = new Scanner(System.in); 
            System.out.println("Please the file names u dumb bing bong");
            fileName = formatFileName(myObj.nextLine()); 
            currentDirectory = System.getProperty("user.dir");
            file=new File(currentDirectory + fileName);   
            fis=new FileInputStream(file);    
            reader = new BufferedReader(new InputStreamReader(fis));
            
            String line = reader.readLine();
            while(line != null){
                strings.add(line);
                line = reader.readLine();
            }   
            Collections.sort(strings);
            InnerNode root = obj.generateMerkleTree(strings);
            obj.generatePatriciaEdges(root);
            obj.printTree(root, fileName);

        }
        catch(Exception e)  
        {  
            e.printStackTrace();  
        }  


    }
    
}