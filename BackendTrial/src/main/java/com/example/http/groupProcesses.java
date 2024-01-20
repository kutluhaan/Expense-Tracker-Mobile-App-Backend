package com.example.http;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.example.templates.*;

import com.mongodb.client.result.UpdateResult;
import com.example.DAO.expenseDAO;
import com.example.DAO.groupDAO;
import com.example.DAO.userDAO;

@RestController
@RequestMapping("/group")
@EnableMongoRepositories(basePackages = {"com.example.DAO"})
public class groupProcesses{
	@Autowired expenseProcesses ExpenseProcess;
	@Autowired groupDAO GroupDAO;
	@Autowired userDAO UserDAO;
	@Autowired expenseDAO ExpenseDAO;
	@Autowired MongoTemplate mongoTemplate;
	
	@PostMapping("/newGroup")
	public String newGroup(@RequestBody() User creator) {
		
		Group newGroup = new Group(creator);
		GroupDAO.insert(newGroup);
		return "Group of " + creator.getName() + " has created";
	}
	
	@PostMapping("/{creator}/addUser")
	public String addUsersToGroup(@PathVariable("creator") Group groupName, @RequestBody() User newMember) {
		boolean check = true;
		String creator = groupName.getCreator();
		Query query = new Query(Criteria.where("creator").is(creator));
		Group group = mongoTemplate.findOne(query, Group.class);
		List<String> currentUsers = group.getUsers();
		
		
		if (!isInGroup(newMember.getID(), group)) {
			currentUsers.add(newMember.getID());
			Update update = new Update().addToSet("users", newMember.getID());
			UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Group.class);
	
			if (updateResult != null && updateResult.getModifiedCount() > 0) {
				System.out.println("User " + newMember.getName() +" added successfully");
			} else {
				System.out.println("User " + newMember.getName() +" insertion failed");
				check = false;
			}
		
		    // Update the group with the new users
		    group.setUsers(currentUsers);
		    // Save the updated group back to the database using your repository or template
		    GroupDAO.save(group);
		} else {
			return "User " + newMember.getName() + " already in group!";
		}
		
		
		if (!check)
			return "Adding users failed";
		return "Adding process done succesfully!";
	}
	
	@DeleteMapping("/{creator}/deleteUser")
	public String deleteUsersFromGroup(@PathVariable("creator") String creUser, @RequestBody() User oldMember) {
	    try {
	        User creator = UserDAO.findByID(creUser);
	        Group group = GroupDAO.findByCreator(creator.getID());
	        
	        // Remove the user's expenses from the group expenses list
	        List<String> groupExpenses = new ArrayList<>(group.getExpenses());
	        groupExpenses.removeIf(expenseId -> expenseId.equals(oldMember.getID()));
	        group.setExpenses(groupExpenses);
	        
	        // Remove the user's expenses from the expenses collection
	        Query expenseQuery = new Query(Criteria.where("owner").is(oldMember.getID()).and("groupExpense").is(group.getCreator()));
	        mongoTemplate.remove(expenseQuery, Expense.class);
	        
	        // Remove the user from the group's users list
	        List<String> currentUsers = new ArrayList<>(group.getUsers());
	        if (currentUsers.removeIf(userId -> userId.equals(oldMember.getID()))) {
	            group.setUsers(currentUsers);
	            GroupDAO.save(group);
	            return "User " + oldMember.getName() + " removed successfully";
	        } else {
	            return "User " + oldMember.getName() + " is not in the group";
	        }
	    } catch (Exception e) {
	        return "Failed to delete user " + oldMember.getName() + " from the group";
	    }
	}

	
	@GetMapping("/totalCost/{creator}")
	public double getTotalCostOfGroup(@PathVariable("creator") String creator) {
		Group group = GroupDAO.findByCreator(creator);
		List<String> expenseIDsOfUser = group.getExpenses();
		double total = 0;
		for (String expenseID: expenseIDsOfUser) {
			Expense tempExp = ExpenseDAO.findExpenseByID(expenseID);
			if (tempExp != null) {
				total += tempExp.getExpense();
			}
		}
		return total;
	}
	
	@PostMapping("/{creator}/{user}/add")
	public String userAddCost(@PathVariable("creator") String creator, @PathVariable("user") String adder, @RequestBody() Expense expenseOfUser) {
		boolean check = true;
		User user = UserDAO.findByID(creator);
		Query query = new Query(Criteria.where("creator").is(user.getID()));
		Group group = mongoTemplate.findOne(query, Group.class);
		
		if (group != null) {
			if (isInGroup(adder, group)) {
				List<String> expenses = group.getExpenses();
				expenses.add(expenseOfUser.getID());
				ExpenseDAO.insert(expenseOfUser);
				Update updateGroup = new Update().addToSet("expenses", expenseOfUser.getID());
				UpdateResult updateGroupResult = mongoTemplate.updateFirst(query, updateGroup, Group.class);
				
				if (updateGroupResult != null && updateGroupResult.getModifiedCount() > 0) {
		            System.out.println("Adding the expense of " + adder + ", " + expenseOfUser +" added successfully");
		        } else {
		        	System.out.println("Adding the expense of " + adder + ", " + expenseOfUser +" failed");
		        	check = false;
		        }				
			}
			else {
				return "User " + adder + " is not a member of group";
			}
			
		} else {
			return "There is no such group in database";
		}
		
		
		if (!check)
			return "Adding expenses failed";
		return "Adding expenses process done succesfully!";
	}

	@PostMapping("/{creator}/deleteExpense")
	public String deleteExpense(@PathVariable("creator") String creator, @RequestBody() Expense expense) {
		boolean check = true;
		User user = UserDAO.findByID(creator);
		Query query = new Query(Criteria.where("creator").is(user.getID()));
		Group group = mongoTemplate.findOne(query, Group.class);
		List<String> currentExpenses = group.getExpenses();
		
		if (currentExpenses.size() == 0) {
			return "No expense";
		}
		
		boolean isIn = false;
		for (String tempExp: currentExpenses) {
			if (tempExp.equals(expense.getID())) {
				currentExpenses.remove(tempExp);
				isIn = true;
				Query queryExpense = new Query(Criteria.where("ID").is(expense.getID()));
				long deletedCount  = mongoTemplate.remove(queryExpense, Expense.class).getDeletedCount();
				if (deletedCount > 0) {
	                System.out.println("Expense of " + expense + " deleted successfully");
	            } else {
	                System.out.println("Expense of " + expense + " not found or already deleted");
	            }
				break;
			}
		}
		
		if (group != null) {
			if (isIn) {
				
				Update updateGroup = new Update().pull("expenses", expense.getID());
				UpdateResult updateGroupResult = mongoTemplate.updateFirst(query, updateGroup, Group.class);
				
				if (updateGroupResult != null && updateGroupResult.getModifiedCount() > 0) {
		            System.out.println("Deleting the " + expense.getName() +" is successful");
		        } else {
		        	System.out.println("Deleting the " + expense.getName() +" failed");
		        	check = false;
		        }
			}else {
				return "Expense is not in group's expenses";
			}
		} 
		
		if (!check) {
			return "Deletion failed!";
		}
		return "Deletion is succesfull!";
	}
	
	@GetMapping("/{creator}/dropGroup")
	public String dropGroup(@PathVariable("creator") User creator) {
	    try {
	        Group group = GroupDAO.findByCreator(creator.getID());
	        List<String> groupMembers = group.getUsers();
	        String groupCreator = group.getCreator();

	        // Find and delete expenses created by group members related to the group
	        for (String memberId : groupMembers) {
	            if (!memberId.equals(groupCreator)) {
	                Query groupExpenseQuery = new Query(Criteria.where("owner").is(memberId).and("groupExpense").is(groupCreator));
	                long deletedCount = mongoTemplate.remove(groupExpenseQuery, Expense.class).getDeletedCount();
	                if (deletedCount > 0) {
	                    System.out.println("Expenses of user " + memberId + " related to the group deleted");
	                }
	            }
	        }

	        // Delete the group's expenses (created by the group creator)
	        Query groupCreatorExpenseQuery = new Query(Criteria.where("owner").is(groupCreator).and("groupExpense").is(groupCreator));
	        long deletedGroupCreatorExpenses = mongoTemplate.remove(groupCreatorExpenseQuery, Expense.class).getDeletedCount();
	        if (deletedGroupCreatorExpenses > 0) {
	            System.out.println("Expenses of group creator " + groupCreator + " related to the group deleted");
	        }

	        // Delete the group
	        Query deleteGroupQuery = new Query(Criteria.where("creator").is(creator.getID()));
	        long deletedGroup = mongoTemplate.remove(deleteGroupQuery, Group.class).getDeletedCount();
	        if (deletedGroup > 0) {
	            return "Group of " + creator.getName() + " deleted successfully";
	        } else {
	            return "Group of " + creator.getName() + " not found or already deleted";
	        }
	    } catch (Exception e) {
	        return "Failed to delete the group of " + creator.getName();
	    }
	}

	public Group getGroup(@RequestParam("ID") String ID) {
		List<Group> allGroups = GroupDAO.findAll();
		for(Group tempGroup: allGroups) {
			List<String> tempUsers = tempGroup.getUsers();	
			if (tempUsers.indexOf(ID) != -1) {
				return tempGroup;
			}
		}
	    return null;
	}

	
	@PostMapping("/{username}/isGroupMember")
	public boolean isInGroup(@PathVariable("username") String user, @RequestBody() Group group) {
		if(group != null) {
			List<String> currentUsers = group.getUsers();
			for(String tempUser: currentUsers) {
				if (tempUser.equals(user)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@GetMapping("/{userName}/expenses")
	public String getUserExpenses(@PathVariable("userName") String userName, @RequestParam("group") Group group){
		Double userExpenses = ExpenseProcess.allExpensesOfUser(userName);
		if (userExpenses != -1) {
			return "Expenses of " + userName + " is " + userExpenses.toString();
		}
		return userName + " has no expense yet!";
	}
}
