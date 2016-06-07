package TestingTools;

/**
 * Used to store monomorphic pairs of objects.
 * @param <T> The type stored in the pair.
 */
public final class Pair<T>
{
	public T first;		// First element
	public T second;	// Second element

	private int hashCode;	// Immutable at construction

	/**
	 * Constructor.
	 * @param first First element of the pair.
	 * @param second Second element of the pair.
	 */
	public Pair(final T first, final T second)
	{
		this.first = first;
		this.second = second;
		hashCode = first.hashCode() + 1009 * second.hashCode();
	}

	/**
	 * Copy constructor.
	 * @param pair The Pair object to copy.
	 */
	public Pair(final Pair<T> pair)
	{
		this(pair.first, pair.second);
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

	/**
	 * Checks if two Objects are equal to each other.
	 * @param other The Object to compare the Pair to
	 * @return True if and only if the two Objects are equal
	 */
	public boolean equals(final Object other)
	{
		if (other == null || !(other instanceof Pair<?>))
			return false;
		Pair<?> otherPair = (Pair<?>) other;
		return first.equals(otherPair.first) && second.equals(otherPair.second);
	}

	public int hashCode()
	{
		return hashCode;
	}

	public Pair<T> copy()
	{
		return new Pair<>(this);
	}
}