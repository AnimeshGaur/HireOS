package com.conns.organization.management.utilities;

public interface Constants {
	public final String STATUS_OCCUPIED = "Occupied";
	public final String STATUS_OPEN = "Open";
	public final String ORPHAN_PARENT_COMMON_ID = "bb541bee-56db-3432-9a66-f43557765ddd";
	public final String ORPHAN_DELIVERY_PARENT_COMMON_ID = "c87ef5cb-a375-4ff2-a87b-e86a95ff9419";
	public final String ORPHAN_CONTACT_CENTRE_PARENT_COMMON_ID = "57950413-d203-483c-bc6b-b7deaea0c6c8";
	public final String ORPHAN_SALES_PARENT_COMMON_ID = "05b42340-a76b-428e-a0a0-264152a4eef2";
	public final String ORPHAN_SERVICE_PARENT_COMMON_ID = "9c986afd-060c-4064-8d2a-66e523272fed";
	public final String UNASSIGN_COMMON_ID = "ff682fa7-640e-4616-bbed-f58ecfdf0db";
	public final String UNASSIGN_PARENT_COMMON_ID = "ab541bee-56db-4936-9a66-f43557765ddc";
	public final String NODE_TYPE_PEOPLE = "People";
	public final String NODE_TYPE_LOCATION_PHYSICAL = "Physical";
	public final String NODE_TYPE_LOCATION_VIRTUAL = "Virtual";
	public final String UI_STATUS_UPDATED = "updated";
	public final String UI_STATUS_PROCESSED = "processed";
	public final String DEFAULT = "0";
	public final String TYPE_EMPLOYEE = "emp";
	public final String TYPE_LOCATION = "loc";
	public final String TYPE_EMPLOYEE_NODE = "emp-node";
	public final String TYPE_LOCATION_NODE = "loc-node";
	public final String CATEGORY_LOCATION = "location";
	public final String CATEGORY_EMPLOYEE = "HR";
	public final String DOMAIN_ORPHANS = "Orphans";
	public final String DOMAIN_DELIVERY = "Delivery";
	public final String DOMAIN_RETAIL = "Retail";
	public final String DOMAIN__SERVICE = "Service";
	public final String DOMAIN_CONTACT_CENTER = "Contact Center";
	public final String UNDEFINED = "Undefined";
	public final String EMPLOYEE_TERMINATED_STATUS = "T1";

	// for logging
	public final String NOT_OPEN_POSTITION = "No an Open position, hence cannot copy/move employee.";
	public final String BAD_REQUEST = "The request could not be handled by the server due to malformed input.";
	public final String NO_TREE_NODE = "There is no node in the Organization Tree corresponding to the given parentCommonId and/or commonId.";
	public final String CANNOT_CLOSE_STORE = "Cannot close the store as child nodes are present.";
}
