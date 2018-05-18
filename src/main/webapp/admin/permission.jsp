<%@ page contentType="text/html; charset=UTF-8" language="java" errorPage="" %>
<%@ page language="java" import="java.util.*"%>
<%   
	String isLogin = (String)session.getAttribute("isLogin");
	String pathName = (String)request.getParameter("pathName");
	String ErrorMsg = "您尚未登录！！";
	String urlsessionnoactive = request.getContextPath() + "/admin/login.jsp?pathName="+pathName;
	if(isLogin == null || "".equals(isLogin)){
%>   
<script language="javascript">
	var urlsessionnoactive="<%=urlsessionnoactive%>";
	//alert("<%=ErrorMsg%>");
	top.location.href=urlsessionnoactive;
</script>
<%
}else{
	List<String> commonAuthList = (List<String>) session.getAttribute("userAuthList");
	System.out.println(commonAuthList);
%>
<script language="javascript">
	var commonAuthList = new Array();
	commonAuthList = "<%=commonAuthList%>".replace('[','').replace(']','').replace(/^\s+|\s+$/g, '').split(',');
	console.log(commonAuthList);
	/**
	 * 判断是否隐藏按钮
	 */
	function isHidBtn(btnId){
	    for(var i = 0; i < commonAuthList.length; i++){
	        if(btnId === commonAuthList[i]){
	            return false;
	        }
	    }
	    return true;
	}
	console.log(isHidBtn('118'));
</script>
<%} %>>