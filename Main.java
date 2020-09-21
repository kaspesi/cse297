import java.io.*;  
import java.util.*;
import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  


//Sources Cited:
//https://www.geeksforgeeks.org/sha-256-hash-in-java/



public class Main {



    class Node {

        private String str;
        private byte[] SHA256;
        public String getSHAString(){
            return toHexString(SHA256);
        };
        public byte[] getSHA(){
            return SHA256;
        };

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
        
        private String str;
        private byte[] SHA256;
        
        public LeafNode(String str) throws NoSuchAlgorithmException{
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


    class InnerNode extends Node {
        private Node leftChild;
        private String leftChildLabel;
        private Node rightChild;
        private String rightChildLabel;
        private byte[] SHA256;


        public InnerNode(Node leftChild, Node rightChild) throws NoSuchAlgorithmException{
            this.leftChild = leftChild;
            this.leftChildLabel = leftChild.getSHAString();
            this.rightChild = rightChild;
            this.rightChildLabel = rightChild.getSHAString();
            this.SHA256 = getSHAChildren(leftChild, rightChild);
        }

        public byte[] getSHA(){
            return this.SHA256;
        }

        public Node getLeftChild(){
            return this.leftChild;
        }

        public Node getRightChild(){
            return this.rightChild();
        }

        public String getLeftChildLabel(){
            return this.leftChildLabel;
        }

        public String getRightChildLabel(){
            return this.rightChildLabel;
        }

        public byte[] getSHAChildren(Node one, Node two) throws NoSuchAlgorithmException {  
            ByteArrayOutputStream outputStream = null;
            MessageDigest md = null;
            byte[] jointHash = null;
            byte[] oneHash = null;
            byte[] twoHash = null;
            try{
                oneHash = one.getSHA();
                twoHash = two.getSHA();
                outputStream = new ByteArrayOutputStream();
                outputStream.write(oneHash);
                outputStream.write(twoHash);
                jointHash = outputStream.toByteArray();
                md = MessageDigest.getInstance("SHA-256");   
            } catch(Exception e){
                e.printStackTrace();  
            }
            
            return md.digest(jointHash);  
        } 

    }

    public static String formatFileName(String fileName){
        if(fileName.contains(".txt")){
            return "/" + fileName;
        } else {
            return "/" + fileName + ".txt";

        }
    }


    public Node generateMerkleTree(ArrayList<String> keys){

        

    }

    public static void main(String[] args) {
        
        String fileName = ""; 
        ArrayList<String> strings = new ArrayList<>();
        FileInputStream fis = null;
        BufferedReader reader = null;
        File file = null;
        String currentDirectory = null;


        try{
            Scanner myObj = new Scanner(System.in); 
            System.out.println("Please enter input file name");
            fileName = formatFileName(myObj.nextLine()); 
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