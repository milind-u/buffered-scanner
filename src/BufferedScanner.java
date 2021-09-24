import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

/**
 * A Buffered Input Reader class that has both the efficiency of the <code>BufferedReader</code>
 * class and the rich API of the <code>Scanner</code> class. Has been tested many times against the
 * <code>Scanner</code> class while reading many numbers or words, and <code>BufferedScanner</code>
 * has greatly outperformed <code>Scanner</code> in terms of elapsed time every time.
 */
public class BufferedScanner extends BufferedReader {

  /**
   * Creates a new <code>BufferedScanner</code> that will read from the given
   * <code>InputStream</code>.
   * 
   * @param in The <code>InputStream</code> to read from.
   */
  public BufferedScanner(InputStream in) {
    super(new InputStreamReader(in));
  }

  /**
   * Creates a new <code>BufferedScanner</code> that will read from the file with the given path.
   * 
   * @param String The path of the file to read from.
   */
  public BufferedScanner(String path) {
    super(new InputStreamReader(newFileInputStream(path)));
  }

  private static FileInputStream newFileInputStream(String path) {
    FileInputStream fis;
    try {
      fis = new FileInputStream(path);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fis = null;
    }
    return fis;
  }

  /**
   * Creates a new <code>BufferedScanner</code> that will read from <code>System.in</code> (stdin).
   */
  public BufferedScanner() {
    this(System.in);
  }

  @Override
  public void close() {
    try {
      super.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public int read() {
    int i = -1;
    try {
      i = super.read();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return i;
  }

  private <T> T read(T t, BiFunction<T, Integer, T> add) {
    int i = read();

    // skip whitespace before characters that matter
    while (Character.isWhitespace(i)) {
      i = read();
    }

    // read till error or next whitespace
    while ((i != -1) && !Character.isWhitespace(i)) {
      t = add.apply(t, i);
      i = read();
    }

    return t;
  }

  /**
   * Reads the next <code>StringBuffer</code> in the <code>InputStream</code>.
   * 
   * @return The next <code>StringBuffer</code>, containing all the characters until the next
   *         whitespace character in the <code>InputStream</code>, the end of the
   *         <code>InputStream</code>, or the occurrence of an <code>IOException</code>
   */
  public StringBuffer readStringBuffer() {
    return read(new StringBuffer(), (sb, i) -> sb.append((char) i.intValue()));
  }

  /**
   * Reads the next <code>int</code> in the <code>InputStream</code>, ignoring all non-digit
   * characters encountered except a '-' at the beginning.
   * 
   * @return The next <code>int</code>, containing all the digits until the next whitespace character
   *         in the <code>InputStream</code>, the end of the <code>InputStream</code>, or the
   *         occurrence of an <code>IOException</code>.
   */
  public int readInt() {
    AtomicBoolean negativeInt = new AtomicBoolean();
    AtomicBoolean firstChar = new AtomicBoolean(true);

    int next = read(0, (i, j) -> {
      // i is the current value read thus far, j is the next digit to add
      int result = i;
      if (firstChar.get()) {
        if (j == '-') {
          negativeInt.set(true);
        }
        firstChar.set(false);
      }
      if (Character.isDigit(j)) {
        result = (i * 10) + Character.getNumericValue(j);
      }
      return result;
    }); // end lambda

    if (negativeInt.get()) {
      next *= -1;
    }
    return next;
  }

  /**
   * Reads the next <code>double</code> in the <code>InputStream</code>, ignoring all non-digit
   * characters encountered except a '-' at the beginning and the first '.' encountered.
   * 
   * @return The next <code>double</code>, containing all the digits until the next whitespace
   *         character in the <code>InputStream</code>, the end of the <code>InputStream</code>, or
   *         the occurrence of an <code>IOException</code>.
   */
  public double readDouble() {
    AtomicBoolean negativeDouble = new AtomicBoolean();
    AtomicBoolean decimal = new AtomicBoolean();
    AtomicReference<Double> decimalPow = new AtomicReference<Double>();
    AtomicBoolean firstChar = new AtomicBoolean(true);

    double next = read(0.0, (i, j) -> {
      double result = i;
      if (firstChar.get()) {
        if (j == '-') {
          negativeDouble.set(true);
        }
        firstChar.set(false);
      }
      if (!decimal.get() && j == '.') {
        decimal.set(true);
        decimalPow.set(0.1);
      } else if (Character.isDigit(j)) {
        final int digit = Character.getNumericValue(j);
        result = (decimal.get() ? i : i * 10)
            + (decimal.get() ? decimalPow.get() * digit : digit);
        if (decimal.get()) {
          decimalPow.set(decimalPow.get() / 10.0);
        }
      }
      return result;
    });
    if (negativeDouble.get()) {
      next *= -1;
      negativeDouble.set(false);;
    }
    return next;
  }

  private void readInts(int[] a) {
    for (int i = 0; i < a.length; i++) {
      a[i] = readInt();
    }
  }

  /**
   * Returns an <code>int[]</code> with the next <code>len</code> <code>int</code>s in the
   * <code>InputStream</code>. Equivalent to setting every element in the array to be
   * <code>readInt</code>. <br>
   * Terminates reading if the end of the <code>InputStream</code> or an <code>IOException</code>
   * occurs.
   * 
   * @param len The length of the array.
   * @return An <code>int[]</code> of length <code>len</code> filled with the next <code>int</code>s
   *         in the <code>InputStream</code>.
   */
  public int[] readInts(final int len) {
    int[] a = new int[len];
    readInts(a);
    return a;
  }

  /**
   * Returns an m by n <code>int[][]</code> with the next <code>int</code>s in the
   * <code>InputStream</code>. Equivalent to calling <code>readInts</code> for every row in the 2d
   * array. <br>
   * Terminates reading if the end of the <code>InputStream</code> or an <code>IOException</code>
   * occurs.
   * 
   * @param m The number of rows in the 2d array
   * @param n The number of columns in the 2d array
   * @return A matrix with m rows and n columns with the next m * n <code>int</code>s in the
   *         <code>InputStream</code>.
   */
  public int[][] readIntMat(final int m, final int n) {
    int[][] mat = new int[m][n];
    for (int[] row : mat) {
      readInts(row);
    }
    return mat;
  }

  private void readStringBuffers(StringBuffer[] a) {
    for (int i = 0; i < a.length; i++) {
      a[i] = readStringBuffer();
    }
  }

  /**
   * Returns an <code>StringBuffer[]</code> with the next <code>len</code> <code>StringBuffer</code>s
   * in the <code>InputStream</code>. Equivalent to setting every element in the array to be
   * <code>readStringBuffer</code>. <br>
   * Terminates reading if the end of the <code>InputStream</code> or an <code>IOException</code>
   * occurs.
   * 
   * @param len The length of the array.
   * @return An <code>StringBuffer[]</code> of length <code>len</code> filled with the next
   *         <code>StringBuffer</code>s in the <code>InputStream</code>.
   */
  public StringBuffer[] readStringBuffers(final int len) {
    StringBuffer[] a = new StringBuffer[len];
    readStringBuffers(a);
    return a;
  }

  /**
   * Returns an m by n <code>StringBuffer[][]</code> with the next <code>StringBuffer</code>s in the
   * <code>InputStream</code>. Equivalent to calling <code>readStringBuffers</code> for every row in
   * the 2d array. <br>
   * Terminates reading if the end of the <code>InputStream</code> or an <code>IOException</code>
   * occurs.
   * 
   * @param m The number of rows in the 2d array
   * @param n The number of columns in the 2d array
   * @return A matrix with m rows and n columns with the next m * n <code>StringBuffer</code>s in the
   *         <code>InputStream</code>.
   */
  public StringBuffer[][] readStringBufferMat(final int m, final int n) {
    StringBuffer[][] mat = new StringBuffer[m][n];
    for (StringBuffer[] row : mat) {
      readStringBuffers(row);
    }
    return mat;
  }

  /**
   * Example use of <code>BufferedScanner</code> reading from stdin.
   * 
   * @param args Not using command line arguments
   */
  public static void main(String[] args) {
    BufferedScanner cin = new BufferedScanner();

    System.out.printf("double: %f%n", cin.readDouble());
    System.out.printf("strbuf: %s%n", cin.readStringBuffer());

    int[] a = cin.readInts(4);
    System.out.print("int[]: ");
    for (int i : a) {
      System.out.printf("%d ", i);
    }
    System.out.println();

    int[][] m = cin.readIntMat(2, 3);
    System.out.println("int[][]:");
    for (int[] row : m) {
      for (int i : row) {
        System.out.printf("%d\t", i);
      }
      System.out.println();
    }

    cin.close();
  }

}
