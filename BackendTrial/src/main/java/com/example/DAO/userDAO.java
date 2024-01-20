package com.example.DAO;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.templates.User;

public interface userDAO extends  MongoRepository<User, String> {
	public User findByName(String name);
	public User findByUserName(String userName);
	public User findByID(String iD);
}
