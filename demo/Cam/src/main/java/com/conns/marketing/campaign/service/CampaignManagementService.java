package com.conns.marketing.campaign.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.conns.marketing.campaign.configuration.AppConfig;
import com.conns.marketing.campaign.configuration.AppConfigBean;
import com.conns.marketing.campaign.entity.CampaignEntity;
import com.conns.marketing.campaign.entity.CampaignFileEntity;
import com.conns.marketing.campaign.entity.CampaignTemplateEntity;
import com.conns.marketing.campaign.entity.CustomerEntity;
import com.conns.marketing.campaign.exception.BadRequestException;
import com.conns.marketing.campaign.exception.NotFoundException;
import com.conns.marketing.campaign.model.Campaign;
import com.conns.marketing.campaign.model.CampaignOutput;
import com.conns.marketing.campaign.model.CampaignOutputForSourceAndSuppression;
import com.conns.marketing.campaign.model.CampaignSearchResult;
import com.conns.marketing.campaign.model.CampaignTemplate;
import com.conns.marketing.campaign.model.CustomerDataFromHive;
import com.conns.marketing.campaign.model.ExternalFile;
import com.conns.marketing.campaign.model.Filter;
import com.conns.marketing.campaign.model.InOutErrorFile;
import com.conns.marketing.campaign.model.SearchResponse;
import com.conns.marketing.campaign.model.Source;
import com.conns.marketing.campaign.model.Suppression;
import com.conns.marketing.campaign.model.SuppressionFile;
import com.conns.marketing.campaign.model.TailAddition;
import com.conns.marketing.campaign.repository.DataSourceSingleton;
import com.conns.marketing.campaign.repository.HiveDao;
import com.conns.marketing.campaign.template.repository.CampaignFileEntityRepository;
import com.conns.marketing.campaign.template.repository.CampaignRepository;
import com.conns.marketing.campaign.template.repository.CampaignTemplateRepository;
import com.conns.marketing.campaign.template.repository.CustomerRepository;
import com.conns.marketing.campaign.template.repository.FilterRepository;
import com.conns.marketing.campaign.template.repository.StateEntityRepository;
import com.conns.marketing.campaign.utilities.DateUtil;
import com.conns.marketing.campaign.utilities.MandatoryFieldChecker;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CampaignManagementService {
	private static final Logger logger = LoggerFactory.getLogger(CampaignManagementService.class);

	ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
	AppConfigBean configBean = (AppConfigBean) applicationContext.getBean("getProfile");

	@Autowired
	private FilterRepository filterRepository;

	@Autowired
	private CampaignRepository campaignRepository;

	@Autowired
	private CampaignTemplateRepository campaignTemplateRepository;

	@Autowired
	private HiveDao hiveDao;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CampaignFileEntityRepository campaignFileEntityRepository;

	@Autowired
	private StateEntityRepository stateEntityRepository;

	/**
	 * generate for campaign ID
	 * 
	 * @return UUID
	 * @throws Exception
	 */
	public String generateGUID() throws Exception {
		return "{\"guid\":\"" + UUID.randomUUID().toString() + "\"}";
	}

	/**
	 * get active and effective campaign templates
	 * 
	 * @return active and effective campaign templates
	 * @throws Exception
	 */
	public SearchResponse getCampaignTemplates() throws Exception {
		String date = DateUtil.getCurrentUTCDate() + "T23:59:59.999Z";

		Date effectivestartDate = DateUtil.getFormattedDate(date);

		List<CampaignTemplate> campaignTemplates = new ArrayList<CampaignTemplate>();

		campaignTemplateRepository.findByStatusAndEffectiveStartDate("active", effectivestartDate)
				.forEach(campaignTemplateEntity -> {
					campaignTemplates.add(campaignTemplateEntity.getCampaignTemplate());
				});

		return new SearchResponse(campaignTemplates);

	}

	/**
	 * get all campaign templates
	 * 
	 * @return all campaign templates
	 * @throws Exception
	 */
	public SearchResponse getAllCampaignTemplates() throws Exception {

		List<CampaignTemplate> campaignTemplates = new ArrayList<CampaignTemplate>();

		campaignTemplateRepository.findAll().forEach(templateEntitiy -> {
			campaignTemplates.add(templateEntitiy.getCampaignTemplate());
		});

		return new SearchResponse(campaignTemplates);
	}

	/**
	 * get campaign output data for last x months (x taken from properties file)
	 * 
	 * @return
	 * @throws Exception
	 */
	public SearchResponse getCampaignOutput() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, configBean.getFilterCriteria());

		List<CampaignEntity> campaignEntites = campaignRepository.findByScheduledDateOutput(calendar.getTime());
		List<CampaignOutput> campaignOutputs = new ArrayList<>();

		for (CampaignEntity campaignEntity : campaignEntites) {
			CampaignOutput campaignOutput = new CampaignOutput();
			campaignOutput.setCampaignId(campaignEntity.getCampaignId());
			campaignOutput.setScheduledDate(campaignEntity.getScheduledDate());
			campaignOutput.setCampaignName(campaignEntity.getCampaignName());
			campaignOutputs.add(campaignOutput);
		}
		return new SearchResponse(campaignOutputs);
	}

	/**
	 * get campaign output data for last x months (x taken from properties file)
	 * 
	 * @param scheduledDate
	 * @return
	 * @throws Exception
	 */
	public SearchResponse getCampaignOutput(String scheduledDate) throws Exception {

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, configBean.getFilterCriteria());

		String date = scheduledDate + "T23:59:59.999Z";
		Date criteria = DateUtil.getFormattedDate(date);

		List<CampaignEntity> campaignEntites = campaignRepository.findByScheduledDate(calendar.getTime(), criteria);
		List<CampaignOutputForSourceAndSuppression> campaignOutputs = new ArrayList<>();

		for (CampaignEntity campaignEntity : campaignEntites) {
			CampaignOutputForSourceAndSuppression campaignOutput = new CampaignOutputForSourceAndSuppression();
			campaignOutput.setCampaignId(campaignEntity.getCampaignId());
			campaignOutput.setCampaignName(campaignEntity.getCampaignName());
			campaignOutput.setScheduledDate(campaignEntity.getScheduledDate());
			campaignOutput.setStatus(campaignEntity.getStatustracker());
			campaignOutputs.add(campaignOutput);

		}
		return new SearchResponse(campaignOutputs);
	}

	/**
	 * persist campaign data in CampaignMDM
	 * 
	 * @param campaign
	 * @throws Exception
	 */
	public void persistCampaign(Campaign campaign) throws Exception {
		MandatoryFieldChecker.isNull(campaign.getCampaignId());
		MandatoryFieldChecker.isNull(campaign.getCampaignName());
		campaign.setSearchKeyCampaignName(campaign.getCampaignName().toLowerCase());
		MandatoryFieldChecker.isNull(campaign.getTemplateId());
		String scheduledDate = campaign.getScheduledDate();
		MandatoryFieldChecker.isNull(scheduledDate);
		MandatoryFieldChecker.isNull(campaign.getScheduledBy());
		MandatoryFieldChecker.isNull(campaign.getStatus());
		MandatoryFieldChecker.isNull(campaign.getPromoCodePrefix());
		MandatoryFieldChecker.isNull(campaign.getProcessThroughDate());

		Source sources = campaign.getSources();
		if (sources != null) {
			MandatoryFieldChecker.isNull(sources.getMasterData());
			MandatoryFieldChecker.isNull(sources.getMasterData().getUse());
			if (sources.getAdditionalFiles() != null && !sources.getAdditionalFiles().isEmpty()) {
				List<ExternalFile> formattedFiles = sources.getAdditionalFiles();
				List<String> filesToCopy = new ArrayList<>();
				formattedFiles.forEach(x -> {
					filesToCopy.add(x.getFileName());
				});
				copyFilesFromAwsToCass("Source", DateUtil.getFormattedDate_yyyyMMdd(scheduledDate), filesToCopy);
			}
		}

		Suppression suppressions = campaign.getSuppressions();
		if (suppressions != null) {
			MandatoryFieldChecker.isNull(suppressions.getEmployeeKillFile());
			MandatoryFieldChecker.isNull(suppressions.getEmployeeKillFile().getStatus());
			MandatoryFieldChecker.isNull(suppressions.getConnsKillFile());
			MandatoryFieldChecker.isNull(suppressions.getConnsKillFile().getStatus());
			MandatoryFieldChecker.isNull(suppressions.getApprovedDeclinedCustomerKillFile());
			MandatoryFieldChecker.isNull(suppressions.getApprovedDeclinedCustomerKillFile().getStatus());
			MandatoryFieldChecker.isNull(suppressions.getBankruptcy());
			MandatoryFieldChecker.isNull(suppressions.getBankruptcy().getStatus());
			MandatoryFieldChecker.isNull(suppressions.getExclusionList());
			MandatoryFieldChecker.isNull(suppressions.getExclusionList().getStatus());
			MandatoryFieldChecker.isNull(suppressions.getStoreKillFile());
			MandatoryFieldChecker.isNull(suppressions.getStoreKillFile().getStatus());
			MandatoryFieldChecker.isNull(suppressions.getThirtyDayDelinquency());
			MandatoryFieldChecker.isNull(suppressions.getThirtyDayDelinquency().getStatus());
			MandatoryFieldChecker.isNull(suppressions.getSalesMTD());
			MandatoryFieldChecker.isNull(suppressions.getSalesMTD().getStatus());
			MandatoryFieldChecker.isNull(suppressions.getProgressiveMTD());
			MandatoryFieldChecker.isNull(suppressions.getProgressiveMTD().getStatus());
			if (suppressions.getSuppressionFiles() != null && !suppressions.getSuppressionFiles().isEmpty()) {
				List<SuppressionFile> formattedFiles = suppressions.getSuppressionFiles();
				List<String> filesToCopy = new ArrayList<>();
				formattedFiles.forEach(x -> {
					filesToCopy.add(x.getFileName());
				});
				copyFilesFromAwsToCass("Suppression", DateUtil.getFormattedDate_yyyyMMdd(scheduledDate), filesToCopy);
			}
		}

		TailAddition tailAdditions = campaign.getTailAdditions();
		if (tailAdditions != null && tailAdditions.getAdditionalFiles() != null
				&& !tailAdditions.getAdditionalFiles().isEmpty()) {
			List<ExternalFile> formattedFiles = tailAdditions.getAdditionalFiles();
			List<String> filesToCopy = new ArrayList<>();
			formattedFiles.forEach(x -> {
				filesToCopy.add(x.getFileName());
			});
			copyFilesFromAwsToCass("TailAddition", DateUtil.getFormattedDate_yyyyMMdd(scheduledDate), filesToCopy);
		}

		DateTime today = new DateTime(DateTimeZone.UTC);

		campaign.setCreatedDate(today.toString());
		campaign.setLastModifiedDate(today.toString());

		campaign.setSortGroup("1");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 4);

		CampaignEntity campaignEntity = new CampaignEntity();

		campaignEntity.setTemplateId(campaign.getTemplateId());
		campaignEntity.setCreatedDate(new Date());
		campaignEntity.setScheduledDate(calendar.getTime());
		campaignEntity.setTemplateName(campaign.getTemplateName());
		campaignEntity.setCampaignName(campaign.getCampaignName());
		campaignEntity.setCampaignId(campaign.getCampaignId());
		campaignEntity.setStatustracker(campaign.getStatus());
		campaignEntity.setCampaign(campaign);

		campaignRepository.save(campaignEntity);

	}

	/**
	 * copy files from temporary location on aws to the cass drive
	 * 
	 * @param formattedFileType
	 * @param date
	 * @param filesToCopy
	 * @throws Exception
	 */
	private void copyFilesFromAwsToCass(String formattedFileType, String date, List<String> filesToCopy)
			throws Exception {
		String inPath = File.separator + "usr" + File.separator + "local" + File.separator + "campaign" + File.separator
				+ formattedFileType.toLowerCase() + File.separator + "in" + File.separator + date;
		String outPath = File.separator + "cass" + File.separator + "CAMPAIGN" + File.separator + formattedFileType
				+ File.separator + "in" + java.io.File.separator + date;
		Path target = Paths.get(outPath);
		if (!Files.exists(target)) {
			Files.createDirectories(target);
		}
		Path in = null;
		for (String file : filesToCopy) {
			in = Paths.get(inPath + File.separator + file);
			target = Paths.get(outPath + File.separator + file);

			if (Files.exists(in)) {
				Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

	/**
	 * persist campaign template data in CampaignTemplate
	 * 
	 * @param campaignTemplate
	 * @throws Exception
	 */
	public void persistCampaignTemplate(CampaignTemplate campaignTemplate) throws Exception {
		MandatoryFieldChecker.isNull(campaignTemplate.getTemplateId());
		MandatoryFieldChecker.isNull(campaignTemplate.getTemplateName());
		DateTime today = new DateTime(DateTimeZone.UTC);
		campaignTemplate.setCreatedDate(today.toString());
		MandatoryFieldChecker.isNull(campaignTemplate.getCreatedBy());
		campaignTemplate.setLastModifiedDate(today.toString());
		MandatoryFieldChecker.isNull(campaignTemplate.getEffectiveStartDate());
		MandatoryFieldChecker.isNull(campaignTemplate.getPromoCodePrefix());
		MandatoryFieldChecker.isNull(campaignTemplate.getStatus());

		CampaignTemplateEntity campaignTemplateEntity = new CampaignTemplateEntity();
		campaignTemplateEntity.setCreatedDate(new Date());
		campaignTemplateEntity.setCampaignTemplate(campaignTemplate);
		campaignTemplateEntity.setActive(campaignTemplate.getStatus());
		campaignTemplateEntity.setEffectiveStartDate(new Date());
		campaignTemplateEntity.setLastModifiedDate(new Date());
		campaignTemplateEntity.setCreatedBy(campaignTemplate.getCreatedBy());
		campaignTemplateEntity.setTemplateId(campaignTemplate.getTemplateId());
		campaignTemplateEntity.setTemplateName(campaignTemplate.getTemplateName());

		campaignTemplateRepository.save(campaignTemplateEntity);

	}

	/**
	 * update a campaign
	 * 
	 * @param campaign
	 * @throws Exception
	 */
	public void updateCampaign(Campaign campaign) throws Exception {

		Campaign processedCampaign = new Campaign();

		if (MandatoryFieldChecker.isNull(campaign.getCampaignId())) {
			processedCampaign.setCampaignId(campaign.getCampaignId());
		}

		CampaignEntity campaignEntity = campaignRepository.findByCampaignId(campaign.getCampaignId());

		if (campaignEntity == null) {
			throw new NotFoundException("Not found campaign with id " + campaign.getCampaignId());
		}

		if (MandatoryFieldChecker.isNull(campaign.getCampaignName())) {
			processedCampaign.setCampaignName(campaign.getCampaignName());
			processedCampaign.setSearchKeyCampaignName(campaign.getCampaignName().toLowerCase());
		}

		if (MandatoryFieldChecker.isNull(campaign.getTemplateId())) {
			processedCampaign.setTemplateId(campaign.getTemplateId());
		}

		if (campaign.getTemplateName() != null) {
			processedCampaign.setTemplateName(campaign.getTemplateName());
		}

		if (MandatoryFieldChecker.isNull(campaign.getScheduledDate())) {
			processedCampaign.setScheduledDate(campaign.getScheduledDate());
		}

		String scheduledDate = campaign.getScheduledDate();

		if (MandatoryFieldChecker.isNull(campaign.getScheduledBy())) {
			processedCampaign.setScheduledBy(campaign.getScheduledBy());
		}

		if (MandatoryFieldChecker.isNull(campaign.getStatus())) {
			processedCampaign.setStatus(campaign.getStatus());
		}

		if (campaign.getStatusTracker() != null) {
			processedCampaign.setStatusTracker(campaign.getStatusTracker());
		}

		if (campaign.getErrorDesc() != null) {
			processedCampaign.setErrorDesc(campaign.getErrorDesc());
		}

		if (campaign.getAuditStatus() != null) {
			processedCampaign.setAuditStatus(campaign.getAuditStatus());
		}

		if (MandatoryFieldChecker.isNull(campaign.getCreatedDate())) {
			processedCampaign.setCreatedDate(campaign.getCreatedDate());
		}

		processedCampaign.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());

		if (campaign.getCampaignStartDate() != null) {
			processedCampaign.setCampaignStartDate(campaign.getCampaignStartDate());
		}

		if (campaign.getCampaignEndDate() != null) {
			processedCampaign.setCampaignEndDate(campaign.getCampaignEndDate());
		}

		if (MandatoryFieldChecker.isNull(campaign.getProcessThroughDate())) {
			processedCampaign.setProcessThroughDate(campaign.getProcessThroughDate());
		}

		if (MandatoryFieldChecker.isNull(campaign.getPromoCodePrefix())) {
			processedCampaign.setPromoCodePrefix(campaign.getPromoCodePrefix());
		}

		if (campaign.getAs400PromoCodeId() != null) {
			processedCampaign.setAs400PromoCodeId(campaign.getAs400PromoCodeId());
		}

		if (campaign.getMaximumRecordsToBuild() != null) {
			processedCampaign.setMaximumRecordsToBuild(campaign.getMaximumRecordsToBuild());
		}

		Source sources = campaign.getSources();
		if (sources != null) {
			if (MandatoryFieldChecker.isNull(sources.getMasterData())
					&& MandatoryFieldChecker.isNull(sources.getMasterData().getUse())) {
				processedCampaign.setSources(sources);
			}
			if (sources.getAdditionalFiles() != null && !sources.getAdditionalFiles().isEmpty()) {
				List<ExternalFile> formattedFiles = sources.getAdditionalFiles();
				List<String> filesToCopy = new ArrayList<>();
				formattedFiles.forEach(x -> {
					filesToCopy.add(x.getFileName());
				});
				copyFilesFromAwsToCass("Source", DateUtil.getFormattedDate_yyyyMMdd(scheduledDate), filesToCopy);
			}
		}

		Suppression suppressions = campaign.getSuppressions();
		if (suppressions != null) {
			if (MandatoryFieldChecker.isNull(suppressions.getEmployeeKillFile())
					&& MandatoryFieldChecker.isNull(suppressions.getApprovedDeclinedCustomerKillFile())
					&& MandatoryFieldChecker.isNull(suppressions.getBankruptcy())
					&& MandatoryFieldChecker.isNull(suppressions.getConnsKillFile())
					&& MandatoryFieldChecker.isNull(suppressions.getExclusionList())
					&& MandatoryFieldChecker.isNull(suppressions.getStoreKillFile())
					&& MandatoryFieldChecker.isNull(suppressions.getThirtyDayDelinquency())
					&& MandatoryFieldChecker.isNull(suppressions.getSalesMTD())
					&& MandatoryFieldChecker.isNull(suppressions.getProgressiveMTD())) {
				processedCampaign.setSuppressions(suppressions);
			}
			if (suppressions.getSuppressionFiles() != null && !suppressions.getSuppressionFiles().isEmpty()) {
				List<SuppressionFile> formattedFiles = suppressions.getSuppressionFiles();
				List<String> filesToCopy = new ArrayList<>();
				formattedFiles.forEach(x -> {
					filesToCopy.add(x.getFileName());
				});
				copyFilesFromAwsToCass("Suppression", DateUtil.getFormattedDate_yyyyMMdd(scheduledDate), filesToCopy);
			}
		}

		TailAddition tailAdditions = campaign.getTailAdditions();
		if (tailAdditions != null) {
			processedCampaign.setTailAdditions(campaign.getTailAdditions());
			if (tailAdditions != null && tailAdditions.getAdditionalFiles() != null
					&& !tailAdditions.getAdditionalFiles().isEmpty()) {
				List<ExternalFile> formattedFiles = tailAdditions.getAdditionalFiles();
				List<String> filesToCopy = new ArrayList<>();
				formattedFiles.forEach(x -> {
					filesToCopy.add(x.getFileName());
				});
				copyFilesFromAwsToCass("TailAddition", DateUtil.getFormattedDate_yyyyMMdd(scheduledDate), filesToCopy);
			}
		}

		if (campaign.getFilters() != null) {
			processedCampaign.setFilters(campaign.getFilters());
		}

		if (campaign.getSasCampaignOutputFile() != null) {
			processedCampaign.setSasCampaignOutputFile(campaign.getSasCampaignOutputFile());
		}

		if (campaign.getSystemCampaignOutputFile() != null) {
			processedCampaign.setSystemCampaignOutputFile(campaign.getSystemCampaignOutputFile());
		}

		if (campaign.getPrintFile() != null) {
			processedCampaign.setPrintFile(campaign.getPrintFile());
		}

		campaign.setSortGroup("1");

		campaignEntity.setCampaign(processedCampaign);
		campaignEntity.setCampaignName(campaign.getCampaignName());
		campaignEntity.setCreatedDate(DateUtil.getFormattedDate(campaign.getCreatedDate()));
		campaignEntity.setCampaignName(campaign.getCampaignName());
		campaignEntity.setScheduledDate(DateUtil.getFormattedDate(campaign.getScheduledDate()));
		campaignEntity.setStatustracker(campaign.getStatus());

		campaignRepository.save(campaignEntity);

	}

	/**
	 * update update selective fields in campaign
	 * 
	 * @param campaign
	 * @throws Exception
	 */
	public void selectivelyUpdateCampaign(Campaign campaign) throws Exception {

		MandatoryFieldChecker.isNull(campaign.getCampaignId());

		CampaignEntity campaignEntity = campaignRepository.findByCampaignId(campaign.getCampaignId());

		if (campaignEntity == null) {
			throw new NotFoundException("Not found campaign with id " + campaign.getCampaignId());
		}

		Campaign existingCampaign = new Campaign();

		if (campaign.getCampaignName() == null) {
			campaign.setCampaignName(existingCampaign.getCampaignName());
			campaign.setSearchKeyCampaignName(existingCampaign.getSearchKeyCampaignName());
		} else {
			campaign.setSearchKeyCampaignName(campaign.getCampaignName().toLowerCase());
		}

		if (campaign.getTemplateId() == null) {
			campaign.setTemplateId(existingCampaign.getTemplateId());
		}

		if (campaign.getTemplateName() == null) {
			campaign.setTemplateName(existingCampaign.getTemplateName());
		}

		if (campaign.getScheduledDate() == null) {
			campaign.setScheduledDate(existingCampaign.getScheduledDate());
		}

		if (campaign.getScheduledBy() == null) {
			campaign.setScheduledBy(existingCampaign.getScheduledBy());
		}

		String statusTracker = existingCampaign.getStatusTracker();
		if (campaign.getStatus() == null) {
			campaign.setStatus(existingCampaign.getStatus());
			if (campaign.getStatusTracker() == null) {
				campaign.setStatusTracker(statusTracker);
			}
		} else {
			if (statusTracker != null
					&& StringUtils.substringAfterLast(statusTracker, "-").equalsIgnoreCase(campaign.getStatus())) {
				campaign.setStatusTracker(statusTracker + "-" + campaign.getStatus());
			}
		}

		if (campaign.getErrorDesc() == null) {
			campaign.setErrorDesc(existingCampaign.getErrorDesc());
		}

		if (campaign.getAuditStatus() == null) {
			campaign.setAuditStatus(existingCampaign.getAuditStatus());
		}

		campaign.setCreatedDate(existingCampaign.getCreatedDate());

		campaign.setLastModifiedDate(new DateTime(DateTimeZone.UTC).toString());

		if (campaign.getCampaignStartDate() == null) {
			campaign.setCampaignStartDate(existingCampaign.getCampaignStartDate());
		}

		if (campaign.getCampaignEndDate() == null) {
			campaign.setCampaignEndDate(existingCampaign.getCampaignEndDate());
		}

		if (campaign.getProcessThroughDate() == null) {
			campaign.setProcessThroughDate(existingCampaign.getProcessThroughDate());
		}

		if (campaign.getPromoCodePrefix() == null) {
			campaign.setPromoCodePrefix(existingCampaign.getPromoCodePrefix());
		}

		if (campaign.getAs400PromoCodeId() == null) {
			campaign.setAs400PromoCodeId(existingCampaign.getAs400PromoCodeId());
		}

		if (campaign.getMaximumRecordsToBuild() == null) {
			campaign.setMaximumRecordsToBuild(existingCampaign.getMaximumRecordsToBuild());
		}

		if (campaign.getSources() == null) {
			campaign.setSources(existingCampaign.getSources());
		}

		Suppression suppressions = campaign.getSuppressions();
		if (suppressions == null) {
			campaign.setSuppressions(existingCampaign.getSuppressions());
		} else {
			if (suppressions.getEmployeeKillFile() == null) {
				campaign.getSuppressions()
						.setEmployeeKillFile(existingCampaign.getSuppressions().getEmployeeKillFile());
			} else {
				if (!suppressions.getEmployeeKillFile().getStatus()) {
					campaign.getSuppressions().getEmployeeKillFile().setSuppressionIn(null);
					campaign.getSuppressions().getEmployeeKillFile().setSuppressionOut(null);
					campaign.getSuppressions().getEmployeeKillFile().setSuppressionError(null);
				}
			}
			if (suppressions.getThirtyDayDelinquency() == null) {
				campaign.getSuppressions()
						.setThirtyDayDelinquency(existingCampaign.getSuppressions().getThirtyDayDelinquency());
			} else {
				if (!suppressions.getThirtyDayDelinquency().getStatus()) {
					campaign.getSuppressions().getThirtyDayDelinquency().setSuppressionIn(null);
					campaign.getSuppressions().getThirtyDayDelinquency().setSuppressionOut(null);
					campaign.getSuppressions().getThirtyDayDelinquency().setSuppressionError(null);
				}
			}
			if (suppressions.getApprovedDeclinedCustomerKillFile() == null) {
				campaign.getSuppressions().setApprovedDeclinedCustomerKillFile(
						existingCampaign.getSuppressions().getApprovedDeclinedCustomerKillFile());
			} else {
				if (!suppressions.getApprovedDeclinedCustomerKillFile().getStatus()) {
					campaign.getSuppressions().getApprovedDeclinedCustomerKillFile().setSuppressionIn(null);
					campaign.getSuppressions().getApprovedDeclinedCustomerKillFile().setSuppressionOut(null);
					campaign.getSuppressions().getApprovedDeclinedCustomerKillFile().setSuppressionError(null);
				}
			}
			if (suppressions.getConnsKillFile() == null) {
				campaign.getSuppressions().setConnsKillFile(existingCampaign.getSuppressions().getConnsKillFile());
			} else {
				if (!suppressions.getConnsKillFile().getStatus()) {
					campaign.getSuppressions().getConnsKillFile().setSuppressionIn(null);
					campaign.getSuppressions().getConnsKillFile().setSuppressionOut(null);
					campaign.getSuppressions().getConnsKillFile().setSuppressionError(null);
				}
			}
			if (suppressions.getStoreKillFile() == null) {
				campaign.getSuppressions().setStoreKillFile(existingCampaign.getSuppressions().getStoreKillFile());
			} else {
				if (!suppressions.getStoreKillFile().getStatus()) {
					campaign.getSuppressions().getStoreKillFile().setSuppressionIn(null);
					campaign.getSuppressions().getStoreKillFile().setSuppressionOut(null);
					campaign.getSuppressions().getStoreKillFile().setSuppressionError(null);
				}
			}
			if (suppressions.getExclusionList() == null) {
				campaign.getSuppressions().setExclusionList(existingCampaign.getSuppressions().getExclusionList());
			} else {
				if (!suppressions.getExclusionList().getStatus()) {
					campaign.getSuppressions().getExclusionList().setSuppressionIn(null);
					campaign.getSuppressions().getExclusionList().setSuppressionOut(null);
					campaign.getSuppressions().getExclusionList().setSuppressionError(null);
				}
			}
			if (suppressions.getBankruptcy() == null) {
				campaign.getSuppressions().setBankruptcy(existingCampaign.getSuppressions().getBankruptcy());
			} else {
				if (!suppressions.getBankruptcy().getStatus()) {
					campaign.getSuppressions().getBankruptcy().setSuppressionIn(null);
					campaign.getSuppressions().getBankruptcy().setSuppressionOut(null);
					campaign.getSuppressions().getBankruptcy().setSuppressionError(null);
				}
			}
			if (suppressions.getSalesMTD() == null) {
				campaign.getSuppressions().setSalesMTD(existingCampaign.getSuppressions().getSalesMTD());
			} else {
				if (!suppressions.getSalesMTD().getStatus()) {
					campaign.getSuppressions().getSalesMTD().setSuppressionIn(null);
					campaign.getSuppressions().getSalesMTD().setSuppressionOut(null);
					campaign.getSuppressions().getSalesMTD().setSuppressionError(null);
				}
			}
			if (suppressions.getProgressiveMTD() == null) {
				campaign.getSuppressions().setProgressiveMTD(existingCampaign.getSuppressions().getProgressiveMTD());
			} else {
				if (!suppressions.getProgressiveMTD().getStatus()) {
					campaign.getSuppressions().getProgressiveMTD().setSuppressionIn(null);
					campaign.getSuppressions().getProgressiveMTD().setSuppressionOut(null);
					campaign.getSuppressions().getProgressiveMTD().setSuppressionError(null);
				}
			}
			if (suppressions.getSuppressionFiles() == null) {
				campaign.getSuppressions()
						.setSuppressionFiles(existingCampaign.getSuppressions().getSuppressionFiles());
			}
			if (suppressions.getSuppressionsCampaignOutputs() == null) {
				campaign.getSuppressions().setSuppressionsCampaignOutputs(
						existingCampaign.getSuppressions().getSuppressionsCampaignOutputs());
			}
		}

		if (campaign.getTailAdditions() == null) {
			campaign.setTailAdditions(existingCampaign.getTailAdditions());
		}

		if (campaign.getFilters() == null) {
			campaign.setFilters(existingCampaign.getFilters());
		} else {
			campaign.getFilters().forEach(filter -> {
				if (existingCampaign.getFilters() != null && existingCampaign.getFilters().contains(filter)) {
					Filter exFilter = existingCampaign.getFilters().get(existingCampaign.getFilters().indexOf(filter));
					if (filter.getSeqId() == null) {
						filter.setSeqId(exFilter.getSeqId());
					}
					if (filter.getType() == null) {
						filter.setType(exFilter.getType());
					}
					if (filter.getFilterId().equals("fl_zip") && filter.getData() == null) {
						filter.setData(exFilter.getData());
					}
					if (filter.getOffset() == null) {
						filter.setOffset(exFilter.getOffset());
					}
				}
			});
		}

		if (campaign.getSystemCampaignOutputFile() == null && existingCampaign.getSystemCampaignOutputFile() != null) {
			campaign.setSystemCampaignOutputFile(existingCampaign.getSystemCampaignOutputFile());
		}

		if (campaign.getSasCampaignOutputFile() == null && existingCampaign.getSasCampaignOutputFile() != null) {
			campaign.setSasCampaignOutputFile(existingCampaign.getSasCampaignOutputFile());
		}

		if (campaign.getPrintFile() == null && existingCampaign.getPrintFile() != null) {
			campaign.setPrintFile(existingCampaign.getPrintFile());
		}

		campaign.setSortGroup("1");
		campaignEntity.setCampaign(campaign);
		campaignEntity.setCampaignName(existingCampaign.getCampaignName());
		campaignEntity.setCreatedDate(new Date());
		campaignEntity.setCampaignName(existingCampaign.getCampaignName());
		campaignEntity.setScheduledDate(new Date());
		campaignEntity.setStatustracker(existingCampaign.getStatus());

		campaignRepository.save(campaignEntity);
	}

	/**
	 * get data for a given campaign id
	 * 
	 * @param campaignID
	 * @return
	 * @throws Exception
	 */
	public Campaign getCampaign(String campaignID) throws Exception {

		Campaign campaign = campaignRepository.findCampaignById(campaignID);
		if (campaign == null) {
			throw new NotFoundException("\"Resource with ID \" + campainId + \" not found.\"");
		}

		return campaign;
	}

	/**
	 * get filter data for given type to populate drop down
	 * 
	 * @param type - filter types (example: payment-types, customer-types, etc)
	 * @return filter data
	 * @throws Exception
	 */
	public String getFilterDataType(String type) throws Exception {
		String response = null;
		DataSourceSingleton instance = DataSourceSingleton.getInstance();
		try {
			response = instance.jsonNode.get(type).toString();
		} catch (NullPointerException ex) {
			logger.error("getFilterDataType - BadRequest - NullPointer \n", ex);
			throw new BadRequestException();
		}
		return "{\"Items\":" + response + "}";
	}

	/**
	 * get master data for given type
	 * 
	 * @param type - master types (example: states, countries, etc)
	 * @return master data
	 * @throws Exception
	 */

	public List<?> getMasterData(String type) throws Exception {

		List<?> items = null;

		switch (type.toLowerCase()) {
		case "state":
		case "states":
			items = (List<?>) stateEntityRepository.findAll();
			break;

		case "filters":
		case "filter":
			items = filterRepository.findAll().get(0).getFilter();
			break;

		default:
			throw new BadRequestException("Invalid type");
		}

		return items;
	}

	/**
	 * get campaigns based on status
	 * 
	 * @param status
	 * @return a list of campaign
	 * @throws Exception
	 */
	public SearchResponse getAllCampaignWithStatus(String status) throws Exception {

		String startDateStr = DateUtil.getCurrentUTCDate() + "T00:00:00.000Z";
		String enddateStr = DateUtil.getCurrentUTCDate() + "T23:59:59.999Z";

		Date startDate = DateUtil.getFormattedDate(startDateStr);
		Date endDate = DateUtil.getFormattedDate(enddateStr);

		List<Campaign> campaigns = campaignRepository.findByStatus(status, startDate, endDate);
		return new SearchResponse(campaigns);
	}

	/**
	 * search campaign based on name and criteria
	 * 
	 * @param criteria1
	 * @param criteria2
	 * @param limit
	 * @param lastEvaluatedKey
	 * @return a list of campaign
	 * @throws Exception
	 */
	public SearchResponse searchByName(String criteria1, String criteria2, String limit, String page) throws Exception {

		Integer pageInt = 1;
		try {
			pageInt = Integer.parseInt(page);
		} catch (NumberFormatException ex) {
			// ignore error for initial release
			logger.error("searchByScheduledDate - BadRequest - Couldn't parse size " + page + " \n", ex);
		}

		Integer limitInt = 1;
		try {
			limitInt = Integer.parseInt(limit);
		} catch (NumberFormatException ex) {
			logger.error("searchByScheduledDate - BadRequest - Couldn't parse limit " + limit + " \n", ex);
			throw new BadRequestException();
		}

		List<CampaignSearchResult> campaigns = new ArrayList<>();

		String startDateStr = criteria2 + "T00:00:00.000Z";
		String endDateStr = criteria2 + "T23:59:59.999Z";

		Date startDate = DateUtil.getFormattedDate(startDateStr);
		Date endDate =DateUtil.getFormattedDate(endDateStr);

		Pageable pageable = PageRequest.of(pageInt, limitInt);

		List<CampaignEntity> campaignEntities = campaignRepository.findByName(criteria1, startDate, endDate, pageable);

		for (CampaignEntity campaignEntity : campaignEntities) {
			CampaignSearchResult campaign = new CampaignSearchResult();
			campaign.setCampaignId(campaignEntity.getCampaignId());
			campaign.setCampaignName(campaignEntity.getCampaignName());
			campaign.setScheduledDate(DateUtil.getCurrentUTCDate());
			campaign.setStatus(campaignEntity.getStatustracker());
			campaigns.add(campaign);

		}
		return new SearchResponse(campaigns);

	}

	/**
	 * search campaign based on date and criteria
	 * 
	 * @param criteria1
	 * @param criteria2
	 * @param limit
	 * @param lastEvaluatedKey
	 * @return a list of campaign
	 * @throws Exception
	 */
	public SearchResponse searchByScheduledDate(String criteria1, String criteria2, String limit, String page)
			throws Exception {

		Integer limitInt = 0;
		try {
			limitInt = Integer.parseInt(limit);
		} catch (NumberFormatException ex) {
			logger.error("searchByScheduledDate - BadRequest - Couldn't parse limit " + limit + " \n", ex);
			throw new BadRequestException();
		}

		Integer pageInt = 1;
		try {
			pageInt = Integer.parseInt(page);
		} catch (NumberFormatException ex) {
			// ignore error for initial release
			logger.error("searchByScheduledDate - BadRequest - Couldn't parse size " + page + " \n", ex);

		}

		String startDateStr = criteria1 + "T00:00:00.000Z";
		String endDateStr = criteria2 + "T23:59:59.999Z";

		Date startDate = DateUtil.getFormattedDate(startDateStr);
		Date endDate = DateUtil.getFormattedDate(endDateStr);

		Pageable pageable = PageRequest.of(pageInt, limitInt);
		
		List<CampaignSearchResult> campaigns = new ArrayList<>();
		
		List<CampaignEntity> campaignEntities = campaignRepository.findByScheduledDateOutput(startDate, endDate,
				pageable);

		for (CampaignEntity campaignEntity : campaignEntities) {
			CampaignSearchResult campaign = new CampaignSearchResult();
			campaign.setCampaignId(campaignEntity.getCampaignId());
			campaign.setCampaignName(campaignEntity.getCampaignName());
			campaign.setScheduledDate(DateUtil.getCurrentUTCDate());
			campaign.setStatus(campaignEntity.getStatustracker());
			campaigns.add(campaign);

		}
		return new SearchResponse(campaigns);
	}

	/**
	 * delete a given campaign template
	 * 
	 * @param templateId
	 * @throws Exception
	 */
	public void deleteCampaignTemplate(String templateId) throws Exception {

		CampaignTemplateEntity campaignTemplateEntity = campaignTemplateRepository.findCampaignById(templateId);
		campaignTemplateEntity.setActive("Deleted");
		campaignTemplateEntity.setEffectiveEndDate(new Date());
		campaignTemplateEntity.setLastModifiedDate(new Date());

		CampaignTemplate existingCampaignTemplate = campaignTemplateEntity.getCampaignTemplate();
		String today = new DateTime(DateTimeZone.UTC).toString();
		existingCampaignTemplate.setStatus("Deleted");
		existingCampaignTemplate.setEffectiveEndDate(today);
		existingCampaignTemplate.setLastModifiedDate(today);

		campaignTemplateRepository.save(campaignTemplateEntity);

	}

	/**
	 * get data for a given template id
	 * 
	 * @param templateId
	 * @return
	 * @throws Exception
	 */
	public CampaignTemplate getCampaignTemplate(String templateId) throws Exception {

		CampaignTemplate template = campaignTemplateRepository.findByTemplateId(templateId);

		if (template == null) {
			throw new NotFoundException("\"Resource with ID \" + templateId + \" not found.\"");
		}

		return template;
	}

	/**
	 * write a single file to a temporary location at aws
	 * 
	 * @param formattedFileType
	 * @param date
	 * @param file
	 * @throws Exception
	 */
	public void writeToAws(String formattedFileType, String date, MultipartFile file) throws Exception {
		Path directoryPath = Paths.get(File.separator + "usr" + File.separator + "local" + File.separator + "campaign"
				+ File.separator + formattedFileType.toLowerCase() + File.separator + "in" + File.separator + date);

		byte[] bytes = file.getBytes();

		if (!Files.exists(directoryPath)) {
			Files.createDirectories(directoryPath);
		}

		Path path = Paths.get(directoryPath + File.separator + file.getOriginalFilename());
		Files.write(path, bytes);
	}

	/**
	 * write multiple files to a temporary location at aws
	 * 
	 * @param formattedFileType
	 * @param date
	 * @param files
	 * @throws Exception
	 */
	public void writeToAws(String formattedFileType, String date, MultipartFile[] files) throws Exception {
		Path directoryPath = Paths.get(File.separator + "usr" + File.separator + "local" + File.separator + "campaign"
				+ File.separator + formattedFileType.toLowerCase() + File.separator + "in" + File.separator + date);

		if (!Files.exists(directoryPath)) {
			Files.createDirectories(directoryPath);
		}

		for (MultipartFile file : files) {
			if (!file.isEmpty()) {
				byte[] bytes = file.getBytes();
				Path path = Paths.get(directoryPath + File.separator + file.getOriginalFilename());
				Files.write(path, bytes);
			}
		}
	}

	/**
	 * get customer records corresponding to the given formatted file (additional
	 * files in source & tail addition and suppression files in suppression section)
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public List<CustomerEntity> getCustomerDataForFormattedFiles(String file) throws Exception {
		Stream<String> lines = Files.lines(Paths.get(file));
		List<String> customerIdsFromFile = lines.skip(1).limit(101).collect(Collectors.toList());
		lines.close();

		List<CustomerEntity> customers = new ArrayList<>();

		if (customerIdsFromFile != null && !customerIdsFromFile.isEmpty()) {
			int range = customerIdsFromFile.size() / 2;
			List<List<String>> customerIds = new ArrayList<>(customerIdsFromFile.stream()
					.collect(Collectors.partitioningBy(s -> customerIdsFromFile.indexOf(s) > range)).values());

			customerIds.forEach(x -> customers.addAll(getCustomerData(x)));
		}
		return customers;
	}

	/**
	 * get customer records from CustomerMDM table for given list of unique_cid
	 * 
	 * @param customerIds
	 * @return
	 */
	private List<CustomerEntity> getCustomerData(List<String> customerIds) {
		return customerRepository.findCustomerByIdIn(customerIds);
	}

	/**
	 * delete file from temporary location on aws server and cass drive
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public Boolean deleteFormattedFile(String formattedFileType, String date, String fileName) throws Exception {
		Boolean flag = false;
		Path path = Paths.get(File.separator + "usr" + File.separator + "local" + File.separator + "campaign"
				+ File.separator + formattedFileType.toLowerCase() + File.separator + "in" + File.separator + date
				+ File.separator + fileName);
		if (Files.deleteIfExists(path)) {
			flag = true;
		} else {
			throw new NotFoundException();
		}

		path = Paths.get(File.separator + "cass" + File.separator + "CAMPAIGN" + File.separator + formattedFileType
				+ File.separator + "in" + File.separator + date + File.separator + fileName);
		if (Files.deleteIfExists(path)) {
			flag = true;
		} else {
			throw new NotFoundException();
		}
		return flag;
	}

	/**
	 * delete file from temporary location on aws server
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public Boolean deleteFormattedFileFromAws(String formattedFileType, String date, String fileName) throws Exception {
		Path path = Paths.get(File.separator + "usr" + File.separator + "local" + File.separator + "campaign"
				+ File.separator + formattedFileType.toLowerCase() + File.separator + "in" + File.separator + date
				+ File.separator + fileName);
		if (Files.deleteIfExists(path)) {
			return true;
		} else {
			throw new NotFoundException();
		}
	}

	/**
	 * delete files from temporary location on aws server and cass drive
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public Boolean deleteFormattedFiles(String formattedFileType, String date, List<String> fileNames)
			throws Exception {
		Boolean flag = true;
		String filePath = File.separator + "usr" + File.separator + "local" + File.separator + "campaign"
				+ File.separator + formattedFileType.toLowerCase() + File.separator + "in" + File.separator + date;
		Path path = null;
		for (String fileName : fileNames) {
			path = Paths.get(filePath + File.separator + fileName);
			if (Files.deleteIfExists(path)) {
				continue;
			} else {
				throw new NotFoundException();
			}
		}

		filePath = File.separator + "cass" + File.separator + "CAMPAIGN" + File.separator + formattedFileType
				+ File.separator + "in" + java.io.File.separator + date;
		for (String fileName : fileNames) {
			path = Paths.get(filePath + File.separator + fileName);
			if (Files.deleteIfExists(path)) {
				continue;
			} else {
				throw new NotFoundException();
			}
		}
		return flag;
	}

	/**
	 * delete files from temporary location on aws server
	 * 
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public Boolean deleteFormattedFilesFromAws(String formattedFileType, String date, List<String> fileNames)
			throws Exception {
		Boolean flag = true;
		String filePath = File.separator + "usr" + File.separator + "local" + File.separator + "campaign"
				+ File.separator + formattedFileType.toLowerCase() + File.separator + "in" + File.separator + date;
		Path path = null;
		for (String fileName : fileNames) {
			path = Paths.get(filePath + File.separator + fileName);
			if (Files.deleteIfExists(path)) {
				continue;
			} else {
				throw new NotFoundException();
			}
		}
		return flag;
	}

	/**
	 * get customer (DynamoDB) and filter data (Hive) for given campaign and file id
	 * 
	 * @param campaignId
	 * @param fileId
	 * @return
	 * @throws Exception
	 */
	public List<CustomerEntity> getCustomerRecordsForCampaignFiles(String campaignId, String fileId) throws Exception {
		List<CampaignFileEntity> campaignFiles = getCampaignFilePath(campaignId, fileId);
		if (campaignFiles != null && !campaignFiles.isEmpty()) {
			Configuration config = new Configuration();
			config.set("fs.defaultFS", configBean.getNameNode()); // set FileSystem URI
			config.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()); // for Maven
			config.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

			System.setProperty("HADOOP_USER_NAME", "hdfs");
			System.setProperty("hadoop.home.dir", "/");

			InOutErrorFile inOutErrorFile = new InOutErrorFile();

			try (FileSystem fileSystem = FileSystem.get(URI.create(configBean.getNameNode()), config)) {
				String filePath = campaignFiles.get(0).getItem().getFile_path();
				FileStatus[] fileStatuses = fileSystem.listStatus(new org.apache.hadoop.fs.Path(filePath));
				if (fileStatuses.length > 1) {
					inOutErrorFile = getCustomerIds(fileStatuses[1], fileSystem,
							campaignFiles.get(0).getItem().getExecution_id());
				}
			}

			return inOutErrorFile.getCustomers();
		}
		throw new NotFoundException();

	}

	/**
	 * get hdfs path for given campaign and file id
	 * 
	 * @param campaignId
	 * @param fileId
	 * @return
	 * @throws Exception
	 */
	private List<CampaignFileEntity> getCampaignFilePath(String campaignId, String fileId) throws Exception {
		return campaignFileEntityRepository.findByCampaignIdAndFileId(campaignId, fileId);
	}

	/**
	 * get hdfs part-xxxx file content for a given file type (filePath)
	 * 
	 * @param fileStatus
	 * @param fileSystem
	 * @param customerIds
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	private InOutErrorFile getCustomerIds(FileStatus fileStatus, FileSystem fileSystem, String executionId) {
		try {
			FSDataInputStream inputStream = fileSystem.open(fileStatus.getPath());
			String fileContent = IOUtils.toString(inputStream, "UTF-8");
			String[] customerListRaw = fileContent.split("\\n");

			List<String> customerIds = Stream.of(customerListRaw).skip(1).limit(100).collect(Collectors.toList());

			inputStream.close();
			fileSystem.close();

			InOutErrorFile data = new InOutErrorFile();
			List<CustomerEntity> customers = new ArrayList<>();
			if (customerIds != null && !customerIds.isEmpty()) {
				int size = customerIds.size();
				List<List<String>> ids = new ArrayList<>();
				ids.add(new ArrayList<>(customerIds.subList(0, (size + 1) / 2)));
				ids.add(new ArrayList<>(customerIds.subList((size + 1) / 2, size)));
				ids.forEach(x -> customers.addAll(getCustomerData(x)));

				data.setCustomers(customers);
			}
			try {
				data.setCustomerData(getCustomerDataFromHive(customerIds, executionId));
			} catch (ClassNotFoundException | SQLException e) {
				logger.error("Failed to get customer data ", e);
			}
			return data;
		} catch (IOException e) {
			logger.error("getCustomerIds - IOException - Couldn't read file \n", e);
		}
		return null;
	}

	/**
	 * get customer filter data from Hive for given customer ids and filter type
	 * 
	 * @param customerIds
	 * @param executionId
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	private List<CustomerDataFromHive> getCustomerDataFromHive(List<String> customerIds, String executionId)
			throws ClassNotFoundException, SQLException {
		String joiner = "'" + StringUtils.join(customerIds, "','") + "'";
		String table = configBean.getCmsDpcTable();

		switch (executionId) {
		case "fl_state":
			return hiveDao.getData(joiner, "cass_stateprovince", table);
		case "fl_zip":
			return hiveDao.getData(joiner, "cass_postalcode_base", table); // array
		case "fl_fico_refresh":
			return hiveDao.getDataWithDecimal(joiner, "fico_score", table);
		case "fl_acc_bal":
			return hiveDao.getDataWithDecimal(joiner, "account_balance", table);
		case "fl_acc_age":
			return hiveDao.getData(joiner, "opened", table);
		case "fl_invalid_ssn":
			return hiveDao.getDataWithDecimal(joiner, "owner1_ssn_nbr", table);
		case "fl_income":
			return hiveDao.getDataWithDecimal(joiner, "monthly_income", table);
		case "fl_min_credit":
			return hiveDao.getDataWithDecimal(joiner, "credit_available", table);
		case "fl_princ_bal":
			return hiveDao.getDataWithDecimal(joiner, "principal_balance", table);
		case "fl_loan_type":
			return hiveDao.getData(joiner, "status_loan_class", table); // array
		case "fl_dob":
			return hiveDao.getData(joiner, "dob_new", table);
		case "fl_acc_class_type":
			return hiveDao.getData(joiner, "status_loan_class", table); // array
		case "fl_acc_status":
			return hiveDao.getData(joiner, "status_loan_class", table); // array
		case "fl_acc_status_class_type":
			return hiveDao.getData(joiner, "status_loan_class", table); // array
		case "fl_past_due":
			return hiveDao.getData(joiner, "status_loan_class", table); // array
		case "fl_cust_type":
			return hiveDao.getData(joiner, "customer_type", table);
		case "fl_acc_per_profile":
			return hiveDao.getDataWithDecimal(joiner, "valid_acc", table);
		case "fl_last_pay":
			return hiveDao.getData(joiner, "last_payment", table);
		}
		return null;
	}

	/**
	 * get print ready or print weight data for given campaign and file id
	 * 
	 * @param campaignId
	 * @param fileId
	 * @return
	 * @throws Exception
	 */
	public String getPrintFileData(String campaignId, String fileId) throws Exception {
		List<CampaignFileEntity> campaignFiles = getCampaignFilePath(campaignId, fileId);
		if (campaignFiles != null && !campaignFiles.isEmpty()) {
			Configuration config = new Configuration();
			config.set("fs.defaultFS", configBean.getNameNode()); // set FileSystem URI
			config.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName()); // for Maven
			config.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());

			System.setProperty("HADOOP_USER_NAME", "hdfs");
			System.setProperty("hadoop.home.dir", "/");

			try (FileSystem fileSystem = FileSystem.get(URI.create(configBean.getNameNode()), config)) {
				String filePath = campaignFiles.get(0).getItem().getFile_path();
				return "{\"Items\": " + new ObjectMapper().writeValueAsString(getPrintFileContent(filePath, fileSystem))
						+ " }";
			}
		}
		throw new NotFoundException();
	}

	/**
	 * get print file content from given path
	 * 
	 * @param filePath
	 * @param fileSystem
	 * @return
	 * @throws Exception
	 */
	private String[] getPrintFileContent(String filePath, FileSystem fileSystem) throws Exception {
		FileStatus[] fileStatus = fileSystem.listStatus(new org.apache.hadoop.fs.Path(filePath));
		String printFileContent = IOUtils.toString(fileSystem.open(fileStatus[0].getPath()), "UTF-8");
		return printFileContent.split("\\n");
	}


}
