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
    private HashMap<String,String> columnMap;
    private HashMap<String,List<List<Object>> > contentMap;

    public void init(){
        columnMap = new HashMap<String, String>();
        contentMap = new HashMap<String,List<List<Object>>>();
    }


    /**
     * Read first column character from the sheet/spreadsheet for testing the refreshColumn method
     * @return The Item column character in the specific row
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public String readFirstColumn() throws IOException, GeneralSecurityException{
        refreshColumn("A");
        return columnMap.get("Item");
    }

    /**
     * Read first key from the Item column for testing the refreshColumn method
     * @return The first key
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public String readFirstKey() throws IOException, GeneralSecurityException{
        refreshColumn("A");
        List<List<Object>> l = contentMap.get("Item");
        return l.get(1).get(0).toString();
    }


    //note: get(a).get(b): a row, b column

    /**
     * Get the column name and content given the character of the column
     * @param s the character of the column
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public void refreshColumn(String s) throws IOException, GeneralSecurityException{
        String sheetname = getSheetTitles().get(0);
        String range = sheetname + "!"+s+":"+s;
        ValueRange response = GoogleAPIUtils.getSheetsService().spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        columnMap.put(values.get(0).get(0).toString(),s);
        contentMap.put(values.get(0).get(0).toString(),values);
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
