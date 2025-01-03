package com.travelagency.busbooking.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import com.travelagency.busbooking.entity.Ticket;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfService {

    public ByteArrayOutputStream createStyledPdf(Ticket ticket) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        pdfDocument.setDefaultPageSize(PageSize.A5.rotate()); // Set A5 landscape for ticket layout
        document.setMargins(10, 10, 10, 10); // Small margins

        // Title Section
        Paragraph title = new Paragraph("Elvis Travel")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(ColorConstants.BLUE)
                .setMarginBottom(10);
        document.add(title);

        // Main Ticket Border with Rounded Corners
        Table outerTable = new Table(UnitValue.createPercentArray(1))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 2))
                .setMarginBottom(10)
                .setPadding(5);

        // Inner Table for Ticket Details
        Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .useAllAvailableWidth()
                .setBorder(Border.NO_BORDER)
                .setMarginBottom(5);

        // Ticket Details
        detailsTable.addCell(createDetailCell("No:", true));
        detailsTable.addCell(createDetailCell(ticket.getId().toString(), false));

        detailsTable.addCell(createDetailCell("User:", true));
        detailsTable.addCell(createDetailCell(ticket.getEmployee().getUsername().toString(), false));

        detailsTable.addCell(createDetailCell("Pasagjer:", true));
        detailsTable.addCell(createDetailCell(ticket.getPassengerName(), false));



        detailsTable.addCell(createDetailCell("Nr. Pashaporte:", true));
        detailsTable.addCell(createDetailCell(ticket.getNr_pashaporte(), false));

        detailsTable.addCell(createDetailCell("Date:", true));
        detailsTable.addCell(createDetailCell(ticket.getTrip().getDepartureDate().toString(), false));

        detailsTable.addCell(createDetailCell("Time:", true));
        detailsTable.addCell(createDetailCell(ticket.getTrip().getDepartureTime(), false));

        detailsTable.addCell(createDetailCell("From:", true));
        detailsTable.addCell(createDetailCell(ticket.getTrip().getOrigin(), false)); // Hardcoded as "From Albania"

        detailsTable.addCell(createDetailCell("Destination:", true));
        detailsTable.addCell(createDetailCell(ticket.getTrip().getDestination(), false));



        detailsTable.addCell(createDetailCell("Price:", true));
        detailsTable.addCell(createDetailCell(ticket.getPrice().toString() + " Leke", false));

        // Add details table inside the outer table
        outerTable.addCell(new Cell().add(detailsTable).setBorder(Border.NO_BORDER));
        document.add(outerTable);

        // Footer Message
        Paragraph footer = new Paragraph("Safe Travels with Elvis Travel!")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.DARK_GRAY);
        document.add(footer);

        // Close document
        document.close();
        return outputStream;
    }

    private Cell createDetailCell(String content, boolean isHeader) {
        try {
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            Cell cell = new Cell()
                    .add(new Paragraph(content)
                            .setFont(isHeader ? boldFont : font)  // Apply bold font for headers
                            .setFontSize(isHeader ? 10 : 9)
                            .setTextAlignment(TextAlignment.LEFT))
                    .setPadding(3)
                    .setBorder(Border.NO_BORDER);

            if (isHeader) {
                cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
            }
            return cell;

        } catch (IOException e) {
            e.printStackTrace();
            return new Cell().add(new Paragraph(content)); // Fallback cell in case of font load error
        }
    }
}
