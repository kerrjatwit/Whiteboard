package Final;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.Scanner;

public class NetHandler {
	
	final public static String host = "localhost";
	
	Socket RoutingSocket;
	Socket ChatSocket;
	DataInputStream in;
	DataOutputStream out;
	Scanner chat;
	boolean run;
	String nextOutput = null;
	String prevOutput = null;
	String nextInput = null;
	Queue<String> inputs;
	

	public NetHandler() throws IOException 
	{
	
	ChatSocket = new Socket("10.200.246.25", 50001);
	
	System.out.println("Connection Successful");
	
	run = true;
	
	runClientThreads();
		
	}
	

	public void runClientThreads() {
		Thread input = new Thread(new Runnable() 
				{

					public void run() 
					{
						
						while(run)
						{
							try
							{
								in = new DataInputStream(ChatSocket.getInputStream());
								nextInput = in.readUTF();
								if(nextInput != null) {
									System.out.println(nextInput);
									inputs.add(nextInput);
								}
							}
							catch(IOException e)
							{
								e.printStackTrace();
							}
						}
						
					}
			
				});
		input.start();
		Thread output = new Thread(new Runnable() 
		{

			public void run() 
			{
				try 
				{
					out = new DataOutputStream(ChatSocket.getOutputStream());
				} 
				catch (IOException e1) 
				{
					e1.printStackTrace();
				}
				while(run)
				{
					//System.out.println(nextOutput);
					if(nextOutput != prevOutput) {
						System.out.println(nextOutput);
						if(nextOutput.compareTo("{quit}") == 0)
						{
							run = false;
							try 
							{
								closeConnection();
							} 
							catch (IOException e) 
							{
								e.printStackTrace();
							}
							
						} else {
							try 
							{
								out.writeUTF(nextOutput);
								System.out.println(nextOutput);
								prevOutput = nextOutput;
							} 
							catch (IOException e) 
							{
								e.printStackTrace();
							}
						}
					}
					
				}
			}
	
		});
		output.start();
	}

	public void closeConnection() throws IOException
	{
		out.close();
		in.close();
		ChatSocket.close();
	}
	
	public void send(String output) {
		nextOutput = output;
		
	}

	public static void main(String[] args) throws IOException {
		NetHandler client = new NetHandler();
	}
}