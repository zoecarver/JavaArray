import sun.misc.Unsafe;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.io.*;

public class Array<T> {

  private Unsafe unsafe;
  private long size;
  private long address;
  private int index;
  private long blockSize;

  public static void main(String[] args) {
    try {
      Array<Integer> array = new Array<Integer>(8024);
      array.set(20, 0);
      System.out.format("get: %d ", array.get(0));
      // array.testUnsafe();
    } catch (Exception e) {
      System.out.println("Error");
      System.out.println(e.getMessage());
    }
  }

  Array (long size) {
    try {
      unsafe = getUnsafe();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    size = size;
    address = unsafe.allocateMemory(size);
    index = 0;
  }

  @SuppressWarnings("sunapi")
  private static Unsafe getUnsafe() throws Exception {
    Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
    singleoneInstanceField.setAccessible(true);
    return (Unsafe) singleoneInstanceField.get(null);
  }

  public void set (T value, int index) throws Exception  {
    byte[] bytes = this.serialize(value);
    for (long i = 0; i < bytes.length; i++)
      unsafe.putInt(address + index + i, (int)bytes[(int)i]);
  }

  public T get (int index) throws Exception  {
    byte[] bytes = new byte[(int)blockSize];
    for (long i = address + index; i < address + index + blockSize; i++)
      bytes[(int)(i - address - index)] = (byte)unsafe.getInt(i);
    return deserialize(bytes);
  }

  private byte[] serialize(T object) throws Exception {
     ByteArrayOutputStream bo = new ByteArrayOutputStream();
     ObjectOutputStream so = new ObjectOutputStream(bo);
     so.writeObject(object);
     so.flush();
     byte[] bytes = bo.toByteArray();
     blockSize = bytes.length;
     return bytes;
  }

  private T deserialize (byte[] bytes) throws Exception {
    ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
    ObjectInputStream si = new ObjectInputStream(bi);
    T object = (T) si.readObject();
    return object;
  }

  private void testUnsafe () throws Exception {
    unsafe.putInt(address + 0, 10);
    System.out.format("%d ", unsafe.getInt(address + 0));
  }
}
