package com.e.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.IOException;


@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class LegalCard {
    @Column(name = "title")
    private String title;
    @Column(name = "address")
    private String address;
    @Column(name = "inn")
    private String inn;
    @Column(name = "kpp")
    private String kpp;
    @Column(name = "bik")
    private String bik;
    @Column(name = "bank")
    private String bank;
    @Column(name = "address_bank")
    private String addressBank;
    @Column(name = "rs")
    private String rs;
    @Column(name = "ks")
    private String ks;
    @Column(name = "director")
    private String director;
    @Column(name = "accountant")
    private String accountant;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Transient
    private String textFileForm;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Transient
    private String textFile;

    public void searchData(String path) throws IOException {

        switch (path.substring(path.lastIndexOf('.')).toLowerCase()) {
            case ".docx" -> {
                XWPFWordExtractor extrDocx = new XWPFWordExtractor(new XWPFDocument(new FileInputStream(path)));
                textFile = extrDocx.getText();
                extrDocx.close();
            }
            case ".xlsx" -> {
                XSSFExcelExtractor extrXlsx = new XSSFExcelExtractor(new XSSFWorkbook(path));
                textFile = extrXlsx.getText();
                extrXlsx.close();
            }
            case ".xls" -> {
                ExcelExtractor extrXls = new ExcelExtractor(new HSSFWorkbook(new FileInputStream(path)));
                textFile = extrXls.getText();
                extrXls.close();
            }
            case ".doc" -> {
                WordExtractor extrDoc = new WordExtractor(new FileInputStream(path));
                textFile = extrDoc.getText();
                extrDoc.close();
            }
            case ".pdf" -> {
                PDDocument extrPdf = PDDocument.load(new FileInputStream(path));
                textFile = new PDFTextStripper().getText(extrPdf);
                extrPdf.close();
            }
        }

        textFileForm = textFile.trim().replaceAll("\\u00a0", "").replaceAll("\s", "").toLowerCase();

        title = writeData(LegalEntities.equalsForEach(textFile), textFile);

        ks = writeData("30101810", textFileForm);

        address = writeData("дрес", textFile);

        bik = writeData("бик", textFileForm);

        if (title.contains(LegalEntities.IP.getName())) {
            inn = writeData("инн", textFileForm);
            rs = writeData("40802810", textFileForm);
            kpp = " ";
            director = title.substring(3);
        } else {
            director = writeData("ктор", textFileForm);

            if (textFileForm.contains("инн/кпп")) {
                writeData("инн/кпп", textFileForm);
            } else if (textFileForm.contains("инн\\кпп")) {
                writeData("инн\\кпп", textFileForm);
            }
            if (textFileForm.contains("инн")) {
                inn = writeData("инн", textFileForm);
                kpp = writeData("кпп", textFileForm);
            }
            rs = writeData("40702810", textFileForm);
        }
        try {
            Document htmlBIK = Jsoup.connect("https://bik-info.ru/bik_" + bik + ".html").get();

            Elements elb = htmlBIK.select("body > div.container > ul:nth-child(7) > li:nth-child(3) > b");
            bank = elb.text();

            elb = htmlBIK.select("body > div.container > ul:nth-child(7) > li:nth-child(4) > b");
            addressBank = "г." + elb.text();

        } catch (IOException e) {
            System.out.println("Неудалось загрузить данные о банке");
        }
    }

    private String writeData(String parameter, String text) {

        int x = text.indexOf(parameter);
        if (x == -1) return null;

        int y = text.indexOf("\n", x);
        if (y == -1) y = text.length();

        if (parameter.equals("инн/кпп") || parameter.equals("инн\\кпп")) {

            inn = text.substring(x + 7, x + 18).trim();
            kpp = text.substring(x + 19, y).trim();
            textFileForm = text.replaceAll(parameter, "");

        } else if (parameter.equals("дрес")) {

            parameter = text.substring(x + 5, y).replaceAll(":", "").trim();

        } else if (parameter.equals(LegalEntities.equalsForEach(parameter))) {

            parameter = LegalEntities.getSubName(parameter) + text.substring(x, y).split("[,|)]")[0].replaceAll(parameter, "").split(" ")[0];

        } else {

            int i = 20;
            if (parameter.equals("инн")) i = 12;
            else if (parameter.equals("кпп") || parameter.equals("бик")) i = 9;
            parameter = String.format("%9." + i + "s", text.substring(x, y).replaceAll("[a-zA-Zа-яА-Я-\\pP]", "").trim());

        }
        return parameter;
    }

    public String toString() {
        return """
                1. Название: %s;
                2. Юридический адрес: %s;
                3. ИНН: %s;
                4. КПП: %s;
                5. БИК: %s;
                6. Банк: %s;
                7. Адрес банка: %s;
                8. р/с: %s;
                9. к/с: %s;
                10. Директор: %s;
                11. Бухгалтер: %s;
                """.formatted(title, address, inn, kpp, bik, bank, addressBank, rs, ks, director, accountant);
    }
}
