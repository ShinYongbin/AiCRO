package AC_DicomData;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.javafx.property.adapter.ReadOnlyPropertyDescriptor;

import AC_DicomIO.AC_DicomReader;
import AC_DicomIO.AC_Tag;


public class AC_SortDcmPath {
	
	static List<String> m_resFilePath = new ArrayList<String>();
	static List<File> m_resFile = new ArrayList<File>();
	static File m_fRootFile;
	 static AC_DicomReader m_diconReader = null;
	
	private static HashMap<String, SortedMap<Double, File>> m_hmSeries = new HashMap<String, SortedMap<Double, File>>();
	
	 static SortedMap<Double, File> m_smEmpty = new TreeMap<Double, File>();
	
	final static String[] CHK_DCM = { "dcm", "DCM", "Dcm",null};
	

	
	
	public AC_SortDcmPath(String filepath)
	{

		
		setRootPath(filepath);
	}
	
	public void setRootPath(String filepath)
	
	
	{
		if(!(new File(filepath).exists()))
		{
			System.out.println(filepath + "Not Exists!!!");
			return;
		}
		
		m_fRootFile = new File(filepath);
	}
	
	
	
	public Collection<File[]> buildDicomFileList()
	{
		
		
	
		chkDir(m_fRootFile);
		
		File[] tmpfile = m_resFile.toArray(new File[m_resFile.size()]);
		Collection<File[]> output = seperateSeries(tmpfile);
	
		
	
		//System.out.println("dss");
		return output;
	
	}
	
	public static Collection<File[]> buildDicomFileList(String inputPath)
	{
		
		File inputfile = new File(inputPath);
	
		chkDir(inputfile);
		
		File[] tmpfile = m_resFile.toArray(new File[m_resFile.size()]);
		Collection<File[]> output = seperateSeries(tmpfile);
	
		
	
		System.out.println("AC_SortDCM : build dicom path!!");
		return output;
	
	}
	
	private static void chkDir(File inF)
	{
		File[] FileList = inF.listFiles();
		
		if(FileList==null)
		{
			 boolean chkDCM =  addDcmDicomPath(inF);
			 if(chkDCM)
				 m_resFile.add(inF);
			 return;
		}
		 
		 int num =FileList.length;
		 
		 for(int i=0; i<num; i++)
		 {
			  if(FileList[i].isDirectory())
			 {
				  chkDir(FileList[i]);
			 }
			 else if(FileList[i].isFile())
			 {
				 boolean chkDCM =  addDcmDicomPath(FileList[i]);
				 if(chkDCM)
					 m_resFile.add(FileList[i]);
			 }
		 }
		 
	}

	private static boolean addDcmDicomPath(File f) {
		// TODO Auto-generated method stub
	
		String fileName = f.getName();
	    String ext = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());


	    for (int i = 0; i < CHK_DCM.length; i++) {
            if (ext.equalsIgnoreCase(CHK_DCM[i]))
            {
            	m_resFilePath.add(f.getAbsolutePath());
            	return true;
            }
            /*else if(!Environment.g_flagStandAlone)
            {
            	m_resFilePath.add(f.getAbsolutePath());
            	return true;
            }*/
        }		
	    return false;
		
	}
	
	public static List<File[]> seperateSeries(File[] files2sort) {
	    HashMap<String, SortedMap<Double, File>> series = new HashMap<String, SortedMap<Double, File>>();

	    
	    try {
	    	for (File file : files2sort) {
	    		String[] sSortValue =  new String[2];
	
	    		
	    		if ( file.exists()) {
	    			AC_DicomReader dh = null;
					try {
						dh = new AC_DicomReader(file);
						 sSortValue = dh.getSortvalue();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println("AC_Sort :: DCM4che Cant open File");
						e.printStackTrace();
					}
					
	    			String seriesUID = sSortValue[0];//getDicomTag2String(Tag.SeriesInstanceUID);
	    			 String tmp[] =sSortValue[1].split("\\\\");
	    			
	    			
	    			double instanceNr = Double.parseDouble(tmp[2]);
	    			if (!series.containsKey(seriesUID)) {
	    				SortedMap<Double, File> al = new TreeMap<Double, File>();
	    				
	    				series.put(seriesUID, al);
	    			} 
	    			
	    			(series.get(seriesUID)).put(instanceNr, file);
	    		}	    			
	    	}
		} catch (Error e) {
			System.out.println(e);
		}
	    
	    
	    List<File[]> retVal = new ArrayList<File[]>();
	    for (SortedMap<Double, File> alFiles : series.values()) 
	    {
		retVal.add(alFiles.values().toArray(new File[0]));		
	    }
	    return retVal;
	}
	
	
	public static File[] seperateSeriesSingle(String files2sort) {
	   
		/// List<File> retVal = new ArrayList<File>();
		 SortedMap<Integer, File> al = new TreeMap<Integer, File>();
		

	    
		 try {
			 for (File file : new File(files2sort).listFiles()) 
			 {
				 
				 String[] sSortValue =  new String[2];


				 if ( file.exists()) {
					 AC_DicomReader dh = null;
					 try {
						 dh = new AC_DicomReader(file);
						 sSortValue = dh.getSortvalue();
					 } catch (Exception e) {
						 // TODO Auto-generated catch block
						 System.out.println("AC_Sort :: DCM4che Cant open File");
						 e.printStackTrace();
					 }

					 String seriesUID = sSortValue[0];//getDicomTag2String(Tag.SeriesInstanceUID);
					 int instanceNr = Integer.parseInt(sSortValue[1]);

					 al.put(instanceNr, file);


				 }	    			
			 }
		 } catch (Error e) {
			 System.out.println(e);
		 }
		 
		 File[] fRessult = al.values().toArray(new File[0]);
	    
	    
	    return fRessult;
	}
	
	public static void addDCN(File files2sort) {
		   
		
		m_diconReader = null;
		String[] sSortValue =  new String[2];
		m_smEmpty =  new TreeMap<Double, File>();
		
		

		 if ( files2sort.exists() && !(files2sort.length()<128)) 
		 {
				

			 try {
				 m_diconReader = new AC_DicomReader(files2sort);
				 sSortValue = m_diconReader.getSortvalue();
			 } catch (Exception e) {
				 // TODO Auto-generated catch block
				 System.out.println("AC_Sort :: DCM4che Cant open File");
				 e.printStackTrace();
			 }
			 
			 if(sSortValue==null)
				 return;

			 
			 
			 String seriesUID = sSortValue[0];//getDicomTag2String(Tag.SeriesInstanceUID);
			 
			 String tmp[] =sSortValue[1].split("\\\\");
 			
 			
 			double instanceNr = Double.parseDouble(tmp[2]);
			
			 if (!m_hmSeries.containsKey(seriesUID)) 
			 {
				 m_hmSeries.put(seriesUID, m_smEmpty);
			 } 

			 (m_hmSeries.get(seriesUID)).put(instanceNr, files2sort);
		 }


		
	    
	}
	
	public static boolean chkDCMFormat(String sFilePath) throws IOException
	{
		if(new File(sFilePath).length()<128)
			return false;
		
		String DICM = "DICM";

		FileInputStream fis = null;
		BufferedInputStream bisInputStream = null;

		fis = new FileInputStream(sFilePath);
		bisInputStream = new BufferedInputStream(fis);
		bisInputStream.mark(400000);
		bisInputStream.reset();




		int ID_OFFSET = 128;

		while (ID_OFFSET > 0)
			ID_OFFSET -= bisInputStream.skip(ID_OFFSET);

		byte[] buf = new byte[4];

		for(int i=0; i<4;i++)
		{
			buf[i] =  (byte) bisInputStream.read();
		}

		//location += length;

		String tmp = new String(buf);
		String newTmp = tmp.replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");

		if (newTmp.equals(DICM)) 
		{
			return true;
		}
		return false;
	}
	
	public static Collection<File[]> sortDcmFileList(File[] fInput)
	{
		
	//	SortedMap<Integer, File> smSeries = new TreeMap<Integer, File>();
		//File[] fileList = fInput.listFiles()
		m_hmSeries.clear();
		try {
			for(File tmp : fInput)
			readDir(tmp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		  List<File[]> retVal = new ArrayList<File[]>();
		 for (SortedMap<Double, File> alFiles : m_hmSeries.values()) 
		 {
			 retVal.add(alFiles.values().toArray(new File[0]));		
		 }

		return retVal;
	}
	
	
	
	
	private static  void readDir(File inF) throws IOException
	{
	
		
		if(inF.isFile())
		{
			addDCN(inF);
			 
			 return;
		}
		 
		
		File[] FileList = inF.listFiles();
		if(FileList == null)
			return;
		
		 int num =FileList.length;
		 
		 for(int i=0; i<num; i++)
		 {
			  if(FileList[i].isDirectory())
			 {
				  chkDir(FileList[i]);
			 }
			 else if(FileList[i].isFile())
			 {
				addDCN(FileList[i]);
				
			 }
		 }
		 
	}
	

	public static File[] sortDcmFileListOnlyDir(String dirPath)
	{
		
		
		File[] filelist = new File(dirPath).listFiles();
		
		
		m_smEmpty =  new TreeMap<Double, File>();
		
		for(File tmp : filelist)
		{
			m_diconReader = null;
			String[] sSortValue =  new String[2];
			
			
			

			 if ( tmp.exists() && !(tmp.length()<128)) 
			 {
					

				 try {
					 m_diconReader = new AC_DicomReader(tmp);
					 sSortValue = m_diconReader.getSortvalue();
				 } catch (Exception e) {
					 // TODO Auto-generated catch block
					 System.out.println("AC_Sort :: DCM4che Cant open File");
					 e.printStackTrace();
				 }
				 
				 if(sSortValue==null)
					 continue;

				 String tmp2[] =sSortValue[1].split("\\\\");
		 			
		 			
		 			double instanceNr = Double.parseDouble(tmp2[2]);
					
				 m_smEmpty.put(instanceNr, tmp);
			 }

		}
		
		
		 File[] retVal = m_smEmpty.values().toArray(new File[m_smEmpty.size()]);
			

			return retVal;
	}
	
	
	
	
	
	
	

}
