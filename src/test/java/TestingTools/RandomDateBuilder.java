package TestingTools;
import java.util.Random;

public final class RandomDateBuilder
{
	private static final Random random = new Random();
	public static final int validYearMin = 1900;
	public static final int validYearMax = 2100;

	/**
	 * Used to return pairs of dates.
	 */
	public static class StringPair
	{
		public String first;
		public String second;

		public StringPair(final String first, final String second)
		{
			this.first = first;
			this.second = second;
		}
	}

	/**
	 * Returns a string representing a date in the format yyyy-mm-dd.
	 * @param isValid True if the string should represent a valid date.
	 * @return The date string.
	 */
	public static String randomDateString(final boolean isValid)
	{
		final int year = randomYear(isValid);
		final int month = randomMonth(isValid);
		final int day = randomDay(isValid, month, year);
		return dateToString(year, month, day);
	}

	/**
	 * Returns a valid date string which represents a date after the given date.
	 * @param minYear The earliest year of the date.
	 * @param minMonth The earliest month of the date.
	 * @param minDay The earliest day of the date.
	 * @return The random date.
	 */
	public static String randomDateString(final int minYear, final int minMonth, final int minDay)
	{
		final int year = randomYear(minYear);
		final int month = randomMonth(minMonth);
		final int day = randomDay(minDay, month, year);
		return dateToString(year, month, day);
	}

	private static String dateToString(final int year, final int month, final int day)
	{
		return year + "-" + month + "-" + day;
	}

	/**
	 * Returns a pair of random dates.
	 * @param isValid True if both dates are valid dates and the second date comes strictly after the first.
	 * @return The pair of dates.
	 */
	public static StringPair randomDateStringPair(final boolean isValid)
	{
		final int year1 = randomYear(isValid);
		final int month1 = randomMonth(isValid);
		final int day1 = randomDay(isValid, month1, year1);
		final String date1 = dateToString(year1, month1, day1);

		final int year2 = isValid ? randomYear(year1) : randomYear(isValid);
		final int month2 = isValid ? randomMonth(month1) : randomMonth(isValid);
		final int day2 = isValid ? randomDay(day1, month2, year2) : randomDay(isValid, month2, year2);
		final String date2 = dateToString(year2, month2, day2);

		return new StringPair(date1, date2);
	}


	private static int randomMonth(final boolean valid)
	{
		return valid ? randomMonth(0) : random.nextInt();
	}

	private static int randomMonth(final int minMonth)
	{
		if (minMonth >= 12)
			throw new IllegalArgumentException("Parameter minMonth must be less than 12. Received " + minMonth + ".");
		return randomInt(Math.max(0, minMonth), 12);
	}

	private static int randomDay(final boolean valid, final int month, final int year)
	{
		return valid ? randomDay(0, month, year) : random.nextInt();
	}

	private static int randomDay(final int minDay, final int month, final int year)
	{
		int daysInMonth = daysInMonth(month, year);
		if (minDay >= daysInMonth)
			throw new IllegalArgumentException("Parameter minDay must be less than daysInMonth" + daysInMonth + ". Received " + minDay + ".");
		return randomInt(minDay, daysInMonth);
	}

	private static int daysInMonth(final int month, final int year)
	{
		int daysInMonth;
		switch (month)
		{
			case 3:
			case 5:
			case 8:
			case 10:
				daysInMonth = 30;
				break;
			case 1:	// February
				if (year % 4 == 0)	// Leap year
					daysInMonth = 29;
				else
					daysInMonth = 28;
				break;
			default:
				daysInMonth = 31;
				break;
		}
		return daysInMonth;
	}

	private static int randomYear(final boolean isValid)
	{
		int year;
		if (isValid)
			year = randomYear(validYearMin);
		else
			year = random.nextInt();
		return year;
	}

	private static int randomYear(final int minYear)
	{
		if (minYear >= validYearMax)
			throw new IllegalArgumentException("Parameter minYear must be before validYearearliest " + validYearMax + ". Received " + minYear + ".");
		return randomInt(minYear, validYearMax);
	}

	/**
	 * Produces a random integer from min to max, exclusive.
	 * @param min The minimum of the random integer.
	 * @param max The maximum of the random integer.
	 * @return The random integer.
	 */
	private static int randomInt(final int min, final int max)
	{
		return random.nextInt(max - min) + min;
	}
}