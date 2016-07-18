package TestingTools;

import com.sepgroup.sep.tests.ut.db.*;
import com.sepgroup.sep.model.*;

import java.util.Date;
import java.util.Random;
import java.util.ArrayList;

import org.aeonbits.owner.ConfigFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Wired on 2016-06-05.
 *  Integration test to ensure correct task generation with randomized data
 *  TODO fix this
 */
public class RandomizedTaskTests {
    TaskModel[] tasksIn, tasksOut;
    boolean[] hasValidName, hasValidDescription, hasValidBudget, hasValidProjectID, hasValidDatePair, hasValidUserID, isValid;

    @Ignore
    @Test
    public void test() {
        populateDB(true);
        updateTest();
        deletionTest();
    }

    public void populateDB(boolean checkCreation) {
        Random random = new Random();
        int numOfTests = 100;
        tasksIn = new TaskModel[numOfTests];
        tasksOut = new TaskModel[numOfTests];
        isValid = new boolean[numOfTests];
        hasValidName = new boolean[numOfTests];
        hasValidDescription = new boolean[numOfTests];
        hasValidBudget = new boolean[numOfTests];
        hasValidProjectID = new boolean[numOfTests];
        hasValidDatePair = new boolean[numOfTests];
        hasValidUserID = new boolean[numOfTests];

        UserModel user = null;
        try{
            user = new UserModel("Coco", "Gonzalez", 20.04);
            user.persistData();
        }
        catch(Exception e) {
            assert(false);
        }

        ProjectModel project = null;
        try{
            project = new ProjectModel("Chinchilla Project", new Date(118, 2, 19), new Date(118, 2, 27), 17496.45, false, user, "Chinchilla stuff");
            project.persistData();
        }
        catch(Exception e) {
            assert(false);
        }

        for(int i = 0; i < numOfTests; i++) {
            hasValidName[i] = random.nextBoolean();
            hasValidDescription[i] = true;
            hasValidProjectID[i] = random.nextBoolean();
            hasValidBudget[i] = random.nextBoolean();
            hasValidDatePair[i] = random.nextBoolean();
            hasValidUserID[i] = random.nextBoolean();

            isValid[i] = hasValidName[i] && hasValidDescription[i] && hasValidProjectID[i] && hasValidBudget[i] && hasValidDatePair[i] && hasValidUserID[i];

            try {
                boolean isEmpty = false;
                boolean hasNewLine = false;

                if (!hasValidName[i]) {
                    isEmpty = random.nextBoolean();
                    hasNewLine = !isEmpty;
                }

                String name = RandomStringBuilder.randomStringMaxLength(isEmpty ? 0 : random.nextInt(199) + 1, hasNewLine);
                String description = RandomStringBuilder.randomStringMaxLength(random.nextInt(199) + 1, random.nextBoolean());
                int projectID = hasValidProjectID[i] ? 1 : (random.nextBoolean() ? -random.nextInt(10) : random.nextInt(8) + 2);
                int userID = hasValidUserID[i] ? 1 : (random.nextBoolean() ? -random.nextInt(10) : random.nextInt(8) + 2);
                float budget = (hasValidBudget[i]? random.nextFloat() * 1000000000 + 0.01f : - random.nextFloat() * 1000000000);

                Pair<Date> datePair = RandomDateBuilder.randomDatePair(hasValidDatePair[i]);
                Date startDate = datePair.first;
                Date deadline = datePair.second;

                boolean done = random.nextBoolean();

                tasksIn[i] = new TaskModel(name, description, projectID, budget, startDate, deadline, done, null);
                tasksIn[i].setAssignee(userID);
                tasksIn[i].persistData();
                tasksOut[i] = TaskModel.getById(tasksIn[i].getTaskId());
            }
            catch(Exception e)
            {

            }

            if(checkCreation)
                assertionCriteria(i);
        }
    }

    public void assertionCriteria(int i){
        String name, description, projectID, userID, budget, startDate, deadline;

        if(tasksOut[i] == null)
        {
            name = "";
            description ="";
            projectID = "";
            userID = "";
            budget = "";
            startDate = "";
            deadline = "";
        }
        else
        {
            name = tasksOut[i].getName();
            description = tasksOut[i].getDescription();
            projectID = Integer.toString(tasksOut[i].getProjectId());
            userID = Integer.toString(tasksOut[i].getAssignee().getUserId());
            budget = Float.toString((float)tasksOut[i].getBudget());
            startDate = tasksOut[i].getStartDate().toString();
            deadline = tasksOut[i].getDeadline().toString();
        }

        // Checks database integrity based on whether it allows creation of projects based upon valid attributes
        // or disallows creation of projects based upon invalid attributes
        assertEquals("Name validity test: "  + tasksIn[i].getName() + " " + name, hasValidName[i], tasksOut[i] != null);
        assertEquals("Description validity test: "  + tasksIn[i].getDescription() + " " + description, hasValidDescription[i], tasksOut[i] != null);
        assertEquals("Project validity test: "  + tasksIn[i].getProjectId() + " " + projectID, hasValidProjectID[i], tasksOut[i] != null);
        assertEquals("Budget validity test: " + tasksIn[i].getBudget() + " " + budget, hasValidBudget[i], tasksOut[i] != null);
        assertEquals("Assignee validity test: " + tasksIn[i].getAssignee().getUserId() + " " + userID, hasValidUserID[i], tasksOut[i] != null);
        assertEquals("Chronology validity test: " + startDate + " " + deadline, hasValidDatePair[i], tasksOut[i] != null);

        // Checks data integrity of output from stored values as compared to the input
        assertEquals("ID test", tasksIn[i].getTaskId(), tasksOut[i].getTaskId());
        assertEquals("Name test", tasksIn[i].getName(), tasksOut[i].getName());
        assertEquals("Description test", tasksIn[i].getDescription(), tasksOut[i].getDescription());
        assertEquals("Date start test", tasksIn[i].getStartDate(), tasksOut[i].getStartDate());
        assertEquals("Date end test", tasksIn[i].getDeadline(), tasksOut[i].getDeadline());
        assertEquals("Project ID test", tasksIn[i].getProjectId(), tasksOut[i].getProjectId());
        assertEquals("Budget test", tasksIn[i].getBudget(), tasksOut[i].getBudget(), 0.005);
        assertEquals("Manager ID test", tasksIn[i].getAssignee().getUserId(), tasksOut[i].getAssignee().getUserId());
        assertEquals("Done test", tasksIn[i].isDone(), tasksOut[i].isDone());
    }

    public void updateTest(){
        for(int i = 0; i < tasksIn.length; i++)
        {

        }
    }

    public void deletionTest() {
        Random random = new Random();
        ArrayList<TaskModel> tempModels = new ArrayList<TaskModel>();

        for(int i = 0; i < tasksOut.length; i++)
            if(isValid[i])
                tempModels.add(tasksOut[i]);

        while(tempModels.size() > 0)
        {
            int i = random.nextInt(tempModels.size());
            try{
                TaskModel.getById(tempModels.get(i).getTaskId()).deleteData();
                tempModels.remove(i);
            }
            catch(Exception e) {
                assert(false);
            }
        }

        for(int i = 0; i < tasksOut.length; i++)
        {
            if(tasksOut[i] != null)
            {
                try {
                    TaskModel.getById(tasksOut[i].getTaskId());
                    fail();
                }
                catch(Exception e)
                {
                    // pass test
                }
            }
        }
    }
}
