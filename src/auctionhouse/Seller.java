package auctionhouse;

import java.util.ArrayList;
import java.util.List;

public class Seller {

	private String name;
	private PersonalDetails personalDetails;
	private String messagingAddress;
	private String sellerAccount;
	
	private List<Lot> createdLots = new ArrayList<Lot>();
	
	public Seller(String name, String messagingAddress, String sellerAccount) {
		super();
		this.name = name;
		this.messagingAddress = messagingAddress;
		this.sellerAccount = sellerAccount;
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

	public String getSellerAccount() {
		return sellerAccount;
	}
	
	
}
