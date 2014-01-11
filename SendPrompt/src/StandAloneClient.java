import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;

//This class is evoked when user types "new"
//Creates a client AND a server
public class StandAloneClient extends JFrame implements Client {
	
	//GUI
	private JButton send;
	private JMenu menu;
	private JScrollPane messageScroll;
	private JPanel sendPane;
	private JTextArea messageArea;
	private JTextField sendArea;
	private GridLayout mainLayout, sendLayout;
	
	//Network Variables
	private Socket clientSocket;
	private PrintStream os = null;
	private BufferedReader is = null;
	private BufferedReader inputLine = null;
	private int port_number;
	private String key;
	
	
	public class sendAction implements ActionListener{ // WHat the send button will do

		@Override
		public void actionPerformed(ActionEvent arg0) {
			os.println(Crypto.generateEncryptedString(sendArea.getText()));
			messageArea.append("You Said: " + sendArea.getText() + "\n");
			messageArea.scrollRectToVisible(new Rectangle(0,messageArea.getHeight() - 2,1,1)); // Auto-Scrolls Message Area
			
			if(sendArea.getText().indexOf("quit//") != -1){
				JOptionPane.showMessageDialog(null, "Good Bye!");
				System.exit(0);
			}
			sendArea.setText(null);
		}
		
	}
	
	public StandAloneClient(int numOfClients, String connectionKey){ //This Constructor is called when it is FIRST initialized
		Server s = new Server(numOfClients, connectionKey);
		port_number = s.getPort();
		(new Thread(s)).start();
		System.out.println(port_number);
		key = connectionKey;
		
		mainLayout = new GridLayout(2,1);
		mainLayout.setVgap(20);
		sendLayout = new GridLayout(2,2);
		//sendLayout.setHgap(20);
		//Initialization of all variables
		send = new JButton("Send");
		menu = new JMenu();
		menu.add("Save Chat History");
		sendArea = new JTextField();
		sendPane = new JPanel();
		sendPane.setLayout(sendLayout);
		sendPane.add(sendArea);
		sendPane.add(send, 1);
		send.addActionListener(new sendAction());
		messageArea = new JTextArea();
		messageArea.setEditable(false);
		messageScroll = new JScrollPane(messageArea);
		getRootPane().setDefaultButton(send);
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);
		messageScroll.setBorder(new EmptyBorder(10,10,0,10));
		sendPane.setBorder(new EmptyBorder(10,10,10,10));
		messageArea.setBackground(new Color(0,10,10));
		messageArea.setForeground(new Color(0,255,255));
		messageScroll.setBackground(new Color(0,0,0,0));
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(true);
		sendArea.setBackground(new Color(0,0,0));
		sendArea.setForeground(new Color(0,255,255));
		sendPane.setBackground(new Color(0,0,0,0));
		messageArea.setRequestFocusEnabled(true);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		
		addWindowListener(new WindowAdapter(){ // What it'll do when the Client Closes
			public void windowClosing(WindowEvent e){
				int option = JOptionPane.showOptionDialog(null, "Would you like to keep the Chat Room Open after you leave?", 
						"On Exit", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 0);
				switch(option){
				case JOptionPane.YES_OPTION: os.println(Crypto.generateEncryptedString("quit//")); setVisible(false); break;
				case JOptionPane.NO_OPTION: System.exit(0); break;
				case JOptionPane.CANCEL_OPTION: 
				}
			}
		});
		
		setSize(400,300);
		getContentPane().setBackground(new Color(0,0,0));
		getContentPane().setLayout(mainLayout);
		getContentPane().add(messageScroll);
		getContentPane().add(sendPane);
		setVisible(true);
	}
	private StandAloneClient(PrintStream OS, BufferedReader INPUT, BufferedReader IS, Socket s, String k, JTextArea message){ //Contructor used for thread
		this.os = OS;
		this.inputLine = INPUT;
		this.is = IS; // Passes these Variables as references
		this.clientSocket = s;
		this.key = k;
		this.messageArea = message;
	}
	
	public void Go() { //Method called to start the client
		
		try {
            clientSocket = new Socket(InetAddress.getLocalHost().getCanonicalHostName(), port_number);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            os = new PrintStream(clientSocket.getOutputStream());
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host ");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to the host ");
        }
        if (clientSocket != null && os != null && is != null) { // Tests null for any input/output
            try {
		
		// Create a thread to read from the server
		
                new Thread(new StandAloneClient(os, inputLine, is, clientSocket, key, messageArea)).start();

            } catch (Exception e) {
                System.err.println("IOException:  " + e);
                
            }
        }
        
    }           
    
    public void run() {		
	String responseLine; // Response String
	
	

	try{
		responseLine = is.readLine();                           //
		os.println(key);										// Initializes Contact with server and asks user for name etc.
		responseLine = is.readLine();							//
		String name = JOptionPane.showInputDialog(responseLine);
		if(name != null){
		os.println(name);
		}else System.exit(0);
		
		while ((responseLine = is.readLine()) != null) {
		
		messageArea.append(Crypto.decryptString(responseLine) +"\n"); // Prints server message to Message Area
		messageArea.scrollRectToVisible(new Rectangle(0,messageArea.getHeight()-2,1,1));
		
		}
            os.close(); // Closing all connections once loop executes
        	is.close();
        	clientSocket.close();
        	
	} catch (Exception e) {
	   JOptionPane.showMessageDialog(null, "The Chat room has ended. Program will now exit.");
	   System.exit(0);
	}
	   
	
    }
}

	
	
	
	

