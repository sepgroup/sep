package TestingTools;

import org.junit.Test;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.utils.DateUtils;
import java.util.Date;
import java.util.Random;

/**
 * Created by Wired on 2016-06-04.
 */
public class TaskTest {

    TaskModel[] input;
    TaskModel[] output;

    @Test
    public void creationTest()
    {
        Random random = new Random();
        try {
            int count = 1000;
            input = new TaskModel[count];
            output = new TaskModel[count];

            int i;
            for(i = 0; i < 500; i++)
            {

                String name = RandomStringBuilder.randomString(random.nextInt(50));
                String description = RandomStringBuilder.randomString(random.nextInt(200));
                int projectID = random.nextInt(200);
                float budget = random.nextFloat() * 1000;
                Date startDate = new Date();
                Date deadline = new Date();
                int userID = random.nextInt(200);

                input[i] = new TaskModel(name, description, projectID, budget, startDate, deadline, false, userID);
                input[i].persistData();
                output[i] = TaskModel.getById(input[i].getTaskId());

                System.out.println(input[i]);
                System.out.println(output[i]);
                System.out.println("\n");
            }

            System.out.println(i);
        }
        catch(Exception e){
            System.err.println("CHINCHILLA");
        }
    }
}
