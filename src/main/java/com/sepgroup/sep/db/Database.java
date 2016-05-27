package com.sepgroup.sep.db;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.io.File;
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
            URL dbUrl = Thread.currentThread().getContextClassLoader().getResource(dbPath);
            String dbFullPath = dbUrl.getFile();
            this.dbPath = dbFullPath;
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbFullPath);
            conn.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            logger.error("Unable to load class org.sqlite.JDBC",e );
            throw new DBException(e);
        } catch ( SQLException e ) {
            logger.error("Unable to open database file", e);
            throw new DBException(e);
	    }
	  }

    public String getDbPath() {
        return dbPath;
    }

    public ResultSet query(String sql) throws SQLException {
        Statement s = conn.createStatement();
        s.setQueryTimeout(5);
        ResultSet rs = s.executeQuery(sql);
        conn.commit();
        return rs;
	}

    public int insert(String sql) throws SQLException {
        PreparedStatement s = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        s.setQueryTimeout(5);

        if (s.executeUpdate() == 0) {
            throw new SQLException("Inserting failed, no rows affected. Query: " + sql);
        }
        conn.commit();

        try (ResultSet generatedKeys = s.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
            else {
                throw new SQLException("Inserting failed, no ID obtained. Query: " + sql);
            }
        }
    }

    public void update(String sql) throws SQLException {
        Statement s = conn.createStatement();
        s.setQueryTimeout(5);

        if (s.executeUpdate(sql) == 0) {
            throw new SQLException("Update failed, no rows affected. Query: " + sql);
        }
        conn.commit();
    }
	
	public void closeConnection() throws SQLException {
        logger.debug("Closing DB connection to " + getDbPath());
        conn.close();
	}

    /**
     * Get the instance of the DB from the specified path
     */
    public static Database getActiveDB() throws DBException {
        if (ConfigFactory.getProperty("configPath") == null) {
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
        Database activeDB = activeDBs.get(dbPath);
        if (isDBActive(dbPath)) {
            return activeDB;
        }
        else {
            return new Database(dbPath);
        }
    }

    public static boolean isDBActive(String dbPath) {
        Database activeDB = activeDBs.get(dbPath);
        return activeDB != null;
    }
}
