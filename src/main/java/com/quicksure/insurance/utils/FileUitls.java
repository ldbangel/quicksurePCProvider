package com.quicksure.insurance.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
public class FileUitls {

	
	/**
	 * 文件大小单位为Mb
	 */
	public static final String UNIT_KEY_M="M";
	/**
	 * 文件大小单位为Kb
	 */
	public static final String UNIT_KEY_K="K";

	/**
	 * 读取文件<br>
	 * 创建时间：Mar 12, 2013 5:43:25 PM </pre>
	 * @param 参数类型 参数名 说明
	 * @return String 说明
	 * @throws 异常类型 说明  
	 * @param fileName
	 * @return
	 * @throws Exception 
	 */
	public static String readFile(String fileName) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("");
		BufferedReader r = null;
		try {
			String line = null;
			r = new BufferedReader(new FileReader(fileName));
			while ((line = r.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			if (r != null) {
				try {
					r.close();
				} catch (IOException e) {
				}
			}
		}
		return sb.toString();
	}
	
	/**
	 * 返回指定文件夹下所有子文件,默认只输出当前文件下子文件，不需要输出指定文件夹的子文件夹中文件<br>
	 * <pre>
	 * 创建时间：Sep 3, 2013 2:58:39 PM 
	 * </pre>
	 * @param folderName
	 * @param outFile
	 * @return
	 */
	public static List<File> listFiles(String folderName,String outFile){
		boolean searchSubfolders = false;//默认只输出当前文件夹下所有子文件
		return listFiles(new File(folderName),outFile,searchSubfolders);
	}
	
	/**
	 * 返回指定文件夹下所有子文件<br>
	 * <pre>
	 * 创建时间：Sep 3, 2013 2:45:14 PM 
	 * </pre>
	 * @param fileName 文件夹路径
	 * @param outFile 过滤指定后缀的文件
	 * @param searchSubfolders 是否需要输当前文件夹下子文件夹中的文件
	 * @return
	 */
	public static List<File> listFiles(String fileName,String outFile,boolean searchSubfolders){
		return listFiles(new File(fileName),outFile,searchSubfolders);
	}
	
	/**
	 * 返回指定文件夹下所有子文件 <br>
	 * <pre>
	 * 创建时间：Sep 3, 2013 2:46:33 PM 
	 * </pre>
	 * @param file 文件夹对像
	 * @param outFile 过滤指定后缀的文件
	 * @param searchSubfolders 是否需要输当前文件夹下子文件夹中的文件
	 * @return
	 */
	public static List<File> listFiles(File file,String outFile,boolean searchSubfolders){
		List<File> list = null;
		File[] files = file.listFiles();
		if(files!=null && files.length>0){
			list = new ArrayList<File>();
			for (File file2 : files) {
				if(file2.isFile()){
					if("".equalsIgnoreCase(trim(outFile)) || (!"".equalsIgnoreCase(trim(outFile)) && file2.getName().indexOf(outFile)<0)){
						list.add(file2);
					}
				}else{
					if(searchSubfolders){
						list.addAll(listFiles(file2,outFile,searchSubfolders));
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * 取得子文件个数据<br>
	 * <pre>
	 * 创建时间：Sep 18, 2013 11:54:11 AM 
	 * </pre>
	 * @param fileLength 被分割文件大小，单位为byte
	 * @param count 子文件大小
	 * @param UnitKey 子文件大小单位 提供两种选项：M(Mb)或K(Kb)
	 * @return
	 */
	private static int getSubFileTotal(long fileLength,int count,String UnitKey){
		Long total = 0L;
		if(UNIT_KEY_M.equalsIgnoreCase(trim(UnitKey))){
			if(fileLength%(count*1024*1024)>0){
				total = fileLength/(count*1024*1024)+1;
			}else{
				total = fileLength/(count*1024*1024);
			}
		}else if(UNIT_KEY_K.equalsIgnoreCase(trim(UnitKey))){
			if(fileLength%(count*1024)>0){
				total = fileLength/(count*1024)+1;
			}else{
				total = fileLength/(count*1024);
			}
		}
		return total.intValue();
	}
	
	/**
	 * 以指定子文件大小，将文件分割为n个子文件
	 * 方法splitFile的简要说明 <br>
	 * <pre>
	 * 创建时间：Sep 18, 2013 11:52:23 AM 
	 * </pre>
	 * @param path 子文件存放目录，可为空
	 * @param fileName 被分割文件
	 * @param count  子文件大小
	 * @param UnitKey 子文件大小单位
	 * @throws Exception
	 */
	public static void splitFile(String path,String fileName,int count,String UnitKey) throws Exception{
		if("".equalsIgnoreCase(trim(fileName))){
			throw new Exception("文件名为空！");
		}
		File file = new File(fileName);
		//如果未指定子文件目录，则默认子文件目录与父文件目录相同
		if("".equalsIgnoreCase(trim(path))){
			path = file.getParent();
		}
		if("".equalsIgnoreCase(trim(UnitKey))){
			UnitKey = UNIT_KEY_M;
		}
		//创建文件操作对像，以只读的方式
		RandomAccessFile raf = new RandomAccessFile(file,"r");  
	    long length = raf.length();//文件长度 单位byte 
	    int total = getSubFileTotal(length, count, UnitKey);
	    raf.close();
	    splitFile(path, fileName, total);
	}
	
	/**
	 * 将指定文件分割为count个小文件，然后名存到path目录下<br>
	 * <pre>
	 * 创建时间：Aug 26, 2013 2:26:27 PM 
	 * </pre>
	 * @param path 分割后的小文件存放路经，默认为D:\temp
	 * @param fileName
	 * @param count
	 * @throws Exception
	 */
	public static void splitFile(String path,String fileName,int count) throws Exception{
		if("".equalsIgnoreCase(trim(fileName))){
			throw new Exception("文件名为空！");
		}
		File file = new File(fileName);
		//如果未指定子文件目录，则默认子文件目录与父文件目录相同
		if("".equalsIgnoreCase(trim(path))){
			path = file.getParent();
		}
		splitFile(path, file, count);
	}
	
	/**
	 * 将指定文件分割为count个小文件，然后名存到path目录下 <br>
	 * <pre>
	 * 创建时间：Aug 26, 2013 2:24:29 PM 
	 * </pre>
	 * @param path
	 * @param file
	 * @param count
	 * @throws Exception
	 */
	public static void splitFile(String path,File file,int count) throws Exception{
		if(file==null || !file.exists()){
	    	throw new Exception("文件不存在！");
	    }
		//创建文件操作对像，以只读的方式
		RandomAccessFile raf = new RandomAccessFile(file,"r");  
	    long length = raf.length();//文件长度  
	    long theadMaxSize =  length / count; //每份的大小 1024 * 1000L;  
	    raf.close();  
	    long offset = 0L;  
	    String sonFileName = "";
	    for(int i=0;i< count-1;i++){ //这里不去处理最后一份  
	        long fbegin = offset;  
	        long fend = (i+1) * theadMaxSize;  
	        sonFileName = path+"\\"+file.getName()+"_"+i+".tmp";
	        offset =write(file,sonFileName,fbegin,fend);  //写文件
	    }  
	    //处理最后一份
	    if(length- offset>0) {
	    	sonFileName = path+"\\"+file.getName()+"_"+(count-1)+".tmp";
	    	write(file,sonFileName,offset,length);  
	    }
	}
	
	/**  
	 * <p>拆分文件,拆分完成后，删除源文件</p>  
	 * @param fileName 源文件 
	 * @param count 拆分的文件个数  
	 * @throws Exception  
	 */ 
	public static void splitFile(File file ,int count) throws Exception {  
		if(file==null || !file.isFile()){
			throw new Exception("文件为空或不为文件！");
		}
	    splitFile(file.getParent(), file, count);
	} 
	
	/**  
	 * <p>拆分文件,拆分完成后，删除源文件</p>  
	 * @param fileName 源文件 
	 * @param count 拆分的文件个数  
	 * @throws Exception  
	 */ 
	public static void splitFile(String fileName ,int count) throws Exception {  
		if("".equalsIgnoreCase(trim(fileName))){
			throw new Exception("文件名为空！");
		}
		File file = new File(fileName);
	    splitFile(file.getParent(), file, count);
	} 
	
    /**  
     * <p>指定每份文件的范围写入不同文件</p>  
     * @param fileName 源文件  
     * @param sonFileName 拆分出的文件名 
     * @param begin 开始指针位置  
     * @param end 结束指针位置  
     * @return  
     * @throws Exception  
     */ 
    public static long write(String fileName,String sonFileName,long begin,long end) throws Exception {  
    	return write(new File(fileName), sonFileName, begin, end);
    }

    /**  
     * <p>指定每份文件的范围写入不同文件</p>  
     * @param fileName 源文件  
     * @param sonFileName 拆分出的文件名 
     * @param begin 开始指针位置  
     * @param end 结束指针位置  
     * @return  
     * @throws Exception  
     */ 
    public static long write(File file,String sonFileName,long begin,long end) throws Exception {  
        RandomAccessFile in = new RandomAccessFile(file,"r"); //以只读的方式打开文件
        RandomAccessFile out = new RandomAccessFile(new File(sonFileName),"rw");//以读写的方式打开文件，如果该文件尚不存在，则尝试创建该文件
        byte[] b = new byte[1024];  
        int n=0;  
        in.seek(begin);//从文件指定位置读取  
        while(in.getFilePointer() <= end && (n= in.read(b))!=-1)  {  
            out.write(b, 0, n);  
        }  
        //返回此文件中的当前偏移量
        long endPointer =in.getFilePointer();  
        in.close();  
        out.close();  
        return endPointer;  
    }
    
    /**
     * 将字符串追加到文件尾部，如果文件不存，则创建文件<br>
     * <pre>
     * 创建时间：Sep 3, 2013 3:08:22 PM 
     * </pre>
     * @param str
     * @param fileName
     * @throws Exception
     */
    public static void writeStringToFile(String str,String fileName)throws Exception {
    	writeStringToFile(str, new File(fileName));
    }
    
    /**
     * 将字符串追加到文件尾部，如果文件不存，则创建文件 <br>
     * <pre>
     * 创建时间：Sep 3, 2013 3:11:59 PM 
     * </pre>
     * @param b
     * @param fileName
     * @throws Exception
     */
    public static void writeStringToFile(byte[] b,String fileName) throws Exception{
    	writeStringToFile(b, new File(fileName));
    }
    
    /**
     * 将字符串追加到文件尾部，如果文件不存，则创建文件 <br>
     * <pre>
     * 创建时间：Sep 3, 2013 3:09:20 PM 
     * </pre>
     * @param str
     * @param file
     * @throws Exception
     */
    public static void writeStringToFile(String str,File file)throws Exception {
    	writeStringToFile(str.getBytes(), file);
    }
    
    /**
     * 向文件尾部写入内容，如果文件不存在，则创建文件 <br>
     * <pre>
     * 创建时间：Sep 3, 2013 3:03:13 PM 
     * </pre>
     * @param b
     * @param file
     * @throws Exception
     */
    public static void writeStringToFile(byte[] b,File file) throws Exception{
    	if(!file.exists() || !file.isFile()){
    		throw new Exception("文件不存在或不是文件！");
    	}
    	RandomAccessFile out = new RandomAccessFile(file,"rw");//以读写的方式打开文件，如果该文件尚不存在，则尝试创建该文件
    	out.write(b);
    	out.close();
    }
    public static String trim(String str){
		return null==str?"":str.trim();
	}
    public static String trim(Object obj){
		return null==obj?"":obj.toString().trim();
	}
    /**
     * copy file
     */
    public static void copyFile(File oldName,File newName){
    	try{
    		FileInputStream fin = new FileInputStream(oldName); 
            FileOutputStream fout = new FileOutputStream(newName); 
            int bytesRead; 
            byte[] buf = new byte[4 * 1024];  // 4K 
            while ((bytesRead = fin.read(buf)) != -1) { 
                fout.write(buf, 0, bytesRead); 
            } 
            fout.flush(); 
            fout.close(); 
            fin.close(); 
    	}
    	catch(Exception e){
    		e.getStackTrace();
    	}
    	 
     } 
    
}
