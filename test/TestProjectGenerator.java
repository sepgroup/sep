import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Random;

/**
 * Created by Wishe on 5/21/2016.
 */

// Can generate many sorts of random projects for testing the application
public class TestProjectGenerator
{
	// Seeds random number generator with current time UTC in seconds
	private static final Random random = new Random(ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond() * 1000);

	// Used to generate random strings of a given fixed length.
	private static class RandomStringBuilder
	{
		// Holds all available symbols to construct strings from
		private static final char[] symbols;

		// Used to hold characters as a string is being constructed
		private final char[] buffer;

		// Initialize symbols with alphanumeric characters
		static
		{
			StringBuilder sb = new StringBuilder();
			for (char c = '0'; c <= '9'; c++)
			{
				sb.append(c);
			}
			for (char c = 'a'; c <= 'z'; c++)
			{
				sb.append(c);
			}
			for (char c = 'A'; c <= 'Z'; c++)
			{
				sb.append(c);
			}
			symbols = sb.toString().toCharArray();
		}

		// Constructs a RandomStrignBuilder for making strings of a given fixed length.
		private RandomStringBuilder(final int length)
		{
			buffer = new char[length];
		}

		// Generates the next random string.
		private String nextString()
		{
			for (int i = 0; i < buffer.length; i++)
			{
				buffer[i] = symbols[random.nextInt(symbols.length)];
			}
			return new String(buffer);
		}
	}

	public static String randomString(final int length)
	{
		return new RandomStringBuilder(length).nextString();
	}

	public static void main(String[] args)
	{
		System.out.println(randomString(10));
	}
}
