/*
 * Author : Lakshmi Vijay
 * ID : 1001576841
 * References : https://www.youtube.com/watch?v=Uo5DY546rKY&t=445s
 *              http://net-informations.com/java/net/socket.htm
 *              http://net-informations.com/java/net/multithreaded.htm
 */

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/* This class creates the UI for Clients
 * It creates variables for GUI and also the socket 
 * creation.
 * Threads for each client called from main()
 */
public class ClientUI extends JFrame
{
/*
 * Variables : userNameTxF : to enter user name
 *             ClientTxtDisplay : to display the client message
 *             clientMsgTxF     : to enter client message to be sent
 */
	private JTextField userNameTxF;
	public  static JTextArea ClientTxtDisplay= new JTextArea();
	private  JTextField clientMsgTxF ;
	private  JButton UNameBtn,sendBtn ;
	private static ClientThread chatClient;
	static Socket socket;
	static Scanner inStream;
	static PrintWriter outStream;
	public static String UserName ="";
	private JLabel ErrorTxtF =new JLabel();

	public static void main(String[] args )
	{
		try 
		{
			new ClientUI();
			//Creates a new socket and listens to port 8888.
			socket = new Socket("127.0.0.1",8888);
			/*Creates an object of class ClientThread and pass
			the socket to constructor of it.
			*/
			chatClient = new ClientThread(socket);
			outStream =new PrintWriter(socket.getOutputStream());
			inStream = new Scanner(socket.getInputStream());
			
			//Starts a new thread for each newly created client
			Thread X =new Thread(chatClient);
			X.start();

		} 
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}	
	public ClientUI() {
		getContentPane().setLayout(null);
		//setting layout and parameters of GUI
		userNameTxF = new JTextField();
		UNameBtn =  new JButton("Submit User Name");
		sendBtn = new JButton("Send To Server");
		setBackground(Color.DARK_GRAY);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		getContentPane().add(userNameTxF);
		userNameTxF.setColumns(10);
		getContentPane().add(UNameBtn);
		clientMsgTxF = new JTextField();
		getContentPane().add(clientMsgTxF);
		clientMsgTxF.setColumns(10);
		getContentPane().add(sendBtn);
		ClientTxtDisplay.setRows(10);
		ClientTxtDisplay.setColumns(10);
		getContentPane().add(ClientTxtDisplay);
		setVisible(true);
		pack();
		clientMsgTxF.setEnabled(false);
		sendBtn.setEnabled(false);
		
		ErrorTxtF = new JLabel("ErrorTextF");
		ErrorTxtF.setVerticalAlignment(SwingConstants.BOTTOM);
		getContentPane().add(ErrorTxtF);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		ErrorTxtF.setVisible(false);
		
	/*
	 *  To detect bad user names,ie. those which contain
	 *  anything other than numbers and letters.
	 */
		UNameBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				try
				{	Pattern p= Pattern.compile("[^a-z0-9 ]",Pattern.CASE_INSENSITIVE);
					UserName = userNameTxF.getText();
					Matcher m=p.matcher(UserName);

					if(m.find())
					{	ErrorTxtF.setVisible(true);
						ErrorTxtF.setText("Bad User Name");
					}
					else
					{	ErrorTxtF.setText("");
						outStream.println(UserName);
						outStream.flush();
						UNameBtn.setEnabled(false);
						clientMsgTxF.setEnabled(true);
						sendBtn.setEnabled(true);
		
					}
					
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
					

				}
			}
		});
		sendBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				try
				{
					//Storing value from Text field in GUI to client message variable
					String clientMessage = clientMsgTxF.getText();
					PrintWriter wr =new PrintWriter(socket.getOutputStream());
					wr.println(clientMessage);
					wr.flush();
					
				}catch(Exception e){
					
					System.out.println(e);
				}

			}
		});
		
	}}
