package TestingTools;

import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.*;

import java.util.Date;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Demo on 5/29/2016.
 */

public class ProjectTest {
    ProjectModel[] projectsIn;
    ProjectModel[] projectsOut;
    boolean[] hasValidDatePair;
    boolean[] hasValidName;
    boolean[] hasValidDescription;
    boolean[] hasValidBudget;
    boolean[] hasValidManager;

    static ProjectModel testProject;

    @Ignore
    @Test
    public void projectGenerationTest() {
        Random random = new Random();
        int numOfTests = 10;
        int passCount = 0;

        UserModel manager = null;
        try {
            manager = new UserModel("Coco", "Gonzalez", 100);
        } catch (InvalidInputException e) {
            System.err.println(e.getLocalizedMessage());
        }
        try {
            manager.persistData();
            System.out.println(UserModel.getById(manager.getUserId()));
        }
        catch(Exception e) {
            System.out.println(e.getLocalizedMessage());
            assert(false);
        }

        projectsIn = new ProjectModel[numOfTests];
        projectsOut = new ProjectModel[numOfTests];
        hasValidDatePair = new boolean[numOfTests];
        hasValidName = new boolean[numOfTests];
        hasValidDescription = new boolean[numOfTests];
        hasValidBudget = new boolean[numOfTests];
        hasValidManager = new boolean[numOfTests];

        System.out.println("\n\nTESTING PROJECT GENERATION\n");
        for (int i = 0; i < numOfTests; i++) {
            hasValidDatePair[i] = random.nextBoolean();
            hasValidName[i] = random.nextBoolean();
            hasValidDescription[i] = random.nextBoolean();
            hasValidBudget[i] = random.nextBoolean();

            Pair<Date> dates = null;
            try {
                dates = RandomDateBuilder.randomDatePair(hasValidDatePair[i]);
            } catch (Exception e) {
                System.out.println("GENERAL ERROR ");
            }

            String name = RandomStringBuilder.randomString(hasValidName[i]? random.nextInt(399) + 1 : 0);

            Date ds = dates.first;
            Date de = dates.second;

            double budget = hasValidBudget[i]? Math.random() * 10000000 : -Math.random() * 10000000;
            boolean done = random.nextBoolean();

            int managerID = random.nextInt(20) - 10;

            try {
                hasValidManager[i] = UserModel.getById(managerID) != null;
            }
            catch(Exception e) {
                hasValidManager[i] = false;
            }

            String description = RandomStringBuilder.randomString(hasValidDescription[i]? random.nextInt(999) + 1 : 0);


            try {
                projectsIn[i] = new ProjectModel(name, ds, de, budget, done, managerID, description);
                projectsIn[i].persistData();
            } catch (DBException e) {
                System.out.println(e.getLocalizedMessage());
            } catch (InvalidInputException e) {
                System.err.println(e.getLocalizedMessage());
            }
        }
        for (int i = 0; i < numOfTests; i++) {
            System.out.println("TEST ITERATION : " + i);
            System.out.println("Name: " + projectsIn[i].getName());
            System.out.println("Date start: " +  projectsIn[i].getStartDate());
            System.out.println("Date end: " +  projectsIn[i].getDeadline());
            System.out.println("Budget: " + projectsIn[i].getBudget());
            System.out.println("Done: " + projectsIn[i].isDone());
            System.out.println("ID: " + projectsIn[i].getProjectId());
            System.out.println("Description: " + projectsIn[i].getProjectDescription() + "\n");

            try{
                projectsOut[i] = ProjectModel.getById(i + 1);
            }catch(Exception e){
                System.out.println("COULD NOT PULL FROM DATABASE");
            }

            // Checks database integrity based on whether it allows creation of projects based upon valid attributes
            // or disallows creation of projects based upon invalid attributes
            assertEquals("Name validity test", hasValidName[i], projectsOut[i] != null);
            assertEquals("Description validity test", hasValidDescription[i], projectsOut[i] != null);
            assertEquals("Budget validity test", hasValidBudget[i], projectsOut[i] != null);
            assertEquals("Manager validity test", hasValidManager[i], projectsOut[i] != null);
            assertEquals("Chronology validity test", hasValidDatePair[i], projectsOut[i] != null);

            // Checks data integrity of output from stored values as compared to the input
            assertEquals("ID test", projectsIn[i].getProjectId(),projectsOut[i].getProjectId());
            assertEquals("Name test", projectsIn[i].getName(), projectsOut[i].getName());
            assertEquals("Description test", projectsIn[i].getProjectDescription(), projectsOut[i].getProjectDescription());
            assertEquals("Date start test", projectsIn[i].getStartDate(),projectsOut[i].getStartDate());
            assertEquals("Date end test", projectsIn[i].getDeadline(),projectsOut[i].getDeadline());
            assertEquals("Budget test", projectsIn[i].getBudget(),projectsOut[i].getBudget(), 0.001);
            assertEquals("Manager ID test", projectsIn[i].getManagerUserId(), projectsOut[i].getManagerUserId());
            assertEquals("Done test", projectsIn[i].isDone(),projectsOut[i].isDone());
            passCount++;
        }

        System.out.println("PROJECT GENERATION PASSED "+passCount+"/"+numOfTests+" TESTS");
    }

    @Test
    public void projectUpdate() {

    }

    @Test
    public void projectDeletion() {

    }
}