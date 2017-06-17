import org.junit.Assert;
import org.junit.Test;
import org.vietabroader.model.VASpreadsheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

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
    public void testReadColumn() throws IOException, GeneralSecurityException{
        VASpreadsheet spreadsheet = new VASpreadsheet(TEST_SPREADSHEET_ID);
        spreadsheet.init();
        String key1 = spreadsheet.readFirstColumn();
        String key2 = "A";
        Assert.assertEquals(key1,key2);
    }

    @Test
    public void testReadKey() throws IOException, GeneralSecurityException{
        VASpreadsheet spreadsheet = new VASpreadsheet(TEST_SPREADSHEET_ID);
        spreadsheet.init();
        String key1 = spreadsheet.readFirstKey();
        String key2 = "Book";
        Assert.assertEquals(key1,key2);
    }
}
