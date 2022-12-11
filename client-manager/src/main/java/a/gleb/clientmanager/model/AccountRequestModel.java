package a.gleb.clientmanager.model;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestModel {

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
    private AccountType accountType;
}
