		function loginOut(){
//			window.location.href("login.jsp");
			window.location.href = "../admin/login.jsp?pathName=index";
		}
  		Ext.onReady(function(){
 				var headPanel = new Ext.Panel(
					{
						region : 'north',
						height : 36,
						border : true,
						html : '<div id="header" class=" docs-header x-border-panel" style="height: 36px; left: 0px; top: 0px; width: 100%;">'
								+ '<div class="api-title" style="position: relative">'
//								+ '<div style="background: url(images/logo.png) no-repeat; position: absolute; left: 0; top: 0; width: 135px; height: 31px;"></div>'
//								+ '<div style="position: absolute; left: 10px; top: 0; width: 135px; height: 31px;"><img src="images/logo.png" height="100%" width="50%"/></div>'
//								+ '<div style="color: #fff; font-family: \'宋体\'; font-size: 16px; font-weight: bold; margin: 9px 0 0 35px;">'
								+ '<span style="color: #fff0ad; font-family: \'宋体\'; font-size: 16px; font-weight: normal; margin: 0 0 0 -90px;">'
								+ '地理信息系统'
								+'后台管理'
								+ '</span>'
								+ '</div>'
								+ '<div style="background: url(images/head_right_bg.jpg) no-repeat right bottom;position: absolute;right:0;top:0;width: 600px; height: 36px;">'
								+ '	<div style="position: absolute; right: 20px; top: 9px;">'
								+ '		<span style="color: #fff000; font-family: \'宋体\'; font-size: 12px;font-weight: bold;"> </span>'
								+ '	<a href="javascript:toggleNotificationDetail()">'
								+ '	<div id="notification_detail" style="background-color:#FFFFA0;font-weight:bold;border:1px solid grey;position:absolute;top:20px;color:red;display:none;z-index:100000;overflow:auto;height:200px;width:400px"></div>'
								+ '	<marquee id="notification_compact" scrollamount="2" style="position:relative;top:5px;border-bottom:1px dotted #fff0ad;color: red; font-family: \'宋体\'; font-size: 12px;width:400px;height: 15px;line-height: 15px;display:none"></marquee>'
								+ '	</a>'
								+ '	<span style="color: #fff0ad; font-family: \'宋体\'; font-size: 14px;">'
								//+ getTopWindow().usr
								+ '</span>'
								+ '	<span>'
								+ '	<a style="color: #cde2ff; font-family: \'宋体\'; font-size: 12px;" href="#" onClick="loginOut();">注销</a> </span>'
								+ '</div>' + '</div>' + '</div>' + '</div>',
						layout : 'fit'
					});
// 				menutree = new Ext.Panel({
//					region: 'west',
//					width: 200,
//					layout: 'accordion',
//					border:false,
//					activeItem: 0,
//					defaults :{
//						xtype: 'treepanel',
//						rootVisible:false,
//						//useArrows: true,
//						autoScroll: true,
//						listeners:{beforeclick:synb,click:sync},
//						collapseFirst: false,
//						loader: new Ext.tree.TreeLoader({
//							preloadChildren: true,
//							clearOnLoad: false,
//						}),
//					},
//					split: true,
//					collapseMode: 'mini',
////					items:menudate
//				})
 				
 				
 				menutree = new Ext.tree.TreePanel({
 		            region:'west',
 		            width: 200,
 		            autoScroll:true,
 		            layout: 'accordion',
 		            split: true,
 		            collapseMode: 'mini',
 		           	activeItem: 0,
 		           	collapseFirst: false,
 		            loader: new Ext.tree.TreeLoader({
 					        url: path+'/user!getTree.action',
// 					        url: '../admin/js/tree.txt',
 					        requestMethod: 'GET'
 				    		}),
 				    listeners:{beforeclick:synb,click:sync},
 				    //构造根节点
 					root: new Ext.tree.AsyncTreeNode({
 					    id: 'root',
 					    text: '菜单',
 						expanded: true
 					})
 				})
		
			maintabpanel = new Ext.TabPanel({
					bodyBorder:false,
					border:false,
					region: 'center',
					defaults:{xtype: 'panel',closable:true},
					tabMargin: 5,
					activeTab: 0,
					items: [
						{
							html:'<iframe width="100%" height="100%" frameborder=0 src="welcome.jsp"></iframe>',
							title: '欢迎使用',
							id:'10000'
						}
					]
				})
  			viewport = new Ext.Viewport({
			layout: 'border',
			border:false,
			width: 987,
			height: 729,
			style: 'padding:5px 5px 5px 5px;',
			defaultType: 'Viewport',
			items: [
				headPanel,
				maintabpanel
				,menutree
			]
	})


	function ajaxReq(srt){
		Ext.Ajax.request( {
			  url : path +"/admin/"+ srt,
			  method : 'post',
			  params : {
			  },
			  success : function(response, options) {
				  
			  },
			  failure : function() {
			  	
			  }
		});
 	}
	function sync(node,e){
		 if(node.isLeaf()){
			e.stopEvent();
			var n = maintabpanel.getComponent(node.id);
			if (!n) {
				ajaxReq(node.attributes.href);
				var n = maintabpanel.add({
					id: node.id,
					title: node.text,
					closable:true,
					html : '<iframe width="100%" height="100%" frameborder=0 src="'+node.attributes.href+'" ></iframe>'
					});
			}
			maintabpanel.setActiveTab(n);
     }
	}

	function synb(node, e){
		return node.isLeaf();
	}
	function urltest(){
		var tab = maintabpanel.add({
							html:'<iframe width="100%" height="100%" frameborder=0 src="http://www.google.com.hk"></iframe>',
							title: a.text
						})
		maintabpanel.activate(tab);
		return 0;
		}
	
	menutree.expandAll();
})
