package com.intellivest.userservice.testUtil;

import javax.sql.DataSource;

import com.intellivest.userservice.user.User;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class TestUtil {

    public static User testUser = User.createUser("00unfsrxoMlovRpMk5d6", "Dylan", "Edwards",
            "dylanedwards290@gmail.com");

    public static User getTestUser() {
        return testUser;
    }

    /**
     * a test util finction to add a user into the database
     * 
     * @param dataSource
     */
    public static void loadUserIntoDatabase(DataSource dataSource) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate();
            jdbcTemplate.setDataSource(dataSource);
            jdbcTemplate.execute("USE intellivest_users_db;");
            jdbcTemplate.execute(
                    "INSERT INTO users (id, first_name, last_name, email) values ('00unfsrxoMlovRpMk5d6','Dylan', 'Edwards', 'dylanedwards290@gmail.com');");
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * a test util function to create a clean database with no users
     * 
     * @param dataSource
     */
    public static void clearUserFromDatabase(DataSource dataSource) {
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate();
            jdbcTemplate.setDataSource(dataSource);
            jdbcTemplate.execute("USE intellivest_users_db;");
            jdbcTemplate.execute("TRUNCATE TABLE users;");
        } catch (Exception e) {
            throw e;
        }
    }

}
