package com.example.templates;

import java.util.List;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document
public class User {
	
	@Id private String id;
	private String ID;
	private String name;
	private String userName;
	private String password;
	private String email;
	private List<String> expenditures;
	
	public User(String ID, String name, String userName, String password, String email) {
		super();
		this.ID = ID;
		this.name = name;
		this.userName = userName;
		this.email = email;
		this.password = password;
		this.expenditures = new ArrayList<String>();
	} 
	
	
	public User() {
		super();
	}


	public User(String id, String iD2, String name, String userName, String password, String email,
			List<String> expenditures) {
		super();
		this.id = id;
		this.ID = iD2;
		this.name = name;
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.expenditures = expenditures;
	}


	//getters	
	public String getID() {
		return this.ID;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	public List<String> getCosts() {
		return this.expenditures;
	}
	
	
	//setters	
	public void setID(String ID) {
		this.ID = ID;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setExpenditures(List<String> expenditures) {
		this.expenditures = expenditures;
	}
	
	public void addExpense(Expense expense) {
		expenditures.add(expense.getID());
	}
	
	public boolean userEquals(User user) {
		if (this.ID.equals(user.getID())) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", userName=" + userName + ", password=" + password + ", email="
				+ email + ", expenditures=" + expenditures + "]";
	}	
}
