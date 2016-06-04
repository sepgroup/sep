package TestingTools;

/**
 * Created by Wishe on 5/21/2016.
 */

// Can generate many sorts of random projects for testing the application
public class TestProjectGenerator
{

	public static void main(String[] args)
	{
		RandomDateBuilder.StringPair dates = RandomDateBuilder.randomDateStringPair(true);
		System.out.println(RandomStringBuilder.randomString(100000000));
		System.out.println(dates.first);
		System.out.println(dates.second);
	}
}
