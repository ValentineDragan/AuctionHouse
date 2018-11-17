package auctionhouse;

public class Seller {

	private int sellerId;
	private PersonalDetails personalDetails;
	private String messagingAddress;
	private String sellerAccount;
	
	public Seller(int sellerId, PersonalDetails personalDetails, String messagingAddress, String sellerAccount) {
		super();
		this.sellerId = sellerId;
		this.personalDetails = personalDetails;
		this.messagingAddress = messagingAddress;
		this.sellerAccount = sellerAccount;
	}

	public int getSellerId() {
		return sellerId;
	}

	public PersonalDetails getPersonalDetails() {
		return personalDetails;
	}

	public String getMessagingAddress() {
		return messagingAddress;
	}

	public String getSellerAccount() {
		return sellerAccount;
	}
	
	
}
