package com.sepgroup.sep.user;

import com.sepgroup.sep.AbstractModel;
import com.sepgroup.sep.ModelNotFoundException;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.DBObject;
import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.task.TaskModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by jeremybrown on 2016-05-17.
 */
public class UserModel extends AbstractModel {

    private static Logger logger = LoggerFactory.getLogger(UserModel.UserModelDBObject.class);

    private UserModelDBObject dbo;

    private int userId;
    private String firstName;
    private String lastName;
    private double salaryPerHour;

    /**
     * Default constructor
     */
    public UserModel() {
        dbo = new UserModelDBObject();
    }

    /**
     * Constructor for use when creating a new user that hasn't been saved to the database yet.
     * The instance's userId field will be set to 0 until it is saved to the DB, when it will be set to the generated DB PK.
     * @param firstName the user's first name
     * @param lastName the user's last name
     */
    public UserModel(String firstName, String lastName, double salaryPerHour) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.salaryPerHour = salaryPerHour;
    }

    /**
     * Constructor for use when fetching & instantiating user model from DB, with userId already set
     * @param userId
     * @param firstName the user's first name
     * @param lastName the user's last name
     */
    public UserModel(int userId, String firstName, String lastName, double salaryPerHour) {
        this(firstName, lastName, salaryPerHour);
        this.userId = userId;
    }

    @Override
    public void refreshData() throws ModelNotFoundException {
        UserModel refreshed = getById(getUserId());

        setFirstName(refreshed.getFirstName());
        setLastName(refreshed.getLastName());
        setUserId(refreshed.getUserId());
        setSalaryPerHour(refreshed.getSalaryPerHour());

        updateObservers();
    }

    @Override
    public void persistData() throws DBException {
        if (this.firstName == null || this.lastName == null) {
            logger.error("First & last names must be set to persist model to DB");
            throw new DBException("First & last names must be set to persist model to DB");
        }
        if (this.userId == 0) {
            // User is new, not already in DB
            int userId = this.dbo.create();
            setUserId(userId);
        }
        else {
            // User is already in DB
            this.dbo.update();
        }
        updateObservers();
    }

    @Override
    public void deleteData() throws DBException {
        this.dbo.delete();
    }

    /**
     * Fetch the data from database which has the given user ID
     * and create one object of user and returns it.
     * @param userId the ID of the user to search for
     * @return the UserModel representing the user of the specified user ID
     */
    public static UserModel getById(int userId) throws ModelNotFoundException {
        return new UserModel().dbo.findById(userId);
    }

    /**
     * It fetches all data from database and make a LinkedList
     * of user objects and return it.
     * @return LinkedList of UserModel objects containing all users present in the DB
     */
    public static List<UserModel> getAll() throws ModelNotFoundException {
        return new UserModel().dbo.findAll();
    }

    /**
     *
     * @return
     */
    public List<TaskModel> getAssignedTasks() {
        return TaskModel.getAllByUser(getUserId());
    }

    /**
     *
     * @return
     */
    public int getUserId() {
        return userId;
    }

    /**
     *
     * @param userId
     */
    private void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     *
     * @return
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     *
     * @param firstName
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     *
     * @return
     */
    public String getLastName() {
        return lastName;
    }

    /**
     *
     * @param lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     *
     * @return
     */
    public double getSalaryPerHour() {
        return salaryPerHour;
    }

    /**
     *
     * @param salaryPerHour
     */
    public void setSalaryPerHour(double salaryPerHour) {
        this.salaryPerHour = salaryPerHour;
    }

    @Override
    public String toString() {
        return "User ID " + getUserId() + ", First name: " + getFirstName() + ", Last name: " + getLastName();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserModel)) {
            return false;
        }
        UserModel other = (UserModel) obj;
        if (other.getUserId() != this.getUserId()) {
            return false;
        }
        if (!other.getFirstName().equals(this.getFirstName())) {
            return false;
        }
        if (!other.getLastName().equals(this.getLastName())) {
            return false;
        }
        if (other.getSalaryPerHour() != this.getSalaryPerHour()) {
            return false;
        }

        return true;
    }

    class UserModelDBObject implements DBObject {

        private final Logger logger = LoggerFactory.getLogger(UserModel.UserModelDBObject.class);

        private static final String USER_ID_COLUMN = "UserID";
        private static final String FIRST_NAME_COLUMN = "FirstName";
        private static final String LAST_NAME_COLUMN = "LastName";
        private static final String SALARY_PER_HOUR_COLUMN = "SalaryPerHour";
        private static final String TABLE_NAME = "User";

        private Database db;

        private UserModelDBObject() {
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
        public List<UserModel> findAll() throws ModelNotFoundException {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + ";");

            List<UserModel> userList = new LinkedList<>();
            try {
                ResultSet rs =  db.query(sql.toString());

                while (rs.next()) {
                    // Extract values
                    int idTemp = rs.getInt(USER_ID_COLUMN);
                    String firstNameTemp = rs.getString(FIRST_NAME_COLUMN);
                    String lastNameTemp = rs.getString(LAST_NAME_COLUMN);
                    double salaryPerHourTemp = rs.getFloat(SALARY_PER_HOUR_COLUMN);
                    userList.add(new UserModel(idTemp, firstNameTemp, lastNameTemp, salaryPerHourTemp));
                }

                if (userList.isEmpty()) {
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
            return userList;
        }

        @Override
        public UserModel findById(int userId) throws ModelNotFoundException {
            logger.debug("Building query for user ID " + userId);
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + " ");
            sql.append("WHERE " + USER_ID_COLUMN + "=" + userId + ";");
            logger.debug("Query: " + sql.toString());

            UserModel u = null;
            try {
                ResultSet rs = db.query(sql.toString());
                if (rs.next()) {
                    logger.debug("Constructing ProjectModel with DB fields");
                    String firstNameTemp = rs.getString(FIRST_NAME_COLUMN);
                    String lastNameTemp = rs.getString(LAST_NAME_COLUMN);
                    double salaryPerHourTemp = rs.getFloat(SALARY_PER_HOUR_COLUMN);
                    u = new UserModel(userId, firstNameTemp, lastNameTemp, salaryPerHourTemp);
                }
                else {
                    logger.info("DB query returned zero results");
                    throw new ModelNotFoundException("DB query for user ID " + userId + " returned no results");
                }
            }
            catch (SQLException e) {
                logger.error("Unable to fetch user with user ID " + userId + ". Query: " + sql, e);
                throw new ModelNotFoundException(e);
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    logger.debug("Unable to close connection to " + db.getDbPath(), e);
                }
            }
            return u;
        }

        @Override
        public List<UserModel> findBySql(String s) throws ModelNotFoundException {
            // TODO
            return null;
        }

        @Override
        public int create() throws DBException {
            logger.debug("Building SQL query for project model");
            StringBuilder sql = new StringBuilder();
            sql.append("INSERT INTO "+ getTableName() + " ");
            sql.append("("+ FIRST_NAME_COLUMN + "," + LAST_NAME_COLUMN + "," + SALARY_PER_HOUR_COLUMN + ") ");
            sql.append("VALUES ('" + getFirstName() + "','" + getLastName() + "'," + getSalaryPerHour() + ");");
            logger.debug("SQL query: " + sql.toString());

            int insertedKey;
            try {
                insertedKey = this.db.insert(sql.toString());
                logger.debug("Insert query returned key " + insertedKey);
            } catch (SQLException e) {
                logger.error("Unable to create project " + ". Query: " + sql, e);
                throw new DBException("Unable to create user " + ". Query: " + sql, e);
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

        @Override
        public void update() throws DBException {
            // Build query
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE "+ getTableName() + " ");
            sql.append("SET ");
            sql.append(FIRST_NAME_COLUMN + "='" + getFirstName() + "',");
            sql.append(LAST_NAME_COLUMN + "='" + getLastName() + "',");
            sql.append(SALARY_PER_HOUR_COLUMN + "=" + getSalaryPerHour() + " ");
            sql.append("WHERE " + USER_ID_COLUMN + "=" + getUserId() + ";");
            logger.debug("SQL query: " + sql.toString());

            try {
                db.update(sql.toString());
            } catch (SQLException e) {
                logger.error("Unable to update user with ID " + getUserId() + ". Query: " + sql, e);
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
            sql.append("WHERE " + USER_ID_COLUMN + "=" + getUserId() + ";");

            try {
                db.update(sql.toString());
            } catch (SQLException e) {
                logger.error("Unable to delete user with ID " + getUserId() + ". Query: " + sql, e);
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
