package a.gleb.user_app.application.port.out;

public interface PasswordHasherPort {

    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
