package TestingTools;

import com.sepgroup.sep.controller.DialogCreator;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.utils.DateUtils;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by Demo on 5/29/2016.
 */

public class ProjectTest {

    static ProjectModel testProject;
    @Test
    public void projectGenerationTest() {
        int numOfTests = 10;
        int passCount = 0;
        ProjectModel[] projectsIn = new ProjectModel[numOfTests];
        ProjectModel[] projectsOut = new ProjectModel[numOfTests];

        System.out.println("\n\nTESTING PROJECT GENERATION\n");
        for (int i = 0; i < numOfTests; i++) {
            Pair<Date> dates = null;
            try {
                dates = RandomDateBuilder.randomDatePair(true);
            } catch (Exception e) {
                System.out.println("GENERAL ERROR ");
            }

            String name = "Chinchi";// RandomStringBuilder.randomString((int)(Math.random()*100+1));
            Date ds = dates.first;
            Date de = dates.second;

            double budget = Math.random() * 1000000;
            boolean done = Math.random() < 0.5;
            int id = i;//(int)(Math.random()*1000);

            String description = "Kind of chinchilla";//RandomStringBuilder.randomString((int)(Math.random()*1000+1));


            projectsIn[i] = new ProjectModel(name, ds, de, budget, done, id, description);
            try {
                projectsIn[i].persistData();
            } catch (DBException e) {
                System.out.println(e.getLocalizedMessage());
                break;
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
                projectsOut[i] = ProjectModel.getById(i);


            }catch(Exception e){

                System.out.println("COULD NOT PULL FROM DATABASE");
            }


            assertEquals("Name test", projectsIn[i].getName(),projectsOut[i].getName());
            assertEquals("Date start test", projectsIn[i].getStartDate(),projectsOut[i].getStartDate());
            assertEquals("Date end test", projectsIn[i].getDeadline(),projectsOut[i].getDeadline());
            assertEquals("Budget test", projectsIn[i].getBudget(),projectsOut[i].getBudget(), 0.00001);
            assertEquals("Done test", projectsIn[i].isDone(),projectsOut[i].isDone());
            assertEquals("ID test", projectsIn[i].getProjectId(),projectsOut[i].getProjectId());
            passCount++;
        }

        System.out.println("PROJECT GENERATION PASSED "+passCount+"/"+numOfTests+" TESTS");
    }

    @Test
    public void projectModificationest() {

    }
}