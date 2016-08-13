package com.sepgroup.sep.tests.system;

import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.model.*;

import java.util.Date;
import java.util.Random;
import java.util.List;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.*;


import static org.junit.Assert.*;

/**
 * Edited by Justin on 07/10/2016.
 * Created by Demo on 5/29/2016.
 * Integration test to ensure correct project generation with randomized data
 * TODO fix this
 */
@Ignore
@RunWith(Parameterized.class)
public class RandomizedProjectTests {
    private static ProjectModel[] projectsIn;
    private static ProjectModel[] projectsOut;
    private static boolean[] hasValidDatePair;
    private static boolean[] hasValidName;
    private static boolean[] hasValidDescription;
    private static boolean[] hasValidBudget;
    private static boolean[] hasValidManager;

    static ProjectModel testProject;

    private static final int NUMBER_OF_TESTS = 10;
    private static int testNumber = 0;

    private static UserModel createdManager;
    private static UserModel createdUser;

    @Parameterized.Parameters
    public static List<Object[]> data() throws Exception {
        /**
         * Runs the test the number of times preset as a constant
         */

        //Initializes variables relevant for project test
        try {
            instantiateProject();
        }
        catch (InvalidInputException iie)
        {
            System.out.println("Incorrect values input for test instantiation ");
            throw iie;
        }
        catch (DBException dbe)
        {
            System.out.println("Exception when initializing DB ");
            throw dbe;
        }
        catch (Exception e) {
            System.out.println("Failure instantiating project variables, ");
            throw e;
        }

        return Arrays.asList(new Object[NUMBER_OF_TESTS][0]);
    }

    private static void instantiateProject() throws InvalidInputException, DBException{
        /**
         * Initializes variables relevant for project test. These are all initialized at the start of
         * RandomizedProjectTests
         */

        DBManager.createDBTablesIfNotExisting();

        createdManager = new UserModel("FIRST", "MANAGER", 100.00);
        createdManager.persistData();

        createdUser = new UserModel("FIRST", "USER", 50.00);
        createdUser.persistData();

        projectsIn = new ProjectModel[NUMBER_OF_TESTS];
        projectsOut = new ProjectModel[NUMBER_OF_TESTS];
        hasValidDatePair = new boolean[NUMBER_OF_TESTS];
        hasValidName = new boolean[NUMBER_OF_TESTS];
        hasValidDescription = new boolean[NUMBER_OF_TESTS];
        hasValidBudget = new boolean[NUMBER_OF_TESTS];
        hasValidManager = new boolean[NUMBER_OF_TESTS];
    }

    @Before
    public void setUp() throws Exception {
        /**
         * Generates a new UserModel objects
         */
        System.out.println("Randomized project integration test " + testNumber);


        //Assigns randomly generated values to the project

    }

    @After
    public void tearDown() throws Exception {
        /**
         * Generates a new UserModel objects
         */
        System.out.println("Ending test " + testNumber);
        testNumber ++;

    }


    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        DBManager.dropAllDBTables();
    }


    private Pair<Date> generateDatePair(boolean hasValidDatePair[])
    {
        Pair<Date> dates = null;
        try {
            dates = RandomDateBuilder.randomDatePair(hasValidDatePair[testNumber]);
        } catch (Exception e) {
            System.out.println("Error with test framework while trying to generate random date pair");
//            throws e;
        }
        return dates;
    }

    private void printAssignedValues(String name, Date sd, Date dl, double budget, boolean done, UserModel managerUserId,
                                     String projectDescription)
    {

    }

    private void printStoredValues()
    {
        System.out.println("Current test number is: " + testNumber);

        System.out.println("Name: " + projectsIn[testNumber].getName());
        System.out.println("Date start: " +  projectsIn[testNumber].getStartDate());
        System.out.println("Date end: " +  projectsIn[testNumber].getDeadline());
        System.out.println("Budget: " + projectsIn[testNumber].getBudget());
        System.out.println("Done: " + projectsIn[testNumber].isDone());
        System.out.println("ID: " + projectsIn[testNumber].getProjectId());
        System.out.println("Description: " + projectsIn[testNumber].getProjectDescription() + "\n");
    }

    @Ignore
    @Test
    public void projectGenerationTest() throws Exception {
        /**
         * Assigns randomly generated values to the project
         */

        Random random = new Random();
        int passCount = 0;

        hasValidDatePair[testNumber] = random.nextBoolean();
        hasValidName[testNumber] = random.nextBoolean();
        hasValidDescription[testNumber] = random.nextBoolean();
        hasValidBudget[testNumber] = random.nextBoolean();

        String name = null;
        Date ds = null;
        Date de = null;
        double budget = 0.0;
        boolean done = random.nextBoolean();
        try {
            //Generates a random date pair based on the randomized boolean value
            Pair<Date> dates = generateDatePair(hasValidDatePair);
            ds = dates.first;
            de = dates.second;

            //Generates random string for the description. Assumes the name should be no more than 50 characters
            name = RandomStringBuilder.randomString(hasValidName[testNumber]? random.nextInt(50) + 1 : 0);

            //Generate random budget based on randomized boolean value
            budget = hasValidBudget[testNumber]? Math.random() * 10000000 : -Math.random() * 10000000;

            //TODO check reason for this
            //Generates random managerID
            int managerID = random.nextInt(20) - 10;
            hasValidManager[testNumber] = true;
            //printAssignedValues(name, ds, de, budget, done, createdManager, description);

        }
        catch (Exception e) {
            System.out.println("Failure instantiating project variables, ");
            throw e;
        }


        //Randomizes the description of the project
        String description = RandomStringBuilder.randomString(hasValidDescription[testNumber]? random.nextInt(999) + 1 : 0);

        //Create a new project with randomized data
        try {
            projectsIn[testNumber] = new ProjectModel(name, ds, de, budget, done, createdManager, description);
            projectsIn[testNumber].persistData();
        } catch (DBException e) {
            System.out.println(e.getLocalizedMessage());
        } catch (InvalidInputException e) {
            System.err.println(e.getLocalizedMessage());
        }



        /**
         * FIX AFTER HERE
         */




//            try {
//                hasValidManager[testNumber] = UserModel.getById(managerID) != null;
//            }
//            catch(Exception e) {
//                hasValidManager[testNumber] = false;
//            }


        System.out.println("Current test number is: " + testNumber);

        System.out.println("Name: " + projectsIn[testNumber].getName());
        System.out.println("Date start: " +  projectsIn[testNumber].getStartDate());
        System.out.println("Date end: " +  projectsIn[testNumber].getDeadline());
        System.out.println("Budget: " + projectsIn[testNumber].getBudget());
        System.out.println("Done: " + projectsIn[testNumber].isDone());
        System.out.println("ID: " + projectsIn[testNumber].getProjectId());
        System.out.println("Description: " + projectsIn[testNumber].getProjectDescription() + "\n");

        try{
            projectsOut[testNumber] = ProjectModel.getById(testNumber + 1);
        }catch(Exception e){
            System.out.println("COULD NOT PULL FROM DATABASE");
        }

        // Checks database integrity based on whether it allows creation of projects based upon valid attributes
        // or disallows creation of projects based upon invalid attributes
        assertEquals("Name validity test", hasValidName[testNumber], projectsOut[testNumber] != null);
        assertEquals("Description validity test", hasValidDescription[testNumber], projectsOut[testNumber] != null);
        assertEquals("Budget validity test", hasValidBudget[testNumber], projectsOut[testNumber] != null);
        assertEquals("Manager validity test", hasValidManager[testNumber], projectsOut[testNumber] != null);
        assertEquals("Chronology validity test", hasValidDatePair[testNumber], projectsOut[testNumber] != null);

        // Checks data integrity of output from stored values as compared to the input
        assertEquals("ID test", projectsIn[testNumber].getProjectId(),projectsOut[testNumber].getProjectId());
        assertEquals("Name test", projectsIn[testNumber].getName(), projectsOut[testNumber].getName());
        assertEquals("Description test", projectsIn[testNumber].getProjectDescription(), projectsOut[testNumber].getProjectDescription());
        assertEquals("Date start test", projectsIn[testNumber].getStartDate(),projectsOut[testNumber].getStartDate());
        assertEquals("Date end test", projectsIn[testNumber].getDeadline(),projectsOut[testNumber].getDeadline());
        assertEquals("Budget test", projectsIn[testNumber].getBudget(),projectsOut[testNumber].getBudget(), 0.001);
        assertEquals("Manager ID test", projectsIn[testNumber].getManager().getUserId(), projectsOut[testNumber].getManager().getUserId());
        assertEquals("Done test", projectsIn[testNumber].isDone(),projectsOut[testNumber].isDone());
        passCount++;

        System.out.println("PROJECT GENERATION PASSED "+passCount+"/"+ NUMBER_OF_TESTS+" TESTS");

    }








//
//
//
//    @Ignore
//    @Test
//    public void projectGenerationTest() {
//        Random random = new Random();
//        //number of randomized tests to generate
//        int passCount = 0;
//
//
//        for (int testNumber = 0; testNumber < NUMBER_OF_TESTS; testNumber++) {
//            hasValidDatePair[testNumber] = random.nextBoolean();
//            hasValidName[testNumber] = random.nextBoolean();
//            hasValidDescription[testNumber] = random.nextBoolean();
//            hasValidBudget[testNumber] = random.nextBoolean();
//
//            Pair<Date> dates = null;
//            try {
//                dates = RandomDateBuilder.randomDatePair(hasValidDatePair[testNumber]);
//            } catch (Exception e) {
//                System.out.println("GENERAL ERROR ");
//            }
//
//            String name = RandomStringBuilder.randomString(hasValidName[testNumber]? random.nextInt(399) + 1 : 0);
//
//            Date ds = dates.first;
//            Date de = dates.second;
//
//            double budget = hasValidBudget[testNumber]? Math.random() * 10000000 : -Math.random() * 10000000;
//            boolean done = random.nextBoolean();
//
//            int managerID = random.nextInt(20) - 10;
//
//
//            hasValidManager[testNumber] = true;
//
////            try {
////                hasValidManager[testNumber] = UserModel.getById(managerID) != null;
////            }
////            catch(Exception e) {
////                hasValidManager[testNumber] = false;
////            }
//
//            String description = RandomStringBuilder.randomString(hasValidDescription[testNumber]? random.nextInt(999) + 1 : 0);
//
//
//            try {
//                projectsIn[testNumber] = new ProjectModel(name, ds, de, budget, done, createdUser, description);
//                projectsIn[testNumber].persistData();
//            } catch (DBException e) {
//                System.out.println(e.getLocalizedMessage());
//            } catch (InvalidInputException e) {
//                System.err.println(e.getLocalizedMessage());
//            }
//        }
////        numOfTests = 1;
//        for (int testNumber = 0; testNumber < NUMBER_OF_TESTS; testNumber++) {
//            System.out.println("TEST ITERATION : " + testNumber);
//            System.out.println("Name: " + projectsIn[testNumber].getName());
//            System.out.println("Date start: " +  projectsIn[testNumber].getStartDate());
//            System.out.println("Date end: " +  projectsIn[testNumber].getDeadline());
//            System.out.println("Budget: " + projectsIn[testNumber].getBudget());
//            System.out.println("Done: " + projectsIn[testNumber].isDone());
//            System.out.println("ID: " + projectsIn[testNumber].getProjectId());
//            System.out.println("Description: " + projectsIn[testNumber].getProjectDescription() + "\n");
//
//            try{
//                projectsOut[testNumber] = ProjectModel.getById(testNumber + 1);
//            }catch(Exception e){
//                System.out.println("COULD NOT PULL FROM DATABASE");
//            }
//
//            // Checks database integrity based on whether it allows creation of projects based upon valid attributes
//            // or disallows creation of projects based upon invalid attributes
//            assertEquals("Name validity test", hasValidName[testNumber], projectsOut[testNumber] != null);
//            assertEquals("Description validity test", hasValidDescription[testNumber], projectsOut[testNumber] != null);
//            assertEquals("Budget validity test", hasValidBudget[testNumber], projectsOut[testNumber] != null);
//            assertEquals("Manager validity test", hasValidManager[testNumber], projectsOut[testNumber] != null);
//            assertEquals("Chronology validity test", hasValidDatePair[testNumber], projectsOut[testNumber] != null);
//
//            // Checks data integrity of output from stored values as compared to the input
//            assertEquals("ID test", projectsIn[testNumber].getProjectId(),projectsOut[testNumber].getProjectId());
//            assertEquals("Name test", projectsIn[testNumber].getName(), projectsOut[testNumber].getName());
//            assertEquals("Description test", projectsIn[testNumber].getProjectDescription(), projectsOut[testNumber].getProjectDescription());
//            assertEquals("Date start test", projectsIn[testNumber].getStartDate(),projectsOut[testNumber].getStartDate());
//            assertEquals("Date end test", projectsIn[testNumber].getDeadline(),projectsOut[testNumber].getDeadline());
//            assertEquals("Budget test", projectsIn[testNumber].getBudget(),projectsOut[testNumber].getBudget(), 0.001);
//            assertEquals("Manager ID test", projectsIn[testNumber].getManager().getUserId(), projectsOut[testNumber].getManager().getUserId());
//            assertEquals("Done test", projectsIn[testNumber].isDone(),projectsOut[testNumber].isDone());
//            passCount++;
//        }
//
//        System.out.println("PROJECT GENERATION PASSED "+passCount+"/"+NUMBER_OF_TESTS+" TESTS");
//    }
//
//    @Test
//    public void projectUpdate() {
//
//    }
//
//    @Test
//    public void projectDeletion() {


}
