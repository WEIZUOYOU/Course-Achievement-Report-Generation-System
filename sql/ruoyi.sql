USE ruoyi;

-- 课程主表（主表）
drop table if exists Courses;
CREATE TABLE Courses (
    course_id VARCHAR(20) PRIMARY KEY COMMENT '课程唯一编号',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_nredit TINYINT UNSIGNED NOT NULL COMMENT '课程学分',
    course_property VARCHAR(20) NOT NULL COMMENT '课程性质',
    course_grade VARCHAR(20) NOT NULL COMMENT '开课年级',
    course_semester TINYINT UNSIGNED NOT NULL COMMENT '第几学期',
    course_requirements TEXT COMMENT '毕业要求指标点',
    course_matrix TEXT COMMENT '课程目标矩阵'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程信息表';

-- 课程达成报告表（子表，保留逻辑关联字段）

/*
CREATE TABLE CourseReports (
    RID INT PRIMARY KEY AUTO_INCREMENT COMMENT '报告唯一编号',
    RFilename VARCHAR(255) NOT NULL COMMENT '文件名',
    RFile LONGBLOB NOT NULL COMMENT 'Word文件二进制',
    RSize INT UNSIGNED NOT NULL COMMENT '文件大小',
    RUploaded DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    TID INT NOT NULL COMMENT '关联教师编号',  -- 保留字段但无外键
    CID VARCHAR(20) NOT NULL COMMENT '关联课程编号'  -- 保留字段但无外键
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程达成报告表';
*/