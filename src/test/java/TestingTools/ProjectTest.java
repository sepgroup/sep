package TestingTools;

import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.utils.DateUtils;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by HP on 5/29/2016.
 */

public class ProjectTest {

    static ProjectModel testProject;
    @Test
    public void projectGenerationTest() {
        int numOfTests = 1000;
        System.out.println("\n\nTESTING PROJECT GENERATION\n");
        for (int i = 0; i < numOfTests; i++) {
            Pair<Date>  dates = null;
            try {
                dates =RandomDateBuilder.randomDatePair(false);
            }
            catch(Exception e){
                System.out.println("GENERAL ERROR ");
            }

            String name = RandomStringBuilder.randomString((int)Math.random()*100+1);
            Date ds = dates.first;
            Date de = dates.second;

            double budget = Math.random()*1000000;
            boolean done = Math.random()<0.5;
            int id = (int)Math.random()*1000;

            String description = RandomStringBuilder.randomString((int)Math.random()*1000+1);


            testProject = new ProjectModel(name, ds, de, budget, done, id, description);
            System.out.println("TEST ITERATION : "+i );
            System.out.println("Name: "+name );
            System.out.println("Date start: "+dates.first);
            System.out.println("Date end: "+dates.second);
            System.out.println("Budget: "+budget);
            System.out.println("Done: "+done);
            System.out.println("ID: "+id);
            System.out.println("Description: "+description+"\n");






            assertEquals("Name test", name, testProject.getName());
            assertEquals("Date start test", dates.first, testProject.getStartDate());
            assertEquals("Date end test", dates.second, testProject.getDeadline());
            assertEquals("Budget test", budget, testProject.getBudget(), 0.00001);
            assertEquals("Done test", done, testProject.isDone());
            assertEquals("ID test", id, testProject.getProjectId());
            assertEquals("Description test", description, testProject.getProjectDescription());
        }
        System.out.println("PROJECT GENERATION PASSED "+numOfTests+" TESTS");
    }

    @Test
    public void projectModificationest() {

    }
}