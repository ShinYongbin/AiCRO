import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import java.util.Collections;

import org.dcm4che3.data.Tag;
import org.opencv.imgcodecs.Imgcodecs;

import com.sun.tools.internal.ws.resources.GeneratorMessages;
import com.sun.xml.internal.rngom.digested.DMixedPattern;

import AC_DicomData.AC_DataConverter;
import AC_DicomData.AC_DicomInfo;
import AC_DicomIO.AC_DicomDictionary;
import AC_DicomIO.AC_DicomReader;
import AC_DicomIO.AC_Tag;
import ij.gui.Roi;
import ij.process.ImageProcessor;
import jxl.Cell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import sun.security.krb5.internal.crypto.DesCbcMd5EType;





public class DataAnalysis
{
	static String FILE_SEP = File.separator;
	static String tmpfloderName = "#4.응급의학" + FILE_SEP + "환자데이터";
	static String InputFloerName = "D:" + FILE_SEP + "98_data" + FILE_SEP + "Sarcopenia_정리" + FILE_SEP + "CT" + 
			FILE_SEP + "#2.울산대병원";
	static String outputFloerName = "D:" + FILE_SEP + "98_data" + FILE_SEP + "NEW5"+ FILE_SEP ;
	static String sPatient = "";
	static List<String> lsPatientID = new ArrayList();
	static List<String> lsStudy = new ArrayList();
	static List<String[]> AnomizationMap = new ArrayList();
	static List<String[]> DicomHeadList = new ArrayList();
	static List<String> chkPng = new ArrayList();
	static String nowFilePath = "";
	static int iPatientIDX = 0;
	static int inowPaintent = 1;
	static int iStartidx = 139;
	
	
	class Descending implements Comparator<Integer> {
		 
	    public int compare(Integer o1, Integer o2) {
	        return o2.compareTo(o1);
	    }
	 
	}

	
	



	public static String findSlicePath(String sFolerPath, int iSliceNum)
			throws Exception
	{
		List<Integer> iChkSliceNUM = new ArrayList();
		List<String> iChkPath = new ArrayList();
		
		File[] fDCMFile = new File(sFolerPath).listFiles();
		for (File Tmp : fDCMFile)
		{
			Dcm4cheV3io dcmIO = new Dcm4cheV3io(Tmp.getAbsolutePath());

			iChkSliceNUM.add(Integer.parseInt(dcmIO.getDicomTag2String(Tag.InstanceNumber)));
			iChkPath.add(Tmp.getAbsolutePath());

		}
		
		int[] TransINT = new int[iChkSliceNUM.size()];
		for(int i=0; i<TransINT.length;i++)
		{
			TransINT[i] = iChkSliceNUM.get(i).intValue();
		}
		String[] TransPath = (String[])iChkPath.toArray(new String[iChkPath.size()]);
		

        Collections.sort(iChkSliceNUM);
        
        int iTmp = iChkSliceNUM.get(iSliceNum-1).intValue();
        String sReturnPath = "";
        for(int i=0; i<TransINT.length;i++)
        {
        	
        	if(TransINT[i]==iTmp)
        	{
        		sReturnPath = TransPath[i];
        		break;
        	}
        		
        }


		return sReturnPath;
	}
	public static String findSlicePathInstatnce(String sFolerPath, int iSliceNum)
			throws Exception
	{
		List<Integer> iChkSliceNUM = new ArrayList();
		List<String> iChkPath = new ArrayList();
		
		File[] fDCMFile = new File(sFolerPath).listFiles();
		for (File Tmp : fDCMFile)
		{
			Dcm4cheV3io dcmIO = new Dcm4cheV3io(Tmp.getAbsolutePath());

			iChkSliceNUM.add(Integer.parseInt(dcmIO.getDicomTag2String(Tag.InstanceNumber)));
			iChkPath.add(Tmp.getAbsolutePath());

		}
		
		int[] TransINT = new int[iChkSliceNUM.size()];
		for(int i=0; i<TransINT.length;i++)
		{
			TransINT[i] = iChkSliceNUM.get(i).intValue();
		}
		String[] TransPath = (String[])iChkPath.toArray(new String[iChkPath.size()]);
		

        Collections.sort(iChkSliceNUM);
        
        int iTmp = iChkSliceNUM.get(iSliceNum-1).intValue();
        String sReturnPath = "";
        for(int i=0; i<TransINT.length;i++)
        {
        	
        	if(TransINT[i]==iTmp)
        	{
        		sReturnPath = TransPath[i];
        		break;
        	}
        		
        }


		return sReturnPath;
	}
	
	public static String chkROIFile(String sFolerPath)
			throws Exception
	{
		File[] fFullPath = new File(sFolerPath).listFiles();
		List<File> fileDCMPath = new ArrayList();
		List<File> fileROIPath = new ArrayList();
		
		List<String> chkfile = new ArrayList();
		List<String> chkfile2 = new ArrayList();
		
		
		
		for(File tmp : fFullPath)

		{
			File[] arrayOfFile1 = tmp.listFiles();
			//int j = fFullPath.length;

			for (File fFullPahtTMP :arrayOfFile1)
			{
				
				if ((fFullPahtTMP.getName().contains("ROI")) || (fFullPahtTMP.getName().contains("roi"))) {
					fileROIPath.add(fFullPahtTMP);
				} else {
					fileDCMPath.add(fFullPahtTMP);
				}
			}
		}
			


			for (File fROITmp : (File[])fileROIPath.toArray(new File[fileROIPath.size()]))
			{
				File[] chkfiles = fROITmp.listFiles();
				
				boolean chkPngLineExt = false;
				boolean chkPngMskExt = false;
				
				for(File fTmpChkFile : chkfiles)
				{
					if(fTmpChkFile.isFile())
					{
						if(SYBFileIO.chkFileExt(fTmpChkFile, "png"))
						{
							if(fTmpChkFile.getName().contains("Line"))
							{
								chkPngLineExt	= true;
							    break;
							}
						/*	if(fTmpChkFile.getName().contains("Mask"))
								chkPngMskExt	= true;*/

						}
						
						
					}
				}
				if(!chkPngLineExt) {
					chkfile.add(fROITmp.getName());
					String sTmp = fROITmp.getAbsolutePath();
					chkfile2.add(sTmp);
					//break;
				}


			
		}
		
		ExcelIO excelop = new ExcelIO("D:" + FILE_SEP + "98_data"  + FILE_SEP +"test4.xls");

         excelop.addColumn(0, (String[])chkfile.toArray(new String[chkfile.size()])   );
         excelop.addColumn(1, (String[])chkfile2.toArray(new String[chkfile2.size()])   );
		
		excelop.writeExcelFile();
		
		
		
		return "";
	}
	
	public static String chkRaw(String sFolerPath)
			throws Exception
	{
		File[] fFullPath = new File(sFolerPath).listFiles();
		List<File> fileDCMPath = new ArrayList();
		List<File> fileROIPath = new ArrayList();
		
		List<String> chkfile = new ArrayList();
		List<String> chkfile2 = new ArrayList();
		
		
		
	
			


		for (File fROITmp : fFullPath)
		{
			File[] chkfiles = fROITmp.listFiles();

			boolean chkPngLineExt = false;
			boolean chkPngMskExt = false;

			for(File fTmpChkFile : chkfiles)
			{
				if(fTmpChkFile.isFile())
				{
					if(SYBFileIO.chkFileExt(fTmpChkFile, "txt"))
					{
						//if(fTmpChkFile.getName().contains("raw"))
						//{
							chkPngLineExt	= true;
							break;
						//}
						/*	if(fTmpChkFile.getName().contains("Mask"))
								chkPngMskExt	= true;*/

					}


				}
			}
			if(!chkPngLineExt) {
				chkfile.add(fROITmp.getName());
				String sTmp = fROITmp.getAbsolutePath();
				chkfile2.add(sTmp);
				//break;
			}
		}





		ExcelIO excelop = new ExcelIO("E:" + FILE_SEP + "98_data"  + FILE_SEP +"test.xls");

         excelop.addColumn(0, (String[])chkfile.toArray(new String[chkfile.size()])   );
         excelop.addColumn(1, (String[])chkfile2.toArray(new String[chkfile2.size()])   );
		
		excelop.writeExcelFile();
		
		
		
		return "";
	}

	public static void make1() throws Exception
	{
		
		List<String> patientID = new ArrayList();
		List<String> filePath = new ArrayList();
		List<String> ManufacturerModelName =  new ArrayList();
		List<String> Manufacturer =  new ArrayList();
		List<String> SliceThnkness =  new ArrayList();
		List<String> KvP =  new ArrayList();
		List<String> XrayYube =  new ArrayList();
		List<String> SingleConllimation =  new ArrayList();
		List<String> TotalCollimation =  new ArrayList();
		List<String> tableFeedPerRotation =  new ArrayList();
		
		
		
		
		File[] fExperList = new File("D:" + FILE_SEP + "98_data" + FILE_SEP + "Donor CT 2 Biopsy_pre").listFiles();
		for(File fPatientFile:fExperList)
		{
			for(File fDataFile:fPatientFile.listFiles())
			{
				File fDCM = fDataFile.listFiles()[0].listFiles()[0];
				Dcm4cheV3io io = new Dcm4cheV3io(fDCM.getAbsolutePath());
				
				patientID.add(io.getDicomTag2String(Tag.PatientID));
				filePath.add(fDataFile.getAbsolutePath());
				ManufacturerModelName.add(io.getDicomTag2String(Tag.ManufacturerModelName));
				Manufacturer.add(io.getDicomTag2String(Tag.Manufacturer ));
				SliceThnkness.add(io.getDicomTag2String(Tag.SliceThickness ));
				KvP.add(io.getDicomTag2String(Tag.KVP ));
				XrayYube.add(io.getDicomTag2String(Tag.XRayTubeCurrent ));
				SingleConllimation.add(io.getDicomTag2String(Tag.SingleCollimationWidth ));
				TotalCollimation.add(io.getDicomTag2String(Tag.TotalCollimationWidth ));
				tableFeedPerRotation.add(io.getDicomTag2String(Tag.TableFeedPerRotation ));
			}
			
		}
		
		ExcelIO excelop = new ExcelIO("D:" + FILE_SEP + "98_data" + FILE_SEP + "test10" + FILE_SEP +"test10.xls");

        excelop.addColumn(0, (String[])patientID.toArray(new String[patientID.size()])   );
        excelop.addColumn(1, (String[])filePath.toArray(new String[filePath.size()])   );
        excelop.addColumn(2, (String[])ManufacturerModelName.toArray(new String[ManufacturerModelName.size()])   );
        excelop.addColumn(3, (String[])Manufacturer.toArray(new String[Manufacturer.size()])   );
        excelop.addColumn(4, (String[])SliceThnkness.toArray(new String[SliceThnkness.size()])   );
        excelop.addColumn(5, (String[])KvP.toArray(new String[KvP.size()])   );
        excelop.addColumn(6, (String[])XrayYube.toArray(new String[XrayYube.size()])   );
        excelop.addColumn(7, (String[])SingleConllimation.toArray(new String[SingleConllimation.size()])   );
        excelop.addColumn(8, (String[])TotalCollimation.toArray(new String[TotalCollimation.size()])   );
        excelop.addColumn(9, (String[])tableFeedPerRotation.toArray(new String[tableFeedPerRotation.size()])   );
        
        
		
		excelop.writeExcelFile();
		
		
	}

	public static void makeNewFileType(String sFilepath, Dcm4cheV3io dcmInput, int[] iMaskValue, int iSliceNum)
			throws Exception
	{
		String[] sTmpSplitPath = sFilepath.split("\\\\");
		String sDicomHeader = "";
		for (int i = 0; i < sTmpSplitPath.length - 2; i++) {
			sDicomHeader = sDicomHeader + sTmpSplitPath[i] + FILE_SEP;
		}
		sDicomHeader = sDicomHeader + sTmpSplitPath[(sTmpSplitPath.length - 2)].replaceAll("_DCM", "_RAW") + ".txt";

		
		
		if(new File(sDicomHeader).exists())
		{
			System.out.println("Skip : " + sDicomHeader);
			return;
		}
		BufferedWriter fw = new BufferedWriter(new FileWriter(sDicomHeader));

		fw.write("#PatientName");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.PatientName));
		fw.newLine();

		fw.write("#InstitutionName");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.InstitutionName));
		fw.newLine();

		fw.write("#SeriesDate ");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.SeriesDate));
		fw.newLine();

		fw.write("#ManufacturerModelName");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.ManufacturerModelName));
		fw.newLine();

		fw.write("#Modality");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.Modality));
		fw.newLine();

		fw.write("#PixelSpacing");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.PixelSpacing));
		fw.newLine();

		fw.write("#Rows");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.Rows));
		fw.newLine();

		fw.write("#Columns");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.Columns));
		fw.newLine();

		fw.write("#SliceNo");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.InstanceNumber));
		fw.newLine();


		fw.write("#HUValue");
		fw.newLine();

		float[] fHUValue = dcmInput.getHUPixelValue();
		
		
		/*SYBOpenCV.loadOpenCV_Lib();
		
	SYBOpenCV.makeHU16bit("", fHUValue,Integer.parseInt((dcmInput.getDicomTag2String(Tag.WindowCenter)))
				,Integer.parseInt((dcmInput.getDicomTag2String(Tag.WindowWidth)))  
				,Integer.parseInt((dcmInput.getDicomTag2String(Tag.Rows)))  
				,Integer.parseInt((dcmInput.getDicomTag2String(Tag.Columns)))  
			)   ;*/
		for (int i = 0; i < fHUValue.length; i++)
		{
			String txt = Float.toString(fHUValue[i]) + " ";
			fw.write(txt);
			fw.flush();
		}
		
		fw.newLine();
		fw.write("#ROIMask");
		fw.newLine();

		//float[] fHUValue = dcmInput.getHUPixelValue();
		for (int i = 0; i < iMaskValue.length; i++)
		{
			String txt = Integer.toString(iMaskValue[i]) + " ";
			fw.write(txt);
			fw.flush();
		}
		
		fw.flush();
		fw.close();
		
		System.out.println("process : " + sDicomHeader);
		
		
		
		
	}
	
	
	public static void makeNewFileTypeUS(String sFilepath, String sSaveFile)
			throws Exception
	{
		System.out.println("make Raw File :" + sFilepath);
		
		 Dcm4cheV3io dcmInput = new Dcm4cheV3io(sFilepath);
		
		String sDicomHeader = "";
	
		sDicomHeader =sSaveFile;

		
		
		if(new File(sDicomHeader).exists())
		{
		
			System.out.println("Skip : " + sDicomHeader);
			return;
		}
		BufferedWriter fw = new BufferedWriter(new FileWriter(sDicomHeader));

		fw.write("#PatientName");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.PatientName));
		fw.newLine();

		fw.write("#InstitutionName");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.InstitutionName));
		fw.newLine();

		fw.write("#SeriesDate ");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.SeriesDate));
		fw.newLine();

		fw.write("#ManufacturerModelName");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.ManufacturerModelName));
		fw.newLine();

		fw.write("#Modality");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.Modality));
		fw.newLine();

		fw.write("#PixelSpacing");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.PixelSpacing));
		fw.newLine();

		fw.write("#Rows");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.Rows));
		fw.newLine();

		fw.write("#Columns");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.Columns));
		fw.newLine();

		fw.write("#SliceNo");
		fw.newLine();
		fw.write(dcmInput.getDicomTag2String(Tag.InstanceNumber));
		fw.newLine();

		/*fw.write("#ROINUM");
		fw.newLine();
		fw.write(Integer.toString(ROIDate));
		fw.newLine();*/
		
		int[][] arrColor = dcmInput.calRGBValue();

		fw.write("#Red");
		fw.newLine();
		for (int i = 0; i < arrColor[0].length; i++)
		{
			String txt = Integer.toString(arrColor[0][i]) + " ";
			fw.write(txt);
			fw.flush();
		}
		fw.newLine();
		fw.write("#Green");
		fw.newLine();
		for (int i = 0; i < arrColor[0].length; i++)
		{
			String txt = Integer.toString(arrColor[1][i]) + " ";
			fw.write(txt);
			fw.flush();
		}
		fw.newLine();
		fw.write("#Blue");
		fw.newLine();
		for (int i = 0; i < arrColor[0].length; i++)
		{
			String txt = Integer.toString(arrColor[2][i]) + " ";
			fw.write(txt);
			fw.flush();
		}
		
		
		fw.flush();
		fw.close();
		
		System.out.println("process : " + sDicomHeader);
		
	}
	
	public static void makeRawUS(String inputPath)
	{
		File[] dcmlist = new File(inputPath).listFiles();
		for(File tmpDCMFoler : dcmlist)
		{
			
			for(File tmpDCM : tmpDCMFoler.listFiles())
			{
				String sRAwFilePaht = "D:\\Intest_RAW\\"+tmpDCMFoler.getName().split("_")[0]+"_"+tmpDCMFoler.getName().split("_")[1]+"_RAW\\";
				File chkdir = new File(sRAwFilePaht);
				if(!chkdir.exists())
					chkdir.mkdirs();
				
				sRAwFilePaht+=tmpDCM.getName().replace("dcm", "txt");
				System.out.println(sRAwFilePaht);
				try {
					makeNewFileTypeUS(tmpDCM.getAbsolutePath(), sRAwFilePaht);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	

	public static void SortFolder(String inputPath, String outputPath)
			throws Exception
	{
		File[] fFullPath = new File(inputPath).listFiles();
		List<File> fileDCMPath = new ArrayList();
		List<File> fileROIPath = new ArrayList();

		File[] arrayOfFile1 = fFullPath;int j = fFullPath.length;
		for (int i = 0; i < j; i++)
		{
			File fFullPahtTMP = arrayOfFile1[i];
			if ((fFullPahtTMP.getName().contains("ROI")) || (fFullPahtTMP.getName().contains("roi"))) {
				fileROIPath.add(fFullPahtTMP);
			} else {
				fileDCMPath.add(fFullPahtTMP);
			}
		}
		System.out.println("Complet");

		int iCaseNum = 1;
		String sOutROOT = outputPath + FILE_SEP;
		for (File fROITmp : (File[])fileROIPath.toArray(new File[fileROIPath.size()]))
		{
			String[] fROISplit = fROITmp.getName().split("_");
			if (fROISplit.length == 3) {
				for (File fDCMTmp : (File[])fileDCMPath.toArray(new File[fileROIPath.size()]))
				{
					String[] fDCMSplit = fDCMTmp.getName().split("_");
					if (fROISplit[0].equalsIgnoreCase(fDCMSplit[0]) && fROISplit[1].equalsIgnoreCase(fDCMSplit[1]) )
					{

						if (SYBFileIO.dirNUM(fDCMTmp) == 0)
						{


							String sDCMFilePath = fDCMTmp.listFiles()[0].getAbsolutePath();
							Dcm4cheV3io dcmio = new Dcm4cheV3io(sDCMFilePath);
							String SeriesData = dcmio.getDicomTag2String(Tag.SeriesDate);
							String sNewDCMFilePath = fDCMSplit[0] + "_" + SeriesData + "_" + "DCM";
							String sNewROIFilePath = fDCMSplit[0] + "_" + SeriesData + "_" + "ROI";

							SYBFileIO.dirCopy(fROITmp.getAbsolutePath(), sOutROOT + sNewROIFilePath);
							SYBFileIO.dirCopy(fDCMTmp.getAbsolutePath(), sOutROOT + sNewDCMFilePath);
							break;
						}

						if (SYBFileIO.dirNUM(fDCMTmp) == 1)
						{
							File fDCMfile2 = fDCMTmp.listFiles()[0];



							{
								String sDCMFilePath = fDCMfile2.listFiles()[0].getAbsolutePath();
								Dcm4cheV3io dcmio = new Dcm4cheV3io(sDCMFilePath);
								String SeriesData = dcmio.getDicomTag2String(Tag.SeriesDate);
								String sNewDCMFilePath = fDCMSplit[0] + "_" + SeriesData + "_" + "DCM";
								String sNewROIFilePath = fDCMSplit[0] + "_" + SeriesData + "_" + "ROI";

								SYBFileIO.dirCopy(fROITmp.getAbsolutePath(), sOutROOT + sNewROIFilePath);
								SYBFileIO.dirCopy(fDCMfile2.getAbsolutePath(), sOutROOT + sNewDCMFilePath);
								break;
							}
						}
						if (SYBFileIO.dirNUM(fDCMTmp) >= 2)
						{

							File fChkFolder1 = fDCMTmp.listFiles()[0];
							File fChkFolder2 = fDCMTmp.listFiles()[1];
							File fDCMfile2 ;

							if(fChkFolder1.getName().contains("B30f"))
							{
								fDCMfile2 = fChkFolder1;
							}
							else
								fDCMfile2 = fChkFolder2;

							{
								String sDCMFilePath = fDCMfile2.listFiles()[0].getAbsolutePath();
								Dcm4cheV3io dcmio = new Dcm4cheV3io(sDCMFilePath);
								String SeriesData = dcmio.getDicomTag2String(Tag.SeriesDate);
								String sNewDCMFilePath = fDCMSplit[0] + "_" + SeriesData + "_" + "DCM";
								String sNewROIFilePath = fDCMSplit[0] + "_" + SeriesData + "_" + "ROI";

								SYBFileIO.dirCopy(fROITmp.getAbsolutePath(), sOutROOT + sNewROIFilePath);
								SYBFileIO.dirCopy(fDCMfile2.getAbsolutePath(), sOutROOT + sNewDCMFilePath);
								break;
							}



						}
					}
				}
			}
			if (fROISplit.length == 4) {
				for (File fDCMTmp : (File[])fileDCMPath.toArray(new File[fileROIPath.size()]))
				{
					String[] fDCMSplit = fDCMTmp.getName().split("_");
					if (fROISplit[0].equalsIgnoreCase(fDCMSplit[0]))
					{
						String sDCMFilePath = fDCMTmp.listFiles()[0].getAbsolutePath();
						Dcm4cheV3io dcmio = new Dcm4cheV3io(sDCMFilePath);
						String SeriesData = dcmio.getDicomTag2String(524321);
						String sNewDCMFilePath = fDCMSplit[0] + "_" + SeriesData + "_" + "DCM";// + "_" + fROISplit[4];
						String sNewROIFilePath = fDCMSplit[0] + "_" + SeriesData + "_" + "ROI";// + "_" + fROISplit[4];
						
						if(new File(sOutROOT+sNewROIFilePath).exists())
					
							continue;
					
							
							

						SYBFileIO.dirCopy(fROITmp.getAbsolutePath(), sOutROOT + sNewROIFilePath);
						SYBFileIO.dirCopy(fDCMTmp.getAbsolutePath(), sOutROOT + sNewDCMFilePath);
						break;
					}
				}
			}
		}
	}
	
	public static void MakeRawFile()
			throws Exception
	{
		
		///경로상에 한글이 존재하면 안됨
		
		String inroot = "D:\\98_data\\01_Sarcopenia\\05_박인자교수님(건강의학)\\02_DCM_DATA_ANOMIZATION";
		String outroot = "D:\\98_data\\01_Sarcopenia\\05_박인자교수님(건강의학)\\DCM_DATA_ANOMIZATION_RAW";
		
		
		File[] fExperimentCase = new File(inroot).listFiles();


		File[] arrayOfFile1 = fExperimentCase;
		int j = fExperimentCase.length;
		for (int i = 0; i < j; i++)
		{
			File PatientCase = arrayOfFile1[i];
			
			System.out.println(PatientCase.getName()+"_run now");

			File[] fReadList = PatientCase.listFiles();
			for (File fReadFile : fReadList)
			{
				
				
				if(fReadFile.getName().contains("DCM"))
				{
					File fROIFolder = new File(fReadFile.getAbsolutePath().replaceAll("_DCM", "_ROI"));
					File fDCMFolaer = fReadFile;
					int chkROINUM = 1;
					if(fReadFile.getName().split("_").length==3)
						chkROINUM = Integer.parseInt(fReadFile.getName().split("_")[2]);
					
					
					
	
					File[] fChkPNG = fROIFolder.listFiles(new FilenameFilter()
					{
						public boolean accept(File dir, String name)
						{
							return name.endsWith(".png");
						}
					});
					if (fChkPNG.length == 1)
					{
						int iSliceNum = Integer.parseInt(fChkPNG[0].getName().replaceAll("[^0-9]", ""));
						String sSeliceDCMPath = findSlicePath(fDCMFolaer.getAbsolutePath(), iSliceNum);
						System.out.println(fChkPNG[0].getName());
						System.out.println(sSeliceDCMPath);


						
						Dcm4cheV3io dcmSelicV = new Dcm4cheV3io(sSeliceDCMPath);
						dcmSelicV.calHUPixelValue();

						SYBOpenCV imp2Mask = new SYBOpenCV();
						imp2Mask.LoadImgFile(fChkPNG[0].getAbsolutePath());
						
						makeNewFileType(sSeliceDCMPath, dcmSelicV, imp2Mask.m_intMask, iSliceNum);
						
					}
				}
			}
			System.out.println(PatientCase.getName()+"_run now");
		}
	}

	public static void chkMaskFile()
			throws Exception
	{
		File[] fFirstDepth2 = new File(InputFloerName).listFiles();

		File[] arrayOfFile1 = fFirstDepth2;int j = fFirstDepth2.length;
		for (int i = 0; i < j; i++)
		{
			File fFirstTmp2 = arrayOfFile1[i];

			File[] fFirstDepth = fFirstTmp2.listFiles();
			for (File fFirstTmp : fFirstDepth)
			{
				File[] fSecondDepth = fFirstTmp.listFiles();
				for (File fSecondTmp : fSecondDepth)
				{
					boolean bChk = false;
					String sTargetROIPath = fSecondTmp.getAbsolutePath();
					String sTargetDCMPath = "";
					if (fSecondTmp.getName().split("_").length != 1)
					{
						String folderType = fSecondTmp.getName().split("_")[1];
						String folderData = fSecondTmp.getName().split("_")[0];
						if (folderType.equalsIgnoreCase("ROI")) {
							for (File fReadFile2 : fSecondDepth)
							{
								String folderType2 = fReadFile2.getName().split("_")[1];
								String folderData2 = fReadFile2.getName().split("_")[0];
								if ((folderData2.equalsIgnoreCase(folderData)) && (folderType2.equalsIgnoreCase("DCM")))
								{
									bChk = true;
									sTargetDCMPath = fReadFile2.getAbsolutePath();
									break;
								}
							}
						}
						if (bChk)
						{
							File[] fChkPNG = fSecondTmp.listFiles();
							if (fChkPNG.length != 1) {
								if (fChkPNG.length == 0) {
									chkPng.add(sTargetROIPath);
								}
							}
						}
					}
				}
			}
		}

		System.out.println("sdf");
	}

	public static void replaceROI(String inputPath, String outputPath) throws Exception
	{
	
		String inroot = inputPath;
		String outroot = outputPath;
		
		
		File[] fExperimentCase = new File(inroot).listFiles();


	//	File[] arrayOfFile1 = fExperimentCase;
		//int j = fExperimentCase.length;
		for (File tmp : fExperimentCase)
		{
			//File PatientCase =tmp
			
		

			File[] fReadList = tmp.listFiles();
			
			
			for (File fReadFile : fReadList)
			{
				String replactFilepath = "";
				String[] iSplitNum = fReadFile.getName().split("_");
				String[] sFilePath = fReadFile.getAbsolutePath().split("\\\\");
				
				for(int k=0; k<sFilePath.length-1;k++)
				{
					replactFilepath+=sFilePath[k]+FILE_SEP;
				}

				if(fReadFile.getName().contains("DCM")) 
				{
					for (File fReadFile2 : fReadFile.listFiles())
					{
						Dcm4cheV3io io = new Dcm4cheV3io(fReadFile2.getAbsolutePath());
						String result_user_no = String.format("%05d", Integer.parseInt(io.getDicomTag2String(Tag.InstanceNumber)));
						SYBFileIO.replaceFileName(fReadFile2.getAbsolutePath(), result_user_no+".dcm");
					}
				}
				
				/*if(fReadFile.getName().contains("ROI"))
				{
					String sDate = fReadFile.getName().split("_")[1];
	
					File[] fChkPNG = fReadFile.listFiles(new FilenameFilter()
					{
						public boolean accept(File dir, String name)
						{
							return name.endsWith(".aroi");
						}
					});
					if (fChkPNG.length == 1)
					{
						SYBFileIO.replaceFileName(fChkPNG[0].getAbsolutePath(), sDate+"_ROI.aroi");
						
					}
				}*/
			}
			//System.out.println(PatientCase.getName()+"_run now");
		}
		
		
		
	}
	
	
	public static void makeDicomInfoList(String inputPath, String outputPath) throws Exception
	{
		List<String> PatientID = new ArrayList();
		List<String> PatientName = new ArrayList();
		List<String> InstitutionName = new ArrayList();
		List<String> SeriesDate = new ArrayList();
		List<String> ManufacturerModelName = new ArrayList();
		List<String> Modality = new ArrayList();
		List<String> PixelSpacing = new ArrayList();
		List<String> Rows = new ArrayList();
		List<String> Columns = new ArrayList();
		
		List<String> BitsAllocated = new ArrayList();
		List<String> BitsStored = new ArrayList();
		List<String> HighBit = new ArrayList();
		
		
		File[] fFullPath = new File(inputPath).listFiles();
		List<File> fileDCMPath = new ArrayList();
		List<File> fileROIPath = new ArrayList();

		File[] arrayOfFile1 = fFullPath;
		
		int j = fFullPath.length;
		for (int i = 0; i < j; i++)
		{
			for(File tmpasrr : arrayOfFile1[i].listFiles() ) 
			{
				File fFullPahtTMP = tmpasrr;

				if ((fFullPahtTMP.getName().contains("ROI")) || (fFullPahtTMP.getName().contains("roi"))) {
					fileROIPath.add(fFullPahtTMP);
				} else if ((fFullPahtTMP.getName().contains("dcm")) || (fFullPahtTMP.getName().contains("DCM")))  {
					fileDCMPath.add(fFullPahtTMP);
				}
			}
		}
		

		for (File fDCMTmp : (File[])fileDCMPath.toArray(new File[fileROIPath.size()]))
		{
			String sDCMPath = fDCMTmp.listFiles()[0].getAbsolutePath();
			Dcm4cheV3io dcmInput = new Dcm4cheV3io(sDCMPath);
			
			PatientID.add(dcmInput.getDicomTag2String(Tag.PatientID));  
			PatientName.add(dcmInput.getDicomTag2String(Tag.PatientName)); 
			
			
		//	InstitutionName.add( chkInstiute(dcmInput.getDicomTag2String(Tag.InstitutionName)));  
			InstitutionName.add( (dcmInput.getDicomTag2String(Tag.InstitutionName)));  
			SeriesDate.add(dcmInput.getDicomTag2String(Tag.SeriesDate)); 
			ManufacturerModelName.add(dcmInput.getDicomTag2String(Tag.ManufacturerModelName)); 
			Modality.add(dcmInput.getDicomTag2String(Tag.Modality)); 
			PixelSpacing.add(dcmInput.getDicomTag2String(Tag.PixelSpacing)); 
			Rows.add(dcmInput.getDicomTag2String(Tag.Rows)); 
			Columns.add(dcmInput.getDicomTag2String(Tag.Columns)); 
			 BitsAllocated.add(dcmInput.getDicomTag2String(Tag.BitsAllocated)); 
			 BitsStored.add(dcmInput.getDicomTag2String(Tag.BitsStored)); 
			 HighBit.add(dcmInput.getDicomTag2String(Tag.HighBit)); 
		}
		
		ExcelIO excelop = new ExcelIO(outputPath);
		
		excelop.addColumn(0, (String[])PatientID.toArray(new String[PatientID.size()])   );
		excelop.addColumn(1, (String[])PatientName.toArray(new String[PatientName.size()])   );
		excelop.addColumn(2, (String[])InstitutionName.toArray(new String[InstitutionName.size()])   );
		excelop.addColumn(3, (String[])SeriesDate.toArray(new String[SeriesDate.size()])   );
		excelop.addColumn(4, (String[])ManufacturerModelName.toArray(new String[ManufacturerModelName.size()])   );
		excelop.addColumn(5, (String[])Modality.toArray(new String[Modality.size()])   );
		excelop.addColumn(6, (String[])PixelSpacing.toArray(new String[PixelSpacing.size()])   );
		excelop.addColumn(7, (String[])Rows.toArray(new String[Rows.size()])   );
		excelop.addColumn(8, (String[])Columns.toArray(new String[Columns.size()])   );
		
		excelop.addColumn(9, (String[])BitsAllocated.toArray(new String[BitsAllocated.size()])   );
		excelop.addColumn(10, (String[])BitsStored.toArray(new String[BitsStored.size()])   );
		excelop.addColumn(11, (String[])HighBit.toArray(new String[HighBit.size()])   );
		
		excelop.writeExcelFile();
	}
	
	//public static void 
	
	public static String chkInstiute(String inInst)
	{
		String[] INSTITUTION_NAME_LIST = 
			{"ASAN MEDICAL CENTER",//#1
					"HANYANG GURI",//#1
					"SEOUL YANG",//#1
					"DAERIM S.M.",//#1
					"HAN SA RANG",
					"VIEVIS NAMUH",
					"YOCHON CHONNAM",
					"KANG NAM",
					"CNUHH",
					"DANKOOK",
					"DONG YANG",
					"EUMC. MOKDONG",
					"HANKOOK",
					"YANGJI",
					"ULSAN"};
		
		
		String output = "N/A";
		int idx =0;
		
		for(String tmp : INSTITUTION_NAME_LIST)
		{
			idx++;
				if(inInst.toUpperCase().contains(tmp))
					output = "Site "+idx;
		}
		
		return output;
	}
	
	public static void Anomization(int idx)
			throws Exception
	{
		String inroot = "D:\\98_data\\01_Sarcopenia\\05_박인자교수님(위장관외과)\\01_DCM_DATA";
		String outroot ="D:\\98_data\\01_Sarcopenia\\05_박인자교수님(위장관외과)\\02_DCM_DATA_ANOMIZATION";
		String sProjectNama = "PIJ_GIS";
		
		int SarcopeniaIDX =  1;
		
		List<String> PatientID = new ArrayList();
		List<String> AnomizationName = new ArrayList();
		List<String> PatientName = new ArrayList();
		List<String> InstitutionName = new ArrayList();
		List<String> AnomizationSite = new ArrayList();
		List<String> SeriesDate = new ArrayList();
		List<String> ManufacturerModelName = new ArrayList();
		List<String> Modality = new ArrayList();
		List<String> PixelSpacing = new ArrayList();
		List<String> Rows = new ArrayList();
		List<String> Columns = new ArrayList();
		
		
		File[] fFullPath = new File(inroot).listFiles();
		List<File> fileDCMPath = new ArrayList();
		List<File> fileROIPath = new ArrayList();
		
		List<String> sPatientID = new ArrayList();
		List<String> sAnomization = new ArrayList();

		File[] arrayOfFile1 = fFullPath;

		for (int i = 0; i < fFullPath.length; i++)
		{
			File fFullPahtTMP = arrayOfFile1[i];
			if ((fFullPahtTMP.getName().contains("ROI")) || (fFullPahtTMP.getName().contains("roi"))) {
				fileROIPath.add(fFullPahtTMP);
			} else if ((fFullPahtTMP.getName().contains("dcm")) || (fFullPahtTMP.getName().contains("DCM")))  {
				fileDCMPath.add(fFullPahtTMP);
			}
		}
		

		
		for(File ftmpDCM:(File[])fileDCMPath.toArray(new File[fileDCMPath.size()]))
		{
			String[] folderName = ftmpDCM.getName().split("_");
			
			
			
			if(sPatientID.size()==0)
			{
				sPatientID.add(folderName[0]);
				sAnomization.add(sProjectNama+SarcopeniaIDX);
				SarcopeniaIDX++;
			}
			else
			{
				boolean chkSame = true;
				for(String sTmp :(String[])sPatientID.toArray(new String[sPatientID.size()]))
				{
					if(sTmp.contains(folderName[0]))
					{
						chkSame = false;
						break;
					}
				}
				if(chkSame)
				{
					sPatientID.add(folderName[0]);
					sAnomization.add(sProjectNama+SarcopeniaIDX);
					SarcopeniaIDX++;
				}
			}
			String sDCMinputtPath = ftmpDCM.getAbsolutePath();
			String sROIinputtPath = sDCMinputtPath.replaceAll("_DCM", "_ROI");
			
			String sDCMOuptPath = outroot+FILE_SEP+
					sProjectNama+(SarcopeniaIDX-1)+FILE_SEP+
					folderName[1]+"_DCM";
			if(folderName.length==4)
				sDCMOuptPath+="_"+folderName[3];
			String sROIOuptPath = sDCMOuptPath.replaceAll("_DCM", "_ROI");
			
			//SYBFileIO.dirCopy(sROIinputtPath, sROIOuptPath);
			
			
			if(new File(outroot+"\\"+sProjectNama+(SarcopeniaIDX-1)).exists())
			{
				System.out.println("File exist : "+outroot+"\\"+sProjectNama+SarcopeniaIDX);
				continue;
			}
			
			
			String sDCMPath = ftmpDCM.listFiles()[0].getAbsolutePath();
		Dcm4cheV3io dcmInput = new Dcm4cheV3io(sDCMPath);
			
			PatientID.add(dcmInput.getDicomTag2String(Tag.PatientID));  
			PatientName.add(dcmInput.getDicomTag2String(Tag.PatientName)); 
			AnomizationName.add(sProjectNama+(SarcopeniaIDX-1));
			AnomizationSite.add( chkInstiute(dcmInput.getDicomTag2String(Tag.InstitutionName)));  
			InstitutionName.add( (dcmInput.getDicomTag2String(Tag.InstitutionName)));  
			SeriesDate.add(dcmInput.getDicomTag2String(Tag.SeriesDate)); 
			ManufacturerModelName.add(dcmInput.getDicomTag2String(Tag.ManufacturerModelName)); 
			Modality.add(dcmInput.getDicomTag2String(Tag.Modality)); 
			PixelSpacing.add(dcmInput.getDicomTag2String(Tag.PixelSpacing)); 
			Rows.add(dcmInput.getDicomTag2String(Tag.Rows)); 
			Columns.add(dcmInput.getDicomTag2String(Tag.Columns)); 
			
			
			
			
			
			System.out.println("Start Anomzation "+sProjectNama + " "+SarcopeniaIDX);
			for(File fDCMfile: ftmpDCM.listFiles())
			{
				String sOutputfilename = sDCMOuptPath+FILE_SEP+fDCMfile.getName();
				SYB_Anonymization.AnonymizationDCMFile(fDCMfile.getAbsolutePath(),
						sOutputfilename, sProjectNama+(SarcopeniaIDX-1));
			}
			System.out.println("end Anomzation "+sProjectNama + " "+SarcopeniaIDX);
		}
		
	

		ExcelIO excelop = new ExcelIO(outroot+ FILE_SEP +"11.xls");
		
		
		excelop.addColumn(0, (String[])AnomizationName.toArray(new String[AnomizationName.size()])   );
		excelop.addColumn(1, (String[])PatientID.toArray(new String[PatientID.size()])   );
		excelop.addColumn(2, (String[])PatientName.toArray(new String[PatientName.size()])   );
		excelop.addColumn(3, (String[])InstitutionName.toArray(new String[InstitutionName.size()])   );
		excelop.addColumn(4, (String[])AnomizationSite.toArray(new String[AnomizationSite.size()])   );
		excelop.addColumn(5, (String[])SeriesDate.toArray(new String[SeriesDate.size()])   );
		excelop.addColumn(6, (String[])ManufacturerModelName.toArray(new String[ManufacturerModelName.size()])   );
		excelop.addColumn(7, (String[])Modality.toArray(new String[Modality.size()])   );
		excelop.addColumn(8, (String[])PixelSpacing.toArray(new String[PixelSpacing.size()])   );
		excelop.addColumn(9, (String[])Rows.toArray(new String[Rows.size()])   );
		excelop.addColumn(10, (String[])Columns.toArray(new String[Columns.size()])   );
		
		excelop.writeExcelFile();
		
		
		

	}
	
	public static void test() throws Exception
	{
		File[] f= new File("D:\\98_data\\test1").listFiles();
		
		for(File tmpf : f)
		{
			Dcm4cheV3io io = new Dcm4cheV3io(tmpf.listFiles()[0]);
			System.out.println(tmpf.getName());;
			System.out.println(io.getDicomTag2String(Tag.PatientID));;
		
		}
		
	}
	
	
	public static void tset10() throws Exception
	{
		String sPngReadPath = "D:\\98_data\\99_TestData\\test1";
		String sTxtReadPath = "D:\\98_data\\99_TestData\\test1";
		String sSaverootPath = "D:\\98_data\\test6\\";
		
		File[] fFullPath = new File(sPngReadPath).listFiles();
		List<File> fileDCMPath = new ArrayList();
		List<File> fileROIPath = new ArrayList();
		
		List<String> chkfile = new ArrayList();
		List<String> chkfile2 = new ArrayList();
		
		
		
		for(File tmp : fFullPath)

		{
			File[] arrayOfFile1 = tmp.listFiles();
			//int j = fFullPath.length;

			for (File fFullPahtTMP :arrayOfFile1)
			{
				
				if ((fFullPahtTMP.getName().contains("ROI")) || (fFullPahtTMP.getName().contains("roi"))) {
					fileROIPath.add(fFullPahtTMP);
				} else {
					fileDCMPath.add(fFullPahtTMP);
				}
				String[] aa =  fFullPahtTMP.getAbsolutePath().split("\\\\");
				System.out.println("dd");
			}

		}
		
		for (File fROITmp : (File[])fileROIPath.toArray(new File[fileROIPath.size()]))
		{
			File[] chkfiles = fROITmp.listFiles();

			boolean chkPngLineExt = false;
			boolean chkPngMskExt = false;

			for(File fTmpChkFile : chkfiles)
			{
				if(fTmpChkFile.isFile())
				{
					if(SYBFileIO.chkFileExt(fTmpChkFile, "png"))
					{
						if(fTmpChkFile.getName().contains("Line"))
						{
							System.out.println(fTmpChkFile.getAbsolutePath());
							
							SYBOpenCV imp2Mask = new SYBOpenCV();
							imp2Mask.LoadImgFile(fTmpChkFile.getAbsolutePath());
							
							String[] sSplitPaht = fROITmp.getAbsolutePath().split("\\\\");
							String stxtPath = sTxtReadPath+FILE_SEP+sSplitPaht[sSplitPaht.length-2]
									+FILE_SEP+sSplitPaht[sSplitPaht.length-1].split("_")[0]+"_RAW";
							if(sSplitPaht[sSplitPaht.length-1].split("_").length==3)
							{
								stxtPath = sTxtReadPath+FILE_SEP+sSplitPaht[sSplitPaht.length-2]
										+FILE_SEP+sSplitPaht[sSplitPaht.length-1].split("_")[0]+
										"_"+sSplitPaht[sSplitPaht.length-1].split("_")[2]+"_RAW";
							}
							
							stxtPath += ".txt";
							
							String sSavepath 
							
							= sSaverootPath+FILE_SEP+sSplitPaht[sSplitPaht.length-2]
									+FILE_SEP+sSplitPaht[sSplitPaht.length-1].split("_")[0]+"_RAW";
							if(sSplitPaht[sSplitPaht.length-1].split("_").length==3)
							{
								sSavepath = sSaverootPath+FILE_SEP+sSplitPaht[sSplitPaht.length-2]
										+FILE_SEP+sSplitPaht[sSplitPaht.length-1].split("_")[0]+
										"_"+sSplitPaht[sSplitPaht.length-1].split("_")[2]+"_RAW";
							}
							
							sSavepath += ".txt";
							SYBFileIO.fileCopy(stxtPath, sSavepath);
							
							BufferedWriter fw = new BufferedWriter(new FileWriter(sSavepath,true));
							fw.newLine();
							fw.write("#ROILine");
							fw.newLine();
							
							
							//float[] fHUValue = dcmInput.getHUPixelValue();
							for (int i = 0; i < imp2Mask.m_intMask.length; i++)
							{
								String txt = Integer.toString(imp2Mask.m_intMask[i]) + " ";
								fw.write(txt);
								fw.flush();
							}
							
							
							fw.close();
							
							
							break;

							
							
							
							
							
			//				brea
						}

					}

				}
			}
			//break;
		}

	}
	
	public static void tset11() throws Exception
	{
		String sPngReadPath = "E:\\98_data\\test13";
	//	String sTxtReadPath = "D:\\98_data\\99_TestData\\test1";
		String sSaverootPath = "E:\\98_data\\sort\\";
		
		File[] fFullPath = new File(sPngReadPath).listFiles();
	
		
		int idx = 0;
		
		for(File tmp : fFullPath)
		{
			File tmp2 = tmp.listFiles()[0];
			
			
			String[] sSplitTmp = tmp.getName().split("_");
			String sFilePath = "";
			
			sFilePath = sSaverootPath + sSplitTmp[0]+"_"+sSplitTmp[sSplitTmp.length-1]+"_DCM";
			
			
			

			
			SYBFileIO.dirCopy(tmp2.getAbsolutePath(), sFilePath);
			
			
			
			
			
			
			
		//	System.out.println("idx : "+ idx++ +"   Num :" + tmp.list().length);
			/*String[] sSplitPaht = tmp.getAbsolutePath().split("\\\\");
			String sSavepath ="";
			
			sSavepath = sSaverootPath+FILE_SEP+sSplitPaht[sSplitPaht.length-1];
			
			if(new File(sSavepath).exists())
				continue;
			
						


			for (File fFullPahtTMP :tmp.listFiles())
			{
				String sSaveTmp = sSavepath + FILE_SEP+fFullPahtTMP.getName();
				
				
			
						
				

						//SYBFileIO.dirCopy(fFullPahtTMP.getAbsolutePath(), sSaveTmp);
						
			}*/

		}
		


	}
	
	public static void MakeWithexel(String input) throws Exception
	{
		//ExcelIO excelio = new ExcelIO("");
		
		String sExcelPath = "";
		String sFilePath = "D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\Test";
		
		Workbook workbook = Workbook.getWorkbook(new File(sExcelPath));
		Sheet sheet = workbook.getSheet(0);
		HashMap<String, Integer> fruitMap = new HashMap();
		
		for(int i=0; i<192; i++)
		{
			Cell cID = sheet.getCell(0, i); 
			NumberCell cSliceNum = (NumberCell) sheet.getCell(2, i); 
			
			String sID =cID.getContents();
			int sSliceNum =(int) cSliceNum.getValue();
			
			System.out.println("ID :" +sID);
			System.out.println("SliceNum :" +sSliceNum);
			
			fruitMap.put(sID, sSliceNum);
			
		}
		
		
		
		File[] fExperimentList = new File(sFilePath).listFiles();
		
		for(int i =0 ; i< fExperimentList.length ; i++)
		{
			
			File tmp2  =fExperimentList[i];
			System.out.println("i idx !!! :: " + i);
			for(File tmp : tmp2.listFiles())
			{
				if(tmp.isFile())
					continue;
				
				String[] sSplit = tmp.getName().split("_");
				
				


				int SliceNum = fruitMap.get(tmp2.getName());




				String sSeliceDCMPath = findSlicePath(tmp.getAbsolutePath(), SliceNum);

				Dcm4cheV3io dcmSelicV = new Dcm4cheV3io(sSeliceDCMPath);
				dcmSelicV.calHUPixelValue();

				int[] iEmpty = new int [10];

				makeNewFileType(sSeliceDCMPath, dcmSelicV, iEmpty, SliceNum);
			}
		}
	}
	
	
	public static void sortDCM(String inputDir, String outDir) throws Exception
	{
		Collection<File[]>  tmp = AC_SortDcmPath.buildDicomFileList( (inputDir));
		
		for(File[] tmpDCM : tmp)
		{
			Dcm4cheV3io dcmInfo = new Dcm4cheV3io(tmpDCM[0]);
			Dcm4cheV3io dcmInfo2 = null;
			String PatientID = dcmInfo.getDicomTag2String(Tag.PatientID);
			String Data = dcmInfo.getDicomTag2String(Tag.SeriesDate);
			
			String outfileDir = outDir+File.separator+PatientID+"_"+Data+"_"+"DCM";
			if(!new File(outfileDir).exists())
				new File(outfileDir).mkdir();
			
			for(File tmpDcmfile : tmpDCM)
			{
				dcmInfo2 = new Dcm4cheV3io(tmpDcmfile);
				String result_user_no = String.format("%05d", Integer.parseInt(dcmInfo2.getDicomTag2String(Tag.InstanceNumber)));
				String outFilePaht = outfileDir+File.separator+result_user_no+".dcm";
				
				SYBFileIO.fileCopy(tmpDcmfile.getAbsolutePath(), outFilePaht);
		
				
			}
			
			
		}
		
	}
	public static void replcaceInstanceName() throws Exception
	{
		
		String path = "D:\\98_data\\01_Sarcopenia\\05_박인자교수님(위장관외과)\\02_DCM_DATA_ANOMIZATION";
	File[] list = new File(path).listFiles();
	
	Dcm4cheV3io dcmInfo2 = null;
	for(File tmp : list)
	{
		File[] dcmfile =  tmp.listFiles()[0].listFiles();
		
		
		 String[] splitInputPath = dcmfile[0].getAbsolutePath().split("\\\\");
		 String outputfilePath = "";
		 for(int i=0; i<splitInputPath.length-4;i++)
		 {
			 outputfilePath += splitInputPath[i]+File.separator;
		 }
		 
		 outputfilePath +="DCM_ANO"+File.separator+splitInputPath[splitInputPath.length-3]
				 +File.separator+splitInputPath[splitInputPath.length-2]
						
				 +File.separator;
		 
		if(new File(outputfilePath).exists())
		{
			System.out.println("File exist : "+outputfilePath);
			continue;
		}
	
		

		System.out.println("Start : "+tmp.getAbsolutePath());
		System.out.println("TO : "+outputfilePath);
		for(File tmpDcmfile : dcmfile)
		{
		
			
		
			
			System.out.println(tmpDcmfile.getName());
			dcmInfo2 = new Dcm4cheV3io(tmpDcmfile);
			String result_user_no = String.format("%05d", Integer.parseInt(dcmInfo2.getDicomTag2String(Tag.InstanceNumber)));
			
			
			String outFilePaht = result_user_no+".dcm";
			
		//	SYBFileIO.fileCopy(tmpDcmfile.getAbsolutePath(), outFilePaht);
			

			
			
			String outputfilePath2 = outputfilePath+
					outFilePaht;
		
			 
			 SYBFileIO.fileCopy(tmpDcmfile.getAbsolutePath(), outputfilePath2);
			 
			 
			
			
			
			
	
			
		}
		
		
	}
		
	
	
	}
	
	public static void sortDCMOneFolder(String inputDir, String outDir) throws Exception
	{
	
		
		File[] tmpDCM  = new File(inputDir).listFiles();
		{
			Dcm4cheV3io dcmInfo = new Dcm4cheV3io(tmpDCM[0]);
			Dcm4cheV3io dcmInfo2 = null;
			String PatientID = dcmInfo.getDicomTag2String(Tag.PatientID);
			String Data = dcmInfo.getDicomTag2String(Tag.SeriesDate);
			
			String outfileDir = outDir+File.separator+PatientID+"_"+Data+"_"+"DCM";
			if(!new File(outfileDir).exists())
				new File(outfileDir).mkdir();
			else
				return;
			
			for(File tmpDcmfile : tmpDCM)
			{
				dcmInfo2 = new Dcm4cheV3io(tmpDcmfile);
				
				
				String result_user_no = String.format("%05d", Integer.parseInt(dcmInfo2.getDicomTag2String(Tag.InstanceNumber)));
				String outFilePaht = outfileDir+File.separator+result_user_no+".dcm";
				
				SYBFileIO.fileCopy(tmpDcmfile.getAbsolutePath(), outFilePaht);
				
			}
			
			
		}
		
	}
	
	public static void test1111()
	{
		Dcm4cheV3io tmp = null;
		try {
			 tmp = new Dcm4cheV3io("D:\\intest_Sort\\10525645_20171213_DCM\\00001.dcm");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tmp.calRGBValue();
		
		
		
	}
	
	
	public static void getSeries() throws Exception
	{
		
		
		List<String> PatientID = new ArrayList();
		List<String> SeriesNUM = new ArrayList();
		List<String> SliceNUM = new ArrayList();
		HashMap<String, String> map1 = new HashMap<>();
		HashMap<String, String> map2 = new HashMap<>();
		HashMap<String, String> map3 = new HashMap<>();
	
		
		
		
		
		//	try {
			Workbook workbook = Workbook.getWorkbook(new File("D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\Sort_list.xls"));
			Sheet sheet = workbook.getSheet(0);  
			
			
			for(int i=1;i<248;i++)
			{
				int idx = i;
				System.out.println(i);
				// cell을 생성해서 Sheet의 내용을 cell단위로 분리해서 읽습니다.
				Cell cell = sheet.getCell(0,idx);  

				// cell내용을 String형태로 담고, DB에 적재하거나 적절히 사용합니다.
				String sPatiendID = cell.getContents(); 

				cell = sheet.getCell(4,idx);  
				// cell내용을 String형태로 담고, DB에 적재하거나 적절히 사용합니다.
				String sSliceNum = cell.getContents().replaceAll("[^0-9]", "");
				
			//	 System.out.println(sPatiendID+ " 1 "+sSliceNum );
			
				map1.put(sPatiendID, sSliceNum);
				
				
				
				cell = sheet.getCell(7,idx);  
				// cell내용을 String형태로 담고, DB에 적재하거나 적절히 사용합니다.
				 sSliceNum = cell.getContents().replaceAll("[^0-9]", "");
				 map2.put(sPatiendID, sSliceNum);
				 
				// System.out.println(sPatiendID+ " 2 "+sSliceNum );
				 
					
					cell = sheet.getCell(10,idx);  
					// cell내용을 String형태로 담고, DB에 적재하거나 적절히 사용합니다.
					 sSliceNum = cell.getContents().replaceAll("[^0-9]", "");
					 map3.put(sPatiendID, sSliceNum);
				//	 System.out.println(sPatiendID+ " 3 "+sSliceNum );
					 
				
			}

			
			/*String inputpath = "D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\Ori\\CT volumetry and sarcopenia in gastrectomized patients(-2016.12)_수술후1년";
			String output = "D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\Sort";
			
			File[] paitentID = new File(inputpath).listFiles();
			for(File tmp1 : paitentID)
			{
				if(tmp1.isFile())
					continue;
				
				File[] fSereis = tmp1.listFiles();
				for(File tmp2 : fSereis)
				{
					
					
					String[] pathmep = tmp2.getAbsolutePath().split("\\\\");
					String sPatientIDpath = pathmep[pathmep.length-2];
					
					String pID = sPatientIDpath.split("_")[0];
					if(pID.equals("NO"))
						break;
					String SeriesNamepath = pathmep[pathmep.length-1];
					String SeriesNumber = SeriesNamepath.split("_")[0];
							
					if(map.get(pID).equals(SeriesNumber))
					/*if(//tmp2.getName().toUpperCase().contains("PELVIS_POST")
							tmp2.getName().toUpperCase().contains("AP_5MM")
							//tmp2.getName().toUpperCase().contains("B30F")
							//||
						(tmp2.getName().toUpperCase().contains("I30F")
							&& !(tmp2.getName().toUpperCase().contains("SCOUT")
									||
									tmp2.getName().toUpperCase().contains("TOPOGRAM"))))
					{
						System.out.println(tmp2.getAbsolutePath());
						try {
							sortDCMOneFolder(tmp2.getAbsolutePath(),output);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						break;
						
					}
				}
			}*/
			
			
			String sFilePath = "D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\DCM_DATA_ANOMIZATION";
			File[] fExperimentList = new File(sFilePath).listFiles();
			
			for(int i =0 ; i< fExperimentList.length ; i++)
			{
				
				File tmp2  =fExperimentList[i];
				System.out.println("i idx !!! :: " + i);
				
				
			//	int[] arrint = {-1,-1,-1};
				int idx =0;				
				for(File tmp : tmp2.listFiles())
				{
					if(tmp.isFile())
						continue;
					else
					{
						int SliceNum =0;
						if(idx==0)
						{
						
							 SliceNum = Integer.parseInt(map1.get(tmp2.getName()));
							 idx++;
						}else if(idx==1)
						{
						
							 SliceNum = Integer.parseInt(map2.get(tmp2.getName()));
							 idx++;
						}else if(idx==2)
						{
							
							 SliceNum = Integer.parseInt(map3.get(tmp2.getName()));
							 idx++;
						}





						String sSeliceDCMPath = findSlicePath(tmp.getAbsolutePath(), SliceNum);

						Dcm4cheV3io dcmSelicV = new Dcm4cheV3io(sSeliceDCMPath);
						dcmSelicV.calHUPixelValue();

						int[] iEmpty = new int [10];

						makeNewFileType(sSeliceDCMPath, dcmSelicV, iEmpty, SliceNum);
						
					}
					
					
			
					


					
				}
			}
			
			
			
			
			
			
	//		System.out.println("");
		
			
			
			
			
			
		/*} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public static void getTxt() throws Exception
	{
		File[] fExperimentList = new File("D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\tmp").listFiles();
		for(File tmp : fExperimentList)
		{
			for(File tmp2: tmp.listFiles())
			{
				if(tmp2.isFile())
				{
				//
				//	makePNG(tmp2.getAbsolutePath());
					
					
					//tmp2.delete();
					/*String[] splipath = tmp2.getAbsolutePath().split("\\\\");
					String copyPath = "";
					for(int i=0 ; i< splipath.length;i++)
					{
						if(i==4)
							copyPath += "ONLY_RAW"+File.separator;
						else if(i==splipath.length-1)
							copyPath += splipath[i];
						else
							copyPath += splipath[i]+File.separator;							
					}
					
					SYBFileIO.fileCopy(tmp2.getAbsolutePath(), copyPath);*/
					
				//	break;
				}
			}
			
		}
		
	}
	
	public static void chkList() throws BiffException, IOException
	{
		
		HashMap<String, String> map = new HashMap<>();
		
		
		
		
		File[] filelist = new File("D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\Sort\\수술후-3-6").listFiles();
		for(File tmp : filelist)
		{
			String pid = tmp.getName().split("_")[0];
			map.put(pid, "ok");
		
		}
		
		
		Workbook workbook = Workbook.getWorkbook(new File("D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\Sort_list.xls"));
		Sheet sheet = workbook.getSheet(0);  
		
		
		for(int i=0;i<248;i++)
		{
			int idx = i;
			//System.out.println(i);
			// cell을 생성해서 Sheet의 내용을 cell단위로 분리해서 읽습니다.
			Cell cell = sheet.getCell(0,idx);  

			// cell내용을 String형태로 담고, DB에 적재하거나 적절히 사용합니다.
			String sPatiendID = cell.getContents(); 

		//	cell = sheet.getCell(1,idx);  
			// cell내용을 String형태로 담고, DB에 적재하거나 적절히 사용합니다.
			//String sSliceNum = cell.getContents().replaceAll("[^0-9]", "");
			
			if(map.get(sPatiendID)==null)
				System.out.println(sPatiendID);
			
		}
		
		
		


			//map.put(sPatiendID, sSliceNum);
	}
	
	public static void tmpSort()
	{
		String inputpath = "D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\tmp";
		String output = "D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\tmp_Sort";
		
		File[] paitentID = new File(inputpath).listFiles();

		//File[] fSereis = tmp1.listFiles();
		for(File tmp2 : paitentID)
		{



			try {
				sortDCMOneFolder(tmp2.getAbsolutePath(),output);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		//	break;

		}

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

				imagePixelData[i] =  iR<<16 | iG<<8 | iG;
			}
			tmpdcm =null;
			return output;


		}

		return null;
	}
	
	static public BufferedImage FastSignal2bffImginmask(double[] inputSignal, 
			int iWidth, int iHeight	,double iwindowCenter, double iWindowWidth,int iSamplesperPixel ,int iSampling,int[] mask)
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
					
					 if(mask[idx]==1)
						imagePixelData[idx] = (byte) 0;
					 else if(mask[idx]==2)
						imagePixelData[idx] = (byte) 123;
					else if(mask[idx]==3)
						imagePixelData[idx] = (byte) 255;

				
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

				imagePixelData[i] =  iR<<16 | iG<<8 | iG;
			}
			tmpdcm =null;
			return output;


		}

		return null;
	}
	
	public static void makePNG(String inputPath, String outputPath) throws Exception
	{
		String filepath = inputPath;
		
		

		    try {
		      ////////////////////////////////////////////////////////////////
		      BufferedReader in = new BufferedReader(new FileReader(filepath));
		      String s;
		      String PatientName = null;
		      String SeriesDate = null;
		      int row = 0;
		      int colum = 0;
		      double[] hu = null;
		      int[] mask = null;
		      String value ="";

		      while ((s = in.readLine()) != null) 
		      {
		    	  
		    	  //System.out.println(s);
		    	  if( s.contains("#PatientName"))
		    		  PatientName = (in.readLine());
		    	  if( s.contains("#SeriesDate"))
		    		  SeriesDate = (in.readLine());
		    	  if( s.contains("#Rows"))
		    		  row = Integer.parseInt(in.readLine());
		    	  if( s.contains("#Columns"))
		    		  colum = Integer.parseInt(in.readLine());

		    	  if( s.contains("#HUValue"))
		    	  {
		    		  value =(in.readLine());
		    		  String[] Split = value.split(" ");
		    		  hu = new double[Split.length];



		    		  for(int i=0; i< Split.length;i++)
		    		  {
		    			  hu[i] = Double.parseDouble(Split[i]);

		    		  }
		    	  }
		    	  if( s.contains("#ROIMask"))
		    	  {
		    		  value =(in.readLine());
		    		  String[] Split = value.split(" ");
		    		  if(Split.length==1)
		    			  continue;
		    		  mask = new int[Split.length];



		    		  for(int i=0; i< Split.length;i++)
		    		  {
		    			  mask[i] = Integer.parseInt(Split[i]);

		    		  }
		    	  }

		      }




		      BufferedImage bfDCMImg2 = 	AC_DataConverter.FastSignal2bffImgNmask(hu ,
		    		  row,  colum, 200,
		    		  1000, mask );		

		      String  sSaveFilePath2 = outputPath;

		      String sSavePath = "D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\6666_roi_image\\"+PatientName+"_"+SeriesDate+".png";



		      if(! new File(sSavePath).exists())
		    	  new File(sSavePath).mkdirs();

		      ImageIO.write(bfDCMImg2, "PNG", new File(sSavePath));







		      //50 300






		      //  System.out.print(s);



		      ////////////////////////////////////////////////////////////////
		    } catch (IOException e) {
		    	System.err.println(e); // 에러가 있다면 메시지 출력
		    	System.exit(1);
		    }


	}
	
	public static void setExcel() throws BiffException, IOException, RowsExceededException, WriteException
	{
		List<String> PatientID = new ArrayList();
		List<String> SeriesNUM = new ArrayList();
		List<String> SliceNUM = new ArrayList();
	/*	HashMap<Integer, String> map1 = new HashMap<>();
		HashMap<Integer, String> map2 = new HashMap<>();
		HashMap<Integer, String> map3 = new HashMap<>();

		
		HashMap<Integer, String> map4 = new HashMap<>();
		HashMap<Integer, String> map5 = new HashMap<>();
		HashMap<Integer, String> map6 = new HashMap<>();

		
		HashMap<Integer, String> map7 = new HashMap<>();
		HashMap<Integer, String> map8 = new HashMap<>();*/
		HashMap<Integer, String[]> map9 = new HashMap<>();
		
		String[][] arrr =  new String[21][258];

		
		Workbook workbook2 = Workbook.getWorkbook(new File(
				"D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\sortlist.xls"));
		Sheet sheet2 = workbook2.getSheet(1);  

		for(int i=0;i<247;i++)
		{
			Cell cell = sheet2.getCell(1,i);  
			String key=  cell.getContents(); 
			
			String[] value = new String[9];
			
			
			cell = sheet2.getCell(2,i);  
			value[0] =  cell.getContents(); 
			
			cell = sheet2.getCell(3,i);  
			value[1] =  cell.getContents(); 
			
			cell = sheet2.getCell(4,i);  
			value[2] =  cell.getContents(); 
			
			cell = sheet2.getCell(5,i);  
			value[3] =  cell.getContents(); 
			
			cell = sheet2.getCell(6,i);  
			value[4] =  cell.getContents(); 
			
			cell = sheet2.getCell(7,i);  
			value[5] =  cell.getContents(); 
			
			cell = sheet2.getCell(8,i);  
			value[6] =  cell.getContents(); 
			
			cell = sheet2.getCell(9,i);  
			value[7] =  cell.getContents(); 
			
			cell = sheet2.getCell(10,i);  
			value[8] =  cell.getContents(); 
			
		 
			map9.put(Integer.parseInt(key), value);
		}



		//	try {
		Workbook workbook = Workbook.getWorkbook(new File(
				"D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\CT volumetry and sarcopenia in gastrectomized patients(-2016.12)_VOL_FINAL_용빈쌤전달.xls"));
		Sheet sheet = workbook.getSheet(0);  


		for(int i=1;i<258;i++)
		{
			int idx = i;
			System.out.println(i);
			// cell을 생성해서 Sheet의 내용을 cell단위로 분리해서 읽습니다.
			Cell cell = sheet.getCell(0,idx);  
			arrr[0][i]=  cell.getContents(); 
			
			String[] mapvalue = new String[9];
			try {
				 mapvalue = map9.get( Integer.parseInt(	arrr[0][i]));
				 if(mapvalue== null)
					 continue;
			}catch (Exception e) {
				// TODO: handle exception
			}
			
			
			
			 cell = sheet.getCell(1,idx);  
			arrr[1][i]=  cell.getContents(); 
			
			 cell = sheet.getCell(2,idx);  
			arrr[2][i]=  cell.getContents(); 
			
			 cell = sheet.getCell(3,idx);  
			arrr[3][i]=  cell.getContents(); 
			
			
			
			//mapp
			 //cell = sheet2.getCell(2,idx);  
			arrr[4][i]=  mapvalue[0];
			
			// cell = sheet2.getCell(3,idx);  
			arrr[5][i]=  mapvalue[1];
			
			// cell = sheet2.getCell(4,idx);  
			arrr[6][i]=  mapvalue[2];
			
			
			
			 cell = sheet.getCell(5,idx);  
			arrr[7][i]=  cell.getContents(); 
			
			 cell = sheet.getCell(6,idx);  
			arrr[8][i]=  cell.getContents(); 
			
			 cell = sheet.getCell(7,idx);  
			arrr[9][i]=  cell.getContents(); 
			
			//map
			
			// cell = sheet2.getCell(5,idx);  
			arrr[10][i]=  mapvalue[3];
	
			// cell = sheet2.getCell(6,idx);  
			arrr[11][i]= mapvalue[4];
			                                
			// cell = sheet2.getCell(7,idx);  
			arrr[12][i]= mapvalue[5];
			
			
			                                
			 cell = sheet.getCell(9,idx);  
			arrr[13][i]= cell.getContents();

			 cell = sheet.getCell(10,idx);  
			arrr[14][i]=  cell.getContents(); 
			
			 cell = sheet.getCell(11,idx);  
			arrr[15][i]=  cell.getContents(); 
			
			 cell = sheet.getCell(12,idx);  
			arrr[16][i]=  cell.getContents(); 
			
			
			//map3
		//	cell = sheet2.getCell(8,idx);  
			arrr[17][i]=mapvalue[6];
                                           
			//cell = sheet2.getCell(9,idx);  
			arrr[18][i]=mapvalue[7];
                                           
			//cell = sheet2.getCell(10,idx);  
			arrr[19][i]=mapvalue[8];
			
			
			//
			cell = sheet.getCell(14,idx);  
			arrr[20][i]=  cell.getContents();
	

			// cell내용을 String형태로 담고, DB에 적재하거나 적절히 사용합니다.
		/*	String sPatiendID =
			int key = Integer.parseInt(sPatiendID);

			cell = sheet.getCell(6,idx);  

			// cell내용을 String형태로 담고, DB에 적재하거나 적절히 사용합니다.
			String sSliceNum = cell.getContents();//.replaceAll("[^0-9]", "");
			if(map1.get(key)==null)
			{
				map1.put(key, sSliceNum);


				cell = sheet.getCell(7,idx);  

				sSliceNum = cell.getContents();//.replaceAll("[^0-9]", "");


				map2.put(key, sSliceNum);



				cell = sheet.getCell(8,idx);  

				sSliceNum = cell.getContents();

				map3.put(key, sSliceNum);



			}
			else if(map4.get(key)==null)
			{
				map4.put(key, sSliceNum);


				cell = sheet.getCell(7,idx);  

				sSliceNum = cell.getContents();//.replaceAll("[^0-9]", "");


				map5.put(key, sSliceNum);



				cell = sheet.getCell(8,idx);  

				sSliceNum = cell.getContents();

				map6.put(key, sSliceNum);



			}
			else if(map7.get(key)==null)
			{
				map7.put(key, sSliceNum);


				cell = sheet.getCell(7,idx);  

				sSliceNum = cell.getContents();//.replaceAll("[^0-9]", "");


				map8.put(key, sSliceNum);



				cell = sheet.getCell(8,idx);  

				sSliceNum = cell.getContents();

				map9.put(key, sSliceNum);



			}*/
		}

		
		
		
		
			
			String outroot = "D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)";

			ExcelIO excelop = new ExcelIO(outroot+ FILE_SEP +"sortlist6.xls");
			
			excelop.addColumn(0, arrr[0]  );
			                       
			excelop.addColumn(1, arrr[1]);
			excelop.addColumn(2, arrr[2]);
			excelop.addColumn(3, arrr[3]);
			                        
			excelop.addColumn(4, arrr[4] );
			excelop.addColumn(5, arrr[5] );
			excelop.addColumn(6, arrr[6] );
			                       
			excelop.addColumn(7, arrr[7] );
			excelop.addColumn(8, arrr[8] );
			excelop.addColumn(9, arrr[9] );
			
			excelop.addColumn(10, arrr[10] );  
			excelop.addColumn(11, arrr[11] );  
			excelop.addColumn(12, arrr[12] );  
			                    
			excelop.addColumn(13, arrr[13] );  
			excelop.addColumn(14, arrr[14] );  
			excelop.addColumn(15, arrr[15] );  
			                    
			excelop.addColumn(16, arrr[16] );  
			excelop.addColumn(17, arrr[17] );  
			excelop.addColumn(18, arrr[18] );  
			                    
			excelop.addColumn(19, arrr[19] );  
			excelop.addColumn(20, arrr[20] );  
		
		  	
			
			excelop.writeExcelFile();
			
			
			
			
			
	}
	
	public static void  foler()
	{
		String sTarget = "D:\\98_data\\01_Sarcopenia\\06_김홍규교수님(건강의학)\\02_DCM_DATA_ANOMIZATION";
		
		File[] ddd = new File(sTarget).listFiles(); 
		
		
		
		String sCopyFolrt = "D:\\98_data\\01_Sarcopenia\\06_김홍규교수님(건강의학)\\03_ONLY_RAW\\";
		
		for(int i=1; i<501; i++)
		{
			String name =sTarget+ "\\KHG_Health"+i;
			
			
			
			
			
			
			File[] ttt= new File(name).listFiles();

			for(File tt2 : ttt)
			{
				if(tt2.isDirectory())
					continue;
				
				String[] split = tt2.getAbsolutePath().split("\\\\");
				
				SYBFileIO.fileCopy(name+"\\\\"+ tt2.getName().split("_")[0]+"_Raw.txt", 
						sCopyFolrt+
								split[5]+"\\\\"+tt2.getName().split("_")[0]+"_Raw.txt");
				
				
			
			}
			
	
	
		}
	}
	
	
	public static void  cccckkk()
	{
		File[] list = new File("D:\\98_data\\01_Sarcopenia\\sikoopark\\Sarcopenia").listFiles();
		//String name ="D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\DCM_DATA_ANOMIZATION\\Stomach";
		String[] lis2t = new String[200];
		int dix =0; 
		for(File tmp : list)
		{
			//if(tmp.getName().contains("ROI"))
			{
				boolean chk = false;
				
				lis2t[dix] = tmp.getAbsolutePath();
				File[] list2 = tmp.listFiles();
				for(File tmp2 : list2)
				{
					if(tmp2.getName().contains("Mask"))
					{
						chk = true;
						break;
					}
						
					
				
				}
				if(!chk)
					System.out.println("No Raw"+ tmp.getAbsolutePath());
			}
					
		}
		
	}
	
	
	
	public static void test32312312312() throws Exception
	{
		File[] pnglist = new File("D:\\98_data\\01_Sarcopenia\\sikoopark\\Sarcopenia").listFiles();
		String[] path = new String[600];
		int idx2 = 0;
		for(File tmp55:pnglist)
		{
			//if(tmp55.getName().contains("_ROI"))
			{
			//	System.out.println(tmp55.getName());
				boolean chk =false;
				for(File tmp123:tmp55.listFiles() )
				{
					
					//System.out.println(tmp123.getName());
					if(tmp123.getName().contains("png"))
					{
						System.out.println(tmp123.getName());
						path[idx2] = tmp123.getAbsolutePath();
						idx2++;
						chk =true;
						break;
					}
					
				
					
				}
				if(!chk)
				{
					path[idx2] = null;
					idx2++;;
				}
			
			}
		}
		
		

		for(int idx =0; idx<100;idx++)
		{
			if(path[idx]==null)
			{
				System.out.println("pass  SarcopeniaTest+1"+idx);
				continue;
				
			}
			System.out.println("prosess sarco"+(idx+1));
			String  sSavepath = "D:\\98_data\\test\\02_이인섭교수님\\"
					+ "Stomach"+(idx+1);
			File[] tmp = new File(sSavepath).listFiles();
			for(File tmp2:tmp)
			{
				if(tmp2.getName().contains(".txt"))
				{
					sSavepath = tmp2.getAbsolutePath();
					
				}
			}


		BufferedWriter fw = new BufferedWriter(new FileWriter(sSavepath,true));
			fw.newLine();
			fw.write("#ROILine");
			fw.newLine();
			
			
		
			SYBOpenCV.loadOpenCV_Lib();

			SYBOpenCV imp2Mask = new SYBOpenCV();
			imp2Mask.LoadImgFile(path[idx]);

			//float[] fHUValue = dcmInput.getHUPixelValue();
			for (int i = 0; i < imp2Mask.m_intMask.length; i++)
			{
				String txt = Integer.toString(imp2Mask.m_intMask[i]) + " ";
				fw.write(txt);
				fw.flush();
			}


			fw.close();
		}
		
		
		
	}
	
	public static void testeststestset() throws Exception
	{
		for(int idx =0; idx<191;idx++)
		{
			String  sSavepath = "D:\\98_data\\01_Sarcopenia\\03_TestSet\\Final\\"
					+ "SarcopeniaTest"+(idx);
			File[] tmp = new File(sSavepath).listFiles();
			for(File tmp2:tmp)
			{
				if(tmp2.getName().contains(".txt"))
				{
					sSavepath = tmp2.getAbsolutePath();
					break;
				}
			}
		//	makePNG(sSavepath);
		}
		
	
		
	}
	
	public static void listExcelCopyDCM() throws BiffException, IOException
	{
		String input = "D:\\98_data\\01_Sarcopenia\\01_김효상교수님(kideny)\\LPartRange\\SEList.xls";
		Workbook workbook = Workbook.getWorkbook(new File(input));
		Sheet sheet = workbook.getSheet(0);
		HashMap<String, Integer> fruitMap = new HashMap();
		
		for(int i=248; i<424; i++)
		{
			Cell cID = sheet.getCell(0, i); 
			NumberCell cSliceNum = (NumberCell) sheet.getCell(1, i); 
			
			String sID =cID.getContents();
			int sSliceNum =(int) cSliceNum.getValue();
			
			
			fruitMap.put(sID, sSliceNum);
			
		}
		
		File[] rootlist = new File("E:\\#1 분석\\김효상교수님\\신용빈선생님_Kidney").listFiles();
		
		for(File fPatientList : rootlist)
		{
			String[] sSplitFileName = fPatientList.getName().split("_");
			String sPatientID =  sSplitFileName[0];
			String sDate =  sSplitFileName[sSplitFileName.length-1];
		
			
			
			File[] fSereislist = fPatientList.listFiles();
			
			if(fSereislist==null)
				continue;
			
			
			System.out.println(sPatientID+"_Start");
			
			
			
			for(File fSereis : fSereislist)
			{
				String[] sSplitSeresName = fSereis.getName().split("_");
				


				
				int iSereisName =  Integer.parseInt(sSplitSeresName[0]);
				
				System.out.println("ID :" +sPatientID);
				System.out.println("SliceNum :" +iSereisName);
				
				if(fruitMap.get(sPatientID)==null)
					break;
				
				int iListNum = fruitMap.get(sPatientID);
				
				
				if(iListNum==iSereisName)
				{
					System.out.println(sPatientID+"_"+iSereisName);
					
				//	String inFileName = "";
					String outFileName = "D:\\98_data\\01_Sarcopenia\\01_김효상교수님(kideny)\\LPartRange\\01_DCM_DATA\\"
							+sPatientID+"_"+ sDate+"_DCM";
					
					SYBFileIO.dirCopy(fSereis.getAbsolutePath(), outFileName);
					
					
					break;
					
				}
			
			}
			
		}
		
		
		
		
	}
	public static void chkDirFileNum()
	{
		String sPath = "D:\\98_data\\01_Sarcopenia\\01_김효상교수님(kideny)\\LPartRange\\DCM_ANO";
		File[] fList = new File(sPath).listFiles();
		
		
		for(File sPatient:fList)
		{
			File ste1 = sPatient.listFiles()[0];
			
			System.out.println(sPatient.getName()+"_"+ ste1.listFiles().length);
		}
		
	}
	
	public static void sibal()
	{
		String sPath = "D:\\98_data\\01_Sarcopenia\\01_김효상교수님(kideny)\\LPartRange\\DCM_ANO";
		File[] fList = new File(sPath).listFiles();
		

		for(File sPatient:fList)
		{
			File[] ste1 = sPatient.listFiles();
			
			for(File tmp:ste1)
			{
				if(tmp.isFile())
					tmp.delete();
			}
			//System.out.println(sPatient.getName()+"_"+ ste1.listFiles().length);
		}
	}
	
	
	public static void sortImagenci() throws BiffException, IOException
	{
		
		
		
		String input = "D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\서동우선생님 internal validation (1).xls";
		Workbook workbook = Workbook.getWorkbook(new File(input));
		Sheet sheet = workbook.getSheet(0);
		HashMap<String, List<String>> fruitMap = new HashMap();
		
		for(int i=1; i<236; i++)
		{
			Cell cID = sheet.getCell(0, i); 
			Cell cSliceNum = sheet.getCell(2, i); 
			
			String sID =cID.getContents();
			String sSliceNum = cSliceNum.getContents();
			
			sSliceNum.replaceAll("-","");		
			
			System.out.println("ID : " + sID + " Date : "+ sSliceNum);
			
			if(fruitMap.get(sID)==null)
			{
				List<String> tmp = new ArrayList<String>();
				tmp.add(sSliceNum);
				fruitMap.put(sID, tmp);
			}else
			{
				List<String> tmp =fruitMap.get(sID);
				tmp.add(sSliceNum);
				
				fruitMap.put(sID, tmp);
				
			}

		}
		
		//List<String> PatientID = new ArrayList<String>();
		
		HashMap<String, String> PatientIDList = new HashMap();
		

		String sPath = "E:\\#2.임상연구\\Sarcopenia연구\\아산응급_Septic shock\\#2.170512 PaE_Sarcopenia_CT_환자데이터\\영상";
		File[] fList = new File(sPath).listFiles();
		
		int idx = 0;
		
		for(File tmplist : fList)
		{
			String[] sSplitname = tmplist.getName().split("_");
			
			List<String> sMacthData = fruitMap.get(sSplitname[0]);
		
			
			if(sMacthData == null)
			{
				//System.out.println("!!!!!!!!!!!!!!!!!!!!!");
				continue;
			}
			
			if(sSplitname[sSplitname.length-1].contains("ROI"))
			{
				//System.out.println(tmplist.getName()+" skip");
				continue;
			}else
			{
				
				String[] sDate = sMacthData.toArray(new String[sMacthData.size()]);
				for(String sDateEchg : sDate)
		    	{
					if(sSplitname[sSplitname.length-1].contains(sDateEchg))
					{
						idx++;
						System.out.println(idx+" key : "+sSplitname[0]+" date : "+ sDateEchg+ "copy!!!!!!");
						
						
						String outpath = 
								"D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\01_DCM_DATA\\"
								+sSplitname[0]+"_"+sDateEchg+"_DCM";
						
						
						
						SYBFileIO.dirCopy(tmplist.getAbsolutePath(), outpath);
						//System.out.println(sSplitname[0]+" "+ sDateEchg);
						
						break;
					}
		    	
		    	}
				
			}
			
		}
	 /*   String[] key = 	PatientIDList.keySet().toArray(new String[	PatientIDList.keySet().size()]);
	    
	    for(String skey : key)
	    {
	    	List<String> tmp = fruitMap.get(skey);
	    	String[] sDate = tmp.toArray(new String[tmp.size()]);
	    	for(String sDateEchg : sDate)
	    	{
	    		
	    		System.out.println("key : "+skey+" date : "+ sDateEchg);
	    	}
	    }*/
		
	}
	
	
	public static void ttt()
	{
		String sPath = "D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\02_DCM_DATA_ANOMIZATION";
		File[] fList = new File(sPath).listFiles();
		
		int sum = 0;
		for(File tmp : fList)
		{
		//	String[] sSplitname = tmp.getName().split("_");
			
			
			
			
			sum += tmp.listFiles().length;
			
			
		}
		
		System.out.println(sum);
	}
	

	public static void sortImagenci1() throws BiffException, IOException
	{
		
		
		
		String input = "D:\\98_data\\01_Sarcopenia\\06_김홍규교수님(건강의학)\\L3_.xls";
		Workbook workbook = Workbook.getWorkbook(new File(input));
		Sheet sheet = workbook.getSheet(0);
		HashMap<String, List<String>> fruitMap = new HashMap();
		
		for(int i=1; i<236; i++)
		{
			Cell cID = sheet.getCell(0, i); 
			Cell cSliceNum = sheet.getCell(2, i); 
			
			String sID =cID.getContents();
			String sSliceNum = cSliceNum.getContents();
			
			sSliceNum.replaceAll("-","");		
			
			System.out.println("ID : " + sID + " Date : "+ sSliceNum);
			
			if(fruitMap.get(sID)==null)
			{
				List<String> tmp = new ArrayList<String>();
				tmp.add(sSliceNum);
				fruitMap.put(sID, tmp);
			}else
			{
				List<String> tmp =fruitMap.get(sID);
				tmp.add(sSliceNum);
				
				fruitMap.put(sID, tmp);
				
			}

		}
		
		//List<String> PatientID = new ArrayList<String>();
		
		HashMap<String, String> PatientIDList = new HashMap();
		

		String sPath = "E:\\#2.임상연구\\Sarcopenia연구\\아산응급_Septic shock\\#2.170512 PaE_Sarcopenia_CT_환자데이터\\영상";
		File[] fList = new File(sPath).listFiles();
		
		int idx = 0;
		
		for(File tmplist : fList)
		{
			String[] sSplitname = tmplist.getName().split("_");
			
			List<String> sMacthData = fruitMap.get(sSplitname[0]);
		
			
			if(sMacthData == null)
			{
				//System.out.println("!!!!!!!!!!!!!!!!!!!!!");
				continue;
			}
			
			if(sSplitname[sSplitname.length-1].contains("ROI"))
			{
				//System.out.println(tmplist.getName()+" skip");
				continue;
			}else
			{
				
				String[] sDate = sMacthData.toArray(new String[sMacthData.size()]);
				for(String sDateEchg : sDate)
		    	{
					if(sSplitname[sSplitname.length-1].contains(sDateEchg))
					{
						idx++;
						System.out.println(idx+" key : "+sSplitname[0]+" date : "+ sDateEchg+ "copy!!!!!!");
						
						
						String outpath = 
								"D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\01_DCM_DATA\\"
								+sSplitname[0]+"_"+sDateEchg+"_DCM";
						
						
						
						SYBFileIO.dirCopy(tmplist.getAbsolutePath(), outpath);
						//System.out.println(sSplitname[0]+" "+ sDateEchg);
						
						break;
					}
		    	
		    	}
				
			}
			
		}
	 /*   String[] key = 	PatientIDList.keySet().toArray(new String[	PatientIDList.keySet().size()]);
	    
	    for(String skey : key)
	    {
	    	List<String> tmp = fruitMap.get(skey);
	    	String[] sDate = tmp.toArray(new String[tmp.size()]);
	    	for(String sDateEchg : sDate)
	    	{
	    		
	    		System.out.println("key : "+skey+" date : "+ sDateEchg);
	    	}
	    }*/
		
	}
	

	public static void MakeRawWithExecl() throws Exception
	{
		
		int iExperinetNum = 461;

		

		String input = "D:\\98_data\\01_Sarcopenia\\05_박인자교수님(위장관외과)\\L3_list.xls";
		Workbook workbook = Workbook.getWorkbook(new File(input));
		Sheet sheet = workbook.getSheet(0);
		String[][] ExperimentList = new String[iExperinetNum][2]; 
		int[] arriSliceNum = new int[iExperinetNum];
		
		for(int i=0; i<iExperinetNum; i++)
		{
			Cell cID = sheet.getCell(0, i); 
			Cell cData = sheet.getCell(2, i); 
			NumberCell cSliceNum = (NumberCell) sheet.getCell(4, i); 
			
			String sID =cID.getContents();
			String sData = cData.getContents();
			
			ExperimentList[i][0] = sID;
			ExperimentList[i][1] = sData;
			arriSliceNum[i] =(int) cSliceNum.getValue();
			
			System.out.println("ID : "+sID+" Date : "+sData+" sliceNum : "+ arriSliceNum[i]);
		}
		
		
		for(int i =0 ; i< iExperinetNum ; i++)
		{
				String path =// "D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\02_DCM_DATA_ANOMIZATION\\"
						"D:\\98_data\\01_Sarcopenia\\05_박인자교수님(위장관외과)\\02_DCM_DATA_ANOMIZATION\\"
						+ExperimentList[i][0]+"\\"+ExperimentList[i][1]+"_DCM";
				
				if(! new File(path).exists())
				{
					System.out.println("noteixits : " + "D:\\98_data\\01_Sarcopenia\\05_박인자교수님(위장관외과)\\02_DCM_DATA_ANOMIZATION\\"
							+ExperimentList[i][0]);
					
					continue;
				}
				else if( new File(path+"\\"+ExperimentList[i][1]+"_RAW.txt").exists())
				{
					
					System.out.println("skip : " + "D:\\98_data\\01_Sarcopenia\\05_박인자교수님(위장관외과)\\02_DCM_DATA_ANOMIZATION\\"
							+ExperimentList[i][0]);
					continue;
				}
				
			
		
				/*if(tmp.isFile())
					continue;
				
				String[] sSplit = tmp.getName().split("_");*/
				
				


				int SliceNum = arriSliceNum[i];
				String result_user_no = String.format("%05d", SliceNum);
				String outFilePaht = result_user_no+".dcm";




				String sSeliceDCMPath = path+"\\"+outFilePaht;//findSlicePath(path, SliceNum);

				Dcm4cheV3io dcmSelicV = new Dcm4cheV3io(sSeliceDCMPath);
				dcmSelicV.calHUPixelValue();

				int[] iEmpty = new int [10];

				makeNewFileType(sSeliceDCMPath, dcmSelicV, iEmpty, SliceNum);
				
				System.out.println("idx : "+ i);
		}
	
		
	}
	public static void replicaeDCMName() throws Exception
	{
	
		
		String sPath = "D:\\98_data\\01_Sarcopenia\\test";//"D:\\98_data\\01_Sarcopenia\\05_박인자교수님(위장관외과)\\02_DCM_DATA_ANOMIZATION";
		//.sPat//String outfileDir = "D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\Test";
		File[] fList = new File(sPath).listFiles();
		
		for(File flistFile : fList)
		{
			File[] tmpDCM = flistFile.listFiles();
			/*for(File tmpDCM1:tmpDCM)
			{
				if(tmpDCM1.isFile())
					continue;
				File[] tmpDCM2 = tmpDCM1.listFiles();*/
			
			for(File tmpDcmfile :tmpDCM)
			{
				Dcm4cheV3io dcmInfo2 = new Dcm4cheV3io(tmpDcmfile);
				
				
				String result_user_no = String.format("%05d", Integer.parseInt(dcmInfo2.getDicomTag2String(Tag.InstanceNumber)));
				String outFilePaht = result_user_no+".dcm";
				
				SYBFileIO.replaceFileName(tmpDcmfile.getAbsolutePath(), outFilePaht);
			
			}
			
		}
	}
	
	public static void makeJPgALL() throws Exception
	{
		String sPath = "D:\\98_data\\01_Sarcopenia\\06_김홍규교수님(건강의학)\\02_DCM_DATA_ANOMIZATION";
		String sOUTPath = "D:\\98_data\\01_Sarcopenia\\06_김홍규교수님(건강의학)\\05_L3_IMAGE";
		File[] fList = new File(sPath).listFiles();
		for(File flistFile : fList)
		{
			File[] fDateList = flistFile.listFiles();
			for(File fDate:fDateList)
			{
				if(!fDate.isFile())
					continue;
				makePNG(fDate.getAbsolutePath(),sOUTPath);
				
				System.out.println("done.. " +flistFile.getName());
			}
		}
		
		
	}
	
	
	public static void chkFileNum() throws Exception
	{
		String sPath = "D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\03_ONLY_RAW";
		String sOUTPath = "D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\05_L3_IMAGE";
		File[] fList = new File(sPath).listFiles();
		
		
		int iSumdir = 0;
		int iSumRaw = 0;
		int idx = 0;
		for(File flistFile : fList)
		{
			File[] fDateList = flistFile.listFiles();
			for(File fDate:fDateList)
			{
				if(!fDate.isFile())
				{
					iSumdir++;
					continue;
				}
				iSumRaw++;
			}
		//	System.out.println("flistFile " +flistFile.getName());
		
		}
		System.out.println("DIR : " +iSumdir +" RAW : "+iSumRaw);
		
	}
	
	

	public static void sortOnlyOneDateWithSeriesNum() throws Exception
	{
		
		
		
		String input = "D:\\98_data\\01_Sarcopenia\\06_김홍규교수님(건강의학)\\L3_List.xls";
		Workbook workbook = Workbook.getWorkbook(new File(input));
		Sheet sheet = workbook.getSheet(0);
		HashMap<String, Integer> ExperimentList = new HashMap();
		
		for(int i=0; i<500; i++)
		{
			Cell cID = sheet.getCell(0, i); 
			//Cell cData = sheet.getCell(5, i); 
			NumberCell cSliceNum = (NumberCell) sheet.getCell(3, i); 
			
			String sID =cID.getContents();
			
			int iSliceNum = (int) cSliceNum.getValue();
			ExperimentList.put(sID, iSliceNum);;
			
			
			
			System.out.println("ID : "+sID+" Date : "+cSliceNum);

		}
		
		//List<String> PatientID = new ArrayList<String>();
		
		HashMap<String, String> PatientIDList = new HashMap();
		

		String sPath = "D:\\98_data\\01_Sarcopenia\\06_김홍규교수님(건강의학)\\01_DCM_DATA";
		String sPath2 = "D:\\98_data\\01_Sarcopenia\\06_김홍규교수님(건강의학)\\01_DCM_DATA_sort";
		File[] fList = new File(sPath).listFiles();
		
		int idx = 0;
		
		for(File tmplist : fList)
		{
			String sSplitname = tmplist.getName();
			
			int iSeriesNum = ExperimentList.get(sSplitname);
			
			
		
			
			for(File tmpDcmfile : tmplist.listFiles())
			{
				Dcm4cheV3io dcmInfo2 = new Dcm4cheV3io(tmpDcmfile);
				
				int iGetSeriesNum =Integer.parseInt(dcmInfo2.getDicomTag2String(Tag.SeriesNumber));
				
			
				
			
				
				if(new File(sPath2+ "\\"+sSplitname+"_"+dcmInfo2.getDicomTag2String(Tag.SeriesDate)+"_DCM").exists())
				{
					System.out.println("skep : " +sSplitname );
					break;
				}
				
				
				
				if(iSeriesNum==iGetSeriesNum)
				{
					
					String result_user_no = String.format("%05d", Integer.parseInt(dcmInfo2.getDicomTag2String(Tag.InstanceNumber)));
					String outFilePaht = result_user_no+".dcm";
					String outPaht = 	sPath2 + "\\"+sSplitname+"_"+dcmInfo2.getDicomTag2String(Tag.SeriesDate)+"_DCM\\"+outFilePaht;
					
				
				
					
					SYBFileIO.fileCopy(tmpDcmfile.getAbsolutePath(), outPaht);
					//System.out.println("notSEr : "+sSplitname);
				}
				
				
			
			
			}
			
			
			
			
			
			
		}
	
		
	}
	
	
	
	public static void makeRawOnlyOneDateWithInstanceNum() throws Exception
	{
		
		
		
		String input = "D:\\98_data\\01_Sarcopenia\\06_김홍규교수님(건강의학)\\02_L3_List.xls";
		Workbook workbook = Workbook.getWorkbook(new File(input));
		Sheet sheet = workbook.getSheet(0);
		HashMap<String, Integer> ExperimentList = new HashMap();
		
		for(int i=0; i<500; i++)
		{
			Cell cID = sheet.getCell(0, i); 
			//Cell cData = sheet.getCell(5, i); 
			NumberCell cSliceNum = (NumberCell) sheet.getCell(4, i); 
			
			String sID =cID.getContents();
			
			int iSliceNum = (int) cSliceNum.getValue();
			ExperimentList.put(sID, iSliceNum);;
			
			
			
			System.out.println("ID : "+sID+" Date : "+cSliceNum);

		}
		
		//List<String> PatientID = new ArrayList<String>();
		
		HashMap<String, String> PatientIDList = new HashMap();
		

		String sPath = "D:\\98_data\\01_Sarcopenia\\06_김홍규교수님(건강의학)\\02_DCM_DATA_ANOMIZATION";
	//	String sPath2 = "D:\\98_data\\01_Sarcopenia\\06_김홍규교수님(건강의학)\\01_DCM_DATA_sort";
		File[] fList = new File(sPath).listFiles();
		
		int idx = 0;
		
		for(File tmplist : fList)
		{
			File sSplitname = tmplist.listFiles()[0];
			
			
			
			
			
			
			
			//int iSeriesNum = ExperimentList.get(sSplitname.get);
			
			




		/*	String sSeliceDCMPath = findSlicePath(sSplitname.getAbsolutePath(), iSeriesNum);

			Dcm4cheV3io dcmSelicV = new Dcm4cheV3io(sSeliceDCMPath);
			dcmSelicV.calHUPixelValue();

			int[] iEmpty = new int [10];

			makeNewFileType(sSeliceDCMPath, dcmSelicV, iEmpty, iSeriesNum, 1);
			
			System.out.println("idx : "+ idx++);*/
			
			
			
			
			
			
		}
	
		
	}
	
	
	public static void copyList() throws Exception
	{
		
		
		
		String input = "D:\\98_data\\03_AiCRO_Dev\\Sarcopenia_data\\List.xls";
		Workbook workbook = Workbook.getWorkbook(new File(input));
		Sheet sheet = workbook.getSheet(0);
		HashMap<String, Boolean> ExperimentList = new HashMap();
		for(int i=0; i<47; i++)
		{
			Cell cID = sheet.getCell(0, i); 
			//Cell cData = sheet.getCell(5, i); 
			//NumberCell cSliceNum = (NumberCell) sheet.getCell(4, i); 
			
			String sID =cID.getContents();
			
			//int iSliceNum = (int) cSliceNum.getValue();
			ExperimentList.put(sID, true);;
			
			
			
			System.out.println("ID : "+sID);

		}
		
		
		HashMap<String, String> PatientIDList = new HashMap();
		
		String rootFilePath = "D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\99_RECEIVE_DATA\\CT volumetry and sarcopenia in gastrectomized patients(-2016.12)_수술전";
		
		for(File sIDPatient : new File(rootFilePath).listFiles())
		{
			String pid = sIDPatient.getName().split("_")[0];
			if(ExperimentList.containsKey(pid) && sIDPatient.getName().toUpperCase().contains("ROI") )
			{
				String outpfile = "D:\\98_data\\new file\\new\\ROI"+sIDPatient.getAbsolutePath().replace(rootFilePath, "");
				SYBFileIO.dirCopy(sIDPatient.getAbsolutePath(), outpfile);
			}
		}
	
		
		
		//List<String> PatientID = new ArrayList<String>();
		
		/*HashMap<String, String> PatientIDList = new HashMap();
		

		String sPath = "D:\\98_data\\01_Sarcopenia\\06_김홍규교수님(건강의학)\\02_DCM_DATA_ANOMIZATION";
	//	String sPath2 = "D:\\98_data\\01_Sarcopenia\\06_김홍규교수님(건강의학)\\01_DCM_DATA_sort";
		File[] fList = new File(sPath).listFiles();
		
		int idx = 0;
		
		for(File tmplist : fList)
		{
			File sSplitname = tmplist.listFiles()[0];
			
			
			
			
			
			
			
			//int iSeriesNum = ExperimentList.get(sSplitname.get);
			
			




		/*	String sSeliceDCMPath = findSlicePath(sSplitname.getAbsolutePath(), iSeriesNum);

			Dcm4cheV3io dcmSelicV = new Dcm4cheV3io(sSeliceDCMPath);
			dcmSelicV.calHUPixelValue();

			int[] iEmpty = new int [10];

			makeNewFileType(sSeliceDCMPath, dcmSelicV, iEmpty, iSeriesNum, 1);
			
			System.out.println("idx : "+ idx++);*/
			
			
			
			
			
			
		//}
	
		
	}
	
	public static void anlsis() throws Exception
	{
		String root = "D:\\AICORTEST_DOWN\\sa1\\Sarcopenia\\ROI (1)";
		String togetroot = "D:\\AICORTEST_DOWN\\sa1\\Sarcopenia";
		
		File[] list = new File(root).listFiles();
		
		for(File tmp : list)
		{
			String UID = tmp.getName().split("_")[0];
			for(File tmp2 : new File(togetroot).listFiles())
			{
				if(tmp2.isDirectory())
				{
					for(File tmp3 : tmp2.listFiles())
					{
						if(tmp3.getName().contains(UID))
						{
							
							String outpath = "D:\\AICORTEST_DOWN\\test\\test\\"+tmp2.getName()+"\\"+tmp3.getName()+"_image";
							String outpath2 = "D:\\AICORTEST_DOWN\\test\\test\\"+tmp2.getName()+"\\"+tmp.getName();
							
							
							SYBFileIO.dirCopy(tmp3.getAbsolutePath(), outpath);
							SYBFileIO.dirCopy(tmp.getAbsolutePath(), outpath2);
							
						}
					}
					
				}
			}
			
			
		}
	}
	

	public static void nUMBV() throws Exception
	{
		HashMap<String, HashMap<String, Boolean>>list = new HashMap<>();
		
		File[] fileList = new File("D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\01_DCM_DATA").listFiles();
		
		for(File tmp : fileList)
		{
			String[] splitName = tmp.getName().split("_");
			String PID = splitName[0];
			String date = splitName[1];
			
			HashMap<String, Boolean> tmpDateMap = new HashMap<>();
			
			if(list.containsKey(PID))
			{
				tmpDateMap = list.get(PID);
			}
			tmpDateMap.put(date, true);
			list.put(PID, tmpDateMap);
		}
		fileList = new File("D:\\100_Backup\\BackUp-전컴퓨터\\98_data\\01_sarcopenia\\99_받은파일(지숙샘)\\Sarcopenia\\#5.추가 Data\\영상").listFiles();
		
		for(File tmp : fileList)
		{
			if(!tmp.getName().contains("ROI"))
				continue;
			
			String[] splitName = tmp.getName().split("_");
			String PID = splitName[0];
			String date = splitName[splitName.length-2];
			if(list.containsKey(PID))
			{
				HashMap<String, Boolean> tmpDateMap = list.get(PID);
				if(tmpDateMap.containsKey(date))
				{	String refilename = PID+"_"+date+"_ROI";
					SYBFileIO.dirCopy(tmp.getAbsolutePath(), "D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\01_ROI_DATA\\"+refilename);
				}
			
			}
			//System.out.println();
		
		}
		
		
	
	}
	public static boolean[] getMask(Roi input1, Roi input2, int iImgWidth,int iImgHeghit)
	{
		boolean[] output = new boolean[iImgWidth*iImgHeghit];
		byte[] mask1 = (byte[]) input1.getMask().getPixels();
		byte[] mask2 = (byte[]) input2.getMask().getPixels();
		
		for(int i=0; i<output.length;i++)
			output[i] = false;
		

		int x1 = input1.getBounds().x;
		int y1 = input1.getBounds().y;
		int w1 = input1.getBounds().width;
		int h1 = input1.getBounds().height;
		
		int x2 = input2.getBounds().x;
		int y2 = input2.getBounds().y;
		int w2 = input2.getBounds().width;
		int h2 = input2.getBounds().height;
		
		for(int i=0; i<w1;i++)
		{
			for(int j=0; j<h1;j++)
			{
				{
					int idx = (j+y1)*iImgHeghit+(i+x1);
					int idx2 =(j)*w1+(i);
		
					if(mask1[idx2] != 0)
						output[idx] = true;
					
				}
				
				
				
			}
			
		}
		
		for(int i=0; i<w2;i++)
		{
			for(int j=0; j<h2;j++)
			{
				{
					int idx = (j+y2)*iImgHeghit+(i+x2);
					int idx2 =(j)*w2+(i);
					
					/*System.out.println("idx :" +idx);
					System.out.println("idx2 :" +idx2);*/
					if(mask2[idx2] != 0 && output[idx])
						 output[idx]=false;
				
					
				}
				
			}
			
		}
		
		
		
		
		
		return output;		
	}
	
	
	public static boolean[] getMask(Roi input1, int iImgWidth,int iImgHeghit)
	{
		if(input1==null)
			return null;
		boolean[] output = new boolean[iImgWidth*iImgHeghit];
	
		byte[] mask1 = (byte[]) input1.getMask().getPixels();
	
		
		for(int i=0; i<output.length;i++)
			output[i] = false;
		

		int x1 = input1.getBounds().x;
		int y1 = input1.getBounds().y;
		int w1 = input1.getBounds().width;
		int h1 = input1.getBounds().height;
	
		
		for(int i=0; i<w1;i++)
		{
			for(int j=0; j<h1;j++)
			{
				{
					int idx = (j+y1)*iImgHeghit+(i+x1);
					int idx2 =(j)*w1+(i);
		
					if(mask1[idx2] != 0)
						output[idx] = true;
					
				}
				
				
				
			}
			
		}
		

		
		
		return output;		
	}
	
	public static boolean[] chkminmak(double[] input, boolean[] mask, double min, double max)
	{
		boolean[] output = new boolean[input.length];
		
		for(int i=0; i<output.length;i++)
		{
			if(input[i]>=min&&input[i]<=max&&mask[i])
				output[i] = true;
		}
		
		
		
		return output;
		
	}
	
	
	
	
	public static int[] gatMAsk(String riofilePath, String dcmfilePath) throws Exception
	{
		
		boolean savefile = false;
		

		 double newMinFat = -190;
		 double newMaxFat = -30;
		 double newMinMuscle = 0;
		 double newMaxMuscle = 100;

		
		FileInputStream fout= new FileInputStream(riofilePath);
		
		ObjectInputStream oos= new ObjectInputStream(fout);
		
		Roi[] tfa = new Roi[1];
		Roi[] sfa       = new Roi[1];
		Roi[] vfa       = new Roi[1];
		Roi[] r_psoas   = new Roi[1];
		Roi[] l_psoas   = new Roi[1];

		Roi[][] totalroi=new Roi[5][tfa.length];

		totalroi = (Roi[][]) oos.readObject();

		tfa =	totalroi[0];
		sfa = totalroi[1];
		vfa = totalroi[2];
		r_psoas = totalroi[3]; 
		l_psoas = totalroi[4]; 
		
		AC_DicomReader dcmReader = new AC_DicomReader(dcmfilePath);
		AC_DicomInfo dcminfo = new AC_DicomInfo();
		byte[] pixel = dcmReader.getPixel(dcminfo);
		double[] dSignal = AC_DataConverter.DCMPixelData2Singnal(pixel, dcminfo);
		
		
		double[] outVfat    =  dSignal.clone();
		double[] outMuslc=  dSignal.clone();
		double[] outSfat    =  dSignal.clone();
	

		
		int iImgWidth = dcminfo.getInt(AC_Tag.Rows);
		int iImgheight = dcminfo.getInt(AC_Tag.Columns);
		
		boolean[] vfmask    =  getMask(tfa[39],  sfa[39],iImgWidth,iImgheight);
		boolean[] muslemask1 = getMask( sfa[39], vfa[39],iImgWidth,iImgheight);
		boolean[] sfatMask = getMask(vfa[39],iImgWidth,iImgheight);
		
		
		boolean[] finalvfmask    =  chkminmak(dSignal, vfmask,newMinFat,newMaxFat);
		boolean[] finalmuslemask1 = chkminmak(dSignal, muslemask1,newMinMuscle,newMaxMuscle);
		boolean[] finalsfatMask = chkminmak(dSignal, sfatMask,newMinFat,newMaxFat);
		
		int[] iMaskMap = new int[finalmuslemask1.length];
		
		for(int i=0; i<finalmuslemask1.length;i++)
		{
			if(finalvfmask[i])
				iMaskMap[i] = 1;
			else if(finalmuslemask1[i])
				iMaskMap[i] = 2;
			else if(finalsfatMask[i])
				iMaskMap[i] = 3;
			else
				iMaskMap[i] = 0;
			
		}
		

		if(savefile)
		{

			for(int i=0; i<vfmask.length;i++)
			{
				if(finalvfmask[i])
					outVfat[i] = 255;;
			}


			for(int i=0; i<vfmask.length;i++)
			{
				if(finalmuslemask1[i])
					outMuslc[i] = 255;;
			}

			for(int i=0; i<vfmask.length;i++)
			{
				if(finalsfatMask[i])
					outSfat[i] = 255;;
			}




			BufferedImage bfDCMImg = 	AC_DataConverter.FastSignal2bffImg(outVfat ,
					dcminfo.getInt(AC_Tag.Columns),    dcminfo.getInt(AC_Tag.Rows), 200,
					1000, dcminfo.getInt(AC_Tag.SamplesperPixel),1 );		


			String sSaveFilePath = "D:\\98_data\\test"+File.separator+"outVfat.png";

			ImageIO.write(bfDCMImg, "PNG", new File(sSaveFilePath));


			bfDCMImg = 	AC_DataConverter.FastSignal2bffImg(outMuslc ,
					dcminfo.getInt(AC_Tag.Columns),    dcminfo.getInt(AC_Tag.Rows), 200,
					1000, dcminfo.getInt(AC_Tag.SamplesperPixel),1 );		



			sSaveFilePath = "D:\\98_data\\test"+File.separator+"outMuslc.png";

			ImageIO.write(bfDCMImg, "PNG", new File(sSaveFilePath));

			bfDCMImg = 	AC_DataConverter.FastSignal2bffImg(outSfat ,
					dcminfo.getInt(AC_Tag.Columns),    dcminfo.getInt(AC_Tag.Rows), 200,
					1000, dcminfo.getInt(AC_Tag.SamplesperPixel),1 );		



			sSaveFilePath = "D:\\98_data\\test"+File.separator+"outSfat.png";

			ImageIO.write(bfDCMImg, "PNG", new File(sSaveFilePath));
		}



		BufferedImage bfDCMImg2 = 	AC_DataConverter.FastSignal2bffImgNmask(outVfat ,
				dcminfo.getInt(AC_Tag.Columns),    dcminfo.getInt(AC_Tag.Rows), 200,
				1000, iMaskMap );		

		String  sSaveFilePath2 = "D:\\98_data\\test"+File.separator+"totalmap.png";

		ImageIO.write(bfDCMImg2, "PNG", new File(sSaveFilePath2));
		return iMaskMap;
		
		
		

		
		
	}
	public static int chkslice (String riofilePath) throws IOException, ClassNotFoundException
	{

		FileInputStream fout= new FileInputStream(riofilePath);

		ObjectInputStream oos= new ObjectInputStream(fout);

		Roi[] tfa = new Roi[1];
		Roi[] sfa       = new Roi[1];
		Roi[] vfa       = new Roi[1];
		Roi[] r_psoas   = new Roi[1];
		Roi[] l_psoas   = new Roi[1];

		Roi[][] totalroi=new Roi[5][tfa.length];

		int slicenum = 0;

		totalroi = (Roi[][]) oos.readObject();

		tfa =	totalroi[0];
		sfa = totalroi[1];
		vfa = totalroi[2];
		r_psoas = totalroi[3]; 
		l_psoas = totalroi[4]; 


		for(int i=0; i<tfa.length ;i++)
		{
			if(tfa[i]!=null)
			{
				slicenum = i;
				break;
			}
		}
		return slicenum;
	}
	
	public static int[] gatMAsk(String riofilePath, double[] dSignal, int iWidth, int iHeght) throws Exception
	{
		
		boolean savefile = false;
		

		 double newMinFat = -190;
		 double newMaxFat = -30;
		 double newMinMuscle = 0;
		 double newMaxMuscle = 100;

		
		FileInputStream fout= new FileInputStream(riofilePath);
		
		ObjectInputStream oos= new ObjectInputStream(fout);
		
		Roi[] tfa = new Roi[1];
		Roi[] sfa       = new Roi[1];
		Roi[] vfa       = new Roi[1];
		Roi[] r_psoas   = new Roi[1];
		Roi[] l_psoas   = new Roi[1];

		Roi[][] totalroi=new Roi[5][tfa.length];
		
		int slicenum = 0;

		totalroi = (Roi[][]) oos.readObject();

		tfa =	totalroi[0];
		sfa = totalroi[1];
		vfa = totalroi[2];
		r_psoas = totalroi[3]; 
		l_psoas = totalroi[4]; 
		
		
		for(int i=0; i<tfa.length ;i++)
		{
			if(tfa[i]!=null)
			{
				slicenum = i;
				break;
			}
		}
		System.out.println("slicenum : " +slicenum);
				

		
		
		double[] outVfat    =  dSignal.clone();
		double[] outMuslc=  dSignal.clone();
		double[] outSfat    =  dSignal.clone();
	

		
		int iImgWidth = iWidth;
		int iImgheight = iHeght;
		
		boolean[] vfmask    =  getMask(tfa[slicenum],  sfa[slicenum],iImgWidth,iImgheight);
		boolean[] muslemask1 = getMask( sfa[slicenum], vfa[slicenum],iImgWidth,iImgheight);
		boolean[] sfatMask = getMask(vfa[slicenum],iImgWidth,iImgheight);
		
		
		boolean[] finalvfmask    =  chkminmak(dSignal, vfmask,newMinFat,newMaxFat);
		boolean[] finalmuslemask1 = chkminmak(dSignal, muslemask1,newMinMuscle,newMaxMuscle);
		boolean[] finalsfatMask = chkminmak(dSignal, sfatMask,newMinFat,newMaxFat);
		
		int[] iMaskMap = new int[finalmuslemask1.length];
		
		for(int i=0; i<finalmuslemask1.length;i++)
		{
			if(finalvfmask[i])
				iMaskMap[i] = 1;
			else if(finalmuslemask1[i])
				iMaskMap[i] = 2;
			else if(finalsfatMask[i])
				iMaskMap[i] = 3;
			else
				iMaskMap[i] = 0;
			
		}
		




		BufferedImage bfDCMImg2 = 	AC_DataConverter.FastSignal2bffImgNmask(outVfat ,
				iWidth,  iHeght, 200,
				1000, iMaskMap );		

		String  sSaveFilePath2 = riofilePath.replace("01_ROI_DATA", "000_roi_png");
		sSaveFilePath2 = riofilePath.replace("01_ROI_DATA", "000_roi_png");
		
		sSaveFilePath2 = sSaveFilePath2.replaceAll("aroi", "png");
		if(! new File(sSaveFilePath2).exists())
			new File(sSaveFilePath2).mkdirs();

		ImageIO.write(bfDCMImg2, "PNG", new File(sSaveFilePath2));
		return iMaskMap;
		
		
		

		
		
	}
	
	public static void makaROIRaw() throws Exception
	{
		
		
		
		String input = "D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\03_20180831_AMOMIZATION_MAP.xls";
		Workbook workbook = Workbook.getWorkbook(new File(input));
		Sheet sheet = workbook.getSheet(0);
		HashMap<String, String> ExperimentList = new HashMap();
		
		for(int i=0; i<235; i++)
		{
			Cell cID = sheet.getCell(0, i); 
			//Cell cData = sheet.getCell(5, i); 
			Cell cPatien = sheet.getCell(1, i); 
			
			String sID =cID.getContents();
			String sPatien =cPatien.getContents();
			
		
			ExperimentList.put(sID, sPatien);;
			
			
			
			System.out.println("ID : "+sID+" Date : "+sPatien);

		}
		
		
		
		
		
		
		
		File[] rootlist = new File("D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\03_ONLY_RAW").listFiles();
		
	
		
		for(File fID : rootlist)
		{
			for(File fRaw : fID.listFiles())
			{
				
				System.out.println("raw path : " + fRaw.getAbsolutePath());
				
		
				BufferedReader in = new BufferedReader(new FileReader(fRaw.getAbsolutePath()));
				String s;
				String sPatientid = null;
				String sdata = null;
				int row = 0;
				int colum = 0;
				double[] hu = null;
				int[] mask = null;
				String value ="";
				
				while ((s = in.readLine()) != null) 
				{
					if( s.contains("#PatientName"))
						sPatientid = (in.readLine());
					if( s.contains("#SeriesDate"))
						sdata = (in.readLine());
					if( s.contains("#Rows"))
						row = Integer.parseInt(in.readLine());
					if( s.contains("#Columns"))
						colum = Integer.parseInt(in.readLine());

					/*if( s.contains("#HUValue"))
					{
						value =(in.readLine());
						String[] Split = value.split(" ");
						hu = new double[Split.length];


					for(int i=0; i< Split.length;i++)
						{
							hu[i] = Double.parseDouble(Split[i]);

						}
					}*/
				}
				
				String Patientid = ExperimentList.get(sPatientid);
				
				String sROIpath = "D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\01_ROI_DATA\\"+Patientid+"_"+sdata+"_ROI\\"+Patientid+".aroi";
				if(!new File(sROIpath).exists())
				{
					System.out.println("!!!!!!!!!!skip thi file" + Patientid);
					continue;
				}
				
				int chkSliceNum = chkslice(sROIpath);
				
				String sDCMpath = "D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\123_copy_DCM_A\\"+sPatientid+"\\"+sdata+"_DCM";
				
				String sfindSlice = findSlicePath(sDCMpath,chkSliceNum+1);
				
				Dcm4cheV3io dcmSelicV = new Dcm4cheV3io(sfindSlice);
				dcmSelicV.calHUPixelValue();
				double[] fHUValue = dcmSelicV.getHUPixelValuedouble();
				
				int[] maskmap = gatMAsk(sROIpath, fHUValue, row, colum);
				
				

				
				
				makeNewFileType(sfindSlice, dcmSelicV, maskmap, chkSliceNum+1);
			
				System.out.println("roi path : " + sROIpath+ " SliceNum : "+chkSliceNum);
				
		
				
			}
		}
		
	}
	
	public static void erwewr65464() 
	{
		
		File[] rootlist = new File("D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\123_copy_DCM_A").listFiles();
		
		
		for(File fID : rootlist)
		{
			for(File fRaw : fID.listFiles())
			{
				if(fRaw.isFile())
					try {
						makePNG(fRaw.getAbsolutePath(), "");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//SYBFileIO.dirCopy(fRaw.getAbsolutePath(), fRaw.getAbsolutePath().replace("02_DCM_DATA_ANOMIZATION", "123_copy_DCM_A"));
			
			}
		}
	}
	

	public static void buildDciomHeader1() throws WriteException, IOException 
	{
		
		//File[] rootlist = new File("D:\\98_data\\01_Sarcopenia\\04_서동우교수님\\123_copy_DCM_A").listFiles();
		
		String projectionName = "107CS_5"
			;		
		
		List<String> patientID = new ArrayList();
		patientID.add("patientID");
		List<String> SeriesNumber = new ArrayList();
		SeriesNumber.add("SeriesNumber");
		List<String> ManufacturerModelName =  new ArrayList();
		ManufacturerModelName.add("ManufacturerModelName");
		List<String> Manufacturer =  new ArrayList();
		Manufacturer.add("Manufacturer");
		List<String> Site =  new ArrayList();
		Site.add("Site");
		List<String> Rows =  new ArrayList();
		Rows.add("Rows");
		List<String> Columns =  new ArrayList();
		Columns.add("Columns");
		List<String> ReconstructionDiameter =  new ArrayList();
		ReconstructionDiameter.add("ReconstructionDiameter");
		List<String> ReconstructionPixelSpacing=  new ArrayList();
		ReconstructionPixelSpacing.add("ReconstructionPixelSpacing");
		List<String> SeriesDate=  new ArrayList();
		SeriesDate.add("SeriesDate");
		
		
		
		
		
		File[] fExperList = new File("D:\\AICORTEST_DOWN\\m_demo\\"+projectionName).listFiles();
		for(File fPatientFile:fExperList)
		{
			for(File fDataFile:fPatientFile.listFiles())
			{
				System.out.println(fDataFile.getAbsolutePath());
				
				if(fDataFile.listFiles().length == 0)
						continue;
				
				
				for(File series : fDataFile.listFiles())
				{

					//File tmpfile = fDataFile.listFiles()[0];


					AC_DicomReader dcmio = new AC_DicomReader(series);
					AC_DicomInfo dcminfo = new AC_DicomInfo();

					try {
						dcmio.getCustomDicomheaer(dcminfo);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					patientID.add(fPatientFile.getName());
					SeriesNumber.add(dcminfo.getString(AC_Tag.SeriesNumber));

					ManufacturerModelName.add(dcminfo.getString(AC_Tag.ManufacturersModelName));

					Manufacturer.add(dcminfo.getString(AC_Tag.Manufacturer));

					Site.add(dcminfo.getString(AC_Tag.InstitutionName));

					Rows.add(dcminfo.getString(AC_Tag.Rows));

					Columns.add(dcminfo.getString(AC_Tag.Columns));

					ReconstructionDiameter.add(dcminfo.getString(AC_Tag.ReconstructionDiameter));
					SeriesDate.add(dcminfo.getString(AC_Tag.SeriesDate));
				}

				//break;
				
		
				
				
			/*	filePath.add(fDataFile.getAbsolutePath());
				ManufacturerModelName.add(io.getDicomTag2String(Tag.ManufacturerModelName));
				Manufacturer.add(io.getDicomTag2String(Tag.Manufacturer ));
				SliceThnkness.add(io.getDicomTag2String(Tag.SliceThickness ));
				KvP.add(io.getDicomTag2String(Tag.KVP ));
				XrayYube.add(io.getDicomTag2String(Tag.XRayTubeCurrent ));
				SingleConllimation.add(io.getDicomTag2String(Tag.SingleCollimationWidth ));
				TotalCollimation.add(io.getDicomTag2String(Tag.TotalCollimationWidth ));
				tableFeedPerRotation.add(io.getDicomTag2String(Tag.TableFeedPerRotation ));*/
			}
			
		}
		
		ExcelIO excelop = new ExcelIO("D:\\AICORTEST_DOWN\\m_demo\\"+projectionName+".xls");

        excelop.addColumn(0, (String[])patientID.toArray(new String[patientID.size()])   );
        excelop.addColumn(1, (String[])SeriesNumber.toArray(new String[SeriesNumber.size()])   );
        excelop.addColumn(2, (String[])SeriesDate.toArray(new String[SeriesDate.size()])   );
        excelop.addColumn(3, (String[])ManufacturerModelName.toArray(new String[ManufacturerModelName.size()])   );
        excelop.addColumn(4, (String[])Manufacturer.toArray(new String[Manufacturer.size()])   );
        excelop.addColumn(5, (String[])Site.toArray(new String[Site.size()])   );
        excelop.addColumn(6, (String[])Rows.toArray(new String[SeriesNumber.size()])   );
        excelop.addColumn(7, (String[])Columns.toArray(new String[ManufacturerModelName.size()])   );
        excelop.addColumn(8, (String[])ReconstructionDiameter.toArray(new String[Manufacturer.size()]) );  

        
		
		excelop.writeExcelFile();
		
		
		
	
	}
	

	static List<File> m_File = new ArrayList<>();
	public static void test123444()
	{
		String inFilePath = "E:\\test";
		chkFile(new File(inFilePath));
		
		for(File tmp : m_File)
		{
			String name = tmp.getName();
			//String[] nameSplit = name.split("\\.");
			
			
			//if(nameSplit[1].contains("dcm"))
			{
				int instanceNum = Integer.parseInt(name);
				String outName =  String.format("%05d", instanceNum)+".dcm";
				SYBFileIO.replaceFileName(tmp.getAbsolutePath(), outName);

			}
		}
		
		
		
	}
	public static void chkFile(File input)
	{
		File[] FileList = input.listFiles();
		
		for(File tmp : FileList)
		{
			if(tmp.isFile())
				m_File.add(tmp);
			else if(tmp.isDirectory())
				chkFile(tmp);
				
		}
		
	}
	
	
	
	
	
	
	public static void main(String[] args)
			throws Exception
	{
		test123444();
		//buildDciomHeader1();
		
		//makaROIRaw();
		//erwewr65464();
		
		//makaROIRaw();
		//asdw();
	//	replicaeDCMName();
	//	nUMBV() ;
		//makeRawOnlyOneDateWithInstanceNum();
	////	sortOnlyOneDateWithSeriesNum();
	//	makeJPgALL();
	//	MakeRawWithExecl();
	//	
		//foler();
		
	//	anlsis();
		
		//Anomization(1);
		//chkFileNum();
	//	replicaeDCMName();
		//MakeRawWithExecl();a
	//	sibal();
	//	ttt();
	//	sortImagenci();
	//	 cccckkk();
			//foler();
		//getTxt() ;
	//	 makePNG();
	//	
	//	tmpSort();;
		
		
	////
	////	chkList();
	//	getTxt();
//		setExcel();
	//	getSeries();
//		getSeries();
		
		
		// tset11();
		//SortFolder("D:\\98_data\\US_test\\Random_(1)"
			//	,"D:\\98_data\\US_test\\Random_(1)_test");*/
	//	 test1111();
		//makeRawUS("D:\\intest_Sort");
	//	sortDCM("D:\\intest","D:\\intest_Sort");
		

		
		/*replaceROI("D:\\98_data\\01_sarcopenia\\02_진행중\\응급추가\\RAW_Sort"
		,"D:\\98_data\\01_sarcopenia\\02_진행중\\응급추가\\test2" );*/
		
	
		//Anomization(1);
		//replcaceInstanceName();
		
	/*	sortDCMOneFolder("D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\tmp\\52081048_20161128_DCM", 
				"D:\\98_data\\01_Sarcopenia\\02_이인섭교수님(stomach)\\tmp\\52081048_20161128_DCM_2"); */
		
	//	MakeRawFile();
	//	test32312312312();
	//	makePNG("D:\\98_data\\01_Sarcopenia\\01_김효상교수님(kideny)\\DCM_DATA_ANOMIZATION\\Kidney1\\20101015_RAW.txt");
	//	testeststestset();
		//replaceROI("E:\\98_data\\sortAnomization3","");
		
	//	exel("E:\\test3.xls");
		
		
		
		/*makeDicomInfoList("D:\\98_data\\01_sarcopenia\\99_받은파일(지숙샘)\\Sarcopenia_정리\\CT\\#3.이인섭선생님"
				,"D:\\98_data\\01_sarcopenia\\99_받은파일(지숙샘)\\Sarcopenia_정리\\CT\\#3.이인섭선생님\\dicominfo.xls" );*/
		
//		chkROIFile("D:" + FILE_SEP + "98_data" + FILE_SEP + "Sarcopenia_정리_RAW",0 );;
	//	make1();
	
    	
	
    //	SYBOpenCV imp2Mask = new SYBOpenCV();
		//imp2Mask.LoadImgFile("D:\\98_data\\Anomiz_Sarcopenia_Raw_final\\SarcopeniaCase1\\20061220_ROI\\Mask_40.png");
		
		

		
		//test();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
