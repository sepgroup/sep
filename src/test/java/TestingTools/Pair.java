package TestingTools;

/**
 * Used to store monomorphic pairs of objects.
 */
public final class Pair<T>
{
	public T first;
	public T second;

	public Pair(final T first, final T second)
	{
		this.first = first;
		this.second = second;
	}

	public Pair swap()
	{
		return new Pair<>(second, first);
	}

	public String toString()
	{
		return "(" + first + ", " + second + ")";
	}

}