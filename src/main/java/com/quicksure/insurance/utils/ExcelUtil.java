package com.quicksure.insurance.utils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;

import com.quicksure.insurance.entity.Coverageinfor;
import com.quicksure.insurance.entity.InsuranceDetailsVO;

public class ExcelUtil {
	@SuppressWarnings("deprecation")
	public static void excelUtil(HttpServletResponse response,String[] headers,
			List<InsuranceDetailsVO> InsuranceDetailsVOs) 
			throws IOException{
		//创建HSSFWorkbook对象(excel的文档对象)  
		HSSFWorkbook wb = new HSSFWorkbook();  
		//建立新的sheet对象（excel的表单）  
		HSSFSheet sheet = wb.createSheet("CSRExport"); 
		
		//设置列宽
		sheet.setColumnWidth(0, 20*256);
		sheet.setColumnWidth(1, 25*256);
		sheet.setColumnWidth(2, 24*256);
		sheet.setColumnWidth(3, 24*256);
		sheet.setColumnWidth(4, 24*256);
		sheet.setColumnWidth(5, 24*256);
		sheet.setColumnWidth(6, 10*256);
		sheet.setColumnWidth(7, 10*256);
		sheet.setColumnWidth(8, 10*256);
		sheet.setColumnWidth(9, 10*256);
		sheet.setColumnWidth(10, 10*256);
		sheet.setColumnWidth(11, 20*256);
		sheet.setColumnWidth(12, 10*256);
		sheet.setColumnWidth(13, 10*256);
		sheet.setColumnWidth(14, 10*256);
		sheet.setColumnWidth(15, 10*256);
		sheet.setColumnWidth(16, 10*256);
		sheet.setColumnWidth(17, 10*256);
		sheet.setColumnWidth(18, 10*256);
		sheet.setColumnWidth(19, 10*256);
		sheet.setColumnWidth(20, 10*256);
		sheet.setColumnWidth(21, 10*256);
		sheet.setColumnWidth(22, 10*256);
		sheet.setColumnWidth(23, 10*256);
		sheet.setColumnWidth(24, 10*256);
		sheet.setColumnWidth(25, 10*256);
		sheet.setColumnWidth(26, 20*256);
		sheet.setColumnWidth(27, 20*256);
		//生成一个样式
		HSSFCellStyle style1 = wb.createCellStyle();
		
		//字体设置
		HSSFFont font = wb.createFont();    
		font.setFontName("仿宋_GB2312");    
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示    
		font.setFontHeightInPoints((short) 12);
		
//		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style1.setAlignment(CellStyle.ALIGN_CENTER);//水平居中  
		style1.setVerticalAlignment(CellStyle.VERTICAL_CENTER);//垂直居中
		style1.setFont(font);
		style1.setWrapText(true);
		
		HSSFRow row1=sheet.createRow(0);  
		
		for (short i = 0; i < headers.length; i++) {
	        row1.createCell(i).setCellValue(headers[i]);
	        row1.getCell(i).setCellStyle(style1);
	    }
		for (int i = 0; i < InsuranceDetailsVOs.size(); i++) {
			HSSFRow row=sheet.createRow(i+1);
			String [] deptaddress = new String [3];
			if(InsuranceDetailsVOs.get(i).getBaseinfor().getDeptAddress()!=null){
				String[] str = InsuranceDetailsVOs.get(i).getBaseinfor().getDeptAddress().split(",");
				if(str.length==0){
					deptaddress[0]="";
					deptaddress[1]="";
					deptaddress[2]="";
				}else if(str.length==1){
					deptaddress[0]=str[0];
					deptaddress[1]="";
					deptaddress[2]="";
				}else if(str.length==2){
					deptaddress[0]=str[0];
					deptaddress[1]=str[1];
					deptaddress[2]="";
				}else if(str.length>=3){
					deptaddress[0]=str[0];
					deptaddress[1]=str[1];
					deptaddress[2]=str[2];
				}
			}
			if(InsuranceDetailsVOs.get(i).getBaseinfor().getCreatetime()!=null 
					&&InsuranceDetailsVOs.get(i).getBaseinfor().getCreatetime()!=""){
				row.createCell(0).setCellValue(InsuranceDetailsVOs.get(i).getBaseinfor().getCreatetime().substring(0, 19));
			}
			row.createCell(1).setCellValue(InsuranceDetailsVOs.get(i).getBaseinfor().getOrderno());
			row.createCell(2).setCellValue(InsuranceDetailsVOs.get(i).getBaseinfor().getSyapplicationno());
			row.createCell(3).setCellValue(InsuranceDetailsVOs.get(i).getBaseinfor().getJqapplicationno());
			row.createCell(4).setCellValue(InsuranceDetailsVOs.get(i).getBaseinfor().getSypolicyno());
			row.createCell(5).setCellValue(InsuranceDetailsVOs.get(i).getBaseinfor().getJqpolicyno());
			row.createCell(6).setCellValue(deptaddress[0]==null||deptaddress[0]==""?"":deptaddress[0]);
			row.createCell(7).setCellValue(deptaddress[1]==null||deptaddress[1]==""?"":deptaddress[1]);
			row.createCell(8).setCellValue(deptaddress[2]==null||deptaddress[2]==""?"":deptaddress[2]);
			row.createCell(9).setCellValue(InsuranceDetailsVOs.get(i).getVhlinfor().getDrvowner());
			row.createCell(10).setCellValue(InsuranceDetailsVOs.get(i).getVhlinfor().getLcnno());
			if(InsuranceDetailsVOs.get(i).getBaseinfor().getJqpolicystartdate()!=null){
				row.createCell(11).setCellValue(InsuranceDetailsVOs.get(i).getBaseinfor().getJqpolicystartdate().substring(0, 19)); //起保日期
			}
			if(InsuranceDetailsVOs.get(i).getBaseinfor().getOrderstate()==10 
					||InsuranceDetailsVOs.get(i).getBaseinfor().getOrderstate()==20){
				row.createCell(12).setCellValue("暂存");
			}else if(InsuranceDetailsVOs.get(i).getBaseinfor().getOrderstate()==30
					||InsuranceDetailsVOs.get(i).getBaseinfor().getOrderstate()==40){
				row.createCell(12).setCellValue("待支付");
			}else if(InsuranceDetailsVOs.get(i).getBaseinfor().getOrderstate()==50
					||InsuranceDetailsVOs.get(i).getBaseinfor().getOrderstate()==60
					||InsuranceDetailsVOs.get(i).getBaseinfor().getOrderstate()==70){
				row.createCell(12).setCellValue("已支付");
			}else{
				row.createCell(12).setCellValue("已撤销");
			}
			row.createCell(13).setCellValue(InsuranceDetailsVOs.get(i).getBaseinfor().getSypremium());
			row.createCell(14).setCellValue(InsuranceDetailsVOs.get(i).getBaseinfor().getJqpremium());
			row.createCell(15).setCellValue(InsuranceDetailsVOs.get(i).getBaseinfor().getTaxpremium());
			row.createCell(16).setCellValue(InsuranceDetailsVOs.get(i).getBaseinfor().getTotalPremium());  //总保费
			if(InsuranceDetailsVOs.get(i).getCoverageinfors().size()>0){
				for(Coverageinfor coverage:InsuranceDetailsVOs.get(i).getCoverageinfors()){
					if("030101".equals(coverage.getInsrnccode())){
						row.createCell(17).setCellValue(coverage.getPremium());
					}else if("030102".equals(coverage.getInsrnccode())){
						row.createCell(18).setCellValue(coverage.getPremium());
						
					}else if("030103".equals(coverage.getInsrnccode())){
						row.createCell(19).setCellValue(coverage.getPremium());
						
					}else if("030104".equals(coverage.getInsrnccode())){
						row.createCell(20).setCellValue(coverage.getPremium());
						
					}else if("030105".equals(coverage.getInsrnccode())){
						row.createCell(21).setCellValue(coverage.getPremium());
						
					}else if("030107".equals(coverage.getInsrnccode())){
						row.createCell(22).setCellValue(coverage.getPremium());
						
					}else if("030108".equals(coverage.getInsrnccode())){
						row.createCell(23).setCellValue(coverage.getPremium());
						
					}else if("030119".equals(coverage.getInsrnccode())){
						row.createCell(24).setCellValue(coverage.getPremium());
						
					}
				}
			}
			if(InsuranceDetailsVOs.get(i).getBaseinfor().getPaymentMethod()==10){
				row.createCell(25).setCellValue("线下支付");
			}else if(InsuranceDetailsVOs.get(i).getBaseinfor().getPaymentMethod()==20){
				row.createCell(25).setCellValue("线上支付");
			}else{
				row.createCell(25).setCellValue("未支付");
			}
			if(InsuranceDetailsVOs.get(i).getBaseinfor().getUpdatetime()!=null){
				row.createCell(26).setCellValue(InsuranceDetailsVOs.get(i).getBaseinfor().getUpdatetime().substring(0, 19));
			}
			if(InsuranceDetailsVOs.get(i).getBaseinfor().getAgentCode()!=null){
				row.createCell(27).setCellValue(InsuranceDetailsVOs.get(i).getBaseinfor().getAgentCode());
			}
		}
		
		String fileName = "default.xls";
		try {
			fileName = "LD"+DateFormatUtils.getSystemDateByYYYYMMDD()+".xls";
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		//输出流
		OutputStream os=null;
        try {
			 response.reset();
			 response.setCharacterEncoding("UTF-8");
			 response.setHeader("Content-Disposition", "attachment; filename="+fileName);
//			 response.setContentType("application/vnd.ms-excel;charset=utf-8"); 
			 response.setContentType("application/octet-stream;charset=UTF-8");  //流输出
			 os = new BufferedOutputStream(response.getOutputStream());
			 wb.write(os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		
	}
	
}
