package auctionhouse;

public class Auctioneer {
	
	private String name;
	private String messagingAddress;
	
	public Auctioneer(String name, String messagingAddress) {
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
