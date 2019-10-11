package com.conns.organization.management.utilities;

import java.util.ArrayList;
import java.util.List;

public class DummyStoreProvider {

	public static List<String> getStores(int count) throws Exception{
		List<String> stores = new ArrayList<>();
		for(int i = 0; i < count; i++) {
		}
		return stores;
	}
	
}
