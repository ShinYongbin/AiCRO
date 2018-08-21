import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SortDcmPath {
	
	List<String> m_resFilePath = new ArrayList<String>();
	static File m_fRootFile;
	
	final String[] CHK_DCM = { "dcm", "DCM", "Dcm"};
	

	
	
	public SortDcmPath(String filepath)
	{

		
		setRootPath(filepath);
	}
	
	public void setRootPath(String filepath)
	
	
	{
		if(!(new File(filepath).exists()))
		{
			
		}
		
		m_fRootFile = new File(filepath);
	}
	
	
	
	public void buildDicomFileList()
	{
		
		
	
		chkDir(m_fRootFile);
		System.out.println("dss");
	
	}
	private void chkDir(File inF)
	{
		 File[] FileList = inF.listFiles();
		 
		 int num =FileList.length;
		 
		 for(int i=0; i<num; i++)
		 {
			  if(FileList[i].isDirectory())
			 {
				  chkDir(FileList[i]);
			 }
			 else if(FileList[i].isFile())
			 {
				 addDcmDicomPath(FileList[i]);
			 }
		 }
		 
	}

	private boolean addDcmDicomPath(File f) {
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

}
