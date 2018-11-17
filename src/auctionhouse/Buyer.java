package auctionhouse;

public class Buyer {
	private int byerId;
	private PersonalDetails personalDetails; // PersonalDetails class needs to be updated!
	private String messagingAddress;
	private String buyerAccount;
	private String buyerAuthorisation;
	
	public Buyer(int byerId, PersonalDetails personalDetails, String messagingAddress, String buyerAccount,
			String buyerAuthorisation) {
		super();
		this.byerId = byerId;
		this.personalDetails = personalDetails;
		this.messagingAddress = messagingAddress;
		this.buyerAccount = buyerAccount;
		this.buyerAuthorisation = buyerAuthorisation;
	}


	public int getByerId() {
		return byerId;
	}

	public PersonalDetails getPersonalDetails() {
		return personalDetails;
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
