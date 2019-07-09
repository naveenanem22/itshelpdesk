package com.itshelpdesk.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itshelpdesk.dao.DashboardDao;
import com.itshelpdesk.model.BarChartDataItem;
import com.itshelpdesk.model.PieChartDataItem;

@Service(value = "dashboardServiceImpl")
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	@Qualifier("dashboardDaoImpl")
	private DashboardDao dashBoardDao;

	@Override
	@Transactional(readOnly = true)
	public Integer fetchCountOfTicketsInLastHourByStatus(String ticketStatus) {
		return dashBoardDao.fetchCountOfTicketsInLastHourByStatus(ticketStatus);
	}

	@Override
	public Integer fetchTotalTicketCountFromStart() {
		return dashBoardDao.fetchTotalTicketCountFromStart();
	}

	@Override
	@Transactional(readOnly = true)
	public List<BarChartDataItem> fetchTicketCountStatusAndMonthWise() {
		return dashBoardDao.fetchTicketCountStatusAndMonthWise();
	}

	@Override
	@Transactional(readOnly = true)
	public List<PieChartDataItem> fetchDepartmentWisePayload() {
		return dashBoardDao.fetchDepartmentWisePayload();
	}

}
