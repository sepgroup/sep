package com.sepgroup.sep.tests.system;

import java.util.Random;

/**
 * Created by Wishe on 6/4/2016.
 */
public final class RandomUtility
{
	private static final Random random = new Random();

	private RandomUtility() {}

	/**
	 * Produces a random integer from min to max, exclusive.
	 * @param min The minimum of the random integer.
	 * @param max The maximum of the random integer.
	 * @return The random integer.
	 */
	public static int randomInt(final int min, final int max)
	{
		return random.nextInt(max - min) + min;
	}

	public static int nextInt()
	{
		return random.nextInt();
	}
}