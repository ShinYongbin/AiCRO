
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.xml.crypto.Data;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.StandardElementDictionary;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.VR;
import org.dcm4che3.image.BufferedImageUtils;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReader;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.DicomOutputStream;

import org.opencv.*;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.sun.corba.se.impl.encoding.CodeSetConversion.BTCConverter;

import org.opencv.core.Core;
import org.opencv.core.CvType;;


public class Dcm4cheV3io {
	
	static String FILE_SEP = File.separator;
	static Attributes m_aMetaData;
	static Attributes m_aAttribs;
	static float[] iHUPixelValue;// = new float[];
	static byte[] bPixel;
	static int m_iRow = 0;
	static int m_iColumn = 0;
	static int iPerPixel = 0;
	//static int [] iPixel;
	
	public float[] getHUPixelValue()
	{

		return iHUPixelValue;
		

	}
	
	public double[] getHUPixelValuedouble()
	{

		double[] retrundulbe = new double[iHUPixelValue.length];
		for(int i=0; i< iHUPixelValue.length;i++)
			retrundulbe[i] = (double)iHUPixelValue[i];
		
		return retrundulbe;
		

	}

	
	public void calHUPixelValue()
	{
 		float fRescaleIntercept  = (float) 0.0;
 		float fRescaleSlope  = (float) 0.0;
 		m_aAttribs.getFloat(Tag.RescaleSlope,fRescaleSlope);
 		fRescaleIntercept = (float)Float.parseFloat(m_aAttribs.getString(Tag.RescaleIntercept));
 		fRescaleSlope = (float) Float.parseFloat(m_aAttribs.getString(Tag.RescaleSlope));
 		iPerPixel =  Integer.parseInt(m_aAttribs.getString(Tag.SamplesPerPixel));
 		int iPixelRepresentation =  Integer.parseInt(m_aAttribs.getString(Tag.PixelRepresentation));
 		iHUPixelValue=new float[m_iRow*m_iColumn];
		
		for(int i=0; i<m_iRow*m_iColumn;i++)
		{

			/*int iFrontbyte = (int)bPixel[i*2];
			
			if(iFrontbyte<0)
				iFrontbyte = (int)(iFrontbyte+256);
			int iBackbyte = (int)bPixel[i*2+1]*256;*/
			
			int b1 = bPixel[i*2+1];
			int b0 = bPixel[i*2];		

			
			double tmp =0;
			
			if(b0<0)
				b0 = (int)(b0+256);

			if(iPixelRepresentation==0  && (b1<0))

				b1 = (int)(b1+256);




			tmp = ((b1 << 8) + b0);
		
			iHUPixelValue[i] = (int)tmp;
			iHUPixelValue[i] = (iHUPixelValue[i]*fRescaleSlope+fRescaleIntercept);

		}
		
	}
	
	
	public int[][] calRGBValue()
	{
 		float fRescaleIntercept  = (float) 0.0;
 		float fRescaleSlope  = (float) 0.0; 
 		
 		int[][] imageCanelOutput = new int[3][m_iColumn*m_iRow];
 	
 		
 		
 		BufferedImage output = new BufferedImage( m_iColumn,m_iRow,  BufferedImage.TYPE_INT_BGR);
 		//byte[] imagePixelData =( (DataBufferByte)output.getRaster().getDataBuffer()).getData();
 		int[] imagePixelData =( (DataBufferInt)output.getRaster().getDataBuffer()).getData();
		
 		for(int i=0; i<m_iColumn*m_iRow;i++)
		{
			//for(int j=0; j<m_iRow;j++)
		//	{
 			
 			int iR = 0;
			int iG = 0;
			int iB = 0;
			
				if(iPerPixel==1)
				{
					iR = (int)bPixel[(i)+0];   
					iG = (int)bPixel[(i)+0];   
					iB = (int)bPixel[(i)+0]; 
					
				}else
				{

					iR = (int)bPixel[(i*3)+0];   
					iG = (int)bPixel[(i*3)+1];   
					iB = (int)bPixel[(i*3)+2];   

				}
				//int idx = (i*m_iRow)+(j);
		
				if(iB<0)
					iB = (iB+256);
				if(iG<0)
					iG = (iG+256);
				if(iR< 0)
					iR = (iR+256);
				
				imageCanelOutput[0][i] = iR;
				imageCanelOutput[1][i] = iG;
				imageCanelOutput[2][i] = iB;
					
				
				/*System.out.println("R : "+iR+ "  G : "+iG+"  B : "+iB);
				Color col = new Color(iR, iB,iG);
				output.setRGB(i, j, col.getRGB());*/
			
			
				//imagePixelData[i] = (byte) ((byte) (bPixel[(i*3+2)])<<16| (byte) (bPixel[(i*3)+1])<<8|(byte) (bPixel[(i*3)]));
				imagePixelData[i] =  iB<<16 | iG<<8 | iG;
				//}
			//	output[idx2] = 0<<16| 0<<8|0;
			/*	if(bPixel[(i*3)]+bPixel[(i*3+1)]+bPixel[(i*3)+2]>300)
					System.out.println("R : "+bPixel[i*3]+ "  G : "+bPixel[i*3+1]+"  B : "+bPixel[i*3+2]);*/
			
		
			//}
		}
 		
		
		/*try {
			ImageIO.write(output, "PNG", new File("D:\\intest_Sort\\10525645_20171213_DCM\\1234.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return imageCanelOutput;
		
		
		 
	}
	
	
	public void saveTxTfile(String filename)
	{
		filename ="D:"+FILE_SEP+"98_data"+FILE_SEP+"test"+FILE_SEP+"30375736_WYK_CT"+FILE_SEP+"100000000.txt";
        try{
                         
            // BufferedWriter 와 FileWriter를 조합하여 사용 (속도 향상)
            BufferedWriter fw = new BufferedWriter(new FileWriter(filename, true));
            
            for(int i=0; i<m_iRow*m_iColumn;i++)
            {
            	String txt = Float.toString(iHUPixelValue[i])+" ";
            	// 파일안에 문자열 쓰기

            	fw.write(txt);
            	fw.flush();
            }
 
            // 객체 닫기
            fw.close();
             
             
        }catch(Exception e){
            e.printStackTrace();
        }



	}
	public Dcm4cheV3io(File file) throws Exception
	{
		
		readDicomFile(file.getAbsolutePath());
	}
	
	public Dcm4cheV3io(String filePath) throws Exception
	{
		
	//	loadOpenCV_Lib();
		readDicomFile(filePath);
		//calHUPixelValue();
		//saveTxTfile("");
		

		
	//	img1.put(0,0,tmp);
		//Imgcodecs.imwrite(outputFloerName, img1);
		
		
				
		
	
	}
	
	
	public void readDicomFile(String filePath)
	{
	 File fInputFile = new File(filePath);
		

		DicomInputStream dis;
		try {
			dis = new DicomInputStream(fInputFile);
		     m_aMetaData = dis.readFileMetaInformation();
		     m_aAttribs = dis.readDataset(-1,-1);
		     
		   //  System.out.println("sdfsdf");
		     
		    bPixel = m_aAttribs.getBytes(Tag.PixelData);
		
		     m_iRow = Integer.parseInt(m_aAttribs.getString(Tag.Rows));
		     m_iColumn = Integer.parseInt(m_aAttribs.getString(Tag.Columns));
		 

		    
		    dis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void wirteDicomFile(String filePath)
	{
	    File fi = new File(filePath);
	    String chkdir = "";
	    for(int i=0;i<fi.getAbsolutePath().split("\\\\").length-1;i++)
	    {
	    	chkdir+= fi.getAbsolutePath().split("\\\\")[i]+File.separator;
	    }
	    
	    
	    if(!new File(chkdir).exists())
	    {
	    	new File(chkdir).mkdirs();
	    }
	    DicomOutputStream dos;
		try {
			dos = new DicomOutputStream(fi);
		    dos.writeDataset(m_aMetaData, m_aAttribs);
		    dos.finish();
		    dos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getDicomTag2String(int inputTag)
	{
		String sReTag = "";
		sReTag = m_aAttribs.getString(inputTag);
		
		//int i = 0x00100010;
		//sReTag = m_aAttribs.getString(i);
		if(sReTag==null)
			sReTag ="N/A";
		
		return sReTag;
	
	}
	
	public void setDicomTag2String(int inputTag, String inputString)
	{
		

		 VR vVR = StandardElementDictionary.vrOf(inputTag, m_aAttribs.getPrivateCreator(inputTag));

	//	 m_aMetaData.remove(inputTag);
		m_aAttribs.remove(inputTag);
		 m_aAttribs.setString(inputTag, vVR, inputString);
		
		// m_aMetaData.setString(inputTag, vVR, inputString);
		
		 
		
	}
	

	

	
	

}
