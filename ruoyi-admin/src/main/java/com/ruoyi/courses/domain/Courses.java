package com.ruoyi.courses.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 课程信息对象 courses
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
public class Courses extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 课程唯一编号 */
    @Excel(name = "课程唯一编号")
    private String courseId;

    /** 课程名称 */
    @Excel(name = "课程名称")
    private String courseName;

    /** 课程学分 */
    @Excel(name = "课程学分")
    private String courseNredit;

    /** 课程性质 */
    @Excel(name = "课程性质")
    private String courseProperty;

    /** 开课年级 */
    @Excel(name = "开课年级")
    private String courseGrade;

    /** 第几学期 */
    @Excel(name = "第几学期")
    private String courseSemester;

    /** 毕业要求指标点 */
    private String courseRequirements;

    /** 课程目标矩阵 */
    private String courseMatrix;

    public void setCourseId(String courseId) 
    {
        this.courseId = courseId;
    }

    public String getCourseId() 
    {
        return courseId;
    }

    public void setCourseName(String courseName) 
    {
        this.courseName = courseName;
    }

    public String getCourseName() 
    {
        return courseName;
    }

    public void setCourseNredit(String courseNredit) 
    {
        this.courseNredit = courseNredit;
    }

    public String getCourseNredit() 
    {
        return courseNredit;
    }

    public void setCourseProperty(String courseProperty) 
    {
        this.courseProperty = courseProperty;
    }

    public String getCourseProperty() 
    {
        return courseProperty;
    }

    public void setCourseGrade(String courseGrade) 
    {
        this.courseGrade = courseGrade;
    }

    public String getCourseGrade() 
    {
        return courseGrade;
    }

    public void setCourseSemester(String courseSemester) 
    {
        this.courseSemester = courseSemester;
    }

    public String getCourseSemester() 
    {
        return courseSemester;
    }

    public void setCourseRequirements(String courseRequirements) 
    {
        this.courseRequirements = courseRequirements;
    }

    public String getCourseRequirements() 
    {
        return courseRequirements;
    }

    public void setCourseMatrix(String courseMatrix) 
    {
        this.courseMatrix = courseMatrix;
    }

    public String getCourseMatrix() 
    {
        return courseMatrix;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("courseId", getCourseId())
            .append("courseName", getCourseName())
            .append("courseNredit", getCourseNredit())
            .append("courseProperty", getCourseProperty())
            .append("courseGrade", getCourseGrade())
            .append("courseSemester", getCourseSemester())
            .append("courseRequirements", getCourseRequirements())
            .append("courseMatrix", getCourseMatrix())
            .toString();
    }
}
