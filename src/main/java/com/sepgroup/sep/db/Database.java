package com.sepgroup.sep.db;

import com.sepgroup.sep.SepUserStorage;
import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private static Logger logger = LoggerFactory.getLogger(Database.class);

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
            logger.debug("Previous DB connection was not closed, closing it now");
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

    /**
     * Closes the current DB connection.
     * @return true if the connection was closed, false if the connection was already closed.
     * @throws SQLException
     */
	public boolean closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            logger.debug("Closing DB connection to " + getDbPath());
            conn.close();
            return true;
        }
        else {
            logger.warn("DB connection was already closed");
            return false;
        }
	}

    public void dropTable(String tableName) throws DBException {
        try {
            openConnection();
            String sql = "DROP TABLE IF EXISTS " + tableName + ";";
            Statement s = conn.createStatement();
            s.setQueryTimeout(5);
            s.executeUpdate(sql);
            conn.commit();
            s.close();
        } catch (SQLException e) {
            throw new DBException("Error dropping table " + tableName, e);
        }
    }
}
