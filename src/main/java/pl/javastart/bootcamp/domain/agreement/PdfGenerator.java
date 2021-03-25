package pl.javastart.bootcamp.domain.agreement;

import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.ITextUserAgent;

import java.io.*;

@Component
public class PdfGenerator {

    private static final String FONT_LOCATION = "/agreement/Calibri.ttf";

    public void generateAgreement(String content, File resultFile) throws IOException, DocumentException {

        FontFactory.register(FONT_LOCATION);
        ITextRenderer renderer = new ITextRenderer();

        ResourceLoaderUserAgent callback = new ResourceLoaderUserAgent(renderer.getOutputDevice());
        callback.setSharedContext(renderer.getSharedContext());
        renderer.getSharedContext().setUserAgentCallback(callback);

        renderer.setDocumentFromString(content);
        renderer.getFontResolver().addFont(FONT_LOCATION, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

        renderer.layout();
        FileOutputStream fileOutputStream = new FileOutputStream(resultFile);
        renderer.createPDF(fileOutputStream);

        fileOutputStream.close();
    }

    private static class ResourceLoaderUserAgent extends ITextUserAgent {

        public ResourceLoaderUserAgent(ITextOutputDevice outputDevice) {
            super(outputDevice);
        }

        protected InputStream resolveAndOpenStream(String uri) {

            InputStream is = super.resolveAndOpenStream(uri);
            String fileName = "";
            try {
                String[] split = uri.split("/");
                fileName = split[split.length - 1];
            } catch (Exception e) {
                return null;
            }

            if (is == null) {
                // Resource is on the classpath
                try {
                    is = ResourceLoaderUserAgent.class.getResourceAsStream("/agreement/" + fileName);
                } catch (Exception e) {
                    // empty
                }
            }
            return is;
        }
    }
}