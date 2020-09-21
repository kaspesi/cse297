import java.io.*;  
import java.util.Scanner;  

public class Main {


    public static String formatFileName(String fileName){
        if(fileName.contains(".txt")){
            return "/" + fileName;
        } else {
            return "/" + fileName + ".txt";

        }
    }

    public static void main(String[] args) {
        
        String fileName = ""; 

        try{
            Scanner myObj = new Scanner(System.in); 
            System.out.println("Please enter input file name");
        
            fileName = formatFileName(myObj.nextLine()); 
            System.out.println("File is: " + fileName); 
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        try  
        {  
            String currentDirectory = System.getProperty("user.dir");
            File file=new File(currentDirectory + fileName);   
            FileInputStream fs=new FileInputStream(file);    
            System.out.println("file content: ");  
            
            int r=0;  
            while((r=fs.read())!=-1)  
            {  
                System.out.print((char)r);      //prints the content of the file  
            }  
            }  
        catch(Exception e)  
        {  
            e.printStackTrace();  
        }  
     
    }
}