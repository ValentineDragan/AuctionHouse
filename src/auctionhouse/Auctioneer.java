package auctionhouse;

import java.util.List;
import java.util.ArrayList;

public class Auctioneer {

	private List<Lot> openedLots = new ArrayList<Lot>();
	
	private String name;
	private String messagingAddress;
	
	public Auctioneer(String name, String messagingAddress) {
		super();
		this.name = name;
		this.messagingAddress = messagingAddress;
	}
	
	public String getName() {
		return name;
	}
	public String getMessagingAddress() {
		return messagingAddress;
	}
	
}
