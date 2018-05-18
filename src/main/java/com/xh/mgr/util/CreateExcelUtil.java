package com.xh.mgr.util;


import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CreateExcelUtil {
    
    public static boolean createExcelFile(String excelPath,List<List<Object>> tarList) {
        boolean isCreateSuccess = false;
        Workbook workbook = null;
        try {
            workbook = new XSSFWorkbook();//HSSFWorkbook();//WorkbookFactory.create(inputStream);
        }catch(Exception e) {
            System.out.println("It cause Error on CREATING excel workbook: ");
            e.printStackTrace();
        }
        
        if(workbook != null) {
            Sheet sheet = workbook.createSheet("testdata");
            Row row0 = sheet.createRow(0);
//            for(int i = 0; i < tarList.get(0).size(); i++) {
//            	Cell cell_1 = row0.createCell(i, Cell.CELL_TYPE_STRING);
//            	CellStyle style = getStyle(workbook);
//            	cell_1.setCellStyle(style);
//            	if(i==0){
//            		cell_1.setCellValue("House Bill");
//            	}else if(i==1){
//            		cell_1.setCellValue("Container Number");
//            	}else{
//            		cell_1.setCellValue("date");
//            	}
//            	sheet.autoSizeColumn(i);
//            }
            for (int rowNum = 1; rowNum < tarList.size()+1; rowNum++) {
            	Row row = sheet.createRow(rowNum);
            	List<Object> list = tarList.get(rowNum-1);
            	for(int i = 0; i < tarList.get(0).size(); i++) {
            		Cell cell = row.createCell(i, Cell.CELL_TYPE_STRING);
//            		cell.setCellValue("cell" + String.valueOf(rowNum+1) + String.valueOf(i+1));
            		cell.setCellValue(list.get(i)+"");
            	}
            }
            
            try {
                FileOutputStream outputStream = new FileOutputStream(excelPath);
                workbook.write(outputStream);
                outputStream.flush();
                outputStream.close();
                isCreateSuccess = true;
            } catch (Exception e) {
                System.out.println("It cause Error on WRITTING excel workbook: ");
                e.printStackTrace();
            }
        }
        File sss = new File(excelPath);
        System.out.println(sss.getAbsolutePath());
        return isCreateSuccess;
    }
    private static CellStyle getStyle(Workbook workbook){
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER); 
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        // 设置单元格字体
        Font headerFont = workbook.createFont(); // 字体
        headerFont.setFontHeightInPoints((short)14);
        headerFont.setColor(HSSFColor.RED.index);
        headerFont.setFontName("宋体");
        style.setFont(headerFont);
        style.setWrapText(true);

        // 设置单元格边框及颜色
        style.setBorderBottom((short)1);
        style.setBorderLeft((short)1);
        style.setBorderRight((short)1);
        style.setBorderTop((short)1);
        style.setWrapText(true);
        return style;
    }
}