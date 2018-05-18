package com.xh.mgr.service;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.xh.mgr.model.RomManager;

@Service
public class RomManagerService {

	static Logger logger = LoggerFactory.getLogger("sweepManager");

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static String[] service = null;




	public void writeExcelData(File file, List<String> msgs) throws IOException {
		WritableWorkbook workbook = Workbook.createWorkbook(file);
		WritableSheet sheet = workbook.createSheet("推送升级结果", 0);
		int outputRow = 0;
		try {
			sheet.addCell(new Label(0, outputRow, "推送升级结果"));
			for (String tmp : msgs) {
				outputRow++;
				sheet.addCell(new Label(0, outputRow, tmp));
			}
			workbook.write();
			workbook.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getGeneralFileName(String title, String ext) {
		return String.format("%s-%s.%s", title, new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), ext);
	}


	public String getRomManager(String id) {
		String sql = "select * from rom_manager where id = ? is_delete = 0";
		List<String> ret = jdbcTemplate.queryForList(sql, new String[] { id }, String.class);
		if (ret != null && ret.size() > 0) {
			return ret.get(0);
		}
		return null;
	}

	public boolean isRepeat(RomManager romManager) {
		boolean flag = false;
		String sql = "";
		int i = 0;
		if (StringUtils.isNotBlank(romManager.getVersion())) {
			sql = " select count(1) from rom_manager where is_delete = 0 and version =  " + romManager.getVersion();
			if (StringUtils.isNotBlank(romManager.getId())) {
				sql = sql + " and id <> '" + romManager.getId() + "'";
			}
			i = jdbcTemplate.queryForInt(sql);
		}
		return i > 0;
	}

	public void insertRomManager(RomManager romManager) {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		String sql = " insert into rom_manager(id,version,type,sys_rom_name,original_rom_name,comment,create_time,modify_time,is_delete,content) VALUES(?,?,?,?,?,?,SYSDATE(),SYSDATE(),0,?) ";
		jdbcTemplate.update(sql, uuid, romManager.getVersion(), romManager.getType(), romManager.getSysRomName(), romManager.getOriginalRomName(),
				romManager.getComment(), romManager.getContent());
	}

	public void updatePushUpdate(String dids, String version) {
		String sql = "";
		sql = "update device_mac set auto_update = '0',force_version ='" + version + "'  where device_id in(" + dids + ")";
		jdbcTemplate.update(sql);
	}

	public void updateAutoUp(String dids, String val) {
		String sql = "";
		if ("0".equals(val)) {
			sql = "update device_mac set auto_update = '" + val + "' where device_id in(" + dids + ")";
		} else {
			sql = "update device_mac set auto_update = '" + val + "' , force_version = null where device_id in(" + dids + ")";
		}
		jdbcTemplate.update(sql);
	}

	public void deleteById(String id) {
		jdbcTemplate.update("update rom_manager set is_delete = 1 where id = ? ", id);
	}

	public int updateRMById(RomManager romManager) {
		if (romManager.getContent() != null) {
			return jdbcTemplate.update(
					"update rom_manager set version = ?,type=?,comment=?,modify_time = SYSDATE(),content=?,original_rom_name=?  where id =?",
					romManager.getVersion(), romManager.getType(), romManager.getComment(), romManager.getContent(), romManager.getOriginalRomName(),
					romManager.getId());
		} else {
			return jdbcTemplate.update("update rom_manager set version = ?,type=?,comment=?,modify_time = SYSDATE() where id =?", romManager.getVersion(),
					romManager.getType(), romManager.getComment(), romManager.getId());
		}
	}

	public List<RomManager> findRMList(Map<String, String> condition) {
		StringBuilder sbud = new StringBuilder("select id,version,type,original_rom_name,comment,create_time,modify_time from rom_manager where is_delete = 0 ");
		boolean islimit = false;
		int start = 0;
		int limit = 0;
		List<Object> params = new ArrayList<Object>();
		int count = 0;
		if (!condition.isEmpty()) {
			if (condition.containsKey("romVersion")) {
				sbud.append(" and version like '%" + condition.get("romVersion") + "%' ");
			}
			if (condition.containsKey("createDateStart")) {
				sbud.append(" and create_time >= '" + condition.get("createDateStart") + "'");
			}
			if (condition.containsKey("createDateEnd")) {
				sbud.append(" and create_time <= '" + condition.get("createDateEnd") + "'");
			}
			if (condition.containsKey("editDateStart")) {
				sbud.append(" and modify_time >= '" + condition.get("editDateStart") + "'");
			}
			if (condition.containsKey("editDateEnd")) {
				sbud.append(" and modify_time <= '" + condition.get("editDateEnd") + "'");
			}
			sbud.append(" order by create_time ");
			try {
				List<RomManager> ret1 = null;
				ret1 = (List<RomManager>) jdbcTemplate.query(sbud.toString(), new RomManagerRowMapper(), params.toArray(new Object[params.size()]));
				if (ret1 != null) {
					count = ret1.size();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (condition.containsKey("start") && condition.containsKey("limit")) {
				sbud.append(" LIMIT ?,? ");
				islimit = true;
				start = Integer.parseInt(condition.get("start"));
				limit = Integer.parseInt(condition.get("limit"));
				params.add(start);
				params.add(limit);
			}
		}
		List<RomManager> ret = null;
		ret = (List<RomManager>) jdbcTemplate.query(sbud.toString(), new RomManagerRowMapper(), params.toArray(new Object[params.size()]));
		if (ret != null && ret.size() > 0) {
			return ret;
		}
		return null;
	}

	public List<RomManager> countRMList(Map<String, String> condition) {
		StringBuilder sbud = new StringBuilder("select id,version,type,original_rom_name,comment,create_time,modify_time from rom_manager where is_delete = 0 ");
		List<String> params = new ArrayList<String>();
		if (!condition.isEmpty()) {
			if (condition.containsKey("romVersion")) {
				sbud.append("and version like '%" + condition.get("romVersion") + "%' ");
			}
			if (condition.containsKey("createDateStart")) {
				sbud.append(" and create_time >= '" + condition.get("createDateStart") + "'");
			}
			if (condition.containsKey("createDateEnd")) {
				sbud.append(" and create_time <= '" + condition.get("createDateEnd") + "'");
			}
			if (condition.containsKey("editDateStart")) {
				sbud.append(" and modify_time >= '" + condition.get("editDateStart") + "'");
			}
			if (condition.containsKey("editDateEnd")) {
				sbud.append(" and modify_time <= '" + condition.get("editDateEnd") + "'");
			}
		}
		List<RomManager> ret = (List<RomManager>) jdbcTemplate.query(sbud.toString(), new RomManagerRowMapper(), params.toArray(new Object[params.size()]));
		if (ret != null && ret.size() > 0) {
			return ret;
		}
		return null;
	}
	
	public String getUserIdByDeviceW(String deviceIdw){
		List<String> list = null;
		String userIds = "";
		list = jdbcTemplate.queryForList("select user_id from device_bind where device_id_w in ('"+deviceIdw+"') and status <> 0", String.class);
		if(list != null){
			for(String str:list){
				userIds = userIds + str +",";
			}
			if(StringUtils.isNotBlank(userIds)){
				userIds = userIds.substring(0, userIds.length()-1);
			}
		}
		return userIds;
	}

	public static class RomManagerRowMapper implements RowMapper<RomManager> {
		@Override
		public RomManager mapRow(ResultSet rs, int rowNum) throws SQLException {
			RomManager rm = new RomManager();
			rm.setId(rs.getString("id"));
			rm.setComment(rs.getString("comment"));
			// rm.setContent(rs.getBytes("content"));
			rm.setCreateTime(rs.getString("create_time"));
			rm.setModifyTime(rs.getString("modify_time"));
			// rm.setIs_delete(rs.getString("is_delete"));
			rm.setOriginalRomName(rs.getString("original_rom_name"));
			// rm.setSysRomName(rs.getString("sys_rom_name"));
			rm.setType(rs.getString("type"));
			rm.setVersion(rs.getString("version"));
			return rm;
		}
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
