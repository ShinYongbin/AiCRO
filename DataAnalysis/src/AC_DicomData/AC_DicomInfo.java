package AC_DicomData;

import java.util.Hashtable;

public class AC_DicomInfo {
	
	Hashtable<Integer, String> m_DicomTable= new Hashtable<Integer, String>();
	


	public void setValue(int iTag, String sValue)
	{
		m_DicomTable.put(iTag,  sValue);
	}
	
	public String getString(int iTag)
	{
		if(m_DicomTable.get(iTag)==null)
			return "N/A";
		
		return m_DicomTable.get(iTag);
	}
	
	public double getDouble(int iTag)
	{
		if(m_DicomTable.get(iTag)==null)
			return -1.0;
		return Double.parseDouble(m_DicomTable.get(iTag));
	}
	public int getInt(int iTag)
	{
		if(m_DicomTable.get(iTag)==null)
			return -1;
		return Integer.parseInt(m_DicomTable.get(iTag));
	}
	public byte getByte(int iTag)
	{
		if(m_DicomTable.get(iTag)==null)
			return -1;
		return Byte.parseByte(m_DicomTable.get(iTag));
	}
	

}
