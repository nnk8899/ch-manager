package com.xh.mgr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.xh.mgr.common.Page;
import com.xh.mgr.util.ToolUtils;

@Service
public class SettingMgrService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	static Logger logger = LoggerFactory.getLogger("SettingMgrService");
	
	public Page<Map<String, Object>> getSettingInfo(String type,String keyWord,int start,int limit){
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(start, limit, false);
		StringBuilder sql = new StringBuilder();
		List<Object> conditions = new ArrayList<Object>();
		sql.append(" select * from setting_manager where 1=1 ");
		if(StringUtils.isNotBlank(type)){
			sql.append(" and type = ? ");
			conditions.add(type);
		}
		if(StringUtils.isNotBlank(keyWord)){
			sql.append(" and name like ? ");
			conditions.add("%"+keyWord+"%");
		}
		List<Map<String, Object>> countList = jdbcTemplate.queryForList(sql.toString(),conditions.toArray());
		page.setTotalCount(countList.size());
		sql.append(" limit ?,?");
		conditions.add(start);
		conditions.add(limit);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), conditions.toArray());
		page.setResult(list);
		return page;
	}
	
	public void updateSettingInfo(String id,String name,String value){
		List<Object> params = new ArrayList<Object>();
		StringBuilder sb = new StringBuilder(" update setting_manager set id = ? ");
		if(ToolUtils.isNotBlank(name)){
			params.add(id);
			if(ToolUtils.isNotBlank(name)){
				sb.append(",name = ?");
				params.add(name);
			}
			if(ToolUtils.isNotBlank(value)){
				sb.append(",value = ?");
				params.add(value);
			}
			sb.append(" where id = ?");
			params.add(id);
		}
		jdbcTemplate.update(sb.toString(), params.toArray(new Object[params.size()]));
	}
	
	public Page<Map<String, Object>> getMonthData(String type){
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(0, 1010, false);
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder(" select value as name,value as value from base_month where type =? ");
		params.add(type);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(),params.toArray());
		page.setTotalCount(list.size());
		page.setResult(list);
		return page;
	}
	
	public Page<Map<String, Object>> getImportInfo(String month,int start,int limit,String startDate,String endDate){
		Page<Map<String, Object>> page = new Page<Map<String, Object>>(start, limit, false);
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder(" select a.id,a.title,a.description,a.month,b.user_name as operate_user_name,a.create_date,a.is_checked  ");
		sql.append(" from analyze_data_importinfo a ,user_login b where a.operate_user_id = b.id ");
		if(StringUtils.isNotBlank(month)){
			sql.append(" and a.month = ? ");
			params.add(month);
		}
		if(StringUtils.isNotBlank(startDate)){
			sql.append(" and a.create_date >= ? ");
			params.add(startDate);
		}
		if(StringUtils.isNotBlank(endDate)){
			sql.append(" and a.create_date <= ? ");
			params.add(endDate);
		}
		sql.append(" order by a.month ");
		
		List<Map<String, Object>> countList = jdbcTemplate.queryForList(sql.toString(),params.toArray());
		page.setTotalCount(countList.size());
		sql.append(" limit ?,?");
		params.add(start);
		params.add(limit);
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
		page.setResult(list);
		return page;
	}
	
	public void updateCompareMonth(String ids){
		if(StringUtils.isNotBlank(ids)){
			String sql = " update analyze_data_importinfo set is_checked = 0 where id in ("+ids+") ";
			jdbcTemplate.update(sql);
		}
	}
	public void cancelUpdateCompareMonth(String ids){
		if(StringUtils.isNotBlank(ids)){
			String sql = " update analyze_data_importinfo set is_checked = 1 where id in ("+ids+") ";
			jdbcTemplate.update(sql);
		}
	}
	
	public void deleteImportById(String importId){
		String importinfsql = " delete from analyze_data_importinfo where id = ? ";
		String scoresql = " delete from analyze_data_score where import_id = ? ";
		String stocksql = " delete from analyze_data_stock where import_id = ? ";
		String targetsql = " delete from analyze_data_target where import_id = ? ";
		String storessql = " delete from base_stores where import_id = ? ";
		String usersql = " delete from base_user where import_id = ? ";
		String areaSql = " delete from base_area where import_id = ? ";
		jdbcTemplate.update(importinfsql,importId);
		jdbcTemplate.update(scoresql,importId);
		jdbcTemplate.update(stocksql,importId);
		jdbcTemplate.update(targetsql,importId);
		jdbcTemplate.update(storessql,importId);
		jdbcTemplate.update(usersql,importId);
		jdbcTemplate.update(areaSql,importId);
	}
	public void deleteSettingInfoById(String id){
		String sql = " delete from setting_manager where id  = ? ";
		if(StringUtils.isNotBlank(id)){
			jdbcTemplate.update(sql,id);
		}
	}
	
	/**
	 * 根据父节点ID获取子节点集合
	 * @param parentId
	 */
	public List<Map<String, Object>> getChildNodeById(String parentId){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("value", "2");
		map.put("name", "全部");
		list2.add(map);
		StringBuilder sb = new StringBuilder();
		sb.append(" select id as value,name from base_area where parent_id = ? ");
		if("0".equals(parentId)){
			sb.append(" and id not in (200,2900,3000,3100)  ");
		}
		if(StringUtils.isNotBlank(parentId)){
			list = jdbcTemplate.queryForList(sb.toString(),parentId);
		}
		list2.addAll(list);
		if(list2 != null && list2.size()>0){
			return list2;
		}
		return null;
	}
	
	/**
	 * 权限用户操作
	 * @param parentId
	 */
	public void editManageUser(String userId,String name,String username,String userpwd ,String daqu,String sheng,String city ){
		if(checkLoginName(username,userId)){
			if("undefined".equals(userId)){//编辑
				String sql = " update base_user set name = ? ,login_name=?,login_pwd=? ,auth_quanguo=1,auth_daqu=?,auth_province=?,auth_city=?,is_allow_login=0 where id = ? ";
				jdbcTemplate.update(sql,name,username,userpwd,daqu,sheng,city,userId);
			}else{//新加
				String id = UUID.randomUUID().toString().replace("-", "");
				String sql = " insert into base_user(id,name,login_name,login_pwd,auth_quanguo,auth_daqu,auth_province,auth_city,auth_type,is_allow_login)VALUES(?,?,?,?,1,?,?,?,2,0) ";
				jdbcTemplate.update(sql, id,name,username,userpwd,daqu,sheng,city);
			}
		}else{
			throw new RuntimeException("登录名："+username+" 有重复！");
		}
	}
	
	
	public boolean checkLoginName(String loginName,String id){
		String sql = " select id from base_user where login_name = ? ";
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		if(StringUtils.isBlank(id)){
			if(StringUtils.isNotBlank(loginName)){
				list = jdbcTemplate.queryForList(sql,loginName);
			}
		}else{
			sql = sql +" and id <> ? ";
			list = jdbcTemplate.queryForList(sql,loginName,id);
		}
		if(list == null || list.size() == 0){
			return true;
		}else{
			return false;
		}
	}
	
	public List<Map<String, Object>> listManageUser(String name,String username){
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(" select bu.id, bu.name,login_name,login_pwd,ba1.name as quanguo,ba2.name as daqu,ba3.name as sheng,ba4.name as city ");
		sql.append(" from base_user bu ");
		sql.append(" LEFT JOIN base_area ba1 on  bu.auth_quanguo = ba1.id  ");
		sql.append(" LEFT JOIN base_area ba2 on  bu.auth_daqu = ba2.id ");
		sql.append(" LEFT JOIN base_area ba3 on  bu.auth_province = ba3.id ");
		sql.append(" LEFT JOIN base_area ba4 on  bu.auth_city = ba4.id ");
		sql.append(" where 1=1 ");
		sql.append(" and auth_type = 2 ");
		if(StringUtils.isNotBlank(name)){
			sql.append(" and bu.name like ? ");
			params.add("%"+name+"%");
		}
		if(StringUtils.isNotBlank(username)){
			sql.append(" and login_name like ? ");
			params.add("%"+username+"%");
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(),params.toArray());
		return list;
	}
	public List<Map<String, Object>> listMonth(String name){
		List<Object> params = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from base_month where 1=1 ");
		if(StringUtils.isNotBlank(name)){
			sql.append(" and value like ? ");
			params.add("%"+name+"%");
		}
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(),params.toArray());
		return list;
	}
	
	
	public String addMonth(String month){
		String msg = null;
		List<Map<String, Object>> cklist = checkMonth(month);
		if(cklist != null && cklist.size()>0){
			msg = month+"已存在！";
		}else{
			String sql = " INSERT into base_month(value,type)VALUES(?,1) ";
			jdbcTemplate.update(sql,month);
		}
		return msg;
	}
	public List<Map<String, Object>> checkMonth(String month){
		String sql = " select * from base_month where value = ?  ";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,month);
		return list;
	}
	
	public void deleteMonth(String id){
		String sql =" delete from base_month where id = ? ";
		if(StringUtils.isNotBlank(id)){
			jdbcTemplate.update(sql,id);
		}
	}
	public void deleteManagerUser(String id){
		String sql =" delete from base_user where id = ? ";
		if(StringUtils.isNotBlank(id)){
			jdbcTemplate.update(sql,id);
		}
	}
	
}
