package com.ruoyi.reports.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports/reports")
public class ReportsController extends BaseController {

    @PreAuthorize("@ss.hasPermi('reports:reports:list')")
    @GetMapping("/list")
    public TableDataInfo list() {
        // 返回空数据表格结构
        return getDataTable(null);
    }
}