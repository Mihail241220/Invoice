package com.e.entity.form;

import be.quodlibet.boxable.*;
import be.quodlibet.boxable.line.LineStyle;
import com.e.entity.Customer;
import com.e.entity.User;
import com.e.entity.form.details.InvoiceDetails;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;


public class PdfInvoice extends Form {

    private final float COF_CM = 28.34645791245791f;
    private final float MARGIN = 2.08f * COF_CM;
    private final float HIGH_TEXT = 0.432f * COF_CM;

    private final LineStyle NORMAL_L = new LineStyle(Color.BLACK, 1f);
    private final LineStyle BLACK_L = new LineStyle(Color.BLACK, 2);
    private final LineStyle WHITE_L = new LineStyle(Color.WHITE, 1f);

    private PDDocument mainDocument;
    private Row<PDPage> row;
    private Cell<PDPage> cell;


    public PdfInvoice(User user) {
        super(user);
    }

    public PdfInvoice(User user, Calendar date, int number) {
        super(user, date, number);
    }

    @Override
    public void createSample() {
        try {

            mainDocument = new PDDocument();
            PDPage myPage = new PDPage(PDRectangle.A4);
            PDPageContentStream contentStream = new PDPageContentStream(mainDocument, myPage);

            //1
            float sub;
            float margin = MARGIN;
            float tableWidth = (9.14f * COF_CM);
            float yPosition = (myPage.getMediaBox().getHeight() - (2.256f * COF_CM));

            BaseTable table = new BaseTable(yPosition, 0, 0, tableWidth, margin, mainDocument, myPage, true, true);

            row = table.createRow(0.788f * COF_CM);

            createMyCell(100, user.getBank() + " " + user.getAddressBank());
            cell.setBottomBorderStyle(null);
            row = table.createRow(0.356f * COF_CM);
            createMyCell(100, "Банк получателя");
            cell.setTopBorderStyle(null);

            row = table.createRow(HIGH_TEXT);

            createMyCell(52f, "ИНН   " + user.getInn());
            createMyCell(48f, "КПП   " + user.getKpp());
            cell.setLeftBorderStyle(WHITE_L);

            row = table.createRow(0.712f * COF_CM);
            createMyCell(100, user.getTitle());
            cell.setBottomBorderStyle(null);
            row = table.createRow(0.356f * COF_CM);
            createMyCell(100, "Получатель");
            cell.setTopBorderStyle(null);

            table.draw();

            //2
            margin = margin + tableWidth - 1f;
            tableWidth = (1.6f * COF_CM);


            table = new BaseTable(yPosition, 0, 0, tableWidth, margin, mainDocument, myPage, true, true);

            row = table.createRow(HIGH_TEXT);
            createMyCell(100, "БИК");

            row = table.createRow(0.712f * COF_CM);
            createMyCell(100, "Сч. №", false, HorizontalAlignment.LEFT, VerticalAlignment.TOP);

            row = table.createRow(1.5f * COF_CM);
            createMyCell(100, "Сч. №", false, HorizontalAlignment.LEFT, VerticalAlignment.TOP);

            table.draw();

            //3
            margin = margin + tableWidth - 1f;
            tableWidth = (5.74f * COF_CM);


            table = new BaseTable(yPosition, 0, 0, tableWidth, margin, mainDocument, myPage, true, true);

            row = table.createRow(HIGH_TEXT);
            createMyCell(100, user.getBik(), false, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
            cell.setBottomBorderStyle(null);

            row = table.createRow(0.712f * COF_CM);
            createMyCell(100, user.getKs(), false, HorizontalAlignment.LEFT, VerticalAlignment.TOP);


            row = table.createRow(1.5f * COF_CM);
            createMyCell(100, user.getRs(), false, HorizontalAlignment.LEFT, VerticalAlignment.TOP);

            table.draw();
            margin = MARGIN;
            tableWidth = 16.46f * COF_CM;
            yPosition = 665.5181f;

            table = new BaseTable(yPosition, 0, 0, tableWidth, margin, mainDocument, myPage, false, true);

            row = table.createRow(0.229f * COF_CM);
            createMyCell(100, "");

            row = table.createRow(1.042f * COF_CM);
            sub = (2.261f / (tableWidth / COF_CM)) * 100;
            createMyCell(sub, "Поставщик (Исполнитель):");
            createMyCell(100 - sub, user.getTitle() + ", ИНН " + user.getInn() +
                    ", КПП " + user.getKpp() + ", " + user.getAddress(), true);
            table.draw();

            contentStream.close();
            mainDocument.addPage(myPage);
            mainDocument.save(user.getTitle() + "\\sample.pdf");
            mainDocument.close();

        } catch (IOException e) {
            System.out.println(e + "PDF Account not create");
        }

    }

    @Override
    public void create(Customer partner, List<InvoiceDetails> positions) {
        final float START_NUM_Account = 692.1921f;
        final float START_BUYER = 629.4898f;
        try {
            PDDocument sampleFile = PDDocument.load(new File(user.getTitle() + "\\sample.pdf"));
            mainDocument = new PDDocument();
            PDPage myPage = sampleFile.getPage(0);
            PDPageContentStream contentStream = new PDPageContentStream(mainDocument, myPage, PDPageContentStream.AppendMode.APPEND, false);

            float sub;
            float margin = MARGIN;
            float tableWidth;
            float yPosition;

            //N Account
            tableWidth = 16.46f * COF_CM;
            yPosition = START_NUM_Account;

            BaseTable table = new BaseTable(yPosition, 0, 0, tableWidth, margin, mainDocument, myPage, true, true);
            row = table.createRow(0.941f * COF_CM);
            createMyCell(100, "Счет на оплату № " + number +
                    " от " + day + " " + month + yearNum + " г.", true);
            cell.setFontSize(15);
            cell.setBorderStyle(WHITE_L);
            cell.setBottomBorderStyle(BLACK_L);

            table.draw();
            //реквизиты
            yPosition = START_BUYER;
            sub = (2.261f / (tableWidth / COF_CM)) * 100;

            table = new BaseTable(yPosition, 0, 0, tableWidth, margin, mainDocument, myPage, false, true);
            row = table.createRow(1.042f * COF_CM);
            createMyCell(sub, "Покупатель (Заказчик):");
            createMyCell(100 - sub, partner.getTitle() + ", ИНН " + partner.getInn() + ", " +
                    "КПП " + partner.getKpp() + ", " + partner.getAddress(), true);

            row = table.createRow(HIGH_TEXT);
            createMyCell(sub, "Основание:");
            createMyCell(100 - sub, " ");

            sub = table.draw();
            //Position
            yPosition = sub - (0.229f * COF_CM);
            sub = 16.358f;
            tableWidth = (sub * COF_CM);

            table = new BaseTable(yPosition, 0, 0, tableWidth,
                    margin, mainDocument, myPage, true, true);
            row = table.createRow(HIGH_TEXT);
            createMyCell(5, "№", true,
                    HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
            cell.setLeftBorderStyle(BLACK_L);
            cell.setTopBorderStyle(BLACK_L);

            createMyCell(50, "Товары (работы, услуги)", true, HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
            cell.setTopBorderStyle(BLACK_L);
            cell.setLeftBorderStyle(null);

            createMyCell(9, "Кол-во", true, HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
            cell.setTopBorderStyle(BLACK_L);
            cell.setLeftBorderStyle(null);

            createMyCell(7, "Ед.", true, HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
            cell.setTopBorderStyle(BLACK_L);
            cell.setLeftBorderStyle(null);

            createMyCell(13, "Цена", true, HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
            cell.setTopBorderStyle(BLACK_L);
            cell.setLeftBorderStyle(null);

            createMyCell(16, "Сумма", true, HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
            cell.setTopBorderStyle(BLACK_L);
            cell.setRightBorderStyle(BLACK_L);
            cell.setLeftBorderStyle(null);
            double sumPos;
            for (int i = 0; i < positions.size(); i++) {
                row = table.createRow(HIGH_TEXT);
                if (i == positions.size() - 1) {
                    createMyCell(5, Integer.toString(i), false, HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
                    cell.setBottomBorderStyle(BLACK_L);
                    cell.setLeftBorderStyle(BLACK_L);

                    createMyCell(50, positions.get(i).getPosition());
                    cell.setBottomBorderStyle(BLACK_L);
                    cell.setLeftBorderStyle(null);

                    createMyCell(9, Integer.toString(positions.get(i).getQuantity()), false, HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE);
                    cell.setBottomBorderStyle(BLACK_L);
                    cell.setLeftBorderStyle(null);

                    createMyCell(7, "шт");
                    cell.setBottomBorderStyle(BLACK_L);
                    cell.setLeftBorderStyle(null);

                    createMyCell(13, positions.get(i).getPrice().toString(), false, HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE);
                    cell.setBottomBorderStyle(BLACK_L);
                    cell.setLeftBorderStyle(null);

                    sumPos = positions.get(i).getPrice() * positions.get(i).getQuantity();
                    createMyCell(16, String.format("%.2f", sumPos), false, HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE);
                    cell.setLeftBorderStyle(null);
                    cell.setRightBorderStyle(BLACK_L);
                    cell.setBottomBorderStyle(BLACK_L);

                } else {
                    createMyCell(5, Integer.toString(i), false, HorizontalAlignment.CENTER, VerticalAlignment.MIDDLE);
                    cell.setLeftBorderStyle(BLACK_L);

                    createMyCell(50, positions.get(i).getPosition());
                    cell.setLeftBorderStyle(null);

                    createMyCell(9, Integer.toString(positions.get(i).getQuantity()), false, HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE);
                    cell.setLeftBorderStyle(null);

                    createMyCell(7, "шт");
                    cell.setLeftBorderStyle(null);

                    createMyCell(13, positions.get(i).getPrice().toString(), false, HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE);
                    cell.setLeftBorderStyle(null);

                    sumPos = positions.get(i).getPrice() * positions.get(i).getQuantity();
                    createMyCell(16, String.format("%.2f", sumPos), false, HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE);
                    cell.setLeftBorderStyle(null);
                    cell.setRightBorderStyle(BLACK_L);
                }
                sumAll += sumPos;
            }

            sub = table.draw();

            yPosition = sub;
            tableWidth = (5 * COF_CM);
            margin = myPage.getMediaBox().getWidth() - (7.61f * COF_CM);

            table = new BaseTable(yPosition, 0, 0, tableWidth, margin, mainDocument, myPage, false, true);
            row = table.createRow(0.229f * COF_CM);

            row = table.createRow(HIGH_TEXT);
            createMyCell(50, "Итого:", true, HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE);
            createMyCell(50, String.format("%.2f", sumAll), true, HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE);

            row = table.createRow(HIGH_TEXT);
            createMyCell(50, "Без налога НДС:", true, HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE);
            createMyCell(50, "-", true, HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE);

            row = table.createRow(HIGH_TEXT);
            createMyCell(50, "Всего к оплате:", true, HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE);
            createMyCell(50, String.format("%.2f", sumAll), true, HorizontalAlignment.RIGHT, VerticalAlignment.MIDDLE);
            sub = table.draw();

            yPosition = sub;
            tableWidth = 16.46f * COF_CM;
            margin = MARGIN;
            table = new BaseTable(yPosition, 0, 0, tableWidth, margin, mainDocument, myPage, false, true);

            row = table.createRow(HIGH_TEXT);
            createMyCell(100, "Всего наименований " + positions.size() + ", на сумму " + String.format("%.2f", sumAll) + " руб.");

            row = table.createRow(HIGH_TEXT);
            createMyCell(100, new FwMoney(sumAll).num2str(true), true, HorizontalAlignment.LEFT, VerticalAlignment.TOP);

            sub = table.draw();
            yPosition = sub;

            table = new BaseTable(yPosition, 0, 0, tableWidth, margin, mainDocument, myPage, false, true);
            row = table.createRow(0.229f * COF_CM);
            row = table.createRow(HIGH_TEXT);
            createMyCell(100, user.getBottomText().replaceAll("\n", "<br>"));
            sub = table.draw();
            yPosition = sub;

            table = new BaseTable(yPosition, 0, 0, tableWidth, margin, mainDocument, myPage, true, true);

            row = table.createRow(0.229f * COF_CM);
            cell = row.createCell(100, " ");
            cell.setBorderStyle(null);
            cell.setBottomBorderStyle(BLACK_L);

            row = table.createRow(0.38f * COF_CM);
            cell = row.createCell(100, " ");
            cell.setBorderStyle(WHITE_L);

            row = table.createRow(2);
            createMyCell(16, "Руководитель", true, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
            cell.setBorderStyle(WHITE_L);

            createMyCell(40, user.getDirector(), false, HorizontalAlignment.RIGHT, VerticalAlignment.TOP);
            cell.setBorderStyle(WHITE_L);
            cell.setBottomBorderStyle(NORMAL_L);


            createMyCell(3, "");
            cell.setBorderStyle(WHITE_L);


            createMyCell(14, "Бухгалтер", true, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
            cell.setBorderStyle(WHITE_L);

            createMyCell(27, user.getAccountant(), false, HorizontalAlignment.RIGHT, VerticalAlignment.TOP);
            cell.setBorderStyle(WHITE_L);
            cell.setBottomBorderStyle(NORMAL_L);
            sub = table.draw();


            contentStream.close();
            mainDocument.addPage(myPage);
            mainDocument.save(InvoiceDir + "\\" + number + "_" + millis + "ns.pdf");

            contentStream = new PDPageContentStream(mainDocument, myPage, PDPageContentStream.AppendMode.APPEND, false);
            PDImageXObject stamp = PDImageXObject.createFromFile(user.getTitle() + "\\" + "stamp1.png", mainDocument);
            contentStream.drawImage(stamp, 170f, sub - 75, stamp.getWidth() * 0.27f, stamp.getHeight() * 0.27f);

            stamp = PDImageXObject.createFromFile(user.getTitle() + "\\" + "stamp2.png", mainDocument);
            contentStream.drawImage(stamp, 350f, sub - 22, stamp.getWidth() * 0.27f, stamp.getHeight() * 0.27f);

            contentStream.close();
            mainDocument.save(InvoiceDir + "\\" + number + "_" + millis + ".pdf");
            mainDocument.close();
            sampleFile.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createMyCell(float width, String value, boolean bold, HorizontalAlignment hAlignment, VerticalAlignment vAlignment) {

        try {
            PDFont font;
            if (bold) font = PDType0Font.load(mainDocument, new File("resources/ArialBold.ttf"));
            else font = PDType0Font.load(mainDocument, new File("resources/Arial.ttf"));

            cell = row.createCell(width, value, hAlignment, vAlignment);
            cell.setFont(font);
            cell.setFontSize(8);
            cell.setTopPadding(Float.MIN_NORMAL);
            cell.setLeftPadding(2);
            cell.setBottomPadding(Float.MIN_NORMAL);
            cell.setRightPadding(2);
            cell.setBorderStyle(NORMAL_L);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createMyCell(float width, String value, boolean bold) {
        try {
            PDFont font;
            if (bold) font = PDType0Font.load(mainDocument, new File("resources/ArialBold.ttf"));
            else font = PDType0Font.load(mainDocument, new File("resources/Arial.ttf"));
            cell = row.createCell(width, value, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
            cell.setFont(font);
            cell.setFontSize(8);
            cell.setTopPadding(Float.MIN_NORMAL);
            cell.setLeftPadding(2);
            cell.setBottomPadding(Float.MIN_NORMAL);
            cell.setRightPadding(2);
            cell.setBorderStyle(NORMAL_L);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createMyCell(float width, String value) {
        try {
            PDFont font = PDType0Font.load(mainDocument, new File("resources/Arial.ttf"));
            cell = row.createCell(width, value, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
            cell.setFont(font);
            cell.setFontSize(8);
            cell.setTopPadding(Float.MIN_NORMAL);
            cell.setLeftPadding(2);
            cell.setBottomPadding(Float.MIN_NORMAL);
            cell.setRightPadding(2);
            cell.setBorderStyle(NORMAL_L);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
