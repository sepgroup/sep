package com.sepgroup.sep.model;

import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.custom.mysql.MysObjects;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.DBObject;
import com.sepgroup.sep.db.Database;
import com.sepgroup.sep.utils.CurrencyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;
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

    private static UserModel emptyUser;

    /**
     * Default constructor
     */
    public UserModel() {
        dbo = new UserModelDBObject();
    }

    /**
     * Constructor for getEmptyUser() only
     */
    private UserModel(String firstName, String lastName) {
        // this.dbo not initialized so that this object cannot be saved to the database
        // trying to do so should crash the app..? yeah should fix this i guess
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Constructor for use when creating a new user that hasn't been saved to the database yet.
     * The instance's userId field will be set to 0 until it is saved to the DB, when it will be set to the generated
     * DB PK.
     * @param firstName the user's first name
     * @param lastName the user's last name
     */
    public UserModel(String firstName, String lastName, double salaryPerHour) throws InvalidInputException {
        this();
        setFirstName(firstName);
        setLastName(lastName);
        setSalaryPerHour(salaryPerHour);
    }

    /**
     * Constructor for package use only, skips data validation
     * @param userId
     * @param firstName the user's first name
     * @param lastName the user's last name
     */
    protected UserModel(int userId, String firstName, String lastName, double salaryPerHour) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.salaryPerHour = CurrencyUtils.roundToTwoDecimals(salaryPerHour);
        this.userId = userId;
    }

    @Override
    public void refreshData() throws ModelNotFoundException, InvalidInputException {
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
            this.userId = userId;
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

    public static UserModel getEmptyUser() {
        if (emptyUser == null) {
            emptyUser = new UserModel("None", "");
        }
        return emptyUser;
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
     * It removes all tuples from User table
     * @throws DBException
     */
    public static void cleanData()throws DBException{
        new UserModel().dbo.clean();
    }

    public static void createTable() throws DBException{
        new UserModel().dbo.createTable();
    }
    /**
     *
     * @return
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Set the user's User ID
     * @param userId the user's User ID
     * @throws InvalidInputException if the user ID is not a positive integer
     */
    private void setUserId(int userId) throws InvalidInputException {
        if (userId < 0) {
            throw new InvalidInputException("User ID must be a positive integer.");
        }
        this.userId = userId;
    }

    /**
     * Get the user's first name
     * @return the user's last name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the user's first name
     * @param firstName the user's first name
     * @throws InvalidInputException if the first name is greater than 50 characters in length
     */
    public void setFirstName(String firstName) throws InvalidInputException {
        if (firstName.length() > 50) {
            throw new InvalidInputException("First name must be 50 characters or less.");
        }
        this.firstName = firstName;
    }

    /**
     * Get the user's last name
     * @return the user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the user's first name
     * @param lastName the user's first name
     * @throws InvalidInputException if the last name is greater than 50 characters in length
     */
    public void setLastName(String lastName) throws InvalidInputException {
        if (lastName.length() > 50) {
            throw new InvalidInputException("First name must be 50 characters or less.");
        }
        this.lastName = lastName;
    }

    /**
     * Get the user's full name (FirstName LastName)
     * @return the user's full name
     */
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    /**
     * Get the user's hourly salary
     * @return the user's hourly salary
     */
    public double getSalaryPerHour() {
        return salaryPerHour;
    }

    /**
     * Set the user's hourly salary
     * @param salaryPerHour the user's hourly salary
     */
    public void setSalaryPerHour(double salaryPerHour) throws InvalidInputException {
        if (salaryPerHour < 0) {
            throw new InvalidInputException("Salary must be positive.");
        } else {
            this.salaryPerHour = CurrencyUtils.roundToTwoDecimals(salaryPerHour);
        }
    }

    @Override
    public String toString() {
        String userIdStr = null;
        String firstNameStr = null;
        String lastNameStr = null;
        userIdStr = (getUserId() != 0) ? Integer.toString(getUserId()) : " ";
        firstNameStr = (getFirstName() != null) ? getFirstName() : " ";
        lastNameStr = (getLastName() != null) ? getLastName() : " ";

        return "[" + userIdStr + "] " + firstNameStr + " " + lastNameStr;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UserModel)) {
            return false;
        }
        UserModel other = (UserModel) obj;
        if (other.getUserId() != this.getUserId()) {
            return false;
        }
        if (!equalsNullable(other.getFirstName(), getFirstName())) {
            return false;
        }
        if (!equalsNullable(other.getLastName(), getLastName())) {
            return false;
        }
        // TODO fix float comparison
        if (other.getSalaryPerHour() != this.getSalaryPerHour()) {
            return false;
        }

        return true;
    }

    // TODO cascade to task assignee & project manager when user deleted

    public class UserModelDBObject implements DBObject {

        private final Logger logger = LoggerFactory.getLogger(UserModel.UserModelDBObject.class);

        public static final String USER_ID_COLUMN = "UserID";
        public static final String FIRST_NAME_COLUMN = "FirstName";
        public static final String LAST_NAME_COLUMN = "LastName";
        public static final String SALARY_PER_HOUR_COLUMN = "SalaryPerHour";
        public static final String TABLE_NAME = "User";

        private Database db;

        DbTable userTable;
        DbColumn userIdColumn;
        DbColumn firstNameColumn;
        DbColumn lastNameColumn;
        DbColumn salaryPerHourColumn;

        DropQuery dropTableQuery;

        private UserModelDBObject() {
            try {
                db = Database.getActiveDB();
            } catch (DBException e) {
                logger.error("Unable to read from database", e);
            }

            setUpTableSchema();
        }

        private void setUpTableSchema() {
            // Add table w/ info
            userTable = db.getDbchema().addTable(TABLE_NAME);
            userIdColumn = userTable.addColumn(USER_ID_COLUMN, "integer", null);
            userIdColumn.notNull();
            userIdColumn.primaryKey();
            firstNameColumn = userTable.addColumn(FIRST_NAME_COLUMN, "varchar", 50);
            lastNameColumn = userTable.addColumn(LAST_NAME_COLUMN, "varchar", 50);
            salaryPerHourColumn = userTable.addColumn(SALARY_PER_HOUR_COLUMN, "float", null);
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

        private UserModel runSingleResultQuery(String sql) throws ModelNotFoundException {
            UserModel u = null;
            try {
                ResultSet rs = db.query(sql);
                if (rs.next()) {
                    logger.debug("Constructing ProjectModel with DB fields");
                    int userIdTemp = rs.getInt(USER_ID_COLUMN);
                    String firstNameTemp = rs.getString(FIRST_NAME_COLUMN);
                    String lastNameTemp = rs.getString(LAST_NAME_COLUMN);
                    double salaryPerHourTemp = rs.getFloat(SALARY_PER_HOUR_COLUMN);
                    u = new UserModel(userIdTemp, firstNameTemp, lastNameTemp, salaryPerHourTemp);
                }
                else {
                    logger.debug("DB query returned zero results");
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

        private List<UserModel> runMultiResultQuery(String sql) throws ModelNotFoundException {
            List<UserModel> userList = new LinkedList<>();
            try {
                ResultSet rs =  db.query(sql);

                while (rs.next()) {
                    // Extract values
                    int idTemp = rs.getInt(USER_ID_COLUMN);
                    String firstNameTemp = rs.getString(FIRST_NAME_COLUMN);
                    String lastNameTemp = rs.getString(LAST_NAME_COLUMN);
                    double salaryPerHourTemp = rs.getFloat(SALARY_PER_HOUR_COLUMN);
                    userList.add(new UserModel(idTemp, firstNameTemp, lastNameTemp, salaryPerHourTemp));
                }

                if (userList.isEmpty()) {
                    logger.debug("DB query returned zero results");
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
        public List<UserModel> findAll() throws ModelNotFoundException {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + ";");

            return runMultiResultQuery(sql.toString());
        }

        @Override
        public UserModel findById(int userId) throws ModelNotFoundException {
            logger.debug("Building query for user ID " + userId);
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * ");
            sql.append("FROM " + getTableName() + " ");
            sql.append("WHERE " + USER_ID_COLUMN + "=" + userId + ";");
            logger.debug("Query: " + sql.toString());

            return runSingleResultQuery(sql.toString());
        }

        @Override
        public List<UserModel> findBySql(String sql) throws ModelNotFoundException, InvalidInputException {
            return runMultiResultQuery(sql);
        }

        @Override
        public int create() throws DBException {
            logger.debug("Building SQL query for inserting user model");
            String insertProjectSql =
                    new InsertQuery(userTable)
                    .addColumn(firstNameColumn, getFirstName())
                    .addColumn(lastNameColumn, getLastName())
                    .addColumn(salaryPerHourColumn, getSalaryPerHour())
                    .validate().toString();
            logger.debug("SQL query: " + insertProjectSql);

            int insertedKey;
            try {
                insertedKey = this.db.insert(insertProjectSql);
                logger.debug("Insert query returned key " + insertedKey);
            } catch (SQLException e) {
                logger.error("Unable to create project " + ". Query: " + insertProjectSql, e);
                throw new DBException("Unable to create user " + ". Query: " + insertProjectSql, e);
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
            logger.debug("Building SQL query for updating user model");
            String updateUserSql = new UpdateQuery(userTable)
                    .addSetClause(firstNameColumn, getFirstName())
                    .addSetClause(lastNameColumn, getLastName())
                    .addSetClause(salaryPerHourColumn, getSalaryPerHour())
                    .addCondition(BinaryCondition.equalTo(userIdColumn, getUserId()))
                    .validate().toString();

            logger.debug("SQL query: " + updateUserSql);

            try {
                db.update(updateUserSql);
            } catch (SQLException e) {
                logger.error("Unable to update user with ID " + getUserId() + ". Query: " + updateUserSql, e);
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
            logger.debug("Building SQL query for deleting user model");
            String deleteUserSql = new DeleteQuery(userTable)
                    .addCondition(BinaryCondition.equalTo(userIdColumn, getUserId()))
                    .validate().toString();
            logger.debug("SQL query: " + deleteUserSql);

            try {
                db.update(deleteUserSql);
            } catch (SQLException e) {
                logger.error("Unable to delete user with ID " + getUserId() + ". Query: " + deleteUserSql, e);
                throw new DBException("Unable to delete user with ID " + getUserId() + ". Query: " + deleteUserSql, e);
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
            logger.debug("Building SQL query for cleaning " + TABLE_NAME + " table");
            String cleanUserTableSql = new DeleteQuery(userTable).validate().toString();
            logger.debug("SQL query: " + cleanUserTableSql);
            try {
                if (this.findAll() != null) {
                    try {
                        db.update(cleanUserTableSql);
                    } catch (SQLException e) {
                        logger.error("Unable to delete data from table " + getTableName(), e);
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
        }

        @Override
        public void createTable() throws DBException {
            logger.debug("Building SQL query for creating " + TABLE_NAME + " table");
            CreateTableQuery createUserTableQuery =
                    new CreateTableQuery(userTable, true)
                    .validate();
            dropTableQuery = createUserTableQuery.getDropQuery();
            String createUserTableSql = createUserTableQuery.toString();

            logger.debug("SQL query: " + createUserTableSql);

            try {
                db.create(createUserTableSql);
            } catch (SQLException e) {
                logger.error("Unable to create table "+ getTableName(), e);
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
                db.create(dropTableQuery.validate().toString());
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
