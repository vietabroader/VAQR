import org.junit.Assert;
import org.junit.Test;
import org.vietabroader.model.VASpreadsheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class VASpreadsheetTest {

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
}
