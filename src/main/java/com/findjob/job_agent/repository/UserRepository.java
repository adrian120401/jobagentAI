package com.findjob.job_agent.repository;

import com.findjob.job_agent.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
