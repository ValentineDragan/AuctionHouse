/**
 * 
 */
package auctionhouse;

import java.util.ArrayList;
import java.util.List;
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
	private List<CatalogueEntry> catalogueEntries = new ArrayList<CatalogueEntry>();
	private Parameters parameters;
	
	private MessagingService messagingService = new MockMessagingService();
	private BankingService bankingService = new MockBankingService();
	
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
    }

    public Status registerBuyer(
            String name,
            String address,
            String bankAccount,
            String bankAuthCode) {
        logger.fine(startBanner("registerBuyer " + name));
        
        return Status.OK();
    }

    public Status registerSeller(
            String name,
            String address,
            String bankAccount) {
        logger.fine(startBanner("registerSeller " + name));
        
        return Status.OK();      
    }

    public Status addLot(
            String sellerName,
            int number,
            String description,
            Money reservePrice) {
        logger.fine(startBanner("addLot " + sellerName + " " + number));
        
        return Status.OK();    
    }

    public List<CatalogueEntry> viewCatalogue() {
        logger.fine(startBanner("viewCatalog"));
        
        logger.fine("Catalogue: " + catalogueEntries.toString());
        return catalogueEntries;
    }

    public Status noteInterest(
            String buyerName,
            int lotNumber) {
        logger.fine(startBanner("noteInterest " + buyerName + " " + lotNumber));
        
        return Status.OK();   
    }

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
        Status status = lotToBid.makeBid(buyerName, bid);
        
        if(status.kind == Status.Kind.OK) {
        	messagingService.bidAccepted(buyers.get(buyerName).getMessagingAddress(), lotNumber, bid);
        }
        
        return status;    
    }

    public Status closeAuction(
            String auctioneerName,
            int lotNumber) {
        logger.fine(startBanner("closeAuction " + auctioneerName + " " + lotNumber));
 
        Lot lot = lots.get(lotNumber);
        Status status;
        
        if(lot != null) {
        	lot.closeLot();
        	List<String> interestedBuyers = lot.getInterestedBuyers();
        	
        	String sellerName = lot.getSellerName();
        	Seller seller = sellers.get(sellerName);
        	
        	String buyerName = lot.getHighestBidderName();
        	Buyer buyer = buyers.get(buyerName);
        	
        	// TODO: add premium, commision, stuff
        	status = bankingService.transfer(buyer.getBuyerAccount(), buyer.getBuyerAuthorisation(), seller.getSellerAccount(), lot.getHighestBidAmount());
        	
        	if(status.kind == Status.Kind.OK) {
        		for(String interestedBuyerName: interestedBuyers) {
            		Buyer interestedBuyer = buyers.get(interestedBuyerName);
            		messagingService.lotSold(interestedBuyer.getMessagingAddress(), lotNumber);
            	}
        	} else {
        		for(String interestedBuyerName: interestedBuyers) {
            		Buyer interestedBuyer = buyers.get(interestedBuyerName);
            		messagingService.lotUnsold(interestedBuyer.getMessagingAddress(), lotNumber);
            	}
        	}
               	
        } else {
        	status = new Status(Status.Kind.ERROR, "Lot does not exist");
        }       
        
        return status;
    }
    
}
