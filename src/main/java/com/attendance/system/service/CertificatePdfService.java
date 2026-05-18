package com.attendance.system.service;

import com.attendance.system.entity.Site;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class CertificatePdfService {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] buildWorkCompletionCertificate(Site site, String projectDescription) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 11);

        document.add(new Paragraph("WORK COMPLETION CERTIFICATE", titleFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Job code: " + site.getJobCode(), normal));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("This is to certify that Coren Techno Mech has successfully completed the following work/project:", normal));
        document.add(new Paragraph(" "));
        String desc = projectDescription != null && !projectDescription.isBlank()
            ? projectDescription
            : (site.getName() != null ? site.getName() : "");
        document.add(new Paragraph(desc, normal));
        document.add(new Paragraph(" "));
        LocalDate start = site.getSiteStartDate();
        LocalDate end = site.getSiteEndDate();
        String duration = (start != null ? DF.format(start) : "___") + " to " + (end != null ? DF.format(end) : "___");
        document.add(new Paragraph("Duration of work: " + duration, normal));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Date of completion: " + DF.format(LocalDate.now()), normal));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(
            "This certificate is awarded as a recognition of the hard work, dedication, and professionalism demonstrated throughout the project.",
            normal));

        document.close();
        return baos.toByteArray();
    }
}
