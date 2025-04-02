USE ruoyi;

-- 教师主表（主表）
CREATE TABLE Teachers (
    TID INT PRIMARY KEY AUTO_INCREMENT COMMENT '教师唯一编号',
    TName VARCHAR(50) NOT NULL COMMENT '教师姓名',
    TStatus ENUM('专业主任', '普通教师') NOT NULL COMMENT '教师身份',
    TDepartment VARCHAR(50) NOT NULL COMMENT '所属专业'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师信息表';

-- 课程主表（主表）
CREATE TABLE Courses (
    CID VARCHAR(20) PRIMARY KEY COMMENT '课程唯一编号',
    CName VARCHAR(100) NOT NULL COMMENT '课程名称',
    CCredit TINYINT UNSIGNED NOT NULL COMMENT '课程学分',
    CProperty VARCHAR(20) NOT NULL COMMENT '课程性质',
    CGrade VARCHAR(20) NOT NULL COMMENT '开课年级',
    CSemester TINYINT UNSIGNED NOT NULL COMMENT '第几学期',
    CRequirements TEXT COMMENT '毕业要求指标点',
    CMatrix TEXT COMMENT '课程目标矩阵'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程信息表';

-- 课程达成报告表（子表，保留逻辑关联字段）
CREATE TABLE CourseReports (
    RID INT PRIMARY KEY AUTO_INCREMENT COMMENT '报告唯一编号',
    RFilename VARCHAR(255) NOT NULL COMMENT '文件名',
    RFile LONGBLOB NOT NULL COMMENT 'Word文件二进制',
    RSize INT UNSIGNED NOT NULL COMMENT '文件大小',
    RUploaded DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    TID INT NOT NULL COMMENT '关联教师编号',  -- 保留字段但无外键
    CID VARCHAR(20) NOT NULL COMMENT '关联课程编号'  -- 保留字段但无外键
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程达成报告表';

-- 教师-课程关系表（多对多中间表）
CREATE TABLE TeacherCourses (
    TID INT NOT NULL COMMENT '教师编号',  -- 保留字段但无外键
    CID VARCHAR(20) NOT NULL COMMENT '课程编号',  -- 保留字段但无外键
    Role ENUM('负责教师', '任课教师') NOT NULL COMMENT '教师角色',
    PRIMARY KEY (TID, CID, Role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师课程关系表';