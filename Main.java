import java.io.*;  
import java.util.*;
import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  





public class Main {

    //https://www.geeksforgeeks.org/sha-256-hash-in-java/


    class Node {
        public byte[] getSHA(String input) throws NoSuchAlgorithmException {  
            MessageDigest  md = MessageDigest.getInstance("SHA-256");  ; 
            return md.digest(input.getBytes(StandardCharsets.UTF_8));  
        } 
        
        public String toHexString(byte[] hash)  { 
            BigInteger number = new BigInteger(1, hash);  
            StringBuilder hexString = new StringBuilder(number.toString(16));  
            while (hexString.length() < 32)  
                hexString.insert(0, '0');   
            return hexString.toString();  
        } 
    }

    class LeafNode extends Node {
        
        String str;
        byte[] SHA256;
        
        LeafNode(String str) throws NoSuchAlgorithmException{
            this.str = str;
            this.SHA256 = getSHA(str);
        }
        

        public String getString(){
            return this.str;
        }

        public byte[] getSHA(){
            return this.SHA256;
        }

        public String getSHAString(){
            return toHexString(this.SHA256);
        }

    }

    public static String formatFileName(String fileName){
        if(fileName.contains(".txt")){
            return "/" + fileName;
        } else {
            return "/" + fileName + ".txt";

        }
    }

    public static void main(String[] args) {
        
        String fileName = ""; 
        ArrayList<String> strings = new ArrayList<>();

        try{
            Scanner myObj = new Scanner(System.in); 
            System.out.println("Please enter input file name");
        
            fileName = formatFileName(myObj.nextLine()); 
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        FileInputStream fis = null;
        BufferedReader reader = null;
        File file = null;
        String currentDirectory = null;

        try  
        {  
            currentDirectory = System.getProperty("user.dir");
            file=new File(currentDirectory + fileName);   
            fis=new FileInputStream(file);    
            reader = new BufferedReader(new InputStreamReader(fis));
            System.out.println("file content: ");  
            
            String line = reader.readLine();
            while(line != null){
                strings.add(line);
                line = reader.readLine();
            }   
            Collections.sort(strings);

        }
        catch(Exception e)  
        {  
            e.printStackTrace();  
        }  

        for(String s: strings){
            System.out.println(s);
        }
     
    }
}