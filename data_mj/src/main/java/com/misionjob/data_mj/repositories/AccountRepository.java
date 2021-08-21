package com.misionjob.data_mj.repositories;

import com.misionjob.data_mj.models.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountRepository extends MongoRepository <Account, String> {
}
