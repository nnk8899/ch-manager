package com.xh.mgr.web;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.xh.mgr.common.BaseAction;
import com.xh.mgr.model.OperateHistory;
import com.xh.mgr.model.RomManager;
import com.xh.mgr.service.OperateHistoryService;
import com.xh.mgr.util.ToolUtils;

public class OperateHistoryAction extends BaseAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3275714765223850730L;
	
	@Autowired
	private OperateHistoryService operateHistoryService;
	public void list() throws JsonGenerationException, JsonMappingException, IOException{
		String msg = "";
		String outPut = "";
		try{
			Map<String,String> condition = new HashMap<String, String>();
			String operateUser = getRequest().getParameter("operateUser");
			String createDateStart = getRequest().getParameter("createDateStart");
			String createDateEnd = getRequest().getParameter("createDateEnd");
			String start = getRequest().getParameter("start");
			String limit = getRequest().getParameter("limit");;
			if (ToolUtils.isNotBlank(operateUser)) {
				condition.put("operateUser", operateUser);
			}
			if (createDateStart != null && !"".equals(createDateStart.trim())) {
				condition.put("createDateStart", createDateStart);
			}
			if (createDateEnd != null && !"".equals(createDateEnd.trim())) {
				condition.put("createDateEnd", createDateEnd);
			}
			if (start != null && !"".equals(start.trim())) {
				condition.put("start", start);
			}
			if (limit != null && !"".equals(limit.trim())) {
				condition.put("limit", limit);
			}
			try {
				outPut = operateHistoryService.getOperateList(condition);
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
			}
			this.printData(getResponse(), outPut);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void listForLogin() throws JsonGenerationException, JsonMappingException, IOException{
		String msg = "";
		String outPut = "";
		try{
			Map<String,String> condition = new HashMap<String, String>();
			String operateUser = getRequest().getParameter("operateUser");
			String createDateStart = getRequest().getParameter("createDateStart");
			String createDateEnd = getRequest().getParameter("createDateEnd");
			String start = getRequest().getParameter("start");
			String limit = getRequest().getParameter("limit");;
			if (ToolUtils.isNotBlank(operateUser)) {
				condition.put("operateUser", operateUser);
			}
			if (createDateStart != null && !"".equals(createDateStart.trim())) {
				condition.put("createDateStart", createDateStart);
			}
			if (createDateEnd != null && !"".equals(createDateEnd.trim())) {
				condition.put("createDateEnd", createDateEnd);
			}
			if (start != null && !"".equals(start.trim())) {
				condition.put("start", start);
			}
			if (limit != null && !"".equals(limit.trim())) {
				condition.put("limit", limit);
			}
			try {
				outPut = operateHistoryService.getOperateListForlogin(condition);
			} catch (Exception e) {
				e.printStackTrace();
				msg = e.getMessage();
			}
			this.printData(getResponse(), outPut);
		}catch(Exception e){
			e.printStackTrace();
		}
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
