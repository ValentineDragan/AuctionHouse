package auctionhouse;

import java.util.Comparator;

public class CatalogueEntryComparator implements Comparator<CatalogueEntry> {
	
	public int compare(CatalogueEntry c1, CatalogueEntry c2) {
		
		int lotNumber1 = c1.lotNumber;
		int lotNumber2 = c2.lotNumber;
		
		if(lotNumber1 < lotNumber2) {
			return -1;
		}
		
		if(lotNumber1 > lotNumber2) {
			return 1;
		}
		
		return 0;
	}
}
