package com.example.http;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.example.templates.*;

import com.example.DAO.expenseDAO;
import com.example.DAO.userDAO;


@RestController
@RequestMapping("/expense")
public class expenseProcesses {
	@Autowired expenseDAO ExpenseDAO;
	@Autowired userDAO UserDAO;
	@Autowired MongoTemplate mongoTemplate;
	
	@PostMapping("/newExpense")
	public String createExpense(@RequestParam("expense") Expense expense) {
		ExpenseDAO.insert(expense);
		return "Success";
	}
	
	@GetMapping("/{ID}")
	public double getExpense(@PathVariable("ID")String ID) {
		Expense expense = ExpenseDAO.findExpenseByID(ID);
		if (expense != null) {
			return expense.getExpense();
		}
		return -1;
	}
	
	@GetMapping("/{username}/{ID}/getExpense")
	public double getExpenseOfUser(@RequestParam("username") String username, @RequestParam("ID") String ID) {
		User user = UserDAO.findByUserName(username);
		Expense expense = ExpenseDAO.findExpenseByOwner(user.getID());
		return expense.getExpense();
	}
	
	@GetMapping("/allExpensesOf/{username}")
	public double allExpensesOfUser(@RequestParam("username") String username) {
		User user = UserDAO.findByUserName(username);
		
		List<Expense> allExpenses = ExpenseDAO.findAll();
		if (allExpenses == null) {
			return -1;
		}
		List<Double> expenses = new ArrayList<Double>();
		for (Expense expense: allExpenses) {
			if (expense.getOwner().equals(user.getID())) {
				expenses.add(expense.getExpense());
			}
		}
	
		double total = 0;
		for (Double num: expenses) {
			total += num;
		}
		return total;
	}
	
	@PostMapping("/delete/{ID}")
	public String deleteExpense(@RequestParam("ID") String ID) {
		Query query = new Query(Criteria.where("ID").is(ID));
		long deletedCount  = mongoTemplate.remove(query, Expense.class).getDeletedCount();
		if (deletedCount > 0) {
            return "Expense deleted successfully";
        } else {
            return "Expense is not found or already deleted";
        }
	}
}
