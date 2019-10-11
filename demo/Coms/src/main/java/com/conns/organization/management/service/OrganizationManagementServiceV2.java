
package com.conns.organization.management.service;

import static com.conns.organization.management.utilities.MandatoryFieldChecker.isNull;
import static org.apache.commons.lang3.StringUtils.isNoneEmpty;
import static org.springframework.util.ObjectUtils.nullSafeToString;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.conns.organization.management.configuration.AppConfig;
import com.conns.organization.management.entity.DistrictStoreRelationEnitity;
import com.conns.organization.management.entity.LocationMasterEntity;
import com.conns.organization.management.entity.RegionEntity;
import com.conns.organization.management.entity.StoreClassificationEntity;
import com.conns.organization.management.entity.StoreEntity;
import com.conns.organization.management.exception.BadRequestException;
import com.conns.organization.management.exception.NotFoundException;
import com.conns.organization.management.model.ClosedStore;
import com.conns.organization.management.model.District;
import com.conns.organization.management.model.LocationDetailsForDate;
import com.conns.organization.management.model.LocationV2;
import com.conns.organization.management.model.SearchResponse;
import com.conns.organization.management.model.Store;
import com.conns.organization.management.model.StoreClassification;
import com.conns.organization.management.model.StoreQuota;
import com.conns.organization.management.repository.DistrictRepository;
import com.conns.organization.management.repository.DistrictStoreRepository;
import com.conns.organization.management.repository.LocationRepository;
import com.conns.organization.management.repository.RegionRepository;
import com.conns.organization.management.repository.StoreClassificationRepository;
import com.conns.organization.management.repository.StoreRepository;
import com.conns.organization.management.utilities.Constants;
import com.conns.organization.management.utilities.DateUtil;
import com.conns.organization.management.utilities.JsonUtil;


@Service
public class OrganizationManagementServiceV2 implements Constants {

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private RegionRepository regionRepository;

	@Autowired
	private DistrictRepository districtRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private DistrictStoreRepository districtStoreRepository;

	@Autowired
	private StoreClassificationRepository storeClassificationRepository;

	private static final Logger logger = LoggerFactory.getLogger(OrganizationManagementServiceV2.class);

	ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
	ModelMapper modelMapper = (ModelMapper) applicationContext.getBean("modelMapper");

	public String getLocations(int page, int size) throws Exception {
		logger.debug("getLocations() called with page === " + page + " , size == " + size);
		Pageable pagebale = PageRequest.of(page, size);

		List<LocationV2> locations = locationRepository.findAll(pagebale).stream().map(post -> convertToLocation(post))
				.collect(Collectors.toList());

		return "{\"Items\":" + JsonUtil.toLocationString(locations) + "}";
	}

	public Integer createLocation(LocationV2 locationRequest) throws Exception {
		logger.debug("createLocation() called with locationRequest === " + locationRequest);

		// generate and set guid

		LocationMasterEntity locationEntity = modelMapper.map(locationRequest, LocationMasterEntity.class);
		locationRequest.setLocationGUID(generateUniqueGUID());
		locationEntity.setLastModifiedDate(new Date());

		locationEntity.getInfo().setLocationNbr(locationEntity.getLocationNbr());
		locationEntity.getInfo().setLocationMaster(locationEntity);

		locationRepository.save(locationEntity);

		return locationEntity.getId();
	}

	public Integer updateLocation(LocationV2 locationRequest) throws Exception {
		logger.debug("createLocation() called with locationRequest === " + locationRequest);

		LocationMasterEntity locationEntity = locationRepository.findByLocationGUID(locationRequest.getLocationGUID());

		if (ObjectUtils.isEmpty(locationEntity)) {
			throw new NotFoundException("No entity found with guid == " + locationRequest.getLocationGUID());
		}

		locationRequest.setId(locationEntity.getId());
		modelMapper.map(locationRequest, locationEntity);

		locationEntity.setLastModifiedDate(new Date());
		locationEntity.getInfo().setLastModifiedDate(new Date());
		locationEntity.getInfo().setLocationNbr(locationEntity.getLocationNbr());

		locationRepository.save(locationEntity);

		return locationEntity.getId();
	}

	
	private String generateUniqueGUID() {
		String generated;
		do {
			generated = UUID.randomUUID().toString();
		} while (locationRepository.existsBylocationGUID(generated));
		return generated;
	}

	private LocationV2 convertToLocation(LocationMasterEntity locationEntity) {
		LocationV2 location = modelMapper.map(locationEntity, LocationV2.class);
		return location;
	}

	public SearchResponse getRegions() throws Exception {
		logger.debug("getRegions()");
		List<RegionEntity> regionEntities = (List<RegionEntity>) regionRepository.findAll();

		return new SearchResponse(regionEntities);

	}

	/**
	 *  GET DISTRICT BY REGION ID
	 * @param regionId
	 * @return
	 * @throws Exception
	 */
	public SearchResponse getDistrictByRegionId(String regionId) throws Exception {
		logger.debug("getDistrictByRegionId() with number " + regionId);

		return new SearchResponse(districtRepository.findByRegionId(regionId).stream().map(districtEntity -> {
			District district = new District();
			district.setDistrictId(nullSafeToString(districtEntity[0]));
			district.setDistrictName(nullSafeToString(districtEntity[1]));

			return district;
		}).collect(Collectors.toList()));

	}

	
	/**
	 * 
	 * GET store by district id where effective end date = 2999-12-31
	 * @param districtId
	 * @return
	 * @throws Exception
	 */
	public SearchResponse getStoreByDistrictId(Integer districtId) throws Exception {
		logger.debug("getStoreByDistrictId() with number " + districtId);

		return new SearchResponse(storeRepository.findByDistrictId(districtId).stream().map(storeEntity -> {
			StoreClassification store = new StoreClassification();
			store.setStoreId((Integer) storeEntity[0]);
			store.setStoreNbr((Integer) storeEntity[1]);
			store.setStoreLocation(nullSafeToString(storeEntity[2]));
			store.setEffectiveEndDate(nullSafeToString(storeEntity[3]));

			return store;
		}).collect(Collectors.toList()));

	}

	
	public SearchResponse getclosedStore() {

		logger.debug("getting store by store procedure");
		return new SearchResponse(storeRepository.findByStoreCloseDate().stream().map(closedStoreEntity -> {

			ClosedStore closedStore = new ClosedStore();

			closedStore.setStoreId(nullSafeToString(closedStoreEntity[0]));
			closedStore.setStoreNbr(nullSafeToString(closedStoreEntity[1]));
			closedStore.setStoreName(nullSafeToString(closedStoreEntity[2]));
			closedStore.setStoreOpenDate(nullSafeToString(closedStoreEntity[3]));
			closedStore.setStoreLocation(nullSafeToString(closedStoreEntity[4]));
			closedStore.setStoreClassification(nullSafeToString(closedStoreEntity[5]));
			closedStore.setStoreManager(nullSafeToString(closedStoreEntity[6]));
			closedStore.setStoreType(nullSafeToString(closedStoreEntity[7]));
			closedStore.setAddressLine1(nullSafeToString(closedStoreEntity[8]));
			closedStore.setAddressLine2(nullSafeToString(closedStoreEntity[9]));
			closedStore.setCity(nullSafeToString(closedStoreEntity[10]));
			closedStore.setState(nullSafeToString(closedStoreEntity[11]));
			closedStore.setZip(nullSafeToString(closedStoreEntity[12]));
			closedStore.setCounty(nullSafeToString(closedStoreEntity[13]));
			closedStore.setStorePhone(nullSafeToString(closedStoreEntity[14]));
			closedStore.setStoreEmail(nullSafeToString(closedStoreEntity[15]));
			closedStore.setLatitude(nullSafeToString(closedStoreEntity[16]));
			closedStore.setLongitude(nullSafeToString(closedStoreEntity[17]));
			closedStore.setRegionId(nullSafeToString(closedStoreEntity[18]));
			closedStore.setRegionName(nullSafeToString(closedStoreEntity[19]));
			closedStore.setDistrictId(nullSafeToString(closedStoreEntity[20]));
			closedStore.setDistrictName(nullSafeToString(closedStoreEntity[21]));
			return closedStore;

		}).collect(Collectors.toList()));

	}

	
	public SearchResponse getLocationDetailsForDate(String date) {

		return new SearchResponse(locationRepository.findByLocationDetailsForDate(date).stream().map(location -> {
			LocationDetailsForDate detailsForDate = new LocationDetailsForDate();

			detailsForDate.setRegionId(nullSafeToString(location[0]));
			detailsForDate.setDistrictId(nullSafeToString(location[1]));
			detailsForDate.setRegionName(nullSafeToString(location[2]));
			detailsForDate.setRvpFirstName(nullSafeToString(location[3]));
			detailsForDate.setRvpLastName(nullSafeToString(location[4]));
			detailsForDate.setStoreId(nullSafeToString(location[5]));
			detailsForDate.setStoreNbr(nullSafeToString(location[6]));
			detailsForDate.setStoreName(nullSafeToString(location[7]));
			detailsForDate.setStoreStartDate(nullSafeToString(location[8]));
			detailsForDate.setStoreOpenDate(nullSafeToString(location[9]));
			detailsForDate.setDistrictName(nullSafeToString(location[10]));
			detailsForDate.setDistrictManager(nullSafeToString(location[11]));
			detailsForDate.setDmHireDate(nullSafeToString(location[12]));
			detailsForDate.setStoreLocation(nullSafeToString(location[13]));
			detailsForDate.setStoreClassification(nullSafeToString(location[14]));
			detailsForDate.setStoreManager(nullSafeToString(location[15]));
			detailsForDate.setStoreType(nullSafeToString(location[16]));
			detailsForDate.setAddressLine1(nullSafeToString(location[17]));
			detailsForDate.setAddressLine2(nullSafeToString(location[18]));
			detailsForDate.setCity(nullSafeToString(location[19]));
			detailsForDate.setState(nullSafeToString(location[20]));
			detailsForDate.setZip(nullSafeToString(location[21]));
			detailsForDate.setCounty(nullSafeToString(location[22]));
			detailsForDate.setStorePhone(nullSafeToString(location[23]));
			detailsForDate.setStoreEmail(nullSafeToString(location[24]));
			detailsForDate.setLatitude(nullSafeToString(location[25]));
			detailsForDate.setLongitude(nullSafeToString(location[26]));

			return detailsForDate;
		}).collect(Collectors.toList()));

	}

	/*
	 * get all store
	 * 
	 * 
	 */
	public SearchResponse getStore() throws Exception {
		logger.debug("getStore()");

		return new SearchResponse(storeRepository.findAll());

	}

	/*
	 * getting store by store number and classification year
	 * 
	 * @parm storeNbr
	 * 
	 * @parm classification year
	 */
	public SearchResponse getStoreByStoreNumber(Integer storeNbr, String date) throws Exception {
		logger.debug("getStoreByStoreId with number" + storeNbr);

		StoreEntity storeEntity = storeRepository.findByStoreNumber(storeNbr);

		isNull(storeEntity);

		DistrictStoreRelationEnitity relationEnitity = districtStoreRepository.findByStore(storeEntity.getStoreId());

		Store store = modelMapper.map(storeEntity, Store.class);
		Integer dateStr = Integer.parseInt(date);
		StoreClassificationEntity storeClassification = storeClassificationRepository.findByStoreNbr(storeNbr, dateStr);

		store.setDistrictId(relationEnitity.getDistrictId());

		List<StoreQuota> quota = storeRepository.findStoreQuotaByStoreNbr(store.getStoreNbr()).stream().map(storeQuotaAmount ->{
			StoreQuota storeQuota = new StoreQuota();
			storeQuota.setQuotaDate(nullSafeToString(storeQuotaAmount[0]));
			storeQuota.setQuotaAmount(nullSafeToString(storeQuotaAmount[1].toString()));
			return storeQuota;
		}).collect(Collectors.toList());

		store.setStoreQuotaAmount(quota);

		if (storeClassification != null) {
			store.setStoreClassification(storeClassification.getStoreClassification());
			store.setStoreClassificationStartDate(
					DateUtil.getFormateddate(storeClassification.getEffectiveStartDate()));
			store.setStoreClassificationEndDate(DateUtil.getFormateddate(storeClassification.getEffectiveEndDate()));

		} else {
			store.setStoreClassification(null);
			store.setStoreClassificationStartDate(null);
			store.setStoreClassificationEndDate(null);

		}
		return new SearchResponse(store);
	}

	

	public void updateStore(Store store) throws Exception {
		logger.debug("updating record  with store  number == " + store.getStoreNbr());

		StoreEntity storeEntity = storeRepository.findByStoreNbr(store.getStoreNbr());

		isNull(storeEntity);

		store.setStoreId(storeEntity.getStoreId());

		modelMapper.map(store, storeEntity);

		storeEntity = storeRepository.save(storeEntity);

		// updating district store relation
		if (store.getStoreOpenDate() != null && store.getDistrictId() != null) {
			// findDistricStoreRelation by storeNumber or id

			DistrictStoreRelationEnitity relationEnitity = districtStoreRepository
					.findByStoreId(storeEntity.getStoreId());

			// step 2 if relationEnitity is null then save else update
			if (relationEnitity == null) {
				relationEnitity = new DistrictStoreRelationEnitity();
				relationEnitity.setEffectiveEndDate(DateUtil.getFormateddate("2999-12-31"));
			}

			relationEnitity.setStoreId(storeEntity.getStoreId());
			relationEnitity.setDistrictId(store.getDistrictId());
			relationEnitity.setEffectiveStartDate(store.getStoreOpenDate());
			relationEnitity.setLastUpdatedDateTime(DateUtil.getFormateddate(DateUtil.getCurrentUTCDate()));

			districtStoreRepository.save(relationEnitity);
		}

		// setting data to store_classification
		if (isNoneEmpty(store.getStoreClassification())) {
			StoreClassificationEntity classificationEntity = storeClassificationRepository
					.findByStoreNbrAndDate(storeEntity.getStoreNbr(), store.getStoreClassificationYear());
			if (classificationEntity == null) {
				classificationEntity = new StoreClassificationEntity();
				classificationEntity.setStoreNbr(storeEntity.getStoreNbr());
				classificationEntity
						.setEffectiveStartDate(DateUtil.getFormateddate(store.getStoreClassificationYear() + "-01-01"));
				classificationEntity
						.setEffectiveEndDate(DateUtil.getFormateddate(store.getStoreClassificationYear() + "-12-31"));
			}
			classificationEntity.setStoreClassification(store.getStoreClassification());
			storeClassificationRepository.save(classificationEntity);
		}

	}

	/*
	 * Create a store
	 * 
	 * 
	 * @store
	 * 
	 */

	public Integer createStore(Store store) throws Exception {

		logger.debug("createStore() called with create store === " + store);

		isNull(store.getStoreNbr());

		StoreEntity storeEntity = modelMapper.map(store, StoreEntity.class);
		storeEntity.setStoreNbr(store.getStoreNbr());

		if (districtRepository.findByDistrictId(store.getDistrictId()) == null) {
			throw new BadRequestException();
		}

		if (storeRepository.findByStoreNumber(store.getStoreNbr()) != null) {
			throw new BadRequestException("store already exist with storeNbr == " + store.getStoreNbr());
		}

		if (isNull(store.getStoreName())) {
			storeEntity.setStoreName(store.getStoreName());
		}

		if (isNull(store.getStoreLocation()) && StringUtils.isNotEmpty(store.getStoreLocation())) {
			storeEntity.setStoreLocation(store.getStoreLocation());
		}

		if (isNull(store.getStoreLocationName1())) {
			storeEntity.setStoreLocationName1(store.getStoreLocationName1());
		}

		storeEntity = storeRepository.save(storeEntity);

		if (store.getStoreOpenDate() != null && store.getDistrictId() != null) {
			DistrictStoreRelationEnitity relationEnitity = new DistrictStoreRelationEnitity();
			relationEnitity.setStoreId(storeEntity.getStoreId());
			relationEnitity.setDistrictId(store.getDistrictId());
			relationEnitity.setEffectiveEndDate(DateUtil.getFormateddate("2999-12-31"));
			relationEnitity.setEffectiveStartDate(store.getStoreOpenDate());
			relationEnitity.setLastUpdatedDateTime(DateUtil.getFormateddate(DateUtil.getCurrentUTCDate()));

			districtStoreRepository.save(relationEnitity);
		}

		if (isNoneEmpty(store.getStoreClassification())) {
			StoreClassificationEntity storeClassificationEntity = storeClassificationRepository
					.findByStoreNbrAndDate(storeEntity.getStoreNbr(), store.getStoreClassificationYear());
			if (storeClassificationEntity == null) {
				storeClassificationEntity = new StoreClassificationEntity();
				storeClassificationEntity.setStoreNbr(storeEntity.getStoreNbr());
				storeClassificationEntity
						.setEffectiveStartDate(DateUtil.getFormateddate(store.getStoreClassificationYear() + "-01-01"));
				storeClassificationEntity
						.setEffectiveEndDate(DateUtil.getFormateddate(store.getStoreClassificationYear() + "-12-31"));
			}
			storeClassificationEntity.setStoreClassification(store.getStoreClassification());
			storeClassificationRepository.save(storeClassificationEntity);
		}

		return storeEntity.getStoreNbr();
	}

}
