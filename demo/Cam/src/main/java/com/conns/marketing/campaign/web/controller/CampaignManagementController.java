package com.conns.marketing.campaign.web.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.conns.marketing.campaign.entity.CustomerEntity;
import com.conns.marketing.campaign.exception.BadRequestException;
import com.conns.marketing.campaign.exception.NotFoundException;
import com.conns.marketing.campaign.model.Campaign;
import com.conns.marketing.campaign.model.CampaignTemplate;
import com.conns.marketing.campaign.model.SearchResponse;
import com.conns.marketing.campaign.service.CampaignManagementService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/v1/campaign")
public class CampaignManagementController {
	private static final Logger logger = LoggerFactory.getLogger(CampaignManagementController.class);

	@Autowired
	private CampaignManagementService service;

	/**
	 * generate for campaign ID
	 * 
	 * @return UUID
	 * @throws Exception
	 */
	@RequestMapping(path = "/guid", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> generateGUID() throws Exception {
		try {
			return new ResponseEntity<String>(service.generateGUID(), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("/guid failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * get active and effective campaign templates
	 * 
	 * @return active and effective campaign templates
	 * @throws Exception
	 */
	@RequestMapping(path = "/templates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchResponse> getCampaignTemplates() throws Exception {
		try {
			return new ResponseEntity<SearchResponse>(service.getCampaignTemplates(), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("/templates failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * get all campaign templates
	 * 
	 * @return all campaign templates
	 * @throws Exception
	 */
	@RequestMapping(path = "/templates/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchResponse> getAllCampaignTemplates() throws Exception {
		try {
			return new ResponseEntity<SearchResponse>(service.getAllCampaignTemplates(), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("/templates/all failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * persist campaign data in CampaignMDM
	 * 
	 * @param campaign
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createCampaign(@RequestBody Campaign campaign) throws Exception {
		try {
			service.persistCampaign(campaign);
			return new ResponseEntity<String>("{\"message\":\"Resource created successfully.\"}", HttpStatus.OK);
		} catch (BadRequestException ex) {
			logger.error("/create failed - Bad Request for " + campaign.getCampaignId() + " \n", ex);
			throw new BadRequestException();
		} catch (Exception ex) {
			logger.error("/create failed - Server Error while persisting " + campaign.getCampaignId() + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * persist template data in CampaignTemplate
	 * 
	 * @param campaignTemplate
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/template/create", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createCampaignTemplate(@RequestBody CampaignTemplate campaignTemplate)
			throws Exception {
		try {
			service.persistCampaignTemplate(campaignTemplate);
			return new ResponseEntity<String>("{\"message\":\"Resource created successfully.\"}", HttpStatus.OK);
		} catch (BadRequestException ex) {
			logger.error("/template/create failed - Bad Request for " + campaignTemplate.getTemplateId() + " \n", ex);
			throw new BadRequestException();
		} catch (Exception ex) {
			logger.error("/template/create failed - Server Error while persisting " + campaignTemplate.getTemplateId()
					+ " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * get campaign output data for last x months (x taken from properties file)
	 *
	 * @return campaign output data
	 * @throws Exception
	 */
	@RequestMapping(path = "/output", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchResponse> getCampaignOutput() throws Exception {
		try {
			return new ResponseEntity<SearchResponse>(service.getCampaignOutput(), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("/output failed - Server Error \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * get campaign output data for last x months (x taken from properties file)
	 * 
	 * @return campaign output data
	 * @throws Exception
	 */
	@RequestMapping(path = "/output/{scheduledDate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchResponse> getCampaignOutput(@PathVariable("scheduledDate") String scheduledDate)
			throws Exception {
		try {
			return new ResponseEntity<SearchResponse>(service.getCampaignOutput(scheduledDate), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("/output failed - Server Error for " + scheduledDate + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * get filter data for given type to populate drop down
	 * 
	 * @param type - filter types (example: payment-types, customer-types, etc)
	 * @return filter data
	 * @throws Exception
	 */
	@RequestMapping(path = "/filter-data/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getFilterDataType(@PathVariable("type") String type) throws Exception {
		try {
			return new ResponseEntity<String>(service.getFilterDataType(type), HttpStatus.OK);
		} catch (BadRequestException ex) {
			logger.error("/filter-data failed - Bad Request for " + type + " \n", ex);
			throw new BadRequestException();
		} catch (Exception ex) {
			logger.error("/filter-data failed - Server Error for " + type + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * get master data for given type
	 * 
	 * @param type - master types (example: states, countries, etc)
	 * @return master data
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(path = "/master-data/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List> getMasterData(@PathVariable("type") String type) throws Exception {
		try {
			return new ResponseEntity<List>(service.getMasterData(type), HttpStatus.OK);
		} catch (BadRequestException ex) {
			logger.error("/master-data failed - Bad Request for " + type + " \n", ex);
			throw new BadRequestException();
		} catch (Exception ex) {
			logger.error("/master-data failed - Server Error for " + type + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * get campaigns based on status
	 * 
	 * @param status
	 * @return a list of campaign
	 * @throws Exception
	 */
	@RequestMapping(path = "/status/{status}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchResponse> getCampaignWithStatus(@PathVariable("status") String status)
			throws Exception {
		try {
			return new ResponseEntity<SearchResponse>(service.getAllCampaignWithStatus(status), HttpStatus.OK);
		} catch (Exception ex) {
			logger.error("/status failed - Server Error for " + status + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * search campaign based on type and criteria
	 * 
	 * @param type      - name or date
	 * @param criteria1
	 * @param criteria2
	 * @return a list of campaign
	 * @throws Exception
	 */
	@RequestMapping(path = "/search/{type}/{criteria1}/{criteria2}/{limit}/{page}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SearchResponse> search(@PathVariable("type") String type,
			@PathVariable("criteria1") String criteria1, @PathVariable("criteria2") String criteria2,
			@PathVariable("limit") String limit, @PathVariable("page") String page) throws Exception {
		try {
			if (type.equalsIgnoreCase("name")) {
				return new ResponseEntity<SearchResponse>(service.searchByName(criteria1, criteria2, limit, page),
						HttpStatus.OK);
			} else if (type.equalsIgnoreCase("date")) {
				return new ResponseEntity<SearchResponse>(
						service.searchByScheduledDate(criteria1, criteria2, limit, page), HttpStatus.OK);
			} else {
				throw new BadRequestException();
			}
		} catch (BadRequestException ex) {
			logger.error("/search failed - Bad Request for type- " + type + " -criteria1- " + criteria1
					+ " -criteria2- " + criteria2 + " \n", ex);
			throw new BadRequestException();
		} catch (Exception ex) {
			logger.error("/search failed - Server Error for type- " + type + " -criteria1- " + criteria1
					+ " -criteria2- " + criteria2 + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * update a campaign
	 * 
	 * @param campaign
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateCampaign(@RequestBody Campaign campaign) throws Exception {
		try {
			service.updateCampaign(campaign);
			return new ResponseEntity<String>("{\"message\":\"Resource updated successfully.\"}", HttpStatus.OK);
		} catch (ConditionalCheckFailedException ex) {
			logger.error("/update failed - Not Found " + campaign.getCampaignId() + " \n", ex);
			throw new NotFoundException("Cannot update resource as " + campaign.getCampaignId() + " does not exist.");
		} catch (BadRequestException ex) {
			logger.error("/update failed - Bad Request " + campaign.getCampaignId() + " \n", ex);
			throw new BadRequestException();
		} catch (Exception ex) {
			logger.error("/update failed - Server Error " + campaign.getCampaignId() + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * update selective fields in campaign
	 * 
	 * @param campaign
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/selective-update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> selectivelyUpdateCampaign(@RequestBody Campaign campaign) throws Exception {
		try {
			service.selectivelyUpdateCampaign(campaign);
			return new ResponseEntity<String>("{\"message\":\"Resource updated successfully.\"}", HttpStatus.OK);
		} catch (ConditionalCheckFailedException ex) {
			logger.error("/selective-update failed - Not Found " + campaign.getCampaignId() + " \n", ex);
			throw new NotFoundException("Cannot update resource as " + campaign.getCampaignId() + " does not exist.");
		} catch (BadRequestException ex) {
			logger.error("/selective-update failed - Bad Request " + campaign.getCampaignId() + " \n", ex);
			throw new BadRequestException();
		} catch (Exception ex) {
			logger.error("/selective-update failed - Server Error " + campaign.getCampaignId() + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * get data for a given campaign id
	 * 
	 * @param campaignID
	 * @return campaign data
	 * @throws Exception
	 */
	@RequestMapping(path = "/id/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Campaign> getCampaign(@PathVariable("id") String campaignID) throws Exception {
		try {
			return new ResponseEntity<Campaign>(service.getCampaign(campaignID), HttpStatus.OK);
		} catch (NotFoundException nfe) {
			logger.error("/template (GET) failed  " + campaignID + " \n", nfe);
			throw new NotFoundException("Resource with ID " + campaignID + " not found.");
		} catch (Exception ex) {
			logger.error("/id (GET) failed - Not Found " + campaignID + " \n", ex);
			throw new Exception("Resource with ID " + campaignID + " not found.");
		}
	}

	/**
	 * get data for a given campaign template id
	 * 
	 * @param id
	 * @return campaign template data
	 * @throws Exception
	 */
	@RequestMapping(path = "/template/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CampaignTemplate> getCampaignTemplate(@PathVariable("id") String templateId)
			throws Exception {
		try {
			return new ResponseEntity<CampaignTemplate>(service.getCampaignTemplate(templateId), HttpStatus.OK);
		} catch (NotFoundException nfe) {
			logger.error("/template (GET) failed  " + templateId + " \n", nfe);
			throw new NotFoundException("Resource with ID " + templateId + " not found.");
		} catch (Exception ex) {
			logger.error("/template (GET) failed - Not Found  " + templateId + " \n", ex);
			throw new Exception("Resource with ID " + templateId + ".");
		}
	}

	/**
	 * delete a campaign template
	 * 
	 * @param templateId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/template/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteCampaignTemplate(@PathVariable("id") String templateId) throws Exception {
		try {
			service.deleteCampaignTemplate(templateId);
			return new ResponseEntity<String>("{\"message\":\"Resource deleted successfully.\"}", HttpStatus.OK);
		} catch (BadRequestException nfe) {
			logger.error("/template (DELETE) - Bad Request " + templateId + " \n", nfe);
			throw new BadRequestException();
		} catch (Exception ex) {
			logger.error("/template (DELETE) - Not Found " + templateId + " \n", ex);
			throw new NotFoundException("Cannot delete resource as " + templateId + " does not exist.");
		}
	}

	/**
	 * <>
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/formatted-file", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CustomerEntity> getCustomerDataForFormattedFiles(
			@RequestParam(value = "path", required = true) String path) throws Exception {
		try {
			if (path != null && !path.isEmpty()) {
				return new ResponseEntity<CustomerEntity>(
						(CustomerEntity) service.getCustomerDataForFormattedFiles(path.trim()), HttpStatus.OK);
			}
			logger.error("/formatted-file failed - Bad Request " + path + " \n");
			throw new BadRequestException();
		} catch (Exception ex) {
			logger.error("/formatted-file failed - Server Error " + path + " \n", ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * upload a single formatted file
	 * 
	 * @param type
	 * @param date
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/formatted-file/{type}/{date}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> uploadFormattedFileToAws(@PathVariable("type") String type,
			@PathVariable("date") String date, @RequestParam("file") MultipartFile file) throws Exception {
		try {
			if (file.isEmpty()) {
				logger.error("/formatted-file (POST) - Bad Request - No file selected for upload. \n");
				throw new BadRequestException();
			}
			service.writeToAws(type, date, file);
			return new ResponseEntity<String>("{\"message\":\"File uploaded successfully.\"}", HttpStatus.OK);
		} catch (BadRequestException ex) {
			logger.error("/formatted-file (POST) failed - Bad Request for type- " + type + " -date- " + date + " \n",
					ex);
			throw new BadRequestException();
		} catch (Exception ex) {
			logger.error("/formatted-file (POST) failed - Server Error for type- " + type + " -date- " + date + " \n",
					ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * upload multiple formatted files
	 * 
	 * @param type
	 * @param date
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/formatted-files/{type}/{date}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> uploadFormattedFilesToAws(@PathVariable("type") String type,
			@PathVariable("date") String date, @RequestParam("files") MultipartFile[] files) throws Exception {
		try {
			if (files.length == 0) {
				logger.error("/formatted-files (POST) - BadRequest - No file selected for upload. \n");
				throw new BadRequestException();
			}
			service.writeToAws(type, date, files);
			return new ResponseEntity<String>("{\"message\":\"File uploaded successfully.\"}", HttpStatus.OK);
		} catch (BadRequestException ex) {
			logger.error("/formatted-files (POST) failed - Bad Request for type- " + type + " -date- " + date + " \n",
					ex);
			throw new BadRequestException();
		} catch (Exception ex) {
			logger.error("/formatted-files (POST) failed - Server Error for type- " + type + " -date- " + date + " \n",
					ex);
			throw new Exception(ex.getMessage());
		}
	}

	/**
	 * delete given formatted file from aws and cass drive
	 * 
	 * @param templateId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/formatted-file/{type}/{date}/{filename}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteFormattedFile(@PathVariable("type") String type,
			@PathVariable("date") String date, @PathVariable("filename") String fileName,
			@RequestParam(value = "flag", required = true) Boolean flag) throws Exception {
		try {
			if (flag) {
				if (service.deleteFormattedFile(type, date, fileName)) {
					return new ResponseEntity<String>("{\"message\":\"Resource deleted successfully.\"}",
							HttpStatus.OK);
				}
			} else {
				if (service.deleteFormattedFileFromAws(type, date, fileName)) {
					return new ResponseEntity<String>("{\"message\":\"Resource deleted successfully.\"}",
							HttpStatus.OK);
				}
			}
			logger.error("/formatted-file (DELETE) failed - Not Found for type- " + type + " -date- " + date
					+ " -fileName- " + fileName + " \n");
			throw new BadRequestException();
		} catch (Exception ex) {
			logger.error("/formatted-file (DELETE) failed - Not Found for type- " + type + " -date- " + date
					+ " -fileName- " + fileName + " \n", ex);
			throw new NotFoundException("Cannot delete resource as " + fileName + " doesn't exist.");
		}
	}

	/**
	 * delete formatted files from aws and cass drive
	 * 
	 * @param templateId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/formatted-files/{type}/{date}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteFormattedFiles(@PathVariable("type") String formattedFileType,
			@PathVariable("date") String date, @RequestBody List<String> fileNames,
			@RequestParam(value = "flag", required = true) Boolean flag) throws Exception {
		try {
			if (fileNames == null || fileNames.isEmpty()) {
				logger.error("/formatted-files (DELETE) failed - Bad Request, files list is empty or null for type- "
						+ formattedFileType + " -date- " + date + " \n");
				throw new BadRequestException();
			} else {
				if (flag) {
					if (service.deleteFormattedFiles(formattedFileType, date, fileNames)) {
						return new ResponseEntity<String>("{\"message\":\"Resources deleted successfully.\"}",
								HttpStatus.OK);
					}
				} else {
					if (service.deleteFormattedFilesFromAws(formattedFileType, date, fileNames)) {
						return new ResponseEntity<String>("{\"message\":\"Resources deleted successfully.\"}",
								HttpStatus.OK);
					}
				}
			}
			logger.error("/formatted-files (DELETE) failed - Not Found for type- " + formattedFileType + " -date- "
					+ date + " \n");
			throw new NotFoundException("Files delete failed - one of the resources listed is not found.");
		} catch (Exception ex) {
			throw new NotFoundException("Files delete failed - one of the resources listed is not found.");
		}
	}

	/**
	 * get customer (DynamoDB) and filter data (Hive) for given campaign and file id
	 * 
	 * @param campaignId, fileId
	 * @return customer data for the given file
	 * @throws Exception
	 */
	@RequestMapping(path = "/campaign-files/{campaignId}/{fileId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CustomerEntity> getCustomerRecordsForCampaignFiles(
			@PathVariable("campaignId") String campaignId, @PathVariable("fileId") String fileId) throws Exception {
		try {
			return new ResponseEntity<CustomerEntity>(
					(CustomerEntity) service.getCustomerRecordsForCampaignFiles(campaignId, fileId), HttpStatus.OK);
		} catch (BadRequestException ex) {
			logger.error("/campaign-files failed - Bad Request for campaignId- " + campaignId + " -fileId- " + fileId
					+ " \n", ex);
			throw new BadRequestException();
		} catch (NotFoundException ex) {
			logger.error("/campaign-files failed - Not Found, no path found in Campaign File table for campaignId- "
					+ campaignId + " -fileId- " + fileId + " \n", ex);
			throw new NotFoundException("No corresponding path found in CampaignFile table for campaign ID - "
					+ campaignId + " file ID - " + fileId);
		} catch (Exception ex) {
			logger.error("/campaign-files failed - Server Error for campaignId- " + campaignId + " -fileId- " + fileId
					+ " \n", ex);
			throw new Exception(ex.getMessage());

		}
	}

	/**
	 * get print ready or print weight data for given campaign and file id
	 * 
	 * @param campaignId
	 * @param fileId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(path = "/print-file/{campaignId}/{fileId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getPrintFileData(@PathVariable("campaignId") String campaignId,
			@PathVariable("fileId") String fileId) throws Exception {
		try {
			return new ResponseEntity<String>(service.getPrintFileData(campaignId, fileId), HttpStatus.OK);
		} catch (BadRequestException ex) {
			logger.error(
					"/print-file failed - Bad Request for campaignId- " + campaignId + " -fileId- " + fileId + " \n",
					ex);
			throw new BadRequestException();
		} catch (NotFoundException ex) {
			logger.error("/print-file failed - Not Found, no path found in Campaign File table for campaignId- "
					+ campaignId + " -fileId- " + fileId + " \n", ex);
			throw new NotFoundException("No corresponding path found in CampaignFile table for campaign ID - "
					+ campaignId + " file ID - " + fileId);
		} catch (Exception ex) {
			logger.error(
					"/print-file failed - Server Error for campaignId- " + campaignId + " -fileId- " + fileId + " \n",
					ex);
			throw new Exception(ex.getMessage());
		}
	}
}