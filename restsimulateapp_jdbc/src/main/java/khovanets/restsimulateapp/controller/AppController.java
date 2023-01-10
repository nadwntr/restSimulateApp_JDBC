package khovanets.restsimulateapp.controller;

import khovanets.restsimulateapp.DatabaseHandler;
import khovanets.restsimulateapp.MyStatementException;
import khovanets.restsimulateapp.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
public class AppController {
    // Метод GET
    @GetMapping("/getMethod/{login}")
    public ResponseEntity getMethod(@PathVariable("login") String login) {
        DatabaseHandler dbHandler = new DatabaseHandler();
        User resUser;
        try {
            // Выполняем SELECT-запрос
            resUser = dbHandler.selectData(login);
        } // Ловим исключения, если они были в методе SELECT
        catch (MyStatementException e) { // Если исключение связано с отсутствием указанного логина
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // Статус - 500
                    .body("Не удалось выполнить SELECT-запрос: " + e); // В теле сообщение об ошибке
        } catch (SQLException e) { // Если исключение связано с другими SQL-ошибками
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // Статус - 500
                    .body("Не удалось выполнить SELECT-запрос: " + e); // В теле сообщение об ошибке
        }
        // Возвращаем ответ в случае отсутствия ошибок
        return ResponseEntity
                .status(HttpStatus.OK) // Статус - 200
                .body(resUser); // В теле json с данными пользователя (логин, пароль, email, дата добавления в БД)
    }

    // Метод POST
    @PostMapping("/postMethod")
    // Принимаем json с данными пользователя
    public ResponseEntity postMethod(@RequestBody User user) {
        DatabaseHandler dbHandler = new DatabaseHandler();
        User resUser;
        try {
            // Выполняем INSERT-запросы
            resUser = dbHandler.insertData(user.getLogin(), user.getPassword(), user.getEmail());
        } // Ловим исключения, если они были в методе INSERT
        catch (MyStatementException e) { // Если исключение связано с отсутствием данных в Body
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // Статус - 400
                    .body("Не удалось выполнить SELECT-запрос: " + e); // В теле сообщение об ошибке
        } catch (SQLException e) { // Если исключение связано с другими SQL-ошибками (например, дублирование данных)
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // Статус - 500
                    .body("Не удалось выполнить INSERT-запросы: " + e); // В теле сообщение об ошибке
        }
        // Возвращаем ответ в случае отсутствия ошибок
        return ResponseEntity
                .status(HttpStatus.OK) // Статус - 200
                .body(resUser); // В теле json с данными пользователя (логин, пароль, email, дата добавления в БД)
    }
}
