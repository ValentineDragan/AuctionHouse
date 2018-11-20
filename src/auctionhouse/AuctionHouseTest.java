/**
 * 
 */
package auctionhouse;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author pbj
 *
 */
public class AuctionHouseTest {

    private static final double BUYER_PREMIUM = 10.0;
    private static final double COMMISSION = 15.0;
    private static final Money INCREMENT = new Money("10.00");
    private static final String HOUSE_ACCOUNT = "AH A/C";
    private static final String HOUSE_AUTH_CODE = "AH-auth";

    private AuctionHouse house;
    private MockMessagingService messagingService;
    private MockBankingService bankingService;

    /*
     * Utility methods to help shorten test text.
     */
    private static void assertOK(Status status) { 
        assertEquals(Status.Kind.OK, status.kind);
    }
    private static void assertError(Status status) { 
        assertEquals(Status.Kind.ERROR, status.kind);
    }
    private static void assertSale(Status status) { 
        assertEquals(Status.Kind.SALE, status.kind);
    }
    
    private static void assertNoSale(Status status) { 
        assertEquals(Status.Kind.NO_SALE, status.kind);
    }
    
    /*
     * Logging functionality
     */

    // Convenience field.  Saves on getLogger() calls when logger object needed.
    private static Logger logger;

    // Update this field to limit logging.
    public static Level loggingLevel = Level.ALL;

    private static final String LS = System.lineSeparator();

    @BeforeClass
    public static void setupLogger() {

        logger = Logger.getLogger("auctionhouse"); 
        logger.setLevel(loggingLevel);

        // Ensure the root handler passes on all messages at loggingLevel and above (i.e. more severe)
        Logger rootLogger = Logger.getLogger("");
        Handler handler = rootLogger.getHandlers()[0];
        handler.setLevel(loggingLevel);
    }

    private String makeBanner(String testCaseName) {
        return  LS 
                + "#############################################################" + LS
                + "TESTCASE: " + testCaseName + LS
                + "#############################################################";
    }

    @Before
    public void setup() {
        messagingService = new MockMessagingService();
        bankingService = new MockBankingService();

        house = new AuctionHouseImp(
                    new Parameters(
                        BUYER_PREMIUM,
                        COMMISSION,
                        INCREMENT,
                        HOUSE_ACCOUNT,
                        HOUSE_AUTH_CODE,
                        messagingService,
                        bankingService));


    }
    /*
     * Setup story running through all the test cases.
     * 
     * Story end point is made controllable so that tests can check 
     * story prefixes and branch off in different ways. 
     */
    private void runStory(int endPoint) {
    	    	
        assertOK(house.registerSeller("SellerY", "@SellerY", "SY A/C"));       
        assertOK(house.registerSeller("SellerZ", "@SellerZ", "SZ A/C")); 
        if (endPoint == 1) return;
        
        assertOK(house.addLot("SellerY", 2, "Painting", new Money("200.00")));
        assertOK(house.addLot("SellerY", 1, "Bicycle", new Money("80.00")));
        assertOK(house.addLot("SellerZ", 5, "Table", new Money("100.00")));
        if (endPoint == 2) return;
        
        assertOK(house.registerBuyer("BuyerA", "@BuyerA", "BA A/C", "BA-auth"));       
        assertOK(house.registerBuyer("BuyerB", "@BuyerB", "BB A/C", "BB-auth"));
        assertOK(house.registerBuyer("BuyerC", "@BuyerC", "BC A/C", "BC-auth"));
        if (endPoint == 3) return;
        
        assertOK(house.noteInterest("BuyerA", 1));
        assertOK(house.noteInterest("BuyerA", 5));
        assertOK(house.noteInterest("BuyerB", 1));
        assertOK(house.noteInterest("BuyerB", 2));
        if (endPoint == 4) return;
        
        assertOK(house.openAuction("Auctioneer1", "@Auctioneer1", 1));

        messagingService.expectAuctionOpened("@BuyerA", 1);
        messagingService.expectAuctionOpened("@BuyerB", 1);
        messagingService.expectAuctionOpened("@SellerY", 1);
        messagingService.verify(); 
        if (endPoint == 5) return;
        
        Money m70 = new Money("70.00");
        assertOK(house.makeBid("BuyerA", 1, m70));
        
        messagingService.expectBidReceived("@BuyerB", 1, m70);
        messagingService.expectBidReceived("@Auctioneer1", 1, m70);
        messagingService.expectBidReceived("@SellerY", 1, m70);
        messagingService.verify();
        if (endPoint == 6) return;
        
        Money m100 = new Money("100.00");
        assertOK(house.makeBid("BuyerB", 1, m100));

        messagingService.expectBidReceived("@BuyerA", 1, m100);
        messagingService.expectBidReceived("@Auctioneer1", 1, m100);
        messagingService.expectBidReceived("@SellerY", 1, m100);
        messagingService.verify();
        if (endPoint == 7) return;
        
        assertSale(house.closeAuction("Auctioneer1",  1));
        messagingService.expectLotSold("@BuyerA", 1);
        messagingService.expectLotSold("@BuyerB", 1);
        messagingService.expectLotSold("@SellerY", 1);
        messagingService.verify();       

        bankingService.expectTransfer("BB A/C",  "BB-auth",  "AH A/C", new Money("110.00"));
        bankingService.expectTransfer("AH A/C",  "AH-auth",  "SY A/C", new Money("85.00"));
        bankingService.verify();        
    }
    
    @Test
    public void testEmptyCatalogue() {
        logger.info(makeBanner("emptyLotStore"));

        List<CatalogueEntry> expectedCatalogue = new ArrayList<CatalogueEntry>();
        List<CatalogueEntry> actualCatalogue = house.viewCatalogue();

        assertEquals(expectedCatalogue, actualCatalogue);
    }

    @Test
    public void testRegisterSeller() {
        logger.info(makeBanner("testRegisterSeller"));
        runStory(1);
    }

    @Test
    public void testRegisterSellerDuplicateNames() {
        logger.info(makeBanner("testRegisterSellerDuplicateNames"));
        runStory(1);     
        assertError(house.registerSeller("SellerY", "@SellerZ", "SZ A/C"));       
    }

    @Test
    public void testAddLot() {
        logger.info(makeBanner("testAddLot"));
        runStory(2);
    }
    
    @Test
    public void testViewCatalogue() {
        logger.info(makeBanner("testViewCatalogue"));
        runStory(2);
        
        List<CatalogueEntry> expectedCatalogue = new ArrayList<CatalogueEntry>();
        expectedCatalogue.add(new CatalogueEntry(1, "Bicycle", LotStatus.UNSOLD)); 
        expectedCatalogue.add(new CatalogueEntry(2, "Painting", LotStatus.UNSOLD));
        expectedCatalogue.add(new CatalogueEntry(5, "Table", LotStatus.UNSOLD));

        List<CatalogueEntry> actualCatalogue = house.viewCatalogue();

        assertEquals(expectedCatalogue, actualCatalogue);
    }

    @Test
    public void testRegisterBuyer() {
        logger.info(makeBanner("testRegisterBuyer"));
        runStory(3);       
    }

    @Test
    public void testNoteInterest() {
        logger.info(makeBanner("testNoteInterest"));
        runStory(4);
    }
      
    @Test
    public void testOpenAuction() {
        logger.info(makeBanner("testOpenAuction"));
        runStory(5);       
    }
      
    @Test
    public void testMakeBid() {
        logger.info(makeBanner("testMakeBid"));
        runStory(7);
    }
  
    @Test
    public void testCloseAuctionWithSale() {
        logger.info(makeBanner("testCloseAuctionWithSale"));
        runStory(8);
    }
     
    // *** New tests start here ***
    
    private void runSecondStory(int endPoint) {
    	
    	addSellers();
        if (endPoint == 1) return;
        
        addLots();
        if (endPoint == 2) return;
        
        addBuyers();
        if (endPoint == 3) return;
        
        noteInterest();
        if (endPoint == 4) return;
        
        openLot();
        if (endPoint == 5) return;
        
        makeBids();
        if (endPoint == 6) return;
        
        closeLotNotSold();
        if (endPoint == 7) return;
    } 
    
    private void addSellers() {
        assertOK(house.registerSeller("Seller1", "@Seller1", "S1 A/C"));       
        assertOK(house.registerSeller("Seller2", "@Seller2", "S2 A/C")); 
        
        assertError(house.registerSeller("Seller2", "@Seller2a", "S2a A/C")); // exisiting name
        assertError(house.registerSeller("Seller3", "", "S2a A/C")); // empty string
        assertError(house.registerSeller("Seller4", "@Seller4", null)); // null string
    }
    
    private void addLots() {
        assertOK(house.addLot("Seller1", 3, "Bookcase", new Money("80.00")));
        assertOK(house.addLot("Seller2", 2, "Statue", new Money("100.00")));
        
        assertError(house.addLot("SellerC", 2, "Painting", new Money("200.00"))); // not exisiting seller
        assertError(house.addLot("Seller1", 1, "Lamp", new Money("-2"))); // negative money
        assertError(house.addLot("Seller2", 3, "Chair", new Money("60.00"))); // exisiting lot number
    }
    
    private void addBuyers() {
        assertOK(house.registerBuyer("Buyer1", "@Buyer1", "BA A/C", "BA-auth"));       
        assertOK(house.registerBuyer("Buyer2", "@Buyer2", "BB A/C", "BB-auth"));
        assertOK(house.registerBuyer("Buyer3", "@Buyer3", "BC A/C", "BC-auth"));
        
        assertError(house.registerBuyer("Buyer2", "@Buyer2", "BB A/C", "BB-auth")); // exisiting name
        assertError(house.registerBuyer("Buyer5", "@Buyer5", "BE A/C", "")); // empty string
        assertError(house.registerBuyer("Buyer6", "@Buyer6", null, "BF-auth")); // null string
    }
    
    private void noteInterest() {
        assertOK(house.noteInterest("Buyer1", 2));
        assertOK(house.noteInterest("Buyer2", 2));
        assertOK(house.noteInterest("Buyer3", 3));
        assertOK(house.noteInterest("Buyer1", 3));
        
        assertError(house.noteInterest("Buyer1", 3)); //not existing lot
        assertError(house.noteInterest("Buyer4", 2)); // not existing buyer
        assertError(house.noteInterest("Buyer3", 3)); // already interested
    }
    
    private void openLot() {
        assertOK(house.openAuction("Auctioneer1", "@Auctioneer1", 2));
        
        assertError(house.openAuction("Auctioneer1", "", 2)); // empty string
        assertError(house.openAuction("Auctioneer1", "", 4)); // not existing lot
        
        messagingService.expectAuctionOpened("@Buyer1", 2);
        messagingService.expectAuctionOpened("@Buyer2", 2);
        messagingService.expectAuctionOpened("@Seller2", 2);
        messagingService.verify();
        
        assertError(house.openAuction("Auctioneer2", "@Auctioneer2", 2)); // already open
    }
    
    private void makeBids() {
    	Money m60 = new Money("60.00");
    	
        assertError(house.makeBid("BuyerA", 2, m60)); // buyer not registered with the system
        assertError(house.makeBid("", 2, m60)); // buyer name null
        assertError(house.makeBid("Buyer1", 1, m60)); // lot does not exist
        assertError(house.makeBid("Buyer3", 2, m60)); // buyer not interested in lot 
        assertError(house.makeBid("Buyer3", 3, m60)); // lot not open
        assertError(house.makeBid("Buyer1", 2, new Money("-100.00"))); // bid value negative
        
        assertOK(house.makeBid("Buyer2", 2, m60));
        
        messagingService.expectBidReceived("@Buyer1", 2, m60);
        messagingService.expectBidReceived("@Auctioneer1", 2, m60);
        messagingService.expectBidReceived("@Seller2", 2, m60);
        messagingService.verify();
    }
    
    private void closeLotNotSold() {
    	
    	assertError(house.closeAuction("",  2)); // auctioneer name invalid
    	assertError(house.closeAuction("Auctioneer2",  2)); // auctioneer2 did not open auction for lot 2
    	assertError(house.closeAuction("Auctioneer1",  1)); // lot 1 does not exist
    	assertError(house.closeAuction("Auctioneer1",  3)); // lot 3 is not open
    	
    	assertNoSale(house.closeAuction("Auctioneer1",  2)); // hammer price less than reserved
    	
    	assertEquals(house.viewCatalogue().get(0).lotNumber, 2);
    	assertEquals(house.viewCatalogue().get(0).status, LotStatus.UNSOLD); // correct catalogue entry state unsold
    	
    	messagingService.expectLotUnsold("@Buyer1", 2);
    	messagingService.expectLotUnsold("@Buyer2", 2);
    	messagingService.expectLotUnsold("@Seller2", 2);
        messagingService.verify();       
    }
    
    @Test
    public void testRegisterSellerWithInvalidInputs() {
    	logger.info(makeBanner("testRegisterSellerWIthInvalidInputs"));
    	runSecondStory(1);    	
    }
    
    @Test
    public void testAddLotWithExpectedErrors() {
    	logger.info(makeBanner("testAddLotWithExpectedErrors"));
    	runSecondStory(2);
    }
    
    // Ensure lots with return error status were not added
    @Test
    public void testViewCatalogueEntries() {
        logger.info(makeBanner("testViewCatalogueEntries"));
    	runSecondStory(2);
        
        List<CatalogueEntry> expectedCatalogueEntries = new ArrayList<CatalogueEntry>();
        expectedCatalogueEntries.add(new CatalogueEntry(2, "Statue", LotStatus.UNSOLD));
        expectedCatalogueEntries.add(new CatalogueEntry(3, "Bookcase", LotStatus.UNSOLD)); 
 
        List<CatalogueEntry> actualCatalogue = house.viewCatalogue();

        assertEquals(expectedCatalogueEntries, actualCatalogue);
    }
    
    @Test
    public void testRegisterBuyerWithInvalidInputs() {
    	logger.info(makeBanner("testRegisterBuyerWithInvalidInputs"));
    	runSecondStory(3);
    }
    
    @Test
    public void testNoteInterestWithExpectedErrors() {
        logger.info(makeBanner("testNoteInterestWithExpectedErrors"));
        runSecondStory(4);
    }
    
    @Test
    public void testOpenAuctionWithExpectedErrors() {
    	logger.info(makeBanner("testNoteInterestWithExpectedErrors"));
    	runSecondStory(5);
    }
    
    @Test
    public void testMakeBidsWithExpectedErrors() {
    	logger.info(makeBanner("testMakeBidWithExpectedErrors"));
    	runSecondStory(6);
    }
    
    @Test
    public void testCloseLotNotSold() {
    	logger.info(makeBanner("testCloseLotNotSold"));
    	runSecondStory(7);
    }
    
    @Test
    public void testOpenSoldLot() {
    	logger.info(makeBanner("testCloseLotNotSold"));
    	runStory(8);
    	
    	// try to open a sold lot again
    	assertError(house.openAuction("Auctioneer1", "@Auctioneer1", 1));
    }
    
    // *** New tests end here ***
    
}
