package TestingTools;

import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.tests.ut.db.*;
import com.sepgroup.sep.controller.DialogCreator;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.*;
import com.sepgroup.sep.utils.DateUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.Random;

import com.sepgroup.sep.db.DBConfig;
import org.aeonbits.owner.ConfigFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Wired on 2016-06-05.
 */
public class TaskTest {
    TaskModel[] tasksIn, tasksOut;
    boolean[] hasValidName, hasValidDescription, hasValidBudget, hasValidProjectID, hasValidDatePair, hasValidUserID;

    @BeforeClass
    public static void setUpBeforeMethod() throws Exception {
        ConfigFactory.setProperty("configPath", DatabaseTest.class.getResource("/test-db.properties").getFile());
    }

    @Test
    public void creationTest() {
        Random random = new Random();
        int numOfTests = 100;
        tasksIn = new TaskModel[numOfTests];
        tasksOut = new TaskModel[numOfTests];
        hasValidName = new boolean[numOfTests];
        hasValidDescription = new boolean[numOfTests];
        hasValidBudget = new boolean[numOfTests];
        hasValidProjectID = new boolean[numOfTests];
        hasValidDatePair = new boolean[numOfTests];
        hasValidUserID = new boolean[numOfTests];

        UserModel user = new UserModel("Coco", "Gonzalez", 20.04);
        try{
            user.persistData();
        }
        catch(Exception e) {
            assert(false);
        }

        ProjectModel project = new ProjectModel("Chinchilla Project", new Date(118, 2, 19), new Date(118, 2, 27), 17496.45, false, user.getUserId(), "Chinchilla stuff");
        try{
            project.persistData();
        }
        catch(Exception e) {
            assert(false);
        }

        for(int i = 0; i < numOfTests; i++) {
            hasValidName[i] = random.nextBoolean();
            hasValidDescription[i] = true;
            hasValidProjectID[i] = random.nextBoolean();
            hasValidDatePair[i] = random.nextBoolean();
            hasValidUserID[i] = random.nextBoolean();

            boolean isEmpty = false;
            boolean hasNewLine = false;

            if (!hasValidName[i]) {
                isEmpty = random.nextBoolean();
                hasNewLine = !isEmpty;
            }

            String name = RandomStringBuilder.randomStringMaxLength(isEmpty ? 0 : random.nextInt(199) + 1, hasNewLine);
            String description = RandomStringBuilder.randomStringMaxLength(random.nextInt(199) + 1, random.nextBoolean());
            int projectID = hasValidProjectID[i] ? 1 : (random.nextBoolean() ? -random.nextInt(10) : random.nextInt(8) + 2);
            float budget = random.nextFloat() * 1000000000 - 50000000;
            Pair<Date> datePair = null;
            Date startDate = new Date();
            Date deadline = new Date();
            try {
                datePair = RandomDateBuilder.randomDatePair(hasValidDatePair[i]);
            } catch (Exception e) {

            }

            startDate = datePair.first;
            deadline = datePair.second;

            boolean done = random.nextBoolean();

            int userID = hasValidUserID[i] ? 1 : (random.nextBoolean() ? -random.nextInt(10) : random.nextInt(8) + 2);

            TaskModel t = new TaskModel(name, description, projectID, budget, startDate, deadline, done, userID);
            try {
                t.persistData();
            } catch (Exception e) {

            }

            // Checks database integrity based on whether it allows creation of projects based upon valid attributes
            // or disallows creation of projects based upon invalid attributes
            assertEquals("Name validity test", hasValidName[i], tasksOut[i] != null);
            assertEquals("Description validity test", hasValidDescription[i], tasksOut[i] != null);
            assertEquals("Budget validity test", hasValidBudget[i], tasksOut[i] != null);
            assertEquals("Manager validity test", hasValidUserID[i], tasksOut[i] != null);
            assertEquals("Chronology validity test", hasValidDatePair[i], tasksOut[i] != null);

            // Checks data integrity of output from stored values as compared to the input
            assertEquals("ID test", tasksIn[i].getTaskId(), tasksOut[i].getTaskId());
            assertEquals("Name test", tasksIn[i].getName(), tasksOut[i].getName());
            assertEquals("Description test", tasksIn[i].getDescription(), tasksOut[i].getDescription());
            assertEquals("Date start test", tasksIn[i].getStartDate(), tasksOut[i].getStartDate());
            assertEquals("Date end test", tasksIn[i].getDeadline(), tasksOut[i].getDeadline());
            assertEquals("Project ID test", tasksIn[i].getProjectId(), tasksOut[i].getProjectId());
            assertEquals("Budget test", tasksIn[i].getBudget(), tasksOut[i].getBudget(), 0.001);
            assertEquals("Manager ID test", tasksIn[i].getAssigneeUserId(), tasksOut[i].getAssigneeUserId());
            assertEquals("Done test", tasksIn[i].isDone(), tasksOut[i].isDone());
        }
    }
}
