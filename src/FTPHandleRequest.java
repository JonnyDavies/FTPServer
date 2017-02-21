import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Random;

public class FTPHandleRequest 
{
	
	    private Socket socket = null;
	    private int version = 1;
	    
	    private DataInputStream dis;
	    private FileOutputStream fos;
	    private DataOutputStream dos;

	    public FTPHandleRequest(Socket socket) 
	    {     
	      this.socket = socket;
	      
	      try
	      {
			  this.dos = new DataOutputStream(socket.getOutputStream());
			  this.dis = new DataInputStream(socket.getInputStream());

	      }
	      catch(Exception e)
	      {
	    	  e.printStackTrace();
	      }
	    }
	    
	  
	    
	    public void init()
	    {
	    	String action = "";
	    	
	    	
	    	  // lets try having the loop here 
	    			/** if we can hang here **/ 
  	
			try 
			{
				
				while (true) 
				{
					
		                System.out.println("Waiting for command");
						
						
						action = this.dis.readUTF();
						System.out.println(action);		
						
						
			            if(action.compareTo("Upload")== 0)
			            {
			            	saveFile();
			                continue;
			            }
			            
			            if(action.compareTo("Download")== 0)
			            {
			            	sendFile();
			                continue;
			            }

				}
								
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
			}
				
	
	    	
	    }
	    
	    
	    public void saveFile()
		{
	    	try 
			{ 
		    	Random rdm = new Random();
		    	this.fos = new FileOutputStream("testfile" + rdm.nextInt(100) + ".jpg");
		    	
		    	
	                
	                int ch;
	                
	                String temp;
	                
	                do
	                {
	                    temp = this.dis.readUTF();
	                    
	                    System.out.println(temp);
	                    
	                    ch = Integer.parseInt(temp);
	                    
	                    if(ch!=-1)
	                    {
	                        this.fos.write(ch);                    
	                    }
	                    
	                }
	                while(ch!=-1);
	                
	                this.fos.close();
		    	
				//this.socket.shutdownInput();
		}
	    	catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	    
	    
	    
	    
	    
	    
	    
	    
	    public void sendFile()
	    {
	    	
			try
			{    	
				
				this.dos.writeUTF("READY");
								
				FileInputStream fis = new FileInputStream("C:\\Users\\Jonny\\Desktop\\horse.jpg");	
				
				
			    int ch;
	            
	            do
	            {
	                ch = fis.read();
	                this.dos.writeUTF(String.valueOf(ch));
	            }
	            
	            while(ch!=-1);  
	            	            
				fis.close();

			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}				
	    	
	    }
	    
	    
	    


}
