package AC_DicomIO;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import AC_DicomData.AC_DicomInfo;


public class AC_DicomReader {
	
	private static final int ID_OFFSET = 128;  //location of "DICM"
	private static final String DICM = "DICM";
	private static final int IMPLICIT_VR = 0x2D2D; // '--' 
	private AC_DicomDictionary m_DicomDic =  new AC_DicomDictionary();;
	
	private String m_sFilePath = null;
	private File m_sFile = null;
	BufferedInputStream m_bisInputStream;
	private boolean m_flagFileEnd = false;
//	private int ifReadLoactaion =0;
	private boolean m_bLittleEndian = true;
	///	TransferSyntaxUID
	private String m_sTransferSyntaxUID = null;
	private boolean m_bigEndianTransferSyntax = false;
	private boolean m_Compressed = false;

	////// Now property
	private int m_VR;
	private int m_nElementLength = 0;
	private int m_iInSequenc = 0;
	private int m_iInItem = 0;
	private int m_TageID;
	
	

	
	public AC_DicomReader() {
		
		//m_DicomDic = new AC_DicomDictionary();

	}
	
	public AC_DicomReader(String sFilePath) {
		//m_DicomDic = new AC_DicomDictionary();
		
		m_sFilePath = sFilePath;
		m_sFile = new File(m_sFilePath);
		
		init(m_sFile);
	}
	public AC_DicomReader(File sFilePath) {
		//m_DicomDic = new AC_DicomDictionary();
		
		
		
		if(m_sFilePath==null)
			m_sFilePath = sFilePath.getAbsolutePath();
	
		init(sFilePath);
	}
	
	private void init(File sFilePath)
	{
		 FileInputStream fis = null;
		 
		 try {
				fis = new FileInputStream(sFilePath);
				m_bisInputStream = new BufferedInputStream(fis);
				m_bisInputStream.mark(400000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace(); 
			}
	}
	
	private void skip(long lskipCount)
	{
		while (lskipCount > 0)
			try {
				lskipCount -= m_bisInputStream.skip(lskipCount);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void readSequnce()
	{
		boolean inSequnce = true;
		
		try {
			int iTmpDicom = getNextTag();
			
			if(m_VR == AC_VR.SQ)
				readSequnce();
			
			if(m_nElementLength == -1)
			{
				while(inSequnce)
				{
					int iTmpDicom2 = getNextTag();
					if(iTmpDicom2==AC_Tag.SequenceDelimitationItem)
						inSequnce = false;
					else
						skip(m_nElementLength);
				}
					
			}else
				
			{
				skip(m_nElementLength);
				System.out.println(String.format("SKIPp = IN SQ = TAG : %08x , Lengt : %d", iTmpDicom,m_nElementLength));
			}
			
			
			System.out.println(String.format("IN SQ = TAG : %08x , Lengt : %d", iTmpDicom,m_nElementLength));
			
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	private void moveHeaderStart() throws IOException
	{
		skip(ID_OFFSET);
		
		if (!getString(4).equals(DICM)) 
		{
			if (m_bisInputStream==null) 
				m_bisInputStream.close();
			if (m_bisInputStream!=null)
			{
				m_bisInputStream.reset();
				 m_flagFileEnd = false;
			}
		}
		
	}
	
	private boolean checkHeaderStart() throws IOException
	{
		skip(ID_OFFSET);
		
		if (!getString(4).equals(DICM)) 
		{
			if (m_bisInputStream==null) 
				m_bisInputStream.close();
			if (m_bisInputStream!=null)
			{
				m_bisInputStream.reset();
				 m_flagFileEnd = false;
			}
			
			return false;
		}
		return true;
		
	}
	
	private void checkTSUID() throws IOException
	{

		m_sTransferSyntaxUID = getString(m_nElementLength);
		
////		if (s.indexOf("1.2.4")>-1||s.indexOf("1.2.5")>-1) {
////		f.close();
////		String msg = "ImageJ cannot open compressed DICOM images.\n \n";
////		msg += "Transfer Syntax UID = "+s;
////		throw new IOException(msg);
////	}
		
		
		
	if (m_sTransferSyntaxUID.indexOf("1.2.840.10008.1.2.2")>=0)
		m_bigEndianTransferSyntax = true;
	
	if (m_sTransferSyntaxUID.indexOf("1.2.840.10008.1.2.4")>=0)
	{
		m_Compressed = true;
		JOptionPane.showMessageDialog(null, "Compressed DCM은 아직 지원하지 않습니다.","File Reader Error",JOptionPane.ERROR_MESSAGE);
	}
	
	
	//if()
	

	}
	
	public String[] getSortvalue() throws IOException
	{
		m_bisInputStream.reset();
		 m_flagFileEnd = false;
		
	
		String sInstanceValue = null;
		String sSeriesUIDeValue = null;
		
		
		if(!checkHeaderStart())
			return null;
		
	
		
		while(sInstanceValue==null || sSeriesUIDeValue==null)
		{
			int iTmpDicom = getNextTag();
		
			
			if(iTmpDicom==AC_Tag.TransferSyntaxUID)
			{			
				checkTSUID();
			}
	
			else if(m_VR==AC_VR.SQ)
			{
				
				readSequnce();
			}

			//else if(iTmpDicom==AC_Tag.InstanceNumber)
			else {
				switch (iTmpDicom)
				{
				/*case AC_Tag.InstanceNumber:
					sInstanceValue = getValue(m_VR);
					break;*/
				case AC_Tag.SeriesInstanceUID:
					sSeriesUIDeValue = getValue(m_VR);
					break;
				case AC_Tag.ImagePositionPatient:
					sInstanceValue = getValue(m_VR);
					break;



				default:
					skip(m_nElementLength);
				/*	System.out.println("skip ");
					System.out.println("Tag ID "+Integer.toHexString(iTmpDicom)
					+ " VR"+m_VR + " lengt "+m_nElementLength );*/
					break;
				}
			}
			/*else if(iTmpDicom==AC_Tag.InstanceNumber)
			{   
				sInstanceValue = getValue(m_VR);
			}
			else if(iTmpDicom==AC_Tag.SeriesInstanceUID)
			{   
				sSeriesUIDeValue = getValue(m_VR);
			}
			else
			{
				skip(m_nElementLength);
				System.out.println("skip ");
				System.out.println("Tag ID "+Integer.toHexString(iTmpDicom)
				+ " VR"+m_VR + " lengt "+m_nElementLength );
			//	System.out.println(" value : "+ sValue);*

			}*/
			//	sValue = getValue(m_VR);	
		}
		
		String[] output = new String[2];
		
		System.out.println("seriesUID "+ sSeriesUIDeValue);
		System.out.println("InstanceNUM "+ sInstanceValue);
		
		output[0] = sSeriesUIDeValue;
		output[1] = sInstanceValue;
		
		return output;
	}
	
	public byte[] getDicomInfo(AC_DicomInfo info) throws IOException
	{
		m_bisInputStream.reset();
		 m_flagFileEnd = false;
		
	
		byte[] bPixelData = null;
		
		boolean decodingTags = true;
		
		moveHeaderStart();
		
		while(decodingTags)
		{
			int iTmpDicom = getNextTag();
			
			
			if(iTmpDicom==AC_Tag.TransferSyntaxUID)
			{			
				checkTSUID();
			}
			else if(m_VR==AC_VR.SQ)
			{
				
				readSequnce();
			}

	
			else 
			{
			
			switch (iTmpDicom)
			{
				case AC_Tag.InstanceNumber: case AC_Tag.SeriesInstanceUID: case AC_Tag.WindowCenter: case AC_Tag.WindowWidth: case AC_Tag.Rows: case AC_Tag.Columns:
				case AC_Tag.BitsAllocated: case AC_Tag.BitsStored: case AC_Tag.RescaleIntercept: case AC_Tag.RescaleSlope: case AC_Tag.StudyInstanceUID: case AC_Tag.StudyTime:
				case AC_Tag.StudyDate: case AC_Tag.PatientID: case AC_Tag.StudyDescription: case AC_Tag.SeriesDescription: case AC_Tag.Modality:
				case AC_Tag.SeriesNumber: case AC_Tag.PatientsName :case AC_Tag.PatientsBirthDate :case AC_Tag.PatientsSex :case AC_Tag.InstitutionName :
				case AC_Tag.StudyID :case AC_Tag.SliceThickness :case AC_Tag.SliceLocation : case AC_Tag.SeriesDate: case AC_Tag.SeriesTime:
				case AC_Tag.XRayTubeCurrent :case AC_Tag.KVP : case AC_Tag.MagneticFieldStrength: case AC_Tag.RepetitionTime:case AC_Tag.EchoTime:
				case AC_Tag.PixelRepresentation:case AC_Tag.SamplesperPixel:case AC_Tag.ImageOrientationPatient:
				info.setValue(iTmpDicom, getValue(m_VR));
				break;
				case AC_Tag.PixelSpacing:
					info.setValue(iTmpDicom, getValue(m_VR));
					break;
			case AC_Tag.PixelData :

				bPixelData = new byte[m_nElementLength];
				for(int i=0; i<m_nElementLength;i++)
				{
					bPixelData[i] =  (byte) m_bisInputStream.read();
				}
				decodingTags =false;
				break;


			default:
				skip(m_nElementLength);
				break;
			}
			}
		}

		
		return bPixelData;
	}
	
	public byte[] getPixel(AC_DicomInfo info) throws IOException
	{
		m_bisInputStream.reset();
		 m_flagFileEnd = false;
		
	
		byte[] bPixelData = null;
		
		boolean decodingTags = true;
		
		moveHeaderStart();
		
		while(decodingTags)
		{
			int iTmpDicom = getNextTag();
			
			
			if(iTmpDicom==AC_Tag.TransferSyntaxUID)
			{			
				checkTSUID();
			}
	
			else if(m_VR==AC_VR.SQ)
			{
				
				readSequnce();
			}

			else 
			{
			
			switch (iTmpDicom)
			{
				case AC_Tag.WindowCenter: case AC_Tag.WindowWidth: case AC_Tag.Rows: case AC_Tag.Columns:
				case AC_Tag.BitsAllocated: case AC_Tag.BitsStored: case AC_Tag.RescaleIntercept: case AC_Tag.RescaleSlope: 
				case AC_Tag.PixelRepresentation: case AC_Tag.SamplesperPixel: case AC_Tag.ImageOrientationPatient:
			//	case AC_Tag.ori
					case AC_Tag.SliceThickness:  case AC_Tag.SliceLocation: case AC_Tag.XRayTubeCurrent:
					case AC_Tag.KVP:  case AC_Tag.MagneticFieldStrength: case AC_Tag.RepetitionTime:
					case AC_Tag.EchoTime:
				info.setValue(iTmpDicom, getValue(m_VR));
				break;
			case AC_Tag.PixelData :

				bPixelData = new byte[m_nElementLength];
				for(int i=0; i<m_nElementLength;i++)
				{
					bPixelData[i] =  (byte) m_bisInputStream.read();
				}
				decodingTags =false;
			
				break;


			default:
				skip(m_nElementLength);
				break;
			}
			}
		}
		
		//m_bisInputStream.close();

		
		return bPixelData;
	}
	
	public void getCustomDicomheaer(AC_DicomInfo info) throws IOException
	{
		m_bisInputStream.reset();
		 m_flagFileEnd = false;
		
	
		byte[] bPixelData = null;
		
		boolean decodingTags = true;
		
		moveHeaderStart();
		
		while(decodingTags)
		{
			int iTmpDicom = getNextTag();
			
			
			if(iTmpDicom==AC_Tag.TransferSyntaxUID)
			{			
				checkTSUID();
			}
	
			else if(m_VR==AC_VR.SQ)
			{
				
				readSequnce();
			}

			else 
			{
			
			switch (iTmpDicom)
			{
				 case AC_Tag.Rows: case AC_Tag.Columns:
				
				case AC_Tag.ManufacturersModelName:
				case AC_Tag.ReconstructionDiameter:
				case AC_Tag.SeriesNumber:
				case AC_Tag.InstitutionName:
				case AC_Tag.Manufacturer:
				case AC_Tag.SeriesDate:
		
				
				info.setValue(iTmpDicom, getValue(m_VR));
				break;
			case AC_Tag.PixelData :

				decodingTags =false;
			
				break;


			default:
				skip(m_nElementLength);
				break;
			}
			}
		}
		
		//m_bisInputStream.close();

		
	}
	
	public void close()
	{
		if(m_DicomDic!=null )
		{
			m_DicomDic.clear();
			m_DicomDic = null;
		}
	
		try {
			m_bisInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getTagValue(int inputTag) throws IOException
	{
		m_bisInputStream.reset();
		 m_flagFileEnd = false;
		
		String sValue = null;
		boolean decodingTags = true;
		
		moveHeaderStart();
		
		while(decodingTags&&  !m_flagFileEnd)
		{
			int iTmpDicom = getNextTag();
		
			
			if(iTmpDicom==AC_Tag.TransferSyntaxUID)
			{			
				checkTSUID();
			}
	
			else if(m_VR==AC_VR.SQ)
			{
				
				readSequnce();
			}

			//else if(iTmpDicom==AC_Tag.InstanceNumber)
			else if(iTmpDicom==inputTag)
			{   sValue = getValue(m_VR);
			System.out.println("get value :" + sValue);
			System.out.println("Tag ID "+Integer.toHexString(iTmpDicom)
			+ " VR"+m_VR + " lengt "+m_nElementLength );
				break;
			}
			else
			{
				skip(m_nElementLength);
				/*System.out.println("skip ");
				System.out.println("Tag ID "+Integer.toHexString(iTmpDicom)
				+ " VR"+m_VR + " lengt "+m_nElementLength );*/
			//	System.out.println(" value : "+ sValue);*

			}

			//	sValue = getValue(m_VR);	
		}
		
		return sValue;
	}
	
	private int getNextTag() throws IOException
	{
		int igroupWord = getShort();
		if (igroupWord==0x0800 && m_bigEndianTransferSyntax) {
			m_bLittleEndian= false;
			igroupWord = 0x0008;
		}
		int ielementWord = getShort();
		
		int tag = igroupWord<<16 | ielementWord;

		
		
		m_nElementLength = getLength();

		
		/*if(m_iInSequenc>0)
			m_nElementLength = -1;*/
		
		

		if(tag==AC_Tag.SequenceDelimitationItem)
		{
			m_iInSequenc--;
			m_nElementLength = -1;
		}

		if(tag==AC_Tag.Item)
		{
			m_iInItem++;
			m_nElementLength = -1;
		}
		if(tag==AC_Tag.ItemDelimitationItem)
		{
			m_iInItem--;
			m_nElementLength = -1;
		}
		
		

		 m_TageID = tag;
		
		return tag;
	
	}
	
	
	
  	int getLength() throws IOException {
		int b0 = getByte();
		int b1 = getByte();
		
		int b2 = getByte();
		int b3 = getByte();
		
		// We cannot know whether the VR is implicit or explicit
		// without the full DICOM Data Dictionary for public and
		// private groups.
		
		// We will assume the VR is explicit if the two bytes
		// match the known codes. It is possible that these two
		// bytes are part of a 32-bit length for an implicit VR.
		m_VR = (b0<<8) + b1;
		
		switch (m_VR) {
		
		
			case AC_VR.OB: case  AC_VR.OW: case  AC_VR.SQ: 
				case  AC_VR.UN: case  AC_VR.UT:
					
				if(m_VR==AC_VR.SQ)
				{
	
					m_iInSequenc++;
				}
				
				// Explicit VR with 32-bit length if other two bytes are zero
				if ( (b2 == 0) || (b3 == 0) ) 
				{
					return getInt();
				}
				// Implicit VR with 32-bit length
				m_VR = IMPLICIT_VR;// = IMPLICIT_VR;
				if (m_bLittleEndian)
					return ((b3<<24) + (b2<<16) + (b1<<8) + b0);
				else
					return ((b0<<24) + (b1<<16) + (b2<<8) + b3);		
			case AC_VR.AE: case AC_VR.AS: case AC_VR.AT: case AC_VR.CS: case AC_VR.DA: case AC_VR.DS: case AC_VR.DT:  case AC_VR.FD:
			case AC_VR.FL: case AC_VR.IS: case AC_VR.LO: case AC_VR.LT: case AC_VR.PN: case AC_VR.SH: case AC_VR.SL: case AC_VR.SS:
			case AC_VR.ST: case AC_VR.TM:case AC_VR.UI: case AC_VR.UL: case AC_VR.US: case AC_VR.QQ:
				// Explicit vr with 16-bit length
				if (m_bLittleEndian)
					return ((b3<<8) + b2);
				else
					return ((b2<<8) + b3);
			default:
				// Implicit VR with 32-bit length...
				m_VR = IMPLICIT_VR;
				if (m_bLittleEndian)
					return ((b3<<24) + (b2<<16) + (b1<<8) + b0);
				else
					return ((b0<<24) + (b1<<16) + (b2<<8) + b3);
		}
	}

	private String getValue(int iVR) throws IOException
	{
		if(m_nElementLength==-1 ||m_nElementLength==0)
			return "-1";
		
		if(IMPLICIT_VR==iVR)
		{
		//	m_DicomDic = new AC_DicomDictionary();
			m_VR = iVR = m_DicomDic.getTagVR(m_TageID);
		}
		
		String sValue ="";
	
		switch(iVR)
		{
		case AC_VR.OB: case AC_VR.UN:
			int ivm = m_nElementLength/2;
			String fullS = "";
			
			byte b0 = (byte)getByte();
			byte b1 = (byte)getByte();
			fullS += Byte.toString(b0) +"\\\\"+ Byte.toString(b1);
			//
			for(int i=1; i<ivm;i++)	
			{
				b0 = (byte)getByte();
				b1 = (byte)getByte();
				fullS +="\\\\"+Byte.toString(b0) +"\\\\"+ Byte.toString(b1);
			}
			sValue =fullS;
			break;
		case AC_VR.UL:
			sValue =(Integer.toString(getInt()));
			break;
		case AC_VR.FD:
			 ivm = m_nElementLength/8;
			 fullS = "";
			
			
			fullS +=Double.toString(getDouble());
			//
			for(int i=1; i<ivm;i++)	
			{
				fullS +="\\\\"+Double.toString(getDouble());
			}
			break;
		case AC_VR.FL:
			
			if (m_nElementLength==2)
				sValue = Float.toString(getFloat());
			else {
				sValue = "";
				int n = m_nElementLength/2;
				for (int i=0; i<n; i++)
					sValue += Float.toString(getFloat())+" ";
			}
			break;
			
		case AC_VR.AE: case AC_VR.AS: case AC_VR.AT: case AC_VR.CS: case AC_VR.DA: case AC_VR.DS: case  AC_VR.DT: 
		case  AC_VR.IS: case  AC_VR.LO: case AC_VR.LT: case AC_VR.PN: case AC_VR.SH: case AC_VR.ST: case AC_VR.TM: case AC_VR.UI:
			sValue = getString(m_nElementLength);
			break;
		case AC_VR.US:
			if (m_nElementLength==2)
				sValue = Integer.toString(getShort());
			else {
				sValue = "";
				int n = m_nElementLength/2;
				for (int i=0; i<n; i++)
					sValue += Integer.toString(getShort())+" ";
			}
			break;
		case IMPLICIT_VR:
			 ivm = m_nElementLength/2;
			 fullS = "";
			
			 b0 = (byte)getByte();
			 b1 = (byte)getByte();
			fullS += Byte.toString(b0) +"\\\\"+ Byte.toString(b1);
			//
			for(int i=1; i<ivm;i++)	
			{
				b0 = (byte)getByte();
				b1 = (byte)getByte();
				fullS += "\\\\"+ Byte.toString(b0) +"\\\\"+ Byte.toString(b1);
			}
			sValue =fullS;
			break;
		case AC_VR.SQ:
			sValue = "sqens";
			break;
			/*	boolean privateTag = ((tag>>16)&1)!=0;
		if (tag!=ICON_IMAGE_SEQUENCE && !privateTag)
			break;		*/
			// else fall through and skip icon image sequence or private sequence
		default:
			skip((long)m_nElementLength);
			sValue = "defult";
		}
		
		
		
		sValue = sValue.trim();
		
		if(sValue.equals(""))
			sValue = "-1";

		return sValue;
	}
	
	
	
	
	String getString(int length) throws IOException {
		byte[] buf = new byte[length];
	
		for(int i=0; i<length;i++)
		{
			buf[i] = (byte)getByte();
		}
		
		//location += length;
		
		String tmp = new String(buf);
		String newTmp = tmp.replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");
		return newTmp;
	}
  
	int getByte() throws IOException {
		int b = m_bisInputStream.read();
		if (b ==-1) 
		{
			 m_flagFileEnd = true;
			//throw new IOException("unexpected EOF");
		
		}
	//	++location;
		return b;
	}

	int getShort() throws IOException {
		int b0 = getByte();
		int b1 = getByte();
		if (m_bLittleEndian)
			return ((b1 << 8) + b0);
		else
			return ((b0 << 8) + b1);
	}
  
	final int getInt() throws IOException {
		int b0 = getByte();
		int b1 = getByte();
		int b2 = getByte();
		int b3 = getByte();
		if (m_bLittleEndian)
			return ((b3<<24) + (b2<<16) + (b1<<8) + b0);
		else
			return ((b0<<24) + (b1<<16) + (b2<<8) + b3);
	}

	double getDouble() throws IOException {
		int b0 = getByte();
		int b1 = getByte();
		int b2 = getByte();
		int b3 = getByte();
		int b4 = getByte();
		int b5 = getByte();
		int b6 = getByte();
		int b7 = getByte();
		long res = 0;
		if (m_bLittleEndian) {
			res += b0;
			res += ( ((long)b1) << 8);
			res += ( ((long)b2) << 16);
			res += ( ((long)b3) << 24);
			res += ( ((long)b4) << 32);
			res += ( ((long)b5) << 40);
			res += ( ((long)b6) << 48);
			res += ( ((long)b7) << 56);         
		} else {
			res += b7;
			res += ( ((long)b6) << 8);
			res += ( ((long)b5) << 16);
			res += ( ((long)b4) << 24);
			res += ( ((long)b3) << 32);
			res += ( ((long)b2) << 40);
			res += ( ((long)b1) << 48);
			res += ( ((long)b0) << 56);
		}
		return Double.longBitsToDouble(res);
	}
    
	float getFloat() throws IOException {
		int b0 = getByte();
		int b1 = getByte();
		int b2 = getByte();
		int b3 = getByte();
		int res = 0;
		if (m_bLittleEndian ) {
			res += b0;
			res += ( ((long)b1) << 8);
			res += ( ((long)b2) << 16);
			res += ( ((long)b3) << 24);     
		} else {
			res += b3;
			res += ( ((long)b2) << 8);
			res += ( ((long)b1) << 16);
			res += ( ((long)b0) << 24);
		}
		return Float.intBitsToFloat(res);
	}


}
