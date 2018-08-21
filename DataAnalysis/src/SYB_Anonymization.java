import org.dcm4che3.data.Tag;


public class SYB_Anonymization {
	
	
	public static final String[] INSTITUTION_NAME_LIST = 
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
	
	
	
	public static final int[] ANONYMIZATION_LIST =
		{
			    Tag.PatientName,
			    Tag.PatientID,
			    //Tag.PatientBirthDate,
			    Tag.OtherPatientIDs,
			    Tag.OtherPatientNames,
			    Tag.InstitutionName,
			    Tag.InstitutionAddress,
			    Tag.AccessionNumber,
			    Tag.StudyID		};
	
	public void AnonymizationDCMFile(String filename, String outputFilePath)
	{
		try {
			Dcm4cheV3io io = new Dcm4cheV3io(filename);
			for(int i=0; i<ANONYMIZATION_LIST.length;i++)
				io.setDicomTag2String(ANONYMIZATION_LIST[i], "Anonymization");
			io.setDicomTag2String(Tag.PatientName,"SacopeniaCase1");
			io.setDicomTag2String(Tag.InstitutionName,"SiteNum2");
			io.wirteDicomFile(outputFilePath);
			

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void AnonymizationDCMFile(String filename, String outputFilePath, String patianID)
	{
		try {
			Dcm4cheV3io io = new Dcm4cheV3io(filename);
			
			String sInstitutionName = io.getDicomTag2String(Tag.InstitutionName);
			
			for(int i=0; i<ANONYMIZATION_LIST.length;i++)
				io.setDicomTag2String(ANONYMIZATION_LIST[i], "Anonymization");
			
			io.setDicomTag2String(Tag.PatientName,patianID);
			io.setDicomTag2String(Tag.PatientID,patianID);
			
			int idx =0;
			for(String tmp : INSTITUTION_NAME_LIST)
			{
				idx++;
				if(sInstitutionName.toUpperCase().contains(tmp)) 
				{
					io.setDicomTag2String(Tag.InstitutionName,"SiteNum"+(idx));
					break;
				}
				else
					io.setDicomTag2String(Tag.InstitutionName,"N/A");
			}
			
			String sData = io.getDicomTag2String(Tag.PatientBirthDate);
			String sAnomi = sData.substring(0, 4)+"0101";
			
			io.setDicomTag2String(Tag.PatientBirthDate,sAnomi);
			
			
			
			
	
			
			
			io.wirteDicomFile(outputFilePath);
			

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



}
