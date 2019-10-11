package com.conns.marketing.campaign.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HiveDataSource {	
	private static String url = "jdbc:hive2://172.16.100.52:10000/;ssl=false";    
	private static final String JDBC_DRIVER_NAME = "org.apache.hive.jdbc.HiveDriver"; 
    private static String username = "srao";   
    private static String password = "Conns123";

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
    	Class.forName(JDBC_DRIVER_NAME); //set JDBC Hive Driver
    	return DriverManager.getConnection(url, username, password); //connect to Hive
    }
}
