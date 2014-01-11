import java.io.*;
import java.util.ArrayList;

import javax.swing.*;

public class Chat {
	
	private static String path;
	private static ArrayList<String> ar;
	public static void main(String[] args) { // Asks for user input to determine
												// which client to initialize as
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Sets
																					// native
																					// look
																					// and
																					// feel
																					// for
																					// GUI
		} catch (Exception e) {

		}
		
			boolean b = false;
		do{
			String s = JOptionPane.showInputDialog("Enter a file path where SendPrompt may store program files specific to this computer");
			File f = new File(s);
			if(!f.isDirectory()) f.mkdir();
			path = s;
			b = true;
			
			
		}while(b == false);
		
		Client userClient = null;
		String responseLine = null;
		do {
			responseLine = JOptionPane
					.showInputDialog("Welcome to SendPrompt!\nType 'Join' to join an existing chat room \nor 'New' to create a new chat room");
			if (responseLine == null)
				System.exit(0);
			if (responseLine.equalsIgnoreCase("New")) {
				responseLine = JOptionPane
						.showInputDialog("Enter a chat room password");
				if (responseLine != null)
					userClient = new StandAloneClient(5, responseLine); // Have
																		// set
																		// to
																		// only
																		// 5
																		// clients
																		// but
																		// is
																		// expandable
			} else if (responseLine.equalsIgnoreCase("Join")) {
				try {
					loadRooms();
				} catch (Exception e) {
					ar = new ArrayList<String>();
				}
				String rp = "Choose the number of the chat room you wish to enter\n";
				for(int i = 0; i < ar.size(); i++){
					rp += i + ")" + ar.get(i) + "\n";
				}
				
				rp += ar.size() + ")" + "Other\n";
				
				int i = Integer.parseInt(JOptionPane.showInputDialog(rp));
				
				boolean add = false;
				try{
					rp = ar.get(i);
				}catch(Exception e){
					rp = JOptionPane.showInputDialog("Enter chat room name: ");
					add = true;
				}
				
				if(rp != null){
					try{
						userClient = new RemoteClient(rp);
					}catch(IOException e){
						JOptionPane.showConfirmDialog(null, "Error! Server cannot be found!\nIt may not longer be running.");
					}
					
					if(userClient != null && add == true){
						try {
							saveRoom(rp);
						} catch (Exception e) {
							JOptionPane.showConfirmDialog(null, e.getMessage());
						} 
					}
				}
				
				
				
				
			} else
				JOptionPane.showMessageDialog(null,
						"Error! Invalid Response. Please try again!");

		} while (userClient == null); // Initiates indiv client or full client
										// based on users choice
		userClient.Go();
	}

	private static void loadRooms() throws IOException,
			ClassNotFoundException {
		FileInputStream Fis = new FileInputStream(new File(path + "SendPrompt.chat"));
		ObjectInputStream Ois = new ObjectInputStream(Fis);

		ArrayList<String> r = (ArrayList<String>)Ois.readObject();
		
		ar = r;
	}

	private static void saveRoom(String name) throws IOException,
			ClassNotFoundException {

		FileOutputStream oS = new FileOutputStream(new File(path + "SendPrompt.chat"));
		ObjectOutputStream Oos = new ObjectOutputStream(oS);

		ar.add(name);
		
		Oos.writeObject(ar);

	}
}
