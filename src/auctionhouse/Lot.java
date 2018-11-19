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
	
	public List<String> getInterestedBuyers() {
		return interestedBuyerNames;
	}
	
	
	public Status addInterestedBuyer(String buyerName) {
		if(!interestedBuyerNames.contains(buyerName)) {
			interestedBuyerNames.add(buyerName);
			return new Status(Status.Kind.OK, "Buyer added to list of interested buyers");
		}
		else
			return new Status(Status.Kind.ERROR, "Buyer is already interested in the Lot");
	}
	
	public Status makeBid(String newBidderName, Money newBidAmount) {
		
		if(!interestedBuyerNames.contains(newBidderName)) {
			return new Status(Status.Kind.ERROR, "Buyer not interested in Lot");
		}
		
		if(highestBidAmount.compareTo(newBidAmount) < 0) {
			highestBidderName = newBidderName;
			highestBidAmount = newBidAmount;
			
			return new Status(Status.Kind.OK, "New bid successful");
		}
		
		return new Status(Status.Kind.ERROR, "Bid less than last highest bid");		
	}
	
	public Status openLot(String assignedAuctioneerName) {
		
		if(lotStatus != LotStatus.UNSOLD)
			return new Status(Status.Kind.ERROR, "Lot already sold");
		
		this.assignedAuctioneerName = assignedAuctioneerName;
		lotStatus = LotStatus.IN_AUCTION;
		highestBidderName = "";
		highestBidAmount = new Money("0");
		
		return new Status(Status.Kind.OK, "Bid open");
	}
	
	public Status closeLot() {
		if(highestBidAmount.compareTo(reservePrice) > 0) {
			lotStatus = LotStatus.SOLD_PENDING_PAYMENT;
			return new Status(Status.Kind.SALE_PENDING_PAYMENT);
		}
		
		lotStatus = LotStatus.UNSOLD;
		return new Status(Status.Kind.NO_SALE);
	}
	
	public void successfulSale() {
		lotStatus = LotStatus.SOLD;
	}

}
