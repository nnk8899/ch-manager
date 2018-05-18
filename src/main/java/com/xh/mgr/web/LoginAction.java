package com.xh.mgr.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.xh.mgr.model.UserLogin;
import com.xh.mgr.service.LoginService;
import com.xh.mgr.service.UserService;
import com.xh.mgr.util.AESUtil;
import com.xh.mgr.common.BaseAction;

public class LoginAction extends BaseAction implements InitializingBean{

	private static final long serialVersionUID = 4566903014123125802L;
//	public static String key = "1234567890123456";
//	public static String key = "1234567890123450";
	@Autowired
	private LoginService loginService;
	@Autowired
	private UserService userService;
	Logger logger = LoggerFactory.getLogger("sweepManager");
	public void login() throws JsonGenerationException, JsonMappingException, IOException{
		String key = ")w[P`Zp6R%B$?MQ`";
//		System.out.println(key);
		HttpSession session=getRequest().getSession();
		String userName = getRequest().getParameter(AESUtil.getInstance().decrypt("MQKRRYz13INs8CyVWzw9pA==",key));
		String userPwd = getRequest().getParameter(AESUtil.getInstance().decrypt("rcGJrOzfcSSXVtd6emIWMA==",key));//sEkELSrH53x0Lvdll0I6pA==
		String userRole = "";
		String userRoleId = "";
		String userId = "";
		String msg = "";
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		if(loginService.checkUser(userName, userPwd,key)){//用户名密码
			//验证成功，进行权限设置
			
			map.put("i_type", "success");
			session.setAttribute("isLogin", "0");
			session.setAttribute("userName", userName);
			List<UserLogin> list = new ArrayList<UserLogin>();
			Map<String, String> condition = new HashMap<String, String>();
			condition.put("userName", userName);
			condition.put("userPwd", userPwd);
			try {
//				userRole = loginService.getUserRole(userName);
				list = userService.getUserListLogin(condition);
				if(list.size() > 0){
					UserLogin user = list.get(0);
					userRole = user.getUserRole()+"";
					userRoleId = user.getUserRoleId();
					userId = user.getId();
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("用户：" + userName + ",在" + new Date() + "获取角色信息失败，错误信息："+e.getMessage());
			}
			session.setAttribute("userRole", userRole);
			session.setAttribute("userRoleId", userRoleId);
			session.setAttribute("userId", userId);
			if(StringUtils.isNotBlank(userRoleId)){
				List<String> authList = userService.getUserRoleAuth(userRoleId);
				session.setAttribute("userAuthList",authList);
			}
			try {
				logger.info("用户：" + userName + ",在" + new Date() + "登录成功。");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			try {
				logger.info("用户：" + userName + ",在" + new Date() + "登录失败。");
			} catch (Exception e) {
				e.printStackTrace();
			}
			session.setAttribute("isLogin", "1");
			map.put("i_type", "error");
			map.put("i_msg", "用户名或密码错误！");
		}
		this.printData(getResponse(), mapper.writeValueAsString(map));
	}
	
	/**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url) {
    	String param = "";
    	String newUrl = "";
    	if(url.contains("?")){
    		String s[] = url.split("\\?");
    		newUrl = s[0];
    		param = s[1];
    	}
        PrintWriter out = null;
        BufferedReader in = null;
//		getParameter("userName");
//		getParameter("userPwd");

        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
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
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
