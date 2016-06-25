package com.sepgroup.sep.db;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
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
     * @param dbPath path to the DB file
     */
	public Database(String dbPath) throws DBException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            logger.error("Unable to load class org.sqlite.JDBC", e);
            throw new DBException(e);
        }

        URL dbUrl = Thread.currentThread().getContextClassLoader().getResource(dbPath);
        String dbFullPath = dbUrl.getFile();
        this.dbPath = dbFullPath;
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
        sql.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]","");
        Statement s = conn.createStatement();
        s.setQueryTimeout(5);
        ResultSet rs = s.executeQuery(sql);
        conn.commit();
        return rs;
	}

    public int insert(String sql) throws SQLException {
        openConnection();
        sql.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]","");
        System.out.println(sql);
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
        sql.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]","");
        Statement s = conn.createStatement();
        s.setQueryTimeout(5);

        if (s.executeUpdate(sql) == 0) {
            logger.error("Update failed, no rows affected. Query: " + sql);
            throw new SQLException("Update failed, no rows affected. Query: " + sql);
        }
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
     * Get the instance of the DB from the specified path
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
            return new Database(dbPath);
        }
    }

    public static boolean isDBActive(String dbPath) {
        logger.debug("Checking if DB " + dbPath + " is active");
        Database activeDB = activeDBs.get(dbPath);
        return activeDB != null;
    }
}
