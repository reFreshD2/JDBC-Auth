package com.company;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.*;

import static java.util.regex.Pattern.*;

public class Main {
    public static final String URL = "jdbc:mysql://localhost:3306/hotel?serverTimezone=UTC";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "**********";

    private static boolean isCorrect(String str) {
        boolean correct = false;
        Pattern pattern = compile("[A-Za-z0-9]{6,18}");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find() && str.length() <= 18) {
            correct = true;
        }
        return correct;
    }

    private static ArrayList<String> Auth() {
        Scanner in = new Scanner(System.in);
        System.out.println("\tАвторизация");
        System.out.print("Логин: ");
        String login = in.nextLine();
        while (!isCorrect(login)) {
            System.out.println("Логин должен иметь длину от 6 до 18 символов. Символы - прописные и заглавные буквы английского алфавита и цифры от 0 до 9");
            System.out.print("Введите логин заново: ");
            login = in.nextLine();
        }
        System.out.print("Пароль: ");
        String pass = in.nextLine();
        while (!isCorrect(pass)) {
            System.out.println("Пароль должен иметь длину от 6 до 18 символов. Символы - прописные и заглавные буквы английского алфавита и цифры от 0 до 9");
            System.out.print("Введите пароль заново: ");
            pass = in.nextLine();
        }
        ArrayList<String> PD = new ArrayList<>();
        PD.add(login);
        PD.add(pass);
        return PD;
    }

    private static int sizeOfRS (ResultSet set) throws SQLException {
        int size = 0;
        while (set.next()) {
            size++;
        }
        set.first();
        return size;
    }

    public static void main(String[] args) {
        System.out.println("Подключение к системе...");
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            System.out.println("Подключение установлено.");
            ArrayList<String> PersonalData = new ArrayList<>();
            boolean inSystem = false;
            while (!inSystem) {
                PersonalData = Auth();
                Statement statement = conn.createStatement();
                String findAdministrator = "SELECT * FROM administrator WHERE LOGIN=\"" + PersonalData.get(0) + "\" and PASS=\"" + PersonalData.get(1) + "\"";
                String findManager = "SELECT * FROM placement_manager WHERE LOGIN=\"" + PersonalData.get(0) + "\" and PASS=\"" + PersonalData.get(1) + "\"";
                String findVisitor = "SELECT * FROM visitor WHERE LOGIN=\"" + PersonalData.get(0) + "\" and PASS=\"" + PersonalData.get(1) + "\"";
                ResultSet rs = statement.executeQuery(findAdministrator);
                if (sizeOfRS(rs) != 0) {
                    System.out.println("Добро пожаловать в систему! Вы вошли как администратор.");
                    inSystem = true;
                } else {
                    rs = statement.executeQuery(findManager);
                    if (sizeOfRS(rs) != 0) {
                        System.out.println(rs.getString("FIO") + ", добро пожаловать в систему! Вы вошли как менеджер-размещения.");
                        inSystem = true;
                    } else {
                        rs = statement.executeQuery(findVisitor);
                        if (sizeOfRS(rs) != 0) {
                            System.out.println(rs.getString("FIO") + ", добро пожаловать в систему! Вы вошли как посетитель.");
                            inSystem = true;
                        } else {
                            System.out.println("Неверный логин или пароль. Повторите авторизацию!");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("Ошибка подключения.");
            System.out.println(ex);
        }
    }
}
