package TestingTools;

/**
 * Used to store monomorphic pairs of objects.
 * @param <T> The type stored in the pair.
 */
public final class Pair<T>
{
	public T first;		// First element
	public T second;	// Second element

	/**
	 * Constructor.
	 * @param first First element of the pair.
	 * @param second Second element of the pair.
	 */
	public Pair(final T first, final T second)
	{
		this.first = first;
		this.second = second;
	}

	/**
	 * Returns a pair with the two elements swapped.
	 * @return A new pair with the two elements swapped.
	 */
	public Pair<T> swap()
	{
		return new Pair<>(second, first);
	}

	/**
	 * Returns string representation of the pair.
	 * @return A string representation of the pair.
	 */
	public String toString()
	{
		return "(" + first + ", " + second + ")";
	}

}