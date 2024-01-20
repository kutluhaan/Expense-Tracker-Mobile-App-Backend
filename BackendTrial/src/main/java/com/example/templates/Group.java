package com.example.templates;

import java.util.List;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Group {
	@Id private String ID;
	private List<String> users;
	private List<String> expenses;
	private String creator;
	
	public Group(User creator) {
		super();
		this.creator = creator.getID();
		this.expenses = new ArrayList<String>();
		this.users = new ArrayList<String>();
		
		users.add(creator.getID());
	}
	
	public Group(String iD, List<String> users, List<String> expenses, String creator) {
		super();
		ID = iD;
		this.users = users;
		this.expenses = expenses;
		this.creator = creator;
	}

	public Group() {
		super();
	}

	//getters
	public List<String> getExpenses(){
		return this.expenses;
	}
	
	public String getCreator() {
		return this.creator;
	}
	
	
	public List<String> getUsers() {
		return users;
	}
	
	//setters
	public void setExpenses(List<String> expenses) {
		this.expenses = expenses;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	
	public void setUsers(List<String> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "Group [users=" + users + ", expenses=" + expenses + ", creator=" + creator + "]";
	} // null

	public String getGroupUser(String userName) {
		String user = "EMPTY";
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i) == userName) {
				user = users.get(i);
			}
		}
		return user;
	}
}
