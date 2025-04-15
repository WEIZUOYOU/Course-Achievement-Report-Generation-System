package com.ruoyi.courses.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.courses.mapper.CoursesMapper;
import com.ruoyi.courses.domain.Courses;
import com.ruoyi.courses.service.ICoursesService;

/**
 * 课程信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
@Service
public class CoursesServiceImpl implements ICoursesService 
{
    @Autowired
    private CoursesMapper coursesMapper;

    /**
     * 查询课程信息
     * 
     * @param courseId 课程信息主键
     * @return 课程信息
     */
    @Override
    public Courses selectCoursesByCourseId(String courseId)
    {
        return coursesMapper.selectCoursesByCourseId(courseId);
    }

    /**
     * 查询课程信息列表
     * 
     * @param courses 课程信息
     * @return 课程信息
     */
    @Override
    public List<Courses> selectCoursesList(Courses courses)
    {
        return coursesMapper.selectCoursesList(courses);
    }

    /**
     * 新增课程信息
     */
    @Override
    public int insertCourses(Courses courses) {
        // 校验课程编号是否已存在
        if (coursesMapper.selectCoursesByCourseId(courses.getCourseId()) != null) {
            throw new RuntimeException("课程编号已存在");
        }
        return coursesMapper.insertCourses(courses);
    }

    /**
     * 修改课程信息
     */
    @Override
    public int updateCourses(Courses courses) {
        // 1. 校验课程是否存在
        Courses existingCourse = coursesMapper.selectCoursesByCourseId(courses.getCourseId());
        if (existingCourse == null) {
            throw new RuntimeException("课程不存在");
        }
        
        // 2. 防止请求体被篡改（可选，根据安全要求）
        // 若不需要可删除此部分
        if (!courses.getCourseId().equals(existingCourse.getCourseId())) {
            throw new RuntimeException("非法操作：课程编号不一致");
        }
        
        return coursesMapper.updateCourses(courses);
    }

    /**
     * 批量删除课程信息
     * 
     * @param courseIds 需要删除的课程信息主键
     * @return 结果
     */
    @Override
    public int deleteCoursesByCourseIds(String[] courseIds)
    {
        return coursesMapper.deleteCoursesByCourseIds(courseIds);
    }

    /**
     * 删除课程信息信息
     * 
     * @param courseId 课程信息主键
     * @return 结果
     */
    @Override
    public int deleteCoursesByCourseId(String courseId)
    {
        return coursesMapper.deleteCoursesByCourseId(courseId);
    }
}
