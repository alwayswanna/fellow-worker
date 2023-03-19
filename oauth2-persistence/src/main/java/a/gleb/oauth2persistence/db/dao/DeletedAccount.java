/*
 * Copyright (c) 3-3/25/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.oauth2persistence.db.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "deleted_accounts")
public class DeletedAccount {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
}
