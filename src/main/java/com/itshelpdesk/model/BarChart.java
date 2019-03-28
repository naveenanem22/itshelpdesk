package com.itshelpdesk.model;

import java.util.List;
import java.util.Map;

public class BarChart {
	private List<Map<Integer, List<Map<String, Integer>>>> data;
	
	public BarChart() {
		
	}

	public List<Map<Integer, List<Map<String, Integer>>>> getData() {
		return data;
	}

	public void setData(List<Map<Integer, List<Map<String, Integer>>>> data) {
		this.data = data;
	}
	
	

}
