package com.aaron.converter;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") 
public class ConverterController {

    private final ConversionService conversionService = new ConversionService();

    @PostMapping("/convert")
    public ResponseEntity<byte[]> handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            System.out.println("Processing: " + file.getOriginalFilename());

            byte[] pdfBytes = conversionService.convertPptxToPdf(file.getInputStream());

            // Filename Logic: Replace .pptx with .pdf
            String originalName = file.getOriginalFilename();
            String newName = (originalName != null && originalName.contains(".")) 
                             ? originalName.substring(0, originalName.lastIndexOf(".")) + ".pdf" 
                             : "converted_document.pdf";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename(newName).build());

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Conversion Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}