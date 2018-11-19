package auctionhouse;

// Class to save information associtated with a seller person.
public class Seller {

	private String name;
	private String messagingAddress;
	private String sellerAccount;

	public Seller(String name, String messagingAddress, String sellerAccount) {
		this.name = name;
		this.messagingAddress = messagingAddress;
		this.sellerAccount = sellerAccount;
	}

	public String getName() {
		return name;
	}

	public String getMessagingAddress() {
		return messagingAddress;
	}

	public String getSellerAccount() {
		return sellerAccount;
	}
	
}
