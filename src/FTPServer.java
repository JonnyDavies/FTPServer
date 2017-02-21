

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FTPServer extends Thread {
		
    private static final int NTHREADS = 6;
    private static final Executor exec = Executors.newFixedThreadPool(NTHREADS);    
    private String started;
    	
	public FTPServer() 
	{
	      this.started = "done";
	}
	
	 public void startListening(int portNumber)
	 {
	      boolean listening = true;

	      try (ServerSocket serverSocket = new ServerSocket(portNumber)) 
	      { 
	              
	        while (listening) 
	        {            
	           Socket connection = serverSocket.accept();
	           
	           FTPHandleRequest ftpr = new FTPHandleRequest(connection);                    
	         
	           Runnable task = new Runnable(){
	             public void run (){
	            	 ftpr.init();
	             }
	           }; 

	           exec.execute(task);                 
	           	           
	        }
	      }        
	      catch (IOException e) {
	          System.err.println("Could not listen on port " + portNumber);
	          System.exit(-1);
	      }
	 }
	 
	public static void main(String[] args) 
	{
		FTPServer fs = new FTPServer();
		fs.startListening(4446);        
	}

}
