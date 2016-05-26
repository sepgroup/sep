package com.sepgroup.sep.user;

import com.sepgroup.sep.db.DBException;
import com.sepgroup.sep.db.DBObject;
import com.sepgroup.sep.db.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by jeremybrown on 2016-05-17.
 */
public class UserModel {
    private int userId;

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


    class UserModelDBO implements DBObject {

        private final Logger logger = LoggerFactory.getLogger(UserModel.UserModelDBO.class);

        private static final String USER_ID_COLUMN = "UserID";
        private static final String FIRST_NAME_COLUMN = "FirstName";
        private static final String LAST_NAME_COLUMN = "LastName";
        private static final String SALARY_PER_HOUR_COLUMN = "SalaryPerHour";

        private static final String tableName = "User";
        private Database db;


        @Override
        public String getTableName() {
            return tableName;
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
        public ResultSet findAll() throws DBException {
            return null;
        }

        @Override
        public ResultSet findById(int i) throws DBException {
            return null;
        }

        @Override
        public ResultSet findBySql(String s) throws DBException {
            return null;
        }

        @Override
        public int create() throws DBException {
            return 0;
        }

        @Override
        public void update() throws DBException {

        }

        @Override
        public void delete() throws DBException {

        }
    }
}
