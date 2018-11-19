package auctionhouse;

import java.util.ArrayList;
import java.util.List;

public class Buyer {
	
	private String name;
	private PersonalDetails personalDetails; // PersonalDetails class needs to be updated!
	private String messagingAddress;
	private String buyerAccount;
	private String buyerAuthorisation;
	
	private List<Lot> biddedLots = new ArrayList<Lot>();
	private List<Lot> interestedLots = new ArrayList<Lot>();
	
	public Buyer(String name, String messagingAddress, String buyerAccount,
			String buyerAuthorisation) {
		super();
		this.name = name;
		this.messagingAddress = messagingAddress;
		this.buyerAccount = buyerAccount;
		this.buyerAuthorisation = buyerAuthorisation;
	}


	public String getName() {
		return name;
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
