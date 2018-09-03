// Memory size notes:
// Integer: 81 bytes
// String: 8 + length bytes

import sun.misc.Unsafe;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.io.*;
import java.util.Random;

public class Array<T> {

    private Unsafe unsafe;
    private long size;
    private long address;
    private long blockSize;

    public static void main(String[] args) {
        Random random = new Random();

        try {
            // MARK - int test
            // size of integer * number of elements (81 * 20)
            Array<Integer> array = new Array<Integer>(81 * 20);
            for (int i = 0; i < 20; i++) // set a random number at every index from 0 to 20
                array.set(random.nextInt(100), i);
            
            for (int i = 0; i < 20; i++) // then print out what the number is
                System.out.format("found: %d at index: %d \n", array.get(i), i);
            
            // MARK - string test
            Array<String> stringArray = new Array<String>((8 + 5) * 20);
            for (int i = 0; i < 20; i++) // set 'Hello' at every index from 0 to 20
                stringArray.set("Hello", i);
            
            for (int i = 0; i < 20; i++) // then print out the value
                System.out.format("found: %s at index: %d \n", stringArray.get(i), i);
        } catch (Exception e) {
            System.out.println("Error: ");
            System.out.println(e.getMessage());
        }
    }

    Array (long size) throws Exception {
        unsafe = getUnsafe();
        size = size;
        address = unsafe.allocateMemory(size);
    }

    @SuppressWarnings("sunapi")
    private static Unsafe getUnsafe() throws Exception {
        Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
        singleoneInstanceField.setAccessible(true);
        return (Unsafe) singleoneInstanceField.get(null);
    }

    public void set (T value, int index) throws Exception  {
        byte[] bytes = this.serialize(value);
        blockSize = bytes.length;

        for (long i = 0; i < bytes.length; i++)
            unsafe.putByte(address + (index * blockSize) + i, bytes[(int)i]);
    }

    public T get (int index) throws Exception  {
        byte[] bytes = new byte[(int)blockSize];
        for (long i = 0; i < blockSize; i++)
            bytes[(int)i] = unsafe.getByte(address + (index * blockSize) + i);
        
        return deserialize(bytes);
    }

    private byte[] serialize(T object) throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream so = new ObjectOutputStream(bo);
        
        so.writeObject(object);
        so.flush();
        
        return bo.toByteArray();
    }

    private T deserialize (byte[] bytes) throws Exception {
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
        ObjectInputStream si = new ObjectInputStream(bi);
        
        return (T) si.readObject();
    }
}
