package com.ruoyi.reports.service.impl;

import com.ruoyi.reports.domain.Reports;
import com.ruoyi.reports.service.IReportsService;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class ReportsServiceImpl implements IReportsService {

    @Override
    public List<Reports> selectReportsList() {
        // 返回空列表
        return Collections.emptyList();
    }
}