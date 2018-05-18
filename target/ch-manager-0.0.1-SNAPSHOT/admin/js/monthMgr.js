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
			url : qrUrl+"listMonth.action",
			reader : new Ext.data.JsonReader({
				root : 'data',
				id : 'id',
				fields : [
					{name:  'value'},
					{name : 'id'},
				]
			}),
			remoteSort : true
		});
		store.load({params:{start:0,limit:20}});
        
        var column=new Ext.grid.ColumnModel( 
            [ 
            	new Ext.grid.RowNumberer(),
//            	sm,
            	{header:"月份",align:'center',dataIndex:"value",sortable:true}, 
	            {header:"操作",align:'center',dataIndex:"id",
	            renderer: function (value, meta, record) {
										     var deleteBtn = "<input id = 'bt_delete_" + record.get('id')
							+ "' onclick=\"deleteMonth('" + record.get('id')
							+ "');\" type='button' value='删除' width ='15px'/>&nbsp;&nbsp;";
										            			
            				return "<div>" +deleteBtn + "</div>";
        				  } .createDelegate(this)
	            } 
            ] 
        ); 
        
        var tbar = new Ext.Toolbar({  
            renderTo : Ext.grid.GridPanel.tbar,// 其中grid是上边创建的grid容器  
            items :['月份：', {
		  		  id : 'shmonth',
		  		  xtype : 'textfield',
		  		  width : 115,
		  	}, {
				text : '查询',
				iconCls : 'Magnifier',
				handler : function() {
					reloadData();
				}
			}, {
				text : '重置',
				iconCls : 'Reload',
				handler : function() {
					Ext.getCmp('shmonth').setValue("");
				}
			},{
				text : '添加月份',
				iconCls : 'Useradd',
				handler : function() {
					showEditRole();
				}
			}/*,{
				text : '设置权限',
				iconCls : 'Cog',
				handler : function() {
					showEditAuth();
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
		var shmonth = document.getElementById('shmonth').value ;
		store.baseParams['value'] = shmonth;
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
	
	function deleteMonth(id){
		Ext.Msg.confirm('tip', '确认删除?',function (button,text){if(button == 'yes'){
			Ext.Ajax.request({
				  url : path + "/setting!deleteMonth.action",
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
            labelWidth: 100,
            labelAlign: "right",
            bodyStyle:"text-align:left",
            border : false,
            items: [
               {   xtype:"textfield", 
            	   width:180,id: "eMonth",
            	   fieldLabel: "月份（6位数字）",
            	   value:_userName,
            	   regex : /^\d{6}$/, //正则表达式在/...../之间
   				   regexText:"月份只能填写6位数字，如201501", //正则表达式错误提示  
               },
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
    				var month = Ext.getCmp('eMonth').getValue();
    				if(typeof(month) == "undefined" || month  == ""){
    					Ext.Msg.alert('提示', '请填写月份');
    					return;
    				}
    		        var reg = /^\d{6}$/;     
    				if(month.match(reg) == null){  
    					Ext.Msg.alert("error", "月份只能填写6位数字，如201501");
    					return;
    				}
    				Ext.Ajax.request({
    					  url : path + "/setting!addMonth.action",
    					  method : 'post',
    					  params : {
    						  month:month,
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
    		title : '添加月份',
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
    
    
    
