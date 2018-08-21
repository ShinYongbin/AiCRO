package AC_DicomData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.sun.javafx.applet.Splash;
import com.sun.javafx.scene.layout.region.SliceSequenceConverter;

import AC_DicomIO.AC_DicomReader;
import AC_DicomIO.AC_Tag;

import SYB_LIB.SYBFileIO;
import SYB_LIB.SYBTOOLS;
import SYB_LIB.StringTOOL;
import javafx.scene.web.WebEngine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;





public class AC_SimpleSeries {
	
	private String sROIFileIncoding = "MS949";
	private String sROIFileSplit = "_";
	
	
//	private Vector<double[] > m_SignalData = new Vector<double[]>();
//	private Vector<Integer> m_InstanceNum = new Vector<Integer>();
	private final int iSaveImgSize = 512; 
	
	
	private File[] m_FilePath = null;
	private BufferedImage m_FirstImage = null;
	private AC_DicomInfo m_DicomInfo = new AC_DicomInfo();
	private double[] m_darrSliceLocation = null;
	private Map<Integer, List<AC_ROI>> m_ROIs = new HashMap<Integer, List<AC_ROI>>();
	
	private int[] m_chkTag = {AC_Tag.WindowCenter, AC_Tag.WindowWidth, AC_Tag.SeriesInstanceUID}; 
	

	
	
	
	private int m_nTotalSlice =0;
	
	////web ACROI - parameter
	private String m_sUID = "";
	private String m_sKey = "";
	private String m_sProtocolname = "";
	private String m_sUserID = "";
	private String m_sAicroAppData = "";
	
	private boolean m_bChkWindow = false;
	
	private int m_nROIlimit = 1;
	
	
	private ImageIcon m_Thumb; 
	//	private int m_nTotalSlice=0;
	
	private  int m_ID =-1;
	private final int THUMH_SZIE =30;
	
	private double m_dMax = 0;
	private double m_dMin = 0;

	
	
	
	public AC_SimpleSeries() {
		// TODO Auto-generated constructor stub
		

	}
	
	public void setWebParamter(String sUID ,String skey,String sProtocolname,String sUserID, String sAicroAppAddData)
	{
		 m_sUID = sUID;         
	    m_sKey = skey;         
	     m_sProtocolname = sProtocolname;
	     m_sUserID = sUserID;
	     m_sAicroAppData = sAicroAppAddData;
	}
	
	public void addDicomInfo(int inpouTag, String vlaue)
	{
		m_DicomInfo.setValue(inpouTag, vlaue);
	}
	
	
	public void setSliceLocation(double[] input)
	{
		m_darrSliceLocation = input;
	}
	


	public String getAicroAppData()
	{
		return m_sAicroAppData ;
	}
	
	public void setAicroAppData(String input)
	{
		m_sAicroAppData = input ;
	}
	
	public String getUserID()
	{
		return m_sUserID ;
	}
	
	public String getUID()
	{
		return m_sUID;
		
	}
	public String getKey()
	{
		return m_sKey;
		
	}
	public String getProtocolname()
	{
		return m_sProtocolname;
		
	}
	public void setDicomInfo(AC_DicomInfo dicomInfo, double[] SingalData)
	{
		m_DicomInfo = dicomInfo;
		
		calMinMax(SingalData);
		chkDicomInfo();

		if(AC_DataType.MR == AC_DataType.chkModality(getString(AC_Tag.Modality)))
		{
			m_FirstImage  = AC_DataConverter.FastSignal2bffImg(SingalData, getInt(AC_Tag.Columns), 
					getInt(AC_Tag.Rows),getDouble(AC_Tag.WindowCenter), getDouble(AC_Tag.WindowWidth),
					getInt(AC_Tag.SamplesperPixel),2);
			System.out.println("WC : " +getDouble(AC_Tag.WindowCenter)+ " WW : "+getDouble(AC_Tag.WindowWidth)  );
		}else if(AC_DataType.CT == AC_DataType.chkModality(getString(AC_Tag.Modality)))
		{
			m_FirstImage  = AC_DataConverter.FastSignal2bffImg(SingalData, getInt(AC_Tag.Columns), 
					getInt(AC_Tag.Rows),200, 1000,getInt(AC_Tag.SamplesperPixel),2);
		}else if(AC_DataType.US == AC_DataType.chkModality(getString(AC_Tag.Modality)))
		{
			m_FirstImage  = AC_DataConverter.FastSignal2bffImg(SingalData, getInt(AC_Tag.Columns), 
					getInt(AC_Tag.Rows),126, 256,getInt(AC_Tag.SamplesperPixel),2);
		}
			

		m_Thumb = new ImageIcon(m_FirstImage);
		
	}
	
	
	public void reDcmTub(AC_DicomInfo dicomInfo,double[] SingalData)
	{
		//m_DicomInfo = dicomInfo;
		
		
		
		calMinMax(SingalData);
		chkDicomInfo();

		if(AC_DataType.MR == AC_DataType.chkModality(dicomInfo.getString(AC_Tag.Modality)))
		{
			m_FirstImage  = AC_DataConverter.FastSignal2bffImg(SingalData, dicomInfo.getInt(AC_Tag.Columns), 
					dicomInfo.getInt(AC_Tag.Rows),dicomInfo.getDouble(AC_Tag.WindowCenter), dicomInfo.getDouble(AC_Tag.WindowWidth),
					dicomInfo.getInt(AC_Tag.SamplesperPixel),2);
			System.out.println("WC : " +dicomInfo.getDouble(AC_Tag.WindowCenter)+ " WW : "+dicomInfo.getDouble(AC_Tag.WindowWidth)  );
		}else if(AC_DataType.CT == AC_DataType.chkModality(dicomInfo.getString(AC_Tag.Modality)))
		{
			m_FirstImage  = AC_DataConverter.FastSignal2bffImg(SingalData, dicomInfo.getInt(AC_Tag.Columns), 
					dicomInfo.getInt(AC_Tag.Rows),200, 1000,dicomInfo.getInt(AC_Tag.SamplesperPixel),2);
		}else if(AC_DataType.US == AC_DataType.chkModality(dicomInfo.getString(AC_Tag.Modality)))
		{
			m_FirstImage  = AC_DataConverter.FastSignal2bffImg(SingalData, dicomInfo.getInt(AC_Tag.Columns), 
					dicomInfo.getInt(AC_Tag.Rows),126, 256,dicomInfo.getInt(AC_Tag.SamplesperPixel),2);
		}
			

		m_Thumb = new ImageIcon(m_FirstImage);
		
	}
	public void setSeriesPath(File[] filePath)
	{
		String[] Stplit = filePath[0].getAbsolutePath().split("\\\\");
		String tmpPath = "";
		for(int i=0; i<Stplit.length-2;i++)
			tmpPath += Stplit[i]+"\\";
		tmpPath += Stplit[Stplit.length-2]+"_image";
		System.out.println("path : " +tmpPath);
		
		if(! new File(tmpPath).exists())
		{
			m_FilePath = filePath;
			m_nTotalSlice = filePath.length;
			return;
		}
			
		 File[] fnewFile = new File(tmpPath).listFiles();

		
		m_FilePath = fnewFile;
		m_nTotalSlice = fnewFile.length;
		
		 AC_DicomReader dcmio = new AC_DicomReader(fnewFile[0]);
		 AC_DicomInfo dcmInfo = new AC_DicomInfo();

		 byte[] bPixelData;
		 double[] dSingnalData = null;
		try {
			bPixelData = dcmio.getDicomInfo(dcmInfo);
			
			dSingnalData = AC_DataConverter.DCMPixelData2Singnal(bPixelData, dcmInfo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		reDcmTub(dcmInfo, dSingnalData);
	}
	public BufferedImage getImage()
	{
		return m_FirstImage;
	}
	
	public File getFile(int idx)
	{
		return m_FilePath[idx];
	}
	
	public int getID()
	{
		return m_ID;
	}
	public void setID(int input)
	{
		m_ID = input;
	}
	
	
	public int getTotalSliceNum()
	{
		return m_nTotalSlice;
	}

	public ImageIcon getThumbImgIcon()
	{
		return m_Thumb;
	}
	
	public int size()
	{
		return m_nTotalSlice;
	}
	
	public String getString(int iTag)
	{
		return m_DicomInfo.getString(iTag);
	}
	
	public double getDouble(int iTag)
	{
		return m_DicomInfo.getDouble(iTag);
	}
	public int getInt(int iTag)
	{
		return m_DicomInfo.getInt(iTag);
	} 
	
	public  void addROI(int nSliceN,AC_ROI inpuRoi)
	{
		if(m_ROIs.size()==m_nROIlimit)
		{
			m_ROIs.clear();
		}
		
		
		List<AC_ROI> tmpROIs = m_ROIs.get(nSliceN);
		
		if(tmpROIs==null)
			tmpROIs = new ArrayList<>();
		
		
		if(tmpROIs.size()==m_nROIlimit)
		{
			tmpROIs.remove(tmpROIs.size()-1);
		}

		inpuRoi.setID(tmpROIs.size()+1);
		tmpROIs.add(inpuRoi);

	
		m_ROIs.put(nSliceN, tmpROIs);
		
		tmpROIs =null;
	}
	public AC_ROI[] getROIs(int nSliceN)
	{
		List<AC_ROI> tmpROIs = m_ROIs.get(nSliceN);
		if(tmpROIs==null)
			return null;
		 AC_ROI[] output = tmpROIs.toArray(new AC_ROI[tmpROIs.size()]);
		
		return output;
	}
	
	public AC_ROI getOneROI()
	{
		Collection<Integer> k = m_ROIs.keySet();
		Iterator<Integer> itr = k.iterator();
		
		int iTxtMarign = iSaveImgSize/2;
		
		while(itr.hasNext())
		{
			int iNowSlice = itr.next();
			List<AC_ROI> tmpROIs = m_ROIs.get(iNowSlice);
			 AC_ROI output = tmpROIs.toArray(new AC_ROI[tmpROIs.size()])[0];
			return output;
		}
		
		return null;
	}
	
	
	
	public boolean hasROIs()
	{
		if(m_ROIs.size() == 0)
			return false;
		else
			return true;
		
	}
	
	public void removeROI(int nSliceN)
	{

		if(nSliceN==-1)
		{m_ROIs.clear();
			m_ROIs = new HashMap<Integer, List<AC_ROI>>(); 
			
			return;
		}
		List<AC_ROI> tmpROIs = m_ROIs.get(nSliceN);
		if(tmpROIs==null)
			tmpROIs = new ArrayList<>();
		
		tmpROIs.removeIf(roi -> roi.isSelected()==true);
		
		if(tmpROIs.size() == 0)
		{
			m_ROIs.clear();
			return;
		}
		
		
		for(int i=0;i<tmpROIs.size();i++)
			tmpROIs.get(i).setID(i+1);
		
		

		m_ROIs.put(nSliceN, tmpROIs);
		

	}

	
	public void  saveROIsImg(String sSaveDirPath)
	{
		Collection<Integer> k = m_ROIs.keySet();
		Iterator<Integer> itr = k.iterator();
		
		
		int iWidth = getInt(AC_Tag.Columns) ;//iSaveImgSize/2;
		int iHeight = getInt(AC_Tag.Rows) ;//iSaveImgSize/2;
		int iTxtMarign = getInt(AC_Tag.Columns)/2 ;//iSaveImgSize/2;
		
		while(itr.hasNext())
		{
			//System.out.println("=======key========"+itr.next());
			int iNowSlice = itr.next();
			List<AC_ROI> tmpROIs = m_ROIs.get(iNowSlice);
			
			try {
				double[] dNowSignal = AC_DataConverter.DCMFile2Signal(getFile(iNowSlice));
				BufferedImage SvaeImg = new BufferedImage(iWidth+iTxtMarign, iHeight,  BufferedImage.TYPE_INT_RGB);
				BufferedImage bfDCMImg = 	AC_DataConverter.FastSignal2bffImg(dNowSignal ,
						 getInt(AC_Tag.Columns),   getInt(AC_Tag.Rows),1000,
						200,getInt(AC_Tag.SamplesperPixel),1 );			
				Graphics2D g2 = SvaeImg.createGraphics();
				
				//bfDCMImg = SYBTOOLS.resizeBufferedImage(bfDCMImg, iSaveImgSize, iSaveImgSize);
				g2.drawImage(bfDCMImg, null, iTxtMarign,0);

				int iYstep = StringTOOL.getStringHight(g2, "ABIO갃");
				
				for(int i=tmpROIs.size()-1; i>=0;i--)
				{
					AC_ROI tmpRoi = tmpROIs.get(i);
					tmpRoi.drawSaveROI(g2, iTxtMarign);
					g2.drawString(tmpRoi.getMeasurString(), 0, iHeight-((tmpROIs.size()-i)*iYstep));
					//tmpRoi.getsi
				}
				
				g2.drawString("ROI MearMent", 0, iHeight-((tmpROIs.size()+1)*iYstep));
				///////////////setTxt
				
				
				/*g2.drawString("Patient ID : "+getString(AC_Tag.PatientID), 0, 1*iYstep);
				g2.drawString("SeriesNum : "+getString(AC_Tag.SeriesNumber), 0, 2*iYstep);
				g2.drawString("Patient ID : "+getString(AC_Tag.PatientID), 0, 3*iYstep);
				g2.drawString("Patient ID : "+getString(AC_Tag.PatientID), 0, 4*iYstep);
				g2.drawString("Patient ID : "+getString(AC_Tag.PatientID), 0, 5*iYstep);
				g2.drawString("Patient ID : "+getString(AC_Tag.PatientID), 0, 6*iYstep);
				g2.drawString("Patient ID : "+getString(AC_Tag.PatientID), 0, 7*iYstep);*/
				
				
				
				
				
				////draw infro
				
				String sSaveFilePath  = "";
			if(true)	
				 sSaveFilePath = sSaveDirPath+File.separator+getString(AC_Tag.PatientID)+"_"+(iNowSlice+1)+".png";
			else
				 sSaveFilePath = sSaveDirPath;
				 //sSaveFilePath = sSaveDirPath+File.separator+getString(AC_Tag.PatientID)+"_"+(iNowSlice+1)+".png";
			
				ImageIO.write(SvaeImg, "JPG", new File(sSaveFilePath));
	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
				
	}
	
	private void calMinMax(double[] arrdSignal)
	{
		double[] dMinMax = SYBTOOLS.getMinMax(arrdSignal);
		
		for (int i=0; i<arrdSignal.length; i++) {
			if(arrdSignal[i]>2000)
				System.out.println("!!!!Max : "+arrdSignal[i]);
		}
		m_dMin =  dMinMax[0];
		m_dMax = dMinMax[1];
		
		System.out.println("Max : "+m_dMax + "Min"+ m_dMin);
	}
	
	private void chkDicomInfo()
	{
		for(int tmpTag : m_chkTag)
		{
			if(getString(tmpTag)=="N/A")
			{
				if(tmpTag == AC_Tag.WindowCenter)
				{
					double dWindowCenter =m_dMax -(Math.abs(m_dMin)+Math.abs(m_dMax))/2;
					//double dWindowWidth = dWindowCenter;
					String sTmp = Double.toString(dWindowCenter);;
					
					System.out.println("WC : Value"+sTmp);
					m_DicomInfo.setValue(tmpTag, sTmp);
				}
				else if(tmpTag == AC_Tag.WindowWidth)
				{
					double dWindowWidth = ((m_dMax)-(m_dMin));
					//double dWindowWidth = dWindowCenter;
					String sTmp = Double.toString(dWindowWidth);;
					
					System.out.println("WW : Value"+sTmp);
					m_DicomInfo.setValue(tmpTag, sTmp);
				}
			
			}
			else if (tmpTag==AC_Tag.SeriesInstanceUID)
			{
				m_sUID = m_DicomInfo.getString(tmpTag);
			}
		}
		
	}
	
	public  void readRoiFile(String inputFile) throws IOException 
	{
		//chk UID
		String chkUID = SYBFileIO.getFileNameWithOutExtend(inputFile);
		System.out.println(chkUID);
		System.out.println(m_sUID);
		if(!chkUID.equals(m_sUID))
		{
			System.out.println("ROI File Reader : Not Matche UID");
			
			JOptionPane.showMessageDialog(null, "ROI파일과 DCM파일의 UID가 일치하지 않습니다.", "ROI Open Error", JOptionPane.ERROR_MESSAGE);

		//	alter
			return;
		}
		
		this.removeROI(-1);
		
		FileInputStream fisInput = new FileInputStream(inputFile);
		InputStreamReader isrInput = new InputStreamReader(fisInput,sROIFileIncoding);
		BufferedReader bffrInput = new BufferedReader(isrInput);
		String sLine ="";
		while ((sLine = bffrInput.readLine()) != null)
		{
			String[] sSplit = sLine.split(sROIFileSplit);

			int iSliceNum = Integer.parseInt(sSplit[0]);
			int iID = Integer.parseInt(sSplit[1]);
			int[] iPos = {Integer.parseInt(sSplit[2]),Integer.parseInt(sSplit[3]),Integer.parseInt(sSplit[4]),Integer.parseInt(sSplit[5])};//sx, sy, iw, ih,
			int iMod = Integer.parseInt(sSplit[6]);
			
			System.out.println("iSliceNum "+iSliceNum);
			System.out.println("iID "+iID);

			System.out.println("iPos "+iPos[0]+"_"+iPos[1]+"_"+iPos[2]+"_"+iPos[3]+"_");

			System.out.println("iMod "+iMod);

			



			List<AC_ROI> tmpROIs = m_ROIs.get(iSliceNum);
			if(tmpROIs==null) {
				tmpROIs = new ArrayList<>();
			}
			AC_ROI newROI = new AC_ROI(iPos[0],iPos[1],iPos[2],iPos[3],iMod);

			newROI.setID(iID);
			tmpROIs.add(newROI);


			m_ROIs.put(iSliceNum, tmpROIs);

			tmpROIs =null;
		}
		//chk ID
	
		System.out.println(sLine);
		bffrInput.close();
	}
	
	public  String saveRoiFile(String inputPath) throws IOException  
	{
		
		String outputFile = inputPath+File.separator+m_sUID+".acroi";
		
		FileOutputStream fosOutput = new FileOutputStream(outputFile);
		OutputStreamWriter oswInput = new OutputStreamWriter(fosOutput,sROIFileIncoding);
		BufferedWriter bffrInput = new BufferedWriter(oswInput);
		
		Collection<Integer> k = m_ROIs.keySet();
		Iterator<Integer> itr = k.iterator();
		
		
		while(itr.hasNext())
		{
			//System.out.println("=======key========"+itr.next());
			int iNowSlice = itr.next();
			List<AC_ROI> tmpROIs = m_ROIs.get(iNowSlice);
			
			int iSliceNum = iNowSlice;

			for(AC_ROI tmp :  tmpROIs.toArray(new AC_ROI[tmpROIs.size()]))
			{

				int iID = tmp.getID();
				int[] iPos = tmp.getPos();//sx, sy, iw, ih,
				int iMod = tmp.getMode();

				String sLine = iSliceNum+sROIFileSplit + 
						iID+sROIFileSplit+ 
						iPos[0]+sROIFileSplit+ iPos[1]+sROIFileSplit+ iPos[2]+sROIFileSplit+ iPos[3]+sROIFileSplit+ 
						iMod;
				bffrInput.write(sLine);
				bffrInput.newLine();
			}
		}
		bffrInput.close();
		
		return outputFile;

	}
	
	public int calSliceLocationNum(double dRefSliceLocation)
	{
		if(m_darrSliceLocation.length==1 || m_darrSliceLocation.length==0)
			return 0;
		
		double dMin = 1000.0;
		int iMinIdx = 0;
		int iMarge =0;
		double dMarge =m_darrSliceLocation[1]-m_darrSliceLocation[0];
		
		System.out.println("dRefSliceLocation :"+dRefSliceLocation);
		
		
		double dTmp1 = Math.abs((m_darrSliceLocation[0]-(dMarge))-dRefSliceLocation);

		if(dTmp1<dMin)
		{
			dMin = dTmp1;
			iMinIdx = 0;
			iMarge = -1;
			System.out.println("dmin : "+dMin+ ", iMinIdx"+iMinIdx);
		}
		
		
		for(int i=0; i<m_darrSliceLocation.length;i++)
		{
			
			
					
			double dTmp = Math.abs((m_darrSliceLocation[i])-dRefSliceLocation);

			if(dTmp<dMin)
			{
				dMin = dTmp;
				iMinIdx = i;
				iMarge = 0;
			}
		}
		
		if(iMarge==-1)
		{
			iMinIdx = m_nTotalSlice-1;
		}
		
		
		return iMinIdx;
	}
	
	public boolean haveROI()
	{
		if(m_ROIs.size() == 0)
			return false;
		else 
			return true;
		
	}
	
	
	public double getSliceLocation(int idx)
	{
		return m_darrSliceLocation[idx];
	}


	
	
	
	
	
	

}
