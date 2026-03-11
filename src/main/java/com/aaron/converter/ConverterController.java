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

            // FIX: Convert the file to a solid byte array right away
            byte[] fileBytes = file.getBytes();
            byte[] pdfBytes = conversionService.convertPptxToPdf(fileBytes, file.getOriginalFilename());

            // SAFETY NET: A valid PDF is always larger than 100 bytes. 
            // If it's smaller, it's a corrupted file or an error message.
            if (pdfBytes == null || pdfBytes.length < 100) {
                System.err.println("Error: The resulting PDF is empty or corrupted.");
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
            }

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