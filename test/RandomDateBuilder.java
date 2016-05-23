import java.util.Random;

public final class RandomDateBuilder
{
	private static final Random random = new Random();

	/**
	 * Returns a string representing a date in the format yyyy-mm-dd.
	 * @param valid True if and only if the string should represent a valid date.
	 * @return The date string.
	 */
	public static String getRandomDateString(final boolean valid)
	{
		return null; // Stub
	}

	private static int getRandomMonth(final boolean valid)
	{
		int month = random.nextInt();
		if (valid)
		{
			month %= 12;
		}
		return month;
	}

	private static int getRandomDay(final int month, final int year, final boolean valid)
	{
		int day = random.nextInt();

		if (valid)
		{
			int days;
			switch (month)
			{
				case 0:
				case 2:
				case 4:
				case 6:
				case 7:
				case 9:
				case 11:
					days = 31;
					break;
				case 1:	// February
					if (year % 4 == 0)	// Leap year
						days = 29;
					else
						days = 28;
					break;
				default:
					days = 30;
					break;
			}
			day %= days;
		}

		return day;
	}

	private static int getRandomYear()
	{
		return random.nextInt();
	}
}
