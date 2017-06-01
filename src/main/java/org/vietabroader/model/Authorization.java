package org.vietabroader.model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains static method(s) for authorizing Google API.
 */
class Authorization {

    private static final Logger logger = LoggerFactory.getLogger(Authorization.class);

    static final String APPLICATION_NAME = "vaqr";

    /// Authorization stuff
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/vietabroader.org-vaqr");
    static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    static HttpTransport HTTP_TRANSPORT;
    private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);

    private static Credential cachedCredential; // Cache credential so that we don't have
                                                // to reload from disk.

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     */
    static Credential authorize() throws IOException, GeneralSecurityException {

        if (cachedCredential != null) {
            return cachedCredential;
        }

        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

        // Load client secrets.
        InputStream in =
                Authorization.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(dataStoreFactory)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");

        logger.info("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());

        cachedCredential = credential;
        return credential;
    }

    /**
     * Removes the stored credential on disk.
     */
    public static void clearStoredCredential() {
        cachedCredential = null;
        try {
            FileUtils.deleteDirectory(DATA_STORE_DIR);
        } catch (IOException e) {
            logger.warn("No stored credential found");
        }
    }
}
