    Ext.QuickTips.init();
    var _cob;
   	Ext.onReady(function(){
   		Ext.override(Ext.menu.DateMenu, {  
   		    render : function() {  
   		        Ext.menu.DateMenu.superclass.render.call(this);  
   		        if (Ext.isGecko || Ext.isSafari || Ext.isChrome) {  
   		            this.picker.el.dom.childNodes[0].style.width = '178px';  
   		            this.picker.el.dom.style.width = '178px';  
   		        }  
   		    }  
   		}); 
   		
   		var qrUrl = path + "/setting!";
   		var order;
//   		var sm = new Ext.grid.CheckboxSelectionModel();
        store = new Ext.data.Store({
			url : qrUrl+"listManageUser.action",
			reader : new Ext.data.JsonReader({
				root : 'data',
				id : 'querydate',
				fields : [
					{name : 'id'},
					{name : 'name'},
					{name : 'login_name'},
					{name : 'login_pwd'},
					{name : 'quanguo'},
					{name : 'daqu'},
					{name : 'sheng'},
					{name : 'city'}
				]
			}),
			remoteSort : true
		});
		store.load({params:{start:0,limit:20}});
        
        var column=new Ext.grid.ColumnModel( 
            [ 
            	new Ext.grid.RowNumberer(),
//            	sm,
            	{header:"用户名称",align:'center',dataIndex:"name",sortable:true}, 
            	{header:"登陆名称",align:'center',dataIndex:"login_name",sortable:true}, 
            	{header:"全国",align:'center',dataIndex:"quanguo",sortable:true}, 
            	{header:"大区",align:'center',dataIndex:"daqu",sortable:true}, 
            	{header:"省份",align:'center',dataIndex:"sheng",sortable:true}, 
            	{header:"城市",align:'center',dataIndex:"city",sortable:true}, 
	            {header:"操作",align:'center',dataIndex:"id",
	            renderer: function (value, meta, record) {
//	            	showLiandong(_managerUserId,_managerName,_managerUserName,_managerUserPwd,_quanguo,_daqu,_sheng,_city)
	            	
					            			var formatStr = "<input id = 'bt_edit_" + record.get('id')
							+ "' onclick=\"showLiandong('" + record.get('id') + "','"+ record.get('name') + "','"
							+ record.get('login_name') + "','"
							+ record.get('login_pwd') + "','"
							+ record.get('quanguo') + "','"
							+ record.get('daqu') + "','"
							+ record.get('sheng') + "','"
							+ record.get('city')
							+ "');\" type='button' value='编辑' width ='15px'/>&nbsp;&nbsp;"; 

										    var deleteBtn = "<input id = 'bt_delete_" + record.get('id')
							+ "' onclick=\"deleteManagerUser('" + record.get('id')
							+ "');\" type='button' value='删除' width ='15px'/>&nbsp;&nbsp;";
										            			
            				var resultStr = String.format(formatStr);
            				return "<div>"+deleteBtn+resultStr+"</div>";
        				  } .createDelegate(this)
	            } 
            ] 
        ); 
        
        var tbar = new Ext.Toolbar({  
            renderTo : Ext.grid.GridPanel.tbar,// 其中grid是上边创建的grid容器  
            items :['用户名称：', {
		  		  id : 'seName',
		  		  xtype : 'textfield',
		  		  width : 115,
		  	},'&nbsp;&nbsp&nbsp&nbsp登陆名称：', {
		  		  id : 'seLoginName',
		  		  xtype : 'textfield',
		  		  width : 115,
		  	},  {
				text : '查询',
				iconCls : 'Magnifier',
				handler : function() {
					reloadData();
				}
			}, {
				text : '重置',
				iconCls : 'Reload',
				handler : function() {
					Ext.getCmp('seName').setValue("");
					Ext.getCmp('seLoginName').setValue("");
				}
			},{
				text : '添加新用户',
				iconCls : 'Useradd',
				handler : function() {
//					showEditRole();
					showLiandong();
				}
			}/*,{
				text : '设置区域权限',
				iconCls : 'Cog',
				handler : function() {
//					showEditAuth();
					showLiandong();
				}
			}*/]

        });  
        var grid = new Ext.grid.EditorGridPanel({ 
			region:'center',
			border:false,
//			autoHeight:true,
			viewConfig: {
	            forceFit: true, //让grid的列自动填满grid的整个宽度，不用一列一列的设定宽度。
	            emptyText: '系统中还没有任务'
	        },
            cm:column, 
            store:store, 
//            sm:sm,
            autoExpandColumn:0, 
            loadMask:true, 
            frame:true, 
            autoScroll:true, 
            tbar:tbar,
            bbar:new Ext.PagingToolbar({
					store : store,
					displayInfo : true,
					pageSize : 20,
					prependButtons : true,
					beforePageText : '第',
					afterPageText : '页，共{0}页',
					displayMsg : '第{0}到{1}条记录，共{2}条',
					emptyMsg : "没有记录"
				}),
        });
        
	
      
  	var mainPanel = new Ext.Panel({
  		region:"center",
		layout:'border',
		border:false,
		items:[grid],
	});
	
   var viewport=new Ext.Viewport({
       //enableTabScroll:true,
       layout:"border",
       items:[
           mainPanel
   	   ]
   });
   });
   
   function reloadData(){
		var seName = document.getElementById('seName').value ;
		var seLoginName = document.getElementById('seLoginName').value ;
		store.baseParams['name'] = seName;
		store.baseParams['userName'] = seLoginName;
		store.reload({
			params: {start:0,limit:20},
			callback: function(records, options, success){
//				console.log(records);
			},
			scope: store
		});
	}
	
	
	function saveInfo(oldName,newName,_id){
		//console.log(_id);
		if(oldName != newName){
			Ext.Msg.confirm('保存数据', '确认?',function (button,text){if(button == 'yes'){
				Ext.Ajax.request( {
					  url : path + "/deviceqr!updateNickName.action",
					  method : 'post',
					  params : {
					   newName : newName,
					   did : _id
					  },
					  success : function(response, options) {
					   var o = Ext.util.JSON.decode(response.responseText);
					   //alert(o.i_type);
					   if(o.i_type && "success"== o.i_type){
					   	
					   }else{
					   	   Ext.Msg.alert('提示', '保存失败'); 
					   }
					  },
					  failure : function() {
					  	
					  }
		 		});
			}});
		}
	}
	
	function deleteManagerUser(id){
		Ext.Msg.confirm('tip', '确认删除用户?',function (button,text){if(button == 'yes'){
			Ext.Ajax.request({
				  url : path + "/setting!deleteManagerUser.action",
				  method : 'post',
				  params : {
					  id:id
				  },
				  success : function(response, options) {
				   var o = Ext.util.JSON.decode(response.responseText);
				   if(o.i_type && "success"== o.i_type){
					   reloadData();
				   }else{
				   	   Ext.Msg.alert('提示', o.i_msg); 
				   }
				  },
				  failure : function() {
					  Ext.Msg.alert('提示', '删除失败'); 
				  }
	 		});
		}});
		
	}
    function showEditRole(_roleId,_userName,_userRole){
    	var isHidden = true;
    	if(typeof(_roleId) == "undefined" || _roleId  == ""){
    		isHidden = false;
    	}
    	var _fileForm =  new Ext.form.FormPanel({
            frame: true,
            autoHeight: true,
            labelWidth: 80,
            labelAlign: "right",
            bodyStyle:"text-align:left",
            border : false,
            items: [
               {xtype:"textfield", width:180,id: "eRoleName", fieldLabel: "角色名",value:_userName},
            ],
         });
    	
    	var _importPanel = new Ext.Panel({
    		layout : "fit",
    		layoutConfig : {
    			animate : true
    		},
    		items : [_fileForm],
    		buttons : [{
    			id : "btn_import_wordclass",
    			text : "保存",
    			handler : function() {
    				var name = Ext.getCmp('eRoleName').getValue();
    				if(typeof(name) == "undefined" || name  == ""){
    					Ext.Msg.alert('提示', '请填写角色名');
    					return;
    				}
    				Ext.Ajax.request({
    					  url : path + "/user!editRole.action",
    					  method : 'post',
    					  params : {
    						  roleId:_roleId,
    						  roleName:name,
    					  },
    					  success : function(response, options) {
    					   var o = Ext.util.JSON.decode(response.responseText);
    					   if(o.i_type && "success"== o.i_type){
    						   Ext.Msg.alert("success",'保存成功！',function(){  
   								newWin.close();
   								reloadData();
    						   });
    					   }else{
    					   	   Ext.Msg.alert('提示', o.i_msg); 
    					   }
    					  },
    					  failure : function() {
    						  Ext.Msg.alert('提示', '操作失败'); 
    					  }
    		 		});
    				
    			}
    		}]
    	});
    	
    	newWin = new Ext.Window({
    		width : 520,
    		height:110,
    		title : '角色编辑',
    		defaults : {// 表示该窗口中所有子元素的特性
    			border : false
    			// 表示所有子元素都不要边框
    		},
    		plain : true,// 方角 默认
    		modal : true,
    		shim : true,
    		collapsible : true,// 折叠
    		closable : true, // 关闭
    		closeAction: 'close',
    		resizable : false,// 改变大小
    		draggable : true,// 拖动
    		minimizable : false,// 最小化
    		maximizable : false,// 最大化
    		animCollapse : true,
    		constrainHeader : true,
    		autoHeight : false,
    		items : [_importPanel]
    	});
		newWin.show();
    }
    
    
    function showEditAuth(roleId){
    	  var authTree = new Ext.tree.TreePanel({
    	        animate : true,
    	        border:false,
    			title:"勾选角色可以操作的菜单",
    			collapsible:true,
    			frame:true,
    			enableDD:true,
    			enableDrag:true,
    			rootVisible:true,
    			autoScroll:true,
    			autoHeight:true,
    			width:150,
    			lines:true,
    			loader: new Ext.tree.TreeLoader({
    				url: path+'/user!getAuthTree.action?roleId='+roleId,
//    			        url: '../admin/js/tree.txt',
    				requestMethod: 'GET'
    			}),
    			root: new Ext.tree.AsyncTreeNode({
    				    id: 'root',
    				    text: '数据管理',
    					expanded: true
    				})
    	 });
    	//判断是否有子结点被选中
    	var childHasChecked = function(node){
			var childNodes = node.childNodes;
			if(childNodes || childNodes.length>0){
		    	for(var i=0;i<childNodes.length;i++){
		    		if(childNodes[i].getUI().checkbox.checked)
		    		return true;
		    	}
			}
			return false;
		}
    	// 级联选中父节点
    	var parentCheck = function(node ,checked){
	    	var checkbox = node.getUI().checkbox;
	    	if(typeof checkbox == 'undefined')
	    	return false;
	    	if(!(checked ^ checkbox.checked))
	    	return false;
	    	if(!checked && childHasChecked(node))
	    	return false;
	    	checkbox.checked = checked;
	    	node.attributes.checked = checked;
	    	node.getUI().checkbox.indeterminate = checked; // 半选中状态
	    	node.getOwnerTree().fireEvent('check', node, checked);
	    	var parentNode = node.parentNode;
	    	if( parentNode !== null){
	    		parentCheck(parentNode,checked);
	    	}
    	}
		authTree.on('checkchange', function(node, checked) {
			node.expand();
			node.attributes.checked = checked;
			var parentNode = node.parentNode;
			if(parentNode !== null){
				parentCheck(parentNode,checked);
			}
			node.eachChild(function(child) {
			    child.ui.toggleCheck(checked);
			    child.attributes.checked = checked;
			    child.fireEvent('checkchange', child, checked);
			});
		 }, authTree);
	     authTree.expandAll();
    	 var _importPanel = new Ext.Panel({
      		layout : "fit",
      		layoutConfig : {
      			animate : true
      		},
      		border:false,
      		items : [authTree],
      		buttons : [{
      			text : "保存",
      			handler : function() {
      				var parm = "";
      				var checkNode=new Array()
      				checkNode = authTree.getChecked();
      				if(checkNode != null){
      					for(var i=0;i<checkNode.length ;i++){
      						parm = parm + checkNode[i].id+','
      					}
      				}else{
      					Ext.Msg.alert('提示', "请勾选菜单！");
      					return;
      				}
      				Ext.Ajax.request({
  					  url : path + "/user!updateUserAuth.action",
  					  method : 'post',
  					  params : {
  						  roleId:roleId,
  						  ids:parm,
  					  },
  					  success : function(response, options) {
  					   var o = Ext.util.JSON.decode(response.responseText);
  					   if(o.i_type && "success"== o.i_type){
  						   Ext.Msg.alert("success",'保存成功！',function(){  
  							    authWin.close();
  						   });
  					   }else{
  					   	   Ext.Msg.alert('提示', o.i_msg); 
  					   }
  					  },
  					  failure : function() {
  						  Ext.Msg.alert('提示', '操作失败'); 
  					  }
      				});
      			}
      		}]
      	});
    	
    	var authWin = new Ext.Window({
    		width : 300,
//    		height:110,
    		autoHeight : true,
    		title : '权限编辑',
    		defaults : {// 表示该窗口中所有子元素的特性
//    			border : false
    			// 表示所有子元素都不要边框
    		},
    		plain : true,// 方角 默认
    		modal : true,
    		shim : true,
    		collapsible : true,// 折叠
    		closable : true, // 关闭
    		closeAction: 'close',
    		resizable : false,// 改变大小
    		draggable : true,// 拖动
    		minimizable : false,// 最小化
    		maximizable : false,// 最大化
    		animCollapse : true,
    		constrainHeader : true,
    		items : [_importPanel]
    	});
    	authWin.show();
    }
    
    

function showLiandong(_managerUserId,_managerName,_managerUserName,_managerUserPwd,_quanguo,_daqu,_sheng,_city) {
//	console.log(_managerUserPwd);
	var coquanguo;
	var genus_store = new Ext.data.SimpleStore({
		fields : [ 'value', 'name' ],
		data : [['1','中国']],
	});
	coquanguo = new Ext.form.ComboBox({
		fieldLabel : '全国',
		name : 'quanguo',
		mode : 'local',
		readOnly : false,
		triggerAction : 'all',
//		emptyText : '省/市',
		store : genus_store,
		valueField : 'value',
		displayField : 'name',
	});
	coquanguo.setValue('中国');
	
	
	
	var princeStore = new Ext.data.Store({
		proxy: new Ext.data.HttpProxy({
			url: path + "/setting!"+"getChildNode.action" //这里是参数可以顺便写,这个数据源是在第一个下拉框select的时候load的
		}),
		reader: new Ext.data.JsonReader({
			root : 'data',
			fields:['value','name']
		})
	});
	var cityStore = new Ext.data.Store({
		proxy: new Ext.data.HttpProxy({
			url: path + "/setting!"+"getChildNode.action" //这里是参数可以顺便写,这个数据源是在第一个下拉框select的时候load的
		}),
		reader: new Ext.data.JsonReader({
			root : 'data',
			fields:['value','name']
		})
	});
	
	var daquStore = new Ext.data.Store({
		proxy: new Ext.data.HttpProxy({
			url: path + "/setting!"+"getChildNode.action?id=1" //这里是参数可以顺便写,这个数据源是在第一个下拉框select的时候load的
		}),
		reader: new Ext.data.JsonReader({
			root : 'data',
			fields:['value','name']
		})/*,
		listeners : {   
            load : function() {   
            	codaqu.setValue(codaqu.getValue());   
            }   
        }  */
	});
	
	var codaqu = new Ext.form.ComboBox({
		fieldLabel : '大区',
		name : 'daqu',
//		mode : 'local',
		readOnly : false,
		triggerAction : 'all',
//		emptyText : '省/市',
		store : daquStore,
		valueField : 'value',
		displayField : 'name',
		listeners : {
			'select' : function() {
				console.log(this.getValue());
				Ext.getCmp('province_id').setValue("");
				princeStore.removeAll();
				Ext.getCmp('city_id').setValue("");
				cityStore.removeAll();
				princeStore.baseParams['id'] = this.getValue();
				princeStore.reload();
				cosheng.store.load();
				cosheng.setValue('全部');
				cocity.setValue('全部');
			}
		}
	});
	daquStore.load();
	codaqu.setValue('全部');
	var cosheng = new Ext.form.ComboBox({
		fieldLabel : '省份',
		name : 'province',
		id : 'province_id',
//		mode : 'local',
		readOnly : false,
		triggerAction : 'all',
//		emptyText : '市/镇/县',
		store : princeStore,
		hiddenName : 'genus',
		valueField : 'value',
		displayField : 'name',
		listeners : {
			'select' : function() {
				Ext.getCmp('city_id').setValue("");
				cityStore.removeAll();
				cityStore.baseParams['id'] = this.getValue();
				cityStore.reload();
				cocity.store.load();
				cocity.setValue('全部');
			}
		}
	
	});
	cosheng.setValue('全部');
	var cocity = new Ext.form.ComboBox({
		fieldLabel : '城市',
		name : 'city_id',
		id : 'city_id',
//		mode : 'local',
		readOnly : false,
		triggerAction : 'all',
//		emptyText : '市/镇/县',
		store : cityStore,
		hiddenName : 'genus',
		valueField : 'value',
		displayField : 'name'
			
	});
	cocity.setValue('全部');
//	_managerUserId,_managerName,_managerUserName,_managerUserPwd,_quanguo,_daqu,_sheng,_city
	if(!_managerUserId){//是undifind
		
	}else{//非undifind
		codaqu.setValue(_daqu);
		cosheng.setValue(_sheng);
		cocity.setValue(_city);
	}
	if(!_managerName){
		_managerName = "";
	}
	if(!_managerUserName){
		_managerUserName = "";
	}
	if(!_managerUserPwd){
		_managerUserPwd = "";
	}
	
	
	var form_condition = new Ext.FormPanel({
		region : 'west',
		split : false,
		labelWidth : 80,

		frame : true,
		width : 275,
		defaults : {
			width : 150
		},
		labelAlign : 'right',
		defaultType : 'textfield',
		autoScroll : true,
		items : [ {  
		            xtype:'hidden',  
		            fieldLabel: 'id',  
		            name: 'id',  
		            id:'_managerUser_id',
		            value: _managerUserId 
			      },
               {
       			xtype : 'fieldset',
       			title : '用户信息',
       			autoHeight : true,
       			autoWidth : true,
       			//hidden:isHid,
       			items : [{xtype:"textfield", width:180,id: "eName",name: "eUserName", fieldLabel: "用户名称",value:_managerName},
       			      {xtype:"textfield", width:180,id: "eUserName",name: "enewpwd", fieldLabel: "登录名",value:_managerUserName},
       			      {xtype:"textfield", width:180,id: "enewpwd",name: "enewpwd", fieldLabel: "登陆密码",value:_managerUserPwd}
       			         ]
       		}, {
       			xtype : 'fieldset',
       			title : '设置权限',
       			autoHeight : true,
       			autoWidth : true,
       			items : [coquanguo,codaqu,cosheng,cocity]
       		}
               
               ],
               buttons : [{
       			id : "btn_save_userinfo",
       			text : "保存",
       			handler : function() {
       				var id = Ext.getCmp('_managerUser_id').getValue();
       				var name = Ext.getCmp('eName').getValue();
       				var username = Ext.getCmp('eUserName').getValue();
       				var pwd = Ext.getCmp('enewpwd').getValue();
       				var daqu = codaqu.getValue();
       				var sheng = cosheng.getValue();
       				var city = cocity.getValue();
       				if("全部" == daqu){
       					daqu = '2';
       				}
       				if("全部" == sheng){
       					sheng = '2';
       				}
       				if("全部" == city){
       					city = '2';
       				}
       				if(typeof(name) == "undefined" || name  == ""){
       					Ext.Msg.alert('提示', '请填写名称');
       					return;
       				}
       				if(typeof(username) == "undefined" || username  == ""){
       					Ext.Msg.alert('提示', '请填写用户名');
       					return;
       				}
       				if(typeof(pwd) == "undefined" || pwd  == ""){
       					Ext.Msg.alert('提示', '请填写用户密码');
       					return;
       				}
       				Ext.Ajax.request({
       					  url : path + "/setting!editManageUser.action",
       					  method : 'post',
       					  params : {
       						  userId:id,
       						  name:name,
       						  userName:username,
       						  userPwd:pwd,
       						  daqu:daqu,
       						  sheng:sheng,
       						  city:city
       					  },
       					  success : function(response, options) {
       					   var o = Ext.util.JSON.decode(response.responseText);
       					   if(o.i_type && "success"== o.i_type){
       						   Ext.Msg.alert("success",'保存成功！',function(){  
       							    win.close();
      								reloadData();
       						   });
       					   }else{
       					   	   Ext.Msg.alert('提示', o.i_msg); 
       					   }
       					  },
       					  failure : function() {
       						  Ext.Msg.alert('提示', '操作失败'); 
       					  }
       		 		});
       			}
       		}]
	});

	var win = new Ext.Window({
		title : '编辑',
		width : 400,
//		autoHeight : true,
		height : 400,
		layout : "fit",
		plain : true,
		closable : true, // 关闭
		closeAction: 'close',
		modal : true,
		items : [ form_condition ]
	});
	win.show();
}