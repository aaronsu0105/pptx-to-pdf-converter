package com.aaron.converter;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;

@Service
public class ConversionService {

    private static final String GOTENBERG_URL = "https://demo.gotenberg.dev/forms/libreoffice/convert";

    // Changed InputStream to byte[] to prevent the stream from closing prematurely
    public byte[] convertPptxToPdf(byte[] pptxBytes, String fileName) throws Exception {
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost uploadFile = new HttpPost(GOTENBERG_URL);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("files", pptxBytes, 
                org.apache.hc.core5.http.ContentType.APPLICATION_OCTET_STREAM, fileName);
            
            uploadFile.setEntity(builder.build());

            return httpClient.execute(uploadFile, response -> {
                int status = response.getCode();
                byte[] resultBytes = EntityUtils.toByteArray(response.getEntity());
                
                if (status >= 200 && status < 300) {
                    return resultBytes;
                } else {
                    // This will print the exact API error to your Render Logs
                    String errorText = new String(resultBytes);
                    System.err.println("API FAILED. Status: " + status + " Message: " + errorText);
                    throw new RuntimeException("API Error " + status + ": " + errorText);
                }
            });
        }
    }
}