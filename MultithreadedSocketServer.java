
/* Author : Lakshmi Vijay
 * Student ID : 1001576841
 * References : https://www.youtube.com/watch?v=Uo5DY546rKY&t=445s
 *              http://net-informations.com/java/net/socket.htm
 *              http://net-informations.com/java/net/multithreaded.htm
 */
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/*
 * This class contains creation of GUI variables.
 * Also contains array list of the connected client sockets and
 * a Map with Integer and String as key to store the connected 
 * client number and User Name.
 */
public class MultithreadedSocketServer extends JFrame
{
	public static ArrayList<Socket> connectionArray = new ArrayList<Socket>();
	public static Map<Integer,String> currentUsers = new HashMap<Integer,String>();
	public  static JTextArea ClientTxtDisplay= new JTextArea();
	public static JTextArea clientConnectedList;
	public static ArrayList<String> clientMessageInList=new ArrayList<String>();

	MultithreadedSocketServer(){
		getContentPane().setLayout(null);
		setBackground(Color.DARK_GRAY);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblClientMessages = new JLabel("Client Messages");
		getContentPane().add(lblClientMessages);
		ClientTxtDisplay.setRows(10);
		ClientTxtDisplay.setColumns(10);
		getContentPane().add(ClientTxtDisplay);

		JLabel lblConnectedClients = new JLabel("Connected Clients");
		getContentPane().add(lblConnectedClients);

		clientConnectedList = new JTextArea();
		clientConnectedList.setRows(10);
		clientConnectedList.setColumns(10);
		getContentPane().add(clientConnectedList);
		setVisible(true);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	/*
	 * Server Starts in main() and listens to port 8888.
	 */
	public static void main(String[] args) throws Exception 
	{

		try
		{
			JFrame serverWindow= new MultithreadedSocketServer();
			ServerSocket server=new ServerSocket(8888);
			//counter to keep track of the number of active clients
			int counter=0;
			System.out.println("Server Started ....");
			
			serverWindow.addWindowListener(new WindowListener() 
			{

				@Override
				public void windowOpened(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowIconified(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowDeiconified(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void windowDeactivated(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}
				/* On closing Server GUI window, the messages sent from clients
				 * are stored to a text file.
				 */
				@Override
				public void windowClosing(WindowEvent arg0) {
					
					try 
					{
						FileWriter fWriter = new FileWriter("MessageFile.txt", true);
						BufferedWriter out = new BufferedWriter(fWriter);
						System.out.println(clientMessageInList);
						for(String s:clientMessageInList) {
							out.write(s+"\n");
						}
						out.close();
						fWriter.close();
					}catch (Exception e) {
						
						e.printStackTrace();
					}



				}
				
				@Override
				public void windowClosed(WindowEvent arg0) 
				{
					// TODO Auto-generated method stub
				}

				@Override
				public void windowActivated(WindowEvent arg0) {
					// TODO Auto-generated method stub

				}
			});

			while(true)
			{
				counter++;
				//server accept the client connection request
				Socket serverClient=server.accept(); 
				//The connected client added to array list of sockets
				connectionArray.add(serverClient);
				/*
				 * Function AddUserName called to add user name to
				the map(list) of connected clients.
				Parameters sent : connected current client socket and the client number
				*/
				AddUserName(serverClient,counter);
				clientConnectedList.setText("");
				for (Integer s:currentUsers.keySet()) {
					clientConnectedList.append(currentUsers.get(s)+"\n");

				}
				/*
				 * Sends request to a separate thread by calling ChatServer and passing
				 * parameters to its Constructor. 
				 * Parameters are the Socket serverClient,Client number, and the array list of
				 * connected client sockets and the Map of current user names
				 */
				ChatServer sct = new ChatServer(serverClient,counter,connectionArray,currentUsers); 
				Thread clientThread = new Thread(sct);
				clientThread.start();


			}}
		catch(Exception e)
		{
			//e.printStackTrace();
			System.out.println(e);
		}
	}
/*
 * Function to add user name of connected clients to the list
 */
	public static void AddUserName (Socket X,int counter) throws IOException
	{
		Scanner Input = new Scanner(X.getInputStream());
		String UserName = Input.nextLine();
		currentUsers.put(counter,UserName);
		System.out.println("User connected - "+UserName);
		for(int i=1;i<=MultithreadedSocketServer.connectionArray.size();i++)
		{
			Socket TEMP_SOCK =(Socket) MultithreadedSocketServer.connectionArray.get(i-1);
			PrintWriter OUT = new PrintWriter(TEMP_SOCK.getOutputStream());
			OUT.println("#?!"+currentUsers);
			OUT.flush();
		}
	}
}
/*
 * ChatServer class handles the client request independent of
 *  other incoming requests using multiple threads. 
 */
class ChatServer implements Runnable
{
	Socket serverClient;
	int clientNo;
	ArrayList<Socket> connectionArray ;
	Map<Integer,String> currentUsers ;
	/*
	 * Constructor ChatServer() takes in parameters
	 */
	ChatServer(Socket inSocket,int counter,ArrayList<Socket> connectionArray, Map<Integer,String> currentUsers) 
	{
		serverClient = inSocket;
		clientNo=counter;
		this.connectionArray = connectionArray;
		this.currentUsers = currentUsers;
	}
	public void run()
	{
		try {
			Scanner inStream = new Scanner(serverClient.getInputStream());

			String clientMessage="", serverMessage="";

			while(true)
			{
				if(inStream.hasNext()==false) {
					return;
				}
				clientMessage=inStream.nextLine();
				//System.out.println(clientMessage);
				serverMessage=currentUsers.get(clientNo) + " sends-   " + clientMessage ;
				MultithreadedSocketServer.ClientTxtDisplay.append(serverMessage+"\n");
				MultithreadedSocketServer.clientMessageInList.add(serverMessage);
				for(int i=0;i<connectionArray.size();i++) 
				{
					Socket temp = (Socket)connectionArray.get(i);
					PrintWriter outStream = new PrintWriter(temp.getOutputStream());
					outStream.println(serverMessage);
					outStream.flush();
				}

			}

		}
		catch (Exception e) {

			e.printStackTrace();
		} 
		/*
		 * When a client exits by closing it's GUI window, server shows 
		 * Client exited and removes from the Array List of Current Users
		 */
		finally
		{
			System.out.println("Client " + clientNo + " EXITS!! ");
			String exitedUser= currentUsers.get(clientNo);
			currentUsers.remove(clientNo, exitedUser);

			refreshList();

		}

	}
	/*
	 * Function to remove the exited client from the GUI Display box
	 */
	public void refreshList() {
		MultithreadedSocketServer.clientConnectedList.setText("");
		for (Integer s:currentUsers.keySet()) {
			MultithreadedSocketServer.clientConnectedList.append(currentUsers.get(s)+"\n");

		}
	}


}