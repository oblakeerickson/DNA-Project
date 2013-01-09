/**
 * GeneBankSearch -- The reader of the gbkFile.
 * @author Janos Cserna, Blake Erickson, Andrew Gable
 * 12 December 2012
 */

import java.io.*;
import java.util.NoSuchElementException;

public class GeneBankSearch {

	public static void main(String[] args) throws IOException {

		// Command Line Arguments
		String btreeFile = args[0];
		String queryFile = args[1];
		String SearchResults = null;

		// Declare Variables
		String query;
		String binQuery = "1";
		long longQuery;

		BinaryWrite.DataFile = btreeFile;
		BinaryWrite.MetaFile = btreeFile.substring(0, btreeFile.indexOf("data.")) + "metadata" + btreeFile.substring(btreeFile.indexOf("a.") +1);
		long[] foo = BinaryWrite.readMeta();

		BinaryWrite.setNodeSize((int) foo[1]);
		BTree bool = new BTree();
		int ns = (int) (((2 * foo[1] - 1) * 8) * 2 + 8 + (8 * (2 * foo[1])) + 8 + 8);
		long offset = foo[0];

		bool.BTreeCreate((int) foo[1], ns, (int) foo[2]);
		bool.setroot(offset);
		int i = 0;
		try {
			if (args.length == 2 || args.length == 3) {
				BufferedReader queryReader = new BufferedReader(new FileReader(
						queryFile));
				BufferedReader btreeReader = new BufferedReader(new FileReader(
						btreeFile));

				FileOutputStream FO = new FileOutputStream("SearchResults");
				OutputStreamWriter out = new OutputStreamWriter(FO);

				while ((query = queryReader.readLine()) != null) {

					for (int j = 0; j < query.length(); j++) {
						char letter = query.charAt(j);
						if (letter == 'a' || letter == 'A') {
							binQuery = binQuery + "00";
						}
						if (letter == 't' || letter == 'T') {
							binQuery = binQuery + "11";
						}
						if (letter == 'c' || letter == 'C') {
							binQuery = binQuery + "01";
						}
						if (letter == 'g' || letter == 'G') {
							binQuery = binQuery + "10";
						}
						letter = ' ';
					}

					// convert longQuery String to Binary Long
					longQuery = Long.parseLong(binQuery, 2);
					binQuery = "1";

					BTreeObject r = bool.root.BTreeSearch(bool.root, longQuery);

					if (r != null) {
						out.write(query + ": " + r.hitFreq() + "\n");

					}

					else {
						out.write(query + ": " + 0 + "\n");
					}

					

				}
				out.close();
			}

			if (args.length == 3 && Integer.parseInt(args[2]) == 0) {
				System.out.println("Standard Query Results... ");

			}
			
			else {
				System.err
						.println("Error 3: Wrong length of arguments passed in.\n");
				System.err
						.println("Must be in this format:\njava GeneBankSearch <btree file> <query file> [<debug level>]");
				System.exit(1);
			}
		} catch (NumberFormatException ex) {
			System.err.println("Error 4: Must enter a number.\n");
			System.err
					.println("Must be in this format:\njava GeneBankSearch <btree file> <query file> [<debug level>]");
			System.exit(1);
		}

		catch (ArrayIndexOutOfBoundsException exs) {
			System.err
					.println("Error 5: Sorry something went very very very wrong, please try again.\n");
			System.err
					.println("Must be in this format:\njava GeneBankSearch <btree file> <query file> [<debug level>]");
			System.exit(1);
		}

		catch (NoSuchElementException ed) {
			System.err
					.println("Error 6: Sorry something went very wrong, please try again.\n");
			System.err
					.println("Must be in this format:\njava GeneBankSearch <btree file> <query file> [<debug level>]");
			System.exit(1);
		} catch (FileNotFoundException ex) {
			System.err
					.println("Error 7: Could not find the file you are looking for, Sorry. Please try again.\n");
			System.err
					.println("Must be in this format:\njava GeneBankSearch <btree file> <query file> [<debug level>]");
			System.exit(1);
		}

		catch (NullPointerException exe) {
			System.err
					.println("Error 8: Sorry, something went very wrong. Please try again.\n");
			System.err
					.println("Must be in this format:\njava GeneBankSearch <btree file> <query file> [<debug level>]");
			System.exit(1);
		}

	}

}
