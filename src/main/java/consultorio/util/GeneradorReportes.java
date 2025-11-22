package consultorio.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import consultorio.model.CitaCalendario;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Stream;

public class GeneradorReportes {

    public static void generarExcel(List<CitaCalendario> citas, String rutaDestino) throws Exception {
        if (!rutaDestino.endsWith(".xlsx")) rutaDestino += ".xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reporte de Citas");

            Row headerRow = sheet.createRow(0);
            String[] columnas = {"ID", "Fecha/Hora", "Paciente", "Doctor/Usuario", "Tipo Consulta", "Estado"};

            CellStyle headerStyle = workbook.createCellStyle();

            org.apache.poi.ss.usermodel.Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            for (int i = 0; i < columnas.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (CitaCalendario cita : citas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(cita.getIdCitas());
                row.createCell(1).setCellValue(cita.getFechaHora().toString().replace("T", " "));
                row.createCell(2).setCellValue(cita.getPacienteNombre());
                row.createCell(3).setCellValue(cita.getUsuarioNombre());
                row.createCell(4).setCellValue(cita.getTipoConsulta());
                row.createCell(5).setCellValue(cita.isActivo() ? "Activa" : "Cancelada");
            }

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fileOut = new FileOutputStream(rutaDestino)) {
                workbook.write(fileOut);
            }
        }
    }

    public static void generarPDF(List<CitaCalendario> citas, String rutaDestino) throws Exception {
        if (!rutaDestino.endsWith(".pdf")) rutaDestino += ".pdf";

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(rutaDestino));

        document.open();

        com.itextpdf.text.Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);

        Paragraph titulo = new Paragraph("Reporte de Citas - Consultorio", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        PdfPTable table = new PdfPTable(5); // 5 columnas
        table.setWidthPercentage(100);

        Stream.of("Fecha", "Paciente", "Doctor", "Consulta", "Estado")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });

        for (CitaCalendario c : citas) {
            table.addCell(c.getFechaHora().toLocalDate().toString());
            table.addCell(c.getPacienteNombre());
            table.addCell(c.getUsuarioNombre());
            table.addCell(c.getTipoConsulta());
            table.addCell(c.isActivo() ? "Activa" : "Cancelada");
        }

        document.add(table);
        document.close();
    }
}