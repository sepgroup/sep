package TestingTools;

import java.text.ParseException;
import java.util.Date;

public class TestClass
{
    public static void main(String[] args)
    {
        for (int i = 1; ; i++)
        {
            try
            {
                RandomDateBuilder.Pair<Date> pair = RandomDateBuilder.randomDatePair(true);
                System.out.println(i + ": " + pair.first + " " + pair.second);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
    }
}