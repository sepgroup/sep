/**
 * Created by Wishe on 5/21/2016.
 */

// Can generate many sorts of random projects for testing the application
public class TestProjectGenerator
{

	public static void main(String[] args)
	{
		System.out.println(RandomStringBuilder.randomString(1000, true));
		System.out.println(RandomStringBuilder.randomString(10));
		System.out.println(RandomStringBuilder.randomString(10));
		System.out.println(RandomStringBuilder.randomString(11));
		System.out.println(RandomStringBuilder.randomString(10));
	}
}
