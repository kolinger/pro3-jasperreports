package me.kolinger.pro3.jaspersreports;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Tomáš Kolinger <tomas@kolinger.name>
 */
public class Report {

    final public static int FORMAT_PDF = 0;
    final public static int FORMAT_HTML = 1;
    final public static int FORMAT_XLS = 2;

    private File file;
    private Integer format;

    public Report(File file, int format) {
        this.file = file;
        this.format = format;
    }

    public void doExport() throws JRException {
        if (format == FORMAT_PDF) {
            exportToPdf();
        } else if (format == FORMAT_HTML) {
            exportToHtml();
        } else if (format == FORMAT_XLS) {
            exportToExcel();
        } else {
            throw new IllegalArgumentException("Unsupported format");
        }
    }

    private JasperPrint fillReport() throws JRException {
        // generate simple model
        Date endDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.add(Calendar.DAY_OF_YEAR, -30);
        Date startDate = calendar.getTime();

        // title
        SimpleDateFormat dateFormat = new SimpleDateFormat("d.M.yyyy");
        SimpleDateFormat shortDateFormat = new SimpleDateFormat("d.M.");
        String title = "Náštěvnost " + shortDateFormat.format(startDate) + " - " + dateFormat.format(endDate);

        // time data
        Random random = new Random();
        List<TimeRow> timeRowsShort = new ArrayList<TimeRow>();
        List<TimeRow> timeRowsLong = new ArrayList<TimeRow>();
        while (endDate.getTime() > calendar.getTime().getTime()) {
            TimeRow row1 = new TimeRow();
            row1.setTime(dateFormat.format(calendar.getTime()));
            row1.setValue((long) (random.nextDouble() * 100L));
            TimeRow row2 = new TimeRow();
            row2.setTime(shortDateFormat.format(calendar.getTime()));
            row2.setValue(row1.getValue());
            timeRowsShort.add(row2);
            timeRowsLong.add(row1);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // pages data
        PageRow row;
        List<PageRow> pagesRows = new ArrayList<PageRow>();
        row = new PageRow();
        row.setPage("http://www.example.com/");
        row.setValue((long) (random.nextDouble() * 600L));
        pagesRows.add(row);
        row = new PageRow();
        row.setPage("http://www.example.com/products");
        row.setValue((long) (random.nextDouble() * 600L));
        pagesRows.add(row);
        row = new PageRow();
        row.setPage("http://www.example.com/about");
        row.setValue((long) (random.nextDouble() * 600L));
        pagesRows.add(row);
        row = new PageRow();
        row.setPage("http://www.example.com/clients");
        row.setValue((long) (random.nextDouble() * 600L));
        pagesRows.add(row);
        row = new PageRow();
        row.setPage("http://www.example.com/contact");
        row.setValue((long) (random.nextDouble() * 600L));
        pagesRows.add(row);

        // fill report
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("title", title);

        JRBeanCollectionDataSource pagesGraphDataSource = new JRBeanCollectionDataSource(pagesRows);
        parameters.put("pagesGraphDataSource", pagesGraphDataSource);

        JRBeanCollectionDataSource timeGraphDataSource = new JRBeanCollectionDataSource(timeRowsShort);
        parameters.put("timeGraphDataSource", timeGraphDataSource);

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(timeRowsLong);
        InputStream template = this.getClass().getClassLoader().getResourceAsStream("ExampleReport.jasper");
        return JasperFillManager.fillReport(template, parameters, dataSource);
    }

    private void exportToPdf() throws JRException {
        JasperPrint jasperPrint = fillReport();

        JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
    }

    private void exportToHtml() throws JRException {
        JasperPrint jasperPrint = fillReport();

        JasperExportManager.exportReportToHtmlFile(jasperPrint, file.getAbsolutePath());
    }

    private void exportToExcel() throws JRException {
        JasperPrint jasperPrint = fillReport();

        JRXlsExporter exporter = new JRXlsExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_FILE, file);
        exporter.exportReport();
    }

    public class TimeRow {
        private String time;
        private Long value;

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }

    public class PageRow {
        private String page;
        private Long value;

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }
}
