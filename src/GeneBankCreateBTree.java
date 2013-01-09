/**
 * GeneBankCreateBTree -- The Master mind of the project, does many crucial things.
 * @author Janos Cserna, Blake Erickson, Andrew Gable
 * 12 December 2012
 */

import java.io.FileNotFoundException;
import java.util.NoSuchElementException;

public class GeneBankCreateBTree {

	public static void main(String[] args) {
		int sLength = 3;
		int degree = 102;
		int dump = 2;

		try {
			if (args.length == 3 || args.length == 4) {

				if (Integer.parseInt(args[0]) >= 0) {
					if (Integer.parseInt(args[0]) >= 2)
						degree = Integer.parseInt(args[0]);
				} else {
					System.err.println("Error 1: Degree must be at least 0.\n");
					System.err
							.println("Must be in this format:\njava GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>]");
					System.exit(1);
				}

				if (Integer.parseInt(args[2]) > 31) {
					System.err
							.println("Error 2:Length of DNA must be between 3 and 31.\n");
					System.err
							.println("Must be in this format:\njava GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>]");
					System.exit(1);
				} else {
					sLength = Integer.parseInt(args[2]);
				}

				String fileName = args[1];

				if (args.length == 4 && Integer.parseInt(args[3]) == 1) {
					// Debug Level 1->
					// Write dump file.
					dump = Integer.parseInt(args[3]);
				}

				if (args.length == 4 && Integer.parseInt(args[3]) == 0) {
					// Debug Level 0 ->
					// Show some data.
					dump = Integer.parseInt(args[3]);
				}

				gbkReader.gbkReader1(degree, fileName, sLength, dump);

			} else {
				System.err
						.println("Error 3: Wrong length of arguments passed in.\n");
				System.err
						.println("Must be in this format:\njava GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>]");
				System.exit(1);
			}

			if (args.length == 4 && Integer.parseInt(args[3]) == 0) {
				System.out.println("DEBUG MODE LEVEL 0.");

				System.out.println("Degree of BTree is: " + degree);
				System.out.println("DNA length is: " + sLength);
			}

			if (args.length == 4 && Integer.parseInt(args[3]) == 1) {
				System.out.println("DEBUG MODE LEVEL 1.\n");
				System.out.println("Making dump file...");
			}

		} catch (NumberFormatException ex) {
			System.err.println("Error 4: Must enter a number.\n");
			System.err
					.println("Must be in this format:\njava GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>]");
			System.exit(1);
		}

		catch (ArrayIndexOutOfBoundsException exs) {
			System.err
					.println("Error 5: Sorry something went very very very wrong, please try again.\n");
			System.err
					.println("Must be in this format:\njava GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>]");
			Thread.dumpStack();
			System.exit(1);
		}

		catch (NoSuchElementException ed) {
			System.err
					.println("Error 6: Sorry something went very wrong, please try again.\n");
			System.err
					.println("Must be in this format:\njava GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>]");
			System.exit(1);
		} catch (NullPointerException exe) {
			System.err
					.println("Error 8: Sorry, something went very wrong. Please try again.\n");
			System.err
					.println("Must be in this format:\njava GeneBankCreateBTree <degree> <gbk file> <sequence length> [<debug level>]");
			System.exit(1);
		}

	}

}
