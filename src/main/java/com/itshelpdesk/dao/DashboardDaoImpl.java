package com.itshelpdesk.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.itshelpdesk.model.BarChartDataItem;
import com.itshelpdesk.model.BarChartRawDataItem;
import com.itshelpdesk.model.PieChartDataItem;
import com.itshelpdesk.model.PieChartRawDataItem;
import com.pc.custom.exceptions.InternalServerException;
import com.pc.report.utils.SeriesFactory;

@Repository("dashboardDaoImpl")
public class DashboardDaoImpl implements DashboardDao {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Integer fetchCountOfTicketsInLastHourByStatus(String ticketStatus) {

		LOGGER.debug("Fetching {} ticket-count in the last one hour", ticketStatus);
		StringBuilder sql = new StringBuilder();
		Integer count;

		switch (ticketStatus) {

		case "New":
			sql.append("SELECT * FROM viewnewticketsinlasthour");
			count = jdbcTemplate.queryForObject(sql.toString(), new LastHourNewTicketCountRowMapper());
			LOGGER.debug("Number of {} tickets in last one hour: {}", ticketStatus, count);
			return count;
		case "Closed":
			sql.append("SELECT * FROM viewclosedticketsinlasthour");
			count = jdbcTemplate.queryForObject(sql.toString(), new LastHourClosedTicketCountRowMapper());
			LOGGER.debug("Number of {} tickets in last one hour: {}", ticketStatus, count);
			return count;
		default:
			throw new InternalServerException("An unexpected error occured while fetching ticket count.");
		}

	}

	@Override
	public List<BarChartDataItem> fetchTicketCountStatusAndMonthWise() {
		LOGGER.debug("Fetching ticket-count by month and status wise");
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM viewticketsbymonthandstatus");

		List<BarChartRawDataItem> barChartRawData = jdbcTemplate.query(sql.toString(),
				new BarChartRawDataItemRowMapper());
		LOGGER.debug("Fetched BarChartRawData: {}", barChartRawData.toString());

		// Processing the barChartRawData to return in hierarchical map
		List<BarChartDataItem> processedBarChartData = new ArrayList<BarChartDataItem>();

		List<LocalDate> yearMonthSeries = SeriesFactory.getLastDaysOfMonthSeries(LocalDate.now(), 6);

		// Loop through the set of months
		yearMonthSeries.forEach(yearMonth -> {
			// Create BarChartItem for month
			BarChartDataItem barChartDataItem = new BarChartDataItem();
			barChartDataItem.setMonth(yearMonth.getMonth().getValue());
			barChartDataItem.setYear(yearMonth.getYear());

			// Extract YearMonth-Status-TicketCount list for looping month
			LOGGER.debug("Looping through year-month: {}", yearMonth.toString());
			List<BarChartRawDataItem> barChartRawDataForGivenYearMonth = barChartRawData.stream()
					.filter(barChartRawDataItem -> {
						return barChartRawDataItem.getLastDateOfTicketCreatedMonth().equals(yearMonth);
					}).collect(Collectors.toList());
			LOGGER.debug("Extracted YearMonth-Status-TicketCount list for a given month: {}",
					barChartRawDataForGivenYearMonth.toString());

			if (barChartRawDataForGivenYearMonth.isEmpty()) {
				LOGGER.debug("No data present in the barChartRawData for the yearMonth: {}", yearMonth.toString());
				// Add Empty dataPoints for missing yearMonth
				String statusArray[] = new String[] { "Open", "Closed", "Processing", "New", "Pending" };
				List<String> statusList = Arrays.asList(statusArray);
				List<Map<String, Integer>> statusTktCountMapList = new ArrayList<Map<String, Integer>>();
				statusList.forEach(statusItem -> {
					Map<String, Integer> emptyStatusTktCountMap = new HashMap<String, Integer>();
					emptyStatusTktCountMap.put(statusItem, 0);
					statusTktCountMapList.add(emptyStatusTktCountMap);
				});
				LOGGER.debug("Added Empty DataPoints: {}", statusTktCountMapList);
				barChartDataItem.setDataPoints(statusTktCountMapList);

			} else {
				// Add Empty data for missing status in Month-Status-TicketCount lit for looping
				// month
				String statusArray[] = new String[] { "Open", "Closed", "Processing", "New", "Pending" };
				List<String> statusList = Arrays.asList(statusArray);
				List<String> missedStatusList = statusList.stream().filter(status -> {
					boolean result = true;
					for (BarChartRawDataItem barChartRawDataItem : barChartRawDataForGivenYearMonth) {
						if (barChartRawDataItem.getStatus().equalsIgnoreCase(status)) {
							result = false;
							break;
						}
					}
					return result;
				}).collect(Collectors.toList());
				LOGGER.debug("Missed StatusList {} for given year-month", missedStatusList.toString());
				List<Map<String, Integer>> statusTktCountMapList = new ArrayList<Map<String, Integer>>();
				missedStatusList.forEach(missedStatus -> {
					Map<String, Integer> emptyStatusTktCountMap = new HashMap<String, Integer>();
					emptyStatusTktCountMap.put(missedStatus, 0);
					statusTktCountMapList.add(emptyStatusTktCountMap);
				});

				// Create List<Map<String, Integer>> for Status-TicketCount pair list from the
				// above list
				barChartRawDataForGivenYearMonth.forEach(barChartRawDataItem -> {
					Map<String, Integer> statusTktCountMap = new HashMap<String, Integer>();
					statusTktCountMap.put(barChartRawDataItem.getStatus(), barChartRawDataItem.getTicketCount());
					statusTktCountMapList.add(statusTktCountMap);
				});
				LOGGER.debug("Created list of map of status-ticket pair {} for the given month {}",
						statusTktCountMapList.toString(), yearMonth.toString());

				barChartDataItem.setDataPoints(statusTktCountMapList);
			}

			processedBarChartData.add(barChartDataItem);
		});

		LOGGER.debug("Processed BarChartData: {}", processedBarChartData);

		// Sort processed BarChartData
		LOGGER.debug("Sort the processed BarChartData by increasing order of time");
		List<BarChartDataItem> sortedBarChartData = processedBarChartData.stream()
				.sorted((barChartDataItem1, barChartDataItem2) -> {
					int yearComparisonResult = barChartDataItem1.getYear().compareTo(barChartDataItem2.getYear());
					if (yearComparisonResult != 0)
						return yearComparisonResult;
					return barChartDataItem1.getMonth().compareTo(barChartDataItem2.getMonth());
				}).collect(Collectors.toList());

		LOGGER.debug("Sorted BarChartData: {}", sortedBarChartData.toString());

		return sortedBarChartData;
	}

	@Override
	public List<PieChartDataItem> fetchDepartmentWisePayload() {
		LOGGER.debug("Fetching department-wise payload");
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM viewdepartmentwiseworkload");

		List<PieChartRawDataItem> pieChartRawData = jdbcTemplate.query(sql.toString(),
				new PieChartRawDataItemRowMapper());

		LOGGER.debug("Fetched PieChartRawData: {}", pieChartRawData);

		// Processing fetched PieChart raw data
		List<PieChartDataItem> pieChartData = new ArrayList<PieChartDataItem>();
		pieChartRawData.forEach(pieChartRawDataItem -> {
			PieChartDataItem pieChartDataItem = new PieChartDataItem();
			pieChartDataItem.setDepartmentName(pieChartRawDataItem.getDepartmentName());
			pieChartDataItem.setPayload(pieChartRawDataItem.getTicketCount());

			pieChartData.add(pieChartDataItem);
		});

		LOGGER.debug("Processed PieChartData: {}", pieChartData);

		return pieChartData;
	}

	private class BarChartRawDataItemRowMapper implements RowMapper<BarChartRawDataItem> {

		@Override
		public BarChartRawDataItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			BarChartRawDataItem barChartRawDataItem = new BarChartRawDataItem();
			barChartRawDataItem.setStatus(rs.getString("sts_name"));
			barChartRawDataItem.setTicketCount(rs.getInt("tkt_count"));
			barChartRawDataItem.setLastDateOfTicketCreatedMonth(rs.getDate("last_day_of_month").toLocalDate());
			return barChartRawDataItem;
		}

	}

	private class LastHourNewTicketCountRowMapper implements RowMapper<Integer> {

		@Override
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Integer(rs.getInt("new_ticket_count_last_hour"));

		}

	}

	private class LastHourClosedTicketCountRowMapper implements RowMapper<Integer> {

		@Override
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new Integer(rs.getInt("closed_ticket_count_last_hour"));

		}

	}

	private class PieChartRawDataItemRowMapper implements RowMapper<PieChartRawDataItem> {

		@Override
		public PieChartRawDataItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			PieChartRawDataItem pieChartRawDataItem = new PieChartRawDataItem();
			pieChartRawDataItem.setDepartmentName(rs.getString("dept_name"));
			pieChartRawDataItem.setTicketCount(rs.getInt("ticket_count"));

			return pieChartRawDataItem;
		}

	}

}
