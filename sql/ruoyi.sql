USE ruoyi;

-- 课程主表（存储课程基本信息）
DROP TABLE IF EXISTS Courses;
CREATE TABLE Courses (
    course_id VARCHAR(20) PRIMARY KEY COMMENT '课程唯一编号',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_credit TINYINT UNSIGNED NOT NULL COMMENT '课程学分',
    course_property VARCHAR(20) NOT NULL COMMENT '课程性质',
    course_grade VARCHAR(20) NOT NULL COMMENT '开课年级',
    course_semester TINYINT UNSIGNED NOT NULL COMMENT '第几学期'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程信息表';

/*
-- ----------------------------
-- 课程主表
-- ----------------------------
DROP TABLE IF EXISTS edu_course;
CREATE TABLE edu_course (
    course_id VARCHAR(20) PRIMARY KEY COMMENT '课程编号',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    course_credit SMALLINT UNSIGNED NOT NULL COMMENT '学分',
    course_property VARCHAR(20) NOT NULL COMMENT '课程性质',
    course_grade VARCHAR(20) NOT NULL COMMENT '开课年级',
    course_semester TINYINT UNSIGNED NOT NULL COMMENT '开课学期',
    status CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新者',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    remark VARCHAR(500) COMMENT '备注'
) ENGINE=InnoDB COMMENT='课程信息表';

-- ----------------------------
-- 课程达成报告表
-- ----------------------------
DROP TABLE IF EXISTS edu_course_report;
CREATE TABLE edu_course_report (
    report_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '报告ID',
    fk_course_id VARCHAR(20) NOT NULL COMMENT '课程编号',
    fk_teacher_id BIGINT NOT NULL COMMENT '教师ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_data LONGBLOB NOT NULL COMMENT '文件内容',
    file_size BIGINT UNSIGNED NOT NULL COMMENT '文件大小（字节）',
    file_type VARCHAR(50) COMMENT '文件类型',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    version INT DEFAULT 1 COMMENT '版本号',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    INDEX idx_course (fk_course_id),
    INDEX idx_teacher (fk_teacher_id),
    FOREIGN KEY (fk_course_id) REFERENCES edu_course(course_id),
    FOREIGN KEY (fk_teacher_id) REFERENCES sys_user(user_id)
) ENGINE=InnoDB COMMENT='课程达成报告表';

-- ----------------------------
-- 毕业要求指标表（增加系统字段）
-- ----------------------------
DROP TABLE IF EXISTS edu_graduation_requirement;
CREATE TABLE edu_graduation_requirement (
    requirement_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '指标ID',
    requirement_code VARCHAR(10) NOT NULL UNIQUE COMMENT '指标编号',
    requirement_desc TEXT NOT NULL COMMENT '指标描述',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新者',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志'
) ENGINE=InnoDB COMMENT='毕业要求指标表';

-- ----------------------------
-- 课程目标表（规范设计）
-- ----------------------------
DROP TABLE IF EXISTS edu_course_objective;
CREATE TABLE edu_course_objective (
    objective_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '目标ID',
    fk_course_id VARCHAR(20) NOT NULL COMMENT '课程ID',
    objective_order TINYINT UNSIGNED NOT NULL COMMENT '目标序号',
    objective_desc TEXT NOT NULL COMMENT '目标描述',
    create_by VARCHAR(64) COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新者',
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志',
    UNIQUE KEY (fk_course_id, objective_order),
    FOREIGN KEY (fk_course_id) REFERENCES edu_course(course_id)
) ENGINE=InnoDB COMMENT='课程目标表';

-- ----------------------------
-- 课程目标-指标关联表（带支撑强度）
-- ----------------------------
DROP TABLE IF EXISTS edu_objective_requirement;
CREATE TABLE edu_objective_requirement (
    objective_id INT NOT NULL COMMENT '目标ID',
    requirement_id INT NOT NULL COMMENT '指标ID',
    strength ENUM('H','M','L') DEFAULT 'M' COMMENT '支撑强度',
    PRIMARY KEY (objective_id, requirement_id),
    FOREIGN KEY (objective_id) REFERENCES edu_course_objective(objective_id),
    FOREIGN KEY (requirement_id) REFERENCES edu_graduation_requirement(requirement_id)
) ENGINE=InnoDB COMMENT='课程目标-指标关联表';
*/