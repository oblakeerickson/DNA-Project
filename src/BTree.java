/**
 * BTree -- The BTree File, the data structure to hold the DNA substrings.
 * @author Janos Cserna, Blake Erickson, Andrew Gable
 * 12 December 2012
 */


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class BTree {
	
	private int degree;
	public Node root;
	public int once = 1;
	public long BTreeOffset;
	public long nodeSize;
	public int sequenceLength;

	class Node{
		public long Parent;
		public BTreeObject DataNode[];
		public long Pointers[];
		public boolean isleaf = true;
		public long numkeys;
		public long offset;
		
		public Node(long parent){
			Parent = parent;
			this.DataNode = new BTreeObject[2*degree-1];
			this.Pointers = new long[2*degree];
			numkeys = 0;
			BTreeOffset = BTreeOffset + nodeSize;
			offset = BTreeOffset;

		}
		public Node(){
			this.DataNode = new BTreeObject[2*degree-1];
			this.Pointers = new long[2*degree];
			numkeys = 0;
		}
		
			public String toString(){
			String result="";
				
			for(int k = 0; k < DataNode.length; k++){
				if(DataNode[k] == null){

				}
				else if(DataNode[k].getData() != -1){
					result = result + Convert(DataNode[k].getData()) + ": " + DataNode[k].hitFreq() + "\n" ;
				}
			}
				return result;
				
		}
	
		public boolean isFull(){
			for(int i = 0; i <= DataNode.length; i++){
				if (DataNode[i] == null){
					return false;
				}
			}
			return true;
		}
		public boolean isEmpty(){
			int check = 0;
			for (int i = 0; i< DataNode.length;i++){
				if(this.DataNode[i] == null)
					check++;
			}
			if (check == DataNode.length)
				return true;
			return false;
		}
			
		public BTreeObject BTreeSearch (Node root, long data){
			
			int a = 0;
			
			if (root.isleaf != true){
					
				while (a < root.DataNode.length && root.DataNode[a] != null && root.DataNode[a].getData() != -1 && isleaf != true ){
						
					if (a >= root.DataNode.length || root.DataNode[a] == null || root.DataNode[a].getData() == -1){
						return null;
					}
					
					
					if (root.DataNode[a].CompareTo(data) < 0){ //go to left
						
						if (root.Pointers[a] == -1)
							return null;
						
						Node temp = new Node();
						Node n = BinaryWrite.binaryRead(temp, root.Pointers[a]);
						return BTreeSearch (n, data);
					}
					
					if (root.DataNode[a].CompareTo(data) == 0){ //equals return data
						return root.DataNode[a];
					}
					
					else 
					
						a++;
					}
					if (root.Pointers[a] == -1)
						return null;
					
					Node temp = new Node();
					Node r = BinaryWrite.binaryRead(temp, root.Pointers[a]);
					
					if (r.offset == 0 && r.Pointers[a] == 0){
						return null;
					}
					
					
					if (r.DataNode[a]!=null || r.Pointers[a] != -1){
					
						return BTreeSearch (r ,data);
					
					}
					
					else
					
						return null; //didn't find it!
				}
			
				else {
					while (root.DataNode[a].getData() != -1 && a<root.DataNode.length&& root.DataNode[a] != null){
						
						if (a >= root.DataNode.length || root.DataNode[a] == null || root.DataNode[a].getData() == -1){
							return null;
						}
						
						
						if (root.DataNode[a].CompareTo(data) < 0){
						
							return null;
						}
						
						
						if (root.DataNode[a].CompareTo(data) == 0){
						
							return root.DataNode[a];
						}
						
						else 
							a++;
					}
					
					return null;
				}				
		}
		
		
	}
	
	public void BTreeCreate(int deg, long ns, int sequence){
		degree = deg;
		sequenceLength = sequence;
		BTreeOffset = 0;
		Node NewNode = new Node(-1);
		this.root = NewNode;

		nodeSize = ns;
		BinaryWrite.bWrite(NewNode);
		}
	
	public void setroot(long offset){
		Node n = new Node();
		root = BinaryWrite.binaryRead(n, offset);
	}
	
	public void BtreeInsert(BTree BT, long data){
		Node r = BT.root;
		if(r.numkeys == 2*degree - 1){
			Node S = new Node(-1);
			BT.root = S;
			S.isleaf = false;
			S.numkeys = 0;
			S.Pointers[0] = r.offset;
			Split(S, 1, r);
			BtreeInsertNon(S, data);
		}
		else 
			BtreeInsertNon(r, data);
		
	}
	
	private void BtreeInsertNon(Node X, long key){
		
		for (int i = 0; i < X.DataNode.length ; i++){
			if (X.DataNode[i] != null && X.DataNode[i].getData() == key){
				X.DataNode[i].frequency();
				BinaryWrite.bWrite(X);
				return;
			}
		}
			
		int i = (int) (X.numkeys-1);
		if(X.isleaf){
				while (i >= 0 && key < X.DataNode[i].getData()){
					X.DataNode[i+1] = X.DataNode[i];
					i--;
				}
				X.DataNode[i+1] = new BTreeObject(key);
				X.numkeys++;
			//BinaryOut(X);
				BinaryWrite.bWrite(X);
			}
		else{
			
			if(X.isEmpty()){
				X.DataNode[i] = new BTreeObject(key);
				i--;
			}
			else{
				if(X.DataNode[i] != null) {
					while (i >= 0 && key < X.DataNode[i].getData()){
						i--;
					}
				}
			}
			i++;
			//read in the ith child of X
			long s = X.Pointers[i];
			Node temp = new Node();
			Node Y = BinaryWrite.binaryRead(temp, s);
			if (Y.numkeys == 2*degree - 1){
				split_child(X, i, Y);
				if(X.DataNode[i] != null)
					if (key > X.DataNode[i].getData())
						i++;
			}
			BtreeInsertNon(Y, key);
		}
	}
	
	public void Split(Node parent, int index, Node child){
		int k = degree;
		int m = degree;
		
		Node z = new Node(parent.offset);
		
		z.isleaf = child.isleaf;
	
		z.numkeys = (degree - 1);
		
		
		for(int j = 0; j < degree-1; j++){
		
			z.DataNode[j] = child.DataNode[j];
		}
		
		if(!child.isleaf){
		
			for(int j = 0; j <= degree-1; j++){
				z.Pointers[j] = child.Pointers[j];
				child.Pointers[j] = child.Pointers[m];
				Node temp = new Node();
				Node S = BinaryWrite.binaryRead(temp, z.Pointers[j]);
				S.Parent = z.offset;
				BinaryWrite.bWrite(S);
				child.Pointers[m] = -1;
				m++;
			}
			
			
		}
		
		child.numkeys = (degree-1); 
		for(int j = 0; j < degree-1; j++){
				child.DataNode[j] = child.DataNode[k];
				child.DataNode[k] = null;
				k++;
		}
		
		for (int j = (int) parent.numkeys; j >= index; j--){
		
			parent.Pointers[j+1] = parent.Pointers[j];
		
		}
		
		parent.numkeys++;
		parent.Pointers[(int) (parent.numkeys-1)] = z.offset;

		
		for (int j = (int) (parent.numkeys - 2); j >= index; j--){
		
			parent.DataNode[j+1] = parent.DataNode[j];
		}
		parent.DataNode[index-1] = child.DataNode[degree-1]; 
		child.DataNode[degree-1] = null;
		child.Parent = parent.offset;
		
		parent.Pointers[index] = child.offset;
		
		BinaryWrite.bWrite(child);
		BinaryWrite.bWrite(z);
		BinaryWrite.bWrite(parent);
	}
	
	public void split_child(Node parent, int index, Node child){
		int k = degree;
		int m = degree;
		
		Node z = new Node(parent.offset);
		
		z.isleaf = child.isleaf;
	
		z.numkeys = (degree - 1);
		
		
		for(int j = 0; j < degree-1; j++){
		
			z.DataNode[j] = child.DataNode[j];
		}
		
		if(!child.isleaf){
		
			for(int j = 0; j <= degree-1; j++){
				z.Pointers[j] = child.Pointers[j];
				child.Pointers[j] = child.Pointers[m];
				Node temp = new Node();
				Node S = BinaryWrite.binaryRead(temp, z.Pointers[j]);
				S.Parent = z.offset;
				BinaryWrite.bWrite(S);
				child.Pointers[m] = -1;
				m++;
			}
			
		}
		
		child.numkeys = (degree-1); 
		for(int j = 0; j < degree-1; j++){
				child.DataNode[j] = child.DataNode[k];
				child.DataNode[k] = null;
				k++;
		}
		
		for (int j = (int) parent.numkeys; j >= index; j--){
		
			parent.Pointers[j+1] = parent.Pointers[j];
		
		}
		parent.numkeys++;
		parent.Pointers[index] = z.offset;

		
		for (int j = (int) (parent.numkeys - 2); j >= index; j--){
		
			parent.DataNode[j+1] = parent.DataNode[j];
		}

		parent.DataNode[index] = child.DataNode[degree-1]; 
		child.DataNode[degree-1] = null;
		child.Parent = parent.offset;
	
		
		BinaryWrite.bWrite(child);
		BinaryWrite.bWrite(z);
		BinaryWrite.bWrite(parent);
	}
	
	public void Inorder() throws IOException{		
		FileOutputStream FO = new FileOutputStream("dump");
		OutputStreamWriter out = new OutputStreamWriter(FO);
		InOrder(root, out);
		
		out.close();
		FO.close();
		
	}
	
	private void InOrder(Node r, OutputStreamWriter out) throws IOException{		
		if(r.isleaf){
			out.append(r.toString());
			return;
		}
		else{
			int i = 0;
			while(i <= r.DataNode.length){
				if (i >= r.DataNode.length || r.DataNode[i] == null || r.DataNode[i].getData() == -1){
					return;
				}
				Node temp = new Node();
				temp = BinaryWrite.binaryRead(temp, r.Pointers[i]);
				InOrder(temp, out);
				if (i < r.DataNode.length && r.DataNode[i] != null && r.DataNode[i].getData() != -1){
					out.append(Convert(r.DataNode[i].getData()) + ": " + r.DataNode[i].hitFreq()+"\n");
				}
				i++;
			}
		}
	}
	
	public String Convert(long key){
		String result = "";
		if (key == -1){
			return result;
		}
		String temp = "";
		String temp2 = "";
		temp = Long.toBinaryString(key);
		for (int i = sequenceLength*2; i > 1; i-= 2){
			try{
			temp2 = temp.substring(i-1, i+1);
			if (temp2.equals("00")) result = result + "A";
			else if (temp2.equals("11")) result = result + "T";
			else if (temp2.equals("01")) result = result + "C";
			else if (temp2.equals("10")) result = result + "G";
			}
			catch(StringIndexOutOfBoundsException ex){
				
			}
		}
		return result;
	}
	
	
	
}
