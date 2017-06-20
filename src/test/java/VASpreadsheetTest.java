import org.junit.Assert;
import org.junit.Test;
import org.vietabroader.model.VASpreadsheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class VASpreadsheetTest {

    // https://docs.google.com/spreadsheets/d/1tkSzIT2AJT9tXHB9BI8FW8CrZiDCEOnpVtCJtQUrhjI
    final private String TEST_SPREADSHEET_ID = "1tkSzIT2AJT9tXHB9BI8FW8CrZiDCEOnpVtCJtQUrhjI";

    @Test
    public void testConstructVASpreadsheet() throws IOException, GeneralSecurityException {
        VASpreadsheet spreadsheet = new VASpreadsheet(TEST_SPREADSHEET_ID);
        spreadsheet.connect();
        Assert.assertEquals("Test Spreadsheet", spreadsheet.getSpreadsheetTitle());
    }

    @Test(expected = IOException.class)
    public void testFailToConstructVASpreadsheet() throws IOException, GeneralSecurityException {
        VASpreadsheet spreadsheet = new VASpreadsheet("XXXXX");
        spreadsheet.connect();
    }

    @Test
    public void testGetSheetTitles() throws IOException, GeneralSecurityException {
        VASpreadsheet spreadsheet = new VASpreadsheet(TEST_SPREADSHEET_ID);
        spreadsheet.connect();
        List<String> titles = spreadsheet.getSheetTitles();
        Assert.assertTrue(titles.contains("Receipt"));
        Assert.assertTrue(titles.contains("Language"));
    }

    @Test
    public void testReadOneColumn() throws IOException, GeneralSecurityException {
        VASpreadsheet spreadsheet = new VASpreadsheet(TEST_SPREADSHEET_ID);
        spreadsheet.connect();

        String ITEM = "Item";
        spreadsheet.setSheetName("Receipt")
                    .setColumnChar(ITEM, "A")
                    .setRow(2, 4)
                    .refreshOneColumn(ITEM);

        List<Object> itemList = spreadsheet.readCol("Item");
        assertList(itemList, "Book", "Laptop", "Desk");
    }

    @Test
    public void testReadMultipleColumn() throws IOException, GeneralSecurityException {
        VASpreadsheet spreadsheet = new VASpreadsheet(TEST_SPREADSHEET_ID);
        spreadsheet.connect();

        String PRICE = "Price";
        String QUANTITY = "Quantity";
        spreadsheet.setSheetName("Receipt")
                .setColumnChar(PRICE, "B")
                .setColumnChar(QUANTITY, "C")
                .setRow(2, 4)
                .refreshAllColumns();

        List<Object> priceList = spreadsheet.readCol(PRICE);
        List<Object> quantityList = spreadsheet.readCol(QUANTITY);
        assertList(priceList, "$10", "$200", "$100");
        assertList(quantityList, "2", "1", "10");
    }

    private void assertList(List<Object> actual, Object... expected) {
        List<Object> expectedList = Arrays.asList(expected);
        Assert.assertEquals(expectedList.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            Assert.assertEquals(expectedList.get(i), actual.get(i));
        }
    }
}
