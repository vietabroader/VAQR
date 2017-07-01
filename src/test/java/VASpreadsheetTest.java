import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.vietabroader.model.VASpreadsheet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class VASpreadsheetTest {

    // https://docs.google.com/spreadsheets/d/1tkSzIT2AJT9tXHB9BI8FW8CrZiDCEOnpVtCJtQUrhjI
    final private static String TEST_SPREADSHEET_ID = "1tkSzIT2AJT9tXHB9BI8FW8CrZiDCEOnpVtCJtQUrhjI";

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

    private static VASpreadsheet testSpreadsheet;

    @BeforeClass
    public static void setUpSpreadsheet() {
        testSpreadsheet = new VASpreadsheet(TEST_SPREADSHEET_ID);
        try {
            testSpreadsheet.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetSheetTitles() throws IOException, GeneralSecurityException {
        List<String> titles = testSpreadsheet.getSheetTitles();
        Assert.assertTrue(titles.contains("Receipt"));
        Assert.assertTrue(titles.contains("Language"));
    }

    @Test
    public void testReadOneColumn() throws
            IOException, GeneralSecurityException, VASpreadsheet.VASpreadsheetException {

        String ITEM = "Item";
        testSpreadsheet.setSheetName("Receipt")
                    .setColumnChar(ITEM, "A")
                    .setRow(2, 4)
                    .refreshOneColumn(ITEM);

        List<Object> itemList = testSpreadsheet.readCol(ITEM);
        assertList(itemList, "Book", "Laptop", "Desk");
    }

    @Test(expected = VASpreadsheet.VASpreadsheetException.class)
    public void testReadOneColumnWrongColumn() throws
            IOException, GeneralSecurityException, VASpreadsheet.VASpreadsheetException {

        String ITEM = "Item";
        testSpreadsheet.setSheetName("Receipt")
                .setColumnChar(ITEM, "A")
                .setRow(2, 4)
                .refreshOneColumn(ITEM);

        List<Object> itemList = testSpreadsheet.readCol("ItemX");
    }


    @Test
    public void testReadMultipleColumn() throws
            IOException, GeneralSecurityException, VASpreadsheet.VASpreadsheetException {
        String PRICE = "Price";
        String QUANTITY = "Quantity";
        testSpreadsheet.setSheetName("Receipt")
                .setColumnChar(PRICE, "B")
                .setColumnChar(QUANTITY, "C")
                .setRow(2, 4)
                .refreshAllColumns();

        List<Object> priceList = testSpreadsheet.readCol(PRICE);
        List<Object> quantityList = testSpreadsheet.readCol(QUANTITY);
        assertList(priceList, "$10", "$200", "$100");
        assertList(quantityList, "2", "1", "10");
    }

    @Test(expected = VASpreadsheet.VASpreadsheetException.class)
    public void testA1ParsingErrorSheetName() throws
        IOException, GeneralSecurityException, VASpreadsheet.VASpreadsheetException {
        String PRICE = "Price";
        testSpreadsheet.setSheetName("AaAaAaAaAa")
                .setColumnChar(PRICE, "B")
                .setRow(2, 4)
                .refreshAllColumns();
    }

    @Test(expected = VASpreadsheet.VASpreadsheetException.class)
    public void testA1ParsingErrorColumn() throws
            IOException, GeneralSecurityException, VASpreadsheet.VASpreadsheetException {
        String PRICE = "Price";
        testSpreadsheet.setSheetName("Receipt")
                .setColumnChar(PRICE, "@")
                .setRow(2, 4)
                .refreshAllColumns();
    }

    @Test
    public void testReadOneValue() throws
            IOException, GeneralSecurityException, VASpreadsheet.VASpreadsheetException {

        String ITEM = "Item";
        testSpreadsheet.setSheetName("Receipt")
                .setColumnChar(ITEM, "A")
                .setRow(2, 4)
                .refreshOneColumn(ITEM);

        Object item = testSpreadsheet.readValue(ITEM, 0);
        Assert.assertEquals("Book", item);
    }

    @Test(expected = VASpreadsheet.VASpreadsheetException.class)
    public void testReadOneValueWrongColumnName() throws
            IOException, GeneralSecurityException, VASpreadsheet.VASpreadsheetException {
        String ITEM = "Item";
        testSpreadsheet.setSheetName("Receipt")
                .setColumnChar(ITEM, "A")
                .setRow(2, 4)
                .refreshOneColumn(ITEM);
        testSpreadsheet.readValue("ItemX", 0);
    }

    @Test(expected = VASpreadsheet.VASpreadsheetException.class)
    public void testReadOneValueWrongRow() throws
            IOException, GeneralSecurityException, VASpreadsheet.VASpreadsheetException {
        String ITEM = "Item";
        testSpreadsheet.setSheetName("Receipt")
                .setColumnChar(ITEM, "A")
                .setRow(2, 4)
                .refreshOneColumn(ITEM);
        testSpreadsheet.readValue(ITEM, 999);
    }

    @Test
    public void testGetNumRow() throws
            IOException, GeneralSecurityException, VASpreadsheet.VASpreadsheetException {
        testSpreadsheet.setRow(2, 4);
        int numRow = testSpreadsheet.getNumRow();
        Assert.assertEquals(numRow,3);
    }

    // https://docs.google.com/spreadsheets/d/1VanAqWzI8Z4g4lYW5gJEUR4KgQyIix89wSkPB63EAJI/
    @Test
    public void testWriteValue() throws
            IOException, GeneralSecurityException, VASpreadsheet.VASpreadsheetException {
        String TEST_SPREADSHEET_ID2 = "1VanAqWzI8Z4g4lYW5gJEUR4KgQyIix89wSkPB63EAJI";
        VASpreadsheet testSpreadsheet2 = new VASpreadsheet(TEST_SPREADSHEET_ID2);
        testSpreadsheet2.connect();
        String ITEM = "Item";
        testSpreadsheet2.setSheetName("Receipt")
                .setColumnChar(ITEM, "A")
                .setRow(2, 4)
                .refreshOneColumn(ITEM);
        testSpreadsheet2.writeValue(ITEM,2,"Empty");
        testSpreadsheet2.uploadOneColumn(ITEM);

        testSpreadsheet2.writeValue(ITEM,2,"Laptop");
        testSpreadsheet2.uploadOneColumn(ITEM);

        testSpreadsheet2.writeValue(ITEM, 2, "Empty");
        testSpreadsheet2.refreshOneColumn(ITEM);

        List<Object> itemList = testSpreadsheet2.readCol(ITEM);
        assertList(itemList, "Book", "Chair", "Laptop");
    }

    private void assertList(List<Object> actual, Object... expected) {
        List<Object> expectedList = Arrays.asList(expected);
        Assert.assertEquals(expectedList.size(), actual.size());
        for (int i = 0; i < actual.size(); i++) {
            Assert.assertEquals(expectedList.get(i), actual.get(i));
        }
    }
}
