package com.sepgroup.sep.db;

import com.sepgroup.sep.SepUserStorage;
import com.sepgroup.sep.model.ModelNotFoundException;
import com.sepgroup.sep.model.ProjectModel;
import com.sepgroup.sep.model.TaskModel;
import com.sepgroup.sep.model.UserModel;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private static Logger logger = LoggerFactory.getLogger(Database.class);
    private static Map<String, Database> activeDBs = new HashMap<>();

    /**
     * Path to the DB file
     */
    private String dbPath;

    /**
     * Connection for connect to database
     */
    private Connection conn;

    /**
     *
     * @param dbPathString path to the DB file
     */
	public Database(String dbPathString) throws DBException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            logger.error("Unable to load class org.sqlite.JDBC", e);
            throw new DBException(e);
        }

        Path dbPath = SepUserStorage.getPath().resolve(dbPathString);
        this.dbPath = dbPath.toAbsolutePath().toString();
    }

    private void openConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            logger.warn("Previous DB connection was not closed, closing it now");
            conn.close();
        }
        try {
            logger.debug("Opening DB connection");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            conn.setAutoCommit(false);
        } catch ( SQLException e ) {
            logger.error("Unable to open database file", e);
            throw new SQLException("Unable to open database file", e);
        }
    }

    public String getDbPath() {
        return dbPath;
    }

    public ResultSet query(String sql) throws SQLException {
        openConnection();
//        sql.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]","");
        Statement s = conn.createStatement();
        s.setQueryTimeout(5);
        ResultSet rs = s.executeQuery(sql);
        conn.commit();
        return rs;
	}

    public int insert(String sql) throws SQLException {
        openConnection();
//        sql.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]","");
        PreparedStatement s = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        s.setQueryTimeout(5);

        if (s.executeUpdate() == 0) {
            logger.error("Inserting failed, no rows affected. Query: " + sql);
            throw new SQLException("Inserting failed, no rows affected. Query: " + sql);
        }
        conn.commit();

        ResultSet generatedKeys = s.getGeneratedKeys();
        if (generatedKeys.next()) {
            s.close();
            return generatedKeys.getInt(1);
        }
        else {
            logger.error("Inserting failed, no ID obtained. Query: " + sql);
            s.close();
            throw new SQLException("Inserting failed, no ID obtained. Query: " + sql);
        }
    }

    public void update(String sql) throws SQLException {
        openConnection();
//        sql.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]","");
        Statement s = conn.createStatement();
        s.setQueryTimeout(5);

        if (s.executeUpdate(sql) == 0) {
            logger.error("Update failed, no rows affected. Query: " + sql);
            throw new SQLException("Update failed, no rows affected. Query: " + sql);
        }
        conn.commit();
        s.close();
    }

    public void create(String sql) throws SQLException {
        openConnection();
        sql.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]",""); // this is never used
        Statement s = conn.createStatement();
        s.setQueryTimeout(5);
        s.execute(sql);
        conn.commit();
        s.close();
    }
	public void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            logger.debug("Closing DB connection to " + getDbPath());
            conn.close();
        }
        else {
            logger.warn("DB connection was already closed");
        }
	}

    /**
     * Get the instance of the DB from the path specified in the properties file (db.properties for main code,
     * db-test.properties for testing code)
     */
    public static Database getActiveDB() throws DBException {
        if (ConfigFactory.getProperty("configPath") == null) {
            logger.debug("DB config path was not previously set, setting it now");
            ConfigFactory.setProperty("configPath", Database.class.getResource("/db.properties").getFile());
        }
        DBConfig cfg = ConfigFactory.create(DBConfig.class);
        String activeDBPath = cfg.activeDbPath();
        return getDB(activeDBPath);
    }

    /**
     * Get the instance of the DB from the specified path
     * @param dbPath the path to the specified DB
     */
    public static Database getDB(String dbPath) throws DBException {
        logger.debug("Getting DB " + dbPath);
        Database activeDB = activeDBs.get(dbPath);
        if (isDBActive(dbPath)) {
            logger.debug("DB " + dbPath + " was already active, using same instance");
            return activeDB;
        }
        else {
            logger.debug("DB " + dbPath + " was not already active, creating instance");
            Database newDb = new Database(dbPath);
            activeDBs.put(dbPath, newDb);
            return newDb;
        }
    }

    public static boolean isDBActive(String dbPath) {
        logger.debug("Checking if DB " + dbPath + " is active");
        Database activeDB = activeDBs.get(dbPath);
        return activeDB != null;
    }
    public void clean()throws DBException, ModelNotFoundException, SQLException{
        ProjectModel.cleanData();
        TaskModel.cleanData();
        UserModel.cleanData();
        String fetchDependency="SELECT * FROM TaskDependency;";
        ResultSet rs=this.query(fetchDependency);
        if(rs.next()){
            String sql="DELETE FROM TaskDependency;";
            this.update(sql);
        }
    }

    public void createTables() throws DBException, SQLException{
        ProjectModel.createTable();
        TaskModel.createTable();
        UserModel.createTable();
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS TaskDependency(");
        sql.append("FKTaskID INT NOT NULL,");
        sql.append("DependOnTaskID INT NOT NULL,");
        sql.append("FOREIGN KEY (FKTaskID) REFERENCES Task(TaskID) ON DELETE CASCADE,");
        sql.append("FOREIGN KEY (DependOnTaskID) REFERENCES Task(TaskID) ON DELETE CASCADE);");
        this.create(sql.toString());
    }

    public void dropTable(String TableName) throws SQLException{
        openConnection();
        String sql="DROP TABLE IF EXISTS "+TableName+";";
        Statement s = conn.createStatement();
        s.setQueryTimeout(5);
        s.executeUpdate(sql);
        conn.commit();
        s.close();
    }

    /**
     * USE WITH CAUTION!
     * @throws SQLException
     */
    public void dropAllTables() throws SQLException {
        dropTable(ProjectModel.ProjectModelDBObject.TABLE_NAME);
        dropTable(TaskModel.TaskModelDBObject.TABLE_NAME);
        dropTable(TaskModel.TaskModelDBObject.DEPENDENCIES_TABLE_NAME);
        dropTable(UserModel.UserModelDBObject.TABLE_NAME);
    }
}
