import java.util.ArrayList;

public class Crypto { // Encryption Class. Most Methods are static so it doesn't
						// have to be evoked
	private static boolean charAtBeginning = false;

	private static long decimateString(String nonDecimal, int Base) { // Changes
																		// a
																		// string
																		// into
																		// decimal
																		// using
																		// base
																		// conversion
		char temp;
		long result = 0;
		int[] Block = new int[nonDecimal.length()];
		for (int i = 0; i < Block.length; i++) {
			temp = nonDecimal.charAt(i);
			switch (temp) {
			case 'A':
				Block[i] = 10;
				break;
			case 'B':
				Block[i] = 11;
				break;
			case 'C':
				Block[i] = 12;
				break;
			case 'D':
				Block[i] = 13;
				break;
			case 'E':
				Block[i] = 14;
				break;
			case 'F':
				Block[i] = 15;
				break;
			case 'G':
				Block[i] = 16;
				break;
			case 'H':
				Block[i] = 17;
				break;
			case 'I':
				Block[i] = 18;
				break;
			case 'J':
				Block[i] = 19;
				break;
			case 'K':
				Block[i] = 20;
				break;
			case 'L':
				Block[i] = 21;
				break;
			case 'M':
				Block[i] = 22;
				break;
			case 'N':
				Block[i] = 23;
				break;
			case 'O':
				Block[i] = 24;
				break;
			case 'P':
				Block[i] = 25;
				break;
			case 'Q':
				Block[i] = 26;
				break;
			case 'R':
				Block[i] = 27;
				break;
			case 'S':
				Block[i] = 28;
				break;
			case 'T':
				Block[i] = 29;
				break;
			case 'U':
				Block[i] = 30;
				break;
			case 'V':
				Block[i] = 31;
				break;
			case 'W':
				Block[i] = 32;
				break;
			case 'X':
				Block[i] = 33;
				break;
			case 'Y':
				Block[i] = 34;
				break;
			case 'Z':
				Block[i] = 35;
				break;
			case 'a':
				Block[i] = 36;
				break;
			case 'b':
				Block[i] = 37;
				break;
			case 'c':
				Block[i] = 38;
				break;
			case 'd':
				Block[i] = 39;
				break;
			case 'e':
				Block[i] = 40;
				break;
			case 'f':
				Block[i] = 41;
				break;
			case 'g':
				Block[i] = 42;
				break;
			case 'h':
				Block[i] = 43;
				break;
			case 'i':
				Block[i] = 44;
				break;
			case 'j':
				Block[i] = 45;
				break;
			case 'k':
				Block[i] = 46;
				break;
			case 'l':
				Block[i] = 47;
				break;
			case 'm':
				Block[i] = 48;
				break;
			case 'n':
				Block[i] = 49;
				break;
			case 'o':
				Block[i] = 50;
				break;
			case 'p':
				Block[i] = 51;
				break;
			case 'q':
				Block[i] = 52;
				break;
			case 'r':
				Block[i] = 53;
				break;
			case 's':
				Block[i] = 54;
				break;
			case 't':
				Block[i] = 55;
				break;
			case 'u':
				Block[i] = 56;
				break;
			case 'v':
				Block[i] = 57;
				break;
			case 'w':
				Block[i] = 58;
				break;
			case 'x':
				Block[i] = 59;
				break;
			case 'y':
				Block[i] = 60;
				break;
			case 'z':
				Block[i] = 61;
				break; // Base-62 Conversion cuz we hard core like dat
			default:
				Block[i] = Integer.parseInt(nonDecimal.substring(i, i + 1));
			}
		}
		for (int i = 0, b = Block.length - 1; i < Block.length; i++, b--) {
			result += (Block[i] * Math.pow(Base, b));
		}
		return result;
	}

	private static String LetterizeInt(int base, long num) { // Converts Decimal
																// back to a
																// given base
		int digit = 0;
		long copy = num;
		long rev = 0;
		String result = "";
		while (num > 0) {
			num = num / base;
			digit++;
		}
		while (digit >= 0) {
			rev = copy / (long) Math.pow(base, digit); // Simply converts
														// decimal to a given
														// number system
			copy = copy % (long) Math.pow(base, digit);
			digit--;
			if (rev >= 10) { // checks to see if
				if (rev < 36)
					result += (char) (rev + 55); // Converts Back to Capital
													// Letters
				else
					result += (char) (rev + 61); // Converts Back to LowerCase
													// Letters
			} else
				result += rev;
		}
		if (result.charAt(0) == '0' && result.charAt(1) != 'x')
			result = result.substring(1);
		else if (result.charAt(1) == 'x')
			result = result.substring(0, 2) + result.substring(3);
		return result;
	}

	private static String[] Encrypt(String str) { // Randomly selects base to
													// convert to using an
													// intelligent algorithm and
													// then converts String to
													// that base
		boolean key = false;
		String[] Result = new String[2];
		int ascii = 122; // Subtract 54 to get minimum base value for uppercase
							// OR 61 for Lower Case
		while (ascii >= 65) {
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) == (char) ascii) {
					key = true;
					break;
				}
			}
			if (key)
				break;
			else
				ascii--;
		}
		int minbase;
		if (ascii < 97)
			minbase = ascii - 54;
		else
			minbase = ascii - 60;
		int base = minbase + (int) (Math.random() * ((62 - minbase) + 1));
		// System.out.println(base + "  " + ascii + "  " + minbase);
		Result[0] = Long.toString(decimateString(str, base));
		Result[1] = LetterizeInt(2, base);

		return Result;
	}

	public static String generateEncryptedString(String UsersMessage) { // Formats
																		// the
																		// String
																		// and
																		// then
																		// calls
																		// Encrypt
		String readIn = UsersMessage;

		if (Character.isLetterOrDigit(readIn.charAt(readIn.length() - 1)))
			readIn += " ";
		if (!Character.isLetterOrDigit(readIn.charAt(0)))
			charAtBeginning = true;
		ArrayList<String> format = new ArrayList<String>();
		for (int i = 0; i < readIn.length(); i++) { // removes punctuation
			String temp = "";
			while (i < readIn.length()
					&& Character.isLetterOrDigit(readIn.charAt(i))) {
				temp += readIn.charAt(i);
				i++;
			}
			if (temp.length() != 0)
				format.add(temp);
		}

		ArrayList<String> Punctuation = new ArrayList<String>(); // Adds
																	// punctuation
																	// to an
																	// array
																	// list
		for (int i = 0; i < readIn.length(); i++) {
			String temp = "";
			while (i < readIn.length()
					&& !Character.isLetterOrDigit(readIn.charAt(i))) { // adds
																		// to an
																		// array
																		// list
																		// all
																		// indiv
																		// ints
				temp += readIn.charAt(i);
				i++;
			}
			if (temp.length() != 0)
				Punctuation.add(temp);
		}

		String[][] message = new String[format.size()][2];
		for (int i = 0; i < format.size(); i++) {
			String temp = format.get(i);
			message[i] = Encrypt(temp);
		}

		// concatenation of Strings
		String OG = "";

		if (charAtBeginning) { // if there is punctuation or symbol at beginning
								// of String
			OG = Punctuation.get(0);
			Punctuation.remove(0);
		}
		for (int i = 0; i < Punctuation.size(); i++) {
			OG += message[i][0] + Punctuation.get(i) + "|" + message[i][1]
					+ "|";
		}

		charAtBeginning = false;
		return OG;
	}

	public static String decryptString(String EncryptedMessage){ //Formats Encrypted String and then calls LetterizeInt
		if(!Character.isLetterOrDigit(EncryptedMessage.charAt(0))) charAtBeginning = true;
			ArrayList<String> format = new ArrayList<String>();
			
			ArrayList<String> punctuation = new ArrayList<String>();
			for(int i = 0; i < EncryptedMessage.length(); i++){
				String temp = "";
				while(i < EncryptedMessage.length() - 1 && !Character.isDigit(EncryptedMessage.charAt(i))){
					if(EncryptedMessage.charAt(i) == '|' && EncryptedMessage.charAt(i + 1) != '|'){
						break;
					}else if( EncryptedMessage.charAt(i + 1) != 0){
						temp+= EncryptedMessage.charAt(i);
						EncryptedMessage = EncryptedMessage.substring(0, i) + EncryptedMessage.substring(i + 1);
					}else{
					temp += EncryptedMessage.charAt(i);
					i++;
					}
				}if(temp.length() != 0) punctuation.add(temp);
			} 
				String rawencrypt = "";
				for(int i = 0; i < EncryptedMessage.length(); i++){
					if(Character.isDigit(EncryptedMessage.charAt(i)) || EncryptedMessage.charAt(i) == '|'){
						rawencrypt += EncryptedMessage.charAt(i);
					}
				} 
				for(int i = 0; i < rawencrypt.length(); i++){
					String temp = "";
					while(i < rawencrypt.length() && rawencrypt.charAt(i) != '|'){
						
						temp += rawencrypt.charAt(i);
						//System.out.println(temp);
						i++;
					}format.add(temp);
				}
				
				
				String[] Words = new String[format.size() / 2];
				int[] Base = new int[Words.length];
				int cw = 0, cb = 0;
				for(int i = 0;  i < format.size() && (cw < Words.length || cb < Base.length); i++){
					if(i % 2 == 0){
						Words[cw] = format.get(i);
						cw++;
					}else{
						Base[cb] = (int) decimateString(format.get(i), 2);
						cb++;
					}
				}
				
				String result = "";
				
				if(charAtBeginning == true){
					result += punctuation.get(0);
					punctuation.remove(0);
				}
				
				for(int i = 0; i < Words.length; i++){
					Words[i] = LetterizeInt(Base[i], Long.parseLong(Words[i]));
					result += Words[i] + punctuation.get(i);
				}
				
				
				
			return result;			
	}
	
//	public static void main(String[] ar){
//		String m = "juzer is a moose";
//		m = generateEncryptedString(m);
//		System.out.println(decryptString(m));		
//	}
}
