package com.itshelpdesk.model;

import java.util.List;
import java.util.Map;

public class BarChartDataItem {
	private Integer month;
	private List<Map<String, Integer>> dataPoints;

	public BarChartDataItem() {

	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public List<Map<String, Integer>> getDataPoints() {
		return dataPoints;
	}

	public void setDataPoints(List<Map<String, Integer>> dataPoints) {
		this.dataPoints = dataPoints;
	}

	@Override
	public String toString() {
		return "BarChart [month=" + month + ", dataPoints=" + dataPoints + "]";
	}
	
	

}
