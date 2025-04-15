package com.ruoyi.courses.mapper;

import java.util.List;
import com.ruoyi.courses.domain.Courses;

/**
 * 课程信息Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
public interface CoursesMapper 
{
    /**
     * 查询课程信息
     * 
     * @param courseId 课程信息主键
     * @return 课程信息
     */
    public Courses selectCoursesByCourseId(String courseId);

    /**
     * 查询课程信息列表
     * 
     * @param courses 课程信息
     * @return 课程信息集合
     */
    public List<Courses> selectCoursesList(Courses courses);

    /**
     * 新增课程信息
     * 
     * @param courses 课程信息
     * @return 结果
     */
    public int insertCourses(Courses courses);

    /**
     * 修改课程信息
     * 
     * @param courses 课程信息
     * @return 结果
     */
    public int updateCourses(Courses courses);

    /**
     * 删除课程信息
     * 
     * @param courseId 课程信息主键
     * @return 结果
     */
    public int deleteCoursesByCourseId(String courseId);

    /**
     * 批量删除课程信息
     * 
     * @param courseIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCoursesByCourseIds(String[] courseIds);
}
