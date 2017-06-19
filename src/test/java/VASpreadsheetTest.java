import org.junit.Assert;
import org.junit.Test;
import org.vietabroader.model.VASpreadsheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.ArrayList;

public class VASpreadsheetTest {

    // https://docs.google.com/spreadsheets/d/1tkSzIT2AJT9tXHB9BI8FW8CrZiDCEOnpVtCJtQUrhjI
    final private String TEST_SPREADSHEET_ID = "1tkSzIT2AJT9tXHB9BI8FW8CrZiDCEOnpVtCJtQUrhjI";

    @Test
    public void testConstructVASpreadsheet() throws IOException, GeneralSecurityException {
        VASpreadsheet spreadsheet = new VASpreadsheet(TEST_SPREADSHEET_ID);
        Assert.assertEquals("Test Spreadsheet", spreadsheet.getSpreadsheetTitle());
    }

    @Test(expected = IOException.class)
    public void testFailToConstructVASpreadsheet() throws IOException, GeneralSecurityException {
        VASpreadsheet spreadsheet = new VASpreadsheet("XXXXX");
    }

    @Test
    public void testGetSheetTitles() throws IOException, GeneralSecurityException {
        VASpreadsheet spreadsheet = new VASpreadsheet(TEST_SPREADSHEET_ID);
        List<String> titles = spreadsheet.getSheetTitles();
        Assert.assertTrue(titles.contains("Receipt"));
        Assert.assertTrue(titles.contains("Language"));
    }


    @Test
    public void testReadKey() throws IOException, GeneralSecurityException{
        VASpreadsheet spreadsheet = new VASpreadsheet(TEST_SPREADSHEET_ID);
        spreadsheet.init();
        spreadsheet.setColumnChar("Item","A");
        spreadsheet.setRow(2,4);
        List<Object> list1 = spreadsheet.readCol("Receipt","Item");
        List<Object> list2 = new ArrayList<Object>();
        list2.add("Book");
        list2.add("Laptop");
        list2.add("Desk");
        Assert.assertEquals(list1.size(),list2.size());
        for (int i = 0; i < list1.size(); i++){
            Assert.assertEquals(list1.get(i),list2.get(i));
        }

    }
}
