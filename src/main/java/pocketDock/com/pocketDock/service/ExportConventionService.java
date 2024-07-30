package pocketDock.com.pocketDock.service;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import org.apache.poi.ss.usermodel.Row;

import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.InputStreamResource;
import java.io.ByteArrayInputStream;


import com.itextpdf.text.Paragraph;

import java.io.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pocketDock.com.pocketDock.entity.Convention;

import javax.imageio.ImageIO;


@Service
public class ExportConventionService {

    public static ResponseEntity<InputStreamResource> exportConventionPDF(List<Convention> conventions, String userName, String userCin, Double userReduction, String cond_resilation, Date db, Date df) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Ajout du logo
            InputStream logoStream = ExportConventionService.class.getResourceAsStream("/images/logo.jpg");
            if (logoStream != null) {
                try {
                    byte[] logoBytes = readStream(logoStream);
                    Image logo = Image.getInstance(logoBytes);
                    logo.scaleToFit(100, 100); // Ajustez la taille du logo si nécessaire
                    document.add(logo);
                    document.add(Chunk.NEWLINE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


// En-tête
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph header = new Paragraph("Convention de partenariat avec Esprit : ", headerFont);
            header.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(header);
            document.add(Chunk.NEWLINE);

// Informations de la convention
            Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            for (Convention convention : conventions) {
                document.add(new Paragraph("L'équipe d'ESPRIT a le privilège de conclure une convention de partenariat avec M./Mme. " + userName + ", identifié(e) par le numéro de CIN " + userCin + ".", dataFont));
                document.add(new Paragraph("La présente convention décrit les termes et conditions du partenariat, avec une description succincte des engagements.", dataFont));
                document.add(new Paragraph("Un taux de réduction des consultations est établi à " + userReduction + "%, conforme aux conditions spécifiées.", dataFont));
                document.add(new Paragraph("En cas de non-respect des conditions de résiliation (" + cond_resilation + "), la convention sera résiliée.", dataFont));
                document.add(new Paragraph("Cette entente est valide à partir du " + db + " jusqu'au " + df + ".", dataFont));
                document.add(Chunk.NEWLINE);
                // Ajout d'un espace entre les conventions
            }

// Pied de page avec les informations de contact
            Font footerFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph footer = new Paragraph("For more information, contact: info@espritschool.com", footerFont);
            footer.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=convention_report.pdf");

        ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    // Méthode utilitaire pour lire un flux d'entrée et le convertir en tableau de bytes
    private static byte[] readStream(InputStream stream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = stream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public static ByteArrayInputStream exportConventionExcel(List<Convention> conventions, List<String> userNames, List<Double> userReductions) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet("Conventions");

            // Création des titres des colonnes
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("PSY Name");
            headerRow.createCell(1).setCellValue("Reduction");
            headerRow.createCell(2).setCellValue("Montant après réduction");

            // Remplissage des données des conventions
            int rowNum = 1; // Commencer à partir de la deuxième ligne après les titres
            for (int i = 0; i < conventions.size(); i++) {
                Convention convention = conventions.get(i);
                String userName = userNames.get(i);
                Double userReduction = userReductions.get(i);

                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(userName);
                row.createCell(1).setCellValue(userReduction);
                double montantApresReduction = 120 - (120 * userReduction);
                row.createCell(2).setCellValue(montantApresReduction);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}