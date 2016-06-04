package TestingTools;

import org.junit.Test;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.utils.DateUtils;

/**
 * Created by Wired on 2016-06-04.
 */
public class TaskTest {

    @Test
    public void creationTest()
    {
        TaskModel t = new TaskModel("Chinchilla Stuff", "Doing stuff that chinchillas would do", 1, 1000000, DateUtils.castStringToDate("2017-03=05"), DateUtils.castStringToDate("2018-07-01"), false, 1);
    }
}
