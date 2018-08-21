import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;

import org.dcm4che3.data.Tag;

import sun.security.krb5.internal.crypto.DesCbcMd5EType;



public class DataAnl
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

	public static void makeNewFileType(String sFilepath, Dcm4cheV3io dcmInput, int[] iMaskValue, int iSliceNum, int ROIDate)
			throws IOException
	{
		String[] sTmpSplitPath = sFilepath.split("\\\\");
		String sDicomHeader = "";
		for (int i = 0; i < sTmpSplitPath.length - 2; i++) {
			sDicomHeader = sDicomHeader + sTmpSplitPath[i] + FILE_SEP;
		}
		sDicomHeader = sDicomHeader + sTmpSplitPath[(sTmpSplitPath.length - 2)].replaceAll("_DCM", "_RAW") + ".txt";


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

		fw.write("#ROINUM");
		fw.newLine();
		fw.write(Integer.toString(ROIDate));
		fw.newLine();

		fw.write("#HUValue");
		fw.newLine();

		float[] fHUValue = dcmInput.getHUPixelValue();
	SYBOpenCV.makeHU16bit("", fHUValue,Integer.parseInt((dcmInput.getDicomTag2String(Tag.WindowCenter)))
				,Integer.parseInt((dcmInput.getDicomTag2String(Tag.WindowWidth)))     )   ;
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
		
		String inroot = "D:\\98_data\\01_Sarcopenia\\01_김효상교수님(kideny)\\DCM_DATA_ANOMIZATION";
		String outroot = "D:\\98_data\\01_sarcopenia\\nowProcess\\test2";
		
		
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
						
						makeNewFileType(sSeliceDCMPath, dcmSelicV, imp2Mask.m_intMask, iSliceNum, chkROINUM);
						
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


		File[] arrayOfFile1 = fExperimentCase;
		int j = fExperimentCase.length;
	//	for (int i = 0; i < j; i++)
		{
			File PatientCase =new File(inputPath);//fExperimentCase[i];
			
		

			File[] fReadList = PatientCase.listFiles();
			
			
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
				
				if(fReadFile.getName().contains("ROI"))
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
				}
			}
			System.out.println(PatientCase.getName()+"_run now");
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
	
	public static void Anomization()
			throws Exception
	{
		String inroot = "D:\\98_data\\99_TestData\\err_Anomi";
		String outroot = "D:\\98_data\\99_TestData\\err_Anomi_anomi";
		
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
		
		int SarcopeniaIDX =364;
		
		for(File ftmpDCM:(File[])fileDCMPath.toArray(new File[fileDCMPath.size()]))
		{
			String[] folderName = ftmpDCM.getName().split("_");
			
			if(sPatientID.size()==0)
			{
				sPatientID.add(folderName[0]);
				sAnomization.add("SarcopeniaCase"+SarcopeniaIDX);
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
					sAnomization.add("SarcopeniaCase"+SarcopeniaIDX);
					SarcopeniaIDX++;
				}
			}
			String sDCMinputtPath = ftmpDCM.getAbsolutePath();
			String sROIinputtPath = sDCMinputtPath.replaceAll("_DCM", "_ROI");
			
			String sDCMOuptPath = outroot+FILE_SEP+
					"SarcopeniaCase"+(SarcopeniaIDX-1)+FILE_SEP+
					folderName[1]+"_DCM";
			if(folderName.length==4)
				sDCMOuptPath+="_"+folderName[3];
			String sROIOuptPath = sDCMOuptPath.replaceAll("_DCM", "_ROI");
			
			//SYBFileIO.dirCopy(sROIinputtPath, sROIOuptPath);
			
			
			String sDCMPath = ftmpDCM.listFiles()[0].getAbsolutePath();
			Dcm4cheV3io dcmInput = new Dcm4cheV3io(sDCMPath);
			
			PatientID.add(dcmInput.getDicomTag2String(Tag.PatientID));  
			PatientName.add(dcmInput.getDicomTag2String(Tag.PatientName)); 
			AnomizationName.add("SarcopeniaCase"+(SarcopeniaIDX-1));
			AnomizationSite.add( chkInstiute(dcmInput.getDicomTag2String(Tag.InstitutionName)));  
			InstitutionName.add( (dcmInput.getDicomTag2String(Tag.InstitutionName)));  
			SeriesDate.add(dcmInput.getDicomTag2String(Tag.SeriesDate)); 
			ManufacturerModelName.add(dcmInput.getDicomTag2String(Tag.ManufacturerModelName)); 
			Modality.add(dcmInput.getDicomTag2String(Tag.Modality)); 
			PixelSpacing.add(dcmInput.getDicomTag2String(Tag.PixelSpacing)); 
			Rows.add(dcmInput.getDicomTag2String(Tag.Rows)); 
			Columns.add(dcmInput.getDicomTag2String(Tag.Columns)); 
			
			
			
			for(File fDCMfile: ftmpDCM.listFiles())
			{
				String sOutputfilename = sDCMOuptPath+FILE_SEP+fDCMfile.getName();
				SYB_Anonymization.AnonymizationDCMFile(fDCMfile.getAbsolutePath(),
						sOutputfilename, "SarcopeniaCase"+(SarcopeniaIDX-1));
			}
			
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
	
	public static void main(String[] args)
			throws Exception
	{
		
	//	 tset10();
		/*SortFolder("D:\\98_data\\01_sarcopenia\\02_진행중\\응급추가\\test"
				,"D:\\98_data\\01_sarcopenia\\02_진행중\\응급추가\\test_sort");*/
		
		/*replaceROI("D:\\98_data\\01_sarcopenia\\02_진행중\\응급추가\\RAW_Sort"
		,"D:\\98_data\\01_sarcopenia\\02_진행중\\응급추가\\test2" );*/
		
	//	chkROIFile("D:\\98_data\\test5");
	//	Anomization();
		
	//	MakeRawFile();
		
		
		
	/*	makeDicomInfoList("D:\\98_data\\01_sarcopenia\\99_받은파일(지숙샘)\\Sarcopenia_정리\\CT\\#3.이인섭선생님"
				,"D:\\98_data\\01_sarcopenia\\99_받은파일(지숙샘)\\Sarcopenia_정리\\CT\\#3.이인섭선생님\\dicominfo.xls" );*/
		
//		chkROIFile("D:" + FILE_SEP + "98_data" + FILE_SEP + "Sarcopenia_정리_RAW",0 );;
//		make1();
	
    	
	
    //	SYBOpenCV imp2Mask = new SYBOpenCV();
		//imp2Mask.LoadImgFile("D:\\98_data\\Anomiz_Sarcopenia_Raw_final\\SarcopeniaCase1\\20061220_ROI\\Mask_40.png");
		
		

		
		//test();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
