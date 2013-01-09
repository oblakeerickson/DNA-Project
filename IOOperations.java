import java.io.RandomAccessFile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

// IO class

public class IOOperations {

        private RandomAccessFile rafile;

        //Reads BTree node at offset into buffer, then from buffer reads node data
        //given the location of btreenode =offset returns btreenode
        protected BTreeNode ReadFromDisk(long offset) {
                if (offset == -1)
                        return null;
                BTreeNode temp = new BTreeNode();
                long key = -1;
                long freq = 0;
                
                byte[] data = new byte[(int) nodeSize];
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                DataInputStream dis = new DataInputStream(bis);
                try {
                        file.seek(offset);
                        file.read(data);
                        temp.offset = offset;
                        temp.isleaf = dis.readBoolean();
                        temp.nkeys = dis.readLong();
                        temp.parentPointer = dis.readLong();

                        if (!temp.isleaf)
                                temp.childrenPointers.add(dis.readLong());

                        for (int i = 0; i < temp.nkeys; i++) {
                                key = dis.readLong();
                                freq = dis.readLong();
                                temp.objectList.add(new TreeObject(key, freq));
                                if (!temp.isleaf)
                                        temp.childrenPointers.add(dis.readLong());
                        }

                        bis.close();
                        dis.close();
                } catch (IOException e) {
                        IOError(e);
                }

                return temp;
        }
        
        //write the node to buffer and then convert it to byte 
        //array and write the array to disk
        public void WriteToDisk(BTreeNode x){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();  
                DataOutputStream dos = new DataOutputStream(bos);
                
                BTreeNode node = x;
                Iterator<TreeObject> objectiter = node.objectList.iterator();
                Iterator<Long> childiter = node.childrenPointers.iterator();
                
                try {
                        file.seek(node.offset);
                        dos.writeBoolean(node.isleaf);
                        dos.writeLong(node.objectList.size());
                        dos.writeLong(node.parentPointer);
                        if (!node.isleaf) {
                                dos.writeLong(childiter.next());
                                while (objectiter.hasNext()) {
                                        TreeObject temp = objectiter.next();
                                        dos.writeLong(temp.key);
                                        dos.writeLong(temp.frequency);
                                        dos.writeLong(childiter.next());
                                }
                        } else {
                                while (objectiter.hasNext()) {
                                        TreeObject temp = objectiter.next();
                                        dos.writeLong(temp.key);
                                        dos.writeLong(temp.frequency);
                                }
                        }
                        dos.flush();
                        byte[] thedata = bos.toByteArray();
                        dos.close();
                        bos.close();
                        file.write(thedata);
                } catch (IOException e) {
                        IOError(e);
                }
                
                return 0;
                
        }
        
        // write root-offset or degree of tree to the beginning of BTree file
        // values come 
        private writeMetadata(String data, long val, RandomAccessFile file){
                if (data.equals("ROOT_OFFSET")) {
                        try {
                                file.seek(ROOT_OFFSET_POS);
                                file.writeChars("ROOT_OFFSET");
                                file.writeLong(val);
                        } catch (IOException e) {
                                IOError(e);
                        }

                } else if (data.equals("DEGREE")) {
                        try {
                                file.seek(DEGREE_POS);
                                file.writeChars("DEGREE");
                                file.writeLong(val);
                        } catch (IOException e) {
                                IOError(e);
                        }
                }
                return 0;
        }
        
        
        // Read the root or degree from the start of the BTree file
        // file is the file stored on disk
        //Note: position = ROOT_OFFSET chars (22 bytes) + 1; long (8 bytes) = 30
        // will return root offset of degree of BTree (depends on data)
        private readMetadata(String data, RandomAccessFile file){
                long value = 0;
                char[] ch = null;

                // for root
                if (data.equals("ROOT_OFFSET")) {
                        ch = new char[11];
                        try {
                                file.seek(0);
                        } catch (IOException e) {
                                IOError(e);
                        }
                }
                // for degree
                else if (data.equals("DEGREE")) {
                        ch = new char[6];
                        try {
                                file.seek(30); 
                        } catch (IOException e) {
                                IOError(e);
                        }
                }

                for (int i = 0; i < ch.length; i++) {
                        try {
                                ch[i] = file.readChar();
                        } catch (IOException e) {
                                IOError(e);
                        }
                }
                String data2 = String.valueOf(ch);
                if (data.equals(data2)) {
                        try {
                                value = file.readLong();
                        } catch (IOException e) {
                                IOError(e);
                        }
                }
                return value;
        }
        
        }