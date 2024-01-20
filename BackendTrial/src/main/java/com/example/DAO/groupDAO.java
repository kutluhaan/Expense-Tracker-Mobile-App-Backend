package com.example.DAO;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.templates.Group;

public interface groupDAO extends  MongoRepository<Group, String> {
	public Group findByCreator(String creator);
}
