package DaltonBank;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import DBUtility.DBUtility;
/**
 * BankMain
 * @author Sanju and Navreet
 *
 */
public class BankMain {
	/**
	 * Main method
	 * interacts with user
	 * @param args
	 */
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.println("Welcome to Dalton Corp Savings and Loan");
		System.out.println("Please create the user account(s)");

		/*
		 * List<Account> accounts = new ArrayList<Account>(); List<Transactions>
		 * trnsList = new ArrayList<Transactions>();
		 */
		System.out.println("Enter an account # or enter (0) to get summary or -1 to stop entering accounts:");
		int accountNum = in.nextInt();
		if (accountNum == 0) {
			summary(in);
		} else {
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
						System.out.println("Enter a transaction type (Check (1), Debit card(2), "
								+ "Deposit(3) or Withdrawal(4)) "
								+ "or enter (5) to transfer money between your accounts" + " or -1 to finish :");
						transactionType = checkType(in.nextInt());
					}

				} while (transactionType != "Break");

			}

			processTransactions();
			displayAccounts();
		}

		in.close();
	}
	/**
	 * Gives summary of transaction
	 * @param in Scanner
	 */
	public static String summary(Scanner in) {
		System.out.println("Please enter account num to get specific details "
				+ "or (0) to get all details");
		int acc = in.nextInt();
		String result= "";
		if (acc == 0) {
			String query = "SELECT a.no_of_transaction,"
					+" b.no_of_deposits,"
					+"   c.total_deposits,"
					+"   d.no_of_withdrawls,"
					+"   e.total_withdrawals,"
					+"   f.fee_charged,"
					+"   g.no_of_checkingacc,"
					+"   h.no_of_savingacc"
					+" FROM"
					+"   (SELECT COUNT(DISTINCT transactionid) AS no_of_transaction FROM transaction"
					+"   ) a,"
					+"   (SELECT COUNT(DISTINCT transactionid) AS no_of_deposits"
					+"   FROM transaction"
					+"   WHERE TYPEID=3"
					+"   ) b,"
					+"   (SELECT SUM(amount) AS total_deposits FROM transaction WHERE TYPEID=3"
 					+"  ) c,"
					+"   (SELECT COUNT(DISTINCT transactionid) AS no_of_withdrawls"
					+"   FROM transaction"
					+"   WHERE TYPEID=4"
					+"   ) d,"
					+"   (SELECT SUM(amount) AS total_withdrawals FROM transaction WHERE TYPEID=4"
					+"   ) e,"
					+"   (SELECT SUM(feecharged) AS fee_charged FROM account"
					+"   ) f,"
					+"   (SELECT COUNT(DISTINCT accnum) AS no_of_checkingacc"
					+"   FROM account"
					+"   WHERE type ='C'"
					+"   ) g,"
					+"   (SELECT COUNT(DISTINCT accnum) AS no_of_savingacc"
					+"   FROM account"
					+"   WHERE type ='S'"
					+"   ) h";
			List<String> params = new ArrayList<String>();
			List<String> types = new ArrayList<String>();
			Map<Integer, List<String>> map = DBUtility.selectData(query, params, types, "ora1", "ora1");
			result= "Total Number of Transactions: " + map.get(1).get(0)
					+ "\nNumber of Deposits: " + map.get(1).get(1)
					+ "\nTotal Deposits: $" + map.get(1).get(2)
					+ "\nNumber of Withdrawals: " + map.get(1).get(3)
					+ "\nTotal Withdrawals: $" + map.get(1).get(4)
					+ "\nTotal amount of Fees charged:  $" + map.get(1).get(5)
					+ "\nNumber of checking accounts: " + map.get(1).get(6)
					+ "\nNumber of savings accounts: " + map.get(1).get(7);
			System.out.println(result);
			
		} else {
			String query = "SELECT a.no_of_transaction,"
					+" b.no_of_deposits,"
					+"   c.total_deposits,"
					+"   d.no_of_withdrawls,"
					+"   e.total_withdrawals,"
					+"   f.fee_charged,"
					+"   g.no_of_checkingacc,"
					+"   h.no_of_savingacc,"
					+"   e.name"
					+" FROM"
					+"   (SELECT COUNT(DISTINCT transactionid) AS no_of_transaction FROM transaction"
					+ " where accnum = ?"
					+"   ) a,"
					+"   (SELECT COUNT(DISTINCT transactionid) AS no_of_deposits"
					+"   FROM transaction"
					+"   WHERE TYPEID=3 and accnum = ?"
					+"   ) b,"
					+"   (SELECT SUM(amount) AS total_deposits FROM transaction WHERE TYPEID=3 and accnum = ?"
 					+"  ) c,"
					+"   (SELECT COUNT(DISTINCT transactionid) AS no_of_withdrawls"
					+"   FROM transaction"
					+"   WHERE TYPEID=4 and accnum = ?"
					+"   ) d,"
					+"   (SELECT SUM(amount) AS total_withdrawals FROM transaction WHERE TYPEID=4 and accnum = ?"
					+"   ) e,"
					+"   (SELECT SUM(feecharged) AS fee_charged FROM account where accnum = ?"
					+"   ) f,"
					+"   (SELECT COUNT(DISTINCT accnum) AS no_of_checkingacc"
					+"   FROM account"
					+"   WHERE type ='C' and accnum = ?"
					+"   ) g,"
					+"   (SELECT COUNT(DISTINCT accnum) AS no_of_savingacc"
					+"   FROM account"
					+"   WHERE type ='S' and accnum = ?"
					+"   ) h,"
					+"  (SELECT distinct name"
					+"   FROM account"
					+"   WHERE accnum = ?"
					+"   ) e";
			List<String> params = new ArrayList<String>();
			params.add(0, acc + "");
			params.add(1, acc + "");
			params.add(2, acc + "");
			params.add(3, acc + "");
			params.add(4, acc + "");
			params.add(5, acc + "");
			params.add(6, acc + "");
			params.add(7, acc + "");
			params.add(8, acc + "");
			List<String> types = new ArrayList<String>();
			types.add(0, "int");
			types.add(1, "int");
			types.add(2, "int");
			types.add(3, "int");
			types.add(4, "int");
			types.add(5, "int");
			types.add(6, "int");
			types.add(7, "int");
			types.add(8, "int");
			Map<Integer, List<String>> map = DBUtility.selectData(query, params, types, "ora1", "ora1");
			result= "Name: "+ map.get(1).get(8) + " Account: " + acc 
							+"\nTotal Number of Transactions: " + map.get(1).get(0)
							+ "\nNumber of Deposits: " + map.get(1).get(1)
							+ "\nTotal Deposits: $" + map.get(1).get(2)
							+ "\nNumber of Withdrawals: " + map.get(1).get(3)
							+ "\nTotal Withdrawals: $" + map.get(1).get(4)
							+ "\nTotal amount of Fees charged:  $" + map.get(1).get(5)
							+ "\nNumber of checking accounts: " + map.get(1).get(6)
							+ "\nNumber of savings accounts: " + map.get(1).get(7);
			System.out.println(result);
		}
		return result;

	}
	/**
	 * Transfers money from one account to another
	 * for the same user
	 * @param in Scanner
	 */
	public static String transferAmount(Scanner in) {
		System.out.println("Please enter the account from which to transfer:");
		int accFrom = in.nextInt();
		System.out.println("Please enter the account to which to transfer:");
		int accTo = in.nextInt();
		System.out.println("Please enter the amount to transfer:");
		float amount = in.nextFloat();

		String query = "Select distinct name from account where accnum in (?,?)";
		List<String> params = new ArrayList<String>();
		params.add(0, accFrom + "");
		params.add(1, accTo + "");
		List<String> types = new ArrayList<String>();
		types.add(0, "int");
		types.add(1, "int");
		Map<Integer, List<String>> map = DBUtility.selectData(query, params, types, "ora1", "ora1");
		if (map.size() != 2) {
			System.out.println("Accounts doesn't belong to the same person");
			return "";
		}

		query = "Select balance from account where accnum in (?,?)";
		params = new ArrayList<String>();
		params.add(0, accFrom + "");
		params.add(0, accTo + "");
		types = new ArrayList<String>();
		types.add(0, "int");
		types.add(0, "int");
		map = DBUtility.selectData(query, params, types, "ora1", "ora1");
		float curBalanceFrom = Float.parseFloat(map.get(1).get(0));
		float curBalanceTo = Float.parseFloat(map.get(2).get(0));

		if ((curBalanceFrom - amount) < 0) {
			System.out.println("Insufficient Balance");
			return "";
		}

		query = "Update account set balance= ? where accnum = ?";
		params = new ArrayList<String>();
		params.add(0, (curBalanceFrom - amount) + "");
		params.add(1, accFrom + "");
		types = new ArrayList<String>();
		types.add(0, "double");
		types.add(1, "int");
		int res = DBUtility.updateData(query, params, types, "ora1", "ora1");
		if (res == 0) {
			System.out.println("Balance not updated");
			return "";
		}

		query = "Update account set balance= ? where accnum = ?";
		params = new ArrayList<String>();
		params.add(0, (curBalanceTo + amount) + "");
		params.add(1, accFrom + "");
		types = new ArrayList<String>();
		types.add(0, "double");
		types.add(1, "int");
		res = DBUtility.updateData(query, params, types, "ora1", "ora1");
		if (res == 0) {
			System.out.println("Balance not updated");
		}
		return "success";
	}
	/**
	 * Displays account info
	 */
	public static String displayAccounts() {

		String query = "Select distinct accnum, balance from account";
		List<String> params = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		Map<Integer, List<String>> map = DBUtility.selectData(query, params, types, "ora1", "ora1");

		for (int i = 1; i < map.keySet().size(); i++) {
			System.out.println("Account Number: " + map.get(i).get(0) + " Balance: " + map.get(i).get(1));
		}
		
		return "success";
	}
	/**
	 * Adds transactions to database
	 * @param trns
	 */
	public static String addTransactions(Transactions trns) {

		String query = "Select * from account where accnum= ?";
		List<String> params = new ArrayList<String>();
		params.add(trns.getAccountNum() + "");
		List<String> types = new ArrayList<String>();
		types.add("int");
		Map<Integer, List<String>> map = DBUtility.selectData(query, params, types, "ora1", "ora1");

		if (map.size() < 2) {
			System.out.println("Account num does not exist");
			return "Account num does not exist";
		}

		query = "Select typeid from types where type= ?";
		params = new ArrayList<String>();
		params.add(trns.getType());
		types = new ArrayList<String>();
		types.add("String");
		map = DBUtility.selectData(query, params, types, "ora1", "ora1");

		if (map.size() < 2) {
			System.out.println("Type Unknown");
			return "Type Unknown";
		}
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
		types.add(3, "date");
		types.add(4, "String");
		int res = DBUtility.updateData(query, params, types, "ora1", "ora1");
		if (res == 0) {
			System.out.println("Row not inserted");
			return "Row not inserted";
		}
		return "success";
	}
	/**https://github.com/navreet10/DaltonBank
	 * Adds accounts to database
	 * @param acc
	 */
	public static String addAccounts(Account acc) {
		String query = "Insert into account values(?,?,?,?,?)";
		List<String> params = new ArrayList<String>();
		params.add(0, acc.getName());
		params.add(1, acc.getBalance() + "");
		params.add(2, acc.getAccountNumber() + "");
		params.add(3, acc.getType());
		params.add(4, 0+"");
		List<String> types = new ArrayList<String>();
		types.add(0, "String");
		types.add(1, "double");
		types.add(2, "int");
		types.add(3, "String");
		types.add(4, "double");
		int res = DBUtility.updateData(query, params, types, "ora1", "ora1");
		if (res == 0) {
			System.out.println("Row not inserted");
			return "Row not inserted";
		}
		return "success";

	}
	/**
	 * Processes transactions
	 */
	public static String processTransactions() {
		String query = "Select * from transaction where status = 'New' order by curdate";
		List<String> params = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		Map<Integer, List<String>> map = DBUtility.selectData(query, params, types, "ora1", "ora1");
		String result = "success";
		for (int i = 1; i < map.keySet().size(); i++) {
			int accnum = Integer.parseInt(map.get(i).get(2));
			String type = checkType(Integer.parseInt(map.get(i).get(0)));
			float amount = Float.parseFloat(map.get(i).get(1));
			int trnsid = Integer.parseInt(map.get(i).get(5));
			query = "Select balance, name,feecharged from account where accnum= ?";
			params = new ArrayList<String>();
			params.add(accnum + "");
			types = new ArrayList<String>();
			types.add("int");
			Map<Integer, List<String>> map1 = DBUtility.selectData(query, params, types, "ora1", "ora1");

			float balance = Integer.parseInt(map1.get(1).get(0));
			String name = map1.get(1).get(1);
			float fee = 0f + Float.parseFloat(map1.get(1).get(2));

			float newBalance = 0.0f;
			if (type.equals("Deposit")) {
				newBalance = amount + balance;
			} else {
				newBalance = balance - amount;
				if (newBalance < 0) {
					boolean ret = checkSavingBalance(-newBalance + 35 + 15, accnum, name);
					int feeAmount = 0;
					if (!ret) {
						newBalance -= 35;
						feeAmount = 35;

					} else {

						newBalance = 0;
						feeAmount = 35 + 15;
					}
					// update fees charged
					query = "update account set feecharged =? where accnum =?";
					params = new ArrayList<String>();
					params.add((fee + feeAmount) + "");
					params.add(accnum + "");
					types = new ArrayList<String>();
					types.add("double");
					types.add("int");
					int res = DBUtility.updateData(query, params, types, "ora1", "ora1");
					if (res == 0) {
						System.out.println("Balance not updated");
						result = "Balance not updated";
					}
				}
			}

			query = "update account set balance =? where accnum =?";
			params = new ArrayList<String>();
			params.add(newBalance + "");
			params.add(accnum + "");
			types = new ArrayList<String>();
			types.add("double");
			types.add("int");
			int res = DBUtility.updateData(query, params, types, "ora1", "ora1");
			if (res == 0) {
				System.out.println("Balance not updated");
				result = "Balance not updated";
			}

			query = "update transaction set status ='Completed' where transactionID = " + trnsid;
			params = new ArrayList<String>();
			types = new ArrayList<String>();
			res = DBUtility.updateData(query, params, types, "ora1", "ora1");
			if (res == 0) {
				System.out.println("Balance not updated");
				result = "Balance not updated";
			}

		}
		return result;
	}
	/**
	 * Checks if saving account exist with sufficient balance
	 * @param newBalance
	 * @param accnum
	 * @param name
	 * @return result in boolean
	 */
	public static boolean checkSavingBalance(float newBalance, int accnum, String name) {
		String query = "Select balance,accnum from account where type='S' and name=? and accnum != ?";
		List<String> params = new ArrayList<String>();
		params.add(0,name);
		params.add(1,accnum + "");
		boolean res = false;
		List<String> types = new ArrayList<String>();
		types.add(0,"String");
		types.add(1,"int");
		Map<Integer, List<String>> map = DBUtility.selectData(query, params, types, "ora1", "ora1");
		Map<Integer, Float> accs = new HashMap<Integer, Float>();
		float total = 0f;
		// System.out.println(map.get(1).get(0) + "-" + map.get(1).get(1));
		for (int i = 1; i < map.keySet().size(); i++) {
			if (Float.parseFloat(map.get(i).get(0)) > 0) {
				total += Float.parseFloat(map.get(i).get(0));

				if (total >= newBalance) {
					accs.put(Integer.parseInt(map.get(i).get(1)), (total - newBalance));
					// System.out.println(map.get(i).get(1) + "-" + (total -
					// newBalance));
					res = true;
					break;
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
/**
 * Updates balance for accounts
 * @param accs
 */
	public static void updateBalance(Map<Integer, Float> accs) {
		Set<Integer> acc = accs.keySet();
		for (int i : acc) {
			String query = "update account set balance = " + accs.get(i) + " where accnum = " + i;

			List<String> params = new ArrayList<String>();
			List<String> types = new ArrayList<String>();
			int res = DBUtility.updateData(query, params, types, "ora1", "ora1");
			if (res == 0) {
				System.out.println("Balance not updated for account");
			}
		}

	}

	/**
	 * Returns the type of transaction
	 * @param next transaction int
	 * @return Returns the type of transaction
	 */
	public static String checkType(int next) {
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
