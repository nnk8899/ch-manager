package com.xh.mgr.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.xh.mgr.common.BaseAction;
import com.xh.mgr.common.Page;

import com.xh.mgr.service.SettingMgrService;

public class SettingMgrAction extends BaseAction{
	private static final long serialVersionUID = -4553868634352910686L;
	
	@Autowired
	private SettingMgrService settingService;
	
	public void getSettingInfo(){
		String startParam = getRequest().getParameter("start");
		String limitParam = getRequest().getParameter("limit");
		String type = getRequest().getParameter("type");
		String keyWord = getRequest().getParameter("keyWord");
		int start = 0;
		int limit = 20;
		if(StringUtils.isNotBlank(startParam)){
			start = Integer.parseInt(startParam);
		}
		if(StringUtils.isNotBlank(limitParam)){
			limit = Integer.parseInt(limitParam);
		}
		Page<Map<String, Object>> page = settingService.getSettingInfo(type,keyWord, start, limit);
		resultSuccess(null, page.getResult(), page.getTotalCount());
	}

	public void updateSettingInfo() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String id = getRequest().getParameter("uId");
		String name = getRequest().getParameter("uName");
		String value = getRequest().getParameter("uValue");
		String msg = null;
		try {
			settingService.updateSettingInfo(id, name, value);
		} catch (Exception e) {
			msg = "保存失败！"+e.getMessage();
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	public void getYearAndMonth(){
		String type = getRequest().getParameter("type");
		Page<Map<String, Object>> page = settingService.getMonthData(type);
		resultSuccess(null, page.getResult(), page.getTotalCount());
	}
	
	
	public void listImport(){
		String startParam = getRequest().getParameter("start");
		String limitParam = getRequest().getParameter("limit");
		String month = getRequest().getParameter("searchMonth");
		String startDate = getRequest().getParameter("createDateStart");
		String endDate = getRequest().getParameter("createDateEnd");
		int start = 0;
		int limit = 20;
		if(StringUtils.isNotBlank(startParam)){
			start = Integer.parseInt(startParam);
		}
		if(StringUtils.isNotBlank(limitParam)){
			limit = Integer.parseInt(limitParam);
		}
		Page<Map<String, Object>> page = settingService.getImportInfo(month, start, limit, startDate, endDate);
		resultSuccess(null, page.getResult(), page.getTotalCount());
	}
	
	public void saveCompareMonth() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String msg = null;
		String ids = getRequest().getParameter("ids");
//		String deids = getRequest().getParameter("deids");//非选中
		String value = getRequest().getParameter("value");
		if(ids.endsWith(",")){
			ids = ids.substring(0, ids.length()-1);
		}
		ids = ids.replaceAll(",","','");
		if(StringUtils.isNotBlank(ids)){
			ids = "'"+ids+"'";
		}
		
//		deids = deids.replaceAll(",","','");
//		if(StringUtils.isNotBlank(deids)){
//			deids = "'"+deids+"'";
//		}
		
		try {
			//		System.out.println();
			if("0".equals(value)){
				settingService.updateCompareMonth(ids);
			}else{
				settingService.cancelUpdateCompareMonth(ids);
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	public void deleteImportById() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String msg = null;
		String id = getRequest().getParameter("id");
		
		try {
			settingService.deleteImportById(id);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	public void deleteSettingInfoById() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String msg = null;
		String id = getRequest().getParameter("id");
		try {
			settingService.deleteSettingInfoById(id);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	
	public void getChildNode(){
		String id = getRequest().getParameter("id");
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = settingService.getChildNodeById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultSuccess(list);
	}
	
	public void deleteManagerUser() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String msg = null;
		String id = getRequest().getParameter("id");
		try {
			settingService.deleteManagerUser(id);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	public void listManageUser(){
		String name = getRequest().getParameter("name");
		String username = getRequest().getParameter("userName");
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = settingService.listManageUser(name, username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultSuccess(list);
	}
	
	public void editManageUser() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String msg = null;
		String userId = getRequest().getParameter("userId");
		String name = getRequest().getParameter("name");
		String username = getRequest().getParameter("userName");
		String userpwd = getRequest().getParameter("userPwd");
		String daqu = getRequest().getParameter("daqu");
		String sheng = getRequest().getParameter("sheng");
		String city = getRequest().getParameter("city");
		try {
			settingService.editManageUser(userId, name, username, userpwd, daqu, sheng, city);
		} catch (Exception e) {
			e.printStackTrace();
			msg = e.getMessage();
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	public void listMonth(){
		String name = getRequest().getParameter("value");
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			list = settingService.listMonth(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultSuccess(list);
	}
	
	
	public void addMonth() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String msg = null;
		String month = getRequest().getParameter("month");
		if(StringUtils.isNotBlank(month)){
			try {
				msg = settingService.addMonth(month);
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
			}
		}else{
			msg ="月份参数为空！";
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	public void deleteMonth() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		String msg = null;
		String id = getRequest().getParameter("id");
		if(StringUtils.isNotBlank(id)){
			try {
				settingService.deleteMonth(id);
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
			}
		}else{
			msg ="id参数为空！";
		}
		if(msg == null){
			map.put("i_type", "success");
			map.put("i_msg", "");
		}else{
			map.put("i_type", "error");
			map.put("i_msg", msg);
		}
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	
	public List<String> analysisStr(String value,String flag){
		List<String> list = new ArrayList<String>();
		try {
			if (StringUtils.isNotBlank(value)) {
				String[] tmp = null;
				tmp = value.split(flag);
				for (String str : tmp) {
					list.add(str);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/**
	 * 功能：公共方法用于响应前台请求
	 * @param response
	 * @param data
	 */
	private void printData(HttpServletResponse response, String data) {
		try {
//			System.out.println(data);
			response.setContentType("text/html;charset=utf-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
			out.println(data);
			out.close();
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
