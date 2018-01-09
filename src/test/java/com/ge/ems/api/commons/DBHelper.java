package com.ge.ems.api.commons;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.dbunit.IDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mssql.InsertIdentityOperation;
import org.dbunit.operation.DatabaseOperation;
import org.testng.Assert;

import com.ge.ems.api.dbunit.DBPropertiesBasedJdbcDatabaseTester;

public class DBHelper {
    public static final Properties configProp = new Properties();
    // Framework objects
    private static IDatabaseTester databaseTester;
    private static FlatXmlDataSetBuilder build;
    private Connection connection;
    
    public DBHelper() {
    	try {
            System.setProperty(DBPropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Class.forName(System.getProperty(DBPropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS));
            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
            System.setProperty(DBPropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL,
            		PropertiesHandler.properties.getProperty(PropertiesHandler.SQL_SERVER));
            System.setProperty(DBPropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME,
            		PropertiesHandler.properties.getProperty(PropertiesHandler.SQL_USERNAME));
            System.setProperty(DBPropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD,
            		PropertiesHandler.properties.getProperty(PropertiesHandler.SQL_PASSWORD));
            databaseTester = new DBPropertiesBasedJdbcDatabaseTester();
            build = new FlatXmlDataSetBuilder();
            build.setColumnSensing(true);
            connection = getConnection();
    	} catch (Exception e) {
             e.printStackTrace();
         }
    }
    
    /**
     * Creates only one database Connection
     *
     * @return - Connection
     */
    private final Connection getConnection() {
        Connection dbConnection = null;
        try {
            dbConnection = this.getDatabaseTester().getConnection().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection = dbConnection;
        return dbConnection;
    }
    
    /**
     * Creates the IDatabaseTester
     *
     * @return - IDatabaseTester
     * @throws Exception
     */
    private IDatabaseTester getDatabaseTester() throws Exception {
        if (databaseTester == null) {
            databaseTester = new DBPropertiesBasedJdbcDatabaseTester();
        }

        return databaseTester;
    }
	
    /**
     * Clean the DB and repopulate table with dataset
     * 
     */
    public void resetDB() {
		//Setup DB
        String[] queries = { "Delete __MigrationHistory;",
                "Delete ApiClients;",
                "Delete Dashboards;",
                "Delete EmsSystems;",
                "Delete GroupDashboards;",
                "Delete UserGroups;",
                "Delete Groups;",
                "Delete Logs;",
                "Delete UserRoles",
                "Delete Roles;",
                "Delete Uploads;",
                "Delete UserClaims;",
                "Delete UserDashboards;",
                "Delete UserEmsSystems;",                
                "Delete UserLogins;",                
                "Delete Users",
                "Delete Views",
                "Delete Widgets" };
        getDBResultBatch(queries);
        System.out.println("Tables are Deleted");
        runCleanInsertDBUnit("src/test/resources/PROFESSORX_EmsWeb.xml");

    }
    
    /**
     * Run Clean Insert operation of DBUnit
     *
     * @param file
     *
     */
    public void runCleanInsertDBUnit(String file) {
        try {
            IDatabaseConnection connection = this.getDatabaseTester().getConnection();
            IDataSet dataSet;
            databaseTester = this.getDatabaseTester();
            dataSet = build.build(new File(file));
            databaseTester.setDataSet(dataSet);
            DatabaseOperation refresh = (new InsertIdentityOperation(DatabaseOperation.CLEAN_INSERT));
            refresh.execute(connection, dataSet);
            System.out.println("Clean - Insert run on: " + file.toString());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }
    
    /**
     * Helper method to execute batch of SQL statements
     * 
     * @param queries - String array of queries that need to be executed
     * 
     * @return: return the results
     */
    public int[] getDBResultBatch(String[] queries) {
        Statement stmt;
        int[] count = null;
        try {
            if (connection == null) {
                Assert.fail("Connection is null");
            }
            stmt = connection.createStatement();
            for (String query : queries) {
                stmt.addBatch(query);
            }
            count = stmt.executeBatch();
        } catch (SQLException e) {
            if (!e.getMessage().equals("No results were returned by the query.")) {
                Assert.fail(String.valueOf(e.getNextException()));
            }
        }
        return count;
    }
    
    /**
     * Runs a query that is passed
     * 
     * @param query
     * 
     * @return - ResultSet
     */
    public ResultSet getDBResult(String query) {
        Statement stmt;
        ResultSet rs = null;
        if (connection == null) {
            Assert.fail("Connection is null");
        }
        try {
            stmt = connection.createStatement();
            stmt.execute(query);
        } catch (SQLException e) {
            if (!(e.getMessage().contains("No results were returned by the query."))) {
                if (e.getNextException() != null) {
                	e.printStackTrace();
                }
            }
        }
        return rs;
    }
}
