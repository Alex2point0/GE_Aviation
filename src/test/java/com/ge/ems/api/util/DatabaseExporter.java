package com.ge.ems.api.util;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;

public class DatabaseExporter {

    public static void main(String[] args) throws Exception
    {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
        Connection jdbcConnection = DriverManager.getConnection("jdbc:sqlserver://PROFESSORX:1433;DatabaseName=PROFESSORX_EmsWeb","sa","fooFoo123");
        IDatabaseConnection connection = new DatabaseConnection( jdbcConnection );
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN , "[?]");
        // full database export
         String[] tables = new String[]{"__MigrationHistory","ApiClients","Dashboards","EmsSystems","GroupDashboards","Groups","Logs","Roles","Uploads","UserClaims","UserDashboards","UserEmsSystems","UserGroups","UserLogins","UserRoles","Users","Views","Widgets"};
         IDataSet fullDataSet = connection.createDataSet(tables);
        // IDataSet fullDataSet = connection.createDataSet();
        FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));
    }
}
