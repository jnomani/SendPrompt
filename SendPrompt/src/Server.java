import javax.swing.*;
import java.net.*;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.*;

public class Server extends JFrame implements Runnable{
	//GUI for Mr. Small :-)
	private JTextArea serverLog;
	private JLabel serverName; 
	private JScrollPane scroll;
	
	//Netowrking Variables
	private Socket clientSocket = null;
	private ServerSocket serverSocket = null;
	private ClientThread[] t;
	private String connectionKey;
	private int port_number;
	

	
	public Server(int numClients, String connectionKey){ //Initates Server with array of ClientTHreads with length numClients and password
		t = new ClientThread[numClients];
		this.connectionKey = connectionKey;
		port_number = 4446;
		boolean connected = false;
		do{ //If first port ios not available it connects to next one
			System.out.println("Using port: " + port_number);
		try {
		    serverSocket = new ServerSocket(port_number);
		    
		    connected = true;
	        }
	        catch (IOException e)
		    {
	        	System.out.println("Error! Could not establish connection on port: " + port_number + 
	        			"\nAttempting to reconnect on port : " + (port_number + 1));
	        	port_number++;
	        	
		    }
		}while(!connected && port_number < 4500); // can have around 55 servers connected on same network.
		if(!connected){
			System.err.println("Could not Establish Connection");
			System.exit(-1);
			
		}try {
			serverName = new JLabel("Chat Room Name: " + InetAddress.getLocalHost().getHostName(), SwingConstants.CENTER);
		}catch (Exception e) {
			return;
		}
		setSize(300,200);
		serverLog = new JTextArea("Server Started on Port: " + port_number + "\n"); // Message Box shows connected Clients
		serverLog.setEditable(false);
		scroll = new JScrollPane(serverLog);
		//scroll.add(serverLog);
		
		getContentPane().setLayout(new GridLayout(2,1));
		if(serverName != null){
			getContentPane().add(serverName);
			//getContentPane().add(serverLog);
			getContentPane().add(scroll);
			setTitle("Chat Room: " + serverName.getText().substring(15));
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			
			setVisible(true);
			
		}
	}
	
	public int getPort(){ // Returns the port the server is running on
		return port_number;
	}
	public void run(){
		
		while(true){ // Constantly checks for new clients to add to the chat room
		    try {
		    	
			clientSocket = serverSocket.accept();
			for(int i=0; i< t.length; i++){
			    if(t[i]==null)
				{
				    (t[i] = new ClientThread(clientSocket,t, connectionKey)).start(); //Starts new thread with the new client
				    serverLog.append("Client Added: " + clientSocket.getInetAddress().getHostName() + "\n");
				    clientSocket = null;
				    break;
				}
			}
			if(clientSocket != null) System.out.println("Error Chat room is full! No more clients allowed!");
			for(int i=0; i< t.length; i++){
				if(t[i] != null && t[i].isAlive() != true) t[i] = null; //If thread died then it is nulled
			}
			
		    }
		    catch (IOException e) {
			System.out.println(e);
			}
		}
	    }
	}


class ClientThread extends Thread{ // Thread class
	ClientThread[] t;
	Socket clientSocket;
	private String connectionKey;
	PrintStream printer;
	BufferedReader input;
	
	public ClientThread(Socket clientSocket, ClientThread[] clientThreads, String passwrd){
		this.clientSocket = clientSocket;
		t = clientThreads;
		this.connectionKey = passwrd;
		
	}
	
	
	public void run(){ //Evoked when the thread is started
		String line, name;
		try{
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			printer = new PrintStream(clientSocket.getOutputStream());
			printer.println("Enter Chat Room Password:");
			name = input.readLine();
			if(!name.equalsIgnoreCase(connectionKey)){ //Checks for password
				printer.println("Wrong Password! Connection will now be TERMINATED!"); //Kills connection if pwd is wrong
				Close();
				return;
			}printer.println("Enter your name: ");
			name = input.readLine();
			if( name == "null") return;
			printer.println(Crypto.generateEncryptedString("Welcome to the chat room, " + name));
			
			
			for(ClientThread c : t){
				if(c != null && c != this) c.printer.println(Crypto.generateEncryptedString("ATTENTION: " + name + " has entered the session!"));
			}while(true){
				line = input.readLine();
				if(Crypto.decryptString(line).equalsIgnoreCase("QUIT//")) break; // quit// is keyword to terminate connection
				for(ClientThread c : t){
					if(c != null && c != this) c.printer.println(Crypto.generateEncryptedString(name + " Says: " +
				Crypto.decryptString(line)));
				}
			}
			printer.println(Crypto.generateEncryptedString("Good Bye, " + name));
			
			for(ClientThread c : t){
				if(c != null && c != this) c.printer.println(Crypto.generateEncryptedString("ATTENTION: " + name + " Has left the session"));
			}
			for(ClientThread c : t){
				if(c == this) c = null;
			}
			Close();
		}catch(Exception e){
			if(e instanceof IOException);
			else System.out.println(e);
		}
	}
	
	private void Close() throws IOException{ // Closes all connections and stops the thread
		
		input.close();
	    printer.close();
	    clientSocket.close();
	    
	    this.stop();
		}
	
	
}