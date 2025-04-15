-- ----------------------------
-- 菜单配置 (sys_menu)
-- ----------------------------

INSERT INTO sys_menu 
  (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
  ('课程信息管理', 2033, 1, 'courses', 'courses/courses/index', 1, 0, 'C', '0', '0', 'courses:courses:list', 'education', 'admin', sysdate(), '', NULL, '课程信息管理菜单');

-- 获取刚插入的主菜单ID
SET @parentId = LAST_INSERT_ID();

-- 按钮权限配置
INSERT INTO sys_menu 
  (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
  -- 查询（对应 listCourses 和 getCourses 接口）
  ('课程信息查询', @parentId, 1, '#', '', 1, 0, 'F', '0', '0', 'courses:courses:query', '#', 'admin', sysdate(), '', NULL, ''),
  -- 新增（对应 addCourses 接口）
  ('课程信息新增', @parentId, 2, '#', '', 1, 0, 'F', '0', '0', 'courses:courses:add', '#', 'admin', sysdate(), '', NULL, ''),
  -- 修改（对应 updateCourses 接口）
  ('课程信息修改', @parentId, 3, '#', '', 1, 0, 'F', '0', '0', 'courses:courses:edit', '#', 'admin', sysdate(), '', NULL, ''),
  -- 删除（对应 delCourses 接口）
  ('课程信息删除', @parentId, 4, '#', '', 1, 0, 'F', '0', '0', 'courses:courses:remove', '#', 'admin', sysdate(), '', NULL, ''),
  -- 导出（若需导出功能，需在 Controller 添加导出接口）
  ('课程信息导出', @parentId, 5, '#', '', 1, 0, 'F', '0', '0', 'courses:courses:export', '#', 'admin', sysdate(), '', NULL, '');