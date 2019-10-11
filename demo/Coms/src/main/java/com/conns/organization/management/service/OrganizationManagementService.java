package com.conns.organization.management.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.document.Expected;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.conns.organization.management.configuration.AppConfig;
import com.conns.organization.management.configuration.AppConfigBean;
import com.conns.organization.management.exception.BadRequestException;
import com.conns.organization.management.exception.NotFoundException;
import com.conns.organization.management.model.Address;
import com.conns.organization.management.model.Location;
import com.conns.organization.management.model.LocationTree;
import com.conns.organization.management.model.OrganizationTree;
import com.conns.organization.management.model.Person;
import com.conns.organization.management.model.PersonTree;
import com.conns.organization.management.model.TreeNode;
import com.conns.organization.management.model.UpdatedTreeItem;
import com.conns.organization.management.repository.DynamoDbDao;
import com.conns.organization.management.utilities.Constants;
import com.conns.organization.management.utilities.MandatoryFieldChecker;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class OrganizationManagementService implements Constants {
	private static final Logger logger = LoggerFactory.getLogger(OrganizationManagementService.class);

	ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
	AppConfigBean configBean = (AppConfigBean) applicationContext.getBean("getProfile");

	@Autowired
	private DynamoDbDao dynamoDao;

	/**
	 * get master data for given type
	 * 
	 * @param type - master types (example: states, countries, etc)
	 * @return master data
	 * @throws Exception
	 */
	public String retrieveMasterData(String type) throws Exception {
		Item item = dynamoDao.getRecord(configBean.getMasterTable(), "id", type);
		if (item == null) {
			logger.error("retrieveMasterData - BadRequest - item is NULL \n");
			throw new BadRequestException(BAD_REQUEST);
		}
		return item.getJSON("items");
	}

	/**
	 * 
	 * @param domain
	 * @param level
	 * @return
	 * @throws Exception
	 */
	public String retrieveDataForTableView(String domain, String level) throws Exception {
		Map<String, String> nameMap = new HashMap<>();
		Map<String, Object> valueMap = new HashMap<>();
		String keyConditionExpression = "#domain = :domain";
		nameMap.put("#domain", "domain");
		valueMap.put(":domain", domain);
		Integer levelInt = Integer.parseInt(level);
		if (levelInt >= 0) {
			nameMap.put("#level", "level");
			valueMap.put(":level", levelInt);
			keyConditionExpression = keyConditionExpression + " AND #level = :level";
		}
		ItemCollection<QueryOutcome> items = dynamoDao.queryTableWithIndex(configBean.getSpreadsheetTable(),
				configBean.getDomainLevelIndex(), keyConditionExpression, nameMap, valueMap);
		List<String> nodes = new ArrayList<>();
		items.forEach(item -> nodes.add(item.toJSON()));
		return "{\"Items\":" + nodes.toString() + "}";
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public String retrieveAllEmployees() throws Exception {
		String projectionExpression = "employeeUniqueId, firstName, middleName, lastName";
		ItemCollection<ScanOutcome> items = dynamoDao.getAllItems(configBean.getPersonTable(), projectionExpression);
		List<String> nodes = new ArrayList<>();
		items.forEach(item -> nodes.add(item.toJSON()));
		return "{\"Items\":" + nodes.toString() + "}";
	}

	/**
	 * get tree nodes for a given parentCommonId
	 * 
	 * @param parentCommonId
	 * @return tree node items as String
	 * @throws Exception
	 */
	public String retrieveOrganizationTree(String parentCommonId) throws Exception {
		KeyAttribute hashKey = new KeyAttribute("parentCommonId", parentCommonId);
		ItemCollection<QueryOutcome> items = dynamoDao.getFilteredItems(configBean.getTreeTable(), hashKey);
		List<String> nodes = new ArrayList<>();
		items.forEach(item -> nodes.add(item.toJSON()));
		return "{\"Items\":" + nodes.toString() + "}";
	}

	/**
	 * get employee info for a given employeeUniqueId
	 * 
	 * @param employeeUniqueId
	 * @return employee item as String
	 * @throws Exception
	 */
	public String retrieveEmployeeDetails(String employeeUniqueId) throws Exception {
		return dynamoDao.getItem(configBean.getPersonTable(), "employeeUniqueId", employeeUniqueId).toJSON();

	}

	/**
	 * get location info for a given locationUniqueId
	 * 
	 * @param locationUniqueId
	 * @return location item as String
	 * @throws Exception
	 */
	public String retrieveLocationDetails(String locationUniqueId) throws Exception {
		return dynamoDao.getItem(configBean.getLocationTable(), "locationUniqueId", locationUniqueId).toJSON();
	}

	/**
	 * get tree nodes for a given status (position)
	 * 
	 * @param status
	 * @return list of tree nodes as String
	 * @throws Exception
	 */
	public String retrieveNodes(String status, String domain) throws Exception {
		Map<String, String> nameMap = new HashMap<>();
		Map<String, Object> valueMap = new HashMap<>();
		nameMap.put("#status", "status");
		valueMap.put(":status", status);
		nameMap.put("#domain", "domain");
		valueMap.put(":domain", domain);
		String keyConditionExpression = "#status = :status";
		String filterExpression = "#domain = :domain";
		ItemCollection<QueryOutcome> items = dynamoDao.queryTableWithIndex(configBean.getTreeTable(),
				configBean.getStatusIndex(), keyConditionExpression, filterExpression, nameMap, valueMap);
		List<String> nodes = new ArrayList<>();
		items.forEach(item -> nodes.add(item.toJSON()));
		return "{\"Items\":" + nodes.toString() + "}";
	}

	/**
	 * get tree nodes for a given status (position)
	 * 
	 * @param status
	 * @return list of tree nodes as String
	 * @throws Exception
	 */
	public String retrieveNodes(String status) throws Exception {
		return getRecords("status", status, configBean.getStatusIndex());
	}

	/**
	 * get locations for a specific category
	 * 
	 * @param level
	 * @return list of locations as String
	 * @throws Exception
	 */
	public String retrieveLocations(String category) throws Exception {
		return getRecords("category", category, configBean.getCategoryIndex());
	}

	/**
	 * get records for a given key-value pair using index
	 * 
	 * @param key
	 * @param value
	 * @param index
	 * @return
	 * @throws Exception
	 */
	private String getRecords(String key, String value, String index) throws Exception {
		Map<String, String> nameMap = new HashMap<>();
		Map<String, Object> valueMap = new HashMap<>();
		nameMap.put("#filterKey", key);
		valueMap.put(":filterValue", value);
		String keyConditionExpression = "#filterKey = :filterValue";
		ItemCollection<QueryOutcome> items = dynamoDao.queryTableWithIndex(configBean.getTreeTable(), index,
				keyConditionExpression, nameMap, valueMap);
		List<String> nodes = new ArrayList<>();
		items.forEach(item -> nodes.add(item.toJSON()));
		return "{\"Items\":" + nodes.toString() + "}";
	}

	/**
	 * save employee
	 * 
	 * @param employee
	 * @throws Exception
	 */
	public Person persistEmployee(Person employee) throws Exception {
		String domain;
		String parentCommonId;

		Set<String> delivery = new HashSet<String>();
		delivery.add("VPLOGIST");
		delivery.add("DIRDIS");
		delivery.add("WHCRSDKOP");
		delivery.add("WHFACLMGR");
		delivery.add("WHSEMGR");
		delivery.add("WHCDMGR");
		delivery.add("WHFACLMGR");
		delivery.add("WHGWA1");
		delivery.add("WHSEMGR");
		delivery.add("WHXDOKSUP");
		delivery.add("DELIVLEAD");
		delivery.add("DELIVMGR");
		delivery.add("DELIVSUPR");
		delivery.add("WHDOCKLD");
		delivery.add("WHGWA1");
		delivery.add("WHSELEAD");
		delivery.add("WHSEMGR");

		Set<String> contactCentre = new HashSet<String>();
		contactCentre.add("CUSSRV1FT");
		contactCentre.add("CUSSRV1PT");
		contactCentre.add("CUSSRV2");
		contactCentre.add("CUSSRVMGR");
		contactCentre.add("CUSSRVSMG");
		contactCentre.add("VPCE");

		Set<String> retail = new HashSet<String>();
		retail.add("COO");
		retail.add("SALESRVP");
		retail.add("SALESDMGR");
		retail.add("SALESMGR");
		retail.add("CASHIER");
		retail.add("SALASTMGR");
		retail.add("SALECLMG");
		retail.add("SALEOPMGR");
		retail.add("SALESPERS");
		retail.add("SALPORTER");
		retail.add("SLSHRLYOP");
		retail.add("SLSTHIRDK");
		retail.add("SALEOPMIT");
		retail.add("SALESMIT");
		retail.add("SALYLS");
		retail.add("SALASTMIT");
		retail.add("SALESLDSP");
		retail.add("SALECOMHR");
		retail.add("SALAREAMG");
		retail.add("ADMINEX");

		Set<String> service = new HashSet<String>();
		service.add("VPSERVICE");
		service.add("SRVDIRECT");
		service.add("SRVMGRCHA");
		service.add("SRVMGRDAL");
		service.add("SRVMGRDCO");
		service.add("SRVMGRELP");
		service.add("SRVMGRGT");
		service.add("SRVMGRHST");
		service.add("SRVMGRMCA");
		service.add("SRVMGRPAZ");
		service.add("SRVMGRSA");
		service.add("SRVOTEOTA");
		service.add("SRVTEASST");
		service.add("SRVTECAPP");
		service.add("SRVTECCOM");
		service.add("SRVTECELC");
		service.add("SRVTECFUR");
		service.add("SRVTECLD");
		service.add("SRVTECUNI");
		service.add("SRVTEINAP");
		service.add("SRVTEINEL");

		if (delivery.contains(employee.getJobCode())) {
			parentCommonId = ORPHAN_DELIVERY_PARENT_COMMON_ID;
			domain = DOMAIN_DELIVERY;
		} else if (contactCentre.contains(employee.getJobCode())) {
			parentCommonId = ORPHAN_CONTACT_CENTRE_PARENT_COMMON_ID;
			domain = DOMAIN_CONTACT_CENTER;
		} else if (retail.contains(employee.getJobCode())) {
			parentCommonId = ORPHAN_SALES_PARENT_COMMON_ID;
			domain = DOMAIN_RETAIL;
		} else if (service.contains(employee.getJobCode())) {
			parentCommonId = ORPHAN_SERVICE_PARENT_COMMON_ID;
			domain = DOMAIN__SERVICE;
		} else {
			parentCommonId = ORPHAN_PARENT_COMMON_ID;
			domain = DOMAIN_ORPHANS;
		}

		MandatoryFieldChecker.isNull(employee.getEmployeeId().trim());
		MandatoryFieldChecker.isNull(employee.getFirstName().trim());
		String employeeUniqueId = UUID.randomUUID().toString();
		employee.setEmployeeUniqueId(employeeUniqueId);
		employee.setDomain(StringUtils.capitalize(domain));
		DateTime today = new DateTime(DateTimeZone.UTC);
		employee.setCreatedDate(today.toString());
		employee.setLastModifiedDate(today.toString());
		employee.setUiStatus(UI_STATUS_UPDATED);
		dynamoDao.persistItem(configBean.getPersonTable(),
				new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(employee));
		assignEmployeeToOrphan(employee, employeeUniqueId, parentCommonId, domain, today.toString());

		return employee;
	}

	private void assignEmployeeToOrphan(Person employee, String employeeUniqueId, String parentCommonId, String domain,
			String today) throws JsonProcessingException, Exception {
		/*
		 * Set<String> delivery = new HashSet<String>(); delivery.add("VPLOGIST");
		 * delivery.add("DIRDIS"); delivery.add("WHCRSDKOP"); delivery.add("WHFACLMGR");
		 * delivery.add("WHSEMGR"); delivery.add("WHCDMGR"); delivery.add("WHFACLMGR");
		 * delivery.add("WHGWA1"); delivery.add("WHSEMGR"); delivery.add("WHXDOKSUP");
		 * delivery.add("DELIVLEAD"); delivery.add("DELIVMGR");
		 * delivery.add("DELIVSUPR"); delivery.add("WHDOCKLD"); delivery.add("WHGWA1");
		 * delivery.add("WHSELEAD"); delivery.add("WHSEMGR");
		 * 
		 * Set<String> contactCentre = new HashSet<String>();
		 * contactCentre.add("CUSSRV1FT"); contactCentre.add("CUSSRV1PT");
		 * contactCentre.add("CUSSRV2"); contactCentre.add("CUSSRVMGR");
		 * contactCentre.add("CUSSRVSMG"); contactCentre.add("VPCE");
		 * 
		 * Set<String> retail = new HashSet<String>(); retail.add("COO");
		 * retail.add("SALESRVP"); retail.add("SALESDMGR"); retail.add("SALESMGR");
		 * retail.add("CASHIER"); retail.add("SALASTMGR"); retail.add("SALECLMG");
		 * retail.add("SALEOPMGR"); retail.add("SALESPERS"); retail.add("SALPORTER");
		 * retail.add("SLSHRLYOP"); retail.add("SLSTHIRDK"); retail.add("SALEOPMIT");
		 * retail.add("SALESMIT"); retail.add("SALYLS"); retail.add("SALASTMIT");
		 * retail.add("SALESLDSP"); retail.add("SALECOMHR"); retail.add("SALAREAMG");
		 * retail.add("ADMINEX");
		 * 
		 * Set<String> service = new HashSet<String>(); service.add("VPSERVICE");
		 * service.add("SRVDIRECT"); service.add("SRVMGRCHA"); service.add("SRVMGRDAL");
		 * service.add("SRVMGRDCO"); service.add("SRVMGRELP"); service.add("SRVMGRGT");
		 * service.add("SRVMGRHST"); service.add("SRVMGRMCA"); service.add("SRVMGRPAZ");
		 * service.add("SRVMGRSA"); service.add("SRVOTEOTA"); service.add("SRVTEASST");
		 * service.add("SRVTECAPP"); service.add("SRVTECCOM"); service.add("SRVTECELC");
		 * service.add("SRVTECFUR"); service.add("SRVTECLD"); service.add("SRVTECUNI");
		 * service.add("SRVTEINAP"); service.add("SRVTEINEL");
		 * 
		 * OrganizationTree orgTree = null; if(delivery.contains(employee.getJobCode()))
		 * { orgTree = new OrganizationTree();
		 * orgTree.setParentCommonId(ORPHAN_DELIVERY_PARENT_COMMON_ID);
		 * orgTree.setDomain(DOMAIN_DELIVERY); } else
		 * if(contactCentre.contains(employee.getJobCode())) { orgTree = new
		 * OrganizationTree();
		 * orgTree.setParentCommonId(ORPHAN_CONTACT_CENTRE_PARENT_COMMON_ID);
		 * orgTree.setDomain(DOMAIN_CONTACT_CENTER); } else
		 * if(retail.contains(employee.getJobCode())) { orgTree = new
		 * OrganizationTree(); orgTree.setParentCommonId(ORPHAN_SALES_PARENT_COMMON_ID);
		 * orgTree.setDomain(DOMAIN_RETAIL); } else if
		 * (service.contains(employee.getJobCode())) { orgTree = new OrganizationTree();
		 * orgTree.setParentCommonId(ORPHAN_SERVICE_PARENT_COMMON_ID);
		 * orgTree.setDomain(DOMAIN__SERVICE); } else { orgTree = new
		 * OrganizationTree(); orgTree.setParentCommonId(ORPHAN_PARENT_COMMON_ID);
		 * orgTree.setDomain(DOMAIN_ORPHANS); }
		 */

		OrganizationTree orgTree = new OrganizationTree();
		orgTree.setCommonId(UUID.randomUUID().toString());
		orgTree.setDomain(domain);
		orgTree.setParentCommonId(parentCommonId);
		orgTree.setCategory(CATEGORY_EMPLOYEE);
		orgTree.setCreatedDate(today);
		orgTree.setLastModifiedDate(today);
		orgTree.setLeaf(true);
		orgTree.setLocation(new LocationTree(DEFAULT, STATUS_OPEN));
		orgTree.setStatus(DOMAIN_ORPHANS);
		orgTree.setType(NODE_TYPE_PEOPLE);
		orgTree.setParentEmployeeName(STATUS_OPEN);
		orgTree.setEmployeeUniqueId(employeeUniqueId);
		orgTree.setLocationUniqueId(DEFAULT);
		orgTree.setEmpUiStatus(UI_STATUS_UPDATED);

		PersonTree emp = new PersonTree();
		emp.setEmployeeUniqueId(employeeUniqueId);
		String fullName = "";
		if (employee.getFirstName() != null) {
			fullName = employee.getFirstName();
		}
		if (employee.getMiddleName() != null) {
			fullName = fullName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			fullName = fullName + " " + employee.getLastName();
		}
		emp.setName(fullName);
		emp.setEmployeeId(employee.getEmployeeId());
		emp.setPositionId(employeeUniqueId);
		emp.setPosition(STATUS_OPEN);
		emp.setLevel(Integer.parseInt(DEFAULT));
		orgTree.setEmployee(emp);

		dynamoDao.persistItem(configBean.getTreeTable(),
				new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(orgTree));

	}

	/**
	 * save location
	 * 
	 * @param location
	 * @throws Exception
	 */
	public Location persistLocation(Location location) throws Exception {
		MandatoryFieldChecker.isNull(location.getName().trim());
		MandatoryFieldChecker.isNull(location.getLocationCode().trim());
		String locationUniqueId = UUID.randomUUID().toString();
		location.setLocationUniqueId(locationUniqueId);
		location.setDomain(StringUtils.capitalize(location.getDomain()));
		DateTime today = new DateTime(DateTimeZone.UTC);
		location.setCreatedDate(today.toString());
		location.setLastModifiedDate(today.toString());

		dynamoDao.persistItem(configBean.getLocationTable(),
				new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(location));

		OrganizationTree orgTree = new OrganizationTree();
		orgTree.setParentCommonId(ORPHAN_PARENT_COMMON_ID);
		orgTree.setCommonId(UUID.randomUUID().toString());
		orgTree.setDomain(DOMAIN_ORPHANS);
		orgTree.setLeaf(true);
		orgTree.setCreatedDate(today.toString());
		orgTree.setLastModifiedDate(today.toString());
		orgTree.setLocationUniqueId(locationUniqueId);
		orgTree.setEmployeeUniqueId(DEFAULT);
		orgTree.setParentEmployeeName(STATUS_OPEN);

		// Based on the discussion with Harshith if EmployeeUiStatus is updated this
		// will fetch the Employee record to Hive.
		// Updated on the 12/14/2018
		orgTree.setEmpUiStatus(UI_STATUS_UPDATED);

		LocationTree loc = new LocationTree();
		loc.setLocationUniqueId(locationUniqueId);
		loc.setName(location.getName().trim());
		orgTree.setLocation(loc);

		PersonTree emp = new PersonTree();
		emp.setEmployeeId(DEFAULT);
		emp.setEmployeeUniqueId(DEFAULT);
		emp.setName(STATUS_OPEN);
		emp.setPosition(STATUS_OPEN);
		emp.setPositionId(DEFAULT);
		orgTree.setEmployee(emp);

		dynamoDao.persistItem(configBean.getTreeTable(),
				new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(orgTree));

		return location;
	}

	/**
	 * move an employee
	 * 
	 * @param fromParentCommonId
	 * @param fromCommonId
	 * @param toParentCommonId
	 * @param toCommonId
	 * @throws NotFoundException
	 * @throws BadRequestException
	 * @throws Exception
	 */
	public void moveEmployee(String fromParentCommonId, String fromCommonId, String toParentCommonId, String toCommonId)
			throws NotFoundException, BadRequestException, Exception {
		OrganizationTree fromNode = new ObjectMapper().readValue(retrieveNode(fromParentCommonId, fromCommonId),
				OrganizationTree.class);
		if (fromNode == null) {
			logger.error("moveEmployee failed " + NO_TREE_NODE + " fromParentCommonId " + fromParentCommonId
					+ " fromCommonId " + fromCommonId + " \n");
			throw new NotFoundException(
					NO_TREE_NODE + " fromParentCommonId " + fromParentCommonId + " and fromCommonId " + fromCommonId);
		}

		OrganizationTree toNode = new ObjectMapper().readValue(retrieveNode(toParentCommonId, toCommonId),
				OrganizationTree.class);
		if (toNode == null) {
			logger.error("moveEmployee failed " + NO_TREE_NODE + " toParentCommonId " + toParentCommonId
					+ " toCommonId " + toCommonId + " \n");
			throw new BadRequestException(
					NO_TREE_NODE + " toParentCommonId " + toParentCommonId + " and toCommonId " + toCommonId);
		}

		if (!fromNode.getLeaf()) {
			if (!toNode.getStatus().equalsIgnoreCase(STATUS_OPEN)) {
				logger.error("moveEmployee failed " + NOT_OPEN_POSTITION + " \n");
				throw new BadRequestException(NOT_OPEN_POSTITION);
			}
		}

		String employeeUniqueId = fromNode.getEmployee().getEmployeeUniqueId();
		PersonTree emp = toNode.getEmployee();
		emp.setEmployeeUniqueId(employeeUniqueId);
		emp.setEmployeeId(fromNode.getEmployee().getEmployeeId());
		emp.setName(fromNode.getEmployee().getName());
		toNode.setEmployee(emp);
		toNode.setEmployeeUniqueId(employeeUniqueId);
		toNode.setStatus(STATUS_OCCUPIED);
		toNode.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());
		toNode.setEmpUiStatus(UI_STATUS_UPDATED);

		Item itemToUpdate = Item
				.fromJSON(new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(toNode));
		Expected expectedCondition = new Expected("commonId").eq(toNode.getCommonId());
		dynamoDao.updateItem(configBean.getTreeTable(), expectedCondition, itemToUpdate);

		String parentCommonId = fromNode.getParentCommonId();
		if (parentCommonId.equals(ORPHAN_PARENT_COMMON_ID) || parentCommonId.equals(ORPHAN_DELIVERY_PARENT_COMMON_ID)
				|| parentCommonId.equals(ORPHAN_CONTACT_CENTRE_PARENT_COMMON_ID)
				|| parentCommonId.equals(ORPHAN_SALES_PARENT_COMMON_ID)
				|| parentCommonId.equals(ORPHAN_SERVICE_PARENT_COMMON_ID)) {
			dynamoDao.deleteItem(configBean.getTreeTable(), "parentCommonId", fromParentCommonId, "commonId",
					fromCommonId);
		} else {
			emp = fromNode.getEmployee();
			emp.setEmployeeUniqueId(DEFAULT);
			emp.setEmployeeId(DEFAULT);
			emp.setName(STATUS_OPEN);
			fromNode.setEmployee(emp);
			fromNode.setEmployeeUniqueId(DEFAULT);
			fromNode.setStatus(STATUS_OPEN);
			fromNode.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());
			fromNode.setEmpUiStatus(UI_STATUS_UPDATED);

			itemToUpdate = Item.fromJSON(
					new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(fromNode));
			expectedCondition = new Expected("commonId").eq(fromNode.getCommonId());
			dynamoDao.updateItem(configBean.getTreeTable(), expectedCondition, itemToUpdate);
		}

		Person employee = new ObjectMapper().readValue(
				dynamoDao.getItem(configBean.getPersonTable(), "employeeUniqueId", employeeUniqueId).toJSON(),
				Person.class);
		employee.setRoleEntity(toNode.getEmployee().getPosition());

		itemToUpdate = Item
				.fromJSON(new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(employee));
		expectedCondition = new Expected("employeeUniqueId").eq(employeeUniqueId);
		dynamoDao.updateItem(configBean.getPersonTable(), expectedCondition, itemToUpdate);

		// Update the parent name and parent position Id on the child nodes of toNode --
		// code change made by Rama
		if (!toNode.getLeaf()) {
			String childCommonId = toNode.getCommonId();
			KeyAttribute hashKey = new KeyAttribute("parentCommonId", childCommonId);
			ItemCollection<QueryOutcome> items = dynamoDao.getFilteredItems(configBean.getTreeTable(), hashKey);

			if (items != null) {
				for (Item node : items) {
					String comId = node.getString("commonId");
					String parentId = node.getString("parentCommonId");

					OrganizationTree childNode = new ObjectMapper().readValue(retrieveNode(parentId, comId),
							OrganizationTree.class);
					String parentEmpName = toNode.getEmployee().getName();
					childNode.setParentEmployeeName(parentEmpName);
					childNode.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());
					childNode.setEmpUiStatus(UI_STATUS_UPDATED);
					Item childItemToUpdate = Item.fromJSON(new ObjectMapper()
							.setSerializationInclusion(Include.NON_NULL).writeValueAsString(childNode));
					expectedCondition = new Expected("commonId").eq(childNode.getCommonId());
					dynamoDao.updateItem(configBean.getTreeTable(), expectedCondition, childItemToUpdate);
				}
			}
		}

		// Update the parent name and parent position Id on the child nodes of fromNode
		// -- code change made by Rama
		if (!toNode.getLeaf()) {
			String childCommonId = fromNode.getCommonId();
			KeyAttribute hashKey = new KeyAttribute("parentCommonId", childCommonId);
			ItemCollection<QueryOutcome> items = dynamoDao.getFilteredItems(configBean.getTreeTable(), hashKey);

			if (items != null) {
				for (Item node : items) {
					String comId = node.getString("commonId");
					String parentId = node.getString("parentCommonId");

					OrganizationTree childNode = new ObjectMapper().readValue(retrieveNode(parentId, comId),
							OrganizationTree.class);
					String parentEmpName = fromNode.getEmployee().getName();
					childNode.setParentEmployeeName(parentEmpName);
					childNode.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());
					childNode.setEmpUiStatus(UI_STATUS_UPDATED);
					Item childItemToUpdate = Item.fromJSON(new ObjectMapper()
							.setSerializationInclusion(Include.NON_NULL).writeValueAsString(childNode));
					expectedCondition = new Expected("commonId").eq(childNode.getCommonId());
					dynamoDao.updateItem(configBean.getTreeTable(), expectedCondition, childItemToUpdate);
				}
			}
		}
	}

	/**
	 * copy employee to an location
	 * 
	 * @param parentCommonId
	 * @param commonId
	 * @param employee
	 * @throws NotFoundException
	 * @throws BadRequestException
	 * @throws Exception
	 */

	public void copyEmployee(String parentCommonId, String commonId, PersonTree employee)
			throws NotFoundException, BadRequestException, Exception {
		OrganizationTree node = new ObjectMapper().readValue(retrieveNode(parentCommonId, commonId),
				OrganizationTree.class);
		if (node == null) {
			logger.error("copyEmployee failed " + NO_TREE_NODE + " parentCommonId " + parentCommonId + " commonId "
					+ commonId + " \n");
			throw new NotFoundException(
					NO_TREE_NODE + " parentCommonId " + parentCommonId + " and commonId " + commonId);
		}

		if (!node.getLeaf()) {
			if (!node.getStatus().equalsIgnoreCase(STATUS_OPEN)) {
				logger.error("copyEmployee failed " + NOT_OPEN_POSTITION + " \n");
				throw new BadRequestException(NOT_OPEN_POSTITION);
			}
		}
		PersonTree emp = node.getEmployee();
		emp.setEmployeeUniqueId(employee.getEmployeeUniqueId());
		emp.setEmployeeId(employee.getEmployeeId());
		emp.setName(employee.getName());
		node.setEmployee(emp);
		node.setEmployeeUniqueId(employee.getEmployeeUniqueId());
		node.setStatus(STATUS_OCCUPIED);
		node.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());
		node.setEmpUiStatus(UI_STATUS_UPDATED);

		Item itemToUpdate = Item
				.fromJSON(new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(node));
		Expected expectedCondition = new Expected("commonId").eq(node.getCommonId());
		dynamoDao.updateItem(configBean.getTreeTable(), expectedCondition, itemToUpdate);

		// Update the parent name and parent position Id on the child nodes of toNode --
		// code change made by Rama
		if (!node.getLeaf()) {
			String childCommonId = node.getCommonId();
			KeyAttribute hashKey = new KeyAttribute("parentCommonId", childCommonId);
			ItemCollection<QueryOutcome> items = dynamoDao.getFilteredItems(configBean.getTreeTable(), hashKey);

			if (items != null) {
				for (Item item : items) {
					String comId = item.getString("commonId");
					String parentId = item.getString("parentCommonId");

					OrganizationTree childNode = new ObjectMapper().readValue(retrieveNode(parentId, comId),
							OrganizationTree.class);
					String parentEmpName = node.getEmployee().getName();
					childNode.setParentEmployeeName(parentEmpName);
					childNode.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());
					childNode.setEmpUiStatus(UI_STATUS_UPDATED);
					Item childItemToUpdate = Item.fromJSON(new ObjectMapper()
							.setSerializationInclusion(Include.NON_NULL).writeValueAsString(childNode));
					expectedCondition = new Expected("commonId").eq(childNode.getCommonId());
					dynamoDao.updateItem(configBean.getTreeTable(), expectedCondition, childItemToUpdate);
				}
			}
		}
	}

	/**
	 * update employee details in employee and tree tables
	 * 
	 * @param employee
	 * @param parentCommonId
	 * @param commonId
	 * @throws NotFoundException
	 * @throws Exception
	 */
	public void updateEmployee(Person employee, String parentCommonId, String commonId)
			throws NotFoundException, Exception {
		updateEmployee(employee);

		OrganizationTree node = new ObjectMapper().readValue(retrieveNode(parentCommonId, commonId),
				OrganizationTree.class);
		if (node == null) {
			logger.error("updateEmployee failed " + NO_TREE_NODE + " parentCommonId " + parentCommonId + " commonId "
					+ commonId + " \n");
			throw new NotFoundException(
					NO_TREE_NODE + " parentCommonId " + parentCommonId + " and commonId " + commonId);
		}
		node.getEmployee().setEmployeeUniqueId(employee.getEmployeeUniqueId().trim());
		node.getEmployee().setEmployeeId(employee.getEmployeeId());
		String fullName = "";
		if (employee.getFirstName() != null) {
			fullName = employee.getFirstName();
		}
		if (employee.getMiddleName() != null) {
			fullName = fullName + " " + employee.getMiddleName();
		}
		if (employee.getLastName() != null) {
			fullName = fullName + " " + employee.getLastName();
		}
		node.setDomain(StringUtils.capitalize(node.getDomain()));
		node.getEmployee().setName(fullName.trim());
		node.getEmployee().setPosition(employee.getRoleEntity());
		node.setEmployeeUniqueId(employee.getEmployeeUniqueId().trim());
		node.setEmpUiStatus(UI_STATUS_UPDATED);
		node.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());

		Item itemToUpdate = Item
				.fromJSON(new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(node));
		Expected expectedCondition = new Expected("commonId").eq(commonId.trim());
		dynamoDao.updateItem(configBean.getTreeTable(), expectedCondition, itemToUpdate);
	}

	/**
	 * update employee details in employee table
	 * 
	 * @param employee
	 * @throws Exception
	 * @throws JsonProcessingException
	 */
	private void updateEmployee(Person employee) throws JsonProcessingException, Exception {
		MandatoryFieldChecker.isNull(employee.getEmployeeUniqueId().trim());
		employee.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());
		employee.setUiStatus(UI_STATUS_UPDATED);

		Item itemToUpdate = Item
				.fromJSON(new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(employee));
		Expected expectedCondition = new Expected("employeeUniqueId").eq(employee.getEmployeeUniqueId());
		dynamoDao.updateItem(configBean.getPersonTable(), expectedCondition, itemToUpdate);
	}

	/**
	 * update location details in location and tree tables
	 * 
	 * @param location
	 * @param parentCommonId
	 * @param commonId
	 * @throws NotFoundException
	 * @throws Exception
	 */
	public void updateLocation(Location location, String parentCommonId, String commonId)
			throws NotFoundException, Exception {
		MandatoryFieldChecker.isNull(location.getLocationUniqueId().trim());
		location.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());
		location.setUiStatus(UI_STATUS_UPDATED);

		Item itemToUpdate = Item
				.fromJSON(new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(location));
		Expected expectedCondition = new Expected("locationUniqueId").eq(location.getLocationUniqueId().trim());
		dynamoDao.updateItem(configBean.getLocationTable(), expectedCondition, itemToUpdate);

		OrganizationTree node = new ObjectMapper().readValue(retrieveNode(parentCommonId, commonId),
				OrganizationTree.class);
		if (node == null) {
			logger.error("updateLocation failed " + NO_TREE_NODE + " parentCommonId " + parentCommonId + " commonId "
					+ commonId + " \n");
			throw new NotFoundException(
					NO_TREE_NODE + " parentCommonId " + parentCommonId + " and commonId " + commonId);
		}
		node.getLocation().setLocationUniqueId(location.getLocationUniqueId().trim());
		node.getLocation().setName(location.getName().trim());
		node.setDomain(StringUtils.capitalize(node.getDomain()));
		node.setLocationUniqueId(location.getLocationUniqueId().trim());
		node.setType(location.getLocationType());
		node.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());
		node.setLocUiStatus(UI_STATUS_UPDATED);

		itemToUpdate = Item
				.fromJSON(new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(node));
		expectedCondition = new Expected("commonId").eq(commonId.trim());
		dynamoDao.updateItem(configBean.getTreeTable(), expectedCondition, itemToUpdate);
	}

	/**
	 * retrieve a single node from the tree table
	 * 
	 * @param parentCommonId
	 * @param commonId
	 * @return tree node as String
	 * @throws Exception
	 */
	private String retrieveNode(String parentCommonId, String commonId) throws Exception {
		return dynamoDao.getItem(configBean.getTreeTable(), "parentCommonId", parentCommonId, "commonId", commonId)
				.toJSON();
	}

	/**
	 * move a location
	 * 
	 * @param parentCommonId
	 * @param commonId
	 * @param toParentCommonId
	 * @param toCommonId
	 * @throws NotFoundException
	 * @throws Exception
	 */
	public void moveLocation(String parentCommonId, String commonId, String toParentCommonId, String toCommonId)
			throws NotFoundException, Exception {
		OrganizationTree node = new ObjectMapper().readValue(retrieveNode(parentCommonId, commonId),
				OrganizationTree.class);
		if (node == null) {
			logger.error("moveLocation failed - " + NO_TREE_NODE + " parentCommonId " + parentCommonId + " commonId "
					+ commonId + " \n");
			throw new NotFoundException(
					NO_TREE_NODE + " parentCommonId " + parentCommonId + " and commonId " + commonId);
		}

		OrganizationTree toNode = new ObjectMapper().readValue(retrieveNode(toParentCommonId, toCommonId),
				OrganizationTree.class);
		if (toNode == null) {
			logger.error("moveLocation failed - " + NO_TREE_NODE + " toParentCommonId " + toParentCommonId
					+ " toCommonId " + toCommonId + " \n");
			throw new NotFoundException(
					NO_TREE_NODE + " toParentCommonId " + toParentCommonId + " and toCommonId " + toCommonId);
		}
		String parentPositionId = toNode.getEmployee().getPositionId();
		String parentEmployeeName = toNode.getEmployee().getName();
		node.setParentCommonId(toNode.getCommonId());
		node.getEmployee().setParentPositionId(parentPositionId);
		node.setParentEmployeeName(parentEmployeeName);
		node.setEmpUiStatus(UI_STATUS_UPDATED);
		node.setLocUiStatus(UI_STATUS_UPDATED);
		node.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());

		dynamoDao.persistItem(configBean.getTreeTable(),
				new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(node));
		dynamoDao.deleteItem(configBean.getTreeTable(), "parentCommonId", parentCommonId, "commonId", commonId);

		toNode.setEmpUiStatus(UI_STATUS_UPDATED);

		Item itemToUpdate = Item
				.fromJSON(new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(toNode));
		Expected expectedCondition = new Expected("commonId").eq(toNode.getCommonId());
		dynamoDao.updateItem(configBean.getTreeTable(), expectedCondition, itemToUpdate);

	}

	/**
	 * close a given store and update the closeDate of location item in the location
	 * tree. if a store node has child nodes attached, then don't allow closeStore
	 * operation.
	 * 
	 * @param parentCommonId
	 * @param commonId
	 * @throws NotFoundException
	 * @throws BadRequestException
	 * @throws Exception
	 */
	public void closeStore(String parentCommonId, String commonId)
			throws NotFoundException, BadRequestException, Exception {
		OrganizationTree node = new ObjectMapper().readValue(retrieveNode(parentCommonId, commonId),
				OrganizationTree.class);
		if (node == null) {
			logger.error("closeStore failed - " + NO_TREE_NODE + " parentCommonId " + parentCommonId + " commonId "
					+ commonId + " \n");
			throw new NotFoundException(
					NO_TREE_NODE + " parentCommonId " + parentCommonId + " and commonId " + commonId);
		}

		ItemCollection<QueryOutcome> items = dynamoDao.getFilteredItems(configBean.getTreeTable(),
				new KeyAttribute("parentCommonId", commonId));
		if (items.getAccumulatedItemCount() > 0) {
			logger.error("closeStore failed - " + CANNOT_CLOSE_STORE + " \n");
			throw new BadRequestException(CANNOT_CLOSE_STORE);
		}
		String locationUniqueId = node.getLocation().getLocationUniqueId();
		dynamoDao.deleteItem(configBean.getTreeTable(), "parentCommonId", parentCommonId, "commonId", commonId);

		Location location = new ObjectMapper().readValue(retrieveLocation(locationUniqueId), Location.class);
		String today = new DateTime(DateTimeZone.UTC).toString();
		location.setCloseDate(today);
		location.setLastModifiedDate(today);
		location.setUiStatus(UI_STATUS_UPDATED);

		Item itemToUpdate = Item
				.fromJSON(new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(location));
		Expected expectedCondition = new Expected("locationUniqueId").eq(location.getLocationUniqueId().trim());
		dynamoDao.updateItem(configBean.getLocationTable(), expectedCondition, itemToUpdate);
	}

	/**
	 * location details of the given location
	 * 
	 * @param locationUniqueId
	 * @return location item as String
	 * @throws Exception
	 */
	private String retrieveLocation(String locationUniqueId) throws Exception {
		return dynamoDao.getItem(configBean.getLocationTable(), "locationUniqueId", locationUniqueId).toJSON();
	}

	/**
	 * retrieve all the updated items from tree, employee, and location tables where
	 * uiStatus = 'Updated'
	 * 
	 * @param type
	 * @return updated items as String
	 * @throws BadRequestException
	 * @throws Exception
	 */
	public String retrieveUpdatedRecords(String type) throws BadRequestException, Exception {
		if (type.equalsIgnoreCase(TYPE_EMPLOYEE) || type.equalsIgnoreCase(TYPE_LOCATION)) {
			return retrieveUpdatedEmployeeLocationRecords(type);
		} else if (type.equalsIgnoreCase(TYPE_EMPLOYEE_NODE) || type.equalsIgnoreCase(TYPE_LOCATION_NODE)) {
			return retrieveUpdatedTreeRecords(type);
		} else {
			throw new BadRequestException(BAD_REQUEST);
		}
	}

	/**
	 * retrieve all the items from employee or location table where uiStatus =
	 * 'Updated'
	 * 
	 * @param type
	 * @return updated items as String
	 * @throws Exception
	 */
	private String retrieveUpdatedEmployeeLocationRecords(String type) throws Exception {
		Map<String, String> nameMap = new HashMap<>();
		Map<String, Object> valueMap = new HashMap<>();
		ItemCollection<ScanOutcome> items = null;

		nameMap.put("#uiStatus", "uiStatus");
		valueMap.put(":uiStatus", UI_STATUS_UPDATED);
		String condition = "#uiStatus = :uiStatus";

		switch (type) {
		case TYPE_EMPLOYEE:
			items = dynamoDao.getFilteredItems(configBean.getPersonTable(), nameMap, valueMap, condition);
			List<Person> employees = new ArrayList<>();
			items.forEach(item -> {
				Person employee = new Person();
				employee.setEmployeeUniqueId(item.getString("employeeUniqueId"));
				employee.setFirstName(item.getString("firstName"));
				if (item.getString("lastName") == null) {
					employee.setLastName(UNDEFINED);
				} else {
					employee.setLastName(item.getString("lastName"));
				}
				if (item.getString("domain") == null) {
					employee.setDomain(UNDEFINED);
				} else {
					employee.setDomain(StringUtils.capitalize(item.getString("domain")));
				}
				if (item.getString("roleEntity") == null) {
					employee.setRoleEntity(UNDEFINED);
				} else {
					employee.setRoleEntity(item.getString("roleEntity"));
				}
				if (item.getString("email") == null) {
					employee.setEmail(UNDEFINED);
				} else {
					employee.setEmail(item.getString("email"));
				}
				if (item.getString("employeeStatus") == null) {
					employee.setEmployeeStatus(UNDEFINED);
				} else {
					employee.setEmployeeStatus(item.getString("employeeStatus"));
				}
				if (item.getString("jobCode") == null) {
					employee.setJobCode(UNDEFINED);
				} else {
					employee.setJobCode(item.getString("jobCode"));
				}
				if (item.getString("jobCodeDescription") == null) {
					employee.setJobCodeDescription(UNDEFINED);
				} else {
					employee.setJobCodeDescription(item.getString("jobCodeDescription"));
				}
				if (item.getString("employmentType") == null) {
					employee.setEmploymentType(UNDEFINED);
				} else {
					employee.setEmploymentType(item.getString("employmentType"));
				}
				employee.setEmployeeId(item.getString("employeeId"));
				if (item.getString("locationCode") == null) {
					employee.setLocationCode(UNDEFINED);
				} else {
					employee.setLocationCode(item.getString("locationCode"));
				}
				if (item.getString("locationDescription") == null) {
					employee.setLocationDescription(UNDEFINED);
				} else {
					employee.setLocationDescription(item.getString("locationDescription"));
				}
				employees.add(employee);
			});
			return "{\"Items\":"
					+ new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(employees)
					+ "}";
		case TYPE_LOCATION:
			items = dynamoDao.getFilteredItems(configBean.getLocationTable(), nameMap, valueMap, condition);
			List<Location> locations = new ArrayList<>();
			items.forEach(item -> {
				Location location = new Location();
				location.setLocationUniqueId(item.getString("locationUniqueId"));
				if (item.getString("domain") == null) {
					location.setDomain(UNDEFINED);
				} else {
					location.setDomain(StringUtils.capitalize(item.getString("domain")));
				}
				if (item.getString("entityLocationCode") == null) {
					location.setEntityLocationCode(UNDEFINED);
				} else {
					location.setEntityLocationCode(item.getString("entityLocationCode"));
				}
				location.setLocationCode(item.getString("locationCode"));
				if (item.getString("name") == null) {
					location.setName(UNDEFINED);
				} else {
					location.setName(item.getString("name"));
				}
				if (item.getString("locationType") == null) {
					location.setLocationType(UNDEFINED);
				} else {
					location.setLocationType(item.getString("locationType"));
				}
				Address primaryAddress = new Address();
				if (item.get("primaryAddress") == null) {
					location.setPrimaryAddress(primaryAddress);
				} else {
					Map<String, String> addressMap = item.getMap("primaryAddress");
					if (addressMap.containsKey("address1")) {
						primaryAddress.setAddress1(addressMap.get("address1"));
					} else {
						primaryAddress.setAddress1(UNDEFINED);
					}
					if (addressMap.containsKey("address2")) {
						primaryAddress.setAddress2(addressMap.get("address2"));
					} else {
						primaryAddress.setAddress2(UNDEFINED);
					}
					if (addressMap.containsKey("city")) {
						primaryAddress.setCity(addressMap.get("city"));
					} else {
						primaryAddress.setCity(UNDEFINED);
					}
					if (addressMap.containsKey("state")) {
						primaryAddress.setState(addressMap.get("state"));
					} else {
						primaryAddress.setState(UNDEFINED);
					}
					if (addressMap.containsKey("zipCode")) {
						primaryAddress.setZipCode(addressMap.get("zipCode"));
					} else {
						primaryAddress.setZipCode(UNDEFINED);
					}
					location.setPrimaryAddress(primaryAddress);
				}
				if (item.getString("phone") == null) {
					location.setPhone(UNDEFINED);
				} else {
					location.setPhone(item.getString("phone"));
				}
				if (item.getString("email") == null) {
					location.setEmail(UNDEFINED);
				} else {
					location.setEmail(item.getString("email"));
				}
				if (item.getString("openDate") == null) {
					location.setOpenDate(UNDEFINED);
				} else {
					location.setOpenDate(item.getString("openDate"));
				}
				if (item.getString("updated") == null) {
					location.setUpdated(UNDEFINED);
				} else {
					location.setUpdated(item.getString("updated"));
				}
				if (item.getString("squareFootage") == null) {
					location.setSquareFootage(UNDEFINED);
				} else {
					location.setSquareFootage(item.getString("squareFootage"));
				}
				if (item.getString("servedByWhse") == null) {
					location.setServedByWhse(UNDEFINED);
				} else {
					location.setServedByWhse(item.getString("servedByWhse"));
				}
				if (item.getString("market") == null) {
					location.setMarket(UNDEFINED);
				} else {
					location.setMarket(item.getString("market"));
				}
				if (item.getString("closeDate") == null) {
					location.setCloseDate(UNDEFINED);
				} else {
					location.setCloseDate(item.getString("closeDate"));
				}
				locations.add(location);
			});
			return "{\"Items\":"
					+ new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(locations)
					+ "}";
		}
		return null;
	}

	/**
	 * retrieve all the items from tree table where uiStatus = 'Updated'
	 * 
	 * @param type
	 * @return updated items as String
	 * @throws Exception
	 */
	private String retrieveUpdatedTreeRecords(String type) throws Exception {
		Map<String, String> nameMap = new HashMap<>();
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put(":uiStatus", UI_STATUS_UPDATED);
		ItemCollection<ScanOutcome> items = null;
		String condition = "#uiStatus = :uiStatus";
		switch (type) {
		case TYPE_EMPLOYEE_NODE:
			nameMap.put("#uiStatus", "empUiStatus");
			items = dynamoDao.getFilteredItems(configBean.getTreeTable(), nameMap, valueMap, condition);
			List<UpdatedTreeItem> employeeNodes = new ArrayList<>();
			items.forEach(item -> {
				if (!item.getString("domain").equalsIgnoreCase(DOMAIN_ORPHANS)) {
					UpdatedTreeItem node = new UpdatedTreeItem();
					node.setCommonId(item.getString("commonId"));
					node.setParentCommonId(item.getString("parentCommonId"));
					node.setDomain(StringUtils.capitalize(item.getString("domain")));
					Map<String, Object> employeeMap = item.getMap("employee");
					PersonTree employee = new PersonTree();
					employee.setEmployeeId(String.valueOf(employeeMap.get("employeeId")));
					employee.setEmployeeUniqueId(String.valueOf(employeeMap.get("employeeUniqueId")));
					employee.setLevel(Integer.parseInt(String.valueOf(employeeMap.get("level"))));
					if (!employeeMap.containsKey("hrId")) {
						employee.setHrId(UNDEFINED);
					} else {
						employee.setHrId(String.valueOf(employeeMap.get("hrId")));
					}
					employee.setName(String.valueOf(employeeMap.get("name")));
					employee.setParentPositionId(String.valueOf(employeeMap.get("parentPositionId")));
					employee.setPosition(String.valueOf(employeeMap.get("position")));
					employee.setPositionId(String.valueOf(employeeMap.get("positionId")));
					node.setObject(employee);
					employeeNodes.add(node);
				}
			});

			return "{\"Items\":"
					+ new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(employeeNodes)
					+ "}";
		case TYPE_LOCATION_NODE:
			nameMap.put("#uiStatus", "locUiStatus");
			items = dynamoDao.getFilteredItems(configBean.getTreeTable(), nameMap, valueMap, condition);
			List<UpdatedTreeItem> locationNodes = new ArrayList<>();
			items.forEach(item -> {
				if (!item.getString("domain").equalsIgnoreCase(DOMAIN_ORPHANS)) {
					UpdatedTreeItem node = new UpdatedTreeItem();
					node.setCommonId(item.getString("commonId"));
					node.setParentCommonId(item.getString("parentCommonId"));
					node.setDomain(StringUtils.capitalize(item.getString("domain")));
					node.setObject(item.get("location"));
					locationNodes.add(node);
				}
			});

			return "{\"Items\":"
					+ new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(locationNodes)
					+ "}";
		}
		return null;
	}

	/**
	 * change the uiStatus of all the items, that were processed by back-end, as
	 * 'Processed'
	 * 
	 * @param idsToUpdate
	 * @param type
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	public void changeUpdatedRecordsStatus(List<String> idsToUpdate, String type)
			throws JsonProcessingException, Exception {
		Item itemToUpdate = null;
		Expected expectedCondition = null;
		String today = new DateTime(DateTimeZone.UTC).toString();
		switch (type) {
		case TYPE_EMPLOYEE:
			Person employee = null;
			for (String employeeUniqueId : idsToUpdate) {
				employee = new ObjectMapper().readValue(retrieveEmployeeDetails(employeeUniqueId), Person.class);
				employee.setLastModifiedDate(today);
				employee.setUiStatus(UI_STATUS_PROCESSED);

				itemToUpdate = Item.fromJSON(
						new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(employee));
				expectedCondition = new Expected("employeeUniqueId").eq(employee.getEmployeeUniqueId());
				dynamoDao.updateItem(configBean.getPersonTable(), expectedCondition, itemToUpdate);
			}
			break;
		case TYPE_LOCATION:
			Location location = null;
			for (String locationUniqueId : idsToUpdate) {
				location = new ObjectMapper().readValue(retrieveLocation(locationUniqueId), Location.class);
				location.setLastModifiedDate(today);
				location.setUiStatus(UI_STATUS_PROCESSED);

				itemToUpdate = Item.fromJSON(
						new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(location));
				expectedCondition = new Expected("locationUniqueId").eq(location.getLocationUniqueId());
				dynamoDao.updateItem(configBean.getLocationTable(), expectedCondition, itemToUpdate);
			}
			break;
		case TYPE_EMPLOYEE_NODE:
			OrganizationTree empNode = null;
			for (String commonId : idsToUpdate) {
				empNode = getSingleItem(commonId);
				if (empNode == null) {
					throw new NotFoundException(NO_TREE_NODE);
				}
				empNode.setLastModifiedDate(today);
				empNode.setEmpUiStatus(UI_STATUS_PROCESSED);

				itemToUpdate = Item.fromJSON(
						new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(empNode));
				expectedCondition = new Expected("commonId").eq(empNode.getCommonId());
				dynamoDao.updateItem(configBean.getTreeTable(), expectedCondition, itemToUpdate);
			}
			break;
		case TYPE_LOCATION_NODE:
			OrganizationTree locNode = null;
			for (String commonId : idsToUpdate) {
				locNode = getSingleItem(commonId);
				if (locNode == null) {
					throw new NotFoundException(NO_TREE_NODE);
				}
				locNode.setLastModifiedDate(today);
				locNode.setLocUiStatus(UI_STATUS_PROCESSED);

				itemToUpdate = Item.fromJSON(
						new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(locNode));
				expectedCondition = new Expected("commonId").eq(locNode.getCommonId());
				dynamoDao.updateItem(configBean.getTreeTable(), expectedCondition, itemToUpdate);
			}
			break;
		}
	}

	/**
	 * return a single tree node item
	 * 
	 * @param commonId
	 * @return OrganizationTree object
	 * @throws Exception
	 */
	private OrganizationTree getSingleItem(String commonId) throws Exception {
		Map<String, String> nameMap = new HashMap<>();
		Map<String, Object> valueMap = new HashMap<>();
		nameMap.put("#commonId", "commonId");
		valueMap.put(":commonId", commonId);
		String keyConditionExpression = "#commonId = :commonId";
		ItemCollection<QueryOutcome> items = dynamoDao.queryTableWithIndex(configBean.getTreeTable(),
				configBean.getCommonIdIndex(), keyConditionExpression, nameMap, valueMap);
		Iterator<Item> itemIterator = items.iterator();
		while (itemIterator.hasNext()) {
			return new ObjectMapper().readValue(itemIterator.next().toJSON(), OrganizationTree.class);
		}
		return null;
	}

	/**
	 * terminate an employee - update employee table and in the tree table, set
	 * employeeUniqueId and employeeId = 0, name and status = 'Open', empUiStatus =
	 * 'Updated'
	 * 
	 * @param employee
	 * @throws JsonProcessingException
	 * @throws Exception
	 */
	public void terminateEmployee(Person employee) throws JsonProcessingException, Exception {
		updateEmployee(employee);

		Map<String, String> nameMap = new HashMap<>();
		Map<String, Object> valueMap = new HashMap<>();
		nameMap.put("#filterKey", "employeeUniqueId");
		valueMap.put(":filterValue", employee.getEmployeeUniqueId());
		String keyConditionExpression = "#filterKey = :filterValue";
		ItemCollection<QueryOutcome> items = dynamoDao.queryTableWithIndex(configBean.getTreeTable(),
				configBean.getEmployeeUniqueIdIndex(), keyConditionExpression, nameMap, valueMap);
		OrganizationTree node = null;
		Item itemToUpdate = null;
		Expected expectedCondition = null;
		PersonTree emp = null;
		for (Item item : items) {
			node = new ObjectMapper().readValue(item.toJSON(), OrganizationTree.class);
			String nodeStatus = node.getStatus();
			String parentCommonId = node.getParentCommonId();
			String commonId = node.getCommonId();

			if (!nodeStatus.equals(DOMAIN_ORPHANS)) {
				emp = node.getEmployee();
				emp.setEmployeeUniqueId(DEFAULT);
				emp.setEmployeeId(DEFAULT);
				emp.setName(STATUS_OPEN);
				node.setEmployee(emp);
				node.setEmployeeUniqueId(DEFAULT);
				node.setStatus(STATUS_OPEN);
				node.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());
				node.setEmpUiStatus(UI_STATUS_UPDATED);
				itemToUpdate = Item.fromJSON(
						new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(node));
				expectedCondition = new Expected("commonId").eq(node.getCommonId());
				dynamoDao.updateItem(configBean.getTreeTable(), expectedCondition, itemToUpdate);
			} else {
				dynamoDao.deleteItem(configBean.getTreeTable(), "parentCommonId", parentCommonId, "commonId", commonId);
			}
		}
	}

	/**
	 * 
	 * @param node
	 * @throws Exception
	 */
	public void persistNode(TreeNode node) throws Exception {
		MandatoryFieldChecker.isNull(node.getLocation().getName().trim());
		MandatoryFieldChecker.isNull(node.getLocation().getLocationCode().trim());
		MandatoryFieldChecker.isNull(node.getLocation().getLocationUniqueId().trim());
		MandatoryFieldChecker.isNull(node.getEmpLevel());
		MandatoryFieldChecker.isNull(node.getLocLevel());
		MandatoryFieldChecker.isNull(node.getParentEmployeeName().trim());

		DateTime today = new DateTime(DateTimeZone.UTC);
		String locationUniqueId = node.getLocation().getLocationUniqueId();
		if (locationUniqueId.equalsIgnoreCase(UNDEFINED)) {
			locationUniqueId = UUID.randomUUID().toString();
			node.getLocation().setLocationUniqueId(locationUniqueId);
			node.getLocation().setCreatedDate(today.toString());
			node.getLocation().setLastModifiedDate(today.toString());

			dynamoDao.persistItem(configBean.getLocationTable(), new ObjectMapper()
					.setSerializationInclusion(Include.NON_NULL).writeValueAsString(node.getLocation()));
		}

		MandatoryFieldChecker.isNull(node.getParentCommonId().trim()); // also validate if the parent common id is
																		// available in the tree
		OrganizationTree orgTree = new OrganizationTree();
		orgTree.setParentCommonId(node.getParentCommonId());
		orgTree.setCommonId(UUID.randomUUID().toString());
		orgTree.setDomain(StringUtils.capitalize(node.getDomain()));
		orgTree.setEmployeeUniqueId(DEFAULT);
		orgTree.setParentEmployeeName(node.getParentEmployeeName());
		orgTree.setLocationUniqueId(locationUniqueId);
		orgTree.setLeaf(node.getLeaf());
		orgTree.setStatus(node.getStatus());
		orgTree.setType(node.getType());
		orgTree.setCreatedDate(today.toString());
		orgTree.setLastModifiedDate(today.toString());
		orgTree.setLocUiStatus(UI_STATUS_UPDATED);
		orgTree.setCategory(node.getCategory());

		LocationTree loc = new LocationTree();
		loc.setLocationUniqueId(locationUniqueId);
		loc.setName(node.getLocation().getName().trim());
		loc.setLevel(node.getLocLevel());
		loc.setLocationCode(node.getLocation().getLocationCode());
		loc.setLocationParentKey(node.getLocationParentKey());
		orgTree.setLocation(loc);

		PersonTree emp = new PersonTree();
		emp.setEmployeeId(DEFAULT);
		emp.setEmployeeUniqueId(DEFAULT);
		emp.setName(STATUS_OPEN);
		emp.setHrId(DEFAULT);
		emp.setPosition(node.getPosition());
		emp.setPositionId(DEFAULT);
		emp.setLevel(node.getEmpLevel());
		emp.setParentPositionId(node.getParentPositionId());
		orgTree.setEmployee(emp);

		dynamoDao.persistItem(configBean.getTreeTable(),
				new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(orgTree));
	}

	/**
	 * 
	 * @param node
	 * @throws Exception
	 */
	public void unassignEmployee(String fromParentCommonId, String fromCommonId) throws Exception {
		OrganizationTree fromNode = new ObjectMapper().readValue(retrieveNode(fromParentCommonId, fromCommonId),
				OrganizationTree.class);
		if (fromNode == null) {
			logger.error("moveEmployee failed " + NO_TREE_NODE + " fromParentCommonId " + fromParentCommonId
					+ " fromCommonId " + fromCommonId + " \n");
			throw new NotFoundException(
					NO_TREE_NODE + " fromParentCommonId " + fromParentCommonId + " and fromCommonId " + fromCommonId);
		}
		String toParentCommonId = UNASSIGN_PARENT_COMMON_ID;
		String toCommonId = UNASSIGN_COMMON_ID;

		OrganizationTree toNode = new ObjectMapper().readValue(retrieveNode(toParentCommonId, toCommonId),
				OrganizationTree.class);
		if (toNode == null) {
			logger.error("moveEmployee failed " + NO_TREE_NODE + " toParentCommonId " + toParentCommonId
					+ " toCommonId " + toCommonId + " \n");
			throw new BadRequestException(
					NO_TREE_NODE + " toParentCommonId " + toParentCommonId + " and toCommonId " + toCommonId);
		}

		DateTime today = new DateTime(DateTimeZone.UTC);
		OrganizationTree orgTree = new OrganizationTree();
		orgTree.setParentCommonId(toCommonId);
		orgTree.setDomain(StringUtils.capitalize(fromNode.getDomain()));

		orgTree.setCommonId(UUID.randomUUID().toString());
		orgTree.setEmployeeUniqueId(fromNode.getEmployee().getEmployeeUniqueId());
		orgTree.setCreatedDate(today.toString());
		orgTree.setLastModifiedDate(today.toString());
		orgTree.setLeaf(true);
		orgTree.setLocation(new LocationTree(DEFAULT, STATUS_OPEN));
		orgTree.setStatus(toNode.getStatus());
		orgTree.setType(toNode.getType());
		orgTree.setParentEmployeeName("Unassigned");
		orgTree.setLocationUniqueId(DEFAULT);
		orgTree.setCategory(toNode.getCategory());

		PersonTree emp = new PersonTree();
		emp.setEmployeeId(fromNode.getEmployee().getEmployeeUniqueId());
		emp.setEmployeeUniqueId(fromNode.getEmployee().getEmployeeUniqueId());
		emp.setName(fromNode.getEmployee().getName());
		emp.setHrId(DEFAULT);
		emp.setPosition(fromNode.getEmployee().getPosition());
		emp.setPositionId(fromNode.getEmployee().getPositionId());
		emp.setLevel(2);
		emp.setParentPositionId(toNode.getEmployee().getPositionId());
		orgTree.setEmployee(emp);

		dynamoDao.persistItem(configBean.getTreeTable(),
				new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(orgTree));

		String parentCommonId = fromNode.getParentCommonId();
		if (parentCommonId.equals(ORPHAN_PARENT_COMMON_ID) || parentCommonId.equals(ORPHAN_DELIVERY_PARENT_COMMON_ID)
				|| parentCommonId.equals(ORPHAN_CONTACT_CENTRE_PARENT_COMMON_ID)
				|| parentCommonId.equals(ORPHAN_SALES_PARENT_COMMON_ID)
				|| parentCommonId.equals(ORPHAN_SERVICE_PARENT_COMMON_ID)) {
			dynamoDao.deleteItem(configBean.getTreeTable(), "parentCommonId", fromParentCommonId, "commonId",
					fromCommonId);
		} else {
			emp = fromNode.getEmployee();
			emp.setEmployeeUniqueId(DEFAULT);
			emp.setEmployeeId(DEFAULT);
			emp.setName(STATUS_OPEN);
			emp.setPositionId(DEFAULT);
			fromNode.setEmployee(emp);
			fromNode.setEmployeeUniqueId(DEFAULT);
			fromNode.setStatus(STATUS_OPEN);
			fromNode.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());
			fromNode.setEmpUiStatus(UI_STATUS_UPDATED);

			Item itemToUpdate = Item.fromJSON(
					new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(fromNode));
			Expected expectedCondition = new Expected("commonId").eq(fromNode.getCommonId());
			dynamoDao.updateItem(configBean.getTreeTable(), expectedCondition, itemToUpdate);
		}

		String childCommonId = fromNode.getCommonId();
		KeyAttribute hashKey = new KeyAttribute("parentCommonId", childCommonId);
		ItemCollection<QueryOutcome> items = dynamoDao.getFilteredItems(configBean.getTreeTable(), hashKey);

		if (items.getAccumulatedItemCount() != 0) {
			for (Item node : items) {
				String comId = node.getString("commonId");
				String parentId = node.getString("parentCommonId");

				OrganizationTree childNode = new ObjectMapper().readValue(retrieveNode(parentId, comId),
						OrganizationTree.class);
				String parentEmpName = fromNode.getEmployee().getName();
				childNode.setParentEmployeeName(parentEmpName);
				childNode.getEmployee().setParentPositionId(DEFAULT);
				childNode.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());
				childNode.setEmpUiStatus(UI_STATUS_UPDATED);
				Item childItemToUpdate = Item.fromJSON(
						new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(childNode));
				Expected expectedCondition = new Expected("commonId").eq(childNode.getCommonId());
				dynamoDao.updateItem(configBean.getTreeTable(), expectedCondition, childItemToUpdate);
			}
		}
	}

	/**
	 * swap employees
	 * 
	 * @param aParentCommonId
	 * @param aCommonId
	 * @param bParentCommonId
	 * @param bCommonId
	 * @throws NotFoundException
	 * @throws BadRequestException
	 * @throws Exception
	 */
	public void swapEmployee(String aParentCommonId, String aCommonId, String bParentCommonId, String bCommonId)
			throws NotFoundException, BadRequestException, Exception {
		OrganizationTree aNode = new ObjectMapper().readValue(retrieveNode(aParentCommonId, aCommonId),
				OrganizationTree.class);
		if (aNode == null) {
			logger.error("swapEmployee failed " + NO_TREE_NODE + " aParentCommonId " + aParentCommonId + " aCommonId "
					+ aCommonId + " \n");
			throw new NotFoundException(
					NO_TREE_NODE + " aParentCommonId " + aParentCommonId + " and aCommonId " + aCommonId);
		}

		OrganizationTree bNode = new ObjectMapper().readValue(retrieveNode(bParentCommonId, bCommonId),
				OrganizationTree.class);
		if (bNode == null) {
			logger.error("swapEmployee failed " + NO_TREE_NODE + " bParentCommonId " + bParentCommonId + " bCommonId "
					+ bCommonId + " \n");
			throw new BadRequestException(
					NO_TREE_NODE + " bParentCommonId " + bParentCommonId + " and bCommonId " + bCommonId);
		}

		String tempParentCommonId = aNode.getParentCommonId();
		aNode.setParentCommonId(bNode.getParentCommonId());
		bNode.setParentCommonId(tempParentCommonId);

		dynamoDao.persistItem(configBean.getTreeTable(),
				new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(aNode));
		dynamoDao.persistItem(configBean.getTreeTable(),
				new ObjectMapper().setSerializationInclusion(Include.NON_NULL).writeValueAsString(bNode));
		dynamoDao.deleteItem(configBean.getTreeTable(), "parentCommonId", aParentCommonId, "commonId", aCommonId);
		dynamoDao.deleteItem(configBean.getTreeTable(), "parentCommonId", bParentCommonId, "commonId", bCommonId);
	}
}
