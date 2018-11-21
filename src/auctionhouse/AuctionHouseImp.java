/**
 * 
 */
package auctionhouse;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Logger;
import java.util.HashMap;

/**
 * @author pbj
 *
 */
public class AuctionHouseImp implements AuctionHouse {
	
	private HashMap<String, Buyer> buyers = new HashMap<String, Buyer>();
	private HashMap<String, Seller> sellers = new HashMap<String, Seller>();
	private HashMap<String, Auctioneer> auctioneers = new HashMap<String, Auctioneer>();
	private HashMap<Integer, Lot> lots = new HashMap<Integer, Lot>();
	private PriorityQueue<CatalogueEntry> catalogueEntries = new PriorityQueue<CatalogueEntry>(new CatalogueEntryComparator());
	private Parameters parameters;
	
	private MessagingService messagingService; 
	private BankingService bankingService; 
	
    private static Logger logger = Logger.getLogger("auctionhouse");
    private static final String LS = System.lineSeparator();
    
    private String startBanner(String messageName) {
        return  LS 
          + "-------------------------------------------------------------" + LS
          + "MESSAGE IN: " + messageName + LS
          + "-------------------------------------------------------------";
    }
   
    
    public AuctionHouseImp(Parameters parameters) {
    	this.parameters = parameters;
    	messagingService = parameters.messagingService;
    	bankingService = parameters.bankingService;
    }
    

    /**
     * Creates a new Buyer object, and adds it to the buyers HashMap.
     * @param name: name of buyer to register
     * @param address: messaging address 
     * @param bankAccount
     * @param bankAuthCode: bank authorisation for payments
     * @return Status 'Error' if the Buyer already exists, or 'OK' if the new Buyer has been successfully created
     */
    public Status registerBuyer(
            String name,
            String address,
            String bankAccount,
            String bankAuthCode) {
        logger.fine(startBanner("registerBuyer " + name));
        
        logger.info("Entering...");
        // Check input is valid
        if(!checkStringValid(name)) {
        	logger.warning("Buyer name given to registerBuyer cannot be null or empty");
        	logger.warning("Buyer registration failed. Exiting." + LS);   
        	return Status.error("Buyer name given to registerBuyer cannot be null or empty");
        }
        
        if(!checkStringValid(address)) {
        	logger.warning("Address given to registerBuyer cannot be null or empty");  
           	logger.warning("Buyer registration failed. Exiting." + LS);  
        	return Status.error("Address given to registerBuyer cannot be null or empty");
        }
        
        if(!checkStringValid(bankAccount)) {
        	logger.warning("Bank account given to registerBuyer cannot be null or empty");  
           	logger.warning("Buyer registration failed. Exiting." + LS);  
        	return Status.error("Bank account given to registerBuyer cannot be null or empty");
        }
        
        if(!checkStringValid(bankAuthCode)) {
           	logger.warning("Bank authorisation code given to registerBuyer cannot be null or empty");  
           	logger.warning("Buyer registration failed. Exiting." + LS);  
        	return Status.error("Bank authorisation code given to registerBuyer cannot be null or empty");
        }
        
        if(buyers.get(name) != null) {
        	logger.warning("Name " + name + " exists as buyer already");
           	logger.warning("Buyer registration failed. Exiting." + LS);  
        	return Status.error("Name " + name + " exists as buyer already");
        }
        
        // Create new buyer object and add it to map
        Buyer buyer = new Buyer(name, address, bankAccount, bankAuthCode);
        buyers.put(name, buyer);
        
       	logger.info("Buyer registered successfully. Exiting." + LS);  
        return Status.OK();
    }

    /**
     * Creates a new Seller object, and adds it to the sellers HashMap.
     * @param name: name of seller to register
     * @param address: messaging address
     * @param bankAccount
     * @return Status 'Error' if the Seller already exists, or 'OK' if the new Seller has been created.
     */
    public Status registerSeller(
            String name,
            String address,
            String bankAccount) {
        logger.fine(startBanner("registerSeller " + name));
        
        logger.info("Entering...");        
        // Check input is valid
        if(!checkStringValid(name)) {
        	logger.warning("Seller name given to registerSeller cannot be null or empty");
        	logger.warning("Seller registration failed. Exiting." + LS);   
        	return Status.error("Seller name given to registerSeller cannot be null or empty");
        }
        
        if(!checkStringValid(address)) {
        	logger.warning("Address given to registerSeller cannot be null or empty");
        	logger.warning("Seller registration failed. Exiting." + LS);   
        	return Status.error("Address given to registerSeller cannot be null or empty");
        }
        
        if(!checkStringValid(bankAccount)) {
        	logger.warning("Bank account given to registerSeller cannot be null or empty");
        	logger.warning("Seller registration failed. Exiting." + LS);   
        	return Status.error("Bank account given to registerSeller cannot be null or empty");
        }
        
        if(sellers.get(name) != null) {
        	logger.warning("Name " + name + " exists as seller already");
        	logger.warning("Seller registration failed. Exiting." + LS);   
        	return Status.error("Name " + name + " exists as seller already");
        }
        
        // Create new seller object and put it to map
        Seller seller = new Seller(name, address, bankAccount);
        sellers.put(name, seller);
        
        logger.info("Seller registered successfully. Exiting." + LS);        
        return Status.OK();      
    }

    /**
     * Creates a new Lot object, adds it to the lots HashMap and adds its CatalogueEntry to the catalogueEntries Queue.
     * @param sellerName
     * @param number: unique lot number
     * @param description
     * @param reservePrice
     * @return Status: 'Error' if the Lot already exists, or 'OK' if the new Lot has been created.
     */
    public Status addLot(
            String sellerName,
            int number,
            String description,
            Money reservePrice) {
        logger.fine(startBanner("addLot " + sellerName + " " + number));
        
        // Check input is valid
        logger.info("Entering...");
        if(!checkStringValid(sellerName)) {
        	logger.warning("Seller name in addLot cannot be null or empty");
        	logger.warning("Adding lot failed. Exiting." + LS);
        	return Status.error("Seller name in addLot cannot be null or empty");
        }
        
        if(sellers.get(sellerName) == null) {
        	logger.warning("Seller with " + sellerName + " does not exist in the System");
        	logger.warning("Adding lot failed. Exiting." + LS);
        	return Status.error("Seller with " + sellerName + " does not exist in the System");
        }
                
        if(lots.get(number) != null) {
        	logger.warning("Lot with number " + number + " already exists");
        	logger.warning("Adding lot failed. Exiting." + LS);
        	return Status.error("Lot with number " + number + " already exists");
        }
        
        if(!checkStringValid(description)) {
        	logger.warning("Lot description in addLot cannot pe null or empty");
        	logger.warning("Adding lot failed. Exiting." + LS);
        	return Status.error("Lot description in addLot cannot pe null or empty");
        }
        
        if(reservePrice == null || reservePrice.lessEqual(new Money("0"))) {
        	logger.warning("reservePrice cannot be null or of negative value in addLot");
        	logger.warning("Adding lot failed. Exiting." + LS);
        	return Status.error("reservePrice cannot be null or of negative value in addLot");
        }
                
        // Create new lot object and put it to map.
        Lot lot = new Lot(sellerName, number, description, reservePrice);
        lots.put(number, lot);

        // Add the corresponding catalogue entry in the priority queue of catalogue entries.
        catalogueEntries.add(lot.getCatalogueEntry());
        
        logger.info("Lot added successfully.Exiting." + LS);
        return Status.OK();    
    }

    /**
     * @return a List of Catalogue Entries, ordered by their lotNumber.
     */
    public List<CatalogueEntry> viewCatalogue() {
        logger.fine(startBanner("viewCatalog"));
        
        logger.info("Entering...");
        logger.fine("Catalogue: " + catalogueEntries.toString());
        
        // Put every entry in the priority queue in a list (order is preserved)
        List<CatalogueEntry> catalogueList = new ArrayList<CatalogueEntry>();
        for(CatalogueEntry ce: catalogueEntries) {
        	catalogueList.add(ce);
        }
        
        logger.info("Exiting." + LS);
        return catalogueList;
    }

    /**
     * Sends a signal to the Lot to add new interested buyer.
     * @return Status: 'Error' if the Lot or Buyer doesn't exist, or 'OK' if the buyer has been added successfully.
     */
    public Status noteInterest(
            String buyerName,
            int lotNumber) {
        logger.fine(startBanner("noteInterest " + buyerName + " " + lotNumber));
        
        logger.info("Entering...");
        if(!checkStringValid(buyerName)) {
        	logger.warning("Buyer name in noteInterest cannot be null or empty");
        	logger.warning("Noting interest failed. Exiting." + LS);
        	return Status.error("Buyer name in noteInterest cannot be null or empty");
        }
        
        // Check Buyer with buyerName registered in the system.
        if(buyers.get(buyerName) == null) {
        	logger.warning("Buyer with name " + buyerName + " not registered");
        	logger.warning("Noting interest failed. Exiting." + LS);       	
        	return Status.error("Buyer with name " + buyerName + " not registered");
        }
        
        Lot lot = lots.get(lotNumber);
        if(lot == null) {
        	logger.warning("Noting interest failed. Exiting." + LS);
        	logger.warning("Noting interest failed. Exiting." + LS);
        	return Status.error("Lot with number " + lotNumber + " does not exist");
        }
                    
        logger.info("Interest noted successfully.Exiting");
        return lot.addInterestedBuyer(buyerName);   
    }

    /**
     * Opens lot auction. Gets the list of all interested buyers
     * and uses messagingService to send a message to all the interested buyers.
     * @param auctioneerName
     * @param auctioneerAddress
     * @param lotNumber
     * @return Status: ERROR if lot does not exists or not in UNOPENED state.
     */
    public Status openAuction(
            String auctioneerName,
            String auctioneerAddress,
            int lotNumber) {
        logger.fine(startBanner("openAuction " + auctioneerName + " " + lotNumber));
        
        logger.info("Entering...");
        if(!checkStringValid(auctioneerName)) {
        	logger.warning("Auctioneer name in openAuction cannot be null or empty");
        	logger.warning("Opening action failed. Exiting." + LS);
        	return Status.error("Auctioneer name in openAuction cannot be null or empty");
        }
        
        if(!checkStringValid(auctioneerAddress)) {
        	logger.warning("Auctioneer address in openAuction cannot be null or empty");
        	logger.warning("Opening action failed. Exiting." + LS);
        	return Status.error("Auctioneer address in openAuction cannot be null or empty");
        }
        
        Lot lot = lots.get(lotNumber);
        if(lot == null) {
        	logger.warning("Lot with number " + lotNumber + " does not exist");
        	logger.warning("Opening action failed. Exiting." + LS);
        	return Status.error("Lot with number " + lotNumber + " does not exist");
        }
        
        // Create the auctioneer object if it does not exists already
        Auctioneer auctioneer;
        if(auctioneers.get(auctioneerName) != null) {
        	auctioneer = auctioneers.get(auctioneerName);
        	logger.info("New auctioneer registered successfully.");
        } else {
        	auctioneer = new Auctioneer(auctioneerName, auctioneerAddress);
        	auctioneers.put(auctioneerName, auctioneer);
        }
        
        Status status = lot.openLot(auctioneerName);
        // Status OK if lot in UNOPENED state
        if(status.kind == Status.Kind.OK) {
        	
        	// Messege seller and interested buyers.
        	logger.info("Messaging seller...");
        	Seller seller = sellers.get(lot.getSellerName());
        	messagingService.auctionOpened(seller.getMessagingAddress(), lotNumber);
        	
        	logger.info("Messaging interested buyers...");
        	List<String> interestedBuyers = lot.getInterestedBuyers();
        	sendMessageToBuyers(interestedBuyers, MessageFlag.AUCTION_OPENED, lotNumber, null);
        	logger.info("Auction opened successfully.");
        }
    	        
        logger.info("Exiting." + LS);
        return status;
    }

    /**
     * Update lot with new bid.
     * @param buyerName: bidder
     * @param lotNumber: number identification for Lot
     * @return Status: ERROR is bid less than the current highest or buyer not interested in lot
     */
    public Status makeBid(
            String buyerName,
            int lotNumber,
            Money bid) {
        logger.fine(startBanner("makeBid " + buyerName + " " + lotNumber + " " + bid));

        logger.info("Entering...");
        if(!checkStringValid(buyerName)) {
        	logger.warning("Buyer name in makeBid cannot be null or empty.");
        	logger.warning("Make bid failed. Exiting");
        	return Status.error("Buyer name in makeBid cannot be null or empty.");
        }
        
        if(buyers.get(buyerName) == null) {
        	logger.warning("Buyer with name " + buyerName + " not registered with the System");
           	logger.warning("Make bid failed. Exiting");
        	return Status.error("Buyer with name " + buyerName + " not registered with the System");
        }
        
        Lot lotToBid = lots.get(lotNumber);
        if(lotToBid == null) {
        	logger.warning("Lot with number " + lotNumber + " does not exists in the System");
           	logger.warning("Make bid failed. Exiting");
        	return Status.error("Lot with number " + lotNumber + " does not exists in the System");
        }
        
        if(bid == null || bid.lessEqual(new Money("0"))) {
        	logger.warning("Bid value cannot be negative in makeBid");
           	logger.warning("Make bid failed. Exiting");
        	return Status.error("Bid value cannot be negative in makeBid");
        }
        
        if (lotToBid.getLotStatus() != LotStatus.IN_AUCTION) {
        	logger.warning("Bid cannot be made when the lot is not in auction");
           	logger.warning("Make bid failed. Exiting");
        	return Status.error("Bid cannot be made when the lot is not in auction");
        }
        
       if (bid.subtract(lotToBid.getHighestBidAmount()).compareTo(parameters.increment) < 0) {
    	   logger.warning("Bid difference cannot be less than the increment bid");
    	   logger.warning("Make bid failed. Exiting");
    	   return Status.error("Bid difference cannot be less than the increment bid");
       }
        
        Status status = lotToBid.makeBid(buyerName, bid);
        
        // Message auctioneer, interested buyers, seller
        if(status.kind == Status.Kind.OK) {
        	
        	logger.info("Messaging auctioneer...");
        	Auctioneer auctioneer = auctioneers.get(lotToBid.getAssignedAuctioneerName());
        	messagingService.bidAccepted(auctioneer.getMessagingAddress(), lotNumber, bid);
        	
        	logger.info("Messaging interested buyers...");
        	List<String> interestedBuyers = lotToBid.getInterestedBuyers();
        	// Do not message current bidder.
        	List<String> buyersToMessage = new ArrayList<String>(interestedBuyers);
        	buyersToMessage.remove(buyerName);
        	sendMessageToBuyers(buyersToMessage, MessageFlag.BID_ACCEPTED, lotNumber, bid);      	    
        	
        	logger.info("Messaging seller...");
        	Seller seller = sellers.get(lotToBid.getSellerName());
        	messagingService.bidAccepted(seller.getMessagingAddress(), lotNumber, bid);
        	
        	logger.info("Bid made successfully");        	
        }
        
        logger.info("Exiting." + LS);
        return status;    
    }

    /**
     * Close lot auction. Change lot state, send messages to buyers, sellers. 
     * Make bank transfers
     * @param auctioneerName: must be the same as the auctioneer the opened the auction for the lot
     * @param lotNumber
     * @return Status
     */
    public Status closeAuction(
            String auctioneerName,
            int lotNumber) {
        logger.fine(startBanner("closeAuction " + auctioneerName + " " + lotNumber));
 
        logger.info("Entering...");
        if(!checkStringValid(auctioneerName)) {
        	logger.warning("Auctioneer name in closeAuction cannot be null or empty");
        	logger.warning("Close auction failed. Exiting." + LS);
        	return Status.error("Auctioneer name in closeAuction cannot be null or empty");
        }
        
        Lot lot = lots.get(lotNumber);
        
        if(lot == null) {
        	logger.warning("Lot with number " + lotNumber + " does not exist");
        	logger.warning("Close auction failed. Exiting." + LS);
        	return Status.error("Lot with number " + lotNumber + " does not exist");
        }
        
        if (lot.getLotStatus() != LotStatus.IN_AUCTION) {
        	logger.warning("Lot with number " + lotNumber + " was not in open auction");
        	logger.warning("Close auction failed. Exiting." + LS);
        	return Status.error("Lot with number " + lotNumber + " was not in open auction");
        }
        
        if(!lot.getAssignedAuctioneerName().equals(auctioneerName)) {
        	logger.warning("Lot auction must be closed by auctioneer that opened it!");
        	logger.warning("Close auction failed. Exiting." + LS);
        	return Status.error("Lot auction must be closed by auctioneer that opened it!");
        }   

        Buyer highestBidder = buyers.get(lot.getHighestBidderName());
		Seller seller = sellers.get(lot.getSellerName());
		Money hammerPrice = lot.getHighestBidAmount();

		// reservePrice not reached, lot not sold
        if (!lot.getReservePrice().lessEqual(hammerPrice)) {       	
        	lot.closeLot(LotStatus.UNSOLD);
        	
			logger.info("Lot " + lotNumber + " was not sold. Hammer price less than reserve price");
			logger.info("Messaging buyers and seller...");
			
			sendMessageToBuyers(lot.getInterestedBuyers(), MessageFlag.LOT_UNSOLD, lotNumber, null);
			messagingService.lotUnsold(seller.getMessagingAddress(), lotNumber);
			
			logger.info("Auction closed. Exiting." + LS);
			return new Status(Status.Kind.NO_SALE, "Lot " + lotNumber + " was not sold. Hammer price less than reserve price");
        }	
        
		Money moneyToCollectFromBuyer = hammerPrice.addPercent(parameters.buyerPremium);

		// Check Buyer -> AuctionHouse transfer was successful before doing AuctionHouse -> seller transfer
		Status buyerTransferStatus = bankingService.transfer(highestBidder.getBuyerAccount(), highestBidder.getBuyerAuthorisation(), parameters.houseBankAccount, moneyToCollectFromBuyer);
		if(buyerTransferStatus.kind == Status.Kind.OK) {		
			
			Money moneyToPaySeller = hammerPrice.subtract(new Money(Double.toString(parameters.commission)));
			Status sellerTransferStatus = bankingService.transfer(parameters.houseBankAccount, parameters.houseBankAuthCode, seller.getSellerAccount(), moneyToPaySeller);
			// Successful transfers, lot sold.
			if(sellerTransferStatus.kind == Status.Kind.OK) {
				lot.closeLot(LotStatus.SOLD);
				
				logger.info("Successful sale for lot " + lotNumber);
				logger.info("Messaging buyers and sellers...");
				
				sendMessageToBuyers(lot.getInterestedBuyers(), MessageFlag.LOT_SOLD, lotNumber, new Money("0"));
				messagingService.lotSold(seller.getMessagingAddress(), lotNumber);
				
				logger.info("Auction closed. Exiting." + LS);
				return new Status(Status.Kind.SALE, "Successful sale for lot " + lotNumber);
			} 			
			logger.warning( "Bank transfer from Auction house to Seller failed for lot " + lotNumber);
		} else {
			logger.warning( "Bank transfer from Buyer to Auction House failed for lot " + lotNumber);
		}		
		
		// One of the transfers failed.
		lot.closeLot(LotStatus.SOLD_PENDING_PAYMENT);
		
		logger.info("Auction closed. Exiting." + LS);
		return new Status(Status.Kind.SALE_PENDING_PAYMENT, "One of the bank transfers failed for lot " + lotNumber);			        
    }
        		
    // Check a string is not null or empty.
    private boolean checkStringValid(String string) {
    	
    	if(string == null)
    		return false;
    	if(string.length() == 0)
    		return false;
    	
    	return true;
    }
    
    private void sendMessageToBuyers(List<String> buyerNames, MessageFlag flagType, int lotNumber, Money amount) {
    	if (flagType.equals(MessageFlag.LOT_SOLD)) {
    		for (String str: buyerNames) {
    			Buyer buyer = buyers.get(str);
    			messagingService.lotSold(buyer.getMessagingAddress(), lotNumber);
    		}
    	}
    	else if (flagType.equals(MessageFlag.LOT_UNSOLD)) {
    		for (String str: buyerNames) {
    			Buyer buyer = buyers.get(str);
    			messagingService.lotUnsold(buyer.getMessagingAddress(), lotNumber);
    		}
    	}
    	else if (flagType.equals(MessageFlag.BID_ACCEPTED)) {
    		for (String str: buyerNames) {
    			Buyer buyer = buyers.get(str);
    			messagingService.bidAccepted(buyer.getMessagingAddress(), lotNumber, amount);
    		}
    	}
    	else if (flagType.equals(MessageFlag.AUCTION_OPENED)) {
    		for (String str: buyerNames) {
    			Buyer buyer = buyers.get(str);
    			messagingService.auctionOpened(buyer.getMessagingAddress(), lotNumber);
    		}
    	}
    }
    
    
}
