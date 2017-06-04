package org.vietabroader;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.Oauth2Scopes;
import com.google.api.services.oauth2.model.Userinfoplus;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * This class contains static methods for authorizing Google API. All methods in this class
 * are blocking type, thus should be run in a separate thread.
 */
public class GoogleAPIUtils {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAPIUtils.class);

    public static final String APPLICATION_NAME = "VAQR";

    // Authorization stuff
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/vietabroader.org-VAQR");
    public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    public static HttpTransport HTTP_TRANSPORT;

    private static final List<String> SCOPES = Arrays.asList(
            SheetsScopes.SPREADSHEETS,
            DriveScopes.DRIVE_FILE,
            Oauth2Scopes.USERINFO_EMAIL);

    // Cache credentials and services so that we don't need to reinitialize every time
    private static Credential cachedCredential;
    private static Sheets cachedSheetsService;
    private static Drive cachedDriveService;

    /**
     * Creates an authorized Credential object. A browser is popped up for signing in to Google
     * if no locally saved credential is found.
     * @return An authorized Credential object.
     */
    public static Credential getCredential() throws IOException, GeneralSecurityException {

        if (cachedCredential != null) {
            logger.info("Cached credential found");
            return cachedCredential;
        }

        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        FileDataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

        // Load client secrets.
        InputStream in =
                GoogleAPIUtils.class.getResourceAsStream("/client_secret.json");
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

        logger.info("Credential acquired");
        logger.info("Credentials saved at " + DATA_STORE_DIR.getAbsolutePath());

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

    /**
     * Opens browser and asks for user name and password if not already signed in.
     * @return Email of the signed in user
     */
    public static String signInAndGetEmail() throws IOException, GeneralSecurityException {
        Credential credential = getCredential();
        Oauth2 oauth2 = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
        logger.info("Retrieving user email...");
        Userinfoplus userinfo = oauth2.userinfo().get().execute();
        return userinfo.getEmail();
    }

    /**
     * Build and return an authorized Sheets API client service.
     * @return An authorized Sheets API client service
     */
    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        if (cachedSheetsService != null) {
            return cachedSheetsService;
        }
        Credential credential = GoogleAPIUtils.getCredential();
        Sheets service = new Sheets.Builder(GoogleAPIUtils.HTTP_TRANSPORT,
                GoogleAPIUtils.JSON_FACTORY,
                credential)
                .setApplicationName(GoogleAPIUtils.APPLICATION_NAME)
                .build();
        cachedSheetsService = service;
        return service;
    }

    /**
     * Builds and returns an authorized Drive API client service.
     * @return An authorized Drive API client service
     */
    private static Drive getDriveService() throws IOException, GeneralSecurityException {
        if (cachedDriveService != null) {
            return cachedDriveService;
        }
        Credential credential = GoogleAPIUtils.getCredential();
        Drive service = new Drive.Builder(GoogleAPIUtils.HTTP_TRANSPORT,
                GoogleAPIUtils.JSON_FACTORY,
                credential)
                .setApplicationName(GoogleAPIUtils.APPLICATION_NAME)
                .build();
        cachedDriveService = service;
        return service;
    }

    /**
     * Uploads an image to Google Drive of current account.
     *
     * @param filePath Path to the local file to be uploaded
     * @param parentId Id of the Drive folder where this file will be put in. Set to null to put the
     *                 file in the root folder
     * @param description Description of the file
     * @return A link to the uploaded file
     */
    public static String uploadImageAndGetLink(String filePath, String parentId, String description)
            throws IOException, GeneralSecurityException {
        Drive drive = getDriveService();

        java.io.File localFile = new java.io.File(filePath);
        File fileMetadata = new File()
                .setName(localFile.getName())
                .setParents(Collections.singletonList(parentId))
                .setDescription(description);

        FileContent mediaContent = new FileContent("image/png", localFile);

        logger.debug("Uploading {}...", filePath);
        File returnedFile = drive.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();
        logger.debug("Upload done. Id: {}. Link: {}", returnedFile.getId(), returnedFile.getWebViewLink());

        return returnedFile.getWebViewLink();
    }
}
