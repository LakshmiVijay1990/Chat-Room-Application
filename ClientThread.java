/*
 * Author : Lakshmi Vijay
 * ID : 1001576841
 * References : https://www.youtube.com/watch?v=Uo5DY546rKY&t=445s
 *              http://net-informations.com/java/net/socket.htm
 *              http://net-informations.com/java/net/multithreaded.htm
 */
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/*
 * This class to handle thread for each
 * client
 */
public class ClientThread implements Runnable
{
	static Scanner inStream;
	static PrintWriter outStream;
	static Socket socket;
	Socket SOCK;
	Scanner INPUT;
	
	PrintWriter OUT;
	/**
	 * @wbp.nonvisual location=51,111
	 */
	

/*
 * Constructor which takes the connected client
 * socket as parameter.
 */
	public ClientThread(Socket clientSocket) 
	{
		this.SOCK=clientSocket;
	}
	

	public void run() 
	{
	 try {
		try 
		{	//INPUT takes the input from the input stream of socket.
			INPUT=new Scanner(this.SOCK.getInputStream());
			OUT=new PrintWriter(SOCK.getOutputStream());
			OUT.flush();
			CheckStream();
		} 
		finally
		{
			SOCK.close();
		}
	 }
	 catch (IOException e1) 
	 {
		e1.printStackTrace();
	 }
	}
	
	public void CheckStream()
	{
		while(true)
		{
			Receive();
		}
	}
	
	/*
	 * Checks whether INPUT has any token. If Yes,
	 * it will echo to all the other clients.
	 */
	public void Receive()
	{
		if(INPUT.hasNext())
		{
			String Message = INPUT.nextLine();
			if(Message.contains("#?!"))
			{
				String Temp1 = Message.substring(3);
				Temp1 = Temp1.replace("[", "");
				String[] CurrentUsers = Temp1.split(",");
				
			}
			else
			{
				ClientUI.ClientTxtDisplay.append(Message+"\n");
			}
		}
	}
			
				
		
	
}
