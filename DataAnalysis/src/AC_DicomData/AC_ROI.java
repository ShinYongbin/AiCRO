package AC_DicomData;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;


import SYB_LIB.SYBTOOLS;

public class AC_ROI extends JPanel implements Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5171260293848865255L;
	private int m_pStartX = 0;
	private int m_pStartY = 0;
	private int m_iWidth = 0;
	private int m_iHeight = 0;
	private int m_Mode = 1;
	private int m_iID = -1;
	private float m_stroke = 2;

	private int m_pDrawX = 0;
	private int m_pDrawY = 0;
	private int m_pDrawW = 0;
	private int m_pDrawH = 0;

	private boolean m_flagOverlay = false;

	private double m_dArea = 0.0;
	private double m_dMax = 0.0;
	private double m_dMin = 0.0;
	private double m_dMean = 0.0;
	private double m_dStandardDeviation = 0.0;
	private double m_dDistance = 0.0;
	private int m_nTotalPixel = 0;

	private boolean m_flagPixelSpacing = true;

	private boolean m_bSelection = false;
	private boolean m_bOnMouse = false;

	private final Color m_cDrawLine = new Color(255, 0, 0);
	private final Color m_cSelectLine = new Color(0, 255, 0); 
	private final Color m_cOnMouse = new Color(255, 255, 0);

	private final Color m_cSaveROIColor = new Color(0, 255, 0);

	private Color m_cNowLine = m_cDrawLine;

	private final int SELECTION_RANGE = 20;

	public static final int RECTANGLE = 0;
	public static final int OVAL = 1;
	public static final int DISTANCE = 2;

	public static final int OnMouse = 0;
	public static final int NonMouse = 0;
	public static final int SectionRoi = 2;
	public static final int NonSectionRoi = 3;

	public AC_ROI() {
		/*
		 * this.setSize(100, 100); this.setLocation(100,100); /* m_pDrawW = 100;
		 * m_pDrawH = 100;
		 */

		init();

	}

	public AC_ROI(int pSx, int pSy, int pEx, int pEy, int Mode) {
		init();
		setROI(pSx, pSy, pEx, pEy, Mode);
	}

	public void init() {

		this.setOpaque(false);

		this.setBackground(new Color(255, 0, 0, 0));
		this.setSize(100, 100);
		this.setLocation(100, 100);

		this.setDoubleBuffered(false);

	}
	
	public int flipped_V(int iWitdh)
	{
		int iTmpSx = iWitdh - m_pStartX -m_iWidth;
		m_pStartX = iTmpSx;
		
		return iTmpSx;
	}

	public void setROI(int pSx, int pSy, int pEx, int pEy, int Mode) {
		m_Mode = Mode;
		m_iID = 0;
		calPoint(pSx, pSy, pEx, pEy);
	}

	public int[] getPos() {
		int[] iarrOutput = { m_pStartX, m_pStartY, m_pStartX + m_iWidth, m_pStartY + m_iHeight };

		return iarrOutput;
	}

	public int getMode() {
		return m_Mode;
	}

	public void setOveray(boolean flag) {
		m_flagOverlay = flag;
	}

	public boolean isOveray() {
		return m_flagOverlay;
	}

	public AC_ROI copy() throws CloneNotSupportedException {
		return (AC_ROI) this.clone();
	}

	public void measurement(double[] dSignal, int iImageH, double dDimX, double dDimY) {
		if (dDimX == -1 || dDimY == -1) {
			m_flagPixelSpacing = false;
			dDimX = dDimY = 1;

		}

		if (m_Mode == OVAL)
			setROIOval(dSignal, iImageH);
		else if (m_Mode == RECTANGLE)
			setROIRectangle(dSignal, iImageH);

		setDimMeasure(dDimX, dDimY);
	}

	public void setDimMeasure(double dDimX, double dDimY) {

		double pSx = m_pStartX * dDimX;
		double pSy = m_pStartY * dDimY;

		double pEx = pSx + (m_iWidth * dDimX);
		double pEy = pSy + (m_iHeight * dDimY);

		double dDistance = Math.sqrt(Math.pow(pEx - pSx, 2) + Math.pow(pEy - pSy, 2));

		m_dDistance = dDistance;

		m_dArea = dDimX * dDimY * (double) m_nTotalPixel;

	}

	public void calPoint(int pSx, int pSy, int pEx, int pEy) {

		int chkWidth = pEx - pSx;
		int chkHeight = pEy - pSy;

		if ((chkWidth > 0 && chkHeight > 0) || m_Mode == DISTANCE) {
			m_pStartX = pSx;
			m_pStartY = pSy;
			m_iWidth = chkWidth;
			m_iHeight = chkHeight;
		}

		else if (chkWidth > 0 && chkHeight < 0) {
			m_pStartX = pSx;
			m_pStartY = pEy;
			m_iWidth = chkWidth;
			m_iHeight = Math.abs(chkHeight);

		} else if (chkWidth < 0 && chkHeight < 0) {
			m_pStartX = pEx;
			m_pStartY = pEy;
			m_iWidth = Math.abs(chkWidth);
			m_iHeight = Math.abs(chkHeight);

		} else if (chkWidth < 0 && chkHeight > 0) {
			m_pStartX = pEx;
			m_pStartY = pSy;
			m_iWidth = Math.abs(chkWidth);
			m_iHeight = chkHeight;

		}

	}

	private void setROIRectangle(double[] dSignal, int iImageH) {

		int pSx = m_pStartX;
		int pSy = m_pStartY;

		int iWidth = m_iWidth;
		int iHeight = m_iHeight;

		double dMin = 2000.0;
		double dMax = 0.0;
		double dMean = 0.0;
		double dSum = 0.0;
		int iTotalPixel = 0;

		for (int j = pSy; j < pSy + iHeight; j++) {

			for (int i = pSx; i < pSx + iWidth; i++) {
				int idx = j * iImageH + i;
				double dTmpValue = dSignal[idx];

				if (dTmpValue > dMax)
					dMax = dTmpValue;
				if (dTmpValue < dMin)
					dMin = dTmpValue;
				dSum += dTmpValue;
				iTotalPixel++;
			}
		}

		dMean = dSum / iTotalPixel;

		m_dMax = dMax;
		m_dMin = dMin;
		m_dMean = dMean;
		m_nTotalPixel = iTotalPixel;
	}

	private void setROIOval(double[] dSignal, int iImageH) {
		int pSx = m_pStartX;
		int pSy = m_pStartY;

		int iWidth = m_iWidth;
		int iHeight = m_iHeight;

		double dMin = 2000.0;
		double dMax = 0.0;
		double dMean = 0.0;
		double dSum = 0.0;
		int iTotalPixel = 0;

		final int iRoiCenterX = pSx + (iWidth / 2);
		final int iRoiCenterY = pSy + (iHeight / 2);

		for (int j = pSy; j < pSy + iHeight; j++) {

			for (int i = pSx; i < pSx + iWidth; i++) {

				int idx = j * iImageH + i;

				if (idx < 0 || idx > dSignal.length)
					continue;

				double dTmpValue = dSignal[idx];

				double dchkInRoi = (Math.pow(i - iRoiCenterX, 2) / Math.pow(iWidth / 2, 2))
						+ (Math.pow(j - iRoiCenterY, 2) / Math.pow(iHeight / 2, 2));
				if (dchkInRoi <= 1.00) {
					if (dTmpValue > dMax)
						dMax = dTmpValue;
					else if (dTmpValue < dMin)
						dMin = dTmpValue;
					dSum += dTmpValue;
					iTotalPixel++;
				}
			}
		}

		dMean = dSum / iTotalPixel;

		m_dMax = dMax;
		m_dMin = dMin;
		m_dMean = dMean;
		m_nTotalPixel = iTotalPixel;

	}

	public void draw(Graphics g) {
		
		Graphics2D g2= (Graphics2D)g;
		
		
		Color oriColor = g2.getColor();
		
		g2.setStroke(new BasicStroke(m_stroke));

		g2.setColor(new Color(180, 239, 20));
		

		if (m_iID == -1)
			return;
		if (m_Mode == RECTANGLE)
			g2.drawRect(m_pStartX, m_pStartY, m_iWidth, m_iHeight);
		else if (m_Mode == OVAL)
			g2.drawOval(m_pStartX, m_pStartY, m_iWidth, m_iHeight);
		else if (m_Mode == DISTANCE)
			g2.drawLine(m_pStartX, m_pStartY, m_pStartX + m_iWidth, m_pStartY + m_iHeight);
		g2.setColor(oriColor);
		/*Color oriColor = g.getColor();

		g.setColor(new Color(180, 239, 20));
		

		if (m_iID == -1)
			return;
		if (m_Mode == RECTANGLE)
			g.drawRect(m_pStartX, m_pStartY, m_iWidth, m_iHeight);
		else if (m_Mode == OVAL)
			g.drawOval(m_pStartX, m_pStartY, m_iWidth, m_iHeight);
		else if (m_Mode == DISTANCE)
			g.drawLine(m_pStartX, m_pStartY, m_pStartX + m_iWidth, m_pStartY + m_iHeight);
		g.setColor(oriColor);*/
	}

	public void drawSaveROI(Graphics g, int iMargin) {
		
		Graphics2D g2= (Graphics2D)g;
		
		Color oriColor = g2.getColor();
		g2.setStroke(new BasicStroke(m_stroke));

		g2.setColor(new Color(255, 0, 0));

		if (m_iID == -1)
			return;
		if (m_Mode == DISTANCE) {
			int iMeanX = ((m_pStartX + iMargin) * 2 + m_iWidth) / 2;
			int iMeanY = (m_pStartY * 2 + m_iHeight) / 2;
			g2.drawString(Integer.toString(m_iID), iMeanX, iMeanY);

		} else
			g2.drawString(Integer.toString(m_iID), m_pStartX + iMargin, m_pStartY);

		g2.setColor(m_cSaveROIColor);

		if (m_Mode == RECTANGLE)
			g2.drawRect(m_pStartX + iMargin, m_pStartY, m_iWidth, m_iHeight);
		else if (m_Mode == OVAL)
			g2.drawOval(m_pStartX + iMargin, m_pStartY, m_iWidth, m_iHeight);
		else if (m_Mode == DISTANCE)
			g2.drawLine(m_pStartX + iMargin, m_pStartY, m_pStartX + m_iWidth + iMargin, m_pStartY + m_iHeight);
		g2.setColor(oriColor);
	}

	public void update(int iMoveFactorX, int iMoveFactorY, double dSpacingFactorW, double dSpacingFactorH) {

		// Color oriColor = g.getColor();

		// g.setColor(new Color(255,174,201));

		// calPoint();

		if (m_Mode == DISTANCE) {

			double dSx = (double) m_pStartX * dSpacingFactorW;
			double dSy = (double) m_pStartY * dSpacingFactorH;

			int pSx = (int) dSx + iMoveFactorX;
			int pSy = (int) dSy + iMoveFactorY;

			int iWidth = (int) ((double) m_iWidth * dSpacingFactorW);
			int iHeight = (int) ((double) m_iHeight * dSpacingFactorH);

			if (m_iWidth > 0 && m_iHeight > 0) {

				this.setLocation(pSx, pSy);
				this.setSize(iWidth + 10, iHeight + 10);

				m_pDrawX = 0;
				m_pDrawY = 0;
				m_pDrawW = iWidth;
				m_pDrawH = iHeight;
			}

			else if (m_iWidth > 0 && m_iHeight < 0) {

				this.setLocation(pSx, pSy + iHeight);
				this.setSize(iWidth + 10, Math.abs(iHeight) + 10);

				m_pDrawX = 0;
				m_pDrawY = Math.abs(iHeight);
				m_pDrawW = iWidth;
				m_pDrawH = 0;
			} else if (m_iWidth < 0 && m_iHeight < 0) {
				this.setLocation(pSx + iWidth, pSy + iHeight);
				this.setSize(Math.abs(iWidth) + 10, Math.abs(iHeight) + 10);

				m_pDrawX = 0;
				m_pDrawY = 0;
				m_pDrawW = Math.abs(iWidth);
				;
				m_pDrawH = Math.abs(iHeight);

			} else if (m_iWidth < 0 && m_iHeight > 0) {
				this.setLocation(pSx + iWidth, pSy);
				this.setSize(Math.abs(iWidth) + 10, Math.abs(iHeight) + 10);

				m_pDrawX = Math.abs(iWidth);
				m_pDrawY = 0;
				m_pDrawW = 0;
				m_pDrawH = (iHeight);

			}

			/*
			 * System.out.println("ori x:"+ m_pStartX + "y:"+ m_pStartY + "W:"+ m_iWidth +
			 * "H:"+m_iHeight);
			 * 
			 * System.out.println("x:"+ pSx + "y:"+ pSy + "W:"+ iWidth + "H:"+iHeight);
			 * 
			 */

			// System.out.println("sdf");
			// revalidate();
			// repaint();

		} else {

			double dSx = (double) m_pStartX * dSpacingFactorW;
			double dSy = (double) m_pStartY * dSpacingFactorH;

			int pSx = (int) dSx + iMoveFactorX;
			int pSy = (int) dSy + iMoveFactorY;

			int iWidth = (int) ((double) m_iWidth * dSpacingFactorW);
			int iHeight = (int) ((double) m_iHeight * dSpacingFactorH);

			this.setLocation(pSx, pSy);
			this.setSize(iWidth + 10, iHeight + 10);

			m_pDrawW = iWidth;
			m_pDrawH = iHeight;

			/*
			 * System.out.println("ori x:"+ m_pStartX + "y:"+ m_pStartY + "W:"+ m_iWidth +
			 * "H:"+m_iHeight);
			 * 
			 * System.out.println("x:"+ pSx + "y:"+ pSy + "W:"+ iWidth + "H:"+iHeight);
			 * 
			 * /* if(m_Mode==DISTANCE) { m_pDrawW = iWidth +pSx; m_pDrawH = iHeight+pSy; }
			 */

			// System.out.println("//");
			// revalidate();
			// repaint();
		}
		// drawPanel(this.getGraphics());
		/*
		 * this.revalidate(); this.repaint();
		 */

	}

	public boolean chkOnMouse(int pMx, int pMy) {
		boolean boutput = false;

		if (m_iID == -1)
			return false;
		if (m_Mode == RECTANGLE) {
			boolean chkX = false;
			boolean chkY = false;

			if (pMx > m_pStartX - SELECTION_RANGE && pMx < m_pStartX + SELECTION_RANGE)
				chkX = true;
			else if (pMx > (m_pStartX + m_iWidth) - SELECTION_RANGE && pMx < (m_pStartX + m_iWidth) + SELECTION_RANGE)
				chkX = true;
			if (pMy > m_pStartY - SELECTION_RANGE && pMy < m_pStartY + SELECTION_RANGE)
				chkY = true;
			else if (pMy > (m_pStartY + m_iHeight) - SELECTION_RANGE && pMy < (m_pStartY + m_iHeight) + SELECTION_RANGE)
				chkY = true;

			if (chkX && pMy > m_pStartY - SELECTION_RANGE && pMy < (m_pStartY + m_iHeight) + SELECTION_RANGE)
				boutput = true;
			else if (chkY && pMx > m_pStartX - SELECTION_RANGE && pMx < (m_pStartX + m_iWidth) + SELECTION_RANGE)
				boutput = true;
		} else if (m_Mode == OVAL) {
			
			/*if(m_iWidth<15||m_iHeight<15)
			{
				System.out.println("m_iWidth" + m_iWidth);
				System.out.println("m_iHeight" + m_iHeight);
				
				if( m_pStartX<=pMx && m_iWidth>=pMx  &&
						m_pStartY<=pMy && m_iHeight>=pMy )
				{
					System.out.println("in cerclae");
					return true;
				}
			}*/

			final int iRoiCenterX = m_pStartX + (m_iWidth / 2);
			final int iRoiCenterY = m_pStartY + (m_iHeight / 2);

			double dchkInRoi = (Math.pow(pMx - iRoiCenterX, 2) / Math.pow(m_iWidth / 2, 2))
					+ (Math.pow(pMy - iRoiCenterY, 2) / Math.pow(m_iHeight / 2, 2));

			double dSelcetionRange = (double) SELECTION_RANGE * 0.05;

			if (dchkInRoi < 1.0 + dSelcetionRange && dchkInRoi > 1.0 - dSelcetionRange)
				boutput = true;
		}

		else if (m_Mode == DISTANCE) {
			double dSelcetionRange = (double) SELECTION_RANGE;
			
			

			double dchkInRoi = SYBTOOLS.calcDistance(m_pStartX, m_pStartY, m_iWidth + m_pStartX, m_iHeight + m_pStartY,
					pMx, pMy);

			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!! :" + dchkInRoi);

			if (dchkInRoi < 1.0 + dSelcetionRange && dchkInRoi > -1.0 - dSelcetionRange
					&& ((pMx > (m_pStartX) - SELECTION_RANGE && pMx < (m_pStartX + m_iWidth) + SELECTION_RANGE
							&& pMy > (m_pStartY) - SELECTION_RANGE && pMy < (m_pStartY + m_iHeight) + SELECTION_RANGE)
							|| (pMx < (m_pStartX) + SELECTION_RANGE && pMx > (m_pStartX + m_iWidth) - SELECTION_RANGE
									&& pMy < (m_pStartY) - SELECTION_RANGE
									&& pMy > (m_pStartY + m_iHeight) - SELECTION_RANGE)
							|| (pMx > (m_pStartX) - SELECTION_RANGE && pMx < (m_pStartX + m_iWidth) + SELECTION_RANGE
									&& pMy < (m_pStartY) - SELECTION_RANGE
									&& pMy > (m_pStartY + m_iHeight) - SELECTION_RANGE)
							|| (pMx < (m_pStartX) + SELECTION_RANGE && pMx > (m_pStartX + m_iWidth) - SELECTION_RANGE
									&& pMy > (m_pStartY) - SELECTION_RANGE
									&& pMy < (m_pStartY + m_iHeight) + SELECTION_RANGE))

			)
				boutput = true;
		}

		return boutput;
	}

	public void drawPanel(Graphics g) {
		System.out.println("AC_ROI : DrawPanel");
		
		Graphics2D g2= (Graphics2D)g;
		g2.setStroke(new BasicStroke(m_stroke));

		if (m_iID == -1)
			return;

		Color oriColor = g2.getColor();

		Font currentFont = g2.getFont();
		float fFontSize = (float) (currentFont.getSize() + 5);
		Font newFont = currentFont.deriveFont(fFontSize);
		g2.setFont(newFont);

		g2.setColor(Color.RED);

		String sNumber = Integer.toString(m_iID);

		if (!isOveray()) {
			if (m_Mode == DISTANCE) {

				if (0 != m_pDrawW && 0 != m_pDrawH)
					g2.drawString(sNumber, m_pDrawW / 2, m_pDrawH / 2);
				else if (0 != m_pDrawW && 0 != m_pDrawY)
					g2.drawString(sNumber, m_pDrawW / 2, m_pDrawY / 2);
				else if (0 != m_pDrawH && 0 != m_pDrawX)
					g2.drawString(sNumber, m_pDrawX / 2, m_pDrawH / 2);

			} else
				g2.drawString(sNumber, 5, 15);

			m_cNowLine = m_cDrawLine;

			if (m_bSelection)
				m_cNowLine = m_cSelectLine;
			if (m_bOnMouse)
				m_cNowLine = m_cOnMouse;

			g2.setColor(m_cNowLine);
		} else if (isOveray()) {
			String ref = "ref";
			if (m_Mode == DISTANCE) {

				if (0 != m_pDrawW && 0 != m_pDrawH)
					g2.drawString(ref, m_pDrawW / 2, m_pDrawH / 2);
				else if (0 != m_pDrawW && 0 != m_pDrawY)
					g2.drawString(ref, m_pDrawW / 2, m_pDrawY / 2);
				else if (0 != m_pDrawH && 0 != m_pDrawX)
					g2.drawString(ref, m_pDrawX / 2, m_pDrawH / 2);

			} else
				g2.drawString(ref, 5, 15);

			g2.setColor(Color.BLUE);
		}

		if (m_Mode == RECTANGLE)
			g2.drawRect(0, 0, m_pDrawW, m_pDrawH);
		else if (m_Mode == OVAL)
			g2.drawOval(0, 0, m_pDrawW, m_pDrawH);
		else if (m_Mode == DISTANCE)
			g2.drawLine(m_pDrawX, m_pDrawY, m_pDrawW, m_pDrawH);

		g2.setColor(oriColor);
	}

	public int getID() {
		return m_iID;
	}

	public void setID(int input) {
		m_iID = input;
	}

	public boolean isOnMouse() {
		return m_bOnMouse;
	}

	public boolean isSelected() {
		return m_bSelection;
	}

	public void setOnMouse(boolean state) {
		m_bOnMouse = state;
	}

	public void setSelected(boolean state) {
		m_bSelection = state;
	}

	public void setColor(Color input) {
		m_cNowLine = input;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		System.out.println("AC_ROI : repatint");

		drawPanel(g);
	}

	public void moveSP(int sPx, int sPy) {
		m_pStartX -= sPx;
		m_pStartY -= sPy;
	}

	public String getMeasurString() {
		String output = null;
		if (m_Mode == RECTANGLE || m_Mode == OVAL) {

			String sID = Integer.toString(m_iID) + " ";
			String sMean = "Mean : " + String.format("%.1f", m_dMean);
			String sArea = "Area : " + String.format("%.1f", m_dArea * 0.01) + "§²";
			if (!m_flagPixelSpacing)
				sArea = "Area : " + String.format("%.1f", m_dArea) + " Px";
			output = String.format("%-2s%-15s%15s", sID, sMean, sArea);
		}

		if (m_Mode == DISTANCE) {
			String sID = Integer.toString(m_iID) + " ";
			String sDistance = "Distance : " + String.format("%.1f", m_dDistance) + " mm";
			if (!m_flagPixelSpacing)
				sDistance = "Distance : " + String.format("%.1f", m_dDistance) + " px";
			// String sArea = "Area : "+String.format("%.1f",m_dArea*0.01)+"§²";
			output = String.format("%-2s%-15s", sID, sDistance);

		}

		return output;
	}

	public String getDistance() {
		return String.format("%.1f", m_dDistance);
	}

	public String getMeans() {
		return String.format("%.3f", m_dMean);
	}
	public void setMeasurementValue( double[] input)
	{
		m_dMax = input[0];
		m_dMin = input[1];
		m_dMean = input[2];
		m_dArea = input[3];
        m_dDistance = input[4];
	}
	
	public double[] getMeasurementValue( )
	{
		double[] input = new double[5];
		input[0] = m_dMax   ;
		input[1] = m_dMin   ;
		 input[2] = m_dMean  ;
		 input[3] = m_dArea ;
         input[4] =  m_dDistance ;
         
         return input;
	}

}
