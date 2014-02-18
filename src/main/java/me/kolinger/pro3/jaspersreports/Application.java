package me.kolinger.pro3.jaspersreports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Application {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        Application app = new Application();
        app.run(args);
    }

    public void run(String[] args) {
        if (args.length > 0) {
            File file = new File(args[0]);

            Integer format = null;
            if (file.getPath().contains(".pdf")) {
                format = Report.FORMAT_PDF;
            } else if (file.getName().contains(".html")) {
                format = Report.FORMAT_HTML;
            } else if (file.getName().contains(".xls")) {
                format = Report.FORMAT_XLS;
            }

            if (format == null) {
                logger.error("Wrong file extension, support is only .pdf, .html and .csv");
            } else {
                try {
                    Report report = new Report(file, format);
                    report.doExport();
                } catch (Exception e) {
                    logger.error("Export failed", e);
                }
            }
        } else {
            logger.warn("You must enter file path first argument");
        }
    }
}
