package com.aaron.converter;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ConversionService {

    // You can use a free Gotenberg instance or a similar API
    private static final String CONVERT_URL = "https://demo.gotenberg.dev/forms/libreoffice/convert";

    public byte[] convertPptxToPdf(InputStream pptxStream, String fileName) throws Exception {
        // We create a temporary file to send to the API
        Path tempFile = Files.createTempFile("upload-", fileName);
        Files.copy(pptxStream, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        try (HttpClient client = HttpClient.newHttpClient()) {
            // This is a simplified Multi-part request logic
            // In a real live app, you'd use a library like Apache HttpComponents
            // For now, let's keep it lean and direct
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CONVERT_URL))
                    .header("Content-Type", "multipart/form-data") 
                    .POST(BodyPublishers.ofFile(tempFile))
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            return response.body();
        } finally {
            Files.deleteIfExists(tempFile); // Keep the server clean!
        }
    }
}