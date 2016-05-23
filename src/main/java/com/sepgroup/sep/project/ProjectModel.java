package com.sepgroup.sep.project;

import com.sepgroup.sep.AbstractModel;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.DBObject;
import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.login.UserModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
		this.startDate =this.castStringToDate(sd);
		this.deadline =this.castStringToDate(dl);
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
		this.projectId =id;
		this.name=name;
		this.budget=budget;
		this.startDate =this.castStringToDate(sd);
		this.deadline =this.castStringToDate(dl);
		this.done =done;
	}

    @Override
    public void refreshData() throws DBException {
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
    public static ProjectModel getById(int projectId){
        ProjectModel p = new ProjectModel();
        try{
            ResultSet rs = p.dbo.findById(projectId);
            while (rs.next()) {
                int idTemp = rs.getInt("projectId");
                String  PnameTemp = rs.getString("ProjectName");
                String stDateTemp=rs.getString("startDate");
                String dlDateTemp=rs.getString("deadline");
                int budgetTemp = rs.getInt("Budget");
                Boolean doneTemp=rs.getBoolean("done");
                p = new ProjectModel(idTemp, PnameTemp, stDateTemp, dlDateTemp, budgetTemp, doneTemp);
            }
        } catch (DBException | SQLException e) {
            logger.error("Unable to find ProjectModel(s)", e);
        }
        return p;
    }

    /**
     * It fetches all data from database and make a LinkedList
     * of project objects and return it
     * @return LinkedList of Project Objects
     */
    public static List<ProjectModel> getAll(){
        List<ProjectModel> projectList = new LinkedList<>();
        try {
            ResultSet rs = new ProjectModel().dbo.findAll();
            while (rs.next()) {
                // Extract values
                int idTemp = rs.getInt("projectId");
                String  PnameTemp = rs.getString("ProjectName");
                String stDateTemp=rs.getString("startDate");
                String dlDateTemp=rs.getString("deadline");
                int budgetTemp = rs.getInt("Budget");
                Boolean doneTemp=rs.getBoolean("done");
                projectList.add(new ProjectModel(idTemp, PnameTemp, stDateTemp, dlDateTemp, budgetTemp, doneTemp));
            }
        }
        catch (DBException | SQLException e) {
            logger.error("Unable to find ProjectModel(s)", e);
        }

        return projectList;
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
	public void setStartDate(String date){
		this.startDate = castStringToDate(date);
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
    public void setDeadline(String deadline){
        this.deadline = castStringToDate(deadline);
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
	 * Makes Date object to the String format which is easier to put in database
	 * @param input Date object that we want to cast to String
	 * @return String format of Date
	 */
	public static String castDateToString(Date input){
		SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
		String date = temp.format(input);
		return date;
	}
	/**
	 * Makes String to Date object
	 * @param input this is a String format variable
	 * @return Date object
	 */
	public static Date castStringToDate(String input){
		SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date=temp.parse(input);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString(){
		return ("Project info is "+this.projectId +" ,"+" Project name is " + this.name + " ,"+this.castDateToString(startDate)+" ,"+this.castDateToString(deadline)+" ,"+this.budget);
	}

    public boolean findBySql(String sql) {
        return false;
    }

    class ProjectModelDBObject implements DBObject {

        private final Logger logger = LoggerFactory.getLogger(ProjectModel.ProjectModelDBObject.class);

        private static final String PROJECT_ID_COLUMN = "PID";
        private static final String PROJECT_NAME_COLUMN = "ProjectName";
        private static final String DEADLINE_COLUMN = "DeadLine";
        private static final String START_DATE_COLUMN = "StartDate";
        private static final String BUDGET_COLUMN = "Budget";
        private static final String DONE_COLUMN = "Done";

        private static final String tableName="Project";
        private Database db = Database.getActiveDB();

        @Override
        public String getTableName() {
            return null;
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
            try {
                ResultSet rs = db.query(sql);
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
                logger.error("Unable to fetch all entries in Project table" + ". Query: " + sql, e);
                throw new DBException(e);
            }
        }

        @Override
        public ResultSet findById(int projectId) throws DBException {
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

        /**
         * It fetches the data from database and make one linklist of project objects
         * @param sql asked sql statement from database
         * @return Linklist of project objects
         */
        @Override
        public ResultSet findBySql(String sql) throws DBException {
            try {
                return db.query(sql);
            } catch (SQLException e) {
                logger.error("Unable to fetch using query" + sql, e);
                throw new DBException(e);
            }
        }

        /**
         * This Method insert the information of new project inside database
         * if fetches the Id from inserted row and assign it to the projectId
         */
        @Override
        public int create() throws DBException {
            // Build query
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO "+ getTableName() + " ");
            sql.append("("+ PROJECT_NAME_COLUMN + "," + START_DATE_COLUMN + "," + DEADLINE_COLUMN + "," + BUDGET_COLUMN + "," + DONE_COLUMN + ") ");
            sql.append("VALUES ('" + getName() + "'");
            sql.append(",'" + castDateToString(getStartDate()) + "'");
            sql.append(",'" + castDateToString(getDeadline()) + "'");
            sql.append(",'" + getBudget() + "'");
            sql.append(",'" + (isDone() ? 1 : 0) + "'");
            sql.append(");");

            try {
                return this.db.insert(sql.toString());
            } catch (SQLException e) {
                logger.error("Unable to create task " + ". Query: " + sql, e);
                throw new DBException(e);
            }
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
            sql.append(START_DATE_COLUMN + "='" + castDateToString(getStartDate()) + "'");
            sql.append(DEADLINE_COLUMN + "='" + castDateToString(getDeadline()) + "'");
            sql.append(BUDGET_COLUMN + "='" + getBudget() + "'");
            sql.append(DONE_COLUMN + "='" + (isDone() ? 1 : 0) + "'");
            sql.append("WHERE " + PROJECT_ID_COLUMN + "=" + getProjectId() + ";");

            try {
                db.update(sql.toString());
            } catch (SQLException e) {
                logger.error("Unable to update project with projectId " + getProjectId() + ". Query: " + sql, e);
                throw new DBException(e);
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
                logger.error("Unable to delete task with taskId" + getProjectId() + ". Query: " + sql, e);
                throw new DBException(e);
            }
        }
    }

}
