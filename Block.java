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

public class Block implements java.io.Serializable{

    private String prevHash;
    private String rootHash;
    private int timeStamp;
    private byte[] target;
    private int nonce;
    private InnerNode root; 
    private String fileName; 
    private Tree tree;
    
    
    public Block (String prevHash, String rootHash, byte[] target, int nonce, String fileName) throws NoSuchAlgorithmException {
        
        
        this.prevHash = prevHash;
        this.fileName = fileName;
        this.target = target;
        this.nonce = nonce;
        long time=System.currentTimeMillis()/1000;
        this.timeStamp = (int)time;
        this.tree = new Tree(fileName);
        this.root = this.tree.getRoot();
        this.rootHash = toHexString(this.root.getSHA());
        this.mineBlock();
    }

    public String[] getHeaderInfo(){
        String[] headerInfo = new String[5];
        headerInfo[0] = this.prevHash;
        headerInfo[1] = this.rootHash;
        headerInfo[2] = (new Integer(this.timeStamp)).toString();
        headerInfo[3] = toHexString(this.target);
        headerInfo[4] = (new Integer(this.nonce)).toString();
        return headerInfo;
    }
    

    public Block() {}

    public String getRootHash(){
        return this.rootHash;
    }

    public InnerNode getRootNode(){
        return this.root;
    }

    public String getFileName(){
        return this.fileName;
    }

    public Tree getTree(){
        return this.tree;
    }

    public String toHexString(byte[] hash)  { 
        BigInteger number = new BigInteger(1, hash);  
        StringBuilder hexString = new StringBuilder(number.toString(16));  
        while (hexString.length() < 32)  
            hexString.insert(0, '0');   
        return hexString.toString();  
    } 
    
    public boolean mineBlock() throws NoSuchAlgorithmException{
        Random rand = new Random();
        String byteString = this.nonce + this.rootHash;
        byte[] guess = getSHA(byteString);
        BigInteger guessNumber = new BigInteger(guess);
        BigInteger targetNumber = new BigInteger(target);
        do{ 
            this.nonce = rand.nextInt();
            byteString = nonce + this.rootHash;
            guess = getSHA(byteString); 
            guessNumber = new BigInteger(guess);
            System.out.println("Mining Attempt");
            System.out.println("Guess: " + guessNumber.toString());
        } while(guessNumber.compareTo(targetNumber) == 1); 
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
            //String retval = new String(md.digest(input.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            String retval = toHexString(md.digest(input.getBytes(StandardCharsets.UTF_8)));
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

    public String printBlocks(ArrayList<Block> blocks, boolean printTree){
        for(Block b : blocks){
            String retString = "";
            InnerNode node = b.getRootNode();
            try {
                String fileName = b.getFileName();
                String[] nameParts = fileName.split("\\.");
                fileName = nameParts[0] + ".block.out";
                fileName = fileName.replace("/", "");
                File myOut = new File(fileName);
                System.out.println();
                System.out.println(myOut.getName());
                System.out.println();
                BufferedWriter writer = new BufferedWriter(new FileWriter(myOut.getName()));
                System.out.println(myOut.createNewFile());
                if(!myOut.createNewFile()) {
                    writer.newLine();
                    writer.write("BEGIN BLOCK");
                    writer.newLine();
                    writer.write("BEGIN HEADER");
                    writer.newLine();
                    String[] headerInfo = b.getHeaderInfo();
                    writer.write(headerInfo[0]);
                    writer.newLine();
                    writer.write(headerInfo[1]);
                    writer.newLine();
                    writer.write(headerInfo[2]);
                    writer.newLine();
                    writer.write(headerInfo[3]);
                    writer.newLine();
                    writer.write(headerInfo[4]);
                    writer.newLine();
                    writer.write("END HEADER");
                    writer.newLine();
                    writer.newLine();

                    LinkedList<InnerNode> q = new LinkedList<>();
                    ArrayList<LeafNode> q_leafs = new ArrayList<>();

                    int p = 0;
                    if (node == null) {
                        return " ";
                    }
                    
                    q.add(node);
                    while (!q.isEmpty()) {
                        p++;
                        InnerNode curr = (InnerNode)q.pollLast();
                        if(!curr.getLeftChild().isLeafNode() && !curr.getRightChild().isLeafNode()){
                            InnerNode leftChild = (InnerNode)curr.getLeftChild();
                            InnerNode rightChild = (InnerNode)curr.getRightChild();
                            String printStr = Integer.toString(p)+ "\n"  + Integer.toString((2*p)) + "\n" + curr.getLeftChildLabel() + "\n" + curr.getSHAString() + "\n" +  curr.getRightChildLabel() + "\n" + Integer.toString((2*p+1)) + "\n\n";
                            System.out.println(printStr);
                            writer.write(Integer.toString(p));
                            writer.newLine();
                            writer.write(Integer.toString((2*p)));
                            writer.newLine();
                            writer.write(curr.getLeftChildLabel());
                            writer.newLine();
                            writer.write(curr.getLeftChildLabel());
                            writer.newLine();
                            writer.write(curr.getSHAString());
                            writer.newLine();
                            writer.write(curr.getRightChildLabel());
                            writer.newLine();
                            writer.write(Integer.toString((2*p+1)));
                            writer.newLine();
                            writer.newLine();
                            q.addFirst((InnerNode)curr.getLeftChild());
                            q.addFirst((InnerNode)curr.getRightChild());
                        } else {
                            LeafNode leftChild = (LeafNode)curr.getLeftChild();
                            LeafNode rightChild = (LeafNode)curr.getRightChild();
                            String printStr = Integer.toString(p)+ "\n"  + Integer.toString((2*p)) + "\n" + curr.getLeftChildLabel() + "\n" + curr.getSHAString() + "\n" + curr.getRightChildLabel() + "\n" + Integer.toString((2*p+1)) + "\n\n";
                            System.out.println(printStr);
                            writer.write(Integer.toString(p));
                            writer.newLine();
                            writer.write(Integer.toString((2*p)));
                            writer.newLine();
                            writer.write(curr.getLeftChildLabel());
                            writer.newLine();
                            writer.write(curr.getLeftChildLabel());
                            writer.newLine();
                            writer.write(curr.getSHAString());
                            writer.newLine();
                            writer.write(curr.getRightChildLabel());
                            writer.newLine();
                            writer.write(Integer.toString((2*p+1)));
                            writer.newLine();
                            writer.newLine();
                            q_leafs.add((LeafNode)curr.getLeftChild());
                            q_leafs.add((LeafNode)curr.getRightChild());
                        }                        
                    }

                    for(LeafNode curr: q_leafs){
                        p++;
                        String printStr = Integer.toString(p) + "\n" + curr.getString() + "\n" + curr.getSHAString() + "\n\n";
                        System.out.println(printStr);
                        writer.write(Integer.toString(p));
                        writer.newLine();
                        writer.write(curr.getString());
                        writer.newLine();
                        writer.write(curr.getSHAString());
                        writer.newLine();
                        writer.newLine();
                    }
                    writer.write("END BLOCK");
                    retString = writer.toString();
                    writer.close();
                    return " ";

                } else {
                    System.out.println("File created already exists.");
                }


            } catch (IOException e) {
                System.out.println("An error has occured creating out file.");
                e.printStackTrace();
            }
            
            System.out.println("BEGIN BLOCK");
            System.out.println("BEGIN HEADER");
            String[] headerInfo = b.getHeaderInfo();
            System.out.println(headerInfo[0] + "\n" + headerInfo[1] + "\n" + headerInfo[2] + "\n" + headerInfo[3] + "\n" + headerInfo[4]);
            System.out.println("END HEADER");
            InnerNode root = b.getRootNode();
            if(printTree) b.getTree().printTree(root, b.getFileName());
            System.out.println("END BLOCK\n");
            
            return retString;
            
      }

      return "";
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
                blocks.add(i, new Block(blocks.get(i-1).calculateBlockHash() , zero.toString(), firstTarget, 10, fileNames[i]));
            }
            b.printBlocks(blocks, false);
            FileOutputStream fos = new FileOutputStream("serializedBlocks");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(blocks);
            oos.close();
            fos.close();
           
        } catch(Exception e){
            e.printStackTrace();
        }
            
    }
}
