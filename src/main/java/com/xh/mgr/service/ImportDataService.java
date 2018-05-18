package com.xh.mgr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ImportDataService implements InitializingBean{
	@Autowired
	private  JdbcTemplate jdbcTemplate;
	static Logger logger = LoggerFactory.getLogger(ImportDataService.class);
	private int intFlag = 0;
//	public static Map<String,String> areaMap = new HashMap<String, String>();
	/**
	 * import data
	 * @param userName
	 * @return
	 */
	@Transactional
	public synchronized void  importDatass(ArrayList<ArrayList<Object>> list,String title,String month,String description,String operateUserId){
		String msg = null;
		int currentRow = 1;
		if(StringUtils.isBlank(month)){
			throw new RuntimeException("年月为空。");
		}
		try {
			List<Map<String, Object>>  checkList = new ArrayList<Map<String,Object>>();
			
			String ckSql = " select * from analyze_data_importinfo where month = ? ";
			List<Map<String, Object>> ckList = jdbcTemplate.queryForList(ckSql,month);
			if(ckList != null && ckList.size()>0){
				throw new RuntimeException(month+"已经存在数据，请确认是否重复！");
			}
			
			//插入  导入信息
			String importUUID = UUID.randomUUID().toString().replace("-", "");
			String importSql = "insert into analyze_data_importinfo (id,title,description,month,create_date,operate_user_id) VALUES (?,?,?,?,SYSDATE(),?)";
//			jdbcTemplate.update(importSql, importUUID,title,description,month,operateUserId);
			
			ArrayList<Object> objList = new ArrayList<Object>();
			String storeUUID = "";
			String SRUUID = "";
			String jxsUUID = "";
			String DSRUUID = "";
			/*
			 * 要检查是否存在信息,check 人员，商店基础信息
			 */
			String maxSql = "";
			String citySql ="";
			int maxAreaId = 100;
			Map<String, String> areaMap = getAreaMap();
			for(int i=1;i<list.size();i++){
				currentRow++;
				objList = list.get(i);
				System.out.println(i);
				//查找地区id，静态map
				String daquName = (String)objList.get(4);
				if(StringUtils.isNotBlank(daquName)){
					daquName = daquName +"大区";
				}else{
					throw new RuntimeException(" 省份数据有误, 不能为空值！");
				}
				String cityName = (String)objList.get(6);
				String provinceName = (String)objList.get(5);
				if("福建闽南".equals(provinceName) || "福建闽北".equals(provinceName)){
					provinceName = "福建";
				}
				maxSql = "select max(id) as id from base_area";
				List<Map<String, Object>> maxList = jdbcTemplate.queryForList(maxSql);
				int maxid = (Integer) maxList.get(0).get("id");
				if(maxid > maxAreaId){
					maxAreaId = maxid+1;
				}
				String areaId = "";
				if (areaMap.containsKey(cityName)) {
					areaId = areaMap.get(cityName);
				} else {// 不存在该地区时，插入地区数据
					if (areaMap.containsKey(provinceName)) {
						String provinceId = areaMap.get(provinceName);
//						maxSql = "select max(id) as id from base_area";
//						List<Map<String, Object>> maxList = jdbcTemplate.queryForList(maxSql);
//						int maxid = (Integer) maxList.get(0).get("id");
						citySql = " INSERT into base_area(id,parent_id,name,type,import_id)values(?,?,?,3,?) ";
//						int area_id = maxid + 1;
						jdbcTemplate.update(citySql, maxAreaId, provinceId,cityName,importUUID);
						areaId = maxAreaId + "";
						areaMap.put(cityName, areaId);
						maxAreaId++;
					} else {
						throw new RuntimeException(provinceName + "  省份数据有误！");
					}
				}
				
				
//				String areaId = "";
//				if(areaMap.containsKey(cityName)){
//					areaId = areaMap.get(cityName);
//				}else{//不存在该地区时，插入地区数据
//					if(areaMap.containsKey(provinceName)){
//						String provinceId = areaMap.get(provinceName);
//						maxSql = "select max(id) as id from base_area";
//						List<Map<String, Object>> maxList = jdbcTemplate.queryForList(maxSql);
//						int maxid = (Integer) maxList.get(0).get("id");
//						citySql = " INSERT into base_area(id,parent_id,name,type)values(?,?,?,3) ";
//						int area_id = maxid+1;
//						jdbcTemplate.update(citySql,area_id,provinceId,cityName);
//						areaId = area_id+"";
//						areaMap.put(cityName, areaId);
//					}else{
//						throw new RuntimeException(provinceName+"  省份数据有误！");
//					}
//				}
				
//				if(StringUtils.isBlank(areaId)){
//					if(areaMap.containsKey(daquName)){
//						areaId = areaMap.get(daquName);
//					}
//				}
				//插入人员信息，分三次插入，SR，经销商，DSR---start
				//插入SR信息
				String SRName = (String)objList.get(7);
				String userRole = "SR";
				checkList = checkSRIsExist(SRName, areaId);
				if(checkList == null || checkList.size() == 0){
					SRUUID = UUID.randomUUID().toString().replace("-", "");
					String srsql = "insert into base_user(id,parent_id,name,base_user_role,type,area_id,import_id)VALUES(?,?,?,?,'4',?,?)" ;
					jdbcTemplate.update(srsql, SRUUID,areaId,SRName,userRole,areaId,importUUID);
				}else{
					SRUUID = (String)checkList.get(0).get("id");
				}
				
				//插入经销商信息
				String jxsName = (String)objList.get(9);
				String jxsLevel = (String)objList.get(8);
				String jxsNo = (String)objList.get(10);
				userRole = "经销商";
				checkList = new ArrayList<Map<String,Object>>();
				checkList = checkJXSIsExist(jxsName, jxsNo,SRUUID);
				if(checkList == null || checkList.size() == 0){
					jxsUUID = UUID.randomUUID().toString().replace("-", "");
					String jxsSql = "insert into base_user(id,parent_id,name,base_user_role,type,area_id,import_id,jxs_level,jxs_no)VALUES(?,?,?,?,'4',?,?,?,?)";
					jdbcTemplate.update(jxsSql, jxsUUID,SRUUID,jxsName,userRole,areaId,importUUID,jxsLevel,jxsNo);
				}else{
					jxsUUID = (String)checkList.get(0).get("id");
				}
				
				//插入DSR信息
				String DSRName =  (String)objList.get(0);
				userRole = "DSR";
				String DSRNO = (String)objList.get(2);
				String DSRXZNO = (String)objList.get(1);
				checkList = new ArrayList<Map<String,Object>>();
				checkList = checkDSRIsExist(DSRName, DSRXZNO,jxsUUID);
				if(checkList == null || checkList.size() == 0){
					DSRUUID = UUID.randomUUID().toString().replace("-", "");
					String DSRSql = "insert into base_user(id,parent_id,name,base_user_role,type,area_id,import_id,base_user_no,base_user_xz_no)VALUES(?,?,?,?,'4',?,?,?,?)";
					jdbcTemplate.update(DSRSql, DSRUUID,jxsUUID,DSRName,userRole,areaId,importUUID,DSRNO,DSRXZNO);
				}else{
					DSRUUID = (String)checkList.get(0).get("id");
				}
				//插入人员信息，分三次插入，SR，经销商，DSR---end
				
				//插入商店信息
				String stroeName = (String)objList.get(11);//门店全称
				String storeAddr = (String)objList.get(12);//门店地址
				String storeType = (String)objList.get(13);//门店店型
				String storeNo = (String)objList.get(3);//商店编号
				String storeProperty = (String)objList.get(75);//门店性质分类
				String SFANo = (String)objList.get(74);//SFANO
				int storeIsNomal = 0;
				if("异常店".equals(storeProperty)){
					storeIsNomal = 1;
				}
				checkList = new ArrayList<Map<String,Object>>();
				checkList = checkStoreIsExist(SFANo, importUUID, stroeName);
				if(checkList == null || checkList.size() == 0){
					storeUUID = UUID.randomUUID().toString().replace("-", "");
					String storeSql = "insert into base_stores(id,store_name,store_addr,store_type,dsr_id,store_no,jxs_id,sr_id,is_nomal,area_id,import_id,sfa_no)VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
					jdbcTemplate.update(storeSql, storeUUID,stroeName,storeAddr,storeType,DSRUUID,storeNo,jxsUUID,SRUUID,storeIsNomal,areaId,importUUID,SFANo);
				}else{
					storeUUID = (String)checkList.get(0).get("id");
				}
				//插入得分信息，analyze_data_score
				List<Object> params = new ArrayList<Object>();
				String scoreUUID = UUID.randomUUID().toString().replace("-", "");
				StringBuilder scoresql = new StringBuilder();
				scoresql.append( "insert into analyze_data_score(id,import_id,store_id,standard_sku_sum_total,");
				params.add(scoreUUID);params.add(importUUID);params.add(storeUUID);params.add((String)objList.get(14));
				
				scoresql.append(" original_scores,standard_scores,actual_sku_sum_total,actual_mustselect_sku_sum_total, ");
//				System.out.println(Float.parseFloat((String)objList.get(15)));
				params.add(Float.parseFloat((String)objList.get(15)));params.add(Float.parseFloat((String)objList.get(16)));params.add((String)objList.get(17));params.add((String)objList.get(18));
				
				scoresql.append(" miss_mustselect_sku_sum_total,ls_actual_mustselect_sku_sum_total,mjj_actual_mustselect_sku_sum_total,old_product_actual_mustselect_sku_sum_total, ");
				params.add((String)objList.get(19));params.add((String)objList.get(20));params.add((String)objList.get(21));params.add((String)objList.get(22));
				
				scoresql.append(" ja_actual_mustselect_sku_sum_total,newp1_actual_mustselect_sku_sum_total,newp2_actual_mustselect_sku_sum_total,newp3_actual_mustselect_sku_sum_total, ");
				params.add((String)objList.get(23));params.add((String)objList.get(24));params.add((String)objList.get(25));params.add((String)objList.get(26));
				
				scoresql.append(" newp4_actual_mustselect_sku_sum_total,newp5_actual_mustselect_sku_sum_total,ls_sku_count,ls_hualu_sku_count, ");
				params.add((String)objList.get(27));params.add((String)objList.get(28));params.add((String)objList.get(29));params.add((String)objList.get(30));
				
				scoresql.append(" ls_suishen_sku_count,ls_muyu_sku_count,ls_xiangzao_sku_count,ls_jichu_sku_count, ");
				params.add((String)objList.get(31));params.add((String)objList.get(32));params.add((String)objList.get(33));params.add((String)objList.get(34));
				
				scoresql.append(" ls_baobao_sku_count,ls_fen_sku_count,ls_zcjf_sku_count,ls_news1_sku_count, ");
				params.add((String)objList.get(35));params.add((String)objList.get(36));params.add((String)objList.get(37));params.add((String)objList.get(38));
				
				scoresql.append(" ls_news2_sku_count,ls_news3_sku_count,ls_news4_sku_count,ls_news5_sku_count, ");
				params.add((String)objList.get(39));params.add((String)objList.get(40));params.add((String)objList.get(41));params.add((String)objList.get(42));
				
				scoresql.append(" ls_news6_sku_count,ls_news7_sku_count,ls_news8_sku_count,ls_news9_sku_count, ");
				params.add((String)objList.get(43));params.add((String)objList.get(44));params.add((String)objList.get(45));params.add((String)objList.get(46));
				
				scoresql.append(" mjj_sku_count,mjj_trzy_sku_count,mjj_jss_sku_count,mjj_trsx_sku_count, ");
				params.add((String)objList.get(47));params.add((String)objList.get(48));params.add((String)objList.get(49));params.add((String)objList.get(50));
				
				scoresql.append(" mjj_jfyp_sku_count,mjj_qita_sku_count,mjj_ertong_sku_count,mjj_myl_sku_count, ");
				params.add((String)objList.get(51));params.add((String)objList.get(52));params.add((String)objList.get(53));params.add((String)objList.get(54));
				
				scoresql.append(" mjj_fangshai_sku_count,mjj_jiemian_sku_count,mjj_news1_sku_count,mjj_news2_sku_count, ");
				params.add((String)objList.get(55));params.add((String)objList.get(56));params.add((String)objList.get(57));params.add((String)objList.get(58));
				
				scoresql.append(" mjj_news3_sku_count,mjj_news4_sku_count,mjj_news5_sku_count,mjj_news6_sku_count, ");
				params.add((String)objList.get(59));params.add((String)objList.get(60));params.add((String)objList.get(61));params.add((String)objList.get(62));
				
				scoresql.append(" mjj_news7_sku_count,mjj_news8_sku_count,mjj_news9_sku_count,yy_sku_count, ");
				params.add((String)objList.get(63));params.add((String)objList.get(64));params.add((String)objList.get(65));params.add((String)objList.get(66));
				
				scoresql.append(" ys_sku_count,ja_sku_count,newp1_sku_count,newp2_sku_count, ");
				params.add((String)objList.get(67));params.add((String)objList.get(68));params.add((String)objList.get(69));params.add((String)objList.get(70));
				
				scoresql.append(" newp3_sku_count,newp4_sku_count,newp5_sku_count,sfa_no");
				params.add((String)objList.get(71));params.add((String)objList.get(72));params.add((String)objList.get(73));params.add((String)objList.get(74));
				scoresql.append(" ,store_is_nomal ");
				params.add(storeIsNomal);
				scoresql.append(" ,store_type ");
				params.add(storeType);
				scoresql.append(" ,score_step_id )");
				String scoresStep = (String)objList.get(77);
				if("0分".equals(scoresStep)){
					params.add(1);
				}else if("1-59分".equals(scoresStep)){
					params.add(2);
				}else if("60-79分".equals(scoresStep)){
					params.add(3);
				}else if("80-100分".equals(scoresStep)){
					params.add(4);
				}else{
					params.add(1);
				}
				scoresql.append( "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
//				System.out.println(params.toArray());
				jdbcTemplate.update(scoresql.toString(), params.toArray());
				
//*************//插入铺货信息analyze_data_stock
				params = new ArrayList<Object>();
				String stockUUID = UUID.randomUUID().toString().replace("-", "");
				StringBuilder stocksql = new StringBuilder();
				stocksql.append(" insert into analyze_data_stock(id,store_id,import_id,score_id,");
				params.add(stockUUID);params.add(storeUUID);params.add(importUUID);params.add(scoreUUID);
				
				stocksql.append(" ls_hls_isstock,ls_ss_isstock,ls_myl_isstock,ls_xz_isstock,");
				params.add((String)objList.get(78));params.add((String)objList.get(79));params.add((String)objList.get(80));params.add((String)objList.get(81));
				
				stocksql.append(" ls_xsy_isstock,ls_bb_isstock,ls_fen_isstock,ls_zcjf_isstock,");
				params.add((String)objList.get(82));params.add((String)objList.get(83));params.add((String)objList.get(84));params.add((String)objList.get(85));
				
				stocksql.append(" ls_news1_isstock,ls_news2_isstock,ls_news3_isstock,ls_news4_isstock,");
				params.add((String)objList.get(86));params.add((String)objList.get(87));params.add((String)objList.get(88));params.add((String)objList.get(89));
				
				stocksql.append(" ls_news5_isstock,ls_news6_isstock,ls_news7_isstock,ls_news8_isstock,");
				params.add((String)objList.get(90));params.add((String)objList.get(91));params.add((String)objList.get(92));params.add((String)objList.get(93));
				
				stocksql.append(" ls_news9_isstock,mjj_tyzy_isstock,mjj_jss_isstock,mjj_trsx_isstock,");
				params.add((String)objList.get(94));params.add((String)objList.get(95));params.add((String)objList.get(96));params.add((String)objList.get(97));
				
				stocksql.append(" mjj_jfyp_isstock,mjj_qita_isstock,mjj_ertong_isstock,mjj_myl_isstock,");
				params.add((String)objList.get(98));params.add((String)objList.get(99));params.add((String)objList.get(100));params.add((String)objList.get(101));
				
				stocksql.append(" mjj_fangshai_isstock,mjj_jiemian_isstock,mjj_news1_isstock,mjj_news2_isstock,");
				params.add((String)objList.get(102));params.add((String)objList.get(103));params.add((String)objList.get(104));params.add((String)objList.get(105));
				
				stocksql.append(" mjj_news3_isstock,mjj_news4_isstock,mjj_news5_isstock,mjj_news6_isstock,");
				params.add((String)objList.get(106));params.add((String)objList.get(107));params.add((String)objList.get(108));params.add((String)objList.get(109));
				
				stocksql.append(" mjj_news7_isstock,mjj_news8_isstock,mjj_news9_isstock,yy_isstock,");
				params.add((String)objList.get(110));params.add((String)objList.get(111));params.add((String)objList.get(112));params.add((String)objList.get(113));
				
				stocksql.append(" ys_isstock,ja_isstock,ls_isstock,mjj_isstock,");
				params.add((String)objList.get(114));params.add((String)objList.get(115));params.add((String)objList.get(116));params.add((String)objList.get(117));
				
				stocksql.append(" newp1_isstock,newp2_isstock,newp3_isstock,newp4_isstock,newp5_isstock");
				params.add((String)objList.get(118));params.add((String)objList.get(119));params.add((String)objList.get(120));params.add((String)objList.get(121));params.add((String)objList.get(122));
				stocksql.append(" ,store_type ");
				params.add(storeType);
				stocksql.append(" ,store_is_nomal,ja_jdqj,ja_gdxl,ja_mtqj,ja_cjj,ja_xyy,ja_xyz,ja_jdqj01,ja_gdxl01,ja_mtqj01,ja_cjj01,ja_xyy01,ja_xyz01) ");
				params.add(storeIsNomal);
				params.add((String)objList.get(128));params.add((String)objList.get(129));params.add((String)objList.get(130));params.add((String)objList.get(131));
				params.add((String)objList.get(132));params.add((String)objList.get(133));params.add((String)objList.get(134));params.add((String)objList.get(135));
				params.add((String)objList.get(136));params.add((String)objList.get(137));params.add((String)objList.get(138));params.add((String)objList.get(139));
				stocksql.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
				
				jdbcTemplate.update(stocksql.toString(), params.toArray());
//*************//插入达标信息analyze_data_target
				params = new ArrayList<Object>();
				String targetUUID = UUID.randomUUID().toString().replace("-", "");
				StringBuilder targetsql = new StringBuilder();
				targetsql.append(" insert into analyze_data_target(id,store_id,import_id,score_id,store_istarget,ls_istarget,mjj_istarget,old_product_istarget,ja_istarget,area_id,store_is_nomal,store_type)");
				targetsql.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
				params.add(targetUUID);params.add(storeUUID);params.add(importUUID);params.add(scoreUUID);
				params.add("".equals((String)objList.get(123))?null:(String)objList.get(123));
				params.add("".equals((String)objList.get(124))?null:(String)objList.get(124));
				params.add("".equals((String)objList.get(125))?null:(String)objList.get(125));
				params.add("".equals((String)objList.get(126))?null:(String)objList.get(126));
				params.add("".equals((String)objList.get(127))?null:(String)objList.get(127));
//				params.add((String)objList.get(124));
//				params.add((String)objList.get(125));
//				params.add((String)objList.get(126));
//				params.add((String)objList.get(127));
				params.add(areaId);
				params.add(storeIsNomal);
				params.add(storeType);
				jdbcTemplate.update(targetsql.toString(), params.toArray());
			}
			jdbcTemplate.update(importSql, importUUID,title,description,month,operateUserId);
		} catch (Exception e) {
			e.printStackTrace();
			msg = "第"+currentRow+"行数据有误，请检查。"+e.getMessage();
			logger.error(msg, e);
			throw new RuntimeException(msg);
		}
	}
	
	public Map<String,String> getAreaMap(){
		String sql = " select id,name from base_area ";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		Map<String,String> areaMap = new HashMap<String, String>();
		for(int i=0;i<list.size();i++){
			areaMap.put((String)list.get(i).get("name"), list.get(i).get("id")+"");
		}
		return areaMap;
	}
	/**
	 * 检查门店是否存在
	 * @param storeName
	 * @param sfaNo
	 * @return
	 */
	public List<Map<String, Object>> checkStoreIsExist(String sfaNo,String importId,String storeName){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
//		String sql = "select id from base_stores where  1=1  and sfa_no = ? ";
		String sql = " select id from base_stores where  1=1  and sfa_no = ? and import_id = ? and store_name = ? ";
		list = jdbcTemplate.queryForList(sql,sfaNo,importId,storeName);
		return list;
	}
	/**
	 * 检查SR是否存在
	 * @param storeName
	 * @param storeAreaId
	 * @param DsrId
	 * @return
	 */
	public List<Map<String, Object>> checkSRIsExist(String SRName,String areaId){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select id from base_user where name = ? and base_user_role = 'SR' and area_id = ?";
		list = jdbcTemplate.queryForList(sql,SRName,areaId);
		return list;
	}
	/**
	 * 检查经销商是否存在
	 * @param storeName
	 * @param storeAreaId
	 * @param DsrId
	 * @return
	 */
	public List<Map<String, Object>> checkJXSIsExist(String jxsName,String jxsNo,String SRUUID){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select id from base_user where name = ? and base_user_role = '经销商' and jxs_no = ? and parent_id = ? ";
		list = jdbcTemplate.queryForList(sql,jxsName,jxsNo,SRUUID);
		return list;
	}
	/**
	 * 检查DSR是否存在
	 * @param storeName
	 * @param storeAreaId
	 * @param DsrId
	 * @return
	 */
	public List<Map<String, Object>> checkDSRIsExist(String DSRName,String DSRXZNO,String jxsUUID){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select id from base_user where name = ? and base_user_role = 'DSR' and base_user_xz_no = ? and parent_id = ? ";
		list = jdbcTemplate.queryForList(sql,DSRName,DSRXZNO,jxsUUID);
		return list;
	}
	
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
//		if(intFlag == 0){
//			getAreaMap();
//		}
//		intFlag++;
	}

}
