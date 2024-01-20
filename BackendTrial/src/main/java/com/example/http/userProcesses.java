package com.example.http;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.example.templates.User;
import com.example.templates.Expense;
import com.mongodb.client.result.UpdateResult;
import com.example.DAO.expenseDAO;
import com.example.DAO.userDAO;

@RestController
@Component
@RequestMapping("/user")
public class userProcesses {
	@Autowired userDAO UserDAO;
	@Autowired expenseDAO ExpenseDAO;
	@Autowired MongoTemplate mongoTemplate;
	
	@PostMapping("/newUser")
	public String newUser(@RequestBody() User newUser) {
		User insertedUser = UserDAO.insert(newUser);
		if (insertedUser == null) {
			return "User " + newUser.getName() + " has not created succesfully";
		}
		return "User " + newUser.getName() + " has succesfully created";
	}
	
	@GetMapping("/login/{username}")
	public boolean userExist(@PathVariable("username") String userName) {
		if(UserDAO.findByUserName(userName) != null) {
			return true;
		}
		return false;
	}
	
	@PostMapping("/{username}/addExpense")
	public String addExpense(@PathVariable("username") String userName, @RequestBody() Expense expense) {
		//update user
		boolean userUpdate = true;
		Query userQuery = new Query(Criteria.where("userName").is(userName));
		
		User user = mongoTemplate.findOne(userQuery, User.class);
		List<String> currentExpenses = user.getCosts();
		currentExpenses.add(expense.getID());
		ExpenseDAO.insert(expense);
		Update update = new Update().addToSet("expenditures", expense);
		UpdateResult updateResult = mongoTemplate.updateFirst(userQuery, update, User.class);
		if (updateResult != null && updateResult.getModifiedCount() > 0) {
            System.out.println("Expense " + expense.getName() +" added successfully to user " + userName);
        } else {
        	System.out.println("The insertion of " + expense.getName() +" failed for user " + userName);
        	userUpdate = false;
        }
		
		user.setExpenditures(currentExpenses);
		UserDAO.save(user);
		
		
		if (!userUpdate)
			return "Adding expenses failed";
		return "Adding expenses succesfully!";
	}
	
	@PostMapping("/{username}/deleteExpense")
	public String deleteExpense(@PathVariable("username") String userName,@RequestBody() Expense expense) {
		//update user
		Query userQuery = new Query(Criteria.where("userName").is(userName));
		User user = mongoTemplate.findOne(userQuery, User.class);
		List<String> currentExpenses = user.getCosts();
		boolean found = false;
		
		for(String tempExp: currentExpenses) {
			if (expense.Equals(tempExp)) {
				currentExpenses.remove(tempExp);
				user.setExpenditures(currentExpenses);
				UserDAO.save(user);
				found =true;
				System.out.println("Expense " + expense.getName() + " deleted succesfully!");
				break;
				
			}
		}
		if (found) {
			// Fetch individual expenses of the user
			Query individualExpensesQuery = new Query(Criteria.where("ID").is(expense.getID()).and("owner").is(user.getID()).and("groupExpense").is("none"));
			long deletedCount = mongoTemplate.remove(individualExpensesQuery, Expense.class).getDeletedCount();
			if (deletedCount > 0){
				System.out.println("Deletion of " + expense.getName() + " of " + userName + " is successfull");
			}
		}
		return "Expense " + expense.getName() + " could not be deleted!";
	}
	
	@GetMapping("/{username}/totalCost")
	public double getTotalCost(@PathVariable("username") String userName) {
	    try {
	        double totalCost = 0;
	        Query userQuery = new Query(Criteria.where("userName").is(userName));
	        User user = mongoTemplate.findOne(userQuery, User.class);

	        if (user != null) {
	            // Fetch individual expenses of the user
	            Query individualExpensesQuery = new Query(Criteria.where("owner").is(user.getID()).and("groupExpense").is("none"));
	            List<Expense> individualExpenses = mongoTemplate.find(individualExpensesQuery, Expense.class);

	            for (Expense expense : individualExpenses) {
	                totalCost += expense.getExpense();
	            }

	            return totalCost;
	        } else {
	            return -1; // User not found
	        }
	    } catch (Exception e) {
	        return -1; // Error occurred
	    }
	}

	
	@GetMapping("/{username}")
	public String getUserInfo(@PathVariable("username") String userName) {
		Query query = new Query(Criteria.where("userName").is(userName));
		User user = mongoTemplate.findOne(query, User.class);
		if (user != null)
			return user.toString();
		return "User is not present!";
	}
	
	@PostMapping("/{username}/{fieldToChange}/changeInfo")
	public String changeInfo(@PathVariable("username") String userName, @PathVariable("fieldToChange") String field, @RequestBody() String newElement) {
		boolean done = true;
		Query query = new Query(Criteria.where("userName").is(userName));
		User user = mongoTemplate.findOne(query, User.class);
		
		if (field == "name") {
			user.setName(newElement);
			Update update = new Update().set("name", newElement);
			UpdateResult updateResult = mongoTemplate.updateFirst(query, update, User.class);
			if (!(updateResult != null && updateResult.getModifiedCount() > 0)) {
				done = false;
	        }
			user.setName(field);
			
		} else if (field == "userName") {
			user.setUserName(newElement);
			Update update = new Update().set("userName", newElement);
			UpdateResult updateResult = mongoTemplate.updateFirst(query, update, User.class);
			if (!(updateResult != null && updateResult.getModifiedCount() > 0)) {
				done = false;
	        }
			user.setUserName(field);
		} else if (field == "password") {
			user.setPassword(newElement);
			Update update = new Update().set("password", newElement);
			UpdateResult updateResult = mongoTemplate.updateFirst(query, update, User.class);
			if (!(updateResult != null && updateResult.getModifiedCount() > 0)) {
				done = false;
	        }
			user.setPassword(field);
		} else if (field == "email") {
			user.setEmail(newElement);
			Update update = new Update().set("email", newElement);
			UpdateResult updateResult = mongoTemplate.updateFirst(query, update, User.class);
			if (!(updateResult != null && updateResult.getModifiedCount() > 0)) {
				done = false;
	        }
			user.setEmail(field);
		}
		
		if (done) {
			return "Updating " + field + " is done succesfully!";
		}
		return field + " cannot be updated!";
	}
}
