package com.sepgroup.sep.model;

import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.DBObject;
import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.utils.CurrencyUtils;
import com.sepgroup.sep.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jeremybrown on 2016-05-18.
 */
public class TaskModel extends AbstractModel {

    private static Logger logger = LoggerFactory.getLogger(TaskModel.class);

    private TaskModelDBObject dbo;

    private int taskId;
    private String name;
    private String description;
    private int projectId;
    private double budget;
    private Date startDate;
    private Date deadline;
    private boolean done;
    private UserModel assignee;
    private List<String> tags;
    private List<TaskModel> dependencies;

    /**
     * Default constructor
     */
    public TaskModel() {
        dbo = new TaskModelDBObject();
        tags = new LinkedList<>();
        dependencies = new LinkedList<>();
    }

    /**
     *
     * @param name
     * @param description
     * @param projectId
     */
    public TaskModel(String name, String description, int projectId) throws InvalidInputException {
        this();
        setName(name);
        setDescription(description);
        setProjectId(projectId);
    }

    /**
     *
     * @param name
     * @param description
     * @param projectId
     * @param budget
     * @param startDate
     * @param deadline
     * @param done
     * @param assignee
     * @throws InvalidInputException
     */
    public TaskModel(String name, String description, int projectId, double budget, Date startDate, Date deadline,
            boolean done, UserModel assignee) throws InvalidInputException {
        this(name, description, projectId);
        setBudget(budget);
        setStartDate(startDate);
        setDeadline(deadline);
        setDone(done);
        setAssignee(assignee);
    }

    /**
     *
     * @param name
     * @param description
     * @param projectId
     * @param budget
     * @param startDate
     * @param deadline
     * @param done
     * @param assignee
     * @param tags
     * @throws InvalidInputException
     */
    public TaskModel(String name, String description, int projectId, double budget, Date startDate, Date deadline,
            boolean done, UserModel assignee, List<String> tags) throws InvalidInputException {
        this(name, description, projectId, budget, startDate, deadline, done, assignee);
        if (tags != null) setTags(tags);
    }

    /**
     * Constructor for package use, skips validation
     * @param taskId
     * @param name
     * @param description
     * @param projectId
     * @param budget
     * @param startDate
     * @param deadline
     * @param done
     * @param assignee
     * @param tags
     */
    protected TaskModel(int taskId, String name, String description, int projectId, double budget, Date startDate,
            Date deadline, boolean done, UserModel assignee, List<String> tags) {
        this();
        this.name = name;
        this.description = description;
        this.projectId = projectId;
        this.budget = CurrencyUtils.roundToTwoDecimals(budget);
        this.startDate = startDate;
        this.deadline = deadline;
        this.done = done;
        this.assignee = assignee;
        this.tags = tags;
        this.taskId = taskId;
    }

    private static List<String> getTagsListFromString(String tagsString) {
        return Arrays.asList(tagsString.split(" "));
    }

    /**
     *
     * @return
     */
    public List<TaskModel> getDependencies() {
        // TODO return copy
        return dependencies;
    }

    private void setDependencies(List<TaskModel> dependencies) {
        this.dependencies = dependencies;
    }

    /**
     * Add a task dependency to this task.
     * Will not be added if it was already present in the list.
     * @param task task on which this task is dependent
     * @return true if the task was added to the list of dependencies, false if it was not added
     */
    public void addDependency(TaskModel task) throws TaskDependencyException {
        // Don't add dependency to itself
        if (task.equals(this)) {
            throw new TaskDependencyException("A task dependency cannot be added to itself.");
        }
        // Don't add dependency if it already exists
        else if (dependencies.stream().anyMatch(task::equals)) {
            throw new TaskDependencyException("Dependency from task " + this.getTaskId() + " to task "
                    + task.getTaskId() + " already exists.");
        }
        // Don't add dependency if it creates a cycle
        else if (hasDependencyCycle(task)) {
            throw new TaskDependencyException("Adding dependency to task " + task.getTaskId()
                    + " creates a dependency cycle.");
        }
        // Add dependency if none of the above conditions are met
        else {
            dependencies.add(task);
        }
    }

    private void internalAddDependency(TaskModel task) {
        try {
            addDependency(task);
        } catch (TaskDependencyException e) {
            throw new RuntimeException("Task dependency error", e);
        }
    }

    /**
     * Checks if adding the given task as a dependency to this task would create a dependency cycle.
     * @param task the given task
     * @return true if adding the given task as a dependency to this task would create a dependency cycle, false if not
     */
    private boolean hasDependencyCycle(TaskModel task) {
        try {
            task.refreshData();
        } catch (ModelNotFoundException e) {
            logger.error("Task not found in DB when refreshing data, it has probably been deleted since.");
            return false;
        }
        List<TaskModel> taskDependencies = task.getDependencies();
        if (taskDependencies.isEmpty()) {
            return false;
        }
        else if (taskDependencies.stream().anyMatch(this::equals)) {
            return true;
        }
        else {
            // Recusrive call
            for (TaskModel t : taskDependencies) {
                if (hasDependencyCycle(t)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     *
     * @param task
     * @return
     */
    public boolean removeDependency(TaskModel task) {
        return dependencies.remove(task);
    }

    @Override
    public void refreshData() throws ModelNotFoundException {
        TaskModel refreshed = getById(getTaskId());

        this.name = refreshed.getName();
        this.description = refreshed.getDescription();
        this.projectId = refreshed.getProjectId();
        this.budget = CurrencyUtils.roundToTwoDecimals(refreshed.getBudget());
        this.startDate = refreshed.getStartDate();
        this.deadline = refreshed.getDeadline();
        this.done = refreshed.isDone();
        this.assignee = refreshed.getAssignee();
        this.tags = refreshed.getTags();
        this.taskId = refreshed.getTaskId();
        this.dependencies = refreshed.getDependencies();

        updateObservers();
    }

    @Override
    public void persistData() throws DBException {
        if (getName() == null || getName().equals("") || getProjectId() == 0) {
            logger.error("Name & project ID must be set to persist model to DB");
            throw new DBException("Name & project ID must be set to persist model to DB");
        }
        if (this.taskId != 0) {
            // Task is already in DB
            this.dbo.update();
        }
        else {
            // Task is new, will set task id once it is saved to DB
            int taskId = this.dbo.create();
            this.taskId = taskId;
        }
        updateObservers();
    }

    @Override
    public void deleteData() throws DBException {
        this.dbo.delete();

    }

    public static List<TaskModel> getAll() throws ModelNotFoundException, InvalidInputException {
        return new TaskModel().dbo.findAll();
    }

    public static TaskModel getById(int taskId) throws ModelNotFoundException {
        return new TaskModel().dbo.findById(taskId);
    }

    public static List<TaskModel> getAllByProject(int projectId) throws ModelNotFoundException {
        return new TaskModel().dbo.findAllByProject(projectId);
    }

    public static List<TaskModel> getAllByProject(ProjectModel project) throws ModelNotFoundException,
            InvalidInputException{
        return getAllByProject(project.getProjectId());
    }

    public static List<TaskModel> getAllByAssignee(int userId) throws ModelNotFoundException, InvalidInputException {
        return new TaskModel().dbo.findAllByAssignee(userId);
    }

    public static List<TaskModel> getAllByAssignee(UserModel assignee) throws ModelNotFoundException,
            InvalidInputException {
        return getAllByAssignee(assignee.getUserId());
    }

    public static void cleanData() throws DBException{
        new TaskModel().dbo.clean();
    }

    public static void createTable() throws DBException{
        new TaskModel().dbo.createTable();
    }

    /**
     *
     * @return
     */
    public int getTaskId() {
        return taskId;
    }

    /**
     *
     * @param taskId
     * @throws InvalidInputException
     */
    private void setTaskId(int taskId) throws InvalidInputException {
        if (taskId < 0) {
            throw new InvalidInputException("Task ID must be a positive integer.");
        }
        this.taskId = taskId;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * @throws InvalidInputException
     */
    public void setName(String name) throws InvalidInputException {
        if (name.length() > 50) {
            throw new InvalidInputException("Name cannot be longer than 50 characters.");
        }
        this.name = name.replaceAll("(\\r|\\n|\\t)", " "); // Replace tabs/newlines with spaces
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     *
     * @param projectId
     */
    public void setProjectId(int projectId) throws InvalidInputException {
        if (projectId < 0) {
            throw new InvalidInputException("Project ID must be a positive integer.");
        }
        else if (projectId > 0) {
            try {
                ProjectModel.getById(projectId);
            } catch (ModelNotFoundException e) {
                throw new InvalidInputException("No project exists with ID " + projectId + ".");
            }
        }
        this.projectId = projectId;
    }

    /**
     *
     * @return
     */
    public double getBudget() {
        return budget;
    }

    /**
     *
     * @param budget
     */
    public void setBudget(double budget) throws InvalidInputException {
        if (budget < 0) {
            throw new InvalidInputException("Budget must be a positive number");
        }
        this.budget = CurrencyUtils.roundToTwoDecimals(budget);
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getStartDateString() {
        if (this.startDate != null) {
            return DateUtils.castDateToString(this.startDate);
        } else {
            return null;
        }
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

    public void removeStartDate() {
        this.startDate = null;
    }

    public Date getDeadline() {
        return deadline;
    }

    public String getDeadlineString() {
        if (this.deadline != null) {
            return DateUtils.castDateToString(this.deadline);
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
        if (deadline != null) {
            if (startDate != null) {
                if (deadline.before(startDate)) {
                    throw new InvalidInputException("Deadline must be after start date.");
                } else {
                    this.deadline = DateUtils.filterDateToMidnight(deadline);
                }
            } else {
                throw new InvalidInputException("Cannot set actual deadline on task w/o start date");
            }
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

    public void removeDeadline() {
        this.deadline = null;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public UserModel getAssignee() {
        return assignee;
    }

    public void setAssignee(UserModel assignee) throws InvalidInputException {
        if (assignee == null) {
            logger.debug("Set assignee given a null value, removing assignee.");
        }
        else if (assignee.getUserId() == 0) {
            throw new InvalidInputException("Trying to set assignee as user " +
            " ID of 0. User must be saved to database before assigning a task.");
        }
        else if (assignee.getUserId() < 0) {
            throw new InvalidInputException("User ID must be a positive integer");
        }

        this.assignee = assignee;
    }

    public void removeAssignee() {
        this.assignee = null;
    }

    public void setAssignee(int assigneeUserId) throws InvalidInputException, ModelNotFoundException {
        if (assigneeUserId == 0) {
            throw new InvalidInputException("User ID cannot be 0");
        }
        else if (assigneeUserId < 0) {
            throw new InvalidInputException("User ID must be a positive integer");
        }
        else {
            this.assignee = UserModel.getById(assigneeUserId);
        }
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        // TODO filter tags 50 chars
        this.tags = tags;
    }

    public String getTagsString() {
        return tags.stream().collect(Collectors.joining(" "));
    }

    public boolean addTag(String tag) {
        // TODO check validity 50 chars
        if (tags.stream().noneMatch(t -> t.equals(tag))) {
            tags.add(tag);
            return true;
        }
        return false;
    }

    public void addTagsFromString(String tagsString) {
        // TODO filter chars
        getTagsListFromString(tagsString).forEach(this::addTag);
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    @Override
    public String toString() {
        String startDateStr = "";
        String deadlineStr = "";
        String assigneeStr = "";
        if (getStartDate() != null) startDateStr = DateUtils.castDateToString(getStartDate());
        if (getDeadline() != null) deadlineStr = DateUtils.castDateToString(getDeadline());
        if (getAssignee() != null) assigneeStr = getAssignee().toString();
        String tagsStr = getTags().stream().collect(Collectors.joining(" "));
        String dependenciesStr = getDependencies().stream().map(TaskModel::getName).collect(Collectors.joining(", "));

        return "Task ID: " + getTaskId() + ", name: " + getName() + ", description: " + getDescription() +
                ", project ID: " + getProjectId() + ", budget: " + getBudget() + ", start date: " + startDateStr +
                ", deadline: " + deadlineStr + ", done: " + isDone() + ", manager user ID: " +
                assigneeStr + ", tags" + tagsStr + ", task dependencies: " + dependenciesStr;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TaskModel)) {
            return false;
        }
        TaskModel other = (TaskModel) obj;
        if (other.getTaskId() != getTaskId()) {
            return false;
        }
        if (!equalsNullable(other.getName(), getName())) {
            return false;
        }
        if (!equalsNullable(other.getDescription(), getDescription())) {
            return false;
        }
        if (other.getProjectId() != getProjectId()) {
            return false;
        }
        if (other.getBudget() != getBudget()) {
            return false;
        }
        if (!equalsNullable(other.getStartDate(), getStartDate())) {
            return false;
        }
        if (!equalsNullable(other.getDeadline(), getDeadline())) {
            return false;
        }
        if (other.isDone() != isDone()) {
            return false;
        }
        if (!equalsNullable(other.getAssignee(), getAssignee())) {
            return false;
        }
        if (!equalsNullable(other.getTags(), getTags())) {
            return false;
        }
//        if (!equalsNullable(other.getDependencies(), getDependencies())) {
//            return false;
//        }

        return true;
    }

    public class TaskModelDBObject implements DBObject {

        private final Logger logger = LoggerFactory.getLogger(TaskModelDBObject.class);

        public static final String TABLE_NAME = "Task";
        public static final String TASK_ID_COLUMN = "TaskID";
        public static final String PROJECT_ID_COLUMN = "FKProjectID";
        public static final String TASK_NAME_COLUMN = "TaskName";
        public static final String DESCRIPTION_COLUMN = "TaskDescription";
        public static final String START_DATE_COLUMN = "StartDate";
        public static final String DEADLINE_COLUMN = "Deadline";
        public static final String BUDGET_COLUMN = "Budget";
        public static final String DONE_COLUMN = "Done";
        public static final String TAGS_COLUMN = "Tags";
        public static final String ASSIGNEE_USER_ID_COLUMN = "FKUserID";

        public static final String DEPENDENCIES_TABLE_NAME = "TaskDependency";
        public static final String DEPENDENCIES_MAIN_TASK_COLUMN = "FKTaskID";
        public static final String DEPENDENCIES_DEPENDS_ON_TASK_COLUMN = "DependOnTaskID";

        private Database db;

        private TaskModelDBObject() {
            try {
                db = Database.getActiveDB();
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
         * @throws DBException if there is an error
         */
        @Override
        public int getLastInsertedId() throws DBException {
            String sql="SELECT last_insert_rowid() AS tempID;";
            try {
                ResultSet rs= db.query(sql);
                return (rs.getInt("tempID"));
            } catch (SQLException e) {
                logger.error("Unable to find last inserted id" + ". Query: " + sql, e);
                throw new DBException(e);
            }
        }

        /**
         *
         * @param sql
         * @return
         * @throws ModelNotFoundException
         */
        private TaskModel runSingleResultQuery(String sql) throws ModelNotFoundException {
            TaskModel m = null;
            try {
                ResultSet rs =  db.query(sql);

                if (rs.next()) {
                    // Extract values
                    int idTemp = rs.getInt(TASK_ID_COLUMN);
                    String nameTemp = rs.getString(TASK_NAME_COLUMN);
                    String descriptionTemp = rs.getString(DESCRIPTION_COLUMN);
                    int projectIdTemp = rs.getInt(PROJECT_ID_COLUMN);
                    String stDateTemp = rs.getString(START_DATE_COLUMN);
                    String dlDateTemp = rs.getString(DEADLINE_COLUMN);
                    Date stDateTempDate;
                    Date dlDateTempDate;
                    try {
                        stDateTempDate = DateUtils.castStringToDate(stDateTemp);
                        dlDateTempDate = DateUtils.castStringToDate(dlDateTemp);
                    } catch (ParseException e) {
                        logger.error("Unable to parse Date from DB, this really shouldn't happen.");
                        throw new ModelNotFoundException("Unable to parse Date from DB, this really shouldn't happen.", e);
                    }
                    double budgetTemp = rs.getFloat(BUDGET_COLUMN);
                    boolean doneTemp = rs.getBoolean(DONE_COLUMN);
                    int userIdTemp = rs.getInt(ASSIGNEE_USER_ID_COLUMN);
                    UserModel assignee = null;

                    // Set UserModel
                    if (userIdTemp > 0) {
                        String firstNameTemp = rs.getString(UserModel.UserModelDBObject.FIRST_NAME_COLUMN);
                        String lastNameTemp = rs.getString(UserModel.UserModelDBObject.LAST_NAME_COLUMN);
                        double salaryPerHourTemp = rs.getFloat(UserModel.UserModelDBObject.SALARY_PER_HOUR_COLUMN);

                        assignee = new UserModel(userIdTemp, firstNameTemp, lastNameTemp, salaryPerHourTemp);
                    }

                    String tagsTemp = rs.getString(TAGS_COLUMN);
                    List<String> tagsListTemp;
                    if (tagsTemp != null) {
                        tagsListTemp = getTagsListFromString(tagsTemp);
                    }
                    else {
                        tagsListTemp = new LinkedList<>();
                    }
                    m = new TaskModel(idTemp, nameTemp, descriptionTemp, projectIdTemp, budgetTemp, stDateTempDate,
                            dlDateTempDate, doneTemp, assignee, tagsListTemp);
                }
                else {
                    logger.debug("DB query returned zero results");
                    throw new ModelNotFoundException("DB query for task returned no results");
                }
            }
            catch (SQLException e) {
                logger.error("Unable to fetch task. Query: " + sql, e);
                throw new ModelNotFoundException(e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    logger.debug("Unable to close connection to " + db.getDbPath(), e);
                }
            }

            return m;
        }

        private List<TaskModel> runMultiResultQuery(String sql) throws ModelNotFoundException {
            List<TaskModel> taskList = new LinkedList<>();
            try {
                ResultSet rs =  db.query(sql);

                while (rs.next()) {
                    // Extract values
                    int idTemp = rs.getInt(TASK_ID_COLUMN);
                    String nameTemp = rs.getString(TASK_NAME_COLUMN);
                    String descriptionTemp = rs.getString(DESCRIPTION_COLUMN);
                    int projectIdTemp = rs.getInt(PROJECT_ID_COLUMN);
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
                    int userIdTemp = rs.getInt(ASSIGNEE_USER_ID_COLUMN);
                    UserModel assignee = null;

                    // Set UserModel
                    if (userIdTemp > 0) {
                        String firstNameTemp = rs.getString(UserModel.UserModelDBObject.FIRST_NAME_COLUMN);
                        String lastNameTemp = rs.getString(UserModel.UserModelDBObject.LAST_NAME_COLUMN);
                        double salaryPerHourTemp = rs.getFloat(UserModel.UserModelDBObject.SALARY_PER_HOUR_COLUMN);

                        assignee = new UserModel(userIdTemp, firstNameTemp, lastNameTemp, salaryPerHourTemp);
                    }

                    String tagsTemp = rs.getString(TAGS_COLUMN);
                    List<String> tagsListTemp;
                    if (tagsTemp != null) {
                        tagsListTemp = getTagsListFromString(tagsTemp);
                    }
                    else {
                        tagsListTemp = new LinkedList<>();
                    }
                    taskList.add(new TaskModel(idTemp, nameTemp, descriptionTemp, projectIdTemp, budgetTemp,
                            stDateTempDate, dlDateTempDate, doneTemp, assignee, tagsListTemp));
                }

                if (taskList.isEmpty()) {
                    logger.debug("DB query returned zero results");
                    throw new ModelNotFoundException("DB query for tasks returned no results");
                }
            }
            catch (SQLException e) {
                logger.error("Unable to fetch entries in Task table" + ". Query: " + sql, e);
                throw new ModelNotFoundException(e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    logger.debug("Unable to close connection to " + db.getDbPath(), e);
                }
            }

            return taskList;
        }

        @Override
        public List<TaskModel> findAll() throws ModelNotFoundException  {
            String sql = "SELECT * " + "FROM " + getTableName() + " ";
            sql += "LEFT JOIN " + UserModel.UserModelDBObject.TABLE_NAME + " ";
            sql += "ON " + UserModel.UserModelDBObject.TABLE_NAME + "." +
                    UserModel.UserModelDBObject.USER_ID_COLUMN + "=" + TABLE_NAME + "." + ASSIGNEE_USER_ID_COLUMN
                    + ";";

            List<TaskModel> tasks = runMultiResultQuery(sql);
            for (TaskModel t : tasks) {
                List<TaskModel> dependencies = findTaskDependencies(t);
                dependencies.forEach(t::internalAddDependency);
            }
            return tasks;
        }

        /**
         * Get a list of TaskModel objects that are the dependencies of the specified TaskModel
         * @param m the TaskModel to find dependencies
         * @return the dependencies of the taskModel
         */
        public List<TaskModel> findTaskDependencies(TaskModel m) {
            return findTaskDependencies(m.getTaskId());
        }

        /**
         * Get a list of TaskModel objects that are the dependencies of the task with the specified task ID
         * @param taskId the ID of the task to find dependencies for
         * @return the dependencies of the taskModel
         */
        public List<TaskModel> findTaskDependencies(int taskId) {
            String sql = "SELECT * FROM " + TABLE_NAME + " ";
            sql += "LEFT JOIN " + UserModel.UserModelDBObject.TABLE_NAME + " ";
            sql += "ON " + UserModel.UserModelDBObject.TABLE_NAME + "." +
                    UserModel.UserModelDBObject.USER_ID_COLUMN + "=" + TABLE_NAME + "." + ASSIGNEE_USER_ID_COLUMN
                    + " ";
            sql += "WHERE " + TABLE_NAME + "." + TASK_ID_COLUMN + " in( ";
            sql += "SELECT " + DEPENDENCIES_TABLE_NAME + "." + DEPENDENCIES_DEPENDS_ON_TASK_COLUMN + " ";
            sql += "FROM " + DEPENDENCIES_TABLE_NAME + " ";
            sql += "WHERE " + DEPENDENCIES_TABLE_NAME + "." + DEPENDENCIES_MAIN_TASK_COLUMN + "=" + taskId + ");";
//            sql.append("INNER JOIN " + DEPENDENCIES_TABLE_NAME + " ");
//            sql.append("ON " + TABLE_NAME + "." + TASK_ID_COLUMN + "=" + DEPENDENCIES_TABLE_NAME + "." +
//                    DEPENDENCIES_DEPENDS_ON_TASK_COLUMN + " ");
//            sql.append("WHERE " + DEPENDENCIES_TABLE_NAME + "." + DEPENDENCIES_MAIN_TASK_COLUMN + "=" + taskId + ";");
            logger.debug("Query: " + sql);

            // TODO FIX BROKEN QUERY

            try {
//                return runMultiResultQuery(sql.toString());
                List<TaskModel> tasks = runMultiResultQuery(sql);
                for (TaskModel t : tasks) {
                    List<TaskModel> dependencies = findTaskDependencies(t);
                    dependencies.forEach(t::internalAddDependency);
                }
                return tasks;
            } catch (ModelNotFoundException e) {
                logger.debug("No task dependencies found for task with ID " + taskId);
            }
            return new LinkedList<>();
        }

        @Override
        public TaskModel findById(int taskId) throws ModelNotFoundException {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + " ");
            sql.append("LEFT JOIN " + UserModel.UserModelDBObject.TABLE_NAME + " ");
            sql.append("ON " + UserModel.UserModelDBObject.TABLE_NAME + "." +
                    UserModel.UserModelDBObject.USER_ID_COLUMN + "=" + TABLE_NAME + "." + ASSIGNEE_USER_ID_COLUMN
                    + " ");
            sql.append("WHERE " + TASK_ID_COLUMN + "=" + taskId + ";");
            logger.debug("Query: " + sql.toString());

            TaskModel t = runSingleResultQuery(sql.toString());
            findTaskDependencies(t).forEach(t::internalAddDependency);

            return t;
        }

        public List<TaskModel> findAllByAssignee(int assigneeUserId) throws ModelNotFoundException,
                InvalidInputException {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + " ");
            sql.append("LEFT JOIN " + UserModel.UserModelDBObject.TABLE_NAME + " ");
            sql.append("ON " + UserModel.UserModelDBObject.TABLE_NAME + "." +
                    UserModel.UserModelDBObject.USER_ID_COLUMN + "=" + TABLE_NAME + "." + ASSIGNEE_USER_ID_COLUMN
                    + " ");
            sql.append("WHERE " + ASSIGNEE_USER_ID_COLUMN + "=" + assigneeUserId + ";");

            List<TaskModel> tasks = runMultiResultQuery(sql.toString());
            for (TaskModel t : tasks) {
                List<TaskModel> dependencies = findTaskDependencies(t);
                dependencies.forEach(t::internalAddDependency);
            }

            return tasks;
        }

        public List<TaskModel> findAllByProject(int projectId) throws ModelNotFoundException {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + " ");
            sql.append("LEFT JOIN " + UserModel.UserModelDBObject.TABLE_NAME + " ");
            sql.append("ON " + UserModel.UserModelDBObject.TABLE_NAME + "." +
                    UserModel.UserModelDBObject.USER_ID_COLUMN + "=" + TABLE_NAME + "." + ASSIGNEE_USER_ID_COLUMN
                    + " ");
            sql.append("WHERE " + PROJECT_ID_COLUMN + "=" + projectId + ";");

            List<TaskModel> tasks = runMultiResultQuery(sql.toString());
            for (TaskModel t : tasks) {
                List<TaskModel> dependencies = findTaskDependencies(t);
                dependencies.forEach(t::internalAddDependency);
            }

            return tasks;
        }

        @Override
        public List<TaskModel> findBySql(String sql) throws ModelNotFoundException {
            List<TaskModel> tasks = runMultiResultQuery(sql);
            for (TaskModel t : tasks) {
                List<TaskModel> dependencies = findTaskDependencies(t);
                dependencies.forEach(t::internalAddDependency);
            }
            return tasks;
        }

        private void addDependencyToDb(int baseTaskId, TaskModel dependsOnTask) {
            if (baseTaskId == dependsOnTask.getTaskId()) {
                logger.error("A task cannot depend on itself! Aborting save to DB.");
            }
            logger.debug("Building SQL query for task dependencies");
            StringBuilder depSql = new StringBuilder();
            depSql.append("INSERT INTO " + DEPENDENCIES_TABLE_NAME + " ");
            depSql.append("( " + DEPENDENCIES_MAIN_TASK_COLUMN + ", " + DEPENDENCIES_DEPENDS_ON_TASK_COLUMN + ") ");
            depSql.append("VALUES (" + baseTaskId + "," + dependsOnTask.getTaskId() + ");");

            try {
                db.insert(depSql.toString());
            } catch (SQLException e) {
                logger.error("Unable to create task dependency. Query: " + depSql, e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    logger.error("Unable to close DB connection", e);
                }
            }
        }

        private void deleteDependencyFromDb(int baseTaskId, TaskModel dependsOnTask) {
            logger.debug("Building SQL query for task dependencies");
            StringBuilder depSql = new StringBuilder();
            depSql.append("DELETE FROM " + DEPENDENCIES_TABLE_NAME + " ");
            depSql.append("WHERE " + DEPENDENCIES_MAIN_TASK_COLUMN + "=" + baseTaskId + " ");
            depSql.append("AND " + DEPENDENCIES_DEPENDS_ON_TASK_COLUMN + "=" + dependsOnTask.getTaskId() + ";");

            try {
                db.insert(depSql.toString());
            } catch (SQLException e) {
                logger.error("Unable to delete task dependency. Query: " + depSql, e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    logger.error("Unable to close DB connection", e);
                }
            }
        }

        @Override
        public int create() throws DBException {
            logger.debug("Building SQL query for task model");
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO "+ getTableName() + " ");
            sql.append("(" + TASK_NAME_COLUMN);
            if (getDescription() != null) sql.append("," + DESCRIPTION_COLUMN);
            sql.append("," + PROJECT_ID_COLUMN);
            sql.append("," + BUDGET_COLUMN);
            if (getStartDate() != null) sql.append("," + START_DATE_COLUMN);
            if (getDeadline() != null) sql.append("," + DEADLINE_COLUMN);
            sql.append("," + DONE_COLUMN);
            if (getAssignee() != null) sql.append("," + ASSIGNEE_USER_ID_COLUMN);
            if (getTags().size() > 0) sql.append("," + TAGS_COLUMN);
            sql.append(") ");

            sql.append("VALUES ('" + getName() + "'");
            if (getDescription() != null) sql.append(",'" + getDescription() + "'");
            sql.append("," + getProjectId() + "");
            sql.append(",'" + getBudget() + "'");
            if (getStartDate() != null) sql.append(",'" + DateUtils.castDateToString(getStartDate()) + "'");
            if (getDeadline() != null) sql.append(",'" + DateUtils.castDateToString(getDeadline()) + "'");
            sql.append(",'" + (isDone() ? 1 : 0) + "'");
            if (getAssignee() != null) sql.append(",'" + getAssignee().getUserId() + "'");
            if (getTags().size() > 0)sql.append(",'" + getTagsString() + "'");
            sql.append(");");

            logger.debug("SQL query: " + sql.toString());

            int insertedKey;
            try {
                insertedKey = db.insert(sql.toString());
            } catch (SQLException e) {
                logger.error("Unable to create task " + ". Query: " + sql, e);
                throw new DBException(e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    throw new DBException(e);
                }
            }

            // Add dependencies
            getDependencies().forEach(t -> addDependencyToDb(insertedKey, t));

            return insertedKey;
        }

        @Override
        public void update() throws DBException {
            // Build query
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE "+ getTableName() + " ");
            sql.append("SET ");
            sql.append(TASK_NAME_COLUMN + "='" + getName() + "'");
            sql.append(", " + DESCRIPTION_COLUMN + "='" + (getDescription() != null ? getDescription() : "") + "' ");
            sql.append(", " + PROJECT_ID_COLUMN + "=" + getProjectId() + " ");
            sql.append(", " + BUDGET_COLUMN + "=" + getBudget() + " ");
            sql.append(", " + START_DATE_COLUMN + "='" +
                    (getStartDate() != null ? DateUtils.castDateToString(getStartDate()) : "NULL") + "' ");
            sql.append(", " + DEADLINE_COLUMN + "='" +
                    (getDeadline() != null ? DateUtils.castDateToString(getDeadline()) : "NULL") + "' ");
            sql.append(", " + DONE_COLUMN + "=" + (isDone() ? 1 : 0) + " ");
            sql.append(", " + ASSIGNEE_USER_ID_COLUMN + "=" +
                    (getAssignee() != null ? getAssignee().getUserId() : "0") + " ");
            if (getTags().size() > 0) sql.append(", " + TAGS_COLUMN + "='" + getTagsString() + "' ");
            sql.append("WHERE " + TASK_ID_COLUMN + "=" + getTaskId() + ";");

            try {
                db.update(sql.toString());
            } catch (SQLException e) {
                logger.error("Unable to update task with taskId" + getTaskId() + ". Query: " + sql, e);
                throw new DBException(e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    throw new DBException(e);
                }
            }

            List<TaskModel> previousDependencies = findTaskDependencies(getTaskId());

            // Remove deleted dependencies from DB
            List<TaskModel> dependenciesToRemove = new LinkedList<>();
            previousDependencies.forEach(d -> {
                if (!getDependencies().contains(d)) {
                    dependenciesToRemove.add(d);
                }
            });
            dependenciesToRemove.forEach(d -> deleteDependencyFromDb(getTaskId(), d));

            // Add created dependencies to DB
            List<TaskModel> dependenciesToAdd = new LinkedList<>();
            getDependencies().forEach(d -> {
                if (!previousDependencies.contains(d)) {
                    dependenciesToAdd.add(d);
                }
            });
            dependenciesToAdd.forEach(d -> addDependencyToDb(getTaskId(), d));
        }

        @Override
        public void delete() throws DBException {
            // Build query
            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM " + getTableName() + " ");
            sql.append("WHERE " + TASK_ID_COLUMN + "=" + getTaskId() + ";");

            try {
                db.update(sql.toString());
            } catch (SQLException e) {
                logger.error("Unable to delete task with taskId" + getTaskId() + ". Query: " + sql, e);
                throw new DBException("Unable to delete task with taskId" + getTaskId() + ". Query: " + sql, e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    throw new DBException("Unable to close connection to " + db.getDbPath(), e);
                }
            }
        }

        @Override
        public void clean() throws DBException {
            String cleanTaskTableSql = "DELETE FROM "+ getTableName()+";";

            try {
                if (this.findAll() != null) {
                    try {
                        db.update(cleanTaskTableSql);
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
                logger.debug(e.getLocalizedMessage());
            }

            // Clean task dependencies table
            String fetchDependency="SELECT * FROM TaskDependency;";
            try {
                ResultSet rs = db.query(fetchDependency);
                if (rs.next()) {
                    String cleanTaskDependenciesTableSql = "DELETE FROM TaskDependency;";
                    db.update(cleanTaskDependenciesTableSql);
                }
            } catch (SQLException e) {
                logger.error("Unable to delete data from table " + getTableName(), e);
            }
        }


        @Override
        public void createTable() throws DBException{
            // Create task table query
            StringBuilder createTaskTableSql = new StringBuilder();
            createTaskTableSql.append("CREATE TABLE IF NOT EXISTS "+ getTableName()+" (");
            createTaskTableSql.append(TASK_ID_COLUMN+ " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT"+",");
            createTaskTableSql.append(PROJECT_ID_COLUMN+" INTEGER NOT NULL"+",");
            createTaskTableSql.append(TASK_NAME_COLUMN+" VARCHAR(50) NOT NULL"+",");
            createTaskTableSql.append(START_DATE_COLUMN+" DATE"+",");
            createTaskTableSql.append(DEADLINE_COLUMN+" DATE"+",");
            createTaskTableSql.append(BUDGET_COLUMN+" FLOAT CHECK("+BUDGET_COLUMN+" >= 0)"+",");
            createTaskTableSql.append(DONE_COLUMN+" BOOLEAN"+",");
            createTaskTableSql.append(TAGS_COLUMN+" TEXT"+",");
            createTaskTableSql.append(DESCRIPTION_COLUMN+" TEXT"+",");
            createTaskTableSql.append(ASSIGNEE_USER_ID_COLUMN+" INT"+",");
            createTaskTableSql.append("FOREIGN KEY ("+PROJECT_ID_COLUMN+") REFERENCES Project(ProjectID) ON DELETE CASCADE"+",");
            createTaskTableSql.append("FOREIGN KEY ("+ASSIGNEE_USER_ID_COLUMN+") REFERENCES User(UserID) ON DELETE CASCADE"+",");
            createTaskTableSql.append("CONSTRAINT chk_date CHECK(" + DEADLINE_COLUMN + " >= "+START_DATE_COLUMN+"));");

            // Create task dependencies table query
            StringBuilder createTaskDependenciesTableSql = new StringBuilder();
            createTaskDependenciesTableSql.append("CREATE TABLE IF NOT EXISTS TaskDependency(");
            createTaskDependenciesTableSql.append("FKTaskID INT NOT NULL,");
            createTaskDependenciesTableSql.append("DependOnTaskID INT NOT NULL,");
            createTaskDependenciesTableSql.append("FOREIGN KEY (FKTaskID) REFERENCES Task(TaskID) ON DELETE CASCADE,");
            createTaskDependenciesTableSql.append("FOREIGN KEY (DependOnTaskID) REFERENCES Task(TaskID) ON DELETE CASCADE);");

            // Run create queries
            try {
                db.create(createTaskTableSql.toString());
                db.create(createTaskDependenciesTableSql.toString());
            } catch (SQLException e) {
                logger.error("Unable to create table", e);
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
