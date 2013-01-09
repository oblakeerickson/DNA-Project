/**
 * ReadFile -- The file reader.
 * @author Janos Cserna, Blake Erickson, Andrew Gable
 * 12 December 2012
 */

import java.io.*;

public class ReadFile {

	public static void main(String[] args) {
		   
		    File file = new File("../" + args[0]);
		    
		    byte[] result = new byte[(int)file.length()];
		    try {
		      InputStream input = null;
		      try {
		        int totalBytesRead = 0;
		        input = new BufferedInputStream(new FileInputStream(file));
		        while(totalBytesRead < result.length){
		          int bytesRemaining = result.length - totalBytesRead;
		          //input.read() returns -1, 0, or more :
		          int bytesRead = input.read(result, totalBytesRead, bytesRemaining); 
		          if (bytesRead > 0){
		            totalBytesRead = totalBytesRead + bytesRead;
		          }
		         
		          long value = 0;
		          
		          
		          for (int j = 0; j < result.length; j+=8)
		          {
		        	  for (int i = j; i < j+8; i++)
		        	  {
		        		  value = (value << 8) + (result[i] & 0xff);
		        	  }
		        	  System.out.println(value);
		          }
		        
		        }
		        
		       
		      }
		      finally {
		       
		        input.close();
		      }
		    }
		    catch (FileNotFoundException ex) {
		      
		    }
		    catch (IOException ex) {
		      
		    }
		    

	}

}
