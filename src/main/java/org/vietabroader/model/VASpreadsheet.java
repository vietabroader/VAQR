package org.vietabroader.model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class models a Google spreadsheet and is specialized for VAQR. It hides
 * away Sheets API calls and caches the select cell values in local memory.
 */
public class VASpreadsheet {

    private static Sheets cachedSheetsService;

    /**
     * Build and return an authorized Sheets API client service.
     * @return an authorized Sheets API client service
     */
        public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        if (cachedSheetsService != null) {
            return cachedSheetsService;
        }
        Credential credential = Authorization.authorize();
        Sheets service = new Sheets.Builder(Authorization.HTTP_TRANSPORT,
                                            Authorization.JSON_FACTORY,
                                            credential)
                .setApplicationName(Authorization.APPLICATION_NAME)
                .build();
        cachedSheetsService = service;
        return service;
    }

    private String spreadsheetId;
    private Spreadsheet spreadSheet;

    /**
     * Constructs a new VASpreadsheet object.
     * @param spreadsheetId id of the spreadsheet. This value can be obtained from the URL to the
     *                      spreadsheet.
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public VASpreadsheet(String spreadsheetId) throws IOException, GeneralSecurityException {
        this.spreadsheetId = spreadsheetId;
        this.spreadSheet = getSheetsService().spreadsheets().get(spreadsheetId).execute();
    }

    /**
     * @return the title of this spreadsheet
     */
    public String getSpreadsheetTitle() {
        return this.spreadSheet.getProperties().getTitle();
    }

    /**
     * @return a list of titles of the sheets belong to this spreadsheet.
     */
    public List<String> getSheetTitles() {
        List<String> titles = new ArrayList<>();
        for (Sheet sh : spreadSheet.getSheets()) {
            titles.add(sh.getProperties().getTitle());
        }
        return titles;
    }
}
