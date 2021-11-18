import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Class to demonstrate the efficiency boost of <code>BufferedScanner</code> over
 * <code>Scanner</code>
 */
public class ScannerTest {

  private static final int READS = 215711;

  private static final String WORDS = "moby_dick.txt";
  private static final String DOUBLES = "doubles.txt";

  private static double seconds(final long ns) {
    return ns / Math.pow(10, 9);
  }

  private static void writeDoubles() {
    PrintStream out = null;
    try {
      out = new PrintStream(DOUBLES);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    for (int i = 0; i < READS; i++) {
      out.print((Math.random() >= 0.5 ? 1 : -1) * (Math.random() * 100.0));
      out.print(" ");
      if (Math.random() > 0.3) {
        out.println();
      }
    }
  }

  private static void test(final boolean words) {
    long start = System.nanoTime();
    final String fileName = (words ? WORDS : DOUBLES);

    Scanner s = null;;
    try {
      s = new Scanner(new FileInputStream(fileName));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    Runnable read = (words ? s::next : s::nextDouble);
    for (int i = 0; i < READS; i++) {
      read.run();
    }
    long end = System.nanoTime();
    final double scannerTime = seconds(end - start);
    System.out.printf("Scanner elapsed time: %fs%n", scannerTime);
    s.close();

    start = System.nanoTime();
    var bs = new BufferedScanner(fileName);
    read = (words ? bs::readString : bs::readDouble);
    for (int i = 0; i < READS; i++) {
      read.run();
    }
    end = System.nanoTime();
    final double bufScannerTime = seconds(end - start);
    System.out.printf("Buffered Scanner elapsed time: %fs%n", bufScannerTime);
    bs.close();

    final double timeDiff = Math.abs(bufScannerTime - scannerTime);
    final double pctDiff =
        (Math.min(scannerTime, bufScannerTime) / Math.max(scannerTime, bufScannerTime)) * 100.0;
    System.out.printf("%sScanner was %fs better. (%f%% of the other)%n%n",
        bufScannerTime < scannerTime ? "Buffered" : "", timeDiff, pctDiff);
  }

  public static void main(String[] args) {
    writeDoubles();
    System.out.println("words:");
    test(true);
    System.out.println("doubles:");
    test(false);
  }

}
