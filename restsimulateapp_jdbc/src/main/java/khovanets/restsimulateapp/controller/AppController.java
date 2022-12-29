package khovanets.restsimulateapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import khovanets.restsimulateapp.DatabaseHandler;
import khovanets.restsimulateapp.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
public class AppController {
    // Метод GET
    @GetMapping("/getMethod/{login}")
    public ResponseEntity getMethod(@PathVariable("login") String login) throws JsonProcessingException, SQLException {
        DatabaseHandler dbHandler = new DatabaseHandler();
        User resUser;
        try {
            // Выполняем SELECT-запрос
            resUser = dbHandler.selectData(login);
        } // Ловим исключение, если оно было в методе SELECT (например, отсутствие пользователя с указанным логином)
        catch (SQLException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // Статус - 400
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
    public ResponseEntity postMethod(@RequestBody User user) throws JsonProcessingException, SQLException {
        DatabaseHandler dbHandler = new DatabaseHandler();
        User resUser = new User();
        try {
            // Выполняем INSERT-запросы
            resUser = dbHandler.insertData(user.getLogin(), user.getPassword(), user.getEmail());
        } // Ловим исключение, если оно было в методе INSERT
        catch (SQLException e) {
            // Если исключение связано с отсутствием данных в Body
            if (e.getMessage().contains("Отсутствуют все необходимые параметры для запроса.")) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST) // Статус - 400
                        .body("Не удалось выполнить INSERT-запросы: " + e); // В теле сообщение об ошибке
            } else {
                // Если исключение связано с другими ошибками (например, дублирование данных)
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR) // Статус - 500
                        .body("Не удалось выполнить INSERT-запросы: " + e); // В теле сообщение об ошибке
            }
        }
        // Возвращаем ответ в случае отсутствия ошибок
        return ResponseEntity
                .status(HttpStatus.OK) // Статус - 200
                .body(resUser); // В теле json с данными пользователя (логин, пароль, email, дата добавления в БД)
    }
}
