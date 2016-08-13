package com.sepgroup.sep.tests;

import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.UserModel;

import java.util.Date;

/**
 * Created by jeremybrown on 2016-08-13.
 */
public class TestCommons {

    private static Date createdProjectStartDate = new Date(System.currentTimeMillis() - 5 * 9999*9999);
    private static Date createdProjectDeadline = new Date(System.currentTimeMillis() + 5 * 9999*9999);

    public static ProjectModel createMainProject() throws Exception {
        ProjectModel createdProject = new ProjectModel();
        createdProject.setName("Project 1");
        createdProject.setBudget(100000);
        createdProject.setStartDate(createdProjectStartDate);
        createdProject.setDeadline(createdProjectDeadline);
        createdProject.persistData();

        return createdProject;
    }

    public static UserModel createMainUser() throws Exception {
        UserModel createdUser = new UserModel("FIRST", "LAST", 22.00);
        createdUser.persistData();

        return createdUser;
    }
}
