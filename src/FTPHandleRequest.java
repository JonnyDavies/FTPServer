import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;

import javafx.scene.control.TreeItem;

public class FTPHandleRequest 
{
	
	    private Socket socket = null;

	    
	    private DataInputStream dis;
	    private FileOutputStream fos;
	    private DataOutputStream dos;
	    private String password;
	    
	    private ArrayList<String> remoteFiles;

	    public FTPHandleRequest(Socket socket) 
	    {     
	      this.socket = socket;
	      
	      try
	      {
			  this.dos = new DataOutputStream(socket.getOutputStream());
			  this.dis = new DataInputStream(socket.getInputStream());
			  this.remoteFiles = new ArrayList<String>();
			  this.password = "CTEC3604";

	      }
	      catch(Exception e)
	      {
	    	  e.printStackTrace();
	      }
	    }
	    
	  
	    
	    public void init()
	    {
	    	String action = "";
			boolean socketOpen = true;

	    	
	    	  // lets try having the loop here 
	    			/** if we can hang here **/ 
  	
			try 
			{
				
				while (socketOpen) 
				{
					
		                System.out.println("Waiting for command");
						

						action = this.dis.readUTF();
						System.out.println(action);		
						
					    if(action.compareTo("Password")== 0)
			            {
					    	isPasswordValid();
			                continue;
			            }
						
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
			            
			            if(action.compareTo("Refresh")== 0)
			            {
			            	refreshDirectory();
			                continue;
			            }
			            
			            if(action.compareTo("Additional")== 0)
			            {
			            	houseKeeping();
			            	socketOpen = false;
			                continue;
			            }
				}
								
			} 
			catch (IOException e1) 
			{
				e1.printStackTrace();
			} 	
	    }
	    
	    public void isPasswordValid()
	    {
	    	try 
	    	{
				String password = dis.readUTF();
				
				if(password.equals(this.password))
				{
					this.dos.writeUTF("ACCEPTED");
				}
				else
				{
					this.dos.writeUTF("REJECTED");
				}
			
	    	} 
	    	catch (IOException e) 
	    	{
				e.printStackTrace();
			}
	    }
	    
	    public void houseKeeping()
	    { 
	    	 try
    	     {
    		    System.out.println("Closing down . . .");
				this.dos.close();
	 		    this.dis.close();
	 		    this.socket.close();
			 } 
	    	 catch (IOException e) 
    	     {
				e.printStackTrace();
			 }	    	
	    }   
	    
	    public void saveFile()
		{
	    	try 
			{ 
	            String filename = dis.readUTF();
	            
	            // makes this directory configurable !!!!!!!!!!!!!
	    		this.fos = new FileOutputStream("C:\\Users\\Jonny\\Desktop\\RemoteFiles\\" + filename);

	    		int ch;	                
	            String temp;
	                
	                do
	                {
	                    temp = this.dis.readUTF();
	                    
	                    ch = Integer.parseInt(temp);
	                    
	                    if(ch!=-1)
	                    {
	                        this.fos.write(ch);                    
	                    }
	                    
	                }
	                while(ch!=-1);
	                
	                this.fos.close();
		    	
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
				// get file name
				String file = this.dis.readUTF();
				FileInputStream fis = new FileInputStream("C:\\Users\\Jonny\\Desktop\\RemoteFiles\\" + file);	
			
				this.dos.writeUTF("READY");

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
	   
	    public void refreshDirectory()
	    {
	    	
			try
			{    						
				// read the directories from the file 
				this.populateTree();				
				this.dos.writeUTF("READY");				
				this.dos.writeInt(remoteFiles.size());
				
				for(int i = 0; i < remoteFiles.size(); i++)	
				{
					this.dos.writeUTF(remoteFiles.get(i));
				}
               
				remoteFiles.clear();
				
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}				
	    	
	    }
  
		public void populateTree()
		{			
			remoteFiles = new ArrayList<String>();
			
			TreeItem<Path> treeItem = new TreeItem<Path>();
	        treeItem.setValue(Paths.get("C:/Users/Jonny/Desktop/RemoteFiles"));
	        remoteFiles.add(treeItem.getValue().toString());

	        try {
				createTree(treeItem);
			} catch (IOException e) {
				e.printStackTrace();
			}		    
		}
		
	   public void createTree(TreeItem<Path> rootItem) throws IOException 
	    {
	        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootItem.getValue())) 
	        {
	            for (Path path : directoryStream) 
	            {
	                remoteFiles.add(path.getFileName().toString());
	 
	            	TreeItem<Path> newItem = new TreeItem<Path>(path);
	                newItem.setExpanded(true);	                
	                rootItem.getChildren().add(newItem);
	                if (Files.isDirectory(path)) 
	                {
	                    createTree(newItem);
	                }
	            }
	        }
	   }
}
