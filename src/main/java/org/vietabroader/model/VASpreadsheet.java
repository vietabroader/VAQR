package org.vietabroader.model;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.sun.istack.internal.Nullable;
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
     * Constructs a new VASpreadsheet object.
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
     * Get the column name and content given the name of the column
     *
     * @param col the character of the column
     * @return True if the specified column exists. False otherwise.
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public boolean refreshOneColumn(String col) throws IOException, GeneralSecurityException {
        String colChar = columnNameToChar.get(col);
        if (colChar == null) {
            return false;
        }
        String range = sheetName + "!" + colChar + fromRow + ":" + colChar + toRow;
        ValueRange response = GoogleAPIUtils.getSheetsService().spreadsheets().values()
                .get(spreadsheetId, range)
                .setMajorDimension("COLUMNS")
                .execute();

        List<List<Object>> values = response.getValues();
        cachedColumns.put(col, values.get(0));
        return true;
    }

    public void refreshAllColumns() throws IOException, GeneralSecurityException {
        for (String key : columnNameToChar.keySet()) {
            refreshOneColumn(key);
        }
    }

    /**
     * Read a named column in this cache
     * @param col name of the column
     * @return items in specified column. Null if there is no such column
     * @throws IOException
     * @throws GeneralSecurityException
     */
    @Nullable
    public List<Object> readCol(String col) throws IOException, GeneralSecurityException {
        return cachedColumns.get(col);
    }

}
