/**
 * gbkReader -- The reader of the gbk File.
 * @author Janos Cserna, Blake Erickson, Andrew Gable
 * 12 December 2012
 */

import java.io.*;

public class gbkReader {

	public static void gbkReader1(int degree, String fname, int sequenceLength, int debug) {

		BTreeObject r = null;
		
		int insertions = 0;
		boolean afterOrigin = false;
		String finalString = "";
		long myLong = 0;
		String myTemp = "";
		String myLongString = "1"; // start with 1 so 0's don't get truncated

		// command line arguments
		int MyDegree = degree;
		String fileName = fname;
		int sl = sequenceLength; // Sequence Length

		BTree tree = new BTree();
		int size = BinaryWrite.setNodeSize(MyDegree);
		BinaryWrite.SetDataFile(fname, MyDegree, sl);
		BinaryWrite.SetMetaFile(fname, MyDegree, sl);

		tree.BTreeCreate(MyDegree, size, sl);

		BufferedReader br = null;

		try {

			String s;

			br = new BufferedReader(new FileReader(fileName));

			while ((s = br.readLine()) != null) {

				if (s.contains("ORIGIN")) {
					afterOrigin = true;
				}
				// Remove Spaces
				s = s.replaceAll("\\s", "");

				// Remove digits
				s = s.replaceAll("\\d+", "");

				if (s.length() > 0 && afterOrigin == true) {
					if (s.contains("//")) {
						afterOrigin = false;
						for (int i = 0; i < finalString.length() - sl + 1; i++) {
							myTemp = finalString.substring(i, i + sl);

							// convert to binary values
							for (int j = 0; j < myTemp.length(); j++) {
								char letter = myTemp.charAt(j);
								if (letter == 'a' || letter == 'A') {
									myLongString = myLongString + "00";
								}
								if (letter == 't' || letter == 'T') {
									myLongString = myLongString + "11";
								}
								if (letter == 'c' || letter == 'C') {
									myLongString = myLongString + "01";
								}
								if (letter == 'g' || letter == 'G') {
									myLongString = myLongString + "10";
								}
								letter = ' ';
							}
							

							// convert to Long base 2
							myLong = Long.parseLong(myLongString, 2);
							

								// Insert into the Tree
								tree.BtreeInsert(tree, myLong);
								insertions++;
							

							
							

							BinaryWrite.WriteMetaData(MyDegree, sl, tree.root);

							
							myTemp = "";
							myLongString = "1";

						}
						finalString = "";
						
						
					}

					// Build final String
					if (s.contains("//") || s.contains("ORIGIN")) {
					} else {
						finalString = finalString + s;
					}

				}

			}
			
			if (debug == 1){
				//dump File
				tree.Inorder();
			}


		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	
	
	

}
