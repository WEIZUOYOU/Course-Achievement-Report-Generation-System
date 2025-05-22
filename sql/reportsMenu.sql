INSERT INTO sys_menu 
  (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES
  ('成绩分析管理', 2007, 1, 'reports', 'reports/reports/index', 1, 0, 'C', '0', '0', 'reports:reports:list', 'chart', 'admin', sysdate(), '成绩分析管理菜单');