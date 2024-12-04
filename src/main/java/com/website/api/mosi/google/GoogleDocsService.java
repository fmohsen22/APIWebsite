package com.website.api.mosi.google;


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.docs.v1.Docs;
import com.google.api.services.docs.v1.DocsScopes;
import com.google.api.services.docs.v1.model.Document;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class GoogleDocsService {
    private static final String APPLICATION_NAME = "Envisage Cosmetic Clinic";
    private static final String DOCUMENT_ID = "1sIlV1Fk5r3mE3zkm4rAH4aHXsXOkMvr-cnqi67pAk8A"; // Replace with your Google Doc ID
    private Docs docsService;

    public GoogleDocsService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(System.getenv("GOOGLE_APPLICATION_CREDENTIALS")))
                .createScoped(List.of(DocsScopes.DOCUMENTS_READONLY));

        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        docsService = new Docs.Builder(httpTransport, jsonFactory, new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public String readDocumentText() throws IOException {
        Document document = docsService.documents().get(DOCUMENT_ID).execute();
        StringBuilder content = new StringBuilder();

        document.getBody().getContent().forEach(element -> {
            if (element.getParagraph() != null) {
                element.getParagraph().getElements().forEach(textElement -> {
                    if (textElement.getTextRun() != null) {
                        content.append(textElement.getTextRun().getContent());
                    }
                });
            }
        });

        return content.toString();
    }

    public static void main(String[] args) {
        try {
            GoogleDocsService googleDocsService = new GoogleDocsService();
            String documentText = googleDocsService.readDocumentText();
            System.out.println("Document Content:\n" + documentText);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
