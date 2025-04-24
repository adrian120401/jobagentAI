package com.findjob.job_agent.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import com.findjob.job_agent.model.dto.ResumeProfile;
import com.findjob.job_agent.model.entity.JobSearched;
import com.findjob.job_agent.service.AI.CVAnalyzeService;
import com.findjob.job_agent.service.AI.ModifyCVServiceAI;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import java.io.ByteArrayInputStream;

@Service
public class FileService {
    private final CVAnalyzeService cvAnalyzeService;
    private final ModifyCVServiceAI modifyCVService;

    public FileService(CVAnalyzeService cvAnalyzeService, ModifyCVServiceAI modifyCVService) {
        this.cvAnalyzeService = cvAnalyzeService;
        this.modifyCVService = modifyCVService;
    }

    public ResumeProfile readCV(byte[] file) {
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper textStripper = new PDFTextStripper();

            String rawText = textStripper.getText(document);
            document.close();

            String sanitizedText = sanitizeText(rawText);

            String response = cvAnalyzeService.analyzeCV(sanitizedText);

            if (response == null) {
                throw new Exception("Error analyzing cv");
            }

            return ResumeProfile.parseResumeResponse(response);

        } catch (Exception e) {
            throw new RuntimeException("Error processing PDF");
        }
    }

    public String sanitizeText(String rawText) {
        return rawText
                .replaceAll("\\r?\\n", "\\\\n")
                .replaceAll("\\s{2,}", " ")
                .replaceAll("•", "-")
                .replaceAll("–", "-")
                .trim();
    }

    public byte[] modifyCV(byte[] file, String userMessage, ResumeProfile resumeProfile, JobSearched jobSearched,
            String summary) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(file);
                XWPFDocument document = new XWPFDocument(bis)) {

            String cvText = collectText(document);

            List<Map<String, String>> replacements = modifyCVService.getCVModifications(cvText, userMessage,
                    resumeProfile, jobSearched, summary);

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        String modifiedText = text;
                        for (Map<String, String> replacement : replacements) {
                            String oldText = replacement.get("oldText");
                            String newText = replacement.get("newText");
                            if (oldText != null && newText != null) {
                                modifiedText = modifiedText.replace(oldText, newText);
                            }
                        }
                        if (!modifiedText.equals(text)) {
                            run.setText(modifiedText, 0);
                        }
                    }
                }
            }

            document.getTables().forEach(table -> {
                table.getRows().forEach(row -> {
                    row.getTableCells().forEach(cell -> {
                        String text = cell.getText();
                        if (text != null) {
                            String modifiedText = text;
                            for (Map<String, String> replacement : replacements) {
                                System.out.println("oldText: " + replacement.get("oldText"));
                                System.out.println("newText: " + replacement.get("newText"));
                                String oldText = replacement.get("oldText");
                                String newText = replacement.get("newText");
                                if (oldText != null && newText != null) {
                                    System.out.println("Modifying text: " + modifiedText);
                                    modifiedText = modifiedText.replace(oldText, newText);
                                }
                            }
                            if (!modifiedText.equals(text)) {
                                cell.setText(modifiedText);
                            }
                        }
                    });
                });
            });

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error modifying DOCX", e);
        }
    }

    private String collectText(XWPFDocument document) {
        StringBuilder sb = new StringBuilder();
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            sb.append(paragraph.getText()).append("\n");
        }
        return sb.toString();
    }
}
