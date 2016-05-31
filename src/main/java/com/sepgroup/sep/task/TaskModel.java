package com.sepgroup.sep.task;

import com.sepgroup.sep.AbstractModel;
import com.sepgroup.sep.ModelNotFoundException;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.DBObject;
import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.project.ProjectModel;
import com.sepgroup.sep.user.UserModel;
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
    private int assigneeUserId;
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

    public TaskModel(String name, String description, int projectId) {
        this();
        this.name = name;
        this.description = description;
        this.projectId = projectId;
    }

    public TaskModel(String name, String description, int projectId, double budget, Date startDate, Date deadline,
            boolean done, int assigneeUserId) {
        this(name, description, projectId);
        this.budget = budget;
        if (startDate != null) this.startDate = startDate;
        if (deadline != null) this.deadline = deadline;
        this.done = done;
        this.assigneeUserId = assigneeUserId;
    }

    public TaskModel(String name, String description, int projectId, double budget, Date startDate, Date deadline,
            boolean done, int assigneeUserId, List<String> tags) {
        this(name, description, projectId, budget, startDate, deadline, done, assigneeUserId);
        if (tags != null) this.tags = tags;
    }

    public TaskModel(int taskId, String name, String description, int projectId, double budget, Date startDate,
            Date deadline, boolean done, int assigneeUserId, List<String> tags) {
        this(name, description, projectId, budget, startDate, deadline, done, assigneeUserId, tags);
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

    /**
     *
     * @param task
     * @return
     */
    public boolean addDependency(TaskModel task) {
        if (dependencies.stream().noneMatch((t) -> t.equals(task))) {
            dependencies.add(task);
            return true;
        }
        return false;
    }

    public boolean removeDependency(TaskModel task) {
        return dependencies.remove(task);
    }

    @Override
    public void refreshData() throws ModelNotFoundException {
        TaskModel refreshed = getById(getTaskId());

        setTaskId(refreshed.getTaskId());
        setName(refreshed.getName());
        setDescription(refreshed.getDescription());
        setProjectId(refreshed.getProjectId());
        setBudget(refreshed.getBudget());
        setStartDate(refreshed.getStartDate());
        setDeadline(refreshed.getDeadline());
        setDone(refreshed.isDone());
        setAssigneeUserId(refreshed.getAssigneeUserId());
        setTags(refreshed.getTags());

        updateObservers();
    }

    @Override
    public void persistData() throws DBException {
        if (getName() == null || getName() == "" || getProjectId() == 0) {
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
            setTaskId(taskId);
        }
        updateObservers();
    }

    @Override
    public void deleteData() throws DBException {
        this.dbo.delete();

    }

    public static List<TaskModel> getAll() throws ModelNotFoundException {
        return new TaskModel().dbo.findAll();
    }

    public static TaskModel getById(int taskId) throws ModelNotFoundException {
        return new TaskModel().dbo.findById(taskId);
    }

    public static List<TaskModel> getAllByProject(int projectId) throws ModelNotFoundException {
        return new TaskModel().dbo.findAllByProject(projectId);
    }

    public static List<TaskModel> getAllByProject(ProjectModel project) throws ModelNotFoundException {
        return getAllByProject(project.getProjectId());
    }

    public static List<TaskModel> getAllByAssignee(int userId) throws ModelNotFoundException {
        return new TaskModel().dbo.findAllByAssignee(userId);
    }

    public static List<TaskModel> getAllByAssignee(UserModel assignee) throws ModelNotFoundException {
        return getAllByAssignee(assignee.getUserId());
    }


    public int getTaskId() {
        return taskId;
    }

    private void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getProjectId() {
        return projectId;
    }

    private void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getAssigneeUserId() {
        return assigneeUserId;
    }

    public void setAssigneeUserId(int assigneeUserId) {
        this.assigneeUserId = assigneeUserId;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTagsString() {
        return tags.stream().collect(Collectors.joining(" "));
    }

    public boolean addTag(String tag) {
        if (tags.stream().noneMatch(t -> t.equals(tag))) {
            tags.add(tag);
            return true;
        }
        return false;
    }

    public void addTagsFromString(String tagsString) {
        getTagsListFromString(tagsString).forEach(t -> addTag(t));
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    @Override
    public String toString() {
        // TODO
        return "Task TID = " + this.taskId + ", name = " + this.name + " , start date = ";
//                DateUtils.castDateToString(startDate) + ", end date = " + DateUtils.castDateToString(deadline);
    }

    @Override
    public boolean equals(Object obj) {
        // TODO fixed upstream
        if (!(obj instanceof TaskModel)) {
            return false;
        }
        TaskModel other = (TaskModel) obj;
        if (other.getProjectId() != projectId) {
            return false;
        }
        if (other.getName() != null && name != null && !other.getName().equals(name)) {
            return false;
        }
        if (other.getTaskId() != taskId) {
            return false;
        }
        if (other.getDependencies() != null && dependencies != null && !other.getDependencies().equals(dependencies)) {
            return false;
        }

        return true;
    }

    class TaskModelDBObject implements DBObject {

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
         * @throws SQLException if there is an error
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
                    double budgetTemp = rs.getInt(BUDGET_COLUMN);
                    boolean doneTemp = rs.getBoolean(DONE_COLUMN);
                    int userIdTemp = rs.getInt(ASSIGNEE_USER_ID_COLUMN);
                    String tagsTemp = rs.getString(TAGS_COLUMN);
                    List<String> tagsListTemp = null;
                    if (tagsTemp != null) {
                        tagsListTemp = getTagsListFromString(tagsTemp);
                    }
                    else {
                        tagsListTemp = new LinkedList<>();
                    }
                    m = new TaskModel(idTemp, nameTemp, descriptionTemp, projectIdTemp, budgetTemp, stDateTempDate,
                            dlDateTempDate, doneTemp, userIdTemp, tagsListTemp);
                }
                else {
                    logger.info("DB query returned zero results");
                    throw new ModelNotFoundException("DB query for task with ID " + taskId + " returned no results");
                }
            }
            catch (SQLException e) {
                logger.error("Unable to fetch all entries in Project table" + ". Query: " + sql, e);
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
                        throw new ModelNotFoundException("Unable to parse Date from DB, this really shouldn't happen.", e);
                    }
                    double budgetTemp = rs.getInt(BUDGET_COLUMN);
                    boolean doneTemp = rs.getBoolean(DONE_COLUMN);
                    int userIdTemp = rs.getInt(ASSIGNEE_USER_ID_COLUMN);
                    String tagsTemp = rs.getString(TAGS_COLUMN);
                    List<String> tagsListTemp;
                    if (tagsTemp != null) {
                        tagsListTemp = getTagsListFromString(tagsTemp);
                    }
                    else {
                        tagsListTemp = new LinkedList<>();
                    }
                    taskList.add(new TaskModel(idTemp, nameTemp, descriptionTemp, projectIdTemp, budgetTemp, stDateTempDate,
                            dlDateTempDate, doneTemp, userIdTemp, tagsListTemp));
                }

                if (taskList.isEmpty()) {
                    logger.info("DB query returned zero results");
                    throw new ModelNotFoundException("DB query for all projects returned no results");
                }
            }
            catch (SQLException e) {
                logger.error("Unable to fetch all entries in Project table" + ". Query: " + sql, e);
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
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + ";");

            return runMultiResultQuery(sql.toString());
        }

        @Override
        public TaskModel findById(int taskId) throws ModelNotFoundException {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + " ");
            sql.append("WHERE " + TASK_ID_COLUMN + "=" + taskId + ";");
            logger.debug("Query: " + sql.toString());

            return runSingleResultQuery(sql.toString());
        }

        public List<TaskModel> findAllByAssignee(int assigneeUserId) throws ModelNotFoundException  {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + " ");
            sql.append("WHERE " + ASSIGNEE_USER_ID_COLUMN + "=" + assigneeUserId + ";");

            return runMultiResultQuery(sql.toString());
        }

        public List<TaskModel> findAllByProject(int projectId) throws ModelNotFoundException {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + " ");
            sql.append("WHERE " + PROJECT_ID_COLUMN + "=" + projectId + ";");

            return runMultiResultQuery(sql.toString());
        }

        @Override
        public List<TaskModel> findBySql(String sql) throws ModelNotFoundException {
            return runMultiResultQuery(sql);
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
            sql.append("," + ASSIGNEE_USER_ID_COLUMN);
            if (getTags().size() > 0) sql.append("," + TAGS_COLUMN);
            sql.append(") ");

            sql.append("VALUES ('" + getName() + "'");
            if (getDescription() != null) sql.append(",'" + getDescription() + "'");
            sql.append(",'" + getProjectId() + "'");
            sql.append(",'" + getBudget() + "'");
            if (getStartDate() != null) sql.append(",'" + DateUtils.castDateToString(getStartDate()) + "'");
            if (getDeadline() != null) sql.append(",'" + DateUtils.castDateToString(getDeadline()) + "'");
            sql.append(",'" + (isDone() ? 1 : 0) + "'");
            sql.append(",'" + getAssigneeUserId() + "'");
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

            // TODO dependencies

            return insertedKey;
        }

        @Override
        public void update() throws DBException {
            // Build query
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE "+ getTableName() + " ");
            sql.append("SET ");
            sql.append(TASK_NAME_COLUMN + "='" + getName() + "'");
            if (getDescription() != null) sql.append(", " + DESCRIPTION_COLUMN + "='" + getDescription() + "' ");
            sql.append(", " + PROJECT_ID_COLUMN + "=" + getProjectId() + " ");
            sql.append(", " + BUDGET_COLUMN + "=" + getBudget() + " ");
            if (getStartDate() != null) sql.append(", " + START_DATE_COLUMN + "='" + DateUtils.castDateToString(getStartDate()) + "' ");
            if (getDeadline() != null) sql.append(", " + DEADLINE_COLUMN + "='" + DateUtils.castDateToString(getDeadline()) + "' ");
            sql.append(", " + DONE_COLUMN + "=" + (isDone() ? 1 : 0) + " ");
            sql.append(", " + ASSIGNEE_USER_ID_COLUMN + "=" + getAssigneeUserId() + " ");
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

            // TODO dependencies
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

            // TODO dependencies
        }
    }

}
