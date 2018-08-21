package AC_DicomData;

public class AC_DataType {
	
	public final static int UNKNOWN= -1;
	public final static int CT = 0;
	public final static int MR = 1;
	public final static int US = 3;
	

	
	public static int chkModality(String sinpput)
	{
		String sInputUpper = sinpput.toUpperCase();
		
		if(sInputUpper.contains("CT"))
			return CT; 
		if(sInputUpper.contains("MR"))
			return MR; 
		if(sInputUpper.contains("US"))
			return US; 
		

		return UNKNOWN; 
	
	}
}
