import java.awt.print.Pageable;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;


public class ITest {

	private static final float MARGIN_LEFT = 36;
	private static final float MARGIN_TOP = 48;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new ITest().createPdf("d:\\temp\\test.pdf");
	}

	private void placeChunck(String text, float f, float g, PdfWriter writer) throws DocumentException, IOException {
        PdfContentByte cb = writer.getDirectContent();
        cb.saveState();
        BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        cb.setFontAndSize(bf, 16);
        cb.beginText();
        cb.moveText(f, g);
        cb.showText(text + "=1=");
        cb.endText();
        cb.restoreState();
    }	
	
	private void addSenderAddress(PdfWriter writer) {
		
	}
	
    public void createPdf(String filename)
	throws DocumentException, IOException {
        // step 1
        Document document = new Document();
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
        // step 3
        document.open();
        document.setMargins(MARGIN_LEFT, 72, 108, 180);
        
        PdfContentByte canvas = writer.getDirectContent();
		ColumnText ct1 = new ColumnText(canvas);
		ColumnText ct2 = new ColumnText(canvas);
        Rectangle pageRect = document.getPageSize();
		ct1.setSimpleColumn(MARGIN_LEFT, pageRect.getTop() - MARGIN_TOP, pageRect.getWidth() / 2 - 10, 500);
        Paragraph p2 = new Paragraph("Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. " + 2);

        ct1.addElement(new Paragraph("Ralph Fiergolla"));
        ct1.addElement(new Paragraph("Bertha von Suttner Str. 8"));
        ct1.addElement(new Paragraph("54317 Gusterath"));
        
        ct2.addText(p2);
        int go1 = ct1.go();
        float yLine = ct1.getYLine();
		ct2.setSimpleColumn(pageRect.getWidth() / 2 + 10, yLine, pageRect.getWidth(), 500);
        int go2 = ct2.go();
        document.close();
    }	
}
