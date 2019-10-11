package com.conns.organization.management.repository;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Repository;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Expected;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.model.InternalServerErrorException;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.conns.organization.management.configuration.AppConfig;

@Repository
public class DynamoDbDao {
	ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
	AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	DynamoDB db = new DynamoDB(client);

	/**
	 * get a single record
	 * @param tableName
	 * @param partitionKey
	 * @param partitionValue
	 * @return an Item
	 * @throws Exception
	 */
	public Item getRecord(String tableName, String partitionKey, String partitionValue) throws Exception {
		Table table = db.getTable(tableName);
		return table.getItem(partitionKey, partitionValue);
	}
	
	/**
	 * query table items using hashKey
	 * @param tableName
	 * @param hashKey
	 * @return filtered items
	 */
	public ItemCollection<QueryOutcome> getFilteredItems(String tableName, KeyAttribute hashKey) {
		Table table = db.getTable(tableName);
		return table.query(hashKey);
	}
	
	/**
	 * put an item into the table
	 * @param tableName
	 * @param jsonString
	 * @throws Exception
	 */
	public void persistItem(String tableName, String jsonString) throws Exception { 
		Table table = db.getTable(tableName);
		table.putItem(Item.fromJSON(jsonString));
	}
	
	/**
	 * query an index with name and value maps and key condition
	 * @param tableName
	 * @param indexName
	 * @param keyConditionExpression
	 * @param nameMap
	 * @param valueMap
	 * @return filtered items
	 * @throws ProvisionedThroughputExceededException
	 * @throws ResourceNotFoundException
	 * @throws InternalServerErrorException
	 */
	public ItemCollection<QueryOutcome> queryTableWithIndex(String tableName, String indexName, String keyConditionExpression, Map<String, String> nameMap, Map<String, Object> valueMap) 
	           throws ProvisionedThroughputExceededException, ResourceNotFoundException, InternalServerErrorException {
		
		Table table = db.getTable(tableName);
		Index index = table.getIndex(indexName);
		
		QuerySpec spec = new QuerySpec()
							 .withKeyConditionExpression(keyConditionExpression)
							 .withNameMap(nameMap)
							 .withValueMap(valueMap);
		return index.query(spec);
	}
	
	/**
	 * query an index with name and value maps, key condition and filter expression
	 * @param tableName
	 * @param indexName
	 * @param keyConditionExpression
	 * @param filterExpression
	 * @param nameMap
	 * @param valueMap
	 * @return
	 * @throws ProvisionedThroughputExceededException
	 * @throws ResourceNotFoundException
	 * @throws InternalServerErrorException
	 */
	public ItemCollection<QueryOutcome> queryTableWithIndex(String tableName, String indexName, String keyConditionExpression, String filterExpression, Map<String, String> nameMap, Map<String, Object> valueMap) 
	           throws ProvisionedThroughputExceededException, ResourceNotFoundException, InternalServerErrorException {
		
		Table table = db.getTable(tableName);
		Index index = table.getIndex(indexName);
		
		QuerySpec spec = new QuerySpec()
							 .withKeyConditionExpression(keyConditionExpression)
							 .withFilterExpression(filterExpression)
							 .withNameMap(nameMap)
							 .withValueMap(valueMap);
		return index.query(spec);
	}
	
	/**
	 * update item with expected condition
	 * @param tableName
	 * @param expectedCondition
	 * @param table item
	 * @throws Exception
	 */
	public void updateItem(String tableName, Expected expectedCondition, Item item) throws Exception {
		Table table = db.getTable(tableName);
		table.putItem(item, expectedCondition);
	}
	
	/**
	 * get an item using hash key-value
	 * @param tableName
	 * @param hashKeyName
	 * @param hashKeyValue
	 * @return table item
	 * @throws Exception
	 */
	public Item getItem(String tableName, String hashKeyName, String hashKeyValue) throws Exception {
		Table table = db.getTable(tableName);
		return table.getItem(hashKeyName, hashKeyValue);
	}
	
	/**
	 * delete an item using hash key-value and range key-value pairs
	 * @param tableName
	 * @param hashKeyName
	 * @param hashKeyValue
	 * @param rangeKeyName
	 * @param rangeKeyValue
	 * @throws Exception
	 */
	public void deleteItem(String tableName, String hashKeyName, String hashKeyValue, String rangeKeyName, String rangeKeyValue) throws Exception {
		Table table = db.getTable(tableName);
		DeleteItemSpec deleteItemSpec = new DeleteItemSpec().withPrimaryKey(hashKeyName, hashKeyValue, rangeKeyName, rangeKeyValue);
		table.deleteItem(deleteItemSpec);
	}
	
	/**
	 * get an item using hash key-value and range key-value pairs
	 * @param tableName
	 * @param hashKeyName
	 * @param hashKeyValue
	 * @param rangeKeyName
	 * @param rangeKeyValue
	 * @return table item
	 * @throws Exception
	 */
	public Item getItem(String tableName, String hashKeyName, String hashKeyValue, String rangeKeyName, String rangeKeyValue) throws Exception {
		Table table = db.getTable(tableName);
		return table.getItem(hashKeyName, hashKeyValue, rangeKeyName, rangeKeyValue);
	}
	
	/**
	 * scan table for given condition on name-value pair
	 * @param tableName
	 * @param nameMap
	 * @param valueMap
	 * @param condition
	 * @return scanned items
	 * @throws Exception
	 */
	public ItemCollection<ScanOutcome> getFilteredItems(String tableName, Map<String, String> nameMap, Map<String, Object> valueMap, String condition) throws Exception {
		Table table = db.getTable(tableName);
		ScanSpec scanSpec = new ScanSpec().withFilterExpression(condition).withNameMap(nameMap).withValueMap(valueMap);
		return table.scan(scanSpec);
	}
	
	/**
	 * get all records from a table
	 * @param tableName
	 * @return ItemCollection of ScanOutcome
	 * @throws Exception
	 */
	public ItemCollection<ScanOutcome> getAllItems(String tableName, String projectionExpression) throws Exception {
		Table table = db.getTable(tableName);
		ScanSpec scanSpec = new ScanSpec().withProjectionExpression(projectionExpression);
		return table.scan(scanSpec);
	}
}
