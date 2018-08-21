
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.dcm4che3.data.Tag;


public class AC_SortDcmPath {
	
	static List<String> m_resFilePath = new ArrayList<String>();
	static List<File> m_resFile = new ArrayList<File>();
	static File m_fRootFile;
	
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
        }		
	    return false;
		
	}
	
	public static List<File[]> seperateSeries(File[] files2sort) {
	    HashMap<String, SortedMap<Integer, File>> series = new HashMap<String, SortedMap<Integer, File>>();

	    
	    try {
	    	for (File file : files2sort) {
	    		String[] sSortValue =  new String[2];
	
	    		
	    		if ( file.exists()) {
	    			Dcm4cheV3io  dh = null;
					try {
						dh = new Dcm4cheV3io(file);
						 sSortValue[0] = dh.getDicomTag2String(Tag.StudyInstanceUID);
						 
						
						sSortValue[1] = dh.getDicomTag2String(Tag.InstanceNumber);
						 
						 
					} catch (Exception e) {
						// TODO Auto-generated catch block
						System.out.println("AC_Sort :: DCM4che Cant open File");
						e.printStackTrace();
					}
					
	    			String seriesUID = sSortValue[0];//getDicomTag2String(Tag.SeriesInstanceUID);
	    			int instanceNr = Integer.parseInt(sSortValue[1]);
	    			if (!series.containsKey(seriesUID)) {
	    				SortedMap<Integer, File> al = new TreeMap<Integer, File>();
	    				series.put(seriesUID, al);
	    			} 
	    			(series.get(seriesUID)).put(instanceNr, file);
	    		}	    			
	    	}
		} catch (Error e) {
			System.out.println(e);
		}
	    
	    
	    List<File[]> retVal = new ArrayList<File[]>();
	    for (SortedMap<Integer, File> alFiles : series.values()) 
	    {
		retVal.add(alFiles.values().toArray(new File[0]));		
	    }
	    return retVal;
	}

}
