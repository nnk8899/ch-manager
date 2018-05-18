package com.xh.mgr.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.xh.mgr.common.Page;
import com.xh.mgr.model.CategoryTreeBean;
import com.xh.mgr.model.CategoryTreeBeanCk;
import com.xh.mgr.model.UserLogin;
import com.xh.mgr.model.UserRole;
import com.xh.mgr.model.ModuleStoreBean;
import com.xh.mgr.util.Md5Util;
import com.xh.mgr.util.PinYinHelper;
import com.xh.mgr.util.ToolUtils;

@Service
public class UserService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	static Logger logger = LoggerFactory.getLogger("sweepManager");
	
	public void updatePwd(String userId,String userName,String newUserPwd){
//		String md5Pwd = Md5Util.generatePassword(newUserPwd);
		jdbcTemplate.update("update user_login set user_password = ? where id =? ",newUserPwd,userId);
		syncPwd(userId, userName, newUserPwd);
	}
	
	public List<String> getUserRoleAuth(String roleId){
		String sql = "select menu_id from sweepmgr_auth where role_id = ? ";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,roleId);
		List<String> authList = new ArrayList<String>();
		if (list != null && list.size() > 0) {
			for(int i=0;i<list.size();i++){
				authList.add((list.get(i).get("menu_id")+""));
			}
		}
		return authList;
	}
	
	
	
	
	public void syncPwd(String userId,String userName,String newUserPwd){
		String sql = "  select is_allow_weblogin,user_name from user_login where id = ? ";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,userId);
		String isAllow = "";
		if(list != null && list.size()>0){
			isAllow = list.get(0).get("is_allow_weblogin")+"";
			if("0".equals(isAllow)){//允许登陆
				String sql1 = " update base_user set login_pwd = ? where auth_type = 0 and  login_name = ?  ";
				jdbcTemplate.update(sql1,newUserPwd,userName);
			}
		}
	}
	
	public List<ModuleStoreBean> getRole(){
		return jdbcTemplate.query(" select id,role_name from user_role where is_delete = 0 ",new ModuleStoreBeanRowMapper());
	}
	
	public List<UserRole> getRoleList(Map<String, String> condition,StringBuilder sb){
		List<UserRole> list = new ArrayList<UserRole>();
		List<Object> params = new ArrayList<Object>();
		StringBuilder sbud = new StringBuilder(" select * from user_role where is_delete = 0 ");
		if (!condition.isEmpty()) { 
			if (condition.containsKey("roleName")) {
				sbud.append(" and role_name like ?");
				params.add("%"+condition.get("roleName")+"%");
			}
			list = (List<UserRole>) jdbcTemplate.query(sbud.toString(), new UserRoleRowMapper(), params.toArray(new Object[params.size()]));
			sb.append(list.size());
			if (condition.containsKey("start") && condition.containsKey("limit")) {
				sbud.append(" LIMIT ?,? ");
				int start = Integer.parseInt(condition.get("start"));
				int limit = Integer.parseInt(condition.get("limit"));
				params.add(start);
				params.add(limit);
			}
		}
		list = new ArrayList<UserRole>();
		list = (List<UserRole>) jdbcTemplate.query(sbud.toString(), new UserRoleRowMapper(), params.toArray(new Object[params.size()]));
		if (list != null && list.size() > 0) {
			return list;
		}
		return null;
	}
	public Page<Map<String, Object>> getConsoleUserList(Map<String, String> condition){
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(0, 1000, false);
		List<Object> params = new ArrayList<Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		StringBuilder sbud = new StringBuilder("select a.id,a.user_name,a.user_password,a.is_delete,a.user_role as user_role_id,b.role_name as user_role,is_allow_weblogin from user_login a left join user_role b on a.user_role = b.id where 1 = 1 and a.is_delete = 0 and b.is_delete = 0 ");
		if (!condition.isEmpty()) { 
			if (condition.containsKey("userName")) {
				sbud.append(" and a.user_name like ?");
				params.add("%"+condition.get("userName")+"%");
			}
			if(condition.containsKey("userRole")){
				sbud.append(" and a.user_role = ?");
				params.add(condition.get("userRole"));
			}
			List<Map<String, Object>> countList = jdbcTemplate.queryForList(sbud.toString(),params.toArray());
			page.setTotalCount(countList.size());
			if (condition.containsKey("start") && condition.containsKey("limit")) {
				sbud.append(" LIMIT ?,? ");
				int start = Integer.parseInt(condition.get("start"));
				int limit = Integer.parseInt(condition.get("limit"));
				params.add(start);
				params.add(limit);
			}
			list = jdbcTemplate.queryForList(sbud.toString(),params.toArray());
		}
		page.setResult(list);
		return page;
	}
	
	public List<UserLogin> getUserList(Map<String, String> condition){
		List<UserLogin> list = null;
		List<Object> params = new ArrayList<Object>();
		StringBuilder sbud = new StringBuilder("select a.id,a.user_name,a.user_password,a.is_delete,a.user_role as user_role_id,b.role_name as user_role from user_login a left join user_role b on a.user_role = b.id where 1 = 1 and a.is_delete = 0 and b.is_delete = 0 ");
		if (!condition.isEmpty()) { 
			if (condition.containsKey("userName")) {
				sbud.append(" and a.user_name like ?");
				params.add("%"+condition.get("userName")+"%");
			}
			if(condition.containsKey("userRole")){
				sbud.append(" and a.user_role = ?");
				params.add(condition.get("userRole"));
			}
			if (condition.containsKey("start") && condition.containsKey("limit")) {
				sbud.append(" LIMIT ?,? ");
				int start = Integer.parseInt(condition.get("start"));
				int limit = Integer.parseInt(condition.get("limit"));
				params.add(start);
				params.add(limit);
			}
		}
		list = (List<UserLogin>) jdbcTemplate.query(sbud.toString(), new UserLoginRowMapper(), params.toArray(new Object[params.size()]));
		if (list != null && list.size() > 0) {
			return list;
		}
		return null;
	}
	public int getUserListCount(Map<String, String> condition){
		List<UserLogin> list = null;
		List<Object> params = new ArrayList<Object>();
		StringBuilder sbud = new StringBuilder("select count(1) from user_login a left join user_role b on a.user_role = b.id where 1 = 1 and a.is_delete = 0 ");
		if (!condition.isEmpty()) { 
			if (condition.containsKey("userName")) {
				sbud.append(" and a.user_name like ?");
				params.add("%"+condition.get("userName"+"%"));
			}
			if(condition.containsKey("userRole")){
				sbud.append(" and a.user_role = ?");
				params.add(condition.get("userRole"));
			}
		}
		int count = jdbcTemplate.queryForInt(sbud.toString(), params.toArray(new Object[params.size()]));
		return count;
	}
	public List<UserLogin> getUserListLogin(Map<String, String> condition){
		List<UserLogin> list = null;
		List<Object> params = new ArrayList<Object>();
		StringBuilder sbud = new StringBuilder("select *,1 as user_role_id from user_login where 1 = 1 and is_delete = 0 ");
		if (!condition.isEmpty()) { 
			if (condition.containsKey("userName")) {
				sbud.append(" and user_name = ?");
				params.add(condition.get("userName"));
			}
			if(condition.containsKey("userPwd")){
				sbud.append(" and user_password = ?");
				params.add(condition.get("userPwd"));
//				params.add(Md5Util.generatePassword(condition.get("userPwd")));
			}
		}
		list = (List<UserLogin>) jdbcTemplate.query(sbud.toString(), new UserLoginRowMapper(), params.toArray(new Object[params.size()]));
		if (list != null && list.size() > 0) {
			return list;
		}
		return null;
	}
	
	public void deleteUserByRoleId(String roleId){
		if(StringUtils.isNotBlank(roleId)){
			jdbcTemplate.update("update user_login set is_delete = 1 where user_role =?",roleId);
		}
	}
	public void deleteRole(String roleId){
		if(StringUtils.isNotBlank(roleId)){
			jdbcTemplate.update("update user_role set is_delete = 1 where id =?",roleId);
		}
	}
	public void deleteUser(String userId){
		if(StringUtils.isNotBlank(userId)){
			jdbcTemplate.update("update user_login set is_delete = 1 where id =?",userId);
		}
	}
	public void addUser(String userName,String userPwd,int role){
		String uuid = UUID.randomUUID().toString().replace("-", "");
		jdbcTemplate.update("insert into user_login(id,user_name,user_password,user_role)values(?,?,?,?)",uuid,userName,userPwd,role);
	}
	public boolean isExitUser(String userName,String userId){
		int i = 0;
		if (StringUtils.isBlank(userId)) {
			i = jdbcTemplate.queryForInt("select count(1) from user_login where user_name = ? and is_delete = 0 ",userName);
		}else{
			i = jdbcTemplate.queryForInt("select count(1) from user_login where user_name = ? and id <> ? and is_delete = 0 ",userName,userId);
		}
		return i>0;
	}
	public void DeleteRoleAuth(String roleId){
		jdbcTemplate.update("delete from sweepmgr_auth where role_id =?",roleId);
	}
	public void addRoleAuth(String roleId,String menuId){
		jdbcTemplate.update("insert into sweepmgr_auth (role_id,menu_id) values(?,?)",roleId,menuId);
	}
	public void addRole(String roleName){
		jdbcTemplate.update("insert into user_role(role_name)values(?)",roleName);
	}
	public boolean isExitRole(String roleName){
		int i = jdbcTemplate.queryForInt("select count(1) from user_role where role_name = ? and is_delete = 0 ",roleName);
		return i>0;
	}
	
	public void editRole(String roleId,String roleName){
		if(ToolUtils.isNotBlank(roleId)){
			List<Object> params = new ArrayList<Object>();
			StringBuilder sb = new StringBuilder(" update user_role set id = ? ");
			params.add(roleId);
			if(ToolUtils.isNotBlank(roleName)){
				sb.append(",role_name = ?");
				params.add(roleName);
			}
			sb.append(" where id = ?");
			params.add(roleId);
			jdbcTemplate.update(sb.toString(), params.toArray(new Object[params.size()]));
		}
	}
	public void editUser(UserLogin user){
		if(ToolUtils.isNotBlank(user.getId())){
			List<Object> params = new ArrayList<Object>();
			StringBuilder sb = new StringBuilder(" update user_login set id = ? ");
			params.add(user.getId());
			if(ToolUtils.isNotBlank(user.getUserName())){
				sb.append(",user_name = ?");
				params.add(user.getUserName());
			}
			if(ToolUtils.isNotBlank(user.getUserPassword())){
				sb.append(",user_password = ?");
				params.add(user.getUserPassword());
			}
			if(ToolUtils.isNotBlank(user.getUserRole()+"")){
				sb.append(",user_role = ?");
				params.add(user.getUserRole());
			}
			sb.append(" where id = ?");
			params.add(user.getId());
			jdbcTemplate.update(sb.toString(), params.toArray(new Object[params.size()]));
			
			syncPwd(user.getId(), user.getUserName(), user.getUserPassword());
		}
	}
	public List<CategoryTreeBean> getTreeList(String roleId) {
		List<CategoryTreeBean> list = new ArrayList();
//		String sql = " select * from sweepmgr_menu ORDER BY id desc ";
		StringBuilder sb = new StringBuilder();
		sb.append(" select a.* ");
		sb.append(" from sweepmgr_menu a,(select menu_id from sweepmgr_auth  where role_id = ?) b  ");
		sb.append(" where a.id = b.menu_id ORDER BY a.id desc  ");
		list = (List<CategoryTreeBean>)jdbcTemplate.query(sb.toString(), new CategoryTreeBeanRowMapper(),roleId);
		return list;
	}
	public List<CategoryTreeBeanCk> getTreeCKListAuth(String roleId) {
		List<CategoryTreeBeanCk> list = new ArrayList();
//		String sql = " select * from sweepmgr_menu ORDER BY id desc ";
		StringBuilder sb = new StringBuilder();
		sb.append(" select a.*,b.menu_id as is_check ");
		sb.append(" from sweepmgr_menu a left join (select menu_id from sweepmgr_auth where role_id = ?) b on  a.id = b.menu_id ORDER BY id desc ");
		list = (List<CategoryTreeBeanCk>)jdbcTemplate.query(sb.toString(), new CategoryTreeBeanCKRowMapper(),roleId);
		return list;
	}
	public List<CategoryTreeBeanCk> getTreeCKListAuthDo(String roleId) {//赋予权限
		List<CategoryTreeBeanCk> list = new ArrayList();
//		String sql = " select * from sweepmgr_menu ORDER BY id desc ";
		StringBuilder sb = new StringBuilder();
		sb.append(" select a.id,a.parent_id,a.menu_url,a.menu_name,a.button,a.auth_leaf as leaf,b.menu_id as is_check ");
		sb.append(" from sweepmgr_menu a left join (select menu_id from sweepmgr_auth where role_id = ?) b on  a.id = b.menu_id ORDER BY id desc ");
		list = (List<CategoryTreeBeanCk>)jdbcTemplate.query(sb.toString(), new CategoryTreeBeanCKRowMapper(),roleId);
		return list;
	}
	
	public static class CategoryTreeBeanRowMapper implements RowMapper<CategoryTreeBean> {
		@Override
		public CategoryTreeBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			CategoryTreeBean devBind = new CategoryTreeBean();
			devBind.setId(rs.getString("id"));
			devBind.setText(rs.getString("menu_name"));
			devBind.setParent_id(rs.getString("parent_id"));
			devBind.setHref(rs.getString("menu_url"));
			devBind.setLeaf(rs.getInt("leaf")==0?false:true );
			return devBind;
		}
	}
	public static class CategoryTreeBeanCKRowMapper implements RowMapper<CategoryTreeBeanCk> {
		@Override
		public CategoryTreeBeanCk mapRow(ResultSet rs, int rowNum) throws SQLException {
			CategoryTreeBeanCk devBind = new CategoryTreeBeanCk();
			devBind.setId(rs.getString("id"));
			devBind.setText(rs.getString("menu_name"));
			devBind.setParent_id(rs.getString("parent_id"));
//			devBind.setHref(rs.getString("menu_url"));
			devBind.setLeaf(rs.getInt("leaf")==0?false:true );
			devBind.setChecked(rs.getString("is_check") == null?false:true);
			return devBind;
		}
	}
	
	
	
	public static class UserLoginRowMapper implements RowMapper<UserLogin> {
		@Override
		public UserLogin mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserLogin rm = new UserLogin();
			rm.setId(rs.getString("id"));
			rm.setUserName(rs.getString("user_name"));
			rm.setUserPassword(rs.getString("user_password"));
			rm.setUserRole(rs.getString("user_role"));
			rm.setUserRoleId(rs.getString("user_role_id"));
			rm.setIsDelete(rs.getInt("is_delete"));
			return rm;
		}
	}
	public static class UserRoleRowMapper implements RowMapper<UserRole> {
		@Override
		public UserRole mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserRole rm = new UserRole();
			rm.setId(rs.getString("id"));
			rm.setRoleName(rs.getString("role_name"));
			return rm;
		}
	}
	public static class ModuleStoreBeanRowMapper implements RowMapper<ModuleStoreBean> {
		@Override
		public ModuleStoreBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			ModuleStoreBean rm = new ModuleStoreBean();
			rm.setValue(rs.getString("id"));
			rm.setText(rs.getString("role_name"));
			return rm;
		}
	}
	
	public Page<Map<String, Object>> getWebUser(String userName,String userRole,int start,int limit){
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(start, limit, false);
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(" select bu.id, bu.name,bu.base_user_role,bup.name as parent_name,ba.name as area_name,bu.is_allow_login,bu.login_name ");
		sql.append(" from base_user bu");
		sql.append(" LEFT JOIN base_user bup on  bu.parent_id = bup.id ");
		sql.append(" LEFT JOIN base_area ba on bu.area_id = ba.id ");
		sql.append(" where 1=1 AND (bu.auth_type is null || bu.auth_type=1) ");
		if(StringUtils.isNotBlank(userName)){
			sql.append(" and bu.name like ? ");
			params.add("%"+userName+"%");
		}
		if(StringUtils.isNotBlank(userRole)){
			sql.append(" and bu.base_user_role =  ? ");
			params.add(userRole);
		}
		sql.append(" GROUP BY bu.name ,bu.base_user_role ");
		sql.append(" order BY bu.name desc ");
		List<Map<String, Object>> countList = jdbcTemplate.queryForList(sql.toString(),params.toArray());
		page.setTotalCount(countList.size());
		sql.append(" limit ?,?");
		params.add(start);
		params.add(limit);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
		page.setResult(list);
		return page;
	}

	public Page<Map<String, Object>> getWebUserRole(){
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(0, 1000, false);
		String sql = " select DISTINCT(base_user_role) from base_user ";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		page.setResult(list);
		return page;
	}
	
	
	public void setWebUserAllowLogin(String[] ids,String value){
		if(ids != null && ids.length>0){//允许登陆用户设置
				String sql = " select name,base_user_role,login_name ,is_allow_login from base_user where id = ? ";
				String upsql = " ";
				List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
				for(int i =0;i<ids.length;i++){
					if("0".equals(value)){
						list = jdbcTemplate.queryForList(sql,ids[i]);
						if(list != null && list.size()>0){
							String isAllowLogin = list.get(0).get("is_allow_login")+"";
							if(StringUtils.isNotBlank(isAllowLogin)){
								if("1".equals(isAllowLogin)){//未设置过登陆
//									String namePy = getPinyin((String)list.get(0).get("name"));
//									String loginName = list.get(0).get("base_user_role")+"_"+namePy;
//									String loginPwd = namePy+"888888";
									String name = (String)list.get(0).get("name");
									String role = (String)list.get(0).get("base_user_role");
									String loginName = getDefaultName(name, role);
									String loginPwd = getDefaultPwd(name, role);
									upsql = "update base_user set login_name = ? ,login_pwd = ?,is_allow_login = 0 where id = ?";
									jdbcTemplate.update(upsql, loginName,loginPwd,ids[i]);
								}else{//设置过登陆
									//不做操作
								}
							}
						}
					}else{
						upsql = " update base_user set is_allow_login = 1 where id = ?  ";
						jdbcTemplate.update(upsql,ids[i] );
					}
				}
			
		}
			
	}
	/**
	 * 设置后台登陆
	 * @param ids
	 * @param deids
	 * @param value 0 设置登陆。1取消登陆
	 */
	public synchronized void setConsoleUserAllowLogin(String[] ids,String value){
		if(ids != null && ids.length>0){//允许登陆用户设置
			String sql = " update user_login set is_allow_weblogin = "+value+" where id = ? ";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
			String userName = "";
			String userPwd = "";
			String uuid = "";
			List<Object> params = new ArrayList<Object>();
			String sqls = "";
			String sqlt = "";
			String sqlm = "";
			String cancelSql = "";
			for(int i =0;i<ids.length;i++){
				params = new ArrayList<Object>();
				jdbcTemplate.update(sql,ids[i]);
				sqlt = " select user_name,user_password  from user_login where id = ? ";
				list2 = jdbcTemplate.queryForList(sqlt,ids[i]);
				userName = (String)list2.get(0).get("user_name");
				userPwd = (String)list2.get(0).get("user_password");
				if("0".equals(value)){//设置登陆
					sqlm = " select * from base_user where login_name = ? and login_pwd =? ";
					List<Map<String, Object>> list3 = jdbcTemplate.queryForList(sqlm,userName,userPwd);
					if(list3 == null || list3.size() == 0){
						uuid = UUID.randomUUID().toString().replace("-", "");
						sqls = " insert into base_user(id,name,login_name,login_pwd,is_allow_login,auth_type)VALUES(?,?,?,?,?,?) ";
						params.add(uuid);
						params.add(userName);
						params.add(userName);
						params.add(userPwd);
						params.add(0);
						params.add(0);
						jdbcTemplate.update(sqls,params.toArray());
					}
				}else{//取消登陆，设置is_allow_login 为1
					cancelSql = " update base_user set is_allow_login = 1 where auth_type = 0 and login_name = ? and login_pwd = ? ";
					params = new ArrayList<Object>();
					params.add(userName);
					params.add(userPwd);
					jdbcTemplate.update(sqls,params.toArray());
				}
			}
		}
	}
	
	public void resetWebUserPwdById(String userId,String userName,String userRole){
		String defaultPwd = getDefaultPwd(userName, userRole);
		String sql = " update base_user set login_pwd = ? where id = ? ";
		if(StringUtils.isNotBlank(userId)){
			jdbcTemplate.update(sql,defaultPwd,userId);
		}
	}
	
	public static String getDefaultPwd(String name,String userRole){
		String namePy = "";
		String defaultPwd = "";
		if(StringUtils.isNotBlank(name)){
			namePy = getPinyin(name);
		}
		if("经销商".equals(userRole)){
			userRole = "JXS";
		}
		defaultPwd = namePy+"_"+userRole+"88888888";
		return defaultPwd;
	}
	public static String getDefaultName(String name,String userRole){
		String namePy = "";
		String defaultName = "";
		if(StringUtils.isNotBlank(name)){
			namePy = getPinyin(name);
		}
		if("经销商".equals(userRole)){
			userRole = "JXS";
		}
		defaultName = namePy+"_"+userRole;
		return defaultName;
	}
	
	public static String getPinyin(String name){
		PinYinHelper py = new PinYinHelper();
		String[] pinyin = py.gePinYin(name);
		String pinyinStr =  "";
		for(String p:pinyin){
			pinyinStr +=p;
		}
		return pinyinStr;
	}
	
}
