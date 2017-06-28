package org.vietabroader.model;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.vietabroader.GoogleAPIUtils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class models a Google spreadsheet and is specialized for VAQR. It hides
 * away Sheets API calls and caches the select cell values in local memory.
 */
public class VASpreadsheet {

    // Spreadsheet data
    private String spreadsheetId;
    private Spreadsheet spreadSheet;

    // Sheet data
    private int fromRow = 1;
    private int toRow = 1;
    private String sheetName;
    private HashMap<String, String> columnNameToChar = new HashMap<>();
    private HashMap<String, List<Object>> cachedColumns = new HashMap<>();

    /**
     * Construct a new VASpreadsheet object.
     * @param spreadsheetId id of the spreadsheet. This value can be obtained from the URL to the
     *                      spreadsheet.
     */
    public VASpreadsheet(String spreadsheetId) {
        this.spreadsheetId = spreadsheetId;
    }

    /**
     * Connect to the spreadsheet and download metadata. This method needs to be called before calling
     * any of the sheet-related methods.
     *
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void connect() throws IOException, GeneralSecurityException {
        this.spreadSheet = GoogleAPIUtils.getSheetsService().spreadsheets()
                .get(spreadsheetId)
                .execute();
    }

    /**
     * @return The title of this spreadsheet
     */
    public String getSpreadsheetTitle() {
        return this.spreadSheet.getProperties().getTitle();
    }

    /**
     * @return A list of titles of the sheets belong to this spreadsheet.
     */
    public List<String> getSheetTitles() {
        List<String> titles = new ArrayList<>();
        for (Sheet sh : spreadSheet.getSheets()) {
            titles.add(sh.getProperties().getTitle());
        }
        return titles;
    }

    /**
     * Set a name for a column
     * @param columnName the name of the column
     * @param character the character of the column
     */
     public VASpreadsheet setColumnChar(String columnName, String character){
         columnNameToChar.put(columnName,character);
         return this;
     }

    /**
     * Set the starting and ending row of this cache
     * @param rowStart the first row
     * @param rowEnd the last row
     */
     public VASpreadsheet setRow(int rowStart, int rowEnd){
         fromRow = rowStart;
         toRow = rowEnd;
         return this;
     }

     public VASpreadsheet setSheetName(String sheetName) {
         this.sheetName = sheetName;
         return this;
     }

    /**
     * Update one column with online data
     *
     * @param colName the character of the column
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws VASpreadsheetException
     */
    public void refreshOneColumn(String colName) throws
            IOException, GeneralSecurityException, VASpreadsheetException {
        String colChar = columnNameToChar.get(colName);
        if (colChar == null) {
            throw new VASpreadsheetException("Cannot find column name: " + colName);
        }
        String range = sheetName + "!" + colChar + fromRow + ":" + colChar + toRow;
        try {
            ValueRange response = GoogleAPIUtils.getSheetsService().spreadsheets().values()
                    .get(spreadsheetId, range)
                    .setMajorDimension("COLUMNS")
                    .execute();

            List<List<Object>> values = response.getValues();
            cachedColumns.put(colName, values.get(0));
        } catch (GoogleJsonResponseException e) {
            throw new VASpreadsheetException("Spreadsheet responds with error: " + e.getDetails().getMessage());
        }
    }

    /**
     * Update all column with online data
     *
     * @throws IOException
     * @throws GeneralSecurityException
     * @throws VASpreadsheetException
     */
    public void refreshAllColumns() throws
            IOException, GeneralSecurityException, VASpreadsheetException {
        for (String key : columnNameToChar.keySet()) {
            refreshOneColumn(key);
        }
    }

    /**
     * Remove all column names in columnNameToChar
     *
     */
    public void removeAllColumns() {
        this.columnNameToChar.clear();
    }

    /**
     * Read a named column in this cache
     *
     * @param colName name of the column
     * @return a list of values in the specified column
     * @throws VASpreadsheetException when column name does not exist
     */
    public List<Object> readCol(String colName) throws VASpreadsheetException {
        if (!cachedColumns.containsKey(colName)) {
            throw new VASpreadsheetException("Cannot find column name: " + colName);
        }
        return cachedColumns.get(colName);
    }

    /**
     * Read a value given its column name and row number
     *
     * @param colName name of the column
     * @param row row number
     * @return a value
     * @throws VASpreadsheetException when column name does not exist or row number is out of range
     */
    public Object readValue(String colName, int row) throws VASpreadsheetException {
        List<Object> col = cachedColumns.get(colName);
        if (col == null) {
            throw new VASpreadsheetException("Cannot find column name: " + colName);
        }
        if (row < 0 || row >= col.size()) {
            throw new VASpreadsheetException(
                    "Row number is out of range. Should be within [0, " + col.size() + ")");
        }
        return col.get(row);
    }



    public static class VASpreadsheetException extends Exception {
        public VASpreadsheetException(String msg) {
            super(msg);
        }
    }
}
