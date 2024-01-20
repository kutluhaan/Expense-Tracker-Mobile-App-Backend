package com.example.main;

import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.example.templates.*;
import com.example.DAO.*;
import com.example.http.*;


@SpringBootApplication
@ComponentScan(basePackages = {"com.example.http"})
public class mainApplication implements CommandLineRunner {
	@Autowired userDAO UserDAO;
	@Autowired groupDAO GroupDAO;
	@Autowired expenseDAO ExpenseDAO;
	@Autowired userProcesses UserProcess;
	@Autowired groupProcesses GroupProcess;
	@Autowired expenseProcesses ExpenseProcess;
	
	Logger logger = LoggerFactory.getLogger(mainApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(mainApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		User user1 = new User("1", "Kutluhan Aygüzel", "kutluhan52", "12345Kutluhan", "kutluhan@sabanciuniv.edu");
		User user2 = new User("2", "Uğur Haykır", "ugurAcıbadem", "deneme123", "haykir@sabanciuniv.edu");
		User user3 = new User("3", "Selim Böğürcü", "bogurcu1", "pass", "bogurcu.selim@sabanciuniv.edu");
		User user4 = new User("4", "Akif Horasan", "horasanAkif", "horasan", "horasam@sabanciuniv.edu");
		if (UserDAO.count() == 0) {
			
			UserProcess.newUser(user1);
			UserProcess.newUser(user2);
			UserProcess.newUser(user3);
			UserProcess.newUser(user4);
			
		}
		
		if (UserDAO.count() != 0) {
			logger.info("Başarılı!");
		}
		
		// /*
		logger.info(GroupProcess.newGroup(user1));
		logger.info(GroupProcess.newGroup(user2));
		logger.info(GroupProcess.newGroup(user3));
		logger.info(GroupProcess.newGroup(user4));
		
		Group group4 = GroupProcess.getGroup(user4.getID());
		
		Expense expenseUser33 = new Expense("8", "Benzinlik", 545, user3.getID(), "4");
		Expense expenseUser43 = new Expense("7", "Ucuz Ayakkabı", 558, user4.getID(), "4");
		
		logger.info(GroupProcess.addUsersToGroup(group4, user3));
		
		GroupProcess.userAddCost(group4.getCreator(), user3.getID(), expenseUser33);
		GroupProcess.userAddCost(group4.getCreator(), user4.getID(), expenseUser43);
		
		logger.info(GroupProcess.dropGroup(user3));
		logger.info(GroupProcess.dropGroup(user1));
		logger.info(GroupProcess.dropGroup(user1));
		logger.info(GroupProcess.dropGroup(user1));
		
		logger.info(GroupProcess.addUsersToGroup(group4, user1));
		logger.info(GroupProcess.addUsersToGroup(group4, user2));
		logger.info(GroupProcess.addUsersToGroup(group4, user3));
		
		if (GroupProcess.isInGroup(user3.getID(), GroupProcess.getGroup(user4.getID()))) {
			logger.info("User 3 is in group4");
			logger.info(Double.toString(GroupProcess.getTotalCostOfGroup(user4.getID())));
		
			Expense expenseUser3 = new Expense("5", "Benzinlik", 545, user3.getID(), "4");
			Expense expenseUser4 = new Expense("6", "Ucuz Ayakkabı", 558, user4.getID(), "4");
			
			GroupProcess.userAddCost(group4.getCreator(), user3.getID(), expenseUser3);
			GroupProcess.userAddCost(group4.getCreator(), user4.getID(), expenseUser4);
			
			logger.info(GroupProcess.deleteExpense(group4.getCreator(), expenseUser4)); 
			
			logger.info(Double.toString(GroupProcess.getTotalCostOfGroup(user4.getID())));
			
			logger.info(GroupProcess.deleteUsersFromGroup(group4.getCreator(), user2));
			logger.info(GroupProcess.deleteUsersFromGroup(group4.getCreator(), user3));
			
			logger.info(GroupProcess.getUserExpenses(user4.getUserName(), group4));
			
		} else {
			logger.info("Not working properly");
		}
		// */ 
		
		// /*
		if (UserProcess.userExist(user1.getUserName())) {
			
			Expense expense1 = new Expense("1", "Yemekhane", 505, user1.getID(), "none");
			Expense expense2 = new Expense("2", "Uçak", 5045, user1.getID(), "none");
			Expense expense3 = new Expense("3", "Bilet", 305, user1.getID(), "none");
			Expense expense4 = new Expense("4", "Sinema", 5, user1.getID(), "none");
			Expense expense5 = new Expense("5", "Benzin", 55, user1.getID(), "none");
			Expense expense6 = new Expense("6", "Ayakkabı", 5058, user1.getID(), "none");
			
			UserProcess.addExpense(user1.getUserName(), expense1);
			UserProcess.addExpense(user1.getUserName(), expense2);
			UserProcess.addExpense(user1.getUserName(), expense3);
			UserProcess.addExpense(user1.getUserName(), expense4);
			UserProcess.addExpense(user1.getUserName(), expense5);
			UserProcess.addExpense(user1.getUserName(), expense6);
			
			double total = UserProcess.getTotalCost(user1.getUserName());
			logger.info("Total cost for " + user1.getName() + " is " + Double.toString(total) + " TL.");
			
			UserProcess.deleteExpense(user1.getUserName(), expense6);
			double totalAgain = UserProcess.getTotalCost(user1.getUserName());
			logger.info("Total cost for " + user1.getName() + " is " + Double.toString(totalAgain) + " TL.");
			
			UserProcess.changeInfo(user1.getUserName(), "userName", "kutluhannnn");
			user1.setUserName("kutluhannnn");
			logger.info(UserProcess.getUserInfo(user1.getUserName()));
		}
		else {
			logger.info(user1.getName() + "does NOT exists in database!");
		}
		
		// */
	}
}
