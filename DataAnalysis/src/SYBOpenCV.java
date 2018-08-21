import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;

import javax.imageio.ImageIO;

import org.opencv.*;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.Core;
import org.opencv.core.CvType;;


public class SYBOpenCV {
	
	static Mat m_inputImgMat;
	static boolean[] m_mask;
	static int m_iHeight = 0;
	static int m_iWidth = 0;
	int[] m_intMask;
	
	public SYBOpenCV() throws Exception
	{
		
		loadOpenCV_Lib();
	}
	
	public static void loadOpenCV_Lib() throws Exception 
	{
	    // get the model
	    String model = System.getProperty("sun.arch.data.model");
	    // the path the .dll lib location
	    String libraryPath = "C:\\opencv 3.2\\build\\java\\x86/";
	    // check for if system is 64 or 32
	    if(model.equals("64")) {
	        libraryPath = "C:\\opencv 3.2\\build\\java\\x64\\";
	    }
	    // set the path
	    System.setProperty("java.library.path", libraryPath);
	    Field sysPath = ClassLoader.class.getDeclaredField("sys_paths");
	    sysPath.setAccessible(true);
	    sysPath.set(null, null);
	    // load the lib
	    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public void LoadImgFile(String filepath) throws Exception
	{
		//filepath = "C:\\test\\Mask_45.png";
		
		m_inputImgMat = Imgcodecs.imread(filepath,Imgcodecs.CV_LOAD_IMAGE_COLOR);//, Imgcodecs.c);
		m_iWidth = m_inputImgMat.rows();
		m_iHeight = m_inputImgMat.cols();
		
		Mat tmp1 = new Mat(m_iWidth,m_iHeight,Imgcodecs.CV_LOAD_IMAGE_COLOR);
		Mat tmp2 = new Mat(m_iWidth,m_iHeight,Imgcodecs.CV_LOAD_IMAGE_COLOR);
		Mat tmp3 = new Mat(m_iWidth,m_iHeight,Imgcodecs.CV_LOAD_IMAGE_COLOR);
		
		m_intMask = new int[m_iWidth*m_iHeight];
		for(int i=0; i<m_iWidth;i++)
		{
			for(int j=0; j<m_iHeight;j++)
			{
				int idx = i*m_iWidth+j;
				double[] dBuff = m_inputImgMat.get(i,j);
				m_intMask[idx] = (int)dBuff[0];
				/*tmp1.put(i, j, dBuff1);
				tmp2.put(i, j, dBuff1);
				tmp3.put(i, j, dBuff1);
				if(m_intMask[idx]==1)
				{
					tmp1.put(i, j, dBuff2);
				}
				if(m_intMask[idx]==2)
				{
					tmp2.put(i, j, dBuff2);
				}
					
				if(m_intMask[idx]==3)
				{
					tmp3.put(i, j, dBuff2);
				}
					
					
				System.out.println(dBuff[0]);
				System.out.println(dBuff[1]);
				System.out.println(dBuff[2]);
				System.out.println(" ");*/
				
				
			}
		}
		/*String FILE_SEP = File.separator;
		String filepath2 = "D:"+FILE_SEP+"98_data"+FILE_SEP+"test"+FILE_SEP+"test1.jpg";
		Imgcodecs.imwrite(filepath2, tmp1);
		 filepath2 = "D:"+FILE_SEP+"98_data"+FILE_SEP+"test"+FILE_SEP+"test2.jpg";
		Imgcodecs.imwrite(filepath2, tmp2);
		 filepath2 = "D:"+FILE_SEP+"98_data"+FILE_SEP+"test"+FILE_SEP+"test3.jpg";
		Imgcodecs.imwrite(filepath2, tmp3);*/

	  
	}
	
	public static void makeHU16bit(String sSvatPath, float[] dcmiput, int iwindowCenter, int iWindowWidth)
	{
		Mat tmp1 = new Mat(m_iWidth,m_iHeight,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		
	
		
		
		float[] tmpdcm = dcmiput.clone();
		int[] itmp = new int[dcmiput.length];
		
		int imin = (int) (iwindowCenter-0.5-(iWindowWidth-1)/2);
		int imax =  (int) (iwindowCenter-0.5+(iWindowWidth-1)/2);
		
		
		
		
		
		/*for(int i=0; i<tmpdcm.length; i++)
		{
			if(tmpdcm[i]>fmax)
				fmax = tmpdcm[i];
			if(tmpdcm[i]<fmin)
				fmin = tmpdcm[i];
		}
		
		for(int i=0; i<tmpdcm.length; i++)
		{
			if(tmpdcm[i]>fmax)
				fmax = tmpdcm[i];
			if(tmpdcm[i]<fmin)
				fmin = tmpdcm[i];
		}*/
		
		for(int i=0; i<tmpdcm.length; i++)
		{
			if(tmpdcm[i]<= imin)
				itmp[i] = 0;
			else if(tmpdcm[i] > imax)
				itmp[i] = 255;
			else
				itmp[i] = (int) ((tmpdcm[i]-imin)/(iWindowWidth)*255);
		}
		
		
	

	
		for(int i=0; i<m_iWidth;i++)
		{
			for(int j=0; j<m_iHeight;j++)
			{
				int idx = i*m_iWidth+j;
				tmp1.put(i, j, (int)itmp[idx]);
			}
		}
		
		
		String FILE_SEP = File.separator;
		String filepath2 = "D:"+FILE_SEP+"98_data"+FILE_SEP+"test"+FILE_SEP+"test1.jpg";
		Imgcodecs.imwrite(filepath2, tmp1);
	}
	
	public static void makeHU16bit(String sSvatPath, float[] dcmiput, int iwindowCenter, int iWindowWidth,int row, int coulum)
	{
		m_iWidth = row;
		m_iHeight = coulum;
		
		//Mat tmp1 = new Mat(m_iWidth,m_iHeight,Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		
	
		
		
		float[] tmpdcm = dcmiput.clone();
		int[] itmp = new int[dcmiput.length];
		
		int imin = (int) (iwindowCenter-0.5-(iWindowWidth-1)/2);
		int imax =  (int) (iwindowCenter-0.5+(iWindowWidth-1)/2);
		
		
		
		
		
		/*for(int i=0; i<tmpdcm.length; i++)
		{
			if(tmpdcm[i]>fmax)
				fmax = tmpdcm[i];
			if(tmpdcm[i]<fmin)
				fmin = tmpdcm[i];
		}
		
		for(int i=0; i<tmpdcm.length; i++)
		{
			if(tmpdcm[i]>fmax)
				fmax = tmpdcm[i];
			if(tmpdcm[i]<fmin)
				fmin = tmpdcm[i];
		}*/
		
		for(int i=0; i<tmpdcm.length; i++)
		{
			if(tmpdcm[i]<= imin)
				itmp[i] = 0;
			else if(tmpdcm[i] > imax)
				itmp[i] = 255;
			else
				itmp[i] = (int) ((tmpdcm[i]-imin)/(iWindowWidth)*255);
		}
		
		
	

	
		/*for(int i=0; i<m_iWidth;i++)
		{
			for(int j=0; j<m_iHeight;j++)
			{
				int idx = i*m_iWidth+j;
				tmp1.put(i, j, (int)itmp[idx]);
			}
		}
		
		
		String FILE_SEP = File.separator;
		String filepath2 = "D:"+FILE_SEP+"98_data"+FILE_SEP+"test"+FILE_SEP+"test1.jpg";
		Imgcodecs.imwrite(filepath2, tmp1);*/
	}
	

	
	public static void calMask( ) 
	{
		Mat tmp1 = new Mat();

		
	boolean[] tmp2 = new boolean[512*512];
		
		for(int i=0; i<512;i++)
		{
			for(int j=0; j<512;j++)
			{
				int idx = i*m_iWidth+j;
				double[] dBuff = m_inputImgMat.get(i,j);
				System.out.println(dBuff[0]);
				System.out.println(dBuff[1]);
				System.out.println(dBuff[2]);
				System.out.println(" ");
				
				double[] dBuff1 = {100.0};
				double[] dBuff2 = {0.0};
				if( //dBuff[0]<91 && dBuff[0] > 0 &&
					//dBuff[1]<220	&&dBuff[1]>100
					dBuff[2]>100	)
				{
					System.out.println(" ");
					dBuff[0] = 255;
					dBuff[1] = 255;
					dBuff[2] = 255;
					m_inputImgMat.put(i, j, dBuff);
					tmp2[idx] = true;
				}
				else{
					dBuff[0] = 0;
					dBuff[1] = 0;
					dBuff[2] = 0;
					m_inputImgMat.put(i, j, dBuff);
					tmp2[idx] = false;
				}
					
			}
		}
		 String FILE_SEP = File.separator;
		
		String filepath = "D:"+FILE_SEP+"98_data"+FILE_SEP+"test"+FILE_SEP+"30375736_WYK_CT_ROI"+FILE_SEP+"Capture"+FILE_SEP
		+"Total Muscle Area22.jpg";
		Imgcodecs.imwrite(filepath, m_inputImgMat);
		
	
			String filename ="D:"+FILE_SEP+"98_data"+FILE_SEP+"test"+FILE_SEP+"30375736_WYK_CT_ROI"+FILE_SEP+"Capture"+FILE_SEP
					+"Total Muscle Area222.txt";
	        try{
	                         
	            // BufferedWriter 와 FileWriter를 조합하여 사용 (속도 향상)
	            BufferedWriter fw = new BufferedWriter(new FileWriter(filename, true));
	            
	            for(int i=0; i<512*512;i++)
	            {
	            	String txt ="";
	            
	            	if(tmp2[i])
	            		txt = "1 ";
	            		
	            	else
	            		txt = "0 ";

	            	fw.write(txt);
	            	fw.flush();
	            }
	 
	            // 객체 닫기
	            fw.close();
	             
	             
	        }catch(Exception e){
	            e.printStackTrace();
	        }
	}

	
	
	

}
