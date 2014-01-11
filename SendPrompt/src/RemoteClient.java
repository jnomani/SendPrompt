import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class RemoteClient extends JFrame implements Client {
	private JButton send;
	private JMenu menu;
	private JScrollPane messageScroll;
	private JPanel sendPane;
	private JTextArea messageArea;
	private JTextField sendArea;
	private GridLayout mainLayout, sendLayout;
	private WindowAdapter chatListener;

	private Socket clientSocket;
	private PrintStream os = null;
	private BufferedReader is = null;
	private int port_number;

	public class sendAction implements ActionListener { // Send button

		@Override
		public void actionPerformed(ActionEvent arg0) {
			os.println(Crypto.generateEncryptedString(sendArea.getText()));
			messageArea.append("You Said: " + sendArea.getText() + "\n");
			messageArea.scrollRectToVisible(new Rectangle(0, messageArea
					.getHeight() - 2, 1, 1));
			// Toolkit.getDefaultToolkit().
			if (sendArea.getText().indexOf("quit//") != -1) {
				JOptionPane.showMessageDialog(null, "Good Bye!");
				System.exit(0);
			}
			sendArea.setText(null);
		}

	}

	public RemoteClient(String serverName) throws IOException {
		port_number = 4446;
		do {
			try {
				clientSocket = new Socket(serverName, port_number);

			} catch (IOException e) {
				port_number++;
				System.out.println("Error! Attempting to connect on Port: "
						+ port_number);
			}
		} while (clientSocket == null && port_number <= 4500);
		if (clientSocket == null)
			throw new IOException("Server Not Found");

		mainLayout = new GridLayout(2, 1);
		mainLayout.setVgap(20);
		sendLayout = new GridLayout(2, 2);
		// sendLayout.setHgap(5);
		// Initializes variables
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
		messageScroll.setBorder(new EmptyBorder(10, 10, 0, 10));
		sendPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		messageArea.setBackground(new Color(0, 10, 10));
		messageArea.setForeground(new Color(0, 255, 255));
		messageScroll.setBackground(new Color(0, 0, 0, 0));
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(true);
		sendArea.setBackground(new Color(0, 0, 0));
		sendArea.setForeground(new Color(0, 255, 255));
		sendPane.setBackground(new Color(0, 0, 0, 0));
		messageArea.setRequestFocusEnabled(true);

		setSize(400, 300);
		getContentPane().setBackground(new Color(0, 0, 0));
		getContentPane().setLayout(mainLayout);
		getContentPane().add(messageScroll);
		getContentPane().add(sendPane);
		setVisible(true);
		addWindowListener( // Listens for Close
		new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					os.println(Crypto.generateEncryptedString("quit//"));
				} catch (Exception ex) {

				}
				System.exit(0);
			}
		});
	}

	private RemoteClient(PrintStream os2, BufferedReader is2,
			Socket clientSocket2, JTextArea messages) { // Called for thread
		this.os = os2;
		this.is = is2;
		this.clientSocket = clientSocket2; // Passes input and output to thread
		this.messageArea = messages;
	}

	// TODO Auto-generated constructor stub

	@Override
	public void Go() { // Evoked when it is started
		try {
			os = new PrintStream(clientSocket.getOutputStream());
			is = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		} catch (Exception e) {
		}
		if (clientSocket != null && os != null && is != null) {
			try {

				// Creates a thread to read from the server

				new Thread(new RemoteClient(os, is, clientSocket, messageArea))
						.start();

			} catch (Exception e) {
				System.err.println("IOException:  " + e);

			}
		}
	}

	@Override
	public void run() {
		String responseLine;

		try {

			responseLine = is.readLine();
			/*
			 * Initiates communication with Server Asks for PAssword and then
			 * potentially a name from Client
			 */
			os.println(JOptionPane.showInputDialog(responseLine));
			responseLine = is.readLine();
			if (responseLine.indexOf("Enter") == -1) {
				JOptionPane.showMessageDialog(null, responseLine);
			} else {
				String name = JOptionPane.showInputDialog(responseLine);
				if (name != null)
					os.println(name);
				else
					System.exit(0);
			}

			while ((responseLine = is.readLine()) != null) { // While there is
																// still input
																// from Server
				messageArea.append(Crypto.decryptString(responseLine) + "\n");
				messageArea.scrollRectToVisible(new Rectangle(0, messageArea
						.getHeight() - 2, 1, 1));
				// System.out.println(responseLine);

				if (Crypto.decryptString(responseLine).indexOf("Good Bye, ") != -1)
					break;

			}

			os.close();
			is.close();
			clientSocket.close();
			System.exit(0);
		} catch (Exception e) {
			System.out.println();
			if (e instanceof SocketException) {
				JOptionPane.showMessageDialog(null,
						"The chat room has ended. SendPrompt will now exit."); // If
																				// server
																				// is
																				// closed
																				// then
																				// all
																				// clients
																				// are
																				// closed
				System.exit(0);
			}
		}

	}
}
