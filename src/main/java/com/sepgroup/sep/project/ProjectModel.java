package com.sepgroup.sep.project;

import com.sepgroup.sep.AbstractModel;
import com.sepgroup.sep.ModelNotFoundException;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.DBObject;
import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.user.UserModel;
import com.sepgroup.sep.task.TaskModel;
import com.sepgroup.sep.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ProjectModel extends AbstractModel {

    private static Logger logger = LoggerFactory.getLogger(ProjectModel.class);

    private ProjectModelDBObject dbo;

    private int projectId;
    private String name;
    private int budget;
    private Date startDate;
    private Date deadline;
    private boolean done;

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
	public ProjectModel(String name, String sd, String dl, int budget, boolean done){
        this();
		this.name=name;
        this.budget=budget;
        try {
            if (sd != null) this.startDate = DateUtils.castStringToDate(sd);
            if (dl != null) this.deadline = DateUtils.castStringToDate(dl);
        } catch (ParseException e) {
            logger.error("Unable to parse string to date, leaving as null.", e);
        }
		this.done =done;
	}
	/**
	 * This constructor is for internal use to fetch data from database
	 * this is private because we do not want the user assigned id to the project
	 * @param id the unique value for project
	 * @param name Name of project
	 * @param sd Start date of project
	 * @param dl Deadline of project
	 * @param budget Dedicated Budget to the project
	 */
	private ProjectModel(int id, String name, String sd, String dl, int budget, boolean done){
        this();
		this.projectId = id;
		this.name = name;
		this.budget = budget;
        try {
            if (sd != null) this.startDate = DateUtils.castStringToDate(sd);
            if (dl != null) this.deadline = DateUtils.castStringToDate(dl);
        } catch (ParseException e) {
            logger.error("Unable to parse string to date, leaving as null.", e);
        }
		this.done = done;
	}

    @Override
    public void refreshData() throws ModelNotFoundException {
        ProjectModel refreshed = getById(getProjectId());

        setName(refreshed.getName());
//        setDescription(refreshed.getDescription());
        setProjectId(refreshed.getProjectId());
        setBudget(refreshed.getBudget());
        setStartDate(refreshed.getStartDate());
        setDeadline(refreshed.getDeadline());
        setDone(refreshed.isDone());

        updateObservers();
    }

    @Override
    public void persistData() throws DBException {
        if (this.projectId != 0) {
            this.dbo.update();
        }
        else {
            int projectId = this.dbo.create();
            setProjectId(projectId);
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

    public static List<ProjectModel> getAllByOwner(UserModel userModel) {
        return getAllByOwner(userModel.getUserId());
    }

    public static List<ProjectModel> getAllByOwner(int userId) {
        // TODO
        logger.error("Not implemented");
        return null;
    }

    /**
     *
     * @param projectId
     */
    private void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getProjectId() {
        return this.projectId;
    }

    /**
	 * Setter for name of project which is used in update method
	 * @param name updated name of project
	 */
	public void setName(String name){
		this.name=name;
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
	 * @param budget updated budget of project
	 */
	public void setBudget(int budget){
		this.budget=budget;
	}

    /**
     *
     * @return
     */
    public int getBudget() {
        return this.budget;
    }

	/**
	 * Setter for Start date of project which is used in update method
	 * @param date updated date of project
	 */
	public void setStartDate(String date) throws ParseException {
		this.startDate = DateUtils.castStringToDate(date);
	}

    /**
     * Setter for Start date of project which is used in update method
     * @param date updated date of project
     */
    public void setStartDate(Date date){
        this.startDate = date;
    }

    /**
     *
     * @return
     */
    public Date getStartDate() {
        return this.startDate;
    }

    /**
	 * Setter for Dead Line of project which is used in update method
	 * @param deadline updated date of deadline
	 */
	public void setDeadline(Date deadline){
		this.deadline = deadline;
	}

    /**
     * Setter for Dead Line of project which is used in update method
     * @param deadline updated date of deadline
     */
    public void setDeadline(String deadline) throws ParseException {
        this.deadline = DateUtils.castStringToDate(deadline);
    }

    /**
     *
     * @return
     */
    public Date getDeadline() {
        return this.deadline;
    }

	/**
	 * Setter for done attribute of project which is used in update method
	 * @param done updated status of project
	 */
	public void setDone(boolean done){
		this.done =done;
	}

    /**
     *
     * @return
     */
    public boolean isDone() {
        return this.done;
    }

    /**
     * Get a list of tasks associated to this project
     * @return a list of tasks associated to this project
     */
    public List<TaskModel> getTasks() {
        return TaskModel.getAllByProject(projectId);
    }

    @Override
	public String toString(){
		return ("Project info is "+ this.projectId + ", " + " Project name is " + this.name + ", ");
//                DateUtils.castDateToString(startDate) + ", " + DateUtils.castDateToString(deadline)+", " + this.budget);
	}

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProjectModel)) {
            return false;
        }
        ProjectModel other = (ProjectModel) obj;
        if (other.getProjectId() != this.projectId) {
            return false;
        }
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
        if (other.getTasks() != null && getTasks() != null && other.getTasks().equals(this.getTasks())) {
            return false;
        }

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
        public static final String TABLE_NAME = "Project";

        private Database db;

        private ProjectModelDBObject() {
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
            String sql = "SELECT last_insert_rowid() AS tempID;";
            int id;
            try {
                ResultSet rs = db.query(sql);
                id = (rs.getInt("tempID"));
            } catch (SQLException e) {
                logger.error("Unable to find last inserted id" + ". Query: " + sql, e);
                throw new DBException(e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    throw new DBException("Unable to close connection to " + db.getDbPath(), e);
                }
            }
            return id;
        }

        @Override
        public List<ProjectModel> findAll() throws ModelNotFoundException {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + ";");

            List<ProjectModel> projectList = new LinkedList<>();
            try {
                ResultSet rs =  db.query(sql.toString());

                while (rs.next()) {
                    // Extract values
                    int idTemp = rs.getInt(ProjectModelDBObject.PROJECT_ID_COLUMN);
                    String pNameTemp = rs.getString(ProjectModelDBObject.PROJECT_NAME_COLUMN);
                    String stDateTemp=rs.getString(ProjectModelDBObject.START_DATE_COLUMN);
                    String dlDateTemp=rs.getString(ProjectModelDBObject.DEADLINE_COLUMN);
                    int budgetTemp = rs.getInt(ProjectModelDBObject.BUDGET_COLUMN);
                    boolean doneTemp=rs.getBoolean(ProjectModelDBObject.DONE_COLUMN);
                    projectList.add(new ProjectModel(idTemp, pNameTemp, stDateTemp, dlDateTemp, budgetTemp, doneTemp));
                }

                if (projectList.isEmpty()) {
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
            return projectList;
        }

        @Override
        public ProjectModel findById(int projectId) throws ModelNotFoundException {
            logger.debug("Building query for project ID " + projectId);
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + " ");
            sql.append("WHERE " + PROJECT_ID_COLUMN + "=" + projectId + ";");
            logger.debug("Query: " + sql.toString());

            ProjectModel p = null;
            try {
                ResultSet rs = db.query(sql.toString());
                if (rs.next()) {
                    logger.debug("Constructing ProjectModel with DB fields");
                    String pNameTemp = rs.getString(ProjectModelDBObject.PROJECT_NAME_COLUMN);
                    String stDateTemp=rs.getString(ProjectModelDBObject.START_DATE_COLUMN);
                    String dlDateTemp=rs.getString(ProjectModelDBObject.DEADLINE_COLUMN);
                    int budgetTemp = rs.getInt(ProjectModelDBObject.BUDGET_COLUMN);
                    boolean doneTemp=rs.getBoolean(ProjectModelDBObject.DONE_COLUMN);
                    p = new ProjectModel(projectId, pNameTemp, stDateTemp, dlDateTemp, budgetTemp, doneTemp);
                }
                else {
                    logger.info("DB query returned zero results");
                    throw new ModelNotFoundException("DB query for project ID " + projectId + " returned no results");
                }
            }
            catch (SQLException e) {
                logger.error("Unable to fetch all tasks with project projectId" + projectId + ". Query: " + sql, e);
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
         * It fetches the data from database and make one linked list of project objects
         * @param sql asked sql statement from database
         * @return Linklist of project objects
         */
        @Override
        public List<AbstractModel> findBySql(String sql) throws ModelNotFoundException {
            // TODO
            return null;
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
            if (getStartDate() != null) {
                sql.append(", " + START_DATE_COLUMN);
            }
            if (getDeadline() != null) {
                sql.append(", " + DEADLINE_COLUMN);
            }
            sql.append(") ");
            sql.append("VALUES ('" + getName() + "'");
            sql.append(",'" + getBudget() + "'");
            sql.append(",'" + (isDone() ? 1 : 0) + "'");
            if (getStartDate() != null) {
                sql.append(",'" + DateUtils.castDateToString(getStartDate()) + "'");
            }
            if (getDeadline() != null) {
                sql.append(",'" + DateUtils.castDateToString(getDeadline()) + "'");
            }
            sql.append(");");
            logger.debug("SQL query: " + sql.toString());

            int insertedKey;
            try {
                insertedKey = this.db.insert(sql.toString());
                logger.debug("Insert query returned key " + insertedKey);
            } catch (SQLException e) {
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
            sql.append(PROJECT_NAME_COLUMN + "='" + getName() + "'");
            sql.append(START_DATE_COLUMN + "='" + DateUtils.castDateToString(getStartDate()) + "'");
            sql.append(DEADLINE_COLUMN + "='" + DateUtils.castDateToString(getDeadline()) + "'");
            sql.append(BUDGET_COLUMN + "='" + getBudget() + "'");
            sql.append(DONE_COLUMN + "='" + (isDone() ? 1 : 0) + "'");
            sql.append("WHERE " + PROJECT_ID_COLUMN + "=" + getProjectId() + ";");
            logger.debug("SQL query: " + sql.toString());

            try {
                db.update(sql.toString());
            } catch (SQLException e) {
                logger.error("Unable to update project with project ID " + getProjectId() + ". Query: " + sql, e);
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
                logger.error("Unable to delete project with project ID " + getProjectId() + ". Query: " + sql, e);
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
