import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.BiFunction;

/**
 * A Buffered Input Reader class that has both the efficiency of the <code>BufferedReader</code>
 * class and the rich API of the <code>Scanner</code> class. Has been tested many times against the
 * <code>Scanner</code> class while reading many numbers or words, and <code>BufferedScanner</code>
 * has greatly outperformed <code>Scanner</code> in terms of elapsed time every time.
 * 
 * @author Milind Upadhyay
 * 
 */
public class BufferedScanner extends BufferedReader {

  /**
   * The value returned by <code>read</code> when a read failure occurs.
   */
  public static final int READ_FAILURE = -1;
  /**
   * The value for any whitespace being counted as a delimiter.
   */
  public static final int WHITESPACE_DELIM = 0;

  private char delim;
  private boolean done;
  private IOException exception;

  /**
   * Creates a new <code>BufferedScanner</code> that will read from the given
   * <code>InputStream</code>.
   * 
   * @param in The <code>InputStream</code> to read from.
   */
  public BufferedScanner(InputStream in) {
    super(new InputStreamReader(in));
    init();
  }

  /**
   * Creates a new <code>BufferedScanner</code> that will read from the file with the given file.
   *
   * @param file The file to read from.
   */
  public BufferedScanner(File file) {
    super(new InputStreamReader(newFileInputStream(file)));
  }

  /**
   * Creates a new <code>BufferedScanner</code> that will read from the file with the given path.
   * 
   * @param path The path of the file to read from.
   */
  public BufferedScanner(String path) {
    super(new InputStreamReader(newFileInputStream(path)));
    init();
  }

  private void init() {
    delim = WHITESPACE_DELIM;
    done = false;
    exception = null;
  }

  private static FileInputStream newFileInputStream(File file) {
    FileInputStream fis;
    try {
      fis = new FileInputStream(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fis = null;
    }
    return fis;
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

  /**
   * Returns the delimiter
   * 
   * @return The delimiter of this scanner, or <code>WHITESPACE_DELIM</code> if whitespace is the
   *         only delimiter.
   */
  public char getDelim() {
    return delim;
  }

  /**
   * Sets the delimiter of this scanner. If not called or <code>delim</code> is
   * <code>WHITESPACE_DELIM</code>, whitespace will be the only delimiter. This delimiter and
   * whitespace are used as a delimiter.
   * 
   * @param delim The delimiter to use, or <code>WHITESPACE_DELIM</code> to only use any whitespace
   *        for a delimiter
   */
  public void setDelim(char delim) {
    this.delim = delim;
  }

  /**
   * Returns <code>true</code> if the stream has been fully read.
   * 
   * @return <code>true</code> if the scanner has finished reading, else <code>false</code>
   */
  public boolean isDone() {
    return done;
  }

  /**
   * Returns the last <code>IOException</code> encountered, or <code>null</code> if there is none.
   * 
   * @return The last <code>IOException</code> encountered, or <code>null</code> if all reads have
   *         been successful.
   */
  public IOException getException() {
    return exception;
  }

  /**
   * Returns <code>true</code> if the stream can continue being read and all reads have been
   * successful.
   * 
   * @return <code>true</code> if the scanner can continue reading, else <code>false</code>
   */
  public boolean isValid() {
    return (!done && exception == null);
  }

  @Override
  public void close() {
    try {
      super.close();
    } catch (IOException e) {
      exception = e;
      e.printStackTrace();
    }
  }

  /**
   * Reads the next character in the buffer.
   * 
   * @return The next character, as an <code>int</code>, or <code>READ_FAILURE</code> if an
   *         <code>IOException</code> occurred or <code>isDone()</code>.
   */
  @Override
  public int read() {
    int i = READ_FAILURE;
    try {
      i = super.read();
      done = (i == READ_FAILURE);
    } catch (IOException e) {
      exception = e;
      e.printStackTrace();
      i = READ_FAILURE;
    }
    return i;
  }

  /**
   * Reads a line of text. A line is considered to be terminated by any one of a line feed ('\n'), a
   * carriage return ('\r'), a carriage return followed immediately by a line feed, or by reaching
   * the end-of-file(EOF).
   * 
   * @return A String containing the contents of the line, not including any line-termination
   *         characters, or null if the end of the stream has been reached without reading any
   *         characters or an <code>IOException</code> occurred.
   */
  @Override
  public String readLine() {
    String s = null;
    try {
      s = super.readLine();
    } catch (IOException e) {
      exception = e;
      e.printStackTrace();
      s = null;
    }
    return s;
  }

  private <T> T read(T t, BiFunction<T, Integer, T> add) {
    int i = read();

    // skip whitespace before characters that matter
    while (Character.isWhitespace(i)) {
      i = read();
    }

    // read until error or next whitespace
    while ((i != READ_FAILURE) && !isDelim((char) i)) {
      t = add.apply(t, i);
      i = read();
    }

    return t;
  }

  private boolean isDelim(char c) {
    return (Character.isWhitespace(c) || (c != WHITESPACE_DELIM && c == delim));
  }

  /**
   * Reads the next <code>StringBuilder</code> in the <code>InputStream</code>.
   * 
   * @return The next <code>StringBuilder</code>, containing all the characters until the next
   *         whitespace character in the <code>InputStream</code>, the end of the
   *         <code>InputStream</code>, or the occurrence of an <code>IOException</code>
   */
  public StringBuilder readString() {
    return read(new StringBuilder(), (sb, i) -> sb.append((char) i.intValue()));
  }

  /**
   * Reads the next <code>int</code> in the <code>InputStream</code>, ignoring all non-digit
   * characters encountered except a '-' at the beginning.
   * 
   * @return The next <code>int</code>, containing all the digits until the next whitespace
   *         character in the <code>InputStream</code>, the end of the <code>InputStream</code>, or
   *         the occurrence of an <code>IOException</code>.
   */
  public int readInt() {
    var negativeInt = new Wrapper<Boolean>(false);
    var firstChar = new Wrapper<Boolean>(true);

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
    var negativeDouble = new Wrapper<Boolean>(false);
    var decimal = new Wrapper<Boolean>(false);
    var decimalPow = new Wrapper<Double>(0.0);
    var firstChar = new Wrapper<Boolean>(true);

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
        result = (decimal.get() ? i : i * 10) + (decimal.get() ? decimalPow.get() * digit : digit);
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
  public int[] readInts(int len) {
    var a = new int[len];
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
  public int[][] readIntMat(int m, int n) {
    var mat = new int[m][n];
    for (var row : mat) {
      readInts(row);
    }
    return mat;
  }

  private void readDoubles(double[] a) {
    for (int i = 0; i < a.length; i++) {
      a[i] = readDouble();
    }
  }

  /**
   * Returns a <code>double[]</code> with the next <code>len</code> <code>double</code>s in the
   * <code>InputStream</code>. Equivalent to setting every element in the array to be
   * <code>readDouble</code>. <br>
   * Terminates reading if the end of the <code>InputStream</code> or an <code>IOException</code>
   * occurs.
   * 
   * @param len The length of the array.
   * @return A <code>double[]</code> of length <code>len</code> filled with the next
   *         <code>double</code>s in the <code>InputStream</code>.
   */
  public double[] readDoubles(int len) {
    var a = new double[len];
    readDoubles(a);
    return a;
  }

  /**
   * Returns a m by n <code>double[][]</code> with the next <code>double</code>s in the
   * <code>InputStream</code>. Equivalent to calling <code>readDoubles</code> for every row in the
   * 2d array. <br>
   * Terminates reading if the end of the <code>InputStream</code> or an <code>IOException</code>
   * occurs.
   * 
   * @param m The number of rows in the 2d array
   * @param n The number of columns in the 2d array
   * @return A matrix with m rows and n columns with the next m * n <code>double</code>s in the
   *         <code>InputStream</code>.
   */
  public double[][] readDoubleMat(int m, int n) {
    var mat = new double[m][n];
    for (var row : mat) {
      readDoubles(row);
    }
    return mat;
  }


  private void readStrings(StringBuilder[] a) {
    for (int i = 0; i < a.length; i++) {
      a[i] = readString();
    }
  }

  /**
   * Returns a <code>StringBuilder[]</code> with the next <code>len</code>
   * <code>StringBuilder</code>s in the <code>InputStream</code>. Equivalent to setting every
   * element in the array to be <code>readString</code>. <br>
   * Terminates reading if the end of the <code>InputStream</code> or an <code>IOException</code>
   * occurs.
   * 
   * @param len The length of the array.
   * @return A <code>StringBuilder[]</code> of length <code>len</code> filled with the next
   *         <code>StringBuilder</code>s in the <code>InputStream</code>.
   */
  public StringBuilder[] readStrings(int len) {
    var a = new StringBuilder[len];
    readStrings(a);
    return a;
  }

  /**
   * Returns an m by n <code>StringBuilder[][]</code> with the next <code>StringBuilder</code>s in
   * the <code>InputStream</code>. Equivalent to calling <code>readStrings</code> for every row in
   * the 2d array. <br>
   * Terminates reading if the end of the <code>InputStream</code> or an <code>IOException</code>
   * occurs.
   * 
   * @param m The number of rows in the 2d array
   * @param n The number of columns in the 2d array
   * @return A matrix with m rows and n columns with the next m * n <code>StringBuilder</code>s in
   *         the <code>InputStream</code>.
   */
  public StringBuilder[][] readStringMat(int m, int n) {
    var mat = new StringBuilder[m][n];
    for (var row : mat) {
      readStrings(row);
    }
    return mat;
  }

  /**
   * Example use of <code>BufferedScanner</code> reading from stdin.
   * 
   * @param args Not using command line arguments
   */
  public static void main(String[] args) {
    var cin = new BufferedScanner();

    System.out.printf("double: %f%n", cin.readDouble());
    System.out.printf("str: %s%n", cin.readString());

    int[] a = cin.readInts(4);
    System.out.print("int[]: ");
    for (int i : a) {
      System.out.printf("%d ", i);
    }
    System.out.println();

    int[][] m = cin.readIntMat(2, 3);
    System.out.println("int[][]:");
    for (var row : m) {
      for (int i : row) {
        System.out.printf("%d\t", i);
      }
      System.out.println();
    }

    cin.close();
  }

  private static class Wrapper<T> {
    private T t;

    public Wrapper(T t) {
      this.t = t;
    }

    public T get() {
      return t;
    }

    public void set(T t) {
      this.t = t;
    }

  }

}
