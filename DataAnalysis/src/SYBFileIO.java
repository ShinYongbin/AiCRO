import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class SYBFileIO {
	
	
	 public static int dirNUM(String inputDIRPaht)
	 {

		 int iDirNUM = 0;
		 
		 iDirNUM = dirNUM(new File(inputDIRPaht));
		 
		 return iDirNUM;
	 }
	 public static int dirNUM(File inputDIR)
	 {
		 int iDirNUM = 0;
		 
		 File[] chkFile = inputDIR.listFiles();
		 
		 for(File ChkFileTMp:chkFile)
		 {
			 if(ChkFileTMp.isDirectory())
				 iDirNUM++;
		 }
		 return iDirNUM;
		 
	 }
	 
	 public static int fileNUM(String inputDIRPaht)
	 {

		 int iDirNUM = 0;
		 
		 iDirNUM = dirNUM(new File(inputDIRPaht));
		 
		 return iDirNUM;
	 }
	 public static int fileNUM(File inputDIR)
	 {
		 int iDirNUM = 0;
		 
		 File[] chkFile = inputDIR.listFiles();
		 
		 for(File ChkFileTMp:chkFile)
		 {
			 if(ChkFileTMp.isFile())
				 iDirNUM++;
		 }
		 return iDirNUM;
		 
	 }
	
	 public static void fileCopy(String inFileName, String outFileName) {
		 
		 String sMakeDir = "";
		 String[] sSplitPaht = outFileName.split("\\\\");
		 for(int i=0; i<sSplitPaht.length-1; i++)
			 sMakeDir += "\\\\"+sSplitPaht[i];
		 
		 new File(sMakeDir).mkdirs();
		 
		 File inputFile = new File(inFileName);
		 File outputFile = new File(outFileName);
		 
		
		
		 
		 fileCopy(inputFile, outputFile);
		 
	 }
	 
	 public static void fileCopy(File inFileName, File outFileName) {
		 
		 if(outFileName.exists())
			 return;
		 try {
			 FileInputStream fis = new FileInputStream(inFileName);
			 FileOutputStream fos = new FileOutputStream(outFileName);

			 FileChannel fcin = fis.getChannel();
			 FileChannel fcout = fos.getChannel();

			 long size = fcin.size();
			 fcin.transferTo(0, size, fcout);

			 /*  int data = 0;
		   while((data=fis.read())!=-1) {
		    fos.write(data);
		   }*/
			 fis.close();
			 fos.close();

		 }catch (IOException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
	 }
	 
	 public static void replaceFileName(String inFileName, String outFileName) {
		 
		 String[] splitInputPath = inFileName.split("\\\\");
		 String outputfilePath = "";
		 for(int i=0; i<splitInputPath.length-1;i++)
		 {
			 outputfilePath += splitInputPath[i]+File.separator;
		 }
		 
		 outputfilePath += outFileName;
		 
		 new File(inFileName).renameTo(new File(outputfilePath));
		 
	 }
	 
	 
	 public static void replaceFileNameCopy(String inFileName, String outFileName) {
		 
		 String[] splitInputPath = inFileName.split("\\\\");
		 String outputfilePath = "";
		 for(int i=0; i<splitInputPath.length-1;i++)
		 {
			 outputfilePath += splitInputPath[i]+File.separator;
		 }
		 
		 outputfilePath += outFileName;
		 
	//	 new File(inFileName).renameTo(new File(outputfilePath));
		 
		 fileCopy(inFileName, outputfilePath);
		 
	 }
	 
	 public static void dirCopy(String inFileName, String outFileName) {
		 
		
		 
		 
		 File[] FileList = new File(inFileName).listFiles();
		 

		 new File(outFileName).mkdirs();
		
		 

		 for(int i=0; i<FileList.length; i++)
		 {
			 
			 File  tmp = new File(outFileName+File.separator+FileList[i].getName());
			 if(tmp.isDirectory() && (!tmp.exists()))
				 tmp.mkdirs();


			
			 
			  if(FileList[i].isDirectory())
			 {
				 tmp.mkdirs();
				 dirCopy(FileList[i].getAbsolutePath(), tmp.getAbsolutePath());
			 }
			 else if(FileList[i].isFile())
			 {
				 fileCopy(FileList[i].getAbsolutePath(), tmp.getAbsolutePath());
			 }
		 }
		
	 
	 }
	   public static boolean chkFileExt(File file, String sExtension) {
	        String fileName = file.getName();
	        String ext = fileName.substring(fileName.lastIndexOf(".") + 1,
	                fileName.length());
	  //      final String[] BAD_EXTENSION = { "jsp", "php", "asp", "html", "perl" };
	 
	       //nt len = BAD_EXTENSION.length;
	       // for (int i = 0; i < len; i++) {
	            if (ext.equalsIgnoreCase(sExtension)) {
	                return true; // 불량 확장자가 존재할때..
	            }
	       // }
	        return false;

	 
	 }
	   
	   public static List<String> getAllFilePath(String rootFilePath)
	   {
		 File[] fileList = new File(rootFilePath).listFiles();
		 List<String> ouptFilePath = new ArrayList<>();
		 
		 
		 for(File tmp : fileList)
		 {
			 
			 if(tmp.isDirectory())
			 {
				 if(getAllFilePath(tmp.getAbsolutePath())==null)
					 continue;
				 
				 ouptFilePath.addAll(getAllFilePath(tmp.getAbsolutePath()));
			 }
			 else if(tmp.isFile())
				 ouptFilePath.add(tmp.getAbsolutePath());
		 
		 }
		   
		   
		return ouptFilePath;
		   
	   }
	   
	  
	   
	   
	   
	   
	   

}
