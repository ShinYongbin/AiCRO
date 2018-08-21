package AC_DicomData;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;


import AC_DicomIO.AC_DicomReader;
import AC_DicomIO.AC_Tag;
import SYB_LIB.SYBTOOLS;



public class AC_DataConverter {
	
	
	 final static boolean littleEndian = true;
	 
	 
	 public static String margeDataTime(String sDate, String sTime)
	 {
		 if(sDate=="N/A" || sTime == "N/A")
		 {
			 System.out.println("Series Data N/A");
			 
			 return "";
		 }
		 
		 String output;
		 
		String sHour = sTime.substring(0,2);
		String sMin = sTime.substring(2,4);
		String sSec = sTime.substring(4,6);
		String sYear = sDate.substring(0,4);
		String sMonth = sDate.substring(4,6);
		String sDay = sDate.substring(6,8);
		
		
		if(Integer.parseInt(sHour)>=0&& Integer.parseInt(sHour)<=11)
			sHour = "오전 "+sHour;
		else if(Integer.parseInt(sHour)>12&& Integer.parseInt(sHour)<=23)
		{
			int iHour = Integer.parseInt(sHour);
			sHour = "오후 "+String.format("%2d", iHour);
		}
		else 
			sHour = "오후 "+sHour;
		
		output = sYear+"-"+sMonth+"-"+sDay
				+" "+sHour+":"+sMin+":"+sSec;
		 
		 
		return output;
		 
	 }
	 
	 public static AC_SimpleSeries DCMFile2ACSimpleSeries(File[] fDCMFiles) throws IOException
	 {
		 AC_SimpleSeries output = new AC_SimpleSeries();
		
		 
	
			 AC_DicomReader dcmio = new AC_DicomReader(fDCMFiles[0]);
			 AC_DicomInfo dcmInfo = new AC_DicomInfo();

			 byte[] bPixelData = dcmio.getDicomInfo(dcmInfo);
			double[] dSingnalData =
					AC_DataConverter.DCMPixelData2Singnal(bPixelData, dcmInfo);
			
			output.setDicomInfo(dcmInfo,dSingnalData);
			output.setSeriesPath(fDCMFiles);
			
			double [] darrSliceLocation = new double[fDCMFiles.length];
	
			
			for(int i=0; i<fDCMFiles.length;i++)
			{
				 dcmio = new AC_DicomReader(fDCMFiles[i]);
				 String tmp[] = dcmio.getTagValue(AC_Tag.ImagePositionPatient).split("\\\\");
				 
				 
				// System.out.println(tmp[2]);
				 
				 if(tmp != null)
					 darrSliceLocation[i] =  Double.parseDouble(tmp[2]);
				
			}
			output.setSliceLocation(darrSliceLocation);

			
			
			dcmio.close();
			dcmInfo=null;
			dSingnalData =null;
			
		
		return output;

	 }
	 
	 
	 public static BufferedImage DCMFile2BuffImg(File fDCMFiles  ) throws IOException
	 {
		 
		 AC_DicomReader dcmio = new AC_DicomReader(fDCMFiles);
		 AC_DicomInfo dcmInfo = new AC_DicomInfo();
		 
		 byte[] bPixelData = dcmio.getPixel(dcmInfo);
		 
		 BufferedImage  output = DCMPixelData2BuffImg(bPixelData,dcmInfo);
		 bPixelData =null;
		 
		 dcmio.close();
		 bPixelData = null;
		// dcmio =null;
		 System.gc();
		 
		return output;
		 
		

	 }
	 
	 
	 public static double[] DCMFile2Signal(File fDCMFiles  ) throws IOException
	 {
		 
		 AC_DicomReader dcmio = new AC_DicomReader(fDCMFiles);
		 AC_DicomInfo dcmInfo = new AC_DicomInfo();
		 
		 byte[] bPixelData = dcmio.getPixel(dcmInfo);
		 
		 double[]  output = DCMPixelData2Singnal(bPixelData,dcmInfo);
		 
		 dcmio.close();
		// bPixelData = null;
		// bPixelData = null;
		// dcmio =null;
		// System.gc();
		return output;
		 
		

	 }
	 
	 public static double[] DCMFile2Signal(File fDCMFiles,double[] getParamater,int iModaliry ) throws IOException
	 {
		 
		 AC_DicomReader dcmio = new AC_DicomReader(fDCMFiles);
		 AC_DicomInfo dcmInfo = new AC_DicomInfo();
		 
		 byte[] bPixelData = dcmio.getPixel(dcmInfo);
		 
		 double[]  output = DCMPixelData2Singnal(bPixelData,dcmInfo);
		 
		 getParamater[0] = dcmInfo.getDouble(AC_Tag.SliceThickness);
		 getParamater[1] = dcmInfo.getDouble(AC_Tag.SliceLocation);
		 
		 
		 
		 if(dcmInfo.getString(AC_Tag.WindowCenter) =="N/A" || dcmInfo.getString(AC_Tag.WindowWidth) =="N/A")
			{
			 double[] dMinMax = SYBTOOLS.getMinMax(output);
			
			 	getParamater[2]  =dMinMax[1] -(Math.abs(dMinMax[0])+Math.abs(dMinMax[1]))/2;
			 //	System.out.println("WC : Value"+getParamater[3]);
		
				//dcmInfo.setValue(AC_Tag.WindowCenter, sTmp);
			
				 getParamater[3]= ((dMinMax[1])-(dMinMax[0]));
				 
				// System.out.println("WW : Value"+getParamater[2]);
		
		//		dcmInfo.setValue(tmpTag, sTmp);
			}
		 else
		 {
			 getParamater[2] = dcmInfo.getDouble(AC_Tag.WindowCenter);
			 getParamater[3] = dcmInfo.getDouble(AC_Tag.WindowWidth);
		 }
		 
		 
	
		 
		 
		 if(iModaliry == AC_DataType.CT)
		 {
			 getParamater[4] = dcmInfo.getDouble(AC_Tag.XRayTubeCurrent);
			 getParamater[5] = dcmInfo.getDouble(AC_Tag.KVP);
		 }else if(iModaliry == AC_DataType.MR) {
			 
			
				 getParamater[4] = dcmInfo.getDouble(AC_Tag.MagneticFieldStrength);
				 getParamater[5] = dcmInfo.getDouble(AC_Tag.RepetitionTime);
				 getParamater[6] = dcmInfo.getDouble(AC_Tag.EchoTime);
			
		}
		 getParamater[7] = dcmInfo.getDouble(AC_Tag.SamplesperPixel);
		 
		 getParamater[8] = dcmInfo.getDouble(AC_Tag.Rows);
		 getParamater[9] = dcmInfo.getDouble(AC_Tag.Columns);
		 
		 
		 
		 
		 dcmio.close();
		// bPixelData = null;
		// bPixelData = null;
		// dcmio =null;
		// System.gc();
		return output;
		 
		

	 }
	 
	 
	
	 public static double[] DCMPixelData2Singnal(byte[] abPixelData,
				int iRow, int iColumn, int iPixelRepresentation,
				int iBitsAllocated, int iBitsStored, int iSampleSperPixel,
				double dRescaleIntercept, double dRescaleSlope)
		{
			double[] adSignalData = new double[iRow*iColumn*iSampleSperPixel];
			
			//littleEndian = false;
			
			
			
			for(int i=0; i<iRow*iColumn*iSampleSperPixel;i++)
			{
				
				if(iBitsAllocated==8)
				{
					int b0 = abPixelData[i];	
					if(b0<0)
						b0 = (int)(b0+256);
					
					adSignalData[i] = b0*dRescaleSlope+dRescaleIntercept;	
				}
				
				if(iBitsAllocated==16)
				{
				
					int b1 = abPixelData[i*2+1];
					int b0 = abPixelData[i*2];		
		
					
					double tmp =0;
					if(littleEndian)
					{

						if(b0<0)
							b0 = (int)(b0+256);

						if(iPixelRepresentation==0  && (b1<0))

							b1 = (int)(b1+256);




						tmp = ((b1 << 8) + b0);
					}
					else
					{
						if(b1<0)
							b1 = (int)(b1+256);
					
						tmp = ((b0 << 8) + b1);
					}
					
					adSignalData[i] = tmp*dRescaleSlope+dRescaleIntercept;	
				}
				else if(iBitsAllocated==32)
				{
					int b3 = abPixelData[i*2+3];
					int b2 = abPixelData[i*2+2];	
					int b1 = abPixelData[i*2+1];
					int b0 = abPixelData[i*2];
					
					double tmp =0;
					if(littleEndian)
					{
						if(b0<0)
							b0 = (int)(b0+256);
						if(b1<0)
							b0 = (int)(b1+256);
						if(b2<0)
							b0 = (int)(b2+256);
						tmp = ((b3<<24) + (b2<<16) + (b1<<8) + b0);
					}
					else
					{

						if(b0<0)
							b3 = (int)(b3+256);
						if(b1<0)
							b2 = (int)(b2+256);
						if(b2<0)
							b1 = (int)(b1+256);
						tmp = ((b0<<24) + (b1<<16) + (b2<<8) + b3);     
					};
					adSignalData[i] = tmp*dRescaleSlope+dRescaleIntercept;	
				}
				
			}
			return adSignalData;
		}
		
	
	
	
		public static double[] DCMPixelData2Singnal(byte[] abPixelData, AC_DicomInfo inputData)
		{
			int irow =inputData.getInt(AC_Tag.Rows);
			int iColumn =inputData.getInt(AC_Tag.Columns);
			int iPixelRepresentation = inputData.getInt(AC_Tag.PixelRepresentation);
			int iBitsAllocated =inputData.getInt(AC_Tag.BitsAllocated);
			int iBitsStored =inputData.getInt(AC_Tag.BitsStored);
			int iSamplesperPixel =inputData.getInt(AC_Tag.SamplesperPixel);
			double dRescaleIntercept = 0.0;
			
			double dRescaleSlope = 1.0; 
			if(inputData.getDouble(AC_Tag.RescaleSlope) != -1.0)
				dRescaleSlope =inputData.getDouble(AC_Tag.RescaleSlope);
			if(inputData.getDouble(AC_Tag.RescaleIntercept)!= -1.0)
				dRescaleIntercept = inputData.getDouble(AC_Tag.RescaleIntercept);
			
			double[] output = DCMPixelData2Singnal(abPixelData, 
					irow, iColumn, iPixelRepresentation, 
					iBitsAllocated, iBitsStored,iSamplesperPixel, 
					dRescaleIntercept, dRescaleSlope);
			
			
			return output;
		}
	
	
	public static BufferedImage DCMPixelData2BuffImg(byte[] abPixelData, AC_DicomInfo inputData)
	{
		int iWidth =inputData.getInt(AC_Tag.Rows);
		int iHeight =inputData.getInt(AC_Tag.Columns);
		int iPixelRepresentation = inputData.getInt(AC_Tag.PixelRepresentation);
		int iBitsAllocated =inputData.getInt(AC_Tag.BitsAllocated);
		int iBitsStored =inputData.getInt(AC_Tag.BitsStored);
		double dRescaleIntercept = 0.0;
		
		double dRescaleSlope = 1.0; 
		if(inputData.getDouble(AC_Tag.RescaleSlope) !=0.0)
			dRescaleSlope =inputData.getDouble(AC_Tag.RescaleSlope);
		if(inputData.getDouble(AC_Tag.RescaleIntercept)!=0.0)
			dRescaleIntercept = inputData.getDouble(AC_Tag.RescaleIntercept);
		
		double dWindowCenter = inputData.getDouble(AC_Tag.WindowCenter);
		double dWindowWidth = inputData.getDouble(AC_Tag.WindowWidth);
		
		double imin = (int) (dWindowCenter-0.5-(dWindowWidth-1)/2);
		double imax =  (int) (dWindowCenter-0.5+(dWindowWidth-1)/2);
		
		Color cZero = new Color(0, 0, 0);
		Color c255 = new Color(255, 255,255);
		
		int iSampling = 2;
		
		if(iWidth+iHeight<=512+512)
		{
			iSampling =1;
		}
		
		BufferedImage output = new BufferedImage( iHeight/iSampling,  iWidth/iSampling,BufferedImage.TYPE_USHORT_GRAY);
		
		
		
		
	
		for(int i=0; i<iWidth/iSampling;i++)
		{
			for(int j=0; j<iHeight/iSampling;j++)
			{
				int idx = (i*iSampling)*iHeight+(j*iSampling);
				double tmpdcm = 0.0;
				
				if(iBitsAllocated==16)
				{
				
					int b1 = abPixelData[i*2+1];
					int b0 = abPixelData[i*2];		
		
					
					double tmp =0;
					if(littleEndian)
					{

						if(b0<0)
							b0 = (int)(b0+256);

						if(iPixelRepresentation==0  && (b1<0))

							b1 = (int)(b1+256);




						tmp = ((b1 << 8) + b0);
					}
					else
					{
						if(b1<0)
							b1 = (int)(b1+256);
					
						tmp = ((b0 << 8) + b1);
					}
					
					tmpdcm = tmp*dRescaleSlope+dRescaleIntercept;	
					
					
					
				}
				else if(iBitsAllocated==32)
				{
					int b3 = abPixelData[idx*2+3];
					int b2 = abPixelData[idx*2+2];	
					int b1 = abPixelData[idx*2+1];
					int b0 = abPixelData[idx*2];
					
					double tmp =0;
					if(littleEndian)
					{
						if(b0<0)
							b0 = (int)(b0+256);
						if(b1<0)
							b0 = (int)(b1+256);
						if(b2<0)
							b0 = (int)(b2+256);
						tmp = ((b3<<24) + (b2<<16) + (b1<<8) + b0);
					}
					else
					{

						if(b0<0)
							b3 = (int)(b3+256);
						if(b1<0)
							b2 = (int)(b2+256);
						if(b2<0)
							b1 = (int)(b1+256);
						tmp = ((b0<<24) + (b1<<16) + (b2<<8) + b3);     
					};
					tmpdcm = tmp*dRescaleSlope+dRescaleIntercept;	
				}
				
				
				
				
				
				
				
				if(tmpdcm<= imin)
					output.setRGB(j, i, cZero.getRGB());
				else if(tmpdcm> imax)
					output.setRGB(j, i, c255.getRGB());
				else {
					int tmp = (int) ((tmpdcm-imin)/(dWindowWidth)*255);
					//int tmp = (int) ((tmpdcm[idx]-(iwindowCenter-0.5))/(iWindowWidth-1)+0*255);
					Color col = new Color(tmp, tmp, tmp);
					output.setRGB(j, i, col.getRGB());
				}

			}
		}
		

		
		
		return output;
	}
	
	
	static public int[] Signal2Pixel(double[] inputSignal, double dwindowCenter, double dWindowWidth)
	{
		
		double[] tmpdcm = inputSignal.clone();
		int[] output = new int[inputSignal.length];
		
		int imin = (int) (dwindowCenter-0.5-(dWindowWidth-1)/2);
		
			for(int i=0; i<tmpdcm.length; i++)
		{
			/*if	output[i] = 0;
			else if(tmpdcm[i] > imax)
				output[i] = 255;
			else*/
				output[i] = (int) ((tmpdcm[i]-imin)/(dWindowWidth)*255);
		}
		return output;
		
	}
	
	
	static public BufferedImage FastSignal2bffImg(double[] inputSignal, 
			int iWidth, int iHeight	,double iwindowCenter, double iWindowWidth,int iSamplesperPixel ,int iSampling)
	{

		iSampling =1;
		double[] tmpdcm = inputSignal.clone();
	
		if(iSamplesperPixel==1)
		{
			BufferedImage output = new BufferedImage( iWidth,  iHeight, BufferedImage.TYPE_BYTE_GRAY);
			byte[] imagePixelData = ((DataBufferByte)output.getRaster().getDataBuffer()).getData();

			double imin = (int) (iwindowCenter-0.5-(iWindowWidth-1)/2);
			double imax =  (int) (iwindowCenter-0.5+(iWindowWidth-1)/2);


			for(int i=0; i<iWidth*iHeight;i++)
			{
					int idx = i;

					if(tmpdcm[idx]<= imin)
						imagePixelData[idx] =0;
					else if(tmpdcm[idx] > imax)
						imagePixelData[idx] = (byte) 255;
					else {
						byte tmp = (byte) ((tmpdcm[idx]-imin)/(iWindowWidth)*255);
						//int tmp = (int) ((tmpdcm[idx]-(iwindowCenter-0.5))/(iWindowWidth-1)+0*255);
						imagePixelData[idx] = tmp;
					}

				
			}
			tmpdcm =null;
			return output;

		}else if(iSamplesperPixel==3)
		{
			BufferedImage output = new BufferedImage( iWidth,  iHeight,BufferedImage.TYPE_INT_RGB);
			int[] imagePixelData = ((DataBufferInt)output.getRaster().getDataBuffer()).getData();

			double imin = (int) (iwindowCenter-0.5-(iWindowWidth-1)/2);
			double imax =  (int) (iwindowCenter-0.5+(iWindowWidth-1)/2);


			for(int i=0; i<iWidth*iHeight;i++)
			{
				int iR =  (int)inputSignal[(i*3)+0];   
				int iG = (int)inputSignal[(i*3)+1];   
				int iB = (int)inputSignal[(i*3)+2];   

				imagePixelData[i] =  iR<<16 | iG<<8 | iB;
			}
			tmpdcm =null;
			return output;


		}

		return null;
	}
	
	
	static public BufferedImage FastSignal2bffImgNmask(double[] inputSignal, 
			int iWidth, int iHeight	,double iwindowCenter, double iWindowWidth, int[] bMask)
	{

		
		double[] tmpdcm = inputSignal.clone();


		BufferedImage output = new BufferedImage( iWidth,  iHeight,BufferedImage.TYPE_INT_RGB);
		int[] imagePixelData = ((DataBufferInt)output.getRaster().getDataBuffer()).getData();

		double imin = (int) (iwindowCenter-0.5-(iWindowWidth-1)/2);
		double imax =  (int) (iwindowCenter-0.5+(iWindowWidth-1)/2);


		for(int i=0; i<iWidth*iHeight;i++)
		{

			if(bMask[i]==0)
			{
				int tmpvalue = 0;



				if(tmpdcm[i]<= imin)
					tmpvalue =0;
				else if(tmpdcm[i] > imax)
					tmpvalue = (int) 255;
				else {
					int tmp = (int) ((tmpdcm[i]-imin)/(iWindowWidth)*255);
					//int tmp = (int) ((tmpdcm[idx]-(iwindowCenter-0.5))/(iWindowWidth-1)+0*255);
					tmpvalue = tmp;
				}

				imagePixelData[i] =  tmpvalue<<16 | tmpvalue<<8 | tmpvalue;
			}else
			{
				
				int iR = 0;
				int iG = 0;
				int iB = 0;

				if(bMask[i]==1) {
					iR = 147; iG = 56; iB = 54;
				}
				else if(bMask[i]==2) {
					iR = 181; iG = 160; iB = 199;
					
				}else if(bMask[i]==3) {
					iR = 1; iG = 176; iB = 76;
				}


				imagePixelData[i] =  iR<<16 | iG<<8 | iB;
			}
		}

		tmpdcm =null;
		return output;


	}
	
	static public BufferedImage FastSignal2bffIconImgReSize(double[] inputSignal, 
			int iWidth, int iHeight	,double iwindowCenter, double iWindowWidth,int iSamplesperPixel ,int iReSize)
	{
		double[] tmpdcm = inputSignal.clone();
		
	
		int smp = iReSize;
		
		//if(iWidth>iHeight)
		{
			
			if(iSamplesperPixel==1)
			{BufferedImage output = null;
			int iStartW = 0;
			int iStartH = 0;
			
			if(iWidth>iHeight)
			{
				 output = new BufferedImage( iWidth/smp,  iWidth/smp, BufferedImage.TYPE_BYTE_GRAY);
				 iStartH = (iWidth-iHeight)/2;
			}else if(iWidth<iHeight){
				
					 output = new BufferedImage( iHeight/smp,  iHeight/smp, BufferedImage.TYPE_BYTE_GRAY);
					 iStartW =(iHeight-iWidth)/2;
				
			}else {
				 output = new BufferedImage( iWidth/smp,  iHeight/smp, BufferedImage.TYPE_BYTE_GRAY);
			}
				byte[] imagePixelData = ((DataBufferByte)output.getRaster().getDataBuffer()).getData();

				double imin = (int) (iwindowCenter-0.5-(iWindowWidth-1)/2);
				double imax =  (int) (iwindowCenter-0.5+(iWindowWidth-1)/2);


				for(int i=0; i<iWidth;i+=smp)
				{
					for(int j=0; j<iHeight;j+=smp)
					{
						int idx = j*iWidth+i;
						int idx2 = ((iStartH+j)/smp)*(iWidth/smp)+((i+iStartW)/smp);

						if(tmpdcm[idx]<= imin)
							imagePixelData[idx2] =0;
						else if(tmpdcm[idx] > imax)
							imagePixelData[idx2] = (byte) 255;
						else {
							byte tmp = (byte) ((tmpdcm[idx]-imin)/(iWindowWidth)*255);
							//int tmp = (int) ((tmpdcm[idx]-(iwindowCenter-0.5))/(iWindowWidth-1)+0*255);
							imagePixelData[idx2] = tmp;
						}
					}

					
				}
				tmpdcm =null;
				return output;

			}else if(iSamplesperPixel==3){
				
				BufferedImage output = null;
				int iStartW = 0;
				int iStartH = 0;
				
				if(iWidth>iHeight)
				{
					 output = new BufferedImage( iWidth/smp,  iWidth/smp, BufferedImage.TYPE_INT_RGB);
					 iStartH = (iWidth-iHeight)/2;
				}else if(iWidth<iHeight){
					
						 output = new BufferedImage( iHeight/smp,  iHeight/smp, BufferedImage.TYPE_INT_RGB);
						 iStartW =(iHeight-iWidth)/2;

				}else {
					output = new BufferedImage( iWidth/smp,  iHeight/smp, BufferedImage.TYPE_INT_RGB);
				}
				int[] imagePixelData = ((DataBufferInt)output.getRaster().getDataBuffer()).getData();

				double imin = (int) (iwindowCenter-0.5-(iWindowWidth-1)/2);
				double imax =  (int) (iwindowCenter-0.5+(iWindowWidth-1)/2);


				for(int i=0; i<iWidth;i+=smp)
				{
					for(int j=0; j<iHeight;j+=smp)
					{
						int idx = j*iWidth+i;
						int idx2 = ((iStartH+j)/smp)*(iWidth/smp)+((i+iStartW)/smp);
						int iR =  (int)inputSignal[(idx*3)+0];   
						int iG = (int)inputSignal[(idx*3)+1];   
						int iB = (int)inputSignal[(idx*3)+2];   

						imagePixelData[idx2] =  iR<<16 | iG<<8 | iB;
					}
				}
				tmpdcm =null;
				return output;


			}

			
		}
		
		
		
	
		

		return null;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	

}
