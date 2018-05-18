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
   		var _pageSize = 20;
   		var reader = new Ext.data.JsonReader({
   			idProperty : 'id',
   			root : 'data',
   			fields : [ {
   				name : 'id',
   				type : 'string'
   			}, {
   				name : 'name',
   				type : 'string'
   			}, {
   				name : 'value',
   				type : 'string'
   			}, {
   				name : 'update_date',
   				type : 'string'
   			}, {
   				name : 'description',
   				type : 'string'
   			}]
   		});
        store = new Ext.data.Store({
			url : qrUrl+"getSettingInfo.action",
			reader : reader,
			remoteSort : true
		});
		store.load({params:{start:0,limit:20}});
		
		var pagingBar = new Ext.PagingToolbar({
			store : store,
			displayInfo : true,
			pageSize : _pageSize,
			beforePageText : '第',
			afterPageText : '页，共{0}页',
			displayMsg : '第{0}到{1}条记录，共{2}条',
			emptyMsg : "没有记录"
		});
		
		
        var column=new Ext.grid.ColumnModel( 
            [ 
            	new Ext.grid.RowNumberer(),
            	{header:"属性",align:'center',dataIndex:"name",sortable:true}, 
	            {header:"值",align:'center',dataIndex:"value",sortable:true},
	            {header:"操作",align:'center',dataIndex:"id",width:50,
	            renderer: function (value, meta, record) {
					            			var formatStr = "<input id = 'bt_edit_" + record.get('id')
							+ "' onclick=\"showEditUser('" + record.get('id') + "','"
							+ record.get('name') + "','"
							+ record.get('value')
							+ "');\" type='button' value='编辑' width ='15px'/>&nbsp;&nbsp;"; 

										     var deleteBtn = "<input id = 'bt_delete_" + record.get('id')
							+ "' onclick=\"deleteSettings('" + record.get('id')
							+ "');\" type='button' value='删除' width ='15px'/>";
										            			
            				var resultStr = String.format(formatStr);
            				return "<div>" + resultStr + "</div>";
        				  } .createDelegate(this)
	            } 
            ] 
        ); 
		//用户角色
		var moduleStore = new Ext.data.Store({
	        proxy: new Ext.data.HttpProxy({
	            url: path + "/user!getRole.action?all="+1 //这里是参数可以顺便写,这个数据源是在第一个下拉框select的时候load的
	        }),
	        reader: new Ext.data.JsonReader({
        	root : 'products',
	        fields:['value','text']
	        })
	    });
		
		_cob = new Ext.form.ComboBox({
			id:'shUserRole',
			width:150,
			forceSelection: true,
			store:moduleStore,
			valueField:'value',
			displayField:'text',
			typeAhead: true,
			triggerAction: 'all',
			selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录
			allowBlank:false,
			editable:false
		});
		_cob.setValue("全部");
    	//------------------------
    	
    	
        var tbar = new Ext.Toolbar({  
            renderTo : Ext.grid.GridPanel.tbar,// 其中grid是上边创建的grid容器  
            items :['用户名：', {
		  		  id : 'shUserName',
		  		  xtype : 'textfield',
		  		  width : 115,
		  	},'用户角色：', _cob, {
				text : '查询',
				handler : function() {
					reloadData();
				}
			}, {
				text : '重置',
				handler : function() {
					Ext.getCmp('shUserName').setValue("");
					Ext.getCmp('shUserRole').setValue("全部");
				}
			},{
				text : '添加新用户',
				handler : function() {
					showEditUser();
				}
			}]

        });  
        var grid = new Ext.grid.EditorGridPanel({ 
			region:'center',
			border:false,
//			autoHeight:true,
//			viewConfig: {
//	            forceFit: true, //让grid的列自动填满grid的整个宽度，不用一列一列的设定宽度。
//	            emptyText: '系统中还没有任务'
//	        },
            cm:column, 
            store:store, 
            autoExpandColumn:0, 
            loadMask:true, 
            frame:true, 
            autoScroll:true, 
//            tbar:tbar,
            bbar:pagingBar,
    		loadMask : true,
    		viewConfig : {
    			forceFit : true
    		}
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
//		var userName = document.getElementById('shUserName').value ;
//		var shUserRole = _cob.getValue();
//		store.baseParams['userName'] = userName;
//		store.baseParams['userRole'] = shUserRole;
		store.reload({
			params: {start:0,limit:20},
			callback: function(records, options, success){
//				console.log(records);
			},
			scope: store
		});
	}
	
	
	
	function deleteSettings(id){
		Ext.Msg.confirm('删除数据', '确认?',function (button,text){if(button == 'yes'){
			Ext.Ajax.request({
				url : path + "/setting!deleteSettingInfoById.action",
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
    function showEditUser(_Id,_Name,_Value){
    	var isHidden = true;
    	var pwdval = "******";
    	if(typeof(_userId) == "undefined" || _Id  == ""){
    		isHidden = false;
    		pwdval ="";
    	}
    	//用户角色
		var _moduleStore = new Ext.data.Store({
	        proxy: new Ext.data.HttpProxy({
	            url: path + "/user!getRole.action?all="+0 //这里是参数可以顺便写,这个数据源是在第一个下拉框select的时候load的
	        }),
	        reader: new Ext.data.JsonReader({
        	root : 'products',
	        fields:['value','text']
	        })
	    });
		_moduleStore.load(); 
//		var co = new Ext.form.ComboBox({
//			id:'couserRole',
//			hiddenName:"couserRole", //提交到后台的input的name   
//			width:180,
//			forceSelection: true,
//			store:_moduleStore,
//			mode: 'local',
//			valueField:'value',
//			displayField:'text',
//			typeAhead: true,
//			triggerAction: 'all',
//			selectOnFocus:true,//用户不能自己输入,只能选择列表中有的记录
//			allowBlank:false,
//			editable:false
//		});
//		_moduleStore.on('load', function() { //数据加载完成后设置下拉框值  
//	        if (_userRole)  
//	        	co.setValue(_userRole);  
//	    }); 
    	var _fileForm =  new Ext.form.FormPanel({
            frame: true,
            autoHeight: true,
            labelWidth: 80,
            labelAlign: "right",
            bodyStyle:"text-align:left",
            border : false,
            items: [
                {
					xtype : "textfield",
					width : 180,
					id : "eName",
					name : "eName",
					fieldLabel : "属性",
					value : _Name
					,readOnly: true
				},
               {xtype:"textfield", width:180,id: "eValue",name: "eValue", fieldLabel: "值",value:_Value}
//               co
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
    				var name = Ext.getCmp('eName').getValue();
    				var value = Ext.getCmp('eValue').getValue();
    				if(typeof(name) == "undefined" || name  == ""){
    					Ext.Msg.alert('提示', '请填写属性');
    					return;
    				}
    				if(typeof(value) == "undefined" || value  == ""){
    					Ext.Msg.alert('提示', '请填写属性值');
    					return;
    				}
    				Ext.Ajax.request({
    					  url : path + "/setting!updateSettingInfo.action",
    					  method : 'post',
    					  params : {
    						  uId:_Id,
    						  uName:name,
    						  uValue:value
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
    						  Ext.Msg.alert('提示', '删除失败'); 
    					  }
    		 		});
    			}
    		}]
    	});
    	
    	newWin = new Ext.Window({
    		width : 520,
    		height:150,
    		title : '用户编辑',
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
    
