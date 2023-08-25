package com.e.entity.form;

import com.e.entity.Customer;
import com.e.entity.User;
import com.e.entity.form.details.InvoiceDetails;
import com.e.entity.form.details.Position;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.List;


public class XlsxInvoice extends Form {

    private Row row;
    private Cell cl;
    private Workbook workbook;
    private Sheet list;

    public XlsxInvoice(User legalCard) {
        super(legalCard);
    }

    public XlsxInvoice(User user, Calendar date, int number) {
        super(user, date, number);
    }


    @Override
    public void createSample() {
        try {
            String[] bottomText = user.getBottomText().split("\n");
            int bottomTextRow = 53;
            Files.copy(Path.of("resources/FormAccount.xlsx"), new FileOutputStream(user.getTitle() + "\\sample.xlsx"));
            workbook = new XSSFWorkbook(new FileInputStream(user.getTitle() + "\\sample.xlsx"));
            list = workbook.getSheetAt(0);
            writeCellsMR(1, 2, 1, 22, user.getBank() + " " + user.getAddressBank());
            writeCellsMR(1, 29, 43, user.getBik());

            writeCellsMR(4, 4, 11, user.getInn());
            writeCellsMR(4, 14, 22, user.getKpp());
            writeCellsMR(2, 3, 29, 43, user.getKs());

            writeCellsMR(5, 6, 1, 22, user.getTitle());
            writeCellsMR(4, 7, 29, 43, user.getRs());
            writeCellsMR(13, 14, 6, 43, user.getTitle() + ", ИНН " + user.getInn() +
                    ", КПП " + user.getKpp() + ", " + user.getAddress());

            writeCellsMR(68, 12, 23, user.getDirector());
            writeCellsMR(68, 35, 43, user.getAccountant());

            for (String line : bottomText) {
                writeCellsMR(bottomTextRow, 1, 43, line);
                bottomTextRow++;
            }
            for (int i = bottomTextRow; i < 66; i++) {
                list.removeRow(list.getRow(i));
            }
            list.shiftRows(66, 68, bottomTextRow - 66);
            workbook.write(new FileOutputStream(user.getTitle() + "\\sample.xlsx"));
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void create(Customer partner, List<InvoiceDetails> positions) {
        byte[] bytes;
        int startRow = 47 - positions.size();
        int numPos = 1;
        int pngID;
        try {
            Files.copy(Path.of(user.getTitle() + "\\sample.xlsx"), new FileOutputStream(InvoiceDir + "\\" + number + "_" + millis + ".xlsx"));
            workbook = new XSSFWorkbook(new FileInputStream(InvoiceDir + "\\" + number + "_" + millis + ".xlsx"));
            list = workbook.getSheetAt(0);
            writeCellsMR(9, 10, 1, 43, "Счет на оплату № " + number + " от " + day + " " + month + " " + yearNum + " г.");
            writeCellsMR(16, 17, 6, 43, partner.getTitle() +
                    ", ИНН " + partner.getInn() + ", " +
                    "КПП " + partner.getKpp() + ", " + partner.getAddress());
            for (Position i : positions) {
                writeCellsMR(startRow, 1, 2, (numPos));
                writeCellsMR(startRow, 3, 23, i.getPosition());
                writeCellsMR(startRow, 24, 27, i.getQuantity());
                list.addMergedRegion(new CellRangeAddress(startRow, startRow, 28, 30));
                writeCellsMR(startRow, 31, 35, i.getPrice());
                writeCellsMR(startRow, 36, 42, i.getPrice() * i.getQuantity());

                sumAll = sumAll + (i.getPrice() * i.getQuantity());
                numPos++;
                startRow++;
            }

            writeCellsMR(48, 37, 42, sumAll);
            writeCellsMR(50, 37, 42, sumAll);
            writeCellsMR(51, 1, 43, "Всего наименований " + positions.size() +
                    ", на сумму " + String.format("%.2f", sumAll) + " руб.");

            FwMoney fw = new FwMoney(sumAll);
            String sum1 = fw.num2str();
            writeCellsMR(52, 1, 41, sum1.substring(0, 1).toUpperCase() + sum1.substring(1));
            workbook.write(new FileOutputStream(InvoiceDir + "\\" + number + "_" + millis + ".xlsx"));
            list.shiftRows((47 - numPos), 46, numPos - 25);
            for (int i = 46 - numPos; i > 22 + numPos; i--) list.removeRow(list.getRow(i));
            list.shiftRows(47, 61, numPos - 25);

            bytes = IOUtils.toByteArray(new FileInputStream(user.getTitle() + "\\" + "stamp1.png"));
            pngID = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            CreationHelper help = workbook.getCreationHelper();
            Drawing<?> drawing = list.createDrawingPatriarch();
            ClientAnchor anchor = help.createClientAnchor();
            anchor.setCol1(12);
            anchor.setRow1(31 + numPos);
            Picture pict = drawing.createPicture(anchor, pngID);
            pict.resize(1.15, 1.05);

            bytes = IOUtils.toByteArray(new FileInputStream(user.getTitle() + "\\" + "stamp2.png"));
            pngID = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            help = workbook.getCreationHelper();
            drawing = list.createDrawingPatriarch();
            anchor = help.createClientAnchor();
            anchor.setCol1(30);
            anchor.setRow1(31 + numPos);
            pict = drawing.createPicture(anchor, pngID);
            pict.resize(1.15, 1.05);

            workbook.write(new FileOutputStream(InvoiceDir + "\\" + number + "_" + millis + ".xlsx"));
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeCellsMR(int rowId, int cellId1, int cellId2, String text) {
        row = list.getRow(rowId);
        cl = row.getCell(cellId1);
        cl.setCellValue(text);
        list.addMergedRegion(new CellRangeAddress(rowId, rowId, cellId1, cellId2));
    }

    private void writeCellsMR(int rowId, int cellId1, int cellId2, double text) {
        row = list.getRow(rowId);
        cl = row.getCell(cellId1);
        cl.setCellValue(text);
        list.addMergedRegion(new CellRangeAddress(rowId, rowId, cellId1, cellId2));
    }

    private void writeCellsMR(int rowId, int cellId1, int cellId2, int text) {
        row = list.getRow(rowId);
        cl = row.getCell(cellId1);
        cl.setCellValue(text);
        list.addMergedRegion(new CellRangeAddress(rowId, rowId, cellId1, cellId2));
    }

    private void writeCellsMR(int rowId1, int rowId2, int cellId1, int cellId2, String text) {
        row = list.getRow(rowId1);
        cl = row.getCell(cellId1);
        cl.setCellValue(text);
        list.addMergedRegion(new CellRangeAddress(rowId1, rowId2, cellId1, cellId2));
    }
}
