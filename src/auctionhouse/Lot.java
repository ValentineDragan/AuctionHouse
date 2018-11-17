package auctionhouse;

public class Lot {
	
	private CatalogueEntry catalogueEntry;
	private int lotId;
	private int sellerId;
	private Money reservePrice;
	private Money openingPrice;
	private int assignedAuctioneerId;
	private LotStatus lotStatus;
	private int highestBidderId;
	private Money highestBidAmount;
	
	public Lot(int lotId, String lotDescription, int sellerId, Money reservePrice) {
		super();
		this.lotId = lotId;
		this.sellerId = sellerId;
		this.reservePrice = reservePrice;
		this.lotStatus = LotStatus.UNSOLD;
		this.catalogueEntry = new CatalogueEntry(lotId, lotDescription, lotStatus);
	}
	
	public CatalogueEntry getCatalogueEntry() {
		return catalogueEntry;
	}


	public int getLotId() {
		return lotId;
	}


	public int getSellerId() {
		return sellerId;
	}


	public Money getReservePrice() {
		return reservePrice;
	}


	public Money getOpeningPrice() {
		return openingPrice;
	}


	public int getAssignedAuctioneerId() {
		return assignedAuctioneerId;
	}


	public LotStatus getLotStatus() {
		return lotStatus;
	}


	public int getHighestBidderId() {
		return highestBidderId;
	}


	public Money getHighestBidAmount() {
		return highestBidAmount;
	}

}
