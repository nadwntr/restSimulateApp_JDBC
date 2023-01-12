package khovanets.restsimulateapp;

import java.sql.*;
public class DatabaseHandler {
    private final String url = "jdbc:postgresql://postgres:5432/userdb?sslmode=disable";
    private final String username = "postgres";
    private final String password = "postgres";

    // Метод для закрытия соединения с базой данных
    public void closeConnection(Connection connection) throws SQLException {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Не удалось закрыть соединение с базой данных: " + e);
            throw new SQLException(e);
        }
    }

    // Метод для получения данных по логину с обычным try-catch и Statement
    public User selectData(String login) throws SQLException, MyStatementException {
        // Переменная для подключения к БД
        Connection connection = null;
        // Переменная для отправки запроса
        Statement stat = null;
        try {
            // Подключаемся к БД
            connection = DriverManager.getConnection(url, username, password);
            // Создаем объект Statement для отправки SQL-запроса
            stat = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            User user;

            // Создаем строку запроса
            String query = "SELECT login, passUser, email, dateUser FROM tableUser " +
                    "INNER JOIN tableEmail ON tableUser.login=tableEmail.fk_login WHERE tableUser.login='" +
                    login + "';";
            // Выполняем запрос
            ResultSet result = stat.executeQuery(query);

            // Проверяем, есть ли пользователь с указанным логином
            result.last();
            int rowCount = result.getRow();
            result.beforeFirst();
            if (rowCount == 0) {
                throw new MyStatementException("Отсутствует пользователь с указанным логином.");
            }

            // Присваиваем результат запроса
            result.next();
            user = new User(result.getString(1), result.getString(2),
                    result.getString(3), result.getString(4));
            // Выводим информацию о выполнении запроса
            System.out.println("SELECT-запрос выполнен.");

            // Возвращаем данные пользователя по логину, если не было исключения
            return user;
            // Ловим исключения
        } catch (MyStatementException e) { // Если исключение связано с отсутствием указанного логина
            System.out.println("Не удалось выполнить SELECT-запрос: " + e);
            throw new MyStatementException(e);
        } catch (SQLException e) { // Если исключение связано с другими SQL-ошибками
            System.out.println("Не удалось выполнить SELECT-запрос: " + e);
            throw new SQLException(e);
        } finally {
            // Закрываем Statement
            stat.close();
            // Закрываем соединение с базой данных
            closeConnection(connection);
        }
    }

    // Метод для вставки данных с try-catch с ресурсами и PreparedStatement
    public User insertData(String login, String pass, String email) throws SQLException, MyStatementException {
        // Создаем строки запросов
        String query = "INSERT INTO tableUser (login, passUser, dateUser) VALUES (?, ?, ?);";
        String queryEmail = "INSERT INTO tableEmail (email, fk_login) VALUES (?, ?);";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStat = connection.prepareStatement(query);
             PreparedStatement preparedStatEmail = connection.prepareStatement(queryEmail)) {

            // Проверяем, что в теле есть все параметры (логин, пароль, email)
            if (login == null || pass == null || email == null) {
                throw new MyStatementException("Отсутствуют все необходимые параметры для запроса.");
            }

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

            // Создаем пользователя с указанными параметрами + датой вставки в БД
            User user = new User(login, pass, email, timestamp.toString());
            // Выводим информацию о выполнении запросов
            System.out.println("INSERT-запросы выполнены.");
            return user;
            // Ловим исключения
        } catch (MyStatementException e) { // Если в теле нет какого-либо параметра (логин, пароль, email)
            System.out.println("Не удалось выполнить SELECT-запрос: " + e);
            throw new MyStatementException(e);
        } catch (SQLException e) { // Если исключение связано с другими SQL-ошибками (например, дублирование данных)
            System.out.println("Не удалось выполнить INSERT-запросы: " + e);
            throw new SQLException(e);
        }
    }
}
