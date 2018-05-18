package com.xh.mgr.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.xh.mgr.common.BaseAction;
import com.xh.mgr.service.ImportDataService;
import com.xh.mgr.service.OperateHistoryService;
import com.xh.mgr.util.ExcelReadUtils;
import com.xh.mgr.util.PoiBigExcelUtil;

public class UpLoadFileAction extends BaseAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1679523149413390544L;
	private ServletContext sc;
	private String savePath="/uploadFile";
	
	// 上传文件域对象
	private File uploadFile; // 与上传属性框的name保持一致   //上传的文件，在extjs 中对应 xtype:'fileuploadfield',  name:'uploadFile'  
	// 上传文件名
	private String uploadFileFileName;
	
	private String importTitle;
	private String importyear;
	private String importmonth;
	private String importComment;
	
	@Autowired
	private ImportDataService importDataService;
	@Autowired
	private OperateHistoryService operateHistoryService;
	public void uploadFile() throws IOException {
		HttpSession session=getRequest().getSession();
		String msg = null;
		String uploadType = getRequest().getParameter("uploadType");
//		String searchc = getRequest().getParameter("searchc");
		String operateUserId = (String)session.getAttribute("userId");
		Map<String, String> map = new HashMap<String, String>();
		if ("1".equals(uploadType)) {
			
			String tempFileDir = getRequest().getSession().getServletContext().getRealPath("/uploadFile");
			File tempDir = new File(tempFileDir);
			final File tempFile = new File(tempDir, getUploadFileFileName());
			try {
				if (!tempFile.exists()) {
					tempFile.createNewFile();
				}
				FileUtils.copyFile(getUploadFile(), tempFile);
				ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
				try {
					list = ExcelReadUtils.readAllRows(tempFile);
				} catch (Exception e) {
					msg = "EXCEL文件解析失败，请确认文件。错误信息："+e.getMessage();
					e.printStackTrace();
				}
				if(StringUtils.isBlank(msg)){
					try {
	//					importDataService.importDatass(list, importTitle,importyear + importmonth, importComment,operateUserId);
						DataProgress dp = new DataProgress();
						dp.setList(list);
						dp.setTitle(importTitle);
						dp.setMonth(importyear);
						dp.setOperateUserId(operateUserId);
						dp.start();
					} catch (Exception e) {
						e.printStackTrace();
						msg = e.getMessage();
					}
				}
				if(msg == null){
					map.put("i_type", "success");
					map.put("success", "true");
					map.put("i_msg", msg);
				}else{
					map.put("i_type", "error");
					map.put("success", "true");
					map.put("i_msg", msg);
				}
				ObjectMapper mapper = new ObjectMapper();
				if(StringUtils.isNotBlank(msg)){
					operateHistoryService.insertOH(getRequest(),(String)session.getAttribute("userId") , "5", msg,msg==null?1:0);
				}
				this.printData(getResponse(), mapper.writeValueAsString(map));
//				resultSuccess("12312");
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}else{
			String tempFileDir = getRequest().getSession().getServletContext().getRealPath("/uploadFile");
			String body = "";
			String ext = "";
			String newName  = "";
			Date date = new Date();
			int pot = getUploadFileFileName().lastIndexOf(".");
			if (pot != -1) {
				body = date.getTime() + "";
				ext = getUploadFileFileName().substring(pot);
			} else {
				body = (new Date()).getTime() + "";
				ext = "";
			}
			newName = body + ext;
			
			File tempDir = new File(tempFileDir);
			if(!tempDir.isDirectory()){
				tempDir.mkdir();
			}
			File tempFile = new File(tempDir, newName);
			if (!tempFile.exists()) {
				tempFile.createNewFile();
			}
			FileUtils.copyFile(getUploadFile(), tempFile);
			map.put("filePath", tempFileDir	+File.separator+ newName);
			map.put("fileName", newName);
		}
		
		if(msg == null){
			map.put("i_type", "success");
			map.put("success", "true");
			map.put("i_msg", msg);
		}else{
			map.put("i_type", "error");
			map.put("success", "true");
			map.put("i_msg", msg);
		}
		ObjectMapper mapper = new ObjectMapper();
		this.printData(getResponse(), mapper.writeValueAsString(map));

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

	public File getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(File uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getUploadFileFileName() {
		return uploadFileFileName;
	}

	public void setUploadFileFileName(String uploadFileFileName) {
		this.uploadFileFileName = uploadFileFileName;
	}

	public String getImportTitle() {
		return importTitle;
	}

	public void setImportTitle(String importTitle) {
		this.importTitle = importTitle;
	}

	public String getImportyear() {
		return importyear;
	}

	public void setImportyear(String importyear) {
		this.importyear = importyear;
	}

	public String getImportmonth() {
		return importmonth;
	}

	public void setImportmonth(String importmonth) {
		this.importmonth = importmonth;
	}

	public String getImportComment() {
		return importComment;
	}

	public void setImportComment(String importComment) {
		this.importComment = importComment;
	}

	
	
}
