package khovanets.restsimulateapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import khovanets.restsimulateapp.DatabaseHandler;
import khovanets.restsimulateapp.User;
import org.springframework.web.bind.annotation.*;

@RestController
public class AppController {
    // Метод GET
    @GetMapping("/getMethod")
    public User getMethod(@RequestParam String login) throws JsonProcessingException {
        DatabaseHandler dbHandler = new DatabaseHandler();
        User user;
        // Выполняем SELECT-запрос
        user = dbHandler.selectData(login);

        // Возвращаем ответ
        return user;
    }

    // Метод POST
    @PostMapping("/postMethod")
    // Принимаем json с данными пользователя
    public String postMethod(@RequestBody User user) throws JsonProcessingException {
        DatabaseHandler dbHandler = new DatabaseHandler();
        // Выполняем INSERT-запросы
        dbHandler.insertData(user.getLogin(), user.getPassword(), user.getEmail());

        // Возвращаем ответ
        return "OK";
    }
}
