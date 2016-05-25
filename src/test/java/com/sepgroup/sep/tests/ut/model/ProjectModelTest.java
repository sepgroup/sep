package com.sepgroup.sep.tests.ut.model;

import com.sepgroup.sep.project.ProjectModel;
import org.aeonbits.owner.ConfigFactory;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by jeremybrown on 2016-05-22.
 */
public class ProjectModelTest {

    @BeforeClass
    public static void setUp() throws Exception {
        ConfigFactory.setProperty("configPath", ProjectModelTest.class.getResource("/test-db.properties").getFile());
    }

    @After
    public void tearDownAfterMethod() throws Exception {

    }

    @Ignore
    @Test
    public void test() throws Exception {
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project 1");
        createdProject.persistData();
        int pId = createdProject.getProjectId();

        ProjectModel fetchedProject = ProjectModel.getById(pId);
        assertThat(fetchedProject, equalTo(createdProject));
    }

    @Test
    public void testGetAll() throws Exception {
        List<ProjectModel> projectList = ProjectModel.getAll();

        assertThat(projectList.size(), isA(Integer.class));
        assertThat(projectList.size(), not(0));
    }
}
