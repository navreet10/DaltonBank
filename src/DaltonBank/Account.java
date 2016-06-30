package DaltonBank;

public class Account {
	// attributes
	private int accountNumber;
	private String name;
	private float balance;
	private String type;

	// getters and setters
	
	public int getAccountNumber() {
		return accountNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setAccountNumber(int accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

}
