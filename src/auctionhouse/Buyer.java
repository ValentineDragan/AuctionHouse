package auctionhouse;

public class Buyer {
	
	private String name;
	private String messagingAddress;
	private String buyerAccount;
	private String buyerAuthorisation;
	
	public Buyer(String name, String messagingAddress, String buyerAccount,
			String buyerAuthorisation) {
		this.name = name;
		this.messagingAddress = messagingAddress;
		this.buyerAccount = buyerAccount;
		this.buyerAuthorisation = buyerAuthorisation;
	}

	public String getName() {
		return name;
	}

	public String getMessagingAddress() {
		return messagingAddress;
	}

	public String getBuyerAccount() {
		return buyerAccount;
	}

	public String getBuyerAuthorisation() {
		return buyerAuthorisation;
	}
	
}
