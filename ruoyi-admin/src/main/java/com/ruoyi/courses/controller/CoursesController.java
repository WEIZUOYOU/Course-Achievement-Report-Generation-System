package com.ruoyi.courses.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.courses.domain.Courses;
import com.ruoyi.courses.service.ICoursesService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 课程信息Controller
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
@RestController
@RequestMapping("/courses/courses")
public class CoursesController extends BaseController
{
    @Autowired
    private ICoursesService coursesService;

    /**
     * 查询课程信息列表
     */
    @PreAuthorize("@ss.hasPermi('courses:courses:list')")
    @GetMapping("/list")
    public TableDataInfo list(Courses courses)
    {
        startPage();
        List<Courses> list = coursesService.selectCoursesList(courses);
        return getDataTable(list);
    }

    /**
     * 导出课程信息列表
     */
    @PreAuthorize("@ss.hasPermi('courses:courses:export')")
    @Log(title = "课程信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Courses courses)
    {
        List<Courses> list = coursesService.selectCoursesList(courses);
        ExcelUtil<Courses> util = new ExcelUtil<Courses>(Courses.class);
        util.exportExcel(response, list, "课程信息数据");
    }

    /**
     * 获取课程信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('courses:courses:query')")
    @GetMapping(value = "/{courseId}")
    public AjaxResult getInfo(@PathVariable("courseId") String courseId)
    {
        return success(coursesService.selectCoursesByCourseId(courseId));
    }

    /**
     * 新增课程信息
     */
    @PreAuthorize("@ss.hasPermi('courses:courses:add')")
    @Log(title = "课程信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Courses courses)
    {
        return toAjax(coursesService.insertCourses(courses));
    }

    /**
     * 修改课程信息
     */
    @PreAuthorize("@ss.hasPermi('courses:courses:edit')")
    @Log(title = "课程信息", businessType = BusinessType.UPDATE)
    @PutMapping("/{courseId}") // 修改路径，添加路径参数
    public AjaxResult edit(
        @PathVariable("courseId") String courseId, // 从路径获取 courseId
        @RequestBody Courses courses // 请求体不包含 courseId
    ) {
        courses.setCourseId(courseId); // 强制使用路径中的 ID
        return toAjax(coursesService.updateCourses(courses));
    }

    /**
     * 删除课程信息
     */
    @PreAuthorize("@ss.hasPermi('courses:courses:remove')")
    @Log(title = "课程信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{courseIds}")
    public AjaxResult remove(@PathVariable String[] courseIds)
    {
        return toAjax(coursesService.deleteCoursesByCourseIds(courseIds));
    }
}
