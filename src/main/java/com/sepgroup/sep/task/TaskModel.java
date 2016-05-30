package com.sepgroup.sep.task;

import com.sepgroup.sep.AbstractModel;
import com.sepgroup.sep.ModelNotFoundException;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.DBObject;
import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.utils.DateUtils;
import com.sun.javafx.tk.Toolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jeremybrown on 2016-05-18.
 */
public class TaskModel extends AbstractModel {

    private static Logger logger = LoggerFactory.getLogger(TaskModel.class);

    private TaskModelDBObject dbo;

    private int taskId;
    private String name;
//    private String description;
    private int projectId;
    private double budget;
    private Date startDate;
    private Date deadline;
    private boolean done;
    private int userId;
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

    public TaskModel(String name, int projectId) {
        this();
        this.name = name;
//        this.description = description;
        this.projectId = projectId;
    }

    public TaskModel(String name, int projectId, double budget, Date startDate, Date deadline, boolean done, int userId) {
        this();
        this.name = name;
//        this.description = description;
        this.projectId = projectId;
        this.budget = budget;
        this.startDate = startDate;
        this.deadline = deadline;
        this.done = done;
        this.userId = userId;
    }

    public TaskModel(int taskId, String name, int projectId, double budget, Date startDate, Date deadline, boolean done, int userId) {
        this();
        this.taskId = taskId;
        this.name = name;
//        this.description = description;
        this.projectId = projectId;
        this.budget = budget;
        this.startDate = startDate;
        this.deadline = deadline;
        this.done = done;
        this.userId = userId;
    }

    /**
     *
     * @return
     */
    public List<TaskModel> getDependencies() {
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

        setName(refreshed.getName());
//        setDescription(refreshed.getDescription());
        setProjectId(refreshed.getProjectId());
        // TODO

        updateObservers();
    }

    @Override
    public void persistData() throws DBException {
        if (this.taskId != 0) {
            this.dbo.update();
        }
        else {
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

    public static List<TaskModel> getAllByProject(int projectId) {
        // TODO
//        List<TaskModel> taskList = new LinkedList<>();
//        try {
//            ResultSet r = new TaskModel().dbo.findByProjectId(projectId);
//            while (r.next()) {
//                // Extract values
//                String taskName = r.getString("TaskName");
//                String taskDescription = r.getString("Description");
//                int taskId = r.getInt("TID");
//
//                // Assign values
//                TaskModel tt = new TaskModel();
//                tt.setTaskId(taskId);
//                tt.setName(taskName);
//                tt.setDescription(taskDescription);
//                tt.setProjectId(projectId);
//                taskList.add(tt);
//                // TODO set dependencies
//            }
//        } catch (DBException | SQLException e) {
//            logger.error("Unable to find TaskModel with project ID " + projectId, e);
//        }
//
//        return taskList;
        return null;
    }

    public static List<TaskModel> getAllByUser(int userId) {
        // TODO
//        List<TaskModel> taskList = new LinkedList<>();
//
//        // Build query
//        StringBuilder sql = new StringBuilder();
//        sql.append("SELECT ...");
//        // TODO
//
//        try {
//            ResultSet r = new TaskModel().dbo.findBySql(sql.toString());
//            while (r.next()) {
//                // Extract values
//                String taskName = r.getString("TaskName");
//                String taskDescription = r.getString("Description");
//                int taskId = r.getInt("TID");
//                int projectId = r.getInt("PID");
//
//                // Assign values
//                TaskModel tt = new TaskModel();
//                tt.setTaskId(taskId);
//                tt.setName(taskName);
//                tt.setDescription(taskDescription);
//                tt.setProjectId(projectId);
////                tt.setUserId(userId);  // TODO
//                taskList.add(tt);
//                // TODO set dependencies
//            }
//        } catch (DBException | SQLException e) {
//            logger.error("Unable to find TaskModel(s) with user ID " + userId, e);
//        }
//
//        return taskList;
        return null;
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

//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }

    public int getProjectId() {
        return projectId;
    }

    private void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean addTag(String tag) {
        if (tags.stream().noneMatch((t) -> t.equals(tag))) {
            tags.add(tag);
            return true;
        }
        return false;
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    @Override
    public String toString() {
        return "Task TID = " + this.taskId + ", name = " + this.name + " , start date = ";
//                DateUtils.castDateToString(startDate) + ", end date = " + DateUtils.castDateToString(deadline);
    }

    @Override
    public boolean equals(Object obj) {
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
//        if (other.getDescription() != null && description != null && !other.getDescription().equals(description)) {
//            return false;
//        }
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
//        public static final String DESCRIPTION_COLUMN = "Description";
        public static final String START_DATE_COLUMN = "StartDate";
        public static final String DEADLINE_COLUMN = "Deadline";
        public static final String BUDGET_COLUMN = "Budget";
        public static final String DONE_COLUMN = "Done";
        public static final String TAGS_COLUMN = "Tags";
        public static final String USER_ID_COLUMN = "FKUserID";

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

        @Override
        public List<TaskModel> findAll() throws ModelNotFoundException  {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + ";");

            List<TaskModel> taskList = new LinkedList<>();
            try {
                ResultSet rs =  db.query(sql.toString());

                while (rs.next()) {
                    // Extract values
                    int idTemp = rs.getInt(TaskModelDBObject.TASK_ID_COLUMN);
                    String nameTemp = rs.getString(TaskModelDBObject.TASK_NAME_COLUMN);
                    int projectIdTemp = rs.getInt(TaskModelDBObject.PROJECT_ID_COLUMN);
                    String stDateTemp = rs.getString(TaskModelDBObject.START_DATE_COLUMN);
                    String dlDateTemp = rs.getString(TaskModelDBObject.DEADLINE_COLUMN);
                    Date stDateTempDate;
                    Date dlDateTempDate;
                    try {
                        stDateTempDate = DateUtils.castStringToDate(stDateTemp);
                        dlDateTempDate = DateUtils.castStringToDate(dlDateTemp);
                    } catch (ParseException e) {
                        logger.error("Unable to parse Date from DB");
                        throw new ModelNotFoundException(e);
                    }
                    double budgetTemp = rs.getInt(TaskModelDBObject.BUDGET_COLUMN);
                    boolean doneTemp = rs.getBoolean(TaskModelDBObject.DONE_COLUMN);
                    int userIdTemp = rs.getInt(TaskModelDBObject.USER_ID_COLUMN);
                    String tagsTemp = rs.getString(TaskModelDBObject.TAGS_COLUMN);
                    List<String> tagsList = Arrays.asList(tagsTemp.split(" "));
                    taskList.add(new TaskModel(idTemp, nameTemp, projectIdTemp, budgetTemp, stDateTempDate, dlDateTempDate, doneTemp, userIdTemp));
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
        public TaskModel findById(int taskId) throws ModelNotFoundException {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + ";");
            sql.append("WHERE " + TASK_ID_COLUMN + "=" + taskId + ";");
            logger.debug("Query: " + sql.toString());

            TaskModel m = null;
            try {
                ResultSet rs =  db.query(sql.toString());

                if (rs.next()) {
                    // Extract values
                    int idTemp = rs.getInt(TaskModelDBObject.TASK_ID_COLUMN);
                    String nameTemp = rs.getString(TaskModelDBObject.TASK_NAME_COLUMN);
                    int projectIdTemp = rs.getInt(TaskModelDBObject.PROJECT_ID_COLUMN);
                    String stDateTemp = rs.getString(TaskModelDBObject.START_DATE_COLUMN);
                    String dlDateTemp = rs.getString(TaskModelDBObject.DEADLINE_COLUMN);
                    Date stDateTempDate;
                    Date dlDateTempDate;
                    try {
                        stDateTempDate = DateUtils.castStringToDate(stDateTemp);
                        dlDateTempDate = DateUtils.castStringToDate(dlDateTemp);
                    } catch (ParseException e) {
                        logger.error("Unable to parse Date from DB");
                        throw new ModelNotFoundException(e);
                    }
                    int budgetTemp = rs.getInt(TaskModelDBObject.BUDGET_COLUMN);
                    boolean doneTemp = rs.getBoolean(TaskModelDBObject.DONE_COLUMN);
                    int userIdTemp = rs.getInt(TaskModelDBObject.USER_ID_COLUMN);
                    String tagsTemp = rs.getString(TaskModelDBObject.TAGS_COLUMN);
                    List<String> tagsList = Arrays.asList(tagsTemp.split(" "));
                    m = new TaskModel(idTemp, nameTemp, projectIdTemp, budgetTemp, stDateTempDate, dlDateTempDate, doneTemp, userIdTemp);
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

        public TaskModel findByProjectId(int projectId) throws ModelNotFoundException {
            // TODO
//            StringBuilder sql = new StringBuilder();
//            sql.append("SELECT * ");
//            sql.append("FROM " + getTableName() + " ");
//            sql.append("WHERE "+ PROJECT_ID_COLUMN +"=" + projectId + ";");
//            try {
//                return db.query(sql.toString());
//            }
//            catch (SQLException e) {
//                logger.error("Unable to fetch all tasks with project projectId" + projectId + ". Query: " + sql, e);
//                throw new DBException(e);
//            }
            return null;
        }

        @Override
        public List<TaskModel> findBySql(String sql) throws ModelNotFoundException {
            // TODO
//            try {
//                return db.query(sql);
//            } catch (SQLException e) {
//                logger.error("Unable to fetch using query" + sql, e);
//                throw new DBException(e);
//            }
            return null;
        }

        @Override
        public int create() throws DBException {
            // Build query
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO "+ getTableName() + " ");
//            sql.append("("+ TASK_NAME_COLUMN + "," + DESCRIPTION_COLUMN + "," + PROJECT_ID_COLUMN + ") ");
            sql.append("VALUES ('" + getName() + "'");
//            sql.append(",'" + getDescription() + "'");
            sql.append(",'" + getProjectId() + "'");
            sql.append(");");
            // TODO dependencies
            // TODO tags

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

            return insertedKey;
        }

        @Override
        public void update() throws DBException {
            // Build query
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE "+ getTableName() + " ");
            sql.append("SET ");
            sql.append(TASK_NAME_COLUMN + "='" + getName() + "'");
//            sql.append(", " + DESCRIPTION_COLUMN + "='" + getDescription() + "' ");
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
                throw new DBException(e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    throw new DBException(e);
                }
            }
        }
    }

}
