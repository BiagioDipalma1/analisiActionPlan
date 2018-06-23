 package gov.nysenate.openleg.converter.pdf;

import gov.nysenate.openleg.api.AbstractApiRequest.ApiRequestException;
import gov.nysenate.openleg.model.IBaseObject;
import gov.nysenate.openleg.model.Transcript;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import gov.nysenate.openleg.util.TranscriptLine;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
// Richiede commento

/**
 * PJDCC - Summary for class responsabilities.
 *
 * @author 
 * @since 
 * @version 
 */
public class PDFConverter
{
 
    

    

    
    /**
       * Comments about this field
       */
    public static final int NO_LINE_NUM_INDENT = 11;
/** Comments about this class */
    public static void write(IBaseObject object, OutputStream out) throws IOException, COSVisitorException, ApiRequestException
    {
        if (object instanceof Transcript) {
            PDFConverter.write((Transcript)object, out);
        }
        else {
            throw new ApiRequestException("Unable to convert "+object.getOtype()+"s to pdf.");
        }
    }
/** Comments about this class */
    public static void write(Transcript transcript, OutputStream out) throws IOException, COSVisitorException
    {
        PDDocument doc = new PDDocument();
        PDFont font = PDType1Font.COURIER;
        Float fontSize = 12f;

        List<TranscriptPage> pages = new TranscriptPageParser().parsePages(transcript);

        PDPage pg = new PDPage(PDPage.PAGE_SIZE_LETTER);
        PDPageContentStream contentStream = new PDPageContentStream(doc, pg);
        
        for (TranscriptPage page : pages) {
            
            
            drawBorder(contentStream);
            
            contentStream.beginText();
            contentStream.setFont(font, fontSize);

            drawPageText(page, contentStream);
            drawStenographer(transcript, page, contentStream);

            contentStream.endText();
            contentStream.close();
            doc.addPage(pg);
        }
        doc.save(out);
        doc.close();
    }
/** Comments about this class */
    private static void drawPageText(TranscriptPage page, PDPageContentStream contentStream) throws IOException {
        Float fontWidth = 7f;
        Float top = 710f;
        Float right = 575f;
        Float left = 105f;
        
        if (page.getTranscriptNumber() == null) {
            contentStream.moveTextPositionByAmount(0, top - fontWidth);
        } else {
            float offset = right - (page.getTranscriptNumber().length() + 1) * fontWidth;
            drawTranscriptNumber(page.getTranscriptNumber(), offset, contentStream);
        }

        for (TranscriptLine line : page.getLines()) {
            int indent;
            String text;
            if (line.hasLineNumber()) {
                indent = lineNumberLength(line) + 1;
                text = line.fullText().trim();
            }
            else {
                indent = NO_LINE_NUM_INDENT;
                text = line.fullText();
            }

            float offset = left - indent * fontWidth;
            drawLine(text, offset, contentStream);
        }
    }
/** Comments about this class */
    private static int lineNumberLength(TranscriptLine line) {
        return line.fullText().trim().split("\\s")[0].length();
    }
/** Comments about this class */
    private static void drawLine(String line, float offset, PDPageContentStream contentStream) throws IOException {
        Float fontSize = 12f;
        contentStream.moveTextPositionByAmount(offset, -fontSize);
        contentStream.drawString(line);
        contentStream.moveTextPositionByAmount(-offset, -fontSize);
    }
/** Comments about this class */
    private static void drawTranscriptNumber(String line, float offset, PDPageContentStream contentStream) throws IOException {
        Float fontSize = 12f;
        Float fontWidth = 7f;
        Float top = 710f;
        contentStream.moveTextPositionByAmount(offset, top + fontWidth);
        contentStream.drawString(line);
        contentStream.moveTextPositionByAmount(-offset, -fontSize * 2);
    }
/** Comments about this class */
    private static void drawStenographer(Transcript transcript, TranscriptPage page, PDPageContentStream contentStream) throws IOException {
        
        Float right = 575f;
        Float left = 105f;
        Float fontSize = 12f;
        Float fontWidth = 7f;
        long KIRKLAND_START_TIME = 1305086400000L;
        long CANDYCO_START_TIME = 1104555600000L;
        long CANDYCO_2003_END = 1072932900000L;
        long CANDYCO_1999_START = 915166800000L;
        long WILLIMAN_START = 725864400000L;
        
        long WILLIMAN_END = 915166500000L;
        
        String stenographer;
        if (transcript.getTimeStamp().getTime() >= KIRKLAND_START_TIME) {
            stenographer = "Kirkland Reporting Service";
        }
        else if (transcript.getTimeStamp().getTime() >= CANDYCO_START_TIME) {
            stenographer = "Candyco Transcription Service, Inc.";
        }
        else if (transcript.getTimeStamp().getTime() >= CANDYCO_1999_START && transcript.getTimeStamp().getTime() <= CANDYCO_2003_END) {
            stenographer = "Candyco Transcription Service, Inc.";
        }
        else if (transcript.getTimeStamp().getTime() >= WILLIMAN_START && transcript.getTimeStamp().getTime() <= WILLIMAN_END) {
            stenographer = "Pauline Williman, Certified Shorthand Reporter";
        }
        else {
            stenographer = "";
        }

        // 27 because page# + 25 lines + 1 line stenographer offset
        float offset = (page.getLineCount()-27)*2*fontSize;
        contentStream.moveTextPositionByAmount(left+ (right-left-stenographer.length()*fontWidth)/2, offset);
        contentStream.drawString(stenographer);
    }
/** Comments about this class */
    private static void drawBorder(PDPageContentStream contentStream) throws IOException {
        Float bot = 90f;
        Float right = 575f;
        Float top = 710f;
        Float left = 105f;
        contentStream.drawLine(left, top, left, bot);
        contentStream.drawLine(left, top, right, top);
        contentStream.drawLine(left, bot, right, bot);
        contentStream.drawLine(right, top, right, bot);
    }
}
