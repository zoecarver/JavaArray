import java.util.Random;

public class SytemArrayTest {
    
    public static void main(String[] args) {
        Random random = new Random();

        Integer[] array = new Integer[1000000];
        
        for (int i = 0; i < 1000000; i++)
            array[i] = random.nextInt(100);
        
        for (int i = 0; i < 1000000; i++)
            System.out.format("found: %s at index: %d \n", array[i], i);
    }
}
