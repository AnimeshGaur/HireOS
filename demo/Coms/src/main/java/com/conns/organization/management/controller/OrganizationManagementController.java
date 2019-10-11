package com.conns.organization.management.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.conns.organization.management.exception.BadRequestException;
import com.conns.organization.management.exception.NotFoundException;
import com.conns.organization.management.model.Location;
import com.conns.organization.management.model.Person;
import com.conns.organization.management.model.PersonTree;
import com.conns.organization.management.model.TreeNode;
import com.conns.organization.management.service.OrganizationManagementService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/v1/organization")
public class OrganizationManagementController {
	
	private static final Logger logger = LoggerFactory.getLogger(OrganizationManagementController.class);
	
	@Autowired
	private OrganizationManagementService service;
	
	/**
	 * get master data for given type
	 * @param type - master types (example: positions)
	 * @return master data
	 * @throws Exception
	 */
	@RequestMapping(path = "/master-data/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getMasterData(@PathVariable("type") String type) throws Exception {
		try {
			return new ResponseEntity<String>(service.retrieveMasterData(type), HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/master-data failed - Amazon DynamoDB Exception for " + type + " \n", ex);
			throw new Exception(ex.getMessage());
		} catch(BadRequestException ex) {
			logger.error("/master-data failed - Bad Request for " + type + " \n", ex);
			throw new BadRequestException();
		} catch (Exception ex) {
			logger.error("/master-data failed - Server Error for " + type + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * 
	 * @param domain
	 * @param level
	 * @return 
	 * @throws Exception
	 */
	@RequestMapping(path = "/view/{domain}/{level}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getTableView(@PathVariable("domain") String domain, @PathVariable("level") String level) throws Exception {
		try {
			return new ResponseEntity<String>(service.retrieveDataForTableView(domain, level), HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/view/{domain} failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/view/{domain} failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/all-employees", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAllEmployees() throws Exception {
		try {
			return new ResponseEntity<String>(service.retrieveAllEmployees(), HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/all-employees failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/all-employees failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * generate nodes for organization tree structure
	 * @param commonId
	 * @return all nodes whose parentCommonId = {id}
	 * @throws Exception
	 */
	@RequestMapping(path = "/tree/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getOrganizationTree(@PathVariable("id") String commonId) throws Exception {
		try {
			return new ResponseEntity<String>(service.retrieveOrganizationTree(commonId), HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/tree/{id} failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/tree/{id} failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * get employee details for a given employee unique ID
	 * @param employeeUniqueId
	 * @return employee JSON
	 * @throws Exception
	 */
	@RequestMapping(path = "/employee/{guid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getEmployee(@PathVariable("guid") String employeeUniqueId) throws Exception {
		try {
			return new ResponseEntity<String>(service.retrieveEmployeeDetails(employeeUniqueId), HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/employee/{guid} failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/employee/{guid} failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * get location details for a given location unique ID
	 * @param locationUniqueId
	 * @return location JSON
	 * @throws Exception
	 */
	@RequestMapping(path = "/location/{guid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getLocation(@PathVariable("guid") String locationUniqueId) throws Exception {
		try {
			return new ResponseEntity<String>(service.retrieveLocationDetails(locationUniqueId), HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/location/{guid} failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/location/{guid} failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * get all positions whose status = status
	 * @param status
	 * @return list of positions
	 * @throws Exception
	 */
	@RequestMapping(path = "/position/{status}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getPositions(@PathVariable("status") String status) throws Exception {
		try {
			return new ResponseEntity<String>(service.retrieveNodes(status), HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/position/{status} failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/position/{status} failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * get all positions whose status = status
	 * @param status
	 * @return list of positions
	 * @throws Exception
	 */
	@RequestMapping(path = "/position/{status}/{domain}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getPositions(@PathVariable("status") String status, @PathVariable("domain") String domain) throws Exception {
		try {
			return new ResponseEntity<String>(service.retrieveNodes(status, domain), HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/position/{status} failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/position/{status} failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * persist employee data in COMS_employee dynamoDB table
	 * @param employee
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/create/employee", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Person> createEmployee(@RequestBody Person employee) throws Exception {
		try {
			return new ResponseEntity<Person>(service.persistEmployee(employee), HttpStatus.OK);
			//return new ResponseEntity<String>("{\"message\":\"Resource created successfully.\"}", HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/create failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch(Exception ex) {
			logger.error("/create failed - Server Error while persisting employee \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * persist location data in COMS_location dynamoDB table
	 * @param location
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/create/location", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Location> createLocation(@RequestBody Location location) throws Exception {
		try {
			return new ResponseEntity<Location>(service.persistLocation(location), HttpStatus.OK);
			//return new ResponseEntity<String>("{\"message\":\"Resource created successfully.\"}", HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/create/location failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch(Exception ex) {
			logger.error("/create/location failed - Server Error while persisting location \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * update employee details
	 * @param employee
	 * @param parentCommonId
	 * @param commonId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/update/employee/{parentCommonId}/{commonId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateEmployee(@PathVariable("parentCommonId") String parentCommonId, @PathVariable("commonId") String commonId, @RequestBody Person employee) throws Exception {
		try {
			service.updateEmployee(employee, parentCommonId, commonId);
			return new ResponseEntity<String>("{\"message\":\"Resource updated successfully.\"}", HttpStatus.OK);
		} catch(ConditionalCheckFailedException ex) {
			logger.error("/update/employee failed - Not Found " + employee.getEmployeeUniqueId() + " \n", ex);
			throw new NotFoundException(ex.getMessage());
		} catch (AmazonDynamoDBException ex) {
			logger.error("/update/employee failed - Amazon DynamoDB Exception " + employee.getEmployeeUniqueId() + " \n", ex);
			throw new Exception(ex.getMessage());
		} catch(Exception ex) {
			logger.error("/update/employee failed - Server Error " + employee.getEmployeeUniqueId() + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * update location details
	 * @param location
	 * @param parentCommonId
	 * @param commonId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/update/location/{parentCommonId}/{commonId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateLocation(@PathVariable("parentCommonId") String parentCommonId, @PathVariable("commonId") String commonId, @RequestBody Location location) throws Exception {
		try {
			service.updateLocation(location, parentCommonId, commonId); //location and tree tables
			return new ResponseEntity<String>("{\"message\":\"Resource updated successfully.\"}", HttpStatus.OK);
		} catch(ConditionalCheckFailedException ex) {
			logger.error("/update/location failed - Not Found " + location.getLocationUniqueId() + " \n", ex);
			throw new NotFoundException(ex.getMessage());
		} catch (AmazonDynamoDBException ex) {
			logger.error("/update/location failed - Amazon DynamoDB Exception " + location.getLocationUniqueId() + " \n", ex);
			throw new Exception(ex.getMessage());
		} catch(Exception ex) {
			logger.error("/update/location failed - Server Error " + location.getLocationUniqueId() + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * move or transfer an employee
	 * @param fromParentCommonId
	 * @param fromCommonId
	 * @param toParentCommonId
	 * @param toCommonId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/move/employee/{fromParentCommonId}/{fromCommonId}/{toParentCommonId}/{toCommonId}", method = RequestMethod.PUT, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> moveEmployee(@PathVariable("fromParentCommonId") String fromParentCommonId, @PathVariable("fromCommonId") String fromCommonId, @PathVariable("toParentCommonId") String toParentCommonId, @PathVariable("toCommonId") String toCommonId, @RequestBody String dummyValue) throws Exception {
		try {
			service.moveEmployee(fromParentCommonId, fromCommonId, toParentCommonId, toCommonId);
			return new ResponseEntity<String>("{\"message\":\"Resource updated successfully.\"}", HttpStatus.OK);
		} catch(ConditionalCheckFailedException ex) {
			logger.error("/move/employee failed - Either fromCommonId or toCommonId are not available in the organization tree \n", ex);
			throw new NotFoundException(ex.getMessage());
		} catch (AmazonDynamoDBException ex) {
			logger.error("/move/employee failed - Amazon DynamoDB Exception " + fromCommonId + " toCommonId " + toCommonId + " \n", ex);
			throw new Exception(ex.getMessage());
		} catch(BadRequestException ex) {
			logger.error("/move/employee failed - Bad Request fromCommonId " + fromCommonId + " toCommonId " + toCommonId + " \n", ex);
			throw new BadRequestException(ex.getMessage());
		} catch(Exception ex) {
			logger.error("/move/employee failed - Server Error fromCommonId " + fromCommonId + " toCommonId " + toCommonId + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * copy employee to another location
	 * @param parentCommonId
	 * @param commonId
	 * @param employee
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/copy/employee/{parentCommonId}/{commonId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> copyEmployee(@PathVariable("parentCommonId") String parentCommonId, @PathVariable("commonId") String commonId, @RequestBody PersonTree employee) throws Exception {
		try {
			service.copyEmployee(parentCommonId, commonId, employee);
			return new ResponseEntity<String>("{\"message\":\"Resource updated successfully.\"}", HttpStatus.OK);
		} catch(ConditionalCheckFailedException ex) {
			logger.error("/copy/employee failed - Not Found " + commonId + " \n", ex);
			throw new NotFoundException(ex.getMessage());
		} catch (AmazonDynamoDBException ex) {
			logger.error("/copy/employee failed - Amazon DynamoDB Exception " + commonId + " " + employee.getEmployeeUniqueId() + " \n", ex);
			throw new Exception(ex.getMessage());
		} catch(BadRequestException ex) {
			logger.error("/copy/employee failed - Bad Request " + commonId + " " + employee.getEmployeeUniqueId() + " \n", ex);
			throw new BadRequestException(ex.getMessage());
		} catch(Exception ex) {
			logger.error("/copy/employee failed - Server Error " + commonId + " " + employee.getEmployeeUniqueId() + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	//update tree node status, leaf
	
	/**
	 * move a location
	 * @param parentCommonId
	 * @param commonId
	 * @param toParentCommonId
	 * @param toCommonId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/move/location/{parentCommonId}/{commonId}/{toParentCommonId}/{toCommonId}", method = RequestMethod.PUT, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> moveLocation(@PathVariable("parentCommonId") String parentCommonId, @PathVariable("commonId") String commonId, @PathVariable("toParentCommonId") String toParentCommonId, @PathVariable("toCommonId") String toCommonId, @RequestBody String dummyValue) throws Exception {
		try {
			service.moveLocation(parentCommonId, commonId, toParentCommonId, toCommonId);
			return new ResponseEntity<String>("{\"message\":\"Resource updated successfully.\"}", HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/move/location failed - Amazon DynamoDB Exception commonId " + commonId + " toCommonId " + toCommonId + " \n", ex);
			throw new Exception(ex.getMessage());
		} catch(BadRequestException ex) {
			logger.error("/move/location failed - Bad Request commonId " + commonId + " toCommonId " + toCommonId + " \n", ex);
			throw new BadRequestException(ex.getMessage());
		} catch(Exception ex) {
			logger.error("/move/location failed - Server Error commonId " + commonId + " toCommonId " + toCommonId + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * close a store
	 * @param parentCommonId
	 * @param commonId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/close/store/{parentCommonId}/{commonId}", method = RequestMethod.PUT, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> closeStore(@PathVariable("parentCommonId") String parentCommonId, @PathVariable("commonId") String commonId, @RequestBody String dummyValue) throws Exception {
		try {
			service.closeStore(parentCommonId, commonId);
			return new ResponseEntity<String>("{\"message\":\"Resource updated successfully.\"}", HttpStatus.OK);
		} catch(ConditionalCheckFailedException ex) {
			logger.error("/close/store failed - Not Found " + commonId + " \n", ex);
			throw new NotFoundException(ex.getMessage());
		} catch (AmazonDynamoDBException ex) {
			logger.error("/close/store failed - Amazon DynamoDB Exception " + commonId + " \n", ex);
			throw new Exception(ex.getMessage());
		} catch(BadRequestException ex) {
			logger.error("/close/store failed - Bad Request " + commonId + " \n", ex);
			throw new BadRequestException(ex.getMessage());
		} catch(Exception ex) {
			logger.error("/close/store failed - Server Error " + commonId + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * get all location category = {category}
	 * @param level
	 * @return list of locations
	 * @throws Exception
	 */
	@RequestMapping(path = "/locations/{category}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getLocations(@PathVariable("category") String category) throws Exception {
		try {
			return new ResponseEntity<String>(service.retrieveLocations(category), HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/locations/{category} failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/locations/{category} failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * get all updated records
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/updated-records/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getUpdatedRecords(@PathVariable("type") String type) throws Exception {
		try {
			return new ResponseEntity<String>(service.retrieveUpdatedRecords(type), HttpStatus.OK); //type = node, loc, emp
		} catch (BadRequestException ex) {
			logger.error("/updated-records failed - Bad Request Exception \n", ex);
			throw new BadRequestException();
		} catch (AmazonDynamoDBException ex) {
			logger.error("/updated-records failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/updated-records failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * update status of all processed records
	 * @param idsToUpdate
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/change-updated-records-status/{type}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> changeUpdatedRecordsStatus(@RequestBody List<String> idsToUpdate, @PathVariable("type") String type) throws Exception {
		try {
			service.changeUpdatedRecordsStatus(idsToUpdate, type);
			return new ResponseEntity<String>("{\"message\":\"Resource updated successfully.\"}", HttpStatus.OK);
		} catch(ConditionalCheckFailedException ex) {
			logger.error("/copy/employee failed - Not Found \n", ex);
			throw new NotFoundException(ex.getMessage());
		} catch (AmazonDynamoDBException ex) {
			logger.error("/copy/employee failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch(BadRequestException ex) {
			logger.error("/copy/employee failed - Bad Request \n", ex);
			throw new BadRequestException(ex.getMessage());
		} catch(Exception ex) {
			logger.error("/copy/employee failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * terminate an employee
	 * @param employee
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/terminate-employee", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> terminateEmployee(@RequestBody Person employee) throws Exception {
		try {
			service.terminateEmployee(employee);
			return new ResponseEntity<String>("{\"message\":\"Resource updated successfully.\"}", HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/terminate-employee failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch(BadRequestException ex) {
			logger.error("/terminate-employee failed - Bad Request " + employee.getEmployeeId() + " \n", ex);
			throw new BadRequestException(ex.getMessage());
		} catch(Exception ex) {
			logger.error("/terminate-employee failed - Server Error " + employee.getEmployeeId() + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * persist employee data in COMS_employee dynamoDB table
	 * @param employee
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/create/node", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createNode(@RequestBody TreeNode node) throws Exception {
		try {
			service.persistNode(node);
			return new ResponseEntity<String>("{\"message\":\"Resource created successfully.\"}", HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/create/node failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch(Exception ex) {
			logger.error("/create/node failed - Server Error while persisting employee \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * persist employee data in COMS_employee dynamoDB table
	 * @param employee
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/unassign/employee/{fromParentCommonId}/{fromCommonId}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> unassignEmployee(@PathVariable("fromParentCommonId") String fromParentCommonId, @PathVariable("fromCommonId") String fromCommonId) throws Exception {
		try {
			service.unassignEmployee(fromParentCommonId,fromCommonId);
			return new ResponseEntity<String>("{\"message\":\"Resource created successfully.\"}", HttpStatus.OK);
		} catch (AmazonDynamoDBException ex) {
			logger.error("/create/node failed - Amazon DynamoDB Exception \n", ex);
			throw new Exception(ex.getMessage());
		} catch(Exception ex) {
			logger.error("/create/node failed - Server Error while persisting employee \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	
	/**
	 * swap employees
	 * @param aParentCommonId
	 * @param aCommonId
	 * @param bParentCommonId
	 * @param bCommonId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/swap/employee/{aParentCommonId}/{aCommonId}/{bParentCommonId}/{bCommonId}", method = RequestMethod.PUT, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> swapEmployee(@PathVariable("aParentCommonId") String aParentCommonId, @PathVariable("aCommonId") String aCommonId, @PathVariable("bParentCommonId") String bParentCommonId, @PathVariable("bCommonId") String bCommonId, @RequestBody String dummyValue) throws Exception {
		try {
			service.swapEmployee(aParentCommonId, aCommonId, bParentCommonId, bCommonId);
			return new ResponseEntity<String>("{\"message\":\"Resource updated successfully.\"}", HttpStatus.OK);
		} catch(ConditionalCheckFailedException ex) {
			logger.error("/swap/employee failed - Either fromCommonId or toCommonId are not available in the organization tree \n", ex);
			throw new NotFoundException(ex.getMessage());
		} catch (AmazonDynamoDBException ex) {
			logger.error("/swap/employee failed - Amazon DynamoDB Exception " + aCommonId + " toCommonId " + bCommonId + " \n", ex);
			throw new Exception(ex.getMessage());
		} catch(BadRequestException ex) {
			logger.error("/swap/employee failed - Bad Request fromCommonId " + aCommonId + " toCommonId " + bCommonId + " \n", ex);
			throw new BadRequestException(ex.getMessage());
		} catch(Exception ex) {
			logger.error("/swap/employee failed - Server Error fromCommonId " + aCommonId + " toCommonId " + bCommonId + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
}
