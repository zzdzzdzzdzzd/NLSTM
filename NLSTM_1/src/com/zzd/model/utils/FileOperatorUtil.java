package com.zzd.model.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class FileOperatorUtil {
	/**
	 * read excel file (.xls)
	 * @param path : file path
	 * @param rowIndex : index of row
	 * @param columnIndex : index of column
	 * @param sheetIndex : index of sheet
	 * @return file contents (Matrix)
	 */
	@SuppressWarnings("deprecation")
	private static Matrix readTableForXLS(String path, int[] rowIndex,
			int[] columnIndex,int sheetIndex) {
		Matrix result = new Matrix(rowIndex.length,columnIndex.length);
		try {
			InputStream ips = new FileInputStream(path);
			@SuppressWarnings("resource")
			HSSFWorkbook wb = new HSSFWorkbook(ips);
			HSSFSheet sheet = wb.getSheetAt(sheetIndex);

			for (int i = 0; i < rowIndex.length; i++) {
				for (int j = 0; j < columnIndex.length; j++) {
					HSSFCell cell = sheet.getRow(rowIndex[i] - 1).getCell(columnIndex[j] - 1);

					switch (cell.getCellType()) {
					case HSSFCell.CELL_TYPE_BOOLEAN:
						if(cell.getBooleanCellValue()){
							result.set(i, j, 1.0 );
						}else{
							result.set(i, j, 0.0 );
						}						
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if (HSSFDateUtil.isCellDateFormatted(cell)) {

						} else {
							result.set(i,j, cell.getNumericCellValue());
						}
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						break;
					default:
						break;
					}
				}
			}
			ips.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * read excel file (.xls)
	 * @param path : file path
	 * @param rowIndex : index of row
	 * @param columnIndex : index of column
	 * @param sheetName : name of sheet
	 * @return : file contents (Matrix)
	 */
	@SuppressWarnings({ "resource", "deprecation" })
	private static Matrix readTableForXLS(String path, int[] rowIndex,
			int[] columnIndex,String sheetName) {
		Matrix result = new Matrix(rowIndex.length,columnIndex.length);
		try {
			InputStream ips = new FileInputStream(path);
			HSSFWorkbook wb = new HSSFWorkbook(ips);
			HSSFSheet sheet = wb.getSheet(sheetName);

			for (int i = 0; i < rowIndex.length; i++) {
				for (int j = 0; j < columnIndex.length; j++) {
					HSSFCell cell = sheet.getRow(rowIndex[i] - 1).getCell(columnIndex[j] - 1);
					switch (cell.getCellType()) {
					// 读取boolean类型
					case HSSFCell.CELL_TYPE_BOOLEAN:
						if(cell.getBooleanCellValue()){
							result.set(i, j,1.0 );
						}else{
							result.set(i, j,0.0 );
						}						
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						if (HSSFDateUtil.isCellDateFormatted(cell)) {

						} else {
							result.set(i,j, cell.getNumericCellValue());
						}
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						break;
					default:
						break;
					}
				}
			}
			ips.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * read excel file (.xlsx)
	 * @param path : file path
	 * @param rowIndex : index of row
	 * @param columnIndex : index of column
	 * @param sheetName : name of sheet
	 * @return : file contents (Matrix)
	 */
	@SuppressWarnings({ "resource", "deprecation" })
	private static Matrix readTableForXLSX(String path, int[] rowIndex,
			int[] columnIndex,String sheetName) {
		Matrix result = new Matrix(rowIndex.length,columnIndex.length);
		try {
			InputStream ips = new FileInputStream(path);
			XSSFWorkbook wb = new XSSFWorkbook(ips);
			XSSFSheet sheet = wb.getSheet(sheetName);
			
			for(int i=0;i<rowIndex.length;i++){
				for(int j=0;j<columnIndex.length;j++){
					XSSFCell cell=sheet.getRow(rowIndex[i] - 1).getCell(columnIndex[j] - 1);
					switch (cell.getCellType()) {
					case XSSFCell.CELL_TYPE_BOOLEAN:
						break;
					case XSSFCell.CELL_TYPE_NUMERIC:
						if (HSSFDateUtil.isCellDateFormatted(cell)) {

						} else {
							result.set(i,j, cell.getNumericCellValue());
						}
						break;
					case XSSFCell.CELL_TYPE_FORMULA:
						break;
					default:
						break;
					}					
				}
			}
			ips.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * read excel file (.xlsx)
	 * @param path : file path
	 * @param rowIndex : index of row
	 * @param columnIndex : index of column
	 * @param sheetIndex : index of sheet
	 * @return file contents (Matrix)
	 */
	@SuppressWarnings({ "resource", "deprecation" })
	private static Matrix readTableForXLSX(String path, int[] rowIndex,
			int[] columnIndex,int sheetIndex) {
		Matrix result = new Matrix(rowIndex.length,columnIndex.length);
		try {
			InputStream ips = new FileInputStream(path);
			XSSFWorkbook wb = new XSSFWorkbook(ips);
			XSSFSheet sheet = wb.getSheetAt(sheetIndex);
			
			for(int i=0;i<rowIndex.length;i++){
				for(int j=0;j<columnIndex.length;j++){
					XSSFCell cell=sheet.getRow(rowIndex[i] - 1).getCell(columnIndex[j] - 1);
					switch (cell.getCellType()) {
					case XSSFCell.CELL_TYPE_BOOLEAN:
						break;
					case XSSFCell.CELL_TYPE_NUMERIC:
						if (HSSFDateUtil.isCellDateFormatted(cell)) {

						} else {
							result.set(i,j, cell.getNumericCellValue());
						}
						break;
					case XSSFCell.CELL_TYPE_FORMULA:
						break;
					default:
						break;
					}					
				}
			}
			ips.close();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	/**
	 * read excel file (.xls  .xlsx)
	 * @param path : file path
	 * @param rowIndex : index of row
	 * @param columnIndex : index of column
	 * @param sheetIndex : index of sheet
	 * @return file contents (Matrix)
	 */
	public static Matrix readExcel(String path, int[] rowIndex,
			int[] columnIndex,int sheetIndex){
		if(path.endsWith(".xls")){
			return FileOperatorUtil.readTableForXLS(path, rowIndex, columnIndex, sheetIndex);
		}else if(path.endsWith(".xlsx")){
			return FileOperatorUtil.readTableForXLSX(path, rowIndex, columnIndex, sheetIndex);
		}else{
			System.out.println("Wrong file type!");
		}
		return null;
	}
	/**
	 * read excel file (.xls  .xlsx)
	 * @param path : file path
	 * @param rowIndex : index of row
	 * @param columnIndex : index of column
	 * @param sheetName : name of sheet
	 * @return : file contents (Matrix)
	 */
	public static Matrix readExcel(String path, int[] rowIndex,
			int[] columnIndex,String sheetName){
		if(path.endsWith(".xls")){
			return FileOperatorUtil.readTableForXLS(path, rowIndex, columnIndex, sheetName);
		}else if(path.endsWith(".xlsx")){
			return FileOperatorUtil.readTableForXLSX(path, rowIndex, columnIndex, sheetName);
		}else{
			System.out.println("Wrong file type!");
		}
		return null;
	}
	
	/**
	 * export metrics
	 * @param losses : metrics between predictions and observations
	 * @param excelPath : file path
	 * @return
	 */
	@SuppressWarnings({ "deprecation", "resource" })
	public static boolean exportMetrics(double[][][] losses, String excelPath) {

		File file = new File(excelPath);
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}

		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		boolean succ = true;

		HSSFWorkbook wb = new HSSFWorkbook();
		
		int caseNum=losses.length;

		String[] tableHead=new String[]{"", "MAE", "MSE", "RMSE", "MAPE", "R2"};
		String[] rowHead = new String[]{"model 5", "model 8", "model 1", "model 2", "model 6"};
		
		for(int i=0;i<caseNum;i++){
			int time=losses[i].length;
			HSSFSheet sheet = wb.createSheet(i+"");
			sheet.setDefaultColumnWidth((short) 15);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFRow row = sheet.createRow(0);
			
			for(int j=0;j<tableHead.length;j++){
				row.createCell(j).setCellValue(tableHead[j]);
				row.getCell(j).setCellStyle(style);
			}
			
			for (int k = 0; k < time; k++) {
				row = sheet.createRow(k + 1);
				for (int j = 0; j <tableHead.length; j++) {
					if(j==0){
						row.createCell(j).setCellValue(rowHead[k]);
						row.getCell(j).setCellStyle(style);						
					}else{
						double value=losses[i][k][j-1];
						if(Double.isInfinite(value)||Double.isNaN(value)){
							value=99999;
						}
						row.createCell(j).setCellValue(value);
						row.getCell(j).setCellStyle(style);
					}

				}
			}
			
		}

		FileOutputStream out;
		try {
			out = new FileOutputStream(excelPath);
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			succ = false;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			succ = false;
			e.printStackTrace();
		}

		return succ;
	}
	

	/**
	 * export predictions
	 * @param content : predictions
	 * @param excelPath : file path
	 * @param sheetIndex :  index of sheet
	 * @return
	 */
	@SuppressWarnings({ "resource", "deprecation" })
	public static boolean exportPredictions(double[][][] contents, String excelPath) {

		File file = new File(excelPath);
		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}

		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		boolean succ = true;
		HSSFWorkbook wb = new HSSFWorkbook();
		
		for(int t=0;t<contents.length;t++){
			String[] tableHead=new String[]{"Normalized predictions","Normalized observations","predictions","observations"};
			
			double[][] content=contents[t];
			HSSFSheet sheet = wb.createSheet((t+1)+"");
			
			sheet.setDefaultColumnWidth((short) 15);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFRow row = sheet.createRow(0);
			
			for(int j=0;j<content[0].length;j++){
				row.createCell(j).setCellValue(tableHead[j]);
				row.getCell(j).setCellStyle(style);
			}
			
			for(int i=0;i<content.length;i++){
				row = sheet.createRow(i + 1);
				for(int j=0;j<content[0].length;j++){
					if(!(i>=5&&j==9)){
						row.createCell(j).setCellValue(content[i][j]);
						row.getCell(j).setCellStyle(style);
					}

				}
			}
		}
		FileOutputStream out;
		try {
			out = new FileOutputStream(excelPath);
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			succ = false;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			succ = false;
			e.printStackTrace();
		}

		return succ;
	}

	

}
