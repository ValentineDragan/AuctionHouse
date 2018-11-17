package auctionhouse;

public class Auctioneer {

	private int auctioneerId;
	private String messagingAddress;
	
	public Auctioneer(int auctioneerId, String messagingAddress) {
		super();
		this.auctioneerId = auctioneerId;
		this.messagingAddress = messagingAddress;
	}
	
	public int getAuctioneerId() {
		return auctioneerId;
	}
	public String getMessagingAddress() {
		return messagingAddress;
	}
}
