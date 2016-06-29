package com.sepgroup.sep.tests.ut.model;

import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import org.aeonbits.owner.ConfigFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by jeremybrown on 2016-05-22.
 */
public class ProjectModelTest {

    private static Date defaultStartDate = new Date();
    private static Date defaultDeadline = new Date();


    @BeforeClass
    public static void setUp() throws Exception {
        ConfigFactory.setProperty("configPath", ProjectModelTest.class.getResource("/test-db.properties").getFile());
    }

    @Test
    public void testPersistData() throws Exception {
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project X");
        createdProject.setBudget(1000.00);
        createdProject.setStartDate(new Date());
        createdProject.setDeadline(new Date());
        createdProject.setDone(true);
        createdProject.setProjectDescription("Project Description. \\nExists on multiple lines.");
        createdProject.getProjectId();
        createdProject.persistData();
        int pId = createdProject.getProjectId();

        ProjectModel fetchedProject = null;
        try {
            fetchedProject = ProjectModel.getById(pId);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }
        assertThat(fetchedProject, equalTo(createdProject));
    }

    @Test
    public void testRefreshData() throws Exception {
        // Create project
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Y");
        createdProject.persistData();
        int pId = createdProject.getProjectId();

        // Fetch project
        ProjectModel fetchedProject = null;
        try {
            fetchedProject = ProjectModel.getById(pId);
        } catch (ModelNotFoundException e) {
            fail(e.getMessage());
        }

        // Modify & persist project
        fetchedProject.setDone(true);
        fetchedProject.setBudget(1000000);
        fetchedProject.persistData();

        // Refresh first project instance
        createdProject.refreshData();

        assertThat(createdProject, equalTo(fetchedProject));

    }

    @Test(expected = ModelNotFoundException.class)
    public void testDeleteData() throws Exception {
        // Create project
        ProjectModel p = new ProjectModel();
        p.setName("Project Y");
        p.persistData();
        int pId = p.getProjectId();

        // Delete project
        p.deleteData();

        // Attempt to fetch project
        ProjectModel.getById(pId);
    }

    @Test
    public void testGetAll() throws Exception {
        List<ProjectModel> projectList = ProjectModel.getAll();

        assertThat(projectList.size(), isA(Integer.class));
        assertThat(projectList.size(), not(0));
    }

    @Test
    public void testGetById() throws Exception {
        // Create project
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project Z");
        createdProject.persistData();
        int pId = createdProject.getProjectId();

        ProjectModel fetchedProject = ProjectModel.getById(pId);

        assertThat(fetchedProject, equalTo(createdProject));
    }

    @Test
    public void testGetAllByManager() throws Exception {
        // TODO
    }

    @Test
    public void testEquals() throws Exception {
        // Create two projects with same data
        ProjectModel p1 = new ProjectModel("Proj", defaultStartDate, defaultDeadline, 1000, false, 0, "P Desc.");
        ProjectModel p2 = new ProjectModel("Proj", defaultStartDate, defaultDeadline, 1000, false, 0, "P Desc.");

        assertTrue(p1.equals(p2));

    }
}
