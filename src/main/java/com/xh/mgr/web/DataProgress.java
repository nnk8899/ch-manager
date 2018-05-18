package com.xh.mgr.web;

import java.util.ArrayList;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.xh.mgr.service.ImportDataService;
import com.xh.mgr.service.OperateHistoryService;

@Component
public class DataProgress extends Thread implements ApplicationContextAware{
	private ImportDataService importDataService;
	private OperateHistoryService operateHistoryService;
	public ArrayList<ArrayList<Object>> list;
	public String title;
	public String month;
	public String description;
	public String operateUserId;
	// Spring应用上下文环境
    private static ApplicationContext applicationContext;
    
	DataProgress(){
		super();
	}
	@Override
	public void run(){
		String msg = null;
		importDataService = (ImportDataService)getBean("importDataService");
		operateHistoryService = (OperateHistoryService)getBean("operateHistoryService");
		long starTime=System.currentTimeMillis();
		try {
			importDataService.importDatass(list, title, month,description, operateUserId);
		} catch (Exception e) {
			msg = e.getMessage();
		}
		long endTime=System.currentTimeMillis();
		long usetime =Math.round((endTime - starTime)/1000);
		operateHistoryService.insertOH(operateUserId , "5", msg==null?"用时："+usetime+"秒":msg,msg==null?1:0);
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		DataProgress.applicationContext = applicationContext;
	}
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    /**
     * 获取对象 这里重写了bean方法，起主要作用
     */
    public static Object getBean(String beanId) throws BeansException {
        return applicationContext.getBean(beanId);
    }
	
	
	
	
	
	public ArrayList<ArrayList<Object>> getList() {
		return list;
	}
	public void setList(ArrayList<ArrayList<Object>> list) {
		this.list = list;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOperateUserId() {
		return operateUserId;
	}
	public void setOperateUserId(String operateUserId) {
		this.operateUserId = operateUserId;
	}
	
}
