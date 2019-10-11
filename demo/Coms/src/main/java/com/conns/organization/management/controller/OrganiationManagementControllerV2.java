package com.conns.organization.management.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import java.text.ParseException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.conns.organization.management.exception.BadRequestException;
import com.conns.organization.management.model.LocationV2;
import com.conns.organization.management.model.SearchResponse;
import com.conns.organization.management.model.Store;
import com.conns.organization.management.service.OrganizationManagementServiceV2;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/v1/organization")
public class OrganiationManagementControllerV2 {

	private static final Logger logger = LoggerFactory.getLogger(OrganiationManagementControllerV2.class);

	@Autowired
	private OrganizationManagementServiceV2 service;

	/**
	 * get all location
	 * 
	 * @return list of locations
	 * @throws Exception
	 */
	// @ExceptionHandler(value = OrganiationManagementControllerV2.class)
	@RequestMapping(path = "/locations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getLocations(
			@RequestParam(value = "page", required = false, defaultValue = "0") String page,
			@RequestParam(value = "limit", required = false, defaultValue = "10") String limit) throws Exception {
		try {
			int pageNumber = Integer.parseInt(page);
			int pageSize = Integer.parseInt(limit);

			return new ResponseEntity<String>(service.getLocations(pageNumber, pageSize), HttpStatus.OK);
		} catch (IllegalArgumentException ex) {
			logger.error("/locations failed -\n", ex);
			throw new BadRequestException(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/locations failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * Get Locatio details by date
	 * 
	 * @param date
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/locationsdetails/{date}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchResponse> getLocationByDate(@PathVariable("date") String date) throws Exception {
		try {

			new SimpleDateFormat("yyyy-mm-dd").parse(date);
			return new ResponseEntity<SearchResponse>(service.getLocationDetailsForDate(date), HttpStatus.OK);
		} catch (ParseException pe) {
			logger.error("/Location failed - Invalid date \n", pe);
			throw new BadRequestException("Invalid date");
			
		} catch (Exception ex) {
			
			logger.error("/Location failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}

	}

	/**
	 * create Location
	 * 
	 * @return location id
	 * @throws Exception
	 */
	@RequestMapping(path = "/location/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createLocation(@RequestBody LocationV2 locationRequest) throws Exception {
		try {
			Integer id = service.createLocation(locationRequest);
			logger.debug("Location saved successfully with id == " + id);
			return new ResponseEntity<String>("{\"message\":\"Resource Created successfully.\"}", HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("/location/create failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * create Location
	 * 
	 * @return location id
	 * @throws Exception
	 */
	@RequestMapping(path = "/location/update", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateLocation(@RequestBody LocationV2 locationRequest) throws Exception {
		try {

			if (StringUtils.isBlank(locationRequest.getLocationGUID())) {
				throw new IllegalArgumentException("Missing required id or guid");
			}

			service.updateLocation(locationRequest);
			logger.debug("Location saved successfully with id == " + locationRequest.getId());
			return new ResponseEntity<String>("{\"message\":\"Resource Updated successfully.\"}", HttpStatus.OK);
		} catch (BadRequestException | IllegalArgumentException ex) {
			logger.error("/location/update failed -\n", ex);
			throw new BadRequestException(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/location/create failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/*
	 * get all regions
	 * 
	 * @return regions
	 * 
	 * @throws Exception
	 */

	@RequestMapping(path = "/regions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchResponse> getRegions() throws Exception {
		try {
			return new ResponseEntity<SearchResponse>(service.getRegions(), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("/regions failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	/*
	 * get district by region id
	 * 
	 * @return district
	 * 
	 * @throws Exception
	 */

	@RequestMapping(path = "/district/{regionid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchResponse> getDistrictByRegionId(@PathVariable("regionid") String regionId)
			throws Exception {
		try {

			return new ResponseEntity<SearchResponse>(service.getDistrictByRegionId(regionId), HttpStatus.OK);
		} catch (IllegalArgumentException ex) {
			logger.error("/District failed -\n", ex);
			throw new BadRequestException(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/regions failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/*
	 * get store by district id
	 * 
	 * @return store
	 * 
	 * @throws Exception
	 */

	@RequestMapping(path = "/store/{districtid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchResponse> getStoreByDistrictId(@PathVariable("districtid") String districtIdStr)
			throws Exception {
		try {
			Integer districtId = Integer.parseInt(districtIdStr);
			return new ResponseEntity<SearchResponse>(service.getStoreByDistrictId(districtId), HttpStatus.OK);
		} catch (IllegalArgumentException ex) {
			logger.error("/Store failed -\n", ex);
			throw new BadRequestException(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/Store failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
	/*
	 * get all Store
	 * 
	 * @return stores
	 * 
	 * @throws Exception
	 */

	@RequestMapping(path = "/store", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchResponse> getStore() throws Exception {
		try {

			return new ResponseEntity<SearchResponse>(service.getStore(), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("/Store failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	@RequestMapping(path = "/closedstores", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchResponse> getclosedStore() throws Exception {
		try {

			return new ResponseEntity<SearchResponse>(service.getclosedStore(), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("/Store failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/*
	 * get Store by store id
	 * 
	 * @return stores
	 * 
	 * @throws Exception
	 */
	@RequestMapping(path = "/store/data/{storenbr}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchResponse> getStoreByStoreNbr(@PathVariable("storenbr") String storeNbr)
			throws Exception {
		try {

			Integer storeNbrInt = Integer.parseInt(storeNbr);
			String date = new SimpleDateFormat("yyyy").format(new Date());
			return new ResponseEntity<SearchResponse>(service.getStoreByStoreNumber(storeNbrInt, date), HttpStatus.OK);

		} catch (IllegalArgumentException ex) {
			logger.error("/Store failed -\n", ex);
			throw new BadRequestException(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/Store failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	@RequestMapping(path = "/store/data/{storenbr}/{date}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchResponse> getStoreByStoreNbrAndYear(@PathVariable("storenbr") String storeNbr, @PathVariable("date") String date) throws Exception {
		try {
			new SimpleDateFormat("yyyy").parse(date);
			Integer storeNbrInt = Integer.parseInt(storeNbr);
			return new ResponseEntity<SearchResponse>(service.getStoreByStoreNumber(storeNbrInt, date), HttpStatus.OK);

		} catch (ParseException ex) {
			logger.error("Date is not in valid Format", ex);
			throw new BadRequestException(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/Store failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/*
	 * Update Store by Store Number
	 * 
	 * 
	 * 
	 * @throws Exception
	 */

	@RequestMapping(path = "/store/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateStore(@RequestBody Store store) throws Exception {
		try {
			if (store.getStoreNbr() == null) {
				throw new IllegalArgumentException("Missing required store number");
			}
			service.updateStore(store);
			return new ResponseEntity<String>("{\"message\":\"Resource updated successfully.\"}", HttpStatus.OK);
		} catch (IllegalArgumentException ex) {
			logger.error("/update failed - Server Error " + store.getStoreNbr() + " \n", ex);
			throw new BadRequestException(ex.getMessage());
		} catch (Exception ex) {
			logger.error("/update failed - Server Error " + store.getStoreNbr() + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/*
	 * Create Store
	 * 
	 * 
	 * 
	 * @throws Exception
	 */
	@RequestMapping(path = "/store/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createStore(@RequestBody Store store) throws Exception {
		try {
			Integer id = service.createStore(store);
			logger.debug("Store saved successfully with id == " + id);
			return new ResponseEntity<String>("{\"message\":\"Resource Created successfully.\"}", HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("/store/create failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}
}
