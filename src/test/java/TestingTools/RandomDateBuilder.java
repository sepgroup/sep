package TestingTools;

import com.sepgroup.sep.utils.DateUtils;

import java.text.ParseException;
import java.util.Date;

public final class RandomDateBuilder
{
	public static final int validYearMin = 1900;
	public static final int validYearMax = 2100;

	public static Pair<Date> randomDatePair(final boolean isValid) throws ParseException
	{
		Pair<String> stringPair = randomDateStringPair(isValid);
		Date date1 = DateUtils.castStringToDate(stringPair.first);
		Date date2 = DateUtils.castStringToDate(stringPair.second);
		return new Pair<>(date1, date2);
	}

	public static Pair<Date> randomDatePair() throws ParseException
	{
		return randomDatePair(false);
	}
	public static Date randomDate(final boolean isValid) throws ParseException
	{
		return DateUtils.castStringToDate(randomDateString(isValid));
	}

	/**
	 * Returns a string representing a date in the format yyyy-mm-dd.
	 * @param isValid True if the string should represent a valid date.
	 * @return The date string.
	 */
	public static String randomDateString(final boolean isValid)
	{
		final int year = randomYear();
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
	 * Returns a pair of random valid dates.
	 * @param isValid True if the second date comes strictly after the first.
	 * @return The pair of dates.
	 */
	public static Pair<String> randomDateStringPair(final boolean isValid)
	{
		final int year1 = randomYear();
		final int month1 = randomMonth(true);
		int day1 = randomDay(true, month1, year1);
		final String date1 = dateToString(year1, month1, day1);

		final int year2 = isValid ? randomYear(year1) : randomYear();
		final int month2 = isValid ? randomMonth(month1) : randomMonth(true);

		day1 = clamp(day1, daysInMonth(month2, year2) - 1);

		final int day2 = isValid ? randomDay(day1, month2, year2) : randomDay(true, month2, year2);
		final String date2 = dateToString(year2, month2, day2);

		return isValid ? new Pair<>(date1, date2) : new Pair<>(date2, date1);
	}

	private static int clamp(final int value, final int max)
	{
		return value < max ? value : max;
	}

	private static int randomMonth(final boolean valid)
	{
		return valid ? randomMonth(0) : RandomUtility.nextInt();
	}

	private static int randomMonth(final int minMonth)
	{
		if (minMonth >= 12)
			throw new IllegalArgumentException("Parameter minMonth must be less than 12. Received " + minMonth + ".");
		return RandomUtility.randomInt(Math.max(0, minMonth), 12);
	}

	private static int randomDay(final boolean valid, final int month, final int year)
	{
		return valid ? randomDay(0, month, year) : RandomUtility.nextInt();
	}

	private static int randomDay(final int minDay, final int month, final int year)
	{
		int daysInMonth = daysInMonth(month, year);
		if (minDay >= daysInMonth)
			throw new IllegalArgumentException("Parameter minDay must be less than daysInMonth " + daysInMonth + ". Received " + minDay + ".");
		return RandomUtility.randomInt(minDay, daysInMonth);
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

	private static int randomYear()
	{
		return randomYear(validYearMin);
	}

	private static int randomYear(final int minYear)
	{
		if (minYear >= validYearMax)
			throw new IllegalArgumentException("Parameter minYear must be before validYearMax " + validYearMax + ". Received " + minYear + ".");
		return RandomUtility.randomInt(minYear, validYearMax);
	}

}