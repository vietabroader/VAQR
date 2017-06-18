package org.vietabroader.model;

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


    private String spreadsheetId;
    private Spreadsheet spreadSheet;
    private int fromRow;
    private int toRow;
    private HashMap<String,String> columnNameToChar;
    private HashMap<String,List<Object>> cachedColumns;

    /**
     * Init the maps
     */
    public void init(){
        columnNameToChar = new HashMap<String, String>();
        cachedColumns = new HashMap<String,List<Object>>();
    }

    /**
     * Set the column
     * @param columnName the name of the column
     * @param character the character of the column
     */
     public void setColumnChar(String columnName, String character){
         columnNameToChar.put(columnName,character);
     }

    /**
     * Set the row
     * @param rowStart the first row
     * @param rowEnd the last row
     */
     public void setRow(int rowStart, int rowEnd){
         fromRow = rowStart;
         toRow = rowEnd;
     }

    /**
     * Read first key from the Item column for testing the refreshColumn method
     * @param sheetName name of the sheet
     * @param col name of the column
     * @return the first key in the column
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public String readFirstKey(String sheetName, String col) throws IOException, GeneralSecurityException{
        refreshColumn(sheetName,col);
        List<Object> l = cachedColumns.get(col);
        return l.get(0).toString();
    }


    //note: get(a).get(b): a row, b column

    /**
     * Get the column name and content given the name of the column
     * @param col the character of the column
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void refreshColumn(String sheetName, String col) throws IOException, GeneralSecurityException{
        String colChar = columnNameToChar.get(col);
        String range = sheetName + "!" + colChar + fromRow + ":" + colChar + toRow;
        ValueRange response = GoogleAPIUtils.getSheetsService().spreadsheets().values()
                .get(spreadsheetId, range)
                .setMajorDimension("COLUMNS")
                .execute();
        List<List<Object>> values = response.getValues();
        cachedColumns.put(col, values.get(0));
    }


    /**
     * Constructs a new VASpreadsheet object.
     * @param spreadsheetId id of the spreadsheet. This value can be obtained from the URL to the
     *                      spreadsheet.
     */
    public VASpreadsheet(String spreadsheetId) throws IOException, GeneralSecurityException {
        this.spreadsheetId = spreadsheetId;
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
}
