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
    public User selectData(String login) throws SQLException {

        try {
            User user = new User();
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement stat = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            // Создаем строку запроса
            String query = "SELECT login, passUser, email, dateUser FROM tableUser " +
                    "INNER JOIN tableEmail ON tableUser.login=tableEmail.fk_login WHERE tableUser.login='" + login + "';";

            // Выполняем запрос
            ResultSet result = stat.executeQuery(query);

            // Проверяем, есть ли пользователь с указанным логином
            try {
                result.last();
                int rowCount = result.getRow();
                result.beforeFirst();
                if (rowCount == 0) {
                    throw new SQLException("Отсутствует пользователь с указанным логином.");
                }
                // Ловим исключение
            } catch (SQLException e) {
                stat.close();
                // Закрываем соединение с базой данных
                closeConnection(connection);
                System.out.println("Не удалось выполнить SELECT-запрос: " + e);
                throw new SQLException(e);
            }

            // Присваиваем и выводим в логи результат
            while (result.next()) {
                user.setLogin(result.getString(1));
                System.out.println("Логин: " + result.getString(1) + "\n");
                user.setPassword(result.getString(2));
                System.out.println("Пароль: " + result.getString(2) + "\n");
                user.setEmail(result.getString(3));
                System.out.println("Email: " + result.getString(3) + "\n");
                user.setDate(result.getString(4));
                System.out.println("Дата: " + result.getString(4) + "\n");
            }
            stat.close();
            // Закрываем соединение с базой данных
            closeConnection(connection);

            // Возвращаем данные пользователя по логину, если не было исключения
            return user;
            // Ловим исключение
        } catch (SQLException e) {
            System.out.println("Не удалось выполнить SELECT-запрос: " + e);
            throw new SQLException(e);
        }
    }

    // Метод для вставки данных с try-catch с ресурсами и PreparedStatement
    public User insertData(String login, String pass, String email) throws SQLException {
        // Если в теле нет какого-либо параметра (логин, пароль, email)
        try {
            if (login == null || pass == null || email == null) {
                throw new SQLException("Отсутствуют все необходимые параметры для запроса.");
            }
            // Ловим исключение
        } catch (SQLException e) {
            System.out.println("Не удалось выполнить INSERT-запросы: " + e);
            throw new SQLException(e);
        }

        // Создаем строки запросов
        String query = "INSERT INTO tableUser (login, passUser, dateUser) VALUES (?, ?, ?);";
        String queryEmail = "INSERT INTO tableEmail (email, fk_login) VALUES (?, ?);";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStat = connection.prepareStatement(query);
             PreparedStatement preparedStatEmail = connection.prepareStatement(queryEmail)) {

            User user = new User();

            // Формируем текущую дату и время
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            user.setLogin(login);
            user.setPassword(pass);
            user.setEmail(email);
            user.setDate(timestamp.toString());

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
            return user;
            // Ловим исключения
        } catch (SQLException e) {
            System.out.println("Не удалось выполнить INSERT-запросы: " + e);
            throw new SQLException(e);
        }
    }
}
