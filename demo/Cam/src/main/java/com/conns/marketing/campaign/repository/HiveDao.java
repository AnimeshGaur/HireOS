package com.conns.marketing.campaign.repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.conns.marketing.campaign.model.CustomerDataFromHive;

@Service
public class HiveDao {

	private void closeResources(ResultSet rs, Statement stmt, Connection con) throws SQLException {
		rs.close();
		stmt.close();
		con.close();
	}
	
	public List<CustomerDataFromHive> getData(String customerIds, String columnName, String tableName) throws ClassNotFoundException, SQLException {
		String query = "SELECT unique_cid, " + columnName + " FROM " + tableName + " WHERE unique_cid IN (" + customerIds + ")"; 
		
		Connection con = HiveDataSource.getConnection();
		
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		List<CustomerDataFromHive> customerData = new ArrayList<>();
		while(rs.next()) {
			CustomerDataFromHive customer = new CustomerDataFromHive();
			customer.setUniqueCID(rs.getString("unique_cid"));
			customer.setFilterData(rs.getString(columnName));
			customerData.add(customer);
		}
		closeResources(rs, stmt, con);
		return customerData;
	}
	
	public List<CustomerDataFromHive> getDataWithDecimal(String customerIds, String columnName, String tableName) throws ClassNotFoundException, SQLException {
		String query = "SELECT unique_cid, " + columnName + " FROM " + tableName + " WHERE unique_cid IN (" + customerIds + ")"; 
		
		Connection con = HiveDataSource.getConnection();
		
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		List<CustomerDataFromHive> customerData = new ArrayList<>();
		while(rs.next()) {
			CustomerDataFromHive customer = new CustomerDataFromHive();
			customer.setUniqueCID(rs.getString("unique_cid"));
			customer.setFilterData(rs.getBigDecimal(columnName).toString());
			customerData.add(customer);
		}
		closeResources(rs, stmt, con);
		return customerData;
	}

}
