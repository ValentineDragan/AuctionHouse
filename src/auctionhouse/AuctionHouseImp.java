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
    

    // Creates a new Buyer object, and adds it to the buyers HashMap.
    // Return the Status: 'Error' if the Buyer already exists, or 'OK' if the new Buyer has been created.
    public Status registerBuyer(
            String name,
            String address,
            String bankAccount,
            String bankAuthCode) {
        logger.fine(startBanner("registerBuyer " + name));
        
        if(buyers.get(name) != null) {
        	return Status.error("Name exists as buyer already");
        }
        
        Buyer buyer = new Buyer(name, address, bankAccount, bankAuthCode);
        buyers.put(name, buyer);
        
        return Status.OK();
    }

    // Creates a new Seller object, and adds it to the sellers HashMap.
    // Return the Status 'Error' if the Seller already exists, or 'OK' if the new Seller has been created.
    public Status registerSeller(
            String name,
            String address,
            String bankAccount) {
        logger.fine(startBanner("registerSeller " + name));
        
        if(sellers.get(name) != null) {
        	return Status.error("Name exists as seller already");
        }
        
        Seller seller = new Seller(name, address, bankAccount);
        sellers.put(name, seller);
        
        return Status.OK();      
    }

    // Creates a new Lot object, adds it to the lots HashMap and adds its CatalogueEntry to the catalogueEntries Queue.
    // Return the Status: 'Error' if the Lot already exists, or 'OK' if the new Lot has been created.
    public Status addLot(
            String sellerName,
            int number,
            String description,
            Money reservePrice) {
        logger.fine(startBanner("addLot " + sellerName + " " + number));
        
        if(lots.get(number) != null) {
        	return Status.error("Lot already exists");
        }
                  
        Lot lot = new Lot(sellerName, number, description, reservePrice);
        lots.put(number, lot);
        
        // update status of CE!
        catalogueEntries.add(lot.getCatalogueEntry());
        
        return Status.OK();    
    }

    // Returns a List of Catalogue Entries, ordered by their lotNumber.
    public List<CatalogueEntry> viewCatalogue() {
        logger.fine(startBanner("viewCatalog"));
        
        logger.fine("Catalogue: " + catalogueEntries.toString());
        
        List<CatalogueEntry> catalogueList = new ArrayList<CatalogueEntry>();
        for(CatalogueEntry ce: catalogueEntries) {
        	catalogueList.add(ce);
        }
        return catalogueList;
    }

    // Sends a signal to the Lot to add new interested buyer.
    // Returns the Status: 'Error' if the Lot doesn't exist, or 'OK' if the buyer has been added successfully.
    public Status noteInterest(
            String buyerName,
            int lotNumber) {
        logger.fine(startBanner("noteInterest " + buyerName + " " + lotNumber));
        
        Lot lot = lots.get(lotNumber);
        if(lot == null) {
        	return Status.error("No exisitng lot");
        }
        
        // Check if buyer is already in the interestedBuyers list! It should return an error
        lot.addInterestedBuyer(buyerName);    	
            
        return Status.OK();   
    }

    // Sends a signal to the Lot to open auction. Gets the list of all interested buyers, and uses messagingService to send a message to all the interested buyers.
    // Returns the Status: ERROR, if the Lot doesn't exist, or Status= lot.openLot(..) ... complete here<
    public Status openAuction(
            String auctioneerName,
            String auctioneerAddress,
            int lotNumber) {
        logger.fine(startBanner("openAuction " + auctioneerName + " " + lotNumber));
        
        Auctioneer auctioneer;
        if(auctioneers.get(auctioneerName) != null) {
        	auctioneer = auctioneers.get(auctioneerName);
        } else {
        	auctioneer = new Auctioneer(auctioneerName, auctioneerAddress);
        	auctioneers.put(auctioneerName, auctioneer);
        }
        
        Lot lot = lots.get(lotNumber);
        Status status;
        
        if(lot != null) {
        	status = lot.openLot(auctioneerName);
        	List<String> interestedBuyers = lot.getInterestedBuyers();
        	
        	Seller seller = sellers.get(lot.getSellerName());
        	messagingService.auctionOpened(seller.getMessagingAddress(), lotNumber);
        	
        	for(String buyerName: interestedBuyers) {
        		Buyer interestedBuyer = buyers.get(buyerName);
        		messagingService.auctionOpened(interestedBuyer.getMessagingAddress(), lotNumber);
        	}
        	
        } else {
        	status = new Status(Status.Kind.ERROR, "Lot does not exist");
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

    public Status closeAuction(
            String auctioneerName,
            int lotNumber) {
        logger.fine(startBanner("closeAuction " + auctioneerName + " " + lotNumber));
 
        Lot lot = lots.get(lotNumber);
        
        if(lot != null) {
        	Status status = lot.closeLot();
        	if(status.kind == Status.Kind.SALE_PENDING_PAYMENT) {
        		
        		// inform interested buyers and sellers
        		for(String str: lot.getInterestedBuyers()) {
        			Buyer buyer = buyers.get(str);
        			messagingService.lotSold(buyer.getMessagingAddress(), lotNumber);
        		}
        		
        		Seller seller = sellers.get(lot.getSellerName());
        		messagingService.lotSold(seller.getMessagingAddress(), lotNumber);
        		
        		// do transfers
        		Buyer winner = buyers.get(lot.getHighestBidderName());
        		Money amount = lot.getHighestBidAmount();
        		
        		Money toAH = amount.addPercent(parameters.buyerPremium);
        		Money toSeller = amount.subtract(new Money(Double.toString(parameters.commission)));
        		Status buyerToAH = bankingService.transfer(winner.getBuyerAccount(), winner.getBuyerAuthorisation(), parameters.houseBankAccount, toAH);
        		Status ahToSeller = bankingService.transfer(parameters.houseBankAccount, parameters.houseBankAuthCode,seller.getSellerAccount(), toSeller);
        		
        		if(buyerToAH.kind == Status.Kind.OK && ahToSeller.kind == Status.Kind.OK) {
        			lot.successfulSale();
            		return new Status(Status.Kind.SALE);
        		} else {
        			// TODO unsuccessful sale + messages!
        			return new Status(Status.Kind.ERROR, "Failed transfer");
        		}
        		
        	} else {
        		return new Status(Status.Kind.NO_SALE);
        	}
        } else {
        	return new Status(Status.Kind.ERROR, "Lot does not exist");
        }       
       
    }
    
}
