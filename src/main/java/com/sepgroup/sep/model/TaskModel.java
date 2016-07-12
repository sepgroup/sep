package com.sepgroup.sep.model;

import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.custom.mysql.MysObjects;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
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

import static com.healthmarketscience.sqlbuilder.SqlObject.NULL_VALUE;

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
        if (tagsString.trim().equals("")) {
            return new LinkedList<>();
        } else {
            return Arrays.asList(tagsString.split(" "));
        }
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
     * Add a task dependency to this task
     * @param task task on which this task is dependent
     * @return true if the task was not already in the list of dependencies
     */
    public boolean addDependency(TaskModel task) {
        if (dependencies.stream().noneMatch((t) -> t.equals(task))) {
            dependencies.add(task);
            return true;
        } else {
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
    public void refreshData() throws ModelNotFoundException, InvalidInputException {
        TaskModel refreshed = getById(getTaskId());

        setTaskId(refreshed.getTaskId());
        setName(refreshed.getName());
        setDescription(refreshed.getDescription());
        setProjectId(refreshed.getProjectId());
        setBudget(refreshed.getBudget());
        setStartDate(refreshed.getStartDate());
        setDeadline(refreshed.getDeadline());
        setDone(refreshed.isDone());
        setAssignee(refreshed.getAssignee());
        setTags(refreshed.getTags());
        setDependencies(refreshed.getDependencies());

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

    public static TaskModel getById(int taskId) throws ModelNotFoundException, InvalidInputException {
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

    public static void cleanData()throws DBException{
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
        name = name.replaceAll("\\n", " "); // Replace newlines with spaces
        if (name.length() > 50) {
            throw new InvalidInputException("Name cannot be longer than 50 characters.");
        }
        this.name = name;
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
            UserModel assignee = UserModel.getById(assigneeUserId);
            this.assignee = assignee;
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
        getTagsListFromString(tagsString).forEach(t -> addTag(t));
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
        String dependenciesStr = getDependencies().stream().map(t -> t.getName()).collect(Collectors.joining(", "));

        return "Task ID: " + getTaskId() + ", name: " + getName() + ", description: " + getDescription() +
                ", project ID: " + getProjectId() + ", budget: " + getBudget() + ", start date: " + startDateStr +
                ", deadline: " + deadlineStr + ", done: " + isDone() + ", manager user ID: " +
                assigneeStr + ", tags" + tagsStr + ", task dependencies: " + dependenciesStr;
    }

    @Override
    public boolean equals(Object obj) {
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
        if (!equalsNullable(other.getDependencies(), getDependencies())) {
            return false;
        }

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

        DbTable taskTable;
        DropQuery dropTaskTableQuery;
        DbColumn taskIdColumn;
        DbColumn projectIdColumn;
        DbColumn taskNameColumn;
        DbColumn descriptionColumn;
        DbColumn startDateColumn;
        DbColumn deadlineColumn;
        DbColumn budgetColumn;
        DbColumn doneColumn;
        DbColumn tagsColumn;
        DbColumn assigneeUserIdColumn;

        DbTable taskDependenciesTable;
        DropQuery dropTaskDependenciesTableQuery;
        DbColumn dependenciesMainTaskColumn;
        DbColumn dependenciesDependsOnTaskColumn;

        private TaskModelDBObject() {
            try {
                db = Database.getActiveDB();
            } catch (DBException e) {
                logger.error("Unable to read from database", e);
            }
            setUpTableSchema();
        }

        private void setUpTableSchema() {
            // Add table w/ info
            taskTable = db.getDbchema().addTable(TABLE_NAME);
            taskIdColumn = taskTable.addColumn(TASK_ID_COLUMN, "integer", null);
            taskIdColumn.notNull();
            taskIdColumn.primaryKey();
            projectIdColumn = taskTable.addColumn(PROJECT_ID_COLUMN, "integer", null);
            projectIdColumn.notNull();
            projectIdColumn.references("fk_project_id", ProjectModel.ProjectModelDBObject.TABLE_NAME, ProjectModel.ProjectModelDBObject.PROJECT_ID_COLUMN);
            // todo on delete cascade
            taskNameColumn = taskTable.addColumn(TASK_NAME_COLUMN, "varchar", 50);
            taskNameColumn.notNull();
            descriptionColumn = taskTable.addColumn(DESCRIPTION_COLUMN, "text", null);
            startDateColumn = taskTable.addColumn(START_DATE_COLUMN, "date", null);
            deadlineColumn = taskTable.addColumn(DEADLINE_COLUMN, "date", null);
            taskTable.checkCondition("chk_date_task", BinaryCondition.greaterThan(deadlineColumn, startDateColumn, true));
            budgetColumn = taskTable.addColumn(BUDGET_COLUMN, "float", null);
            budgetColumn.checkCondition("check_budget_task", BinaryCondition.greaterThan(budgetColumn, 0, true));
            doneColumn = taskTable.addColumn(DONE_COLUMN, "boolean", null);
            tagsColumn = taskTable.addColumn(TAGS_COLUMN, "text", null);
            assigneeUserIdColumn = taskTable.addColumn(ASSIGNEE_USER_ID_COLUMN, "integer", null);
            assigneeUserIdColumn.references("fk_task_assignee", UserModel.UserModelDBObject.TABLE_NAME, UserModel.UserModelDBObject.USER_ID_COLUMN);
            // todo on delete cascade

            taskDependenciesTable = db.getDbchema().addTable(DEPENDENCIES_TABLE_NAME);
            dependenciesMainTaskColumn = taskDependenciesTable.addColumn(DEPENDENCIES_MAIN_TASK_COLUMN, "integer", null);
            dependenciesMainTaskColumn.notNull();
            dependenciesMainTaskColumn.references("fk_main_task", taskTable, taskIdColumn);
            dependenciesDependsOnTaskColumn = taskDependenciesTable.addColumn(DEPENDENCIES_DEPENDS_ON_TASK_COLUMN, "integer", null);
            dependenciesDependsOnTaskColumn.notNull();
            dependenciesDependsOnTaskColumn.references("fk_depends_on_task", taskTable, taskIdColumn);
            // todo on delete cascade
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
            logger.debug("Building select (find all) SQL query for Task");
            String findAll = new SelectQuery()
                    .addAllColumns()
                    .addFromTable(taskTable)
                    .addJoin(SelectQuery.JoinType.LEFT_OUTER, taskTable,
                            db.getDbchema().findTable(UserModel.UserModelDBObject.TABLE_NAME),
                            assigneeUserIdColumn, db.getDbchema().findTable(UserModel.UserModelDBObject.TABLE_NAME).
                                    findColumn(UserModel.UserModelDBObject.USER_ID_COLUMN))
                    .validate().toString();
            logger.debug("Query: " + findAll);

            List<TaskModel> tasks = runMultiResultQuery(findAll);
            for (TaskModel t : tasks) {
                List<TaskModel> dependencies = findTaskDependencies(t);
                dependencies.forEach(t::addDependency);
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
            logger.debug("Building select (find by main task) SQL query for task dependencies");
            String findById = new SelectQuery()
                    .addAllColumns()
                    .addFromTable(taskTable)
                    .addJoin(SelectQuery.JoinType.LEFT_OUTER, taskTable,
                            db.getDbchema().findTable(UserModel.UserModelDBObject.TABLE_NAME),
                            assigneeUserIdColumn, db.getDbchema().findTable(UserModel.UserModelDBObject.TABLE_NAME).
                                    findColumn(UserModel.UserModelDBObject.USER_ID_COLUMN))
                    .addJoin(SelectQuery.JoinType.INNER, taskTable, taskDependenciesTable,
                            taskIdColumn, dependenciesDependsOnTaskColumn)
                    .addCondition(BinaryCondition.equalTo(dependenciesMainTaskColumn, taskId))
                    .validate().toString();
            logger.debug("Query: " + findById);

            try {
                return runMultiResultQuery(findById);
            } catch (ModelNotFoundException e) {
                logger.debug("No task dependencies found for task with ID " + taskId);
            }
            return new LinkedList<>();
        }

        @Override
        public TaskModel findById(int taskId) throws ModelNotFoundException, InvalidInputException {
            logger.debug("Building select (find by ID) SQL query for Task");
            String findById = new SelectQuery()
                    .addAllColumns()
                    .addFromTable(taskTable)
                    .addJoin(SelectQuery.JoinType.LEFT_OUTER, taskTable,
                            db.getDbchema().findTable(UserModel.UserModelDBObject.TABLE_NAME),
                            assigneeUserIdColumn, db.getDbchema().findTable(UserModel.UserModelDBObject.TABLE_NAME).
                                    findColumn(UserModel.UserModelDBObject.USER_ID_COLUMN))
                    .addCondition(BinaryCondition.equalTo(taskIdColumn, taskId))
                    .validate().toString();
            logger.debug("Query: " + findById);

            TaskModel t = runSingleResultQuery(findById);
            findTaskDependencies(t).forEach(t::addDependency);

            return t;
        }

        public List<TaskModel> findAllByAssignee(int assigneeUserId) throws ModelNotFoundException,
                InvalidInputException {
            logger.debug("Building select (find all by assignee) SQL query for Task");
            String findAllByAssigneeSql = new SelectQuery()
                    .addAllColumns()
                    .addFromTable(taskTable)
                    .addJoin(SelectQuery.JoinType.LEFT_OUTER, taskTable,
                            db.getDbchema().findTable(UserModel.UserModelDBObject.TABLE_NAME),
                            assigneeUserIdColumn, db.getDbchema().findTable(UserModel.UserModelDBObject.TABLE_NAME).
                                    findColumn(UserModel.UserModelDBObject.USER_ID_COLUMN))
                    .addCondition(BinaryCondition.equalTo(assigneeUserIdColumn, assigneeUserId))
                    .validate().toString();
            logger.debug("Query: " + findAllByAssigneeSql);

            List<TaskModel> tasks = runMultiResultQuery(findAllByAssigneeSql);
            for (TaskModel t : tasks) {
                List<TaskModel> dependencies = findTaskDependencies(t);
                dependencies.forEach(t::addDependency);
            }

            return tasks;
        }

        public List<TaskModel> findAllByProject(int projectId) throws ModelNotFoundException {
            logger.debug("Building select (find all by project) SQL query for Task");
            String findAllByProjectSql = new SelectQuery()
                    .addAllColumns()
                    .addFromTable(taskTable)
                    .addJoin(SelectQuery.JoinType.LEFT_OUTER, taskTable,
                            db.getDbchema().findTable(UserModel.UserModelDBObject.TABLE_NAME),
                            assigneeUserIdColumn, db.getDbchema().findTable(UserModel.UserModelDBObject.TABLE_NAME).
                                    findColumn(UserModel.UserModelDBObject.USER_ID_COLUMN))
                    .addCondition(BinaryCondition.equalTo(projectIdColumn, projectId))
                    .validate().toString();
            logger.debug("Query: " + findAllByProjectSql);

            List<TaskModel> tasks = runMultiResultQuery(findAllByProjectSql);
            for (TaskModel t : tasks) {
                List<TaskModel> dependencies = findTaskDependencies(t);
                dependencies.forEach(t::addDependency);
            }

            return tasks;
        }

        @Override
        public List<TaskModel> findBySql(String sql) throws ModelNotFoundException {
            List<TaskModel> tasks = runMultiResultQuery(sql);
            for (TaskModel t : tasks) {
                List<TaskModel> dependencies = findTaskDependencies(t);
                dependencies.forEach(t::addDependency);
            }
            return tasks;
        }

        private void addDependencyToDb(int baseTaskId, TaskModel dependsOnTask) {
            if (baseTaskId == dependsOnTask.getTaskId()) {
                logger.error("A task cannot depend on itself! Aborting save to DB.");
            }
            // TODO determine cycles

            logger.debug("Building insert SQL query for task dependencies");
            String insertTaskDependencySql = new InsertQuery(taskDependenciesTable)
                    .addColumn(dependenciesMainTaskColumn, baseTaskId)
                    .addColumn(dependenciesDependsOnTaskColumn, dependsOnTask.getTaskId())
                    .validate().toString();
            logger.debug("SQL query: " + insertTaskDependencySql);

            try {
                db.insert(insertTaskDependencySql);
            } catch (SQLException e) {
                logger.error("Unable to create task dependency. Query: " + insertTaskDependencySql, e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    logger.error("Unable to close DB connection", e);
                }
            }
        }

        private void deleteDependencyFromDb(int baseTaskId, TaskModel dependsOnTask) {
            logger.debug("Building delete SQL query for task dependencies");
            String deleteTaskDependencySql = new DeleteQuery(taskDependenciesTable)
                    .addCondition(BinaryCondition.equalTo(dependenciesDependsOnTaskColumn, dependsOnTask.getTaskId()))
                    .validate().toString();
            logger.debug("SQL query: " + deleteTaskDependencySql);

            try {
                db.insert(deleteTaskDependencySql);
            } catch (SQLException e) {
                logger.error("Unable to delete task dependency. Query: " + deleteTaskDependencySql, e);
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
            logger.debug("Building create SQL query for task model");
            String insertTaskSql = new InsertQuery(taskTable)
                    .addColumn(taskNameColumn, getName())
                    .addColumn(projectIdColumn, getProjectId())
                    .addColumn(descriptionColumn, getDescription())
                    .addColumn(startDateColumn, (getStartDate() != null ? DateUtils.castDateToString(getStartDate()) : NULL_VALUE))
                    .addColumn(deadlineColumn, (getDeadline() != null ? DateUtils.castDateToString(getDeadline()) : NULL_VALUE))
                    .addColumn(budgetColumn, getBudget())
                    .addColumn(doneColumn, (isDone() ? 1 : 0))
                    .addColumn(assigneeUserIdColumn, (getAssignee() != null ? getAssignee().getUserId() : "0"))
                    .addColumn(tagsColumn, getTagsString())
                    .validate().toString();
            logger.debug("SQL query: " + insertTaskSql);

            int insertedKey;
            try {
                insertedKey = db.insert(insertTaskSql);
            } catch (SQLException e) {
                logger.error("Unable to create task " + ". Query: " + insertTaskSql, e);
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
            logger.debug("Building update SQL query for task model");
            String updateTaskSql = new UpdateQuery(taskTable)
                    .addSetClause(taskNameColumn, getName())
                    .addSetClause(descriptionColumn, (getDescription() != null ? getDescription() : ""))
                    .addSetClause(projectIdColumn, getProjectId())
                    .addSetClause(budgetColumn, getBudget())
                    .addSetClause(startDateColumn, (getStartDate() != null ? DateUtils.castDateToString(getStartDate()) : NULL_VALUE))
                    .addSetClause(deadlineColumn, (getDeadline() != null ? DateUtils.castDateToString(getDeadline()) : NULL_VALUE))
                    .addSetClause(doneColumn, (isDone() ? 1 : 0))
                    .addSetClause(assigneeUserIdColumn, (getAssignee() != null ? getAssignee().getUserId() : 0))
                    .addSetClause(tagsColumn, getTagsString())
                    .addCondition(BinaryCondition.equalTo(taskIdColumn, getTaskId()))
                    .validate().toString();
            logger.debug("SQL query: " + updateTaskSql);

            try {
                db.update(updateTaskSql);
            } catch (SQLException e) {
                logger.error("Unable to update task with taskId" + getTaskId() + ". Query: " + updateTaskSql, e);
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
            logger.debug("Building delete SQL query for task model");
            String deleteTaskSql = new DeleteQuery(taskTable)
                    .addCondition(BinaryCondition.equalTo(taskIdColumn, getTaskId()))
                    .validate().toString();
            logger.debug("SQL query: " + deleteTaskSql);

            try {
                db.update(deleteTaskSql);
            } catch (SQLException e) {
                logger.error("Unable to delete task with taskId" + getTaskId() + ". Query: " + deleteTaskSql, e);
                throw new DBException("Unable to delete task with taskId" + getTaskId() + ". Query: " + deleteTaskSql, e);
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
            String cleanTaskTableSql = new DeleteQuery(taskTable).validate().toString();

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
        public void createTable() throws DBException {
            // Task table create query
            logger.debug("Building SQL query for creating " + TABLE_NAME + " table");
            CreateTableQuery createTaskTableQuery = new CreateTableQuery(taskTable, true)
                    .addCustomization(MysObjects.IF_NOT_EXISTS_TABLE)
                    .validate();
            dropTaskTableQuery = createTaskTableQuery.getDropQuery();
            String createTaskTableSql = createTaskTableQuery.toString();
            logger.debug("SQL query: " + createTaskTableSql);

            try {
                db.create(createTaskTableSql);
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

            // Task dependencies table create query
            logger.debug("Building SQL query for creating " + DEPENDENCIES_TABLE_NAME + " table");
            CreateTableQuery createTaskDependenciesTableQuery = new CreateTableQuery(taskDependenciesTable, true)
                    .addCustomization(MysObjects.IF_NOT_EXISTS_TABLE)
                    .validate();
            dropTaskTableQuery = createTaskDependenciesTableQuery.getDropQuery();
            String createTaskDependenciesTableSql = createTaskDependenciesTableQuery.toString();
            logger.debug("SQL query: " + createTaskDependenciesTableSql);

            try {
                db.create(createTaskDependenciesTableSql);
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

        @Override
        public void dropTable() throws DBException {
            try {
                db.create(dropTaskTableQuery.validate().toString());
                db.create(dropTaskDependenciesTableQuery.validate().toString());
            } catch (SQLException e) {
                logger.error("Unable to drop table "+ getTableName(), e);
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
