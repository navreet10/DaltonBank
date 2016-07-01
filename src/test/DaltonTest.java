package test;

import static org.junit.Assert.*;

import org.junit.Test;

import DaltonBank.Account;
import DaltonBank.BankMain;
import DaltonBank.Transactions;

import java.util.Scanner;

public class DaltonTest {
	@Test
	public void testAddAccount() {
		Account acc= new Account(); 
		acc.setAccountNumber(1237);
		acc.setBalance(300);
		acc.setName("Xiao");
		acc.setType("C");
		assertTrue(BankMain.addAccounts(acc).equals("success"));
	
	}
	
	@Test
	public void testAddTransaction() {
		Transactions t= new Transactions(); 
		t.setAccountNum(1236);
		t.setAmount(100);
		t.setDate("02/03/2014");
		t.setType("Check");
		assertTrue(BankMain.addTransactions(t).equals("success"));
	}
	

	@Test
	public void testSummary() {
		assertFalse(BankMain.summary(new Scanner(System.in)).equals(""));
	}
	@Test
	public void testTransferAmount() {		
		assertTrue(BankMain.transferAmount(new Scanner(System.in)).equals("success"));
	}
	@Test
	public void testDisplay() {
		assertTrue(BankMain.displayAccounts().equals("success"));
	}
	
	
	@Test
	public void testProcessTransaction() {
		assertTrue(BankMain.processTransactions().equals("success"));
	}
	@Test
	public void testCheckType() {
		assertTrue(BankMain.checkType(1).equals("Check"));
	}
	
	

}
