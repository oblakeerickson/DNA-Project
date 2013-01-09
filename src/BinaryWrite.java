/**
 * BinaryWrite -- The I/O operations of our project. Writes binary in and out, metadata in and out. 
 * @author Janos Cserna, Blake Erickson, Andrew Gable
 * 12 December 2012
 */

import java.io.*;
import java.nio.ByteBuffer;

public class BinaryWrite {

	final static int DATA_SIZE = 8;
	final static int POINTER_SIZE = 8;
	final static int INT_SIZE = 4;
	static int NODE_SIZE;
	static int DEGREE;
	static int TOTAL_DATA_SIZE;
	static int TOTAL_POINTER_SIZE;
	static String DataFile;
	static String MetaFile;

	public static int setNodeSize(int degree) {

		int size = ((2 * degree - 1) * DATA_SIZE) * 2 + DATA_SIZE
				+ (DATA_SIZE * (2 * degree)) + DATA_SIZE + DATA_SIZE;
		TOTAL_DATA_SIZE = ((2 * degree - 1) * (2 * DATA_SIZE));
		TOTAL_POINTER_SIZE = POINTER_SIZE * (2 * degree);
		NODE_SIZE = size;
		DEGREE = degree;
		return size;
	}
	
	public static void SetDataFile(String FileName, int degree, int k){
		String N = FileName + ".btree.data." + k +  "." + degree;
		DataFile = N;
	}
	
	public static void SetMetaFile(String FileName, int degree, int k){
		String N = FileName + ".btree.metadata." + k +  "." + degree;
		MetaFile = N;
	}

	public static BTree.Node binaryRead(BTree.Node n, Long offset) {

		try {
			// RandomAccessFile metaData = new RandomAccessFile("metaData",
			// "rw");
			RandomAccessFile dataStore = new RandomAccessFile(DataFile, "rw");

			dataStore.seek(offset);

			byte[] byteNode = new byte[NODE_SIZE];
			dataStore.read(byteNode);

			long freq = 0;
			long key = 0;
			long parent = 0;
			long child = 0;
			long selfOffset = 0;
			long numKeys = 0;

			long[] freqArr = new long[(2 * DEGREE) - 1];
			long[] keyArr = new long[(2 * DEGREE) - 1];
			long[] childrenArr = new long[(2 * DEGREE)];

			boolean flag = true;
			int k = 0;
			long location = 0;

			// freq & key
			for (long j = location; j < ((2 * DEGREE) - 1) * 16; j = j + 8L) {
				if (flag == true) {
					for (int i = (int) j; i < j + 8; i++) {
						freq = (freq << 8) + (byteNode[i] & 0xff);
					}
					freqArr[k] = freq;
					flag = false;
				} else {
					for (int i = (int) j; i < j + 8; i++) {
						key = (key << 8) + (byteNode[i] & 0xff);
					}
					keyArr[k] = key;
					flag = true;
					k++;
				}

			}

			location = location + TOTAL_DATA_SIZE;

			for (long i = location; i < location + 8; i++) {
				parent = (parent << 8) + (byteNode[(int) i] & 0xff);
			}
			location = location + DATA_SIZE;
			k = 0;
			for (long j = location; j < location + ((2 * DEGREE)) * 8; j = j + 8L) {
				for (int i = (int) j; i < j + 8; i++) {
					child = (child << 8) + (byteNode[i] & 0xff);
				}
				childrenArr[k] = child;
				k++;
			}
			location = location + TOTAL_POINTER_SIZE;

			for (long i = location; i < location + 8; i++) {
				selfOffset = (selfOffset << 8) + (byteNode[(int) i] & 0xff);
			}

			location = location + DATA_SIZE;

			for (long i = location; i < location + 8; i++) {
				numKeys = (numKeys << 8) + (byteNode[(int) i] & 0xff);
			}

			location = location + DATA_SIZE;

			n.Parent = parent;
			for (int i = 0; i < (2 * DEGREE) - 1; i++) {
				BTreeObject bto = new BTreeObject(keyArr[i]);
				bto.setFreq(freqArr[i]);
				n.DataNode[i] = bto;
			}

			n.Pointers = childrenArr;
			n.offset = selfOffset;
			n.numkeys = (int) numKeys;

			for (int i = 0; i < (2 * DEGREE); i++) {
				if (childrenArr[i] != 0) {
					n.isleaf = false;
				}
			}

			dataStore.close();
			return n;

		}

		// catch(FileNotFoundException ex){
		//
		// }
		catch (IOException ex) {
			Thread.dumpStack();
		}

		return n;
	}

	public static void bWrite(BTree.Node n) {
		try {

			try {
				Long rootLocation = 0L;

				RandomAccessFile dataStore = new RandomAccessFile(DataFile,
						"rw");

				int length = n.DataNode.length;
				long[] childOffsetArr = new long[length + 1];
				long parentOffset = n.Parent;
				long selfOffset = n.offset;
				long nk = n.numkeys;
				long freq;
				long key;

				byte[] freqByte;
				byte[] keyByte;
				byte[] parentOffsetByte;
				byte[] childByte;
				byte[] sOffSet;
				byte[] numKeys;

				for (int i = 0; i < length; i++) // change 2 to 'length'
				{
					// check for null
					if (n.DataNode[i] == null) {
						freq = 0;
						key = -1;
					} else {
						// int frequency
						freq = n.DataNode[i].hitFreq();

						// Long key
						key = n.DataNode[i].getData();

					}

					// Write freq and key to disk
					freqByte = ByteBuffer.allocate(8).putLong(freq).array();
					keyByte = ByteBuffer.allocate(8).putLong(key).array();

					dataStore.seek(selfOffset);
					dataStore.write(freqByte);
					dataStore.seek(selfOffset + 8);
					dataStore.write(keyByte);
					selfOffset = selfOffset + 16;
				}

				parentOffsetByte = ByteBuffer.allocate(8).putLong(parentOffset)
						.array();
				dataStore.seek(selfOffset);
				dataStore.write(parentOffsetByte);
				selfOffset = selfOffset + 8;

				for (int i = 0; i < childOffsetArr.length; i++) {
					childOffsetArr[i] = n.Pointers[i];
					childByte = ByteBuffer.allocate(8)
							.putLong(childOffsetArr[i]).array();

					dataStore.seek(selfOffset);
					dataStore.write(childByte);
					selfOffset = selfOffset + 8;
				}

				sOffSet = ByteBuffer.allocate(8).putLong(n.offset).array();
				dataStore.seek(selfOffset);
				dataStore.write(sOffSet);
				selfOffset = selfOffset + 8;
				numKeys = ByteBuffer.allocate(8).putLong(nk).array();
				dataStore.seek(selfOffset);
				dataStore.write(numKeys);

				// Write root Location to disk

				dataStore.close();
			} finally {
			}

		}

		catch (IOException ex) {

		}
	}

	public static void WriteMetaData(long degree, long sequence, BTree.Node n) {

		try {

			try {

				RandomAccessFile metaData = new RandomAccessFile(MetaFile,
						"rw");

				byte[] sOffSet;
				byte[] deg;
				byte[] seq;

				long selfOffset = 0;

				sOffSet = ByteBuffer.allocate(8).putLong(n.offset).array();
				metaData.seek(selfOffset);
				metaData.write(sOffSet);

				selfOffset = selfOffset + 8;

				deg = ByteBuffer.allocate(8).putLong(degree).array();
				metaData.seek(selfOffset);
				metaData.write(deg);

				selfOffset = selfOffset + 8;

				seq = ByteBuffer.allocate(8).putLong(sequence).array();
				metaData.seek(selfOffset);
				metaData.write(seq);

				metaData.close();

			} finally {

			}

		} catch (FileNotFoundException ex) {

		} catch (IOException ex) {

		}

	}

	public static long[] readMeta() {
		File file = new File(MetaFile);
		try {
			BufferedInputStream input = new BufferedInputStream(
					new FileInputStream(file));

			byte[] r = new byte[(int) file.length()];

			byte[] byteNode = new byte[24];
			long offset = 0;
			long degree = 0;
			long sequence = 0;
			long[] result = new long[3];
			int totalBytesRead = 0;

			while (totalBytesRead < r.length) {
				int bytesRemaining = r.length - totalBytesRead;

				int bytesRead = input.read(r, totalBytesRead, bytesRemaining);
				if (bytesRead > 0) {
					totalBytesRead = totalBytesRead + bytesRead;
				}

				long value = 0;

				int k = 0;
				for (int j = 0; j < r.length; j += 8) {
					for (int i = j; i < j + 8; i++) {
						value = (value << 8) + (r[i] & 0xff);
					}
					result[k] = value;
					k++;
				}

				return result;
			}
		}

		catch (FileNotFoundException ex) {

		} catch (IOException ex) {

		}
		return null;
	}
}
