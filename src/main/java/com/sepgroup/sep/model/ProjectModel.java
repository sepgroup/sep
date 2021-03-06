package com.sepgroup.sep.model;

import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.db.DatabaseFactory;
import com.sepgroup.sep.utils.CurrencyUtils;
import com.sepgroup.sep.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ProjectModel extends AbstractModel {

    private static Logger logger = LoggerFactory.getLogger(ProjectModel.class);
    public static List<TaskModel> tempTasks;

    private ProjectModelDBObject dbo;

    /**
     * Before a project is saved to the database, this should always be zero.
     * Once the project's data is persisted, it will be automatically set to the generated DB key.
     */
    private int projectId;

    private String name;
    private double budget;
    private Date startDate;
    private Date deadline;
    private boolean done;
    private UserModel manager;
    private String projectDescription;
    public static double MaxBudgetProject = 10000000;

	/**
	 * Default constructor
	 */
	public ProjectModel() {
        dbo = new ProjectModelDBObject();
	}

	/**
	 * Constructor for project class
	 * This constructor is used to create objects to insert into the database
	 * id is auto increment and it generates from database so we do not have id in this constructor
	 * @param name Name of project
	 * @param sd Start date of project
	 * @param dl Deadline of project
	 * @param budget Dedicated Budget to the project
	 */
	public ProjectModel(String name, Date sd, Date dl, double budget, boolean done, UserModel managerUserId,
                        String projectDescription) throws InvalidInputException {
        this();
        setName(name);
        setBudget(budget);
        setStartDate(sd);
        setDeadline(dl);
        setDone(done);
        setManager(manager);
        setProjectDescription(projectDescription);
	}
	/**
	 * This constructor is for package use to fetch data from database
	 * this is private because we do not want the user assigned id to the project
	 * @param id the unique value for project
	 * @param name Name of project
	 * @param sd Start date of project
	 * @param dl Deadline of project
	 * @param budget Dedicated Budget to the project
	 */
	protected ProjectModel(int id, String name, Date sd, Date dl, double budget, boolean done, UserModel manager,
                         String projectDescription) {
        this();
        this.name = name;
        this.budget = budget;
        this.startDate = sd;
        this.deadline = dl;
        this.done = done;
        this.projectDescription = projectDescription;
        this.manager = manager;
        this.projectId = id;
	}

    @Override
    public void refreshData() throws ModelNotFoundException, InvalidInputException {
        ProjectModel refreshed = getById(getProjectId());

        setName(refreshed.getName());
        setProjectId(refreshed.getProjectId());
        setBudget(refreshed.getBudget());
        setStartDate(refreshed.getStartDate());
        setDeadline(refreshed.getDeadline());
        setDone(refreshed.isDone());
        setProjectDescription(refreshed.getProjectDescription());
        setManager(refreshed.getManager());

        updateObservers();
    }

    @Override
    public void persistData() throws DBException {
        super.persistData();
        if (this.name == null || this.name.replaceAll(" ", "").equals("")) {
            logger.error("Project name must be set to persist model to DB");
            throw new DBException("Project name must be set to persist model to DB");
        }
        if (this.projectId == 0) {
            // Project is new, not already in DB
            int projectId = this.dbo.create();
            this.projectId = projectId;
        }
        else {
            // Project is already in DB
            this.dbo.update();
        }
        updateObservers();
    }

    @Override
    public void deleteData() throws DBException {
        this.dbo.delete();
    }

    /**
     * It fetches the data from database which has the given ID
     * and create one object of project and returns it
     * @param projectId is the ID that we search for that
     * @return the data inside the row of selected table
     */
    public static ProjectModel getById(int projectId) throws ModelNotFoundException {
        return new ProjectModel().dbo.findById(projectId);
    }

    /**
     * It fetches all data from database and make a LinkedList
     * of project objects and return it
     * @return LinkedList of Project Objects
     */
    public static List<ProjectModel> getAll() throws ModelNotFoundException {
        return new ProjectModel().dbo.findAll();
    }

    public static List<ProjectModel> getAllByManager(UserModel userModel) throws ModelNotFoundException {
        return getAllByManager(userModel.getUserId());
    }

    public static List<ProjectModel> getAllByManager(int managerUserId) throws ModelNotFoundException {
        return new ProjectModel().dbo.findAllByManager(managerUserId);
    }

    public static void cleanData()throws DBException{
        new ProjectModel().dbo.clean();
    }

    public static void createTable() throws DBException{
        new ProjectModel().dbo.createTable();
    }

    /**
     *
     * @param projectId
     * @throws InvalidInputException if project ID is not a positive integer
     */
    private void setProjectId(int projectId) throws InvalidInputException {
        if (projectId < 0) {
            throw new InvalidInputException("Project ID must be a positive integer.");
        }
        this.projectId = projectId;
    }

    public int getProjectId() {
        return this.projectId;
    }

    /**
	 * Setter for name of project which is used in update method
	 * @param name updated name of project
     * @throws InvalidInputException if name length is greater than 50 characters
	 */
	public void setName(String name) throws InvalidInputException {
        if (name.length() > 50) {
            throw new InvalidInputException("Name must not be longer than 50 characters.");
        }

		this.name = name.replaceAll("(\\r|\\n|\\t)", " "); // Replace newlines with spaces
	}

    /**
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

	/**
	 * Setter for budget of project which is used in update method
	 * @param budget updated budget of project.
     * @throws InvalidInputException if budget is not a positive number
	 */
	public void setBudget(double budget) throws InvalidInputException {
        if (budget < 0) {
            throw new InvalidInputException("Budget must be a positive number.");
        }
        if(budget > MaxBudgetProject){
            throw new InvalidInputException("Budget must be a less than"+  MaxBudgetProject+ "$.");
        }
		this.budget = CurrencyUtils.roundToTwoDecimals(budget);
	}

    /**
     *
     * @return
     */
    public double getBudget() {
        return this.budget;
    }

	/**
	 * Setter for Start date of project which is used in update method
	 * @param startDate updated date of project
	 */
	public void setStartDate(String startDate) throws InvalidInputException {
        try {
            setStartDate(DateUtils.castStringToDate(startDate));
        } catch (ParseException e) {
            throw new InvalidInputException("Invalid start date string " + startDate);
        }
	}

    /**
     * Setter for Start date of project which is used in update method
     * @param startDate updated date of project
     * @throws InvalidInputException if the start date is after the deadline
     */
    public void setStartDate(Date startDate) throws InvalidInputException{
        if (deadline != null && startDate != null && startDate.after(deadline)) {
            throw new InvalidInputException("Start date must be before deadline.");
        }
        this.startDate = DateUtils.filterDateToMidnight(startDate);
    }

    /**
     *
     * @return
     */
    public Date getStartDate() {
        return this.startDate;
    }

    public String getStartDateString() {
        if (this.startDate != null) {
            return DateUtils.castDateToString(this.startDate);
        } else {
            return null;
        }
    }

    /**
	 * Setter for Dead Line of project which is used in update method
	 * @param deadline updated date of deadline
     * @throws InvalidInputException if the deadline is before the start date
	 */
	public void setDeadline(Date deadline) throws InvalidInputException {
        if (startDate != null && deadline != null && deadline.before(startDate)) {
            throw new InvalidInputException("Deadline must be after start date.");
        }
        this.deadline = DateUtils.filterDateToMidnight(deadline);
	}

    /**
     * Setter for Dead Line of project which is used in update method
     * @param deadline updated date of deadline
     * @throws InvalidInputException if the
     */
    public void setDeadline(String deadline) throws InvalidInputException {
        try {
            setDeadline(DateUtils.castStringToDate(deadline));
        } catch (ParseException e) {
            throw new InvalidInputException("Invalid deadline string.");
        }
    }

    /**
     *
     * @return
     */
    public Date getDeadline() {
        return this.deadline;
    }

    public String getDeadlineString() {
        if (this.deadline != null) {
            return DateUtils.castDateToString(this.deadline);
        } else {
            return null;
        }
    }

	/**
	 * Setter for done attribute of project which is used in update method
	 * @param done updated status of project
	 */
	public void setDone(boolean done){
		this.done = done;
	}

    /**
     *
     * @return
     */
    public boolean isDone() {
        return this.done;
    }

    /**
     * Determines if a project has a unique root task.
     * @return True if and only if there is a single task in the project with no dependencies.
     */
    public boolean hasRoot()
    {
        List<TaskModel> tasks = null;

        try
        {
            tasks = getTasks();
        }
        catch (ModelNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (InvalidInputException e)
        {
            e.printStackTrace();
        }

        if (tasks.size() == 0)
            return false;

        int numRoots = 0;

        for (final TaskModel task : tasks)
            if (task.getDependencies().size() == 0)
                numRoots++;

        return numRoots == 1;
    }

    /**
     * Retrieves the root task of the project, if there is one.
     * @return The root task if it exists, null otherwise.
     */
    public TaskModel root() {
        List<TaskModel> tasks = null;

        try {
            tasks = getTasks();
        } catch (ModelNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidInputException e) {
            e.printStackTrace();
        }

        if (tasks.size() == 0)
            return null;

        TaskModel root = null;

        for (final TaskModel task : tasks) {
            if (task.getDependencies().size() == 0) {
                if (root == null) {
                    root = task;
                } else {
                    root = null;
                    break;
                }
            }
        }

        return root;
    }

    /**
     *
     * @return
     */
    public String getProjectDescription() {
        return projectDescription;
    }

    /**
     *
     * @param projectDescription
     */
    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    /**
     * Get the project manager
     * @return the UserModel of the project manager
     */
    public UserModel getManager() {
        return manager;
    }

    /**
     * Set the project manager
     * @param manager UserModel of the project manager
     */
    public void setManager(UserModel manager) throws InvalidInputException {
        if (manager == null) {
            logger.debug("Set assignee given a null value, removing assignee.");
        }
        else if (manager.getUserId() == 0) {
            throw new InvalidInputException("Trying to set assignee as user " +
                    " ID of 0. User must be saved to database before managing a project.");
        }
        else if (manager.getUserId() < 0) {
            throw new InvalidInputException("User ID must be a positive integer");
        }

        this.manager = manager;
    }

    public void removeManager() {
        this.manager = null;
    }

    /**
     * Get a list of tasks associated to this project
     * @return a list of tasks associated to this project
     */
    public List<TaskModel> getTasks() throws ModelNotFoundException, InvalidInputException {
        if(tempTasks == null)
            tempTasks = TaskModel.getAllByProject(getProjectId());

        return tempTasks;
    }

    public List<TaskModel> getTasksExceptionSafe() {
        List<TaskModel> tasks = null;
        try {
            tasks = getTasks();
        } catch (ModelNotFoundException e) {
            tasks = new ArrayList<>();
        } catch (InvalidInputException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    /**
     * Total budget of all tasks in the project.
     * @return
     */
    public double getBudgetAtCompletion() throws Exception{
        if(getTasksExceptionSafe().stream().mapToDouble(TaskModel::getBudget).sum()<=MaxBudgetProject){
            return getTasksExceptionSafe().stream().mapToDouble(TaskModel::getBudget).sum();
        }else{
            logger.debug("sum of tasks budgets passed upper boundary of project budget");
            throw new Exception("sum of tasks passed upper boundary of project budget");
        }
    }

    /**
     * Total budget of all completed tasks in the project.
     * @return
     */
    public double getEarnedValue() {
        final List<TaskModel> tasks = getTasksExceptionSafe();
        double pv = 0.0;
        for (final TaskModel task : tasks) {
            if (task.isDone())
                pv += task.getBudget();
        }
        return pv;
    }

    public double getPercentScheduledCompletion() {
        double result=0;
        try{
            result= getPlannedValue() / getBudgetAtCompletion() * 100.0;
        }catch(Exception e){
            logger.error("unable to calculate");
        }
        return result;
    }

    public double getPercentComplete() {
        double result=0;
        try{
            result= getEarnedValue() / getBudgetAtCompletion() * 100.0;
        }catch(Exception e){
            logger.error("unable to calculate");
        }
        return result;
    }

    public double getPlannedValue() throws Exception{
        if(getTasksExceptionSafe().stream().mapToDouble(t -> t.getPlannedValue()).sum()<=MaxBudgetProject){
            return getTasksExceptionSafe().stream().mapToDouble(t -> t.getPlannedValue()).sum();
        }else{
            logger.error("sum of the budget of the tasks passed upper boundary of project budget");
            throw new Exception("sum of the budget of the tasks passed upper boundary of project budget");
        }
    }

    public double getActualCost() throws Exception{
        if(getTasksExceptionSafe().stream().mapToDouble(t -> t.getActualCost()).sum()<=MaxBudgetProject){
            return getTasksExceptionSafe().stream().mapToDouble(t -> t.getActualCost()).sum();
        }else{
            logger.debug("sum of tasks budgets passed upper boundary of project budget");
            throw new Exception("sum of tasks passed upper boundary of project budget");
        }

    }

    public double getCostVariance() {
        double result=0;
        try{
            result= getEarnedValue() - getActualCost();
        }catch(Exception e){
            logger.error("unable to calculate");
        }
        return result;
    }

    public double getScheduleVariance() {
        double result=0;
        try{
            result= getEarnedValue() - getPlannedValue();
        }catch(Exception e){
            logger.error("unable to calculate");
        }
        return result;
    }

    public double getCostPerformanceIndex() {
        double result=0;
        try{
            result= getEarnedValue() / getActualCost();
        }catch(Exception e){
            logger.error("unable to calculate");
        }
        return result;
    }

    public double getSchedulePerformanceIndex() {
        double result=0;
        try{
            result=getEarnedValue() / getPlannedValue();
        }catch(Exception e){
            logger.error("unable to calculate");
        }
        return result;
    }

    public double getEstimateAtCompletion() {
        double result=0;
        try{
            result=getBudgetAtCompletion() / getCostPerformanceIndex();
        }catch(Exception e){
            logger.error("unable to calculate");
        }
        return result;
    }

    public double getEstimateToComplete() {
        double result=0;
        try{
            result=getEstimateAtCompletion() - getActualCost();
        }catch(Exception e){
            logger.error("unable to calculate");
        }
        return result;
    }

    @Override
	public String toString() {
        String startDateStr = "";
        String deadlineStr = "";
        String managerStr = "";
        if (getStartDate() != null) startDateStr = DateUtils.castDateToString(getStartDate());
        if (getDeadline() != null) deadlineStr = DateUtils.castDateToString(getDeadline());
        if (getManager() != null) managerStr = getManager().toString();

		return "Project ID "+ getProjectId() + ", " + " Project name: " + getName() + ", " + "Description: " +
                projectDescription + ", " + "Start date: " + startDateStr + ", " + "Deadline: " + deadlineStr + ", " +
                "Budget: " + getBudget() + ", " + "Manager ID: " + managerStr + ", " + "Done: " + isDone();
	}

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ProjectModel)) {
            return false;
        }
        ProjectModel other = (ProjectModel) obj;
        if (other.getProjectId() != this.projectId) {
            return false;
        }
        // TODO fix
        if (other.getBudget() != this.budget) {
            return false;
        }
        if (other.getStartDate() != null && startDate != null && !other.getStartDate().equals(this.startDate)) {
            return false;
        }
        if (other.getDeadline() != null && deadline != null && !other.getDeadline().equals(this.deadline)) {
            return false;
        }
        if (other.getName() != null && name != null && !other.getName().equals(this.name)) {
            return false;
        }
//        try {
//            if (other.getTasks() != null && getTasks() != null && other.getTasks().equals(this.getTasks())) {
//                return false;
//            }
//        } catch (ModelNotFoundException e) {
//            logger.debug("Hacky, ignoring for now.", e);
//        } catch (InvalidInputException e) {
//            logger.error("I was lazy...", e);
//        }

        return true;
    }

    public class ProjectModelDBObject implements DBObject {

        private final Logger logger = LoggerFactory.getLogger(ProjectModel.ProjectModelDBObject.class);

        public static final String PROJECT_ID_COLUMN = "ProjectID";
        public static final String PROJECT_NAME_COLUMN = "ProjectName";
        public static final String START_DATE_COLUMN = "StartDate";
        public static final String DEADLINE_COLUMN = "Deadline";
        public static final String BUDGET_COLUMN = "Budget";
        public static final String DONE_COLUMN = "Done";
        public static final String MANAGER_USER_ID_COLUMN = "FKManagerID";
        public static final String PROJECT_DESCRIPTION_COLUMN = "ProjectDescription";
        public static final String TABLE_NAME = "Project";

        private Database db;

        /**
         *
         */
        private ProjectModelDBObject() {
            try {
                db = DatabaseFactory.getActiveDB();
            } catch (DBException e) {
                logger.error("Unable to read from database", e);
            }
        }

        @Override
        public String getTableName() {
            return TABLE_NAME;
        }

        /**
         * This method finds the last inserted id in the table.
         * ID in the table is auto increment
         * @return if there is no error last inserted id
         * @throws SQLException if there is an error
         */
        @Override
        public int getLastInsertedId() throws DBException {
            String sql = "SELECT last_insert_rowid() AS tempID;";
            int id;
            try {
                ResultSet rs = db.query(sql);
                id = (rs.getInt("tempID"));
            } catch (SQLException e) {
                logger.error("Unable to find last inserted id" + ". Query: " + sql, e);
                throw new DBException("Unable to find last inserted id" + ". Query: " + sql, e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    logger.warn("Unable to close connection to " + db.getDbPath(), e);
                    throw new DBException("Unable to close connection to " + db.getDbPath(), e);
                }
            }
            return id;
        }

        /**
         *
         * @param sql
         * @return
         * @throws ModelNotFoundException
         */
        private ProjectModel runSingleResultQuery(String sql) throws ModelNotFoundException {
            ProjectModel p = null;
            try {
                ResultSet rs = db.query(sql);
                if (rs.next()) {
                    logger.debug("Constructing ProjectModel with DB fields");
                    int pIdTemp = rs.getInt(PROJECT_ID_COLUMN);
                    String pNameTemp = rs.getString(PROJECT_NAME_COLUMN);
                    String stDateTemp = rs.getString(START_DATE_COLUMN);
                    String dlDateTemp = rs.getString(DEADLINE_COLUMN);
                    Date stDateTempDate;
                    Date dlDateTempDate;
                    try {
                        stDateTempDate = DateUtils.castStringToDate(stDateTemp);
                        dlDateTempDate = DateUtils.castStringToDate(dlDateTemp);
                    } catch (ParseException e) {
                        logger.error("Unable to parse Date from DB, this really shouldn't happen.");
                        throw new ModelNotFoundException("Unable to parse Date from DB, this really shouldn't happen.",
                                e);
                    }
                    double budgetTemp = rs.getFloat(BUDGET_COLUMN);
                    boolean doneTemp = rs.getBoolean(DONE_COLUMN);
                    String projectDescriptionTemp = rs.getString(PROJECT_DESCRIPTION_COLUMN);
                    int managerUserIdTemp = rs.getInt(MANAGER_USER_ID_COLUMN);
                    UserModel manager = null;

                    // Set UserModel
                    if (managerUserIdTemp > 0) {
                        String firstNameTemp = rs.getString(UserModel.UserModelDBObject.FIRST_NAME_COLUMN);
                        String lastNameTemp = rs.getString(UserModel.UserModelDBObject.LAST_NAME_COLUMN);
                        double salaryPerHourTemp = rs.getFloat(UserModel.UserModelDBObject.SALARY_PER_HOUR_COLUMN);

                        manager = new UserModel(managerUserIdTemp, firstNameTemp, lastNameTemp, salaryPerHourTemp);
                    }

                    p = new ProjectModel(pIdTemp, pNameTemp, stDateTempDate, dlDateTempDate, budgetTemp, doneTemp,
                            manager, projectDescriptionTemp);
                }
                else {
                    logger.debug("DB query returned zero results");
                    throw new ModelNotFoundException("DB query for project ID " + projectId + " returned no results");
                }
            }
            catch (SQLException e) {
                logger.error("Unable to fetch project with project ID " + projectId + ". Query: " + sql, e);
                throw new ModelNotFoundException(e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    logger.debug("Unable to close connection to " + db.getDbPath(), e);
                }
            }
            return p;
        }

        /**
         *
         * @param sql
         * @return
         * @throws ModelNotFoundException
         */
        private List<ProjectModel> runMultiResultQuery(String sql) throws ModelNotFoundException {
            List<ProjectModel> projectList = new LinkedList<>();
            try {
                ResultSet rs =  db.query(sql);
                while (rs.next()) {
                    logger.debug("Constructing ProjectModel with DB fields");
                    int pIdTemp = rs.getInt(PROJECT_ID_COLUMN);
                    String pNameTemp = rs.getString(PROJECT_NAME_COLUMN);
                    String stDateTemp = rs.getString(START_DATE_COLUMN);
                    String dlDateTemp = rs.getString(DEADLINE_COLUMN);
                    Date stDateTempDate;
                    Date dlDateTempDate;
                    try {
                        stDateTempDate = DateUtils.castStringToDate(stDateTemp);
                        dlDateTempDate = DateUtils.castStringToDate(dlDateTemp);
                    } catch (ParseException e) {
                        logger.error("Unable to parse Date from DB, this really shouldn't happen.");
                        throw new ModelNotFoundException("Unable to parse Date from DB, this really shouldn't happen.",
                                e);
                    }
                    double budgetTemp = rs.getFloat(BUDGET_COLUMN);
                    boolean doneTemp = rs.getBoolean(DONE_COLUMN);
                    String projectDescriptionTemp = rs.getString(PROJECT_DESCRIPTION_COLUMN);

                    int managerUserIdTemp = rs.getInt(MANAGER_USER_ID_COLUMN);
                    UserModel manager = null;

                    // Set UserModel
                    if (managerUserIdTemp > 0) {
                        String firstNameTemp = rs.getString(UserModel.UserModelDBObject.FIRST_NAME_COLUMN);
                        String lastNameTemp = rs.getString(UserModel.UserModelDBObject.LAST_NAME_COLUMN);
                        double salaryPerHourTemp = rs.getFloat(UserModel.UserModelDBObject.SALARY_PER_HOUR_COLUMN);

                        manager = new UserModel(managerUserIdTemp, firstNameTemp, lastNameTemp, salaryPerHourTemp);
                    }

                    projectList.add(new ProjectModel(pIdTemp, pNameTemp, stDateTempDate, dlDateTempDate, budgetTemp,
                            doneTemp, manager, projectDescriptionTemp));
                }

                if (projectList.isEmpty()) {
                    logger.debug("DB query returned zero results");
                    throw new ModelNotFoundException("DB query for all projects returned no results");
                }
            }
            catch (SQLException e) {
                logger.error("Unable to fetch projects. Query: " + sql,
                        e);
                throw new ModelNotFoundException(e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    logger.debug("Unable to close connection to " + db.getDbPath(), e);
                }
            }
            return projectList;
        }

        /**
         *
         * @return
         * @throws ModelNotFoundException
         */
        @Override
        public List<ProjectModel> findAll() throws ModelNotFoundException {
            String sql = "SELECT * ";
            sql += "FROM " + getTableName() + " ";
            sql += "LEFT JOIN " + UserModel.UserModelDBObject.TABLE_NAME + " ";
            sql += "ON " + UserModel.UserModelDBObject.TABLE_NAME + "." +
                    UserModel.UserModelDBObject.USER_ID_COLUMN + "=" + TABLE_NAME + "." + MANAGER_USER_ID_COLUMN + " ";
            sql += ";";
            logger.debug("Query: " + sql);

            return runMultiResultQuery(sql);
        }

        @Override
        public ProjectModel findById(int projectId) throws ModelNotFoundException {
            logger.debug("Building query for project ID " + projectId);
            String sql = "SELECT * ";
            sql += "FROM " + getTableName() + " ";
            sql += "LEFT JOIN " + UserModel.UserModelDBObject.TABLE_NAME + " ";
            sql += "ON " + UserModel.UserModelDBObject.TABLE_NAME + "." +
                    UserModel.UserModelDBObject.USER_ID_COLUMN + "=" + TABLE_NAME + "." + MANAGER_USER_ID_COLUMN + " ";
            sql += "WHERE " + PROJECT_ID_COLUMN + "=" + projectId + " ";
            sql += ";";
            logger.debug("Query: " + sql);

            return runSingleResultQuery(sql);
        }

        /**
         *
         * @param managerUserId
         * @return
         * @throws ModelNotFoundException
         */
        public List<ProjectModel> findAllByManager(int managerUserId) throws ModelNotFoundException {
            logger.debug("Building query for projects with manager user ID " + managerUserId);
            String sql = "SELECT * ";
            sql += "FROM " + getTableName() + " ";
            sql += "LEFT JOIN " + UserModel.UserModelDBObject.TABLE_NAME + " ";
            sql += "ON " + UserModel.UserModelDBObject.TABLE_NAME + "." +
                    UserModel.UserModelDBObject.USER_ID_COLUMN + "=" + TABLE_NAME + "." + MANAGER_USER_ID_COLUMN + " ";
            sql += "WHERE " + TABLE_NAME + "." + MANAGER_USER_ID_COLUMN + "=" + managerUserId + " ";
            sql += ";";
            logger.debug("Query: " + sql);

            return runMultiResultQuery(sql);
        }

        /**
         * It fetches the data from database and make one linked list of project objects
         * @param sql asked sql statement from database
         * @return Linked list of project objects
         */
        @Override
        public List<ProjectModel> findBySql(String sql) throws ModelNotFoundException, InvalidInputException {
            return runMultiResultQuery(sql);
        }

        /**
         * This Method insert the information of new project inside database
         * if fetches the Id from inserted row and assign it to the projectId
         */
        @Override
        public int create() throws DBException {
            logger.debug("Building SQL query for project model");
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO "+ getTableName() + " ");
            sql.append("("+ PROJECT_NAME_COLUMN + "," + BUDGET_COLUMN + "," + DONE_COLUMN);
            if (getStartDate() != null) sql.append(", " + START_DATE_COLUMN);
            if (getDeadline() != null) sql.append(", " + DEADLINE_COLUMN);
            if (getManager() != null) sql.append(", " + MANAGER_USER_ID_COLUMN);
            if (getProjectDescription() != null) sql.append(", " + PROJECT_DESCRIPTION_COLUMN);
            sql.append(") ");
            sql.append("VALUES ('" + getName() + "'");
            sql.append("," + getBudget());
            sql.append("," + (isDone() ? 1 : 0));
            if (getStartDate() != null) sql.append(",'" + DateUtils.castDateToString(getStartDate()) + "'");
            if (getDeadline() != null) sql.append(",'" + DateUtils.castDateToString(getDeadline()) + "'");
            if (getManager() != null) sql.append(",'" + getManager().getUserId() + "'");
            if (getProjectDescription() != null) sql.append(",'" + getProjectDescription() + "'");
            sql.append(");");
            logger.debug("SQL query: " + sql.toString());
//            System.out.println(sql.toString());
            int insertedKey;
            try {
                insertedKey = this.db.insert(sql.toString());
                logger.debug("Insert query returned key " + insertedKey);
            } catch (SQLException e) {
                logger.error("Unable to create project " + ". Query: " + sql, e);
                throw new DBException("Unable to create project " + ". Query: " + sql, e);
            }
            finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    throw new DBException("Unable to close connection to " + db.getDbPath(), e);
                }
            }

            return insertedKey;
        }

        /**
         * This method update an object which is fetched from database
         */
        @Override
        public void update() throws DBException {
            // Build query
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE "+ getTableName() + " ");
            sql.append("SET ");
            sql.append(PROJECT_NAME_COLUMN + "='" + getName() + "',");
            sql.append(START_DATE_COLUMN + "='" +
                    (getStartDate() != null ? DateUtils.castDateToString(getStartDate()) : "NULL") + "',");
            sql.append(DEADLINE_COLUMN + "='" +
                    (getDeadline() != null ? DateUtils.castDateToString(getDeadline()) : "NULL") + "',");
            sql.append(MANAGER_USER_ID_COLUMN + "='" + (getManager() != null ? getManager().getUserId() : "0")
                    + "',");
            sql.append(PROJECT_DESCRIPTION_COLUMN + "='" + (getProjectDescription() != null ? getProjectDescription()
                    : "") + "',");
            sql.append(BUDGET_COLUMN + "='" + getBudget() + "',");
            sql.append(DONE_COLUMN + "='" + (isDone() ? 1 : 0) + "' ");
            sql.append("WHERE " + PROJECT_ID_COLUMN + "=" + getProjectId() + ";");
            logger.debug("SQL query: " + sql.toString());

            try {
                db.update(sql.toString());
            } catch (SQLException e) {
                logger.error("Unable to update project with ID " + getProjectId() + ". Query: " + sql, e);
                throw new DBException(e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    throw new DBException("Unable to close connection to " + db.getDbPath(), e);
                }
            }
        }

        @Override
        public void delete() throws DBException {
            // Build query
            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM " + getTableName() + " ");
            sql.append("WHERE " + PROJECT_ID_COLUMN + "=" + getProjectId() + ";");

            try {
                db.update(sql.toString());
            } catch (SQLException e) {
                logger.error("Unable to delete project with ID " + getProjectId() + ". Query: " + sql, e);
                throw new DBException(e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    throw new DBException("Unable to close connection to " + db.getDbPath(), e);
                }
            }
        }

        @Override
        public void clean() throws DBException{
            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM "+ getTableName()+";");
            try {
                if (this.findAll() != null) {
                    try {
                        db.update(sql.toString());
                    } catch (SQLException e) {
                        logger.error("Unable to delete data from table "+ getTableName(), e);
                        throw new DBException(e);
                    } finally {
                        try {
                            db.closeConnection();
                        } catch (SQLException e) {
                            throw new DBException("Unable to close connection to " + db.getDbPath(), e);
                        }
                    }
                }
            } catch(ModelNotFoundException e) {
                logger.debug("No projects found to clean in DB.");
            }
        }

        @Override
        public void createTable() throws DBException {
            StringBuilder sql = new StringBuilder();
            sql.append("CREATE TABLE IF NOT EXISTS "+ getTableName() + " (");
            sql.append(PROJECT_ID_COLUMN + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" + ",");
            sql.append(PROJECT_NAME_COLUMN +" VARCHAR(50) NOT NULL" + ",");
            sql.append(START_DATE_COLUMN +" DATE" + ",");
            sql.append(DEADLINE_COLUMN +" DATE" + ",");
            sql.append(BUDGET_COLUMN +" FLOAT CHECK("+ BUDGET_COLUMN+" >= 0)" + ",");
            sql.append(DONE_COLUMN +" BOOLEAN" + ",");
            sql.append(MANAGER_USER_ID_COLUMN + " INT" + ",");
            sql.append(PROJECT_DESCRIPTION_COLUMN + " TEXT" + ",");
            sql.append("FOREIGN KEY ("+MANAGER_USER_ID_COLUMN+") REFERENCES User(UserID) ON DELETE CASCADE" + ",");
            sql.append("CONSTRAINT chk_date CHECK(" + DEADLINE_COLUMN + " >= " + START_DATE_COLUMN + "));");

            try {
                db.create(sql.toString());
            } catch (SQLException e) {
                logger.error("Unable to create table "+ getTableName(), e);
                throw new DBException(e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    throw new DBException("Unable to close connection to " + db.getDbPath(), e);
                }
            }
        }
    }
}
