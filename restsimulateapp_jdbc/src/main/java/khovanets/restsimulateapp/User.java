package khovanets.restsimulateapp;
import lombok.Data;

@Data
public class User {
    private String login;
    private String password;
    private String date;
    private String email;
}
