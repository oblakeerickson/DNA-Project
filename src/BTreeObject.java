/**
 * BTreeObject -- The basic objects to the BTree.
 * @author Janos Cserna, Blake Erickson, Andrew Gable
 * 12 December 2012
 */
public class BTreeObject {
	// frequency
	private int frequency;

	// data set to -1 to not confuse with 0.
	private long data = -1;

	// basic object
	BTreeObject(long info) {
		frequency = 1;
		data = info;
	}

	// How many?
	public boolean frequency() {
		if (data < 0)
			return false;
		else {
			frequency++;
			return true;
		}
	}

	// set to specific number
	public void setFreq(long f) {
		frequency = (int) f;
	}

	// Update frequency
	public int hitFreq() {
		return frequency;
	}

	// get the data
	public long getData() {
		return data;
	}

	public void setData(long data) {
		this.data = data;
	}

	// Compare data to one another.
	public int CompareTo(long data) {
		if (data == this.data)

			return 0;

		if (data < this.data)

			return -1;

		else

			return 1;
	}

	// Print!
	public String toString() {
		return "\n" + frequency + " " + data;
	}
}
