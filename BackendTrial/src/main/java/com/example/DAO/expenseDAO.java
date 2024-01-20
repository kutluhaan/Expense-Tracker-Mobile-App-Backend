package com.example.DAO;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.templates.Expense;

public interface expenseDAO extends  MongoRepository<Expense, String> {
	public Expense findExpenseByID(String ID);
	public Expense findExpenseByOwner(String UserID);
}
