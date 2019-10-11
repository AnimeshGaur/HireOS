package com.conns.marketing.campaign.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Repository;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Expected;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.conns.marketing.campaign.configuration.AppConfig;
import com.conns.marketing.campaign.configuration.AppConfigBean;
import com.conns.marketing.campaign.model.CampaignFile;
import com.conns.marketing.campaign.model.Customer;

@Repository
public class CampaignManagementDao {
	ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
	AppConfigBean configBean = (AppConfigBean) applicationContext.getBean("getProfile");
	
	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	
	DynamoDB db = new DynamoDB(client);

	/**
	 * persist a record
	 * @param tableName
	 * @param jsonString
	 * @return
	 * @throws Exception
	 */
	public void persistRecord(String tableName, String jsonString) throws Exception{ 
		Table table = db.getTable(tableName);
		table.putItem(Item.fromJSON(jsonString));
	}
	
	/**
	 * update a record
	 * @param tableName
	 * @param expectedCondition
	 * @param item
	 * @throws Exception
	 */
	public void updateRecord(String tableName,Expected expectedCondition,Item item) throws Exception{ 
		Table table = db.getTable(tableName);
		table.putItem(item, expectedCondition);
	}

	/**
	 * get a single record
	 * @param tableName
	 * @param partitionKey
	 * @param partitionValue
	 * @return
	 * @throws Exception
	 */
	public Item getRecord(String tableName, String partitionKey, String partitionValue) throws Exception{
		Table table = db.getTable(tableName);
		return table.getItem(partitionKey, partitionValue);
	}
	
	/**
	 * get all records from a table
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public ItemCollection<ScanOutcome> getAllItems(String tableName) throws Exception{
		Table table = db.getTable(tableName);
		ScanSpec scanSpec = new ScanSpec();
		return table.scan(scanSpec);
	}
	
	//for single condition check
	/**
	 * get records based on single condition check
	 * @param tableName
	 * @param projectExpression
	 * @param filterKey
	 * @param condition
	 * @param filterValue
	 * @return
	 * @throws Exception
	 */
	public ItemCollection<ScanOutcome> getFilteredItems(String tableName, String projectExpression, String filterKey, String condition, Object filterValue) throws Exception{
		Table table = db.getTable(tableName);
		
		Map<String, String> nameMap = new HashMap<>();
		nameMap.put("#filterKey", filterKey);
		
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put(":filterValue", filterValue);
		
		ScanSpec scanSpec = new ScanSpec().withProjectionExpression(projectExpression).withFilterExpression("#filterKey " + condition + " :filterValue").withNameMap(nameMap).withValueMap(valueMap);
		return table.scan(scanSpec);
	}
	
	
	//for multi condition check
	/**
	 * get records based on multiple condition checks
	 * @param tableName
	 * @param nameMap
	 * @param valueMap
	 * @param condition
	 * @return
	 * @throws Exception
	 */
	public ItemCollection<ScanOutcome> getFilteredItems(String tableName, Map<String, String> nameMap, Map<String, Object> valueMap, String condition) throws Exception{
		Table table = db.getTable(tableName);
		ScanSpec scanSpec = new ScanSpec().withFilterExpression(condition).withNameMap(nameMap).withValueMap(valueMap);
		 return table.scan(scanSpec);
	}
	
	/**
	 * get records based on multiple condition checks with pagination
	 * @param tableName
	 * @param nameMap
	 * @param valueMap
	 * @param condition
	 * @param limit
	 * @param lastEvaluatedKeyMap
	 * @return scanResult
	 * @throws Exception
	 */
	public ScanResult getPaginatedFilteredItems(String tableName, Map<String, String> nameMap, Map<String, AttributeValue> valueMap, String condition, Integer limit, Map<String, AttributeValue> lastEvaluatedKeyMap) throws Exception{	
		if (lastEvaluatedKeyMap.get("campaignId").getS().equals("0")) {
			lastEvaluatedKeyMap = null;
		}
		ScanRequest scanRequest = new ScanRequest().withTableName(tableName).withFilterExpression(condition).withExpressionAttributeNames(nameMap).withExpressionAttributeValues(valueMap).withLimit(limit).withExclusiveStartKey(lastEvaluatedKeyMap);
		return client.scan(scanRequest);
	}
	
	/**
	 * <>
	 * @param filterExpression
	 * @param attributeValues
	 * @return
	 */
	public PaginatedScanList<Customer> getFilteredCustomerItems(String filterExpression, Map<String, AttributeValue> expressionAttributeValues) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression().withFilterExpression(filterExpression).withExpressionAttributeValues(expressionAttributeValues);
		return mapper.scan(Customer.class, scanExpression);	
	}
	
	/**
	 * <>
	 * @param keyCondition
	 * @param expressionAttributeValues
	 * @return
	 */
	public List<CampaignFile> getCampaignFilePath(String keyCondition, Map<String, AttributeValue> expressionAttributeValues) {
		DynamoDBMapper mapper = new DynamoDBMapper(client);
		DynamoDBQueryExpression<CampaignFile> queryExpression = new DynamoDBQueryExpression<CampaignFile>().withKeyConditionExpression(keyCondition).withExpressionAttributeValues(expressionAttributeValues);
        return mapper.query(CampaignFile.class, queryExpression);
	}

}
