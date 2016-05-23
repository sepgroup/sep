import java.util.Hashtable;
import java.util.Random;

/**
 * Used to generate random strings of a given fixed length.
 */
public final class RandomStringBuilder
{
	// Seeds random number generator with current time UTC in seconds
	private static final Random random = new Random();

	// Holds all available symbols to construct strings from
	private static final char[] symbols;

	// Used to hold characters as a string is being constructed
	private final char[] buffer;

	private static Hashtable<Integer, RandomStringBuilder> builders = new Hashtable<>();

	// Initialize symbols with alphanumeric characters, minuscule and majuscule
	static
	{
		StringBuilder sb = new StringBuilder();

		// Alphanumerics
		appendCharRange(sb, '0', '9');
		appendCharRange(sb, 'a', 'z');
		appendCharRange(sb, 'A', 'Z');

		// Whitespace
		sb.append(' ');
		sb.append('\t');
		sb.append('\n');


		symbols = sb.toString().toCharArray();
	}

	/**
	 * Appends a range of characters to a StringBuilder.
	 * @param sb The StringBuilder to append to.
	 * @param first The first character in the appending range.
	 * @param last The last character in the appending range.
	 */
	private static void appendCharRange(final StringBuilder sb, final char first, final char last)
	{
		for (char c = first; c <= last; c++)
			sb.append(c);
	}

	/**
	 * Constructs a RandomStringBuilder for making strings of a given fixed length.
	 * @param length The length of Strings produced by the RandomStringBuilder.
	 */
	private RandomStringBuilder(final int length)
	{
		if (length <= 0)
			throw new IllegalArgumentException("RandomStringBuilder must have positive length. Received " + length + ".");
		buffer = new char[length];
	}

	/**
	 * Generates the next random string.
	 * @param withNewline True if and only if the generated string is to produce newline characters.
	 * @returns A random String.
	 */
	private String nextString(final boolean withNewline)
	{
		for (int i = 0; i < buffer.length; i++)
			buffer[i] = symbols[random.nextInt(symbols.length + (withNewline ? 0 : -1))];
		return new String(buffer);
	}

	/**
	 * Generates the next random string, including newline characters.
	 * @returns A random String.
	 */
	private String nextString()
	{
		return nextString(false);
	}

	/**
	 * Generates a random string with a specified length.
	 * @param length
	 * @param withNewline True if and only if the generated string is to produce newline characters.
	 * @returns A random String.
	 */
	public static String randomString(final int length, final boolean withNewline)
	{
		RandomStringBuilder builder = builders.get(length);
		if (builder == null)
			builders.put(length, builder = new RandomStringBuilder(length));
		return builder.nextString(withNewline);
	}

	/**
	 * Generates a random string with a specified length.
	 * @param length
	 * @returns A random String.
	 */
	public static String randomString(final int length)
	{
		return randomString(length, false);
	}
}