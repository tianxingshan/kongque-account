package com.kongque.util;


import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPicture;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
/**
 */
public class ExportExcelUtil {
	//格式化时间
		public static SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyyMMddHHmmss");
		/**
		 * 构建导出属性字段可选的Excel数据导出数据模型
		 * @param dataSet 要被导出的数据集
		 * @param excludedFieldes 需要从被导出数据集中剔除的属性字段名集合
		 * @return exportedModelList 将被导出成Excel数据的最终版本数据集，以List&lt;Map&lt;String, Object&gt;&gt;类型返回
		 * @throws IllegalArgumentException
		 * @throws IllegalAccessException
		 * @since 2017-07-26
		 * @author 彭成
		 */
		@SuppressWarnings("unchecked")
		public static <T> List<Map<String, Object>> buildCustomizedExportedModel(Collection<T> dataSet,Set<String> excludedFieldes) throws IllegalArgumentException, IllegalAccessException{
			List<Map<String, Object>> exportedModelList = new ArrayList<>();
			for(T data : dataSet){
				Field[] fields = data.getClass().getDeclaredFields();
				ListOrderedMap exportedModel = new ListOrderedMap();
				for(Field field : fields){
					if(excludedFieldes != null && !excludedFieldes.contains(field.getName())){
						field.setAccessible(true);
						switch(field.getName()){//[2017-07-31]
							case "count":
							case "num":
							if(field.get(data) != null && field.get(data).toString().length() > 0){
								exportedModel.put("detailCount", field.get(data));
							}break;
							case "voucherType":
								if(field.get(data) != null){
									switch((int)field.get(data)){//[2017-08-01]
										case 1:exportedModel.put("voucherType", "微调单");break;
										case 2:exportedModel.put("voucherType", "订单");break;
										default:exportedModel.put("voucherType", "");
									}									
								}break;
							case "styleName"://[2017-08-01]
							case "orderStyleName"://[2017-08-01]
							if(field.get(data) != null && field.get(data).toString().length() > 0){//[2017-08-01]
								exportedModel.put("detailStyleName", field.get(data));
							}break;
							default:exportedModel.put(field.getName(), field.get(data));
						}						
					}
				}
				exportedModelList.add(exportedModel);
			}
			return exportedModelList;
		}
		/**
		 * 
		 * @param dataset 数据
		 * @param out 流
		 * @throws IOException 
		 */
		public static <T> void exportExcel(Collection<T> dataset, OutputStream out) throws IOException {
	        exportExcel(0,0,0,"sheet1", null, dataset, out, "yyyy-MM-dd");
	    }

		/**
		 * 
		 * @param headers 标题
		 * @param dataset 数据
		 * @param out
		 * @throws IOException 
		 */
	    public static <T> void exportExcel(String[] headers, Collection<T> dataset,OutputStream out) throws IOException {
	        exportExcel(0,0,0,"sheet1", headers, dataset, out, "yyyy-MM-dd");
	    }

	    /**
	     * 
	     * @param headers 标题
	     * @param dataset 数据
	     * @param out 
	     * @param pattern 日期格式
	     * @throws IOException 
	     */
	    public static <T> void exportExcel(String[] headers, Collection<T> dataset,OutputStream out, String pattern) throws IOException {
	        exportExcel(0,0,0,"sheet1", headers, dataset, out, pattern);
	    }
	    
	    /**
	     * 导出数据
	     * @param colSplit 需要冻结的列数目，如果没有传0
	     * @param rowSplit 需要冻结的行数目，如果没有传0
	     * @param dataBeginIndex 数据从第几个字段开始导出，每一条的记录，如果没有传0
	     * @param sheetName 表格名字，sheet1的名称
	     * @param headers 标题
	     * @param dataset 数据
	     * @param out 输出流
	     * @throws IOException 
	     */
	    public static <T> void exportExcel(int colSplit,int rowSplit,int dataBeginIndex,String sheetName,String[] headers, Collection<T> dataset,OutputStream out) throws IOException{
	    	exportExcel(colSplit,rowSplit,dataBeginIndex,sheetName, headers, dataset, out, null);
	    }

	    /**
	     * 这是一个通用的方法，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
	     * @param <T>
	     * 
	     * @param title
	     *            表格标题名
	     * @param headers
	     *            表格属性列名数组
	     * @param dataset
	     *            1.需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。此方法支持的 javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
	     *            2.或者Map,key是string类型，value属性
	     * @param out
	     *            与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
	     * @param pattern
	     *            如果有时间数据，设定输出格式。默认为"yyy-MM-dd"，目前不需要
	     * @throws IOException 
	     */
	    @SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	    public static <T> void exportExcel(int colSplit,int rowSplit,int dataBeginIndex,String title, String[] headers,Collection<T> dataset, OutputStream out, String pattern) throws IOException {
	    	// 声明一个工作薄
	        HSSFWorkbook workbook = new HSSFWorkbook();
	        String sheetName = (title == null || title.equals("")) ? "sheet1" : title;
	        // 生成一个表格
	        HSSFSheet sheet = workbook.createSheet(sheetName);
	        
	    	try{
	    		if(colSplit != 0 || rowSplit != 0){
	    			sheet.createFreezePane( colSplit, rowSplit, colSplit, rowSplit );//冻结首行(0,1,0,1)；2,1
	    		}
	            // 设置表格默认列宽度为15个字节
	            sheet.setDefaultColumnWidth(15);
	            sheet.setDefaultRowHeightInPoints(15);
	            // 生成一个样式
	            HSSFCellStyle style = workbook.createCellStyle();
	            // 设置这些样式
	            style.setFillForegroundColor(HSSFColor.WHITE.index);
	            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	            style.setBorderBottom(BorderStyle.THIN);
	            style.setBorderLeft(BorderStyle.THIN);
	            style.setBorderRight(BorderStyle.THIN);
	            style.setBorderTop(BorderStyle.THIN);
	            style.setAlignment(HorizontalAlignment.CENTER);
	            // 生成一个字体
	            HSSFFont font = workbook.createFont();
	            font.setColor(HSSFColor.BLACK.index);
	            font.setFontHeightInPoints((short) 12);
	            font.setBold(true);
	            // 把字体应用到当前的样式
	            style.setFont(font);
	            style.setWrapText(true);//设置自动换行
	            
	            // 生成并设置另一个样式
	            HSSFCellStyle style2 = workbook.createCellStyle();
	            style2.setFillForegroundColor(HSSFColor.WHITE.index);
	            style2.setFillPattern(FillPatternType.SOLID_FOREGROUND );
	            style2.setBorderBottom(BorderStyle.THIN);
	            style2.setBorderLeft(BorderStyle.THIN);
	            style2.setBorderRight(BorderStyle.THIN);
	            style2.setBorderTop(BorderStyle.THIN);
	            style2.setAlignment(HorizontalAlignment.CENTER);
	            style2.setVerticalAlignment(VerticalAlignment.CENTER );
	            // 生成另一个字体
	            HSSFFont font2 = workbook.createFont();
	            font2.setColor(HSSFColor.BLUE.index);
	            font2.setBold(true);
	            // 把字体应用到当前的样式
	            style2.setFont(font2);
	            style2.setWrapText(true);//设置自动换行

	            // 声明一个画图的顶级管理器
	            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
	            // 定义注释的大小和位置,详见文档  
	            //HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
	            // 设置注释内容  
	            //comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
	            // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容. 
	            //comment.setAuthor("leno");

	            // 产生表格标题行
	            HSSFRow row = sheet.createRow(0);
	            if(headers==null){
	            	 Field[] fields = dataset.iterator().next().getClass().getDeclaredFields();
	            	 for(int i=0;i<fields.length;i++){
	            		HSSFCell cell = row.createCell(i);
	 	                cell.setCellStyle(style);
	 	                HSSFRichTextString text = new HSSFRichTextString(fields[i].getName());
	 	                cell.setCellValue(text);
	            	 }
	            }else
		            for (int i = 0; i < headers.length; i++) {
		                HSSFCell cell = row.createCell(i);
		                cell.setCellStyle(style);
		                HSSFRichTextString text = new HSSFRichTextString(headers[i]);
		                cell.setCellValue(text);
		            }

	            // 遍历集合数据，产生数据行
	            Iterator<T> it = dataset.iterator();
	            int index = 0;
	            while (it.hasNext()) {
	                index++;
	                row = sheet.createRow(index);
	                T t = it.next();
	                if(t instanceof Class){
	                	// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
	                    Field[] fields = t.getClass().getDeclaredFields();
	        			for (int i = 0; i < fields.length; i++) {
	        				HSSFCell cell = row.createCell(i);
	        				cell.setCellStyle(style2);
	        				Field field = fields[i];
	        				String fieldName = field.getName();
	        				String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	        				Class tCls = t.getClass();
	        				Method getMethod = tCls.getMethod(getMethodName);
	        				Object value = getMethod.invoke(t);
	        				// 判断值的类型后进行强制类型转换
	        				String textValue = null;
	        				if (value instanceof byte[]) {
	        					// 有图片时，设置行高为60px;
	        					row.setHeightInPoints(600);
	        					// 设置图片所在列宽度为80px,注意这里单位的一个换算
	        					sheet.setColumnWidth(i, (int)35.7 * 400);
	        					// sheet.autoSizeColumn(i);
	        					byte[] bsValue = (byte[]) value;
	        					HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 600, (short)i, index, (short)(i+1), index+1);
	        					anchor.setAnchorType(2);
	        					HSSFPicture pic = patriarch.createPicture(anchor, workbook.addPicture(bsValue, Workbook.PICTURE_TYPE_JPEG));
	        					pic.resize();//这句话一定不要，这是用图片原始大小来显示
	        				} else if (value instanceof Date) {
	        					/**
	        					 * modified by fengfeng.yu
	        					 * CommonsMethod.getNowCorrect2SecondWithEnglishWord()
	        					 * 该方法的注释是得到当前时间，而不是传入的时间，应该的判定应该是得到传入Excel的时间
	        					 * textValue = CommonsMethod.getNowCorrect2SecondWithEnglishWord();
	        					 */
	        					textValue = pattern == null ? new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(value) : new SimpleDateFormat(pattern).format(value);
	        				} else {
	        					// 其它数据类型都当作字符串简单处理
	        					textValue = value.toString();
	        				}
	        				
	        				if (textValue != null){
	        					HSSFRichTextString richString = new HSSFRichTextString(textValue);
	             				HSSFFont font3 = workbook.createFont();
	             				font3.setColor(HSSFColor.BLACK.index);
	             				richString.applyFont(font3);
	             				cell.setCellValue(richString);
	        				}
	        			}
	                }else if(t instanceof ListOrderedMap || t instanceof HashMap){
	                	//HashMap<String,String> map = null;
	                	ListOrderedMap map = (ListOrderedMap) t;
	                	Iterator<String> keys = map.keySet().iterator();
	                	int i = 0;
	                	while(keys.hasNext()){
	                		HSSFCell cell = row.createCell(i);
	        				cell.setCellStyle(style2);
	                		String keyname = keys.next();
	                		//此处根据keyname生成标题
	                		Object keyValue = map.get(keyname);
	                		String textValue = null;
	                		if(keyValue instanceof byte[]){
	                			// 有图片时，设置行高为60px;
	        					row.setHeightInPoints(300);
	        					// 设置图片所在列宽度为80px,注意这里单位的一个换算
	        					sheet.setColumnWidth(i, (int) (35.7 * 200));
	        					// sheet.autoSizeColumn(i);
	        					byte[] bsValue = (byte[]) keyValue;
	        					HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short)i, index, (short) (i+1), index+1);
	        					anchor.setAnchorType(2);
	        					@SuppressWarnings("unused")
								HSSFPicture pic = patriarch.createPicture(anchor, workbook.addPicture(bsValue, Workbook.PICTURE_TYPE_JPEG));
	        					//pic.resize();//这句话一定不要，这是用图片原始大小来显示
	                		} else if (keyValue instanceof Date) {
	                       		String formatted = sdf.format(keyValue);
	        					textValue = formatted.substring(0, 4) + "-" + formatted.substring(4, 6) + "-"
	        							+ formatted.substring(6, 8) + "  " + formatted.substring(8, 10) + ":"
	        							+ formatted.substring(10, 12) + ":" + formatted.substring(12, 14) + " ";
	        				} else{
	                			textValue = (keyValue==null)?"":keyValue.toString();
	                		}
	                		if (textValue != null){
	       					 	HSSFRichTextString richString = new HSSFRichTextString(textValue);
	            				//HSSFFont font3 = workbook.createFont();
	            				//font3.setColor(HSSFColor.BLUE.index);
	            				richString.applyFont(font2);
	            				cell.setCellValue(richString);
	                			cell.setCellValue(textValue);
	       				 	}
	                		i++;
	                	}//end while(keys.hasNext())
	                }else if(t instanceof Object[]){
	                	Object[] object = (Object[])t;
	                	//忽略前两个id字段，之后可以作为参数传入
	                	for(int len = dataBeginIndex;len<object.length;len++){                        
	                		String textValue = "";
	                		if(object[len] != null){
	                			textValue = object[len].toString();
	                		}
	                		HSSFCell cell = row.createCell(len-dataBeginIndex);
	        				cell.setCellStyle(style2);
	                		HSSFRichTextString richString = new HSSFRichTextString(textValue);
	        				richString.applyFont(font2);
	        				cell.setCellValue(richString);
	                	}
	                }else if(t instanceof Object){
	                	// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
	                    Field[] fields = t.getClass().getDeclaredFields();
	        			for (int i = 0; i < fields.length; i++) {
	        				HSSFCell cell = row.createCell(i);
	        				cell.setCellStyle(style2);
	        				Field field = fields[i];
	        				field.setAccessible(true);
	        				Object value = field.get(t);
	        				// 判断值的类型后进行强制类型转换
	        				String textValue = null;
	        				if (value == null) {
	        					textValue = " ";
							}else if (value instanceof byte[]) {
	        					// 有图片时，设置行高为60px;
	        					row.setHeightInPoints(600);
	        					// 设置图片所在列宽度为80px,注意这里单位的一个换算
	        					sheet.setColumnWidth(i, (int)35.7 * 400);
	        					// sheet.autoSizeColumn(i);
	        					byte[] bsValue = (byte[]) value;
	        					HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 600, (short)i, index, (short)(i+1), index+1);
	        					anchor.setAnchorType(2);
	        					HSSFPicture pic = patriarch.createPicture(anchor, workbook.addPicture(bsValue, Workbook.PICTURE_TYPE_JPEG));
	        					pic.resize();//这句话一定不要，这是用图片原始大小来显示
	        				} else if (value instanceof Date) {
	        					/**
	        					 * modified by fengfeng.yu
	        					 * CommonsMethod.getNowCorrect2SecondWithEnglishWord()
	        					 * 该方法的注释是得到当前时间，而不是传入的时间，应该的判定应该是得到传入Excel的时间
	        					 * textValue = CommonsMethod.getNowCorrect2SecondWithEnglishWord();
	        					 */
	        					textValue = pattern == null ? new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(value) : new SimpleDateFormat(pattern).format(value);
	        				} else {
	        					// 其它数据类型都当作字符串简单处理
	        					//modified by fengfeng.yu 20161115, 如果value为null的话，空字符串无法进行处理
	        					textValue = value.toString();
	        				}
	        				
	        				if (textValue != null){
	        					HSSFRichTextString richString = new HSSFRichTextString(textValue);
	             				HSSFFont font3 = workbook.createFont();
	             				font3.setColor(HSSFColor.BLACK.index);
	             				richString.applyFont(font3);
	             				cell.setCellValue(richString);
	        				}
	        			}
	                }
	                //increase row height to accomodate two lines of text
	                row.setHeightInPoints((2*sheet.getDefaultRowHeightInPoints()));        
	            }
	            
	            workbook.write(out);
	        }catch (Exception e) {
	        	e.printStackTrace();
	        	//log.error("IOException 异常！", e);
	        } finally {
	        	if(workbook != null)
	        		workbook.close();
	        }
	    }
}
