package com.sepgroup.sep.db;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jeremybrown on 2016-07-25.
 */
public class DatabaseFactory {

    private static Logger logger = LoggerFactory.getLogger(DatabaseFactory.class);

    private static Map<String, Database> activeDBs = new HashMap<>();

    /**
     * Get the instance of the DB from the path specified in the db.properties file
     */
    public static Database getActiveDB() throws DBException {
        if (ConfigFactory.getProperty("configPath") == null) {
            logger.debug("DB config path was not previously set, setting it now");
            ConfigFactory.setProperty("configPath", "db.properties");
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
        if (isDBActive(dbPath)) {
            logger.debug("DB " + dbPath + " was already active, using same instance");
            return activeDBs.get(dbPath);
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
        return activeDBs.get(dbPath) != null;
    }
}
