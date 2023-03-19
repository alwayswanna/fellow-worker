/*
 * Copyright (c) 3-3/25/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.oauth2persistence.db.repository;

import a.gleb.oauth2persistence.db.dao.DeletedAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeletedAccountRepository extends JpaRepository<DeletedAccount, UUID> {
}
