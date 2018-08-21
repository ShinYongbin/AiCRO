import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class ExcelIO {
	
	static WritableWorkbook m_rWorkBook = null;
	static WritableSheet m_rSheet = null;
	
	public ExcelIO(String filePath, String sheetName) throws IOException
	{
		readExcelFile(filePath, sheetName);
	}
	
	public ExcelIO(String filePath) throws IOException
	{
		String sheetName = "tmp1";
		readExcelFile(filePath, sheetName);
	}
	
	public void readExcelFile(String filePath, String sheetName) throws IOException
	{
		m_rWorkBook = Workbook.createWorkbook(new File(filePath));
		m_rSheet = m_rWorkBook.createSheet(sheetName, 0);
	}
	
	public void readExcelFile(String filePath) throws IOException
	{
		String sheetName = "tmp1";
		m_rWorkBook = Workbook.createWorkbook(new File(filePath));
		m_rSheet = m_rWorkBook.createSheet(sheetName, 0);
	}
	
	
	public void addCell(int row, int column, String sValue) throws RowsExceededException, WriteException 
	{
		Label tmpLable = new Label(0, 0, sValue);
		m_rSheet.addCell(tmpLable);
	}
	
	public void addRow(int column, String[] sValue) throws RowsExceededException, WriteException 
	{
		for(int i=0; i<sValue.length;i++)
		{
			Label tmpLable = new Label(i, column, sValue[i]);
			m_rSheet.addCell(tmpLable);
		}


	}
	
	public void addColumn(int Row, String[] sValue) throws RowsExceededException, WriteException 
	{
		for(int i=0; i<sValue.length;i++)
		{
			Label tmpLable = new Label(Row, i, sValue[i]);
			m_rSheet.addCell(tmpLable);
		}

	}
	
	public void writeExcelFile() throws IOException, WriteException
	{

		m_rWorkBook.write();
        m_rWorkBook.close();

	}
	

}
