package auctionhouse;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;

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
	
	private static Logger logger = Logger.getLogger("auctionhouse");
	
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
	
	/**
	 * Adds the name of a new Buyer to this lot's list of interestedBuyers
	 * 
	 * @param buyerName the name of the buyer
	 * @return Status ERROR if the buyerName is already in the list 
	 *         Status OK if the buyer name has been successfully added to the list
	 */
	public Status addInterestedBuyer(String buyerName) {
		if(!interestedBuyerNames.contains(buyerName)) {
			interestedBuyerNames.add(buyerName);
			
			logger.info("Buyer " + buyerName +  " added to Lot " + this.lotNumber + "'s list of interested buyers successfully");
			return new Status(Status.Kind.OK, "Buyer " + buyerName +  " added to Lot " + this.lotNumber + "'s list of interested buyers");
		}
		
		logger.info("Buyer " + buyerName + " is already interested in Lot " + this.lotNumber);
		return new Status(Status.Kind.ERROR, "Buyer " + buyerName + " is already interested in Lot " + this.lotNumber);
	}

	/**
	 * Updates the lot's highestBidderName and highestBidAmount
	 * 
	 * @param newBidderName the name of the buyer that has made a new bid
	 * @param newBidAmount the amount that this buyer has bid
	 * @return Status ERROR if the buyer's name is not in the list of interestedBuyers
	 *                      or if the newBidAmount is less than the previous highestBidAmount
	 *         Status OK if the new bid was successful
	 */
	public Status makeBid(String newBidderName, Money newBidAmount) {
		
		if(lotStatus != LotStatus.IN_AUCTION) {
			logger.warning("Lot with " + lotNumber + " is not open for auction");
			logger.warning("Make bid failed.");
			return new Status(Status.Kind.ERROR, "Lot with " + lotNumber + " is not open for auction");
		}
		
		if(!interestedBuyerNames.contains(newBidderName)) {
			logger.warning("Buyer " + newBidderName + " is not interested in Lot " + this.lotNumber);
			logger.warning("Make bid failed.");
			return new Status(Status.Kind.ERROR, "Buyer " + newBidderName + " is not interested in Lot " + this.lotNumber);
		}
		
		if(highestBidAmount.compareTo(newBidAmount) < 0) {
			highestBidderName = newBidderName;
			highestBidAmount = newBidAmount;
			
			logger.info("Successful bid");
			return new Status(Status.Kind.OK, "Buyer " + newBidderName + " has successfully bidded on Lot " + this.lotNumber);
		}
		
		logger.warning("Buyer " + newBidderName + " tried to bid less than the highest bid on Lot " + this.lotNumber);
		logger.warning("Make bid failed");
		return new Status(Status.Kind.ERROR, "Buyer " + newBidderName + " tried to bid less than the highest bid on Lot " + this.lotNumber);		
	}
	
	/**
	 * Sets the lot's lotStatus to IN_AUCTION, assigns it an auctioneer and initializes the highestBidderName and highestBidAmount variables
	 * 
	 * @param assignedAuctioneerName the name of the Auctioneer who is assigned to this lot
	 * @return Status ERROR if the lot has already been auctioned
	 *         Status OK if the lot has been successfully opened for auction
	 */
	public Status openLot(String assignedAuctioneerName) {
		
		if(lotStatus != LotStatus.UNSOLD) {
			logger.warning("Lot " + this.lotNumber + ", which has already been auctioned");
			logger.warning("Open lot failed");
			return new Status(Status.Kind.ERROR, "Auctioneer " + assignedAuctioneerName + " tried to open Lot " + this.lotNumber + ", which has already been auctioned");
		}		
		
		this.assignedAuctioneerName = assignedAuctioneerName;
		lotStatus = LotStatus.IN_AUCTION;
		this.catalogueEntry.status = lotStatus;
		highestBidderName = "";
		highestBidAmount = new Money("0");
		
		logger.warning("Open lot successful");
		return new Status(Status.Kind.OK, assignedAuctioneerName + " has opened Lot " + this.lotNumber + " for bidding");
	}
	
	public void closeLot(LotStatus lotStatus) {
		logger.info("Lot status changed to " + lotStatus);
		this.lotStatus = lotStatus;
		catalogueEntry.status = lotStatus;
	}

}
