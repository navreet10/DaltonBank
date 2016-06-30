package DaltonBank;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import DBUtility.DBUtility;

public class BankMain {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("Welcome to Dalton Corp Savings and Loan");
		System.out.println("Please create the user account(s)");

		/*
		 * List<Account> accounts = new ArrayList<Account>(); List<Transactions>
		 * trnsList = new ArrayList<Transactions>();
		 */
		System.out.println("Enter an account # or -1 to stop entering accounts:");
		int accountNum = in.nextInt();
		if (accountNum != -1) {
			do {
				Account account = new Account();
				account.setAccountNumber(accountNum);
				System.out.println("Enter the name for acct # " + accountNum + ":");
				String name = in.next();
				account.setName(name);
				System.out.println("Enter the balance for acct # " + accountNum + ":");
				int balance = in.nextInt();
				account.setBalance(balance);
				System.out.println("Enter the type of account Checking (C) or Saving (S) # " + accountNum + ":");
				String accType = in.next();
				account.setType(accType);
				addAccounts(account);
				// accounts.add(account);
				System.out.println("Enter an account # or -1 to stop entering accounts:");
				accountNum = in.nextInt();
			} while (accountNum != -1);

		}
		// addAccounts(accounts);
		System.out.println(
				"Enter a transaction type (Check (1), Debit card(2), Deposit(3) or Withdrawal(4)) or -1 to finish :");
		String transactionType = checkType(in.nextInt());

		if (!transactionType.equals("Break")) {
			do {
				Transactions transactions = new Transactions();
				transactions.setType(transactionType);
				System.out.println("Enter the acct # " + accountNum + ":");
				int num = in.nextInt();
				transactions.setAccountNum(num);
				System.out.println("Enter the amount of the " + transactionType + ":");
				float amount = in.nextFloat();
				transactions.setAmount(amount);
				System.out.println("Enter the date of the " + transactionType + ":");
				String inp = in.next();
				transactions.setDate(inp);
				DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
				Date date;
				try {
					date = format.parse(inp);
					transactions.setTransDate(date);

				} catch (ParseException e) {
					e.printStackTrace();
				}
				addTransactions(transactions);
				// trnsList.add(transactions);
				System.out.println(
						"Enter a transaction type (Check (1), Debit card(2), " + "Deposit(3) or Withdrawal(4)) "
								+ "or enter (5) to transfer money between your accounts" + " or -1 to finish :");
				transactionType = checkType(in.nextInt());
				if (transactionType.equals("T")) {
					transferAmount(in);
					System.out.println(
							"Enter a transaction type (Check (1), Debit card(2), " + "Deposit(3) or Withdrawal(4)) "
									+ "or enter (5) to transfer money between your accounts" + " or -1 to finish :");
					transactionType = checkType(in.nextInt());
				}

			} while (transactionType != "Break");

		}

		// addTransactions(trnsList);

		processTransactions();

		displayAccounts();
		in.close();
	}

	private static void transferAmount(Scanner in) {
		System.out.println("Please enter the account from which to transfer:");
		int accFrom = in.nextInt();
		System.out.println("Please enter the account to which to transfer:");
		int accTo = in.nextInt();
		System.out.println("Please enter the amount to transfer:");
		float amount = in.nextFloat();
		
		String query = "Select name from account where name ";
		List<String> params = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		Map<Integer, List<String>> map = DBUtility.selectData(query, params, types, "ora1", "ora1");

		

	}

	private static void displayAccounts() {

		String query = "Select distinct accnum, balance from account";
		List<String> params = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		Map<Integer, List<String>> map = DBUtility.selectData(query, params, types, "ora1", "ora1");

		for (int i = 1; i < map.keySet().size(); i++) {
			System.out.println("Account Number: " + map.get(i).get(0) + " Balance: " + map.get(i).get(1));
		}

	}

	private static void addTransactions(Transactions trns) {

		String query = "Select * from account where accnum= ?";
		List<String> params = new ArrayList<String>();
		params.add(trns.getAccountNum() + "");
		List<String> types = new ArrayList<String>();
		types.add("int");
		Map<Integer, List<String>> map = DBUtility.selectData(query, params, types, "ora1", "ora1");

		if (map.size() < 2) {
			System.out.println("Account num does not exist");
			return;
		}

		query = "Select typeid from types where type= ?";
		params = new ArrayList<String>();
		params.add(trns.getType());
		types = new ArrayList<String>();
		types.add("String");
		map = DBUtility.selectData(query, params, types, "ora1", "ora1");

		if (map.size() < 2) {
			System.out.println("Type Unknown");
			return;
		}
		System.out.println(map.get(1).get(0));
		int typeId = Integer.parseInt(map.get(1).get(0));

		query = "Insert into transaction (typeID, amount,accnum,curdate,status) values(?,?,?,?,?)";
		params = new ArrayList<String>();
		params.add(0, typeId + "");
		params.add(1, trns.getAmount() + "");
		params.add(2, trns.getAccountNum() + "");
		params.add(3, trns.getDate());
		params.add(4, "New");
		types = new ArrayList<String>();
		types.add(0, "int");
		types.add(1, "double");
		types.add(2, "int");
		types.add(3, "String");
		types.add(4, "String");
		int res = DBUtility.updateData(query, params, types, "ora1", "ora1");
		if (res == 0) {
			System.out.println("Row not inserted");
		}

	}

	private static void addAccounts(Account acc) {
		String query = "Insert into account values(?,?,?,?)";
		List<String> params = new ArrayList<String>();
		params.add(0, acc.getName());
		params.add(1, acc.getBalance() + "");
		params.add(2, acc.getAccountNumber() + "");
		params.add(3, acc.getType());
		List<String> types = new ArrayList<String>();
		types.add(0, "String");
		types.add(1, "double");
		types.add(2, "int");
		types.add(3, "String");
		int res = DBUtility.updateData(query, params, types, "ora1", "ora1");
		if (res == 0) {
			System.out.println("Row not inserted");
		}

	}

	private static void processTransactions() {
		List<Transactions> trnsList = new ArrayList<Transactions>();
		String query = "Select * from transaction where status = 'New'";
		List<String> params = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		Map<Integer, List<String>> map = DBUtility.selectData(query, params, types, "ora1", "ora1");

		for (int i = 1; i < map.keySet().size(); i++) {
			Transactions trns = new Transactions();
			trns.setType(checkType(Integer.parseInt(map.get(i).get(0))));
			trns.setAmount(Float.parseFloat(map.get(i).get(1)));
			trns.setAccountNum(Integer.parseInt(map.get(i).get(2)));
			trns.setDate(map.get(i).get(3));
			DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
			Date date;
			try {
				date = format.parse(map.get(i).get(3));
				trns.setTransDate(date);

			} catch (ParseException e) {
				e.printStackTrace();
			}
			trnsList.add(trns);
		}

		Collections.sort(trnsList, new Comparator<Transactions>() {
			@Override
			public int compare(Transactions p1, Transactions p2) {
				return p1.getTransDate().compareTo(p2.getTransDate()); // Ascending
			}

		});

		for (Transactions trns : trnsList) {

			query = "Select balance, name from account where accnum= ?";
			params = new ArrayList<String>();
			params.add(trns.getAccountNum() + "");
			types = new ArrayList<String>();
			types.add("int");
			map = DBUtility.selectData(query, params, types, "ora1", "ora1");

			float balance = Integer.parseInt(map.get(1).get(0));
			String name = map.get(1).get(1);
			float newBalance = 0.0f;
			if (trns.getType().equals("Deposit")) {
				newBalance = trns.getAmount() + balance;
			} else {
				newBalance = balance - trns.getAmount();
				if (newBalance < 0) {
					if (!checkSavingBalance(newBalance + 35 + 15, trns.getAccountNum(), name))
						newBalance -= 35;
					else
						newBalance = 0;
				}
			}

			query = "update account set balance =? where accnum =?";
			params = new ArrayList<String>();
			params.add(newBalance + "");
			params.add(trns.getAccountNum() + "");
			types = new ArrayList<String>();
			types.add("double");
			types.add("int");
			int res = DBUtility.updateData(query, params, types, "ora1", "ora1");
			if (res == 0) {
				System.out.println("Balance not updated");
			}

			query = "update transaction set status ='Completed'";
			params = new ArrayList<String>();
			types = new ArrayList<String>();
			res = DBUtility.updateData(query, params, types, "ora1", "ora1");
			if (res == 0) {
				System.out.println("Balance not updated");
			}

		}

	}

	private static boolean checkSavingBalance(float newBalance, int accnum, String name) {
		String query = "Select balance,accnum from account where type='S' and name=?";
		List<String> params = new ArrayList<String>();
		params.add(accnum + "");
		params.add(name);
		boolean res = false;
		List<String> types = new ArrayList<String>();
		types.add("int");
		types.add("String");
		Map<Integer, List<String>> map = DBUtility.selectData(query, params, types, "ora1", "ora1");
		Map<Integer, Float> accs = new HashMap<Integer, Float>();
		float total = 0f;
		for (int i = 1; i < map.keySet().size(); i++) {
			if (Float.parseFloat(map.get(i).get(0)) > 0) {
				total += Float.parseFloat(map.get(i).get(0));
				if (total >= newBalance) {
					accs.put(Integer.parseInt(map.get(i).get(1)), total - newBalance);
					res = true;
				} else {
					accs.put(Integer.parseInt(map.get(i).get(1)), 0f);
				}
			}
		}
		if (res) {
			updateBalance(accs);
		}
		return res;
	}

	private static void updateBalance(Map<Integer, Float> accs) {
		Set<Integer> acc = accs.keySet();
		for (int i : acc) {
			String query = "update account set balance = " + accs.get(acc) + " where accnum = " + i;
			List<String> params = new ArrayList<String>();
			List<String> types = new ArrayList<String>();
			int res = DBUtility.updateData(query, params, types, "ora1", "ora1");
			if (res == 0) {
				System.out.println("Balance not updated for account");
			}
		}

	}

	private static String checkType(int next) {
		String transactionType = null;
		switch (next) {
		case 1:
			transactionType = "Check";
			break;
		case 2:
			transactionType = "Debit card";
			break;
		case 3:
			transactionType = "Deposit";
			break;
		case 4:
			transactionType = "Withdrawal";
			break;
		case 5:
			transactionType = "T";
			break;
		case -1:
			transactionType = "Break";
		}
		return transactionType;
	}

}
