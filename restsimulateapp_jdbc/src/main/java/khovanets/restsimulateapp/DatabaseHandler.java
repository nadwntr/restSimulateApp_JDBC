package khovanets.restsimulateapp;

import java.sql.*;

public class DatabaseHandler {
    private final String url = "jdbc:postgresql://postgres:5432/userdb?sslmode=disable";
    private final String username = "postgres";
    private final String password = "postgres";

    // Метод для закрытия соединения с базой данных
    public void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Не удалось закрыть соединение с базой данных: " + e);
        }
    }

    // Метод для получения данных по логину с обычным try-catch и Statement
    public User selectData(String login) {
        User user = new User();
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement stat = connection.createStatement();
            // Создаем строку запроса
            String query = "SELECT login, passUser, to_char(dateUser, 'DD.MM.YYYY HH:MM:SS'), email FROM tableUser " +
                    "INNER JOIN tableEmail ON tableUser.login=tableEmail.fk_login WHERE tableUser.login='" + login + "';";

            // Выполняем запрос
            ResultSet result = stat.executeQuery(query);

            // Присваиваем и выводим результат
            while (result.next()) {
                user.setLogin(result.getString(1));
                System.out.println("Логин: " + result.getString(1) + "\n");
                user.setPassword(result.getString(2));
                System.out.println("Пароль: " + result.getString(2) + "\n");
                user.setDate(result.getString(3));
                System.out.println("Дата: " + result.getString(3) + "\n");
                user.setEmail(result.getString(4));
                System.out.println("Email: " + result.getString(4) + "\n");
            }
            stat.close();
            // Закрываем соединение с базой данных
            closeConnection(connection);
        } catch (SQLException e) {
            System.out.println("Не удалось выполнить SELECT-запрос: " + e);
        }
        return user;
    }

    // Метод для вставки данных с try-catch с ресурсами и PreparedStatement
    public void insertData(String login, String pass, String email) {
        // Создаем строки запросов
        String query = "INSERT INTO tableUser (login, passUser, dateUser) VALUES (?, ?, ?);";
        String queryEmail = "INSERT INTO tableEmail (email, fk_login) VALUES (?, ?);";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStat = connection.prepareStatement(query);
             PreparedStatement preparedStatEmail = connection.prepareStatement(queryEmail)) {
            // Формируем текущую дату и время
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            // Подставляем значения в запрос для вставки в таблицу tableUser
            preparedStat.setString(1, login);
            preparedStat.setString(2, pass);
            preparedStat.setTimestamp(3, timestamp);
            preparedStat.executeUpdate();

            // Подставляем значения в запрос для вставки в таблицу tableEmail
            preparedStatEmail.setString(1, email);
            preparedStatEmail.setString(2, login);
            preparedStatEmail.executeUpdate();

            System.out.println("INSERT-запросы выполнены.");
        } catch (SQLException e) {
            System.out.println("Не удалось выполнить INSERT-запросы: " + e);
        }
    }
}
