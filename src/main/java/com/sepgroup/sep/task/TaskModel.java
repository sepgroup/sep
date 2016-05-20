package com.sepgroup.sep.task;

import com.sepgroup.sep.AbstractModel;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.DBObject;
import com.sepgroup.sep.db.SQLiteDB;
import com.sepgroup.sep.login.UserModel;
import com.sepgroup.sep.project.ProjectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public TaskModel() {
        dbo = new TaskModelDBObject();
        dependencies = new LinkedList<>();
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

    public List<TaskModel> getDependencies() {
        return dependencies;
    }

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
        if (taskId != 0) {
            dbo.update();
        }
        else {
            int taskId = dbo.create();
            setTaskId(taskId);
        }
        updateObservers();
    }

    @Override
    public void deleteData() throws DBException {
        dbo.delete();

    }

    public static List<TaskModel> getAll() {
        // TODO
        return null;
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

    public static List<TaskModel> getAllByProject(ProjectModel p) {
        List<TaskModel> l = new ArrayList<>();
        try {
            TaskModel t;
            ResultSet r = t.dbo.findBySql();
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
                return t;
            }
            else {
                logger.error("Unable to find TaskModel with ID " + taskId);
            }
        } catch (DBException | SQLException e) {
            logger.error("Unable to find TaskModel with ID " + taskId, e);
        }

        return null;
    }

    public static List<TaskModel> getAllByUser(UserModel u) {
        // TODO
        return null;
    }

    class TaskModelDBObject implements DBObject {

        private final Logger logger = LoggerFactory.getLogger(TaskModelDBObject.class);

        private static final String tableName = "Task";

        private static final String TASK_ID = "TID";
        private static final String PROJECT_ID = "PID";
        private static final String TASK_DESCRIPTION = "Description";
        private static final String TASK_NAME = "TaskName";

        private SQLiteDB db = SQLiteDB.getActiveDB();

        @Override
        public String getTableName() {
            return tableName;
        }

        @Override
        public int getLastInsertedId() {
            // TODO
            return 0;
        }

        @Override
        public ResultSet findById(int taskId) throws DBException {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + " ");
            sql.append("WHERE TID=" + taskId);
            try {
                return db.query(sql.toString());
            }
            catch (SQLException e) {
                throw new DBException(e);
            }
        }

        @Override
        public ResultSet findBySql(String sql) throws DBException {
            try {
                return db.query(sql);
            } catch (SQLException e) {
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

            try {
                return db.insert(sql.toString());
            } catch (SQLException e) {
                throw new DBException(e);
            }
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
                throw new DBException(e);
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
                throw new DBException(e);
            }
        }
    }

}
