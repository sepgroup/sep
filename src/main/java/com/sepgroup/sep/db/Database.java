package com.sepgroup.sep.db;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public Database(String dbPath) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            conn.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            logger.error("Unable to load class org.sqlite.JDBC");
        } catch ( SQLException e ) {
            logger.error("Unable to open database file", e);
            System.exit(0);
	    }
	  }

    public Database() {

    }

    public ResultSet query(String sql) throws SQLException {
        Statement s = conn.createStatement();
        s.setQueryTimeout(5);
        return s.executeQuery(sql);
	}

    public int insert(String sql) throws SQLException {
        PreparedStatement s = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        s.setQueryTimeout(5);

        if (s.executeUpdate() == 0) {
            throw new SQLException("Inserting failed, no rows affected. Query: " + sql);
        }

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
    }
	
	public void closeConnection() throws SQLException {
        conn.close();
	}

    /**
     * Get the instance of the DB from the specified path
     */
    public static Database getActiveDB() {
        DBConfig cfg = ConfigFactory.create(DBConfig.class);
        String activeDBPath = cfg.activeDbPath();
        return getDB(activeDBPath);
    }

    /**
     * Get the instance of the DB from the specified path
     * @param dbPath the path to the specified DB
     */
    public static Database getDB(String dbPath) {
        Database activeDB = activeDBs.get(dbPath);
        if (activeDB != null) {
            return activeDB;
        }
        else {
            return new Database(dbPath);
        }
    }
}
