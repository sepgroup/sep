package com.sepgroup.sep.task;

import com.sepgroup.sep.AbstractModel;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.DBObject;
import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.utils.DateUtils;
import com.sun.javafx.tk.Toolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    private String description;
    private int projectId;
    private List<TaskModel> dependencies;

//    public int budget;
//    private Date startDate;
//    private Date deadline;

    /**
     * Default constructor
     */
    public TaskModel() {
        dbo = new TaskModelDBObject();
        dependencies = new LinkedList<>();
    }

    public TaskModel(String name, String description, int projectId) {
        this();
        this.name = name;
        this.description = description;
        this.projectId = projectId;
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
    public void refreshData() throws DBException {
        TaskModel refreshed = getById(getTaskId());

        setName(refreshed.getName());
        setDescription(refreshed.getDescription());
        setProjectId(refreshed.getProjectId());

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

    public static List<TaskModel> getAll() {
        List<TaskModel> taskList = new LinkedList<>();
        try {
            ResultSet rs = new TaskModel().dbo.findAll();
            while (rs.next()) {
                // Extract values
                String taskName = rs.getString("TaskName");
                String taskDescription = rs.getString("Description");
                int taskId = rs.getInt("TID");
                int projectId = rs.getInt("PID");

                // Assign values
                TaskModel tt = new TaskModel();
                tt.setTaskId(taskId);
                tt.setName(taskName);
                tt.setDescription(taskDescription);
                tt.setProjectId(projectId);
                taskList.add(tt);
            }
        } catch (DBException | SQLException e) {
            logger.error("Unable to find TaskModel(s)", e);
        }

        return taskList;
    }

    public static TaskModel getById(int taskId) {
        TaskModel t = null;
        try {
            ResultSet r = t.dbo.findById(taskId);
            if (r.next()) {
                // Extract values
                String taskName = r.getString("TaskName");
                String taskDescription = r.getString("Description");
                int projectId = r.getInt("PID");

                // Assign values
                t.setTaskId(taskId);
                t.setName(taskName);
                t.setDescription(taskDescription);
                t.setProjectId(projectId);
                // TODO set dependencies
                return t;
            }
            else {
                logger.error("Unable to find TaskModel with ID " + taskId);
            }
        } catch (DBException | SQLException e) {
            logger.error("Unable to find TaskModel with ID " + taskId, e);
        }
        return t;
    }

    public static List<TaskModel> getAllByProject(int projectId) {
        List<TaskModel> taskList = new LinkedList<>();
        try {
            ResultSet r = new TaskModel().dbo.findByProjectId(projectId);
            while (r.next()) {
                // Extract values
                String taskName = r.getString("TaskName");
                String taskDescription = r.getString("Description");
                int taskId = r.getInt("TID");

                // Assign values
                TaskModel tt = new TaskModel();
                tt.setTaskId(taskId);
                tt.setName(taskName);
                tt.setDescription(taskDescription);
                tt.setProjectId(projectId);
                taskList.add(tt);
                // TODO set dependencies
            }
        } catch (DBException | SQLException e) {
            logger.error("Unable to find TaskModel with project ID " + projectId, e);
        }

        return taskList;
    }

    public static List<TaskModel> getAllByUser(int userId) {
        List<TaskModel> taskList = new LinkedList<>();

        // Build query
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ...");
        // TODO

        try {
            ResultSet r = new TaskModel().dbo.findBySql(sql.toString());
            while (r.next()) {
                // Extract values
                String taskName = r.getString("TaskName");
                String taskDescription = r.getString("Description");
                int taskId = r.getInt("TID");
                int projectId = r.getInt("PID");

                // Assign values
                TaskModel tt = new TaskModel();
                tt.setTaskId(taskId);
                tt.setName(taskName);
                tt.setDescription(taskDescription);
                tt.setProjectId(projectId);
//                tt.setUserId(userId);  // TODO
                taskList.add(tt);
                // TODO set dependencies
            }
        } catch (DBException | SQLException e) {
            logger.error("Unable to find TaskModel(s) with user ID " + userId, e);
        }

        return taskList;
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
        if (other.getDescription() != null && description != null && !other.getDescription().equals(description)) {
            return false;
        }
        if (other.getDependencies() != null && dependencies != null && !other.getDependencies().equals(dependencies)) {
            return false;
        }

        return true;
    }

    class TaskModelDBObject implements DBObject {

        private final Logger logger = LoggerFactory.getLogger(TaskModelDBObject.class);

        private static final String tableName = "Task";

        private static final String TASK_ID = "TID";
        private static final String PROJECT_ID = "PID";
        private static final String TASK_DESCRIPTION = "Description";
        private static final String TASK_NAME = "TaskName";

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
            return tableName;
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
        public ResultSet findAll() throws DBException {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + ";");
            try {
                return db.query(sql.toString());
            }
            catch (SQLException e) {
                logger.error("Unable to fetch all entries in Task table" + ". Query: " + sql, e);
                throw new DBException(e);
            }
        }

        @Override
        public ResultSet findById(int taskId) throws DBException {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + " ");
            sql.append("WHERE TID=" + taskId + ";");
            try {
                return db.query(sql.toString());
            }
            catch (SQLException e) {
                logger.error("Unable to fetch task with taskId " + taskId + ". Query: " + sql, e);
                throw new DBException(e);
            }
        }

        public ResultSet findByProjectId(int projectId) throws DBException {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + " ");
            sql.append("WHERE PID=" + projectId + ";");
            try {
                return db.query(sql.toString());
            }
            catch (SQLException e) {
                logger.error("Unable to fetch all tasks with project projectId" + projectId + ". Query: " + sql, e);
                throw new DBException(e);
            }
        }

        @Override
        public ResultSet findBySql(String sql) throws DBException {
            try {
                return db.query(sql);
            } catch (SQLException e) {
                logger.error("Unable to fetch using query" + sql, e);
                throw new DBException(e);
            }
        }

        @Override
        public int create() throws DBException {
            // Build query
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO "+ getTableName() + " ");
            sql.append("("+ TASK_NAME + "," + TASK_DESCRIPTION + "," + PROJECT_ID + ") ");
            sql.append("VALUES ('" + getName() + "'");
            sql.append(",'" + getDescription() + "'");
            sql.append(",'" + getProjectId() + "'");
            sql.append(");");

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
            sql.append(TASK_NAME + "='" + getName() + "'");
            sql.append(", " + TASK_DESCRIPTION + "='" + getDescription() + "' ");
            sql.append("WHERE " + TASK_ID + "=" + getTaskId() + ";");

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
            sql.append("WHERE " + TASK_ID + "=" + getTaskId() + ";");

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
