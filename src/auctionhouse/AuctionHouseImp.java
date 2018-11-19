/**
 * 
 */
package auctionhouse;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Logger;
import java.util.Map;
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
	private PriorityQueue<CatalogueEntry> catalogueEntries = new PriorityQueue<CatalogueEntry>();
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
        
        // Check input is valid
        if(!checkStringValid(name)) {
        	return Status.error("Buyer name given to registerBuyer cannot be null or empty");
        }
        
        if(!checkStringValid(address)) {
        	return Status.error("Address given to registerBuyer cannot be null or empty");
        }
        
        if(!checkStringValid(bankAccount)) {
        	return Status.error("Bank account given to registerBuyer cannot be null or empty");
        }
        
        if(!checkStringValid(bankAuthCode)) {
        	return Status.error("Bank authorisation code given to registerBuyer cannot be null or empty");
        }
        
        if(buyers.get(name) != null) {
        	return Status.error("Name " + name + " exists as buyer already");
        }
        
        // Create new buyer object and add it to map
        Buyer buyer = new Buyer(name, address, bankAccount, bankAuthCode);
        buyers.put(name, buyer);
        
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
        
        // Check input is valid
        if(!checkStringValid(name)) {
        	return Status.error("Seller name given to registerSeller cannot be null or empty");
        }
        
        if(!checkStringValid(address)) {
        	return Status.error("Address given to registerSeller cannot be null or empty");
        }
        
        if(!checkStringValid(bankAccount)) {
        	return Status.error("Bank account given to registerSeller cannot be null or empty");
        }
        
        if(sellers.get(name) != null) {
        	return Status.error("Name " + name + " exists as seller already");
        }
        
        // Create new seller object and put it to map
        Seller seller = new Seller(name, address, bankAccount);
        sellers.put(name, seller);
        
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
        if(!checkStringValid(sellerName)) {
        	return Status.error("Seller name in addLot cannot be null or empty");
        }
                
        if(lots.get(number) != null) {
        	return Status.error("Lot with number " + number + " already exists");
        }
        
        if(!checkStringValid(description)) {
        	return Status.error("Lot description in addLot cannot pe null or empty");
        }
        
        if(reservePrice == null || reservePrice.lessEqual(new Money("0"))) {
        	return Status.error("reservePrice cannot be null or of negative value in addLot");
        }
                
        // Create new lot object and put it to map.
        Lot lot = new Lot(sellerName, number, description, reservePrice);
        lots.put(number, lot);

        // Add the corresponding catalogue entry in the priority queue of catalogue entries.
        catalogueEntries.add(lot.getCatalogueEntry());
        
        return Status.OK();    
    }

    /**
     * @return a List of Catalogue Entries, ordered by their lotNumber.
     */
    public List<CatalogueEntry> viewCatalogue() {
        logger.fine(startBanner("viewCatalog"));
        
        logger.fine("Catalogue: " + catalogueEntries.toString());
        
        // Put every entry in the priority queue in a list (order is preserved)
        List<CatalogueEntry> catalogueList = new ArrayList<CatalogueEntry>();
        for(CatalogueEntry ce: catalogueEntries) {
        	catalogueList.add(ce);
        }
        
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
        
        if(!checkStringValid(buyerName)) {
        	return Status.error("Buyer name in noteInterest cannot be null or empty");
        }
        
        // Check Buyer with buyerName registered in the system.
        if(buyers.get(buyerName) == null) {
        	return Status.error("Buyer with name " + buyerName + " not registered");
        }
        
        Lot lot = lots.get(lotNumber);
        if(lot == null) {
        	return Status.error("Lot with number " + lotNumber + " does not exist");
        }
                    
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
        
        if(!checkStringValid(auctioneerName)) {
        	return Status.error("Auctioneer name in openAuction cannot be null or empty");
        }
        
        if(!checkStringValid(auctioneerAddress)) {
        	return Status.error("Auctioneer address in openAuction cannot be null or empty");
        }
        
        Lot lot = lots.get(lotNumber);
        if(lot == null) {
        	return Status.error("Lot with number " + lotNumber + " does not exist");
        }
        
        // Create the auctioneer object if it does not exists already
        Auctioneer auctioneer;
        if(auctioneers.get(auctioneerName) != null) {
        	auctioneer = auctioneers.get(auctioneerName);
        } else {
        	auctioneer = new Auctioneer(auctioneerName, auctioneerAddress);
        	auctioneers.put(auctioneerName, auctioneer);
        }
        
        Status status = lot.openLot(auctioneerName);
        // !!!!
        if(status.kind == Status.Kind.OK) {
        	
        	List<String> interestedBuyers = lot.getInterestedBuyers();
        	
        	Seller seller = sellers.get(lot.getSellerName());
        	messagingService.auctionOpened(seller.getMessagingAddress(), lotNumber);
        	
        	for(String buyerName: interestedBuyers) {
        		Buyer interestedBuyer = buyers.get(buyerName);
        		messagingService.auctionOpened(interestedBuyer.getMessagingAddress(), lotNumber);
        	}  
        }
    	         
        return status;
    }

    
    public Status makeBid(
            String buyerName,
            int lotNumber,
            Money bid) {
        logger.fine(startBanner("makeBid " + buyerName + " " + lotNumber + " " + bid));

        if(buyers.get(buyerName) == null) {
        	return new Status(Status.Kind.ERROR, "Buyer not registered");
        }
        
        Lot lotToBid = lots.get(lotNumber);
        if(lotToBid == null) {
        	return Status.error("Lot does not exist");
        }
        
        Status status = lotToBid.makeBid(buyerName, bid);
        
        if(status.kind == Status.Kind.OK) {
        	
        	Auctioneer auctioneer = auctioneers.get(lotToBid.getAssignedAuctioneerName());
        	messagingService.bidAccepted(auctioneer.getMessagingAddress(), lotNumber, bid);
        	
        	for(String str: lotToBid.getInterestedBuyers()) {
        		if(str != buyerName) {
        			Buyer buyer = buyers.get(str);
        			messagingService.bidAccepted(buyer.getMessagingAddress(), lotNumber, bid);
        		}
        	}       	
        	
        	Seller seller = sellers.get(lotToBid.getSellerName());
        	messagingService.bidAccepted(seller.getMessagingAddress(), lotNumber, bid);
        	
        }
        
        return status;    
    }

    private void sendMessageToBuyers(List<String> buyerNames, String flagType, int lotNumber, Money amount) {
    	if (flagType.equals("LOT SOLD")) {
    		for (String str: buyerNames) {
    			Buyer buyer = buyers.get(str);
    			messagingService.lotSold(buyer.getMessagingAddress(), lotNumber);
    		}
    	}
    	else if (flagType.equals("LOT UNSOLD")) {
    		for (String str: buyerNames) {
    			Buyer buyer = buyers.get(str);
    			messagingService.lotUnsold(buyer.getMessagingAddress(), lotNumber);
    		}
    	}
    	else if (flagType.equals("BID ACCEPTED")) {
    		for (String str: buyerNames) {
    			Buyer buyer = buyers.get(str);
    			messagingService.bidAccepted(buyer.getMessagingAddress(), lotNumber, amount);
    		}
    	}
    	else if (flagType.equals("AUCTION OPENED")) {
    		for (String str: buyerNames) {
    			Buyer buyer = buyers.get(str);
    			messagingService.auctionOpened(buyer.getMessagingAddress(), lotNumber);
    		}
    	}
    }
    
    // check same auctioneer
    public Status closeAuction(
            String auctioneerName,
            int lotNumber) {
        logger.fine(startBanner("closeAuction " + auctioneerName + " " + lotNumber));
 
        Lot lot = lots.get(lotNumber);
        LotStatus lotStatus;
        
        if(lot != null) {
        	if (lot.getLotStatus() == LotStatus.IN_AUCTION) {
        		//
    			Buyer highestBidder = buyers.get(lot.getHighestBidderName());
    			Seller seller = sellers.get(lot.getSellerName());
    			Money hammerPrice = lot.getHighestBidAmount();
        		
    			//
        		if (lot.getReservePrice().lessEqual(hammerPrice)) {			
        			Money moneyToCollect = hammerPrice.addPercent(parameters.buyerPremium);
        			Money moneyToPay = hammerPrice.subtract(new Money(Double.toString(parameters.commission)));
        			Status buyerTransferStatus = bankingService.transfer(highestBidder.getBuyerAccount(), highestBidder.getBuyerAuthorisation(), parameters.houseBankAccount, moneyToCollect);
        			Status sellerTransferStatus = bankingService.transfer(parameters.houseBankAccount, parameters.houseBankAuthCode, seller.getSellerAccount(), moneyToPay);
        		
        			//
        			if(buyerTransferStatus.kind == Status.Kind.OK && sellerTransferStatus.kind == Status.Kind.OK) {
        				lotStatus = LotStatus.SOLD;
        				lot.closeLot(lotStatus);
        				sendMessageToBuyers(lot.getInterestedBuyers(), "LOT SOLD", lotNumber, new Money("0"));
        				messagingService.lotSold(seller.getMessagingAddress(), lotNumber);
        				return new Status(Status.Kind.SALE, "");
        			} else {
        				lotStatus = LotStatus.SOLD_PENDING_PAYMENT;
        				lot.closeLot(lotStatus);
        				return new Status(Status.Kind.SALE_PENDING_PAYMENT, "");
        			}
        		}
        		//
        		else {
        			lotStatus = LotStatus.UNSOLD;
        			lot.closeLot(lotStatus);
        			sendMessageToBuyers(lot.getInterestedBuyers(), "LOT UNSOLD", lotNumber, new Money("0"));
        			messagingService.lotUnsold(seller.getMessagingAddress(), lotNumber);
        			return new Status(Status.Kind.NO_SALE, "");
        		}
        	}
        	// return ERROR if the lot is not in auction
        	else {
        		return new Status(Status.Kind.ERROR, "");
        	}
        }
        // return ERROR if the lot does not exist
        else {
        	return new Status(Status.Kind.ERROR, "");
        }
    }

        		
    // Check a string is not null or empty.
    private boolean checkStringValid(String string) {
    	
    	if(string == null)
    		return false;
    	if(string.length() == 0)
    		return false;
    	
    	return true;
    }
    
}
