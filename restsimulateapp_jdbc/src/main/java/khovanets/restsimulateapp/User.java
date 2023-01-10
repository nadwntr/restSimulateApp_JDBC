package khovanets.restsimulateapp;
import lombok.AllArgsConstructor;
import lombok.Getter;
@Getter
@AllArgsConstructor
public class User {
    private String login;
    private String password;
    private String email;
    private String date;
}
