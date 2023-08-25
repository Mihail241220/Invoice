package com.e;

import com.e.entity.Customer;
import com.e.entity.User;
import com.e.entity.form.Arrival;
import com.e.entity.form.Invoice;
import com.e.entity.form.Receipt;
import com.e.entity.form.details.ArrivalDetails;
import com.e.entity.form.details.InvoiceDetails;
import com.e.entity.form.details.Position;
import com.e.entity.form.details.Warehouse;
import com.e.service.*;
import jakarta.persistence.NoResultException;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;


public class Menu {
    private static final Scanner sc = new Scanner(System.in);
    private static boolean plug = true;
    private static User user;

    public static void start() {


        String login;
        String password;

        if (binaryChoice("Введите 1 для входа или 0 для регистрации")) {
            // вход
            do {
                System.out.print("Введите логи: ");
                login = sc.nextLine();
                System.out.print("Введите пароль: ");
                password = sc.nextLine();
                try {
                    user = new ServiceUser().getByLogin(login);
                } catch (SQLException | NoResultException s) {
                    System.err.println("Пользователь не найден повторите попытку.\n");
                    continue;
                }
                if (!user.getPassword().equals(password)) {
                    System.out.println("Не верный логин или пароль");

                } else {
                    plug = false;
                }
            } while (plug);
            //регистрация
        } else {
            user = new User();
            System.out.print("Введите логин   ");
            login = sc.nextLine();
            System.out.print("\n Введите пароль   ");
            password = sc.nextLine();
            user.setLogin(login);
            user.setPassword(password);
            if (binaryChoice("Заполнить данные компании в ручную 1 (да)/0 (нет)? ")) {
                // внесение данных вручную
                do {
                    System.out.println("Введите номер строки которую хотите заполнить.\n" +
                            user + "\n\n 0. продолжить");
                    if (sc.hasNextInt()) {
                        int i = sc.nextInt();
                        plug = enterManually(i);
                    } else {
                        System.out.println("Error");
                    }
                    sc.nextLine();
                } while (plug);
            } else {
                //автоматизированное внесение данных
                while (true) {
                    String str;
                    System.out.println("Введите путь к файлу карты предприятия ");
                    try {
                        str = sc.next();
                        user.searchData(str);
                        break;
                    } catch (Exception e) {
                        System.out.println("Error: Файл не найден");
                        sc.nextLine();
                    }
                }
                // проверка и корректировка данных
                do {
                    System.out.println("Проверьте правильность информации.\n" +
                            " Введите № строки, которую хотите исправить " + user + "\n\n 0. Продолжить");
                    if (sc.hasNextInt()) {
                        plug = enterManually(sc.nextInt());
                    } else {
                        System.out.println("Error");
                    }
                } while (plug);
            }
            // добавление печатей
            System.out.println("\n Добавление печати с подписью и подписи");
            do {
                System.out.print("Введите адрес печати с подписью (.png):");
                String stamp1 = sc.nextLine();
                System.out.print("\nВведите адрес подписи (.png):");
                String stamp2 = sc.nextLine();
                try {
                    user.addStamps(stamp1, stamp2);
                    plug = false;
                } catch (IOException e) {

                    System.err.println("\n \n Изображения не сохранены. Проверьте правильность адресов.");
                    plug = true;
                    sc.nextLine();
                }
            } while (plug);
            System.out.println("введите нижний текст для счета");// STOPSHIP: 14.02.2023 todo
            user.setBottomText(sc.nextLine());
            try {
                new ServiceUser().add(user);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        plug = true;
        do {
            //основное навигационное меню
            System.out.println("""
                    1. Добавить контрагента;
                    2. Выставить счет;
                    3. Просмотреть склад;
                    4. Просмотреть всех контрагентов;
                    5. Посмотреть все счета;
                    6. Загрузить отгрузочную накладную;
                    7. Загрузить приходную накладную;
                    9. Посмотреть список на приход;
                    0. Выход
                    """);
            if (sc.hasNextInt()) {
                switch (sc.nextInt()) {
                    case 0 -> plug = false;

                    case 1 -> {
                        enterCustomer();
                        plug = true;
                    }
                    case 2 -> {
                        if (user.getCustomer() != null
                                && binaryChoice("Выставить счет для" + user.getCustomer().getTitle() + "1 (да)/0 (нет)")) {
                            enterInvoice();
                        } else {
                            if (binaryChoice("Выставить счет для существующего контрагента 1 (да)/0 (нет)")) {
                                try {
                                    List<Customer> arrayCustomer = new ServiceCustomer().getAll(user);
                                    for (int i = 0; i < arrayCustomer.size(); i++) {
                                        System.out.println("№" + i + "\n\n" + arrayCustomer.get(i));
                                    }
                                    System.out.print("Введите номер контрагента \n");
                                    if (sc.hasNextInt()) {
                                        user.setCustomer(arrayCustomer
                                                .get(sc.nextInt()));
                                        user.getCustomer().setUser(user);
                                    } else {
                                        System.out.println("Error 'Номер': не верные данные, начните заново");
                                    }
                                } catch (SQLException e) {
                                    System.err.println(e + "   \n\n not found Customer");
                                }
                            } else {
                                enterCustomer();
                            }
                            enterInvoice();
                        }
                        plug = true;
                    }
                    case 3 -> {
                        try {
                            List<Warehouse> list = new ServiceWarehouse().getAll(user);
                            for (Warehouse i : list) {
                                System.out.print(i + "\n");
                            }
                        } catch (SQLException e) {
                            System.err.println("Ошибка БД\n" + e);
                        }
                    }
                    case 4 -> {
                        try {
                            List<Customer> customers = new ServiceCustomer().getAll(user);
                            for (Customer i : customers) {
                                System.out.println("---------\n" + i + "\n");
                            }
                        } catch (SQLException e) {
                            System.err.println("Ошибка БД\n" + e);
                        }
                    }
                    case 5 -> {
                        try {
                            List<Invoice> listI = new ServiceInvoice().getAll(user);
                            for (Invoice i : listI) {
                                System.out.println("---------\n" + i.SortToString() + "\n");
                            }
                        } catch (SQLException e) {
                            System.err.println("Ошибка БД\n" + e);
                        }
                    }
                    case 6 -> {
                        sc.nextLine();
                        Invoice invoice = new Invoice();
                        Receipt receipt;
                        while (true) {
                            System.out.print("Введите номер счета для которого загружается накладная:");
                            if (sc.hasNextInt()) {
                                try {
                                    List<Invoice> listI = new ServiceInvoice().getByNumber(user, sc.nextInt());
                                    sc.nextLine();
                                    if (listI.size() > 0) {
                                        System.out.print("Есть несколько счетов под данным номером:");
                                        int j = 0;
                                        for (Invoice i : listI) {
                                            System.out.println("id" + j);
                                            j++;
                                            System.out.println("---------\n" + i + "\n");
                                        }
                                        System.out.print("\nВведите id нужного счета:");
                                        if (sc.hasNextInt()) {
                                            invoice = listI.get(sc.nextInt());
                                        } else {
                                            System.err.println("Error: неверные данные ввода");
                                        }
                                    } else {
                                        try {
                                            invoice = listI.get(0);
                                        } catch (IndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }
                                } catch (SQLException e) {
                                    System.err.println("Error BD");
                                }

                            }

                        }
                        receipt = new Receipt(invoice);
                        if (binaryChoice("Внести изменения в отгружаемые позиции? 1 (да)/0 (нет)")) {
                            while (true) {
                                for (int i = 1; i <= receipt.getPosition().size(); i++) {
                                    System.out.println("№" + i + receipt.getPosition().get(i));
                                }
                                System.out.print("Введите номер позиции для ее удаления:");
                                if (sc.hasNextInt()) {
                                    receipt.getPosition().remove(sc.nextInt());
                                    sc.nextLine();
                                } else {
                                    System.err.println("Неверный ввод");
                                    continue;
                                }
                                if (binaryChoice("Удалить еще 1 (да)/0 (нет)")) {
                                    sc.nextLine();
                                } else {
                                    break;
                                }
                            }
                        }
                        try {
                            new ServiceReceipt().add(receipt);
                        } catch (SQLException e) {
                            System.err.println("ERROR BD");
                        }
                    }
                    case 7 -> {
                        sc.nextLine();
                        Arrival arrival = new Arrival();
                        arrival.setUser(user);

                        System.out.print("Введите поставщика: ");
                        arrival.setProvider(sc.nextLine());
                        System.out.print(" \nВведите номер накладной : ");
                        arrival.setNumber(sc.nextLine());

                        System.out.print("\nДата: ");
                        while (true) {
                            System.out.print("\nВведите день: ");
                            int[] x = new int[3];
                            if (sc.hasNextInt()) {
                                x[0] = sc.nextInt();
                                sc.nextLine();
                            } else {
                                continue;
                            }
                            System.out.print("\nВведите месяц: ");
                            if (sc.hasNextInt()) {
                                x[1] = sc.nextInt();
                                sc.nextLine();
                            } else {
                                continue;
                            }
                            System.out.print("\nВведите год: ");
                            if (sc.hasNextInt()) {
                                x[2] = sc.nextInt();
                            } else {
                                continue;
                            }
                            arrival.setDate(LocalDate.of(x[2], x[1], x[0]));
                            break;
                        }
                        arrival.setPosition(enterPosition(new ArrivalDetails()));
                        try {
                            new ServiceArrival().add(arrival);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    case 9 -> {
                        try {
                            List<Warehouse> list = new ServiceWarehouse().getArrivalList(user);//TODO
                            for (Warehouse i : list) {
                                System.out.print(i + "\n");
                            }
                        } catch (SQLException e) {
                            System.err.println("Ошибка БД\n" + e);
                        }
                    }
                }
            } else {
                System.err.println("Ошибка: неверная команда");
                sc.nextLine();
            }
        } while (plug);
        System.out.println("конец");
    }

    /**
     * метод binaryChoice возвращает true при 1 введенной в терминал, false при 0
     **/
    static private boolean binaryChoice(String message) {
        int x = -1;
        do {
            System.out.print("\n" + message + " ");
            if (sc.hasNextInt()) {
                x = sc.nextInt();
            } else {
                System.out.println("Error");
            }
            if (x != 1 && x != 0) {
                System.out.println("Не известная команда");
            }
            sc.nextLine();
        } while (x != 1 && x != 0);
        return x == 1;
    }

    /**
     * enterManually заполнение Customer , возвращает false при 0
     */
    private static boolean enterManually(Customer customer, int point) {
        System.out.print("Введите значение:  ");
        sc.nextLine();
        switch (point) {
            case 0 -> {
                return false;
            }
            case 1 -> customer.setTitle(sc.nextLine());
            case 2 -> customer.setAddress(sc.nextLine());
            case 3 -> customer.setInn(sc.nextLine());
            case 4 -> customer.setKpp(sc.nextLine());
            case 5 -> customer.setBik(sc.nextLine());
            case 6 -> customer.setBank(sc.nextLine());
            case 7 -> customer.setAddressBank(sc.nextLine());
            case 8 -> customer.setRs(sc.nextLine());
            case 9 -> customer.setKs(sc.nextLine());
            case 10 -> customer.setDirector(sc.nextLine());
            case 11 -> customer.setAccountant(sc.nextLine());
            case 12 -> customer.setActual_region(sc.nextLine());
            case 13 -> customer.setActual_city(sc.nextLine());
            case 14 -> customer.setTelephone(sc.nextLine());
            case 15 -> customer.setEmail(sc.nextLine());
            case 16 -> customer.setComment(sc.nextLine());
        }
        return true;
    }

    /**
     * enterManually заполнение User , возвращает false при 0
     */
    private static boolean enterManually(int point) {
        if (point == 0) {
            return false;
        }

        Scanner sc1 = new Scanner(System.in);
        System.out.print("Введите значение:  ");
        String srt = sc1.nextLine();
        switch (point) {
            case 1 -> user.setTitle(srt);
            case 2 -> user.setAddress(srt);
            case 3 -> user.setInn(srt);
            case 4 -> user.setKpp(srt);
            case 5 -> user.setBik(srt);
            case 6 -> user.setBank(srt);
            case 7 -> user.setAddressBank(srt);
            case 8 -> user.setRs(srt);
            case 9 -> user.setKs(srt);
            case 10 -> user.setDirector(srt);
            case 11 -> user.setAccountant(srt);
            case 12 -> user.setLogin(srt);
            case 13 -> user.setPassword(srt);
        }
        return true;
    }

    private static <E extends Position> List<E> enterPosition(E position) {
        List<E> positions = new ArrayList<>();
        sc.nextLine();
        do {
            System.out.print("\n Введите название позиции ");
            position.setPosition(sc.nextLine());

            System.out.print("\n Введите количество ");
            if (sc.hasNextInt()) {
                position.setQuantity(sc.nextInt());
            } else {
                System.out.println("\nError 'Количество': не верные данные, начните заново");
                continue;
            }
            System.out.print("\n Введите цену ");
            if (sc.hasNextDouble()) {
                position.setPrice(sc.nextDouble());
            } else {
                System.out.println("\nError 'Цена': не верные данные, начните заново");
                continue;
            }
            positions.add(position);
            if (!binaryChoice("Добавить еще позицию 1 (да)/0 (нет)")) {
                break;
            }
        } while (true);
        return positions;
    }

    private static void enterInvoice() {

        Calendar calendar = new GregorianCalendar();//:TODO:заменить на LocalDate
        Invoice invoice;
        sc.nextLine();
        if (binaryChoice("Номер счета " + (user.getLastInvoice() + 1) +
                " Дата " + calendar.get(Calendar.DATE) + "." + calendar.get(Calendar.MONTH) + "." + calendar.get(Calendar.YEAR)
                + ". \n Изменить номер счета и дату 1(да)/0(нет)?")) {
            int day;
            int month;
            int year;
            int number;
            do {
                System.out.print("Введите день ");
                if (sc.hasNextInt()) {
                    day = sc.nextInt();
                } else {
                    System.out.println("Error: не верные данные");
                    continue;
                }
                System.out.print("Введите месяц ");
                if (sc.hasNextInt()) {
                    month = sc.nextInt();
                } else {
                    System.out.println("Error: не верные данные");
                    continue;
                }
                System.out.print("Введите год ");
                if (sc.hasNextInt()) {
                    year = sc.nextInt();
                } else {
                    System.out.println("Error: не верные данные");
                    continue;
                }
                System.out.print("Введите номер счета ");
                if (sc.hasNextInt()) {
                    number = sc.nextInt();
                } else {
                    System.out.println("Error: не верные данные");
                    continue;
                }
                List<InvoiceDetails> position = enterPosition(new InvoiceDetails());
                calendar = new Calendar.Builder()
                        .setDate(year, month, day)
                        .build();

                invoice = user.createInvoice(position, calendar, number);
                break;
            } while (true);
        } else {
            List<InvoiceDetails> position = enterPosition(new InvoiceDetails());
            invoice = user.createInvoice(position);
        }
        try {
            new ServiceUser().update(user);
            new ServiceInvoice().add(invoice);
        } catch (SQLException e) {
            System.out.println("Счет не сохранен в базу данных");
        }
    }

    private static void enterCustomer() {
        Customer customer = new Customer(user);
        System.out.println("Добавление контрагента");
        if (binaryChoice("Заполнить данные контрагента в ручную 1 (да)/0 (нет)? ")) {
            do {
                System.out.println("Введите номер строки которую хотите заполнить.\n" +
                        customer + "\n\n 0. Выход");
                if (sc.hasNextInt()) {
                    plug = enterManually(customer, sc.nextInt());
                }
            } while (plug);
        } else {
            while (true) {
                String str;
                System.out.println("Введите путь к файлу карты предприятия ");
                try {
                    str = sc.next();
                    customer.searchData(str);
                    break;
                } catch (Exception e) {
                    System.out.println("Error: Файл не найден");
                    sc.nextLine();
                }
            }
            do {
                System.out.println("Проверьте правильность информации.\n" +
                        " Введите № строки, которую хотите исправить " + customer + "\n\n 0. Выход");
                if (sc.hasNextInt()) {
                    plug = enterManually(customer, sc.nextInt());
                } else {
                    System.out.println("Error");
                }
            } while (plug);
        }
        try {
            new ServiceCustomer().add(customer);
            user.setCustomer(customer);
        } catch (SQLException e) {
            System.err.println("Контр агент не сохранен");
        }
    }
}

