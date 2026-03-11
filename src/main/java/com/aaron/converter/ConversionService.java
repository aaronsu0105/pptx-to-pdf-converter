package com.aaron.converter;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;
import java.io.InputStream;

@Service
public class ConversionService {

    private static final String GOTENBERG_URL = "https://demo.gotenberg.dev/forms/libreoffice/convert";

    public byte[] convertPptxToPdf(InputStream pptxStream, String fileName) throws Exception {
        // Use a try-with-resources to ensure the connection closes properly
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost(GOTENBERG_URL);
            
            // Build the request exactly how Gotenberg expects it
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("files", pptxStream, 
                org.apache.hc.core5.http.ContentType.APPLICATION_OCTET_STREAM, fileName);
            
            uploadFile.setEntity(builder.build());

            return httpClient.execute(uploadFile, response -> {
                int status = response.getCode();
                if (status >= 200 && status < 300) {
                    return EntityUtils.toByteArray(response.getEntity());
                } else {
                    // This catches if the API is down or file is too big
                    throw new RuntimeException("API Error: " + status);
                }
            });
        }
    }
}