package auctionhouse;

import java.util.List;
import java.util.ArrayList;

public class Lot {
	
	private CatalogueEntry catalogueEntry;
	private int lotNumber;
	private String sellerName;
	private Money reservePrice;
	private Money openingPrice;
	private String assignedAuctioneerName;
	private LotStatus lotStatus;
	private String highestBidderName;
	private Money highestBidAmount;
	
	private List<String> interestedBuyerNames =  new ArrayList<String>();
	
	public Lot(String sellerName, int lotNumber, String description, Money reservePrice) {

		this.sellerName = sellerName;
		this.lotNumber = lotNumber;
		this.reservePrice = reservePrice;
		this.lotStatus = LotStatus.UNSOLD;
		this.catalogueEntry = new CatalogueEntry(lotNumber, description, lotStatus);
	}
	
	public CatalogueEntry getCatalogueEntry() {
		return catalogueEntry;
	}

	public int getLotNumber() {
		return lotNumber;
	}

	public String getSellerName() {
		return sellerName;
	}

	public Money getReservePrice() {
		return reservePrice;
	}

	public Money getOpeningPrice() {
		return openingPrice;
	}

	public String getAssignedAuctioneerName() {
		return assignedAuctioneerName;
	}

	public LotStatus getLotStatus() {
		return lotStatus;
	}

	public String getHighestBidderName() {
		return highestBidderName;
	}

	public Money getHighestBidAmount() {
		return highestBidAmount;
	}
	
	public void addInterestedBuyer(String buyerName) {
		interestedBuyerNames.add(buyerName);
	}
	
	public Status makeBid(String newBidderName, Money newBidAmount) {
		
		if(highestBidAmount.compareTo(newBidAmount) < 0) {
			highestBidderName = newBidderName;
			highestBidAmount = newBidAmount;
			
			return new Status(Status.Kind.OK, "New bid successful");
		}
		
		return new Status(Status.Kind.ERROR, "Bid less than last highest bid");		
	}

}
