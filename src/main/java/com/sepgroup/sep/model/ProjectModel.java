package com.sepgroup.sep.model;

import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.DBObject;
import com.sepgroup.sep.db.Database;
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
    private int managerUserId;
    private String projectDescription;

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
	public ProjectModel(String name, Date sd, Date dl, double budget, boolean done, int managerUserId,
                        String projectDescription) {
        this();
		this.name = name;
        this.budget = budget;
        setStartDate(sd);
        setDeadline(dl);
		this.done = done;
        this.managerUserId = managerUserId;
        this.projectDescription = projectDescription;
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
	private ProjectModel(int id, String name, Date sd, Date dl, double budget, boolean done, int managerUserId,
                         String projectDescription) {
        this(name, sd, dl, budget, done, managerUserId, projectDescription);
		this.projectId = id;
	}

    @Override
    public void refreshData() throws ModelNotFoundException {
        ProjectModel refreshed = getById(getProjectId());

        setName(refreshed.getName());
        setProjectId(refreshed.getProjectId());
        setBudget(refreshed.getBudget());
        setStartDate(refreshed.getStartDate());
        setDeadline(refreshed.getDeadline());
        setDone(refreshed.isDone());
        setProjectDescription(refreshed.getProjectDescription());

        updateObservers();
    }

    @Override
    public void persistData() throws DBException {
        if (this.name == null) {
            logger.error("Project name must be set to persist model to DB");
            throw new DBException("Project name must be set to persist model to DB");
        }
        if (this.projectId == 0) {
            // Project is new, not already in DB
            int projectId = this.dbo.create();
            setProjectId(projectId);
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
	public void setBudget(double budget){
		this.budget=budget;
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
	 * @param date updated date of project
	 */
	public void setStartDate(String startDate) throws ParseException {
		this.startDate = DateUtils.castStringToDate(startDate);
	}

    /**
     * Setter for Start date of project which is used in update method
     * @param date updated date of project
     */
    public void setStartDate(Date startDate) {
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
        return DateUtils.castDateToString(this.startDate);
    }

    /**
	 * Setter for Dead Line of project which is used in update method
	 * @param deadline updated date of deadline
	 */
	public void setDeadline(Date deadline){
        this.deadline = DateUtils.filterDateToMidnight(deadline);
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

    public String getDeadlineString() {
        return DateUtils.castDateToString(this.deadline);
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

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public int getManagerUserId() {
        return managerUserId;
    }

    public void setManagerUserId(int managerUserId) {
        this.managerUserId = managerUserId;
    }

    /**
     * Get a list of tasks associated to this project
     * @return a list of tasks associated to this project
     */
    public List<TaskModel> getTasks() throws ModelNotFoundException {
        return TaskModel.getAllByProject(getProjectId());
    }

    @Override
	public String toString() {
        // TODO
		return ("Project info is "+ getProjectId() + ", " + " Project name is " + getName() + ", ");
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
        try {
            if (other.getTasks() != null && getTasks() != null && other.getTasks().equals(this.getTasks())) {
                return false;
            }
        } catch (ModelNotFoundException e) {
            logger.debug("Hacky, ignoring for now but should fix.");
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
        public static final String MANAGER_USER_ID_COLUMN = "FKManagerID";
        public static final String PROJECT_DESCRIPTION_COLUMN = "ProjectDescription";
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
                        throw new ModelNotFoundException("Unable to parse Date from DB, this really shouldn't happen.", e);
                    }
                    double budgetTemp = rs.getFloat(BUDGET_COLUMN);
                    boolean doneTemp = rs.getBoolean(DONE_COLUMN);
                    int managerUserIdTemp = rs.getInt(MANAGER_USER_ID_COLUMN);
                    String projectDescriptionTemp = rs.getString(PROJECT_DESCRIPTION_COLUMN);
                    p = new ProjectModel(pIdTemp, pNameTemp, stDateTempDate, dlDateTempDate, budgetTemp, doneTemp,
                            managerUserIdTemp, projectDescriptionTemp);
                }
                else {
                    logger.info("DB query returned zero results");
                    throw new ModelNotFoundException("DB query for project ID " + projectId+ " returned no results");
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
                        throw new ModelNotFoundException("Unable to parse Date from DB, this really shouldn't happen.", e);
                    }
                    double budgetTemp = rs.getFloat(BUDGET_COLUMN);
                    boolean doneTemp = rs.getBoolean(DONE_COLUMN);
                    int managerUserIdTemp = rs.getInt(MANAGER_USER_ID_COLUMN);
                    String projectDescriptionTemp = rs.getString(PROJECT_DESCRIPTION_COLUMN);
                    projectList.add(new ProjectModel(pIdTemp, pNameTemp, stDateTempDate, dlDateTempDate, budgetTemp,
                            doneTemp, managerUserIdTemp, projectDescriptionTemp));
                }

                if (projectList.isEmpty()) {
                    logger.info("DB query returned zero results");
                    throw new ModelNotFoundException("DB query for all projects returned no results");
                }
            }
            catch (SQLException e) {
                logger.error("Unable to fetch all projects with manager user ID" + managerUserId + ". Query: " + sql, e);
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
        public List<ProjectModel> findAll() throws ModelNotFoundException {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + ";");
            logger.debug("Query: " + sql.toString());

            return runMultiResultQuery(sql.toString());
        }

        @Override
        public ProjectModel findById(int projectId) throws ModelNotFoundException {
            logger.debug("Building query for project ID " + projectId);
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + " ");
            sql.append("WHERE " + PROJECT_ID_COLUMN + "=" + projectId + ";");
            logger.debug("Query: " + sql.toString());

            return runSingleResultQuery(sql.toString());
        }

        public List<ProjectModel> findAllByManager(int managerUserId) throws ModelNotFoundException {
            logger.debug("Building query for projects with manager user ID " + managerUserId);
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + " ");
            sql.append("WHERE " + MANAGER_USER_ID_COLUMN + "=" + managerUserId + ";");
            logger.debug("Query: " + sql.toString());

            return runMultiResultQuery(sql.toString());
        }

        /**
         * It fetches the data from database and make one linked list of project objects
         * @param sql asked sql statement from database
         * @return Linklist of project objects
         */
        @Override
        public List<ProjectModel> findBySql(String sql) throws ModelNotFoundException {
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
            if (getManagerUserId() != 0) sql.append(", " + MANAGER_USER_ID_COLUMN);
            if (getProjectDescription() != null) sql.append(", " + PROJECT_DESCRIPTION_COLUMN);
            sql.append(") ");
            sql.append("VALUES ('" + getName() + "'");
            sql.append("," + getBudget());
            sql.append("," + (isDone() ? 1 : 0));
            if (getStartDate() != null) sql.append(",'" + DateUtils.castDateToString(getStartDate()) + "'");
            if (getDeadline() != null) sql.append(",'" + DateUtils.castDateToString(getDeadline()) + "'");
            if (getManagerUserId() != 0) sql.append("," + getManagerUserId());
            if (getProjectDescription() != null) sql.append(",'" + getProjectDescription() + "'");
            sql.append(");");
            logger.debug("SQL query: " + sql.toString());
            System.out.println(sql.toString());
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
            if (getStartDate() != null) sql.append(START_DATE_COLUMN + "='" + DateUtils.castDateToString(getStartDate()) + "',");
            if (getDeadline() != null) sql.append(DEADLINE_COLUMN + "='" + DateUtils.castDateToString(getDeadline()) + "',");
            if (getManagerUserId() != 0) sql.append(MANAGER_USER_ID_COLUMN + "='" + getManagerUserId() + "',");
            if (getProjectDescription() != null) sql.append(PROJECT_DESCRIPTION_COLUMN + "='" + getProjectDescription() + "',");
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
    }

}
