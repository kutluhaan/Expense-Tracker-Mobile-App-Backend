package com.example.templates;


import java.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Expense {
	private String ID;
	private String name;
	private double expense;
	private String owner;
	private LocalDateTime createdAt; 
	private String groupExpense;
	
	public String isGroupExpense() {
		return groupExpense;
	}

	public void setGroupExpense(String groupExpense) {
		this.groupExpense = groupExpense;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Expense(String ID, String name, double expense, String owner, String groupExpense) {
		super();
		this.ID = ID;
		this.name = name;
		this.expense = expense;
		this.owner = owner;
		this.createdAt = LocalDateTime.now();
		this.groupExpense = groupExpense;
	}
	
	public Expense() {
		super();
	}

	//setters
	public void setName(String name) {
		this.name = name;
	}
	
	public void setExpense(double expense) {
		this.expense = expense;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}


	
	//getters
	public String getID() {
		return this.ID;
	}
	public String getName() {
		return name;
	}

	
	public double getExpense() {
		return expense;
	}

	
	public String getOwner() {
		return this.owner;
	}	
	
	public boolean Equals(String expense) {
		if (this.ID.equals(expense)) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "Expense [name=" + name + ", expense=" + expense + ", owner=" + owner + "]";
	}
}
