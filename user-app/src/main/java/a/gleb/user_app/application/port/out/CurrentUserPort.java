package a.gleb.user_app.application.port.out;

public interface CurrentUserPort {

    /**
     * @return the current account's login, or an empty string if unauthenticated.
     */
    String getCurrentLogin();

    /**
     * @return the current account's login.
     * @throws org.springframework.web.server.ResponseStatusException 401 if missing/unauthenticated.
     */
    String requireCurrentLogin();
}
