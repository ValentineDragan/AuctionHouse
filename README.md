# AuctionHouse

This project was developed for the **Software Engineering course** at The University of Edinburgh.

The project contains the design and implementation of the BidIT Auction House System. The System enables Auld Reekie Auction House to have an online catalogue of lots that can be browsed by the general public. In addition, Buyers can use the system on their electronic devices (smartphones or personal computers) to submit bids off-site.

## Software Architecture Description

Using the Separation of Concerns design principle, I have identified 3 main areas of the program, each separated further by distinct concerns. 15 Classes and 1 Interface were created in order to implement the project in a clean and accurate manner. 

* Actors
  * **Buyer**: stores the data of a registered buyer
  * **Seller**: stores the data of a registered seller
  * **Auctioneer**: stores the data of a registered auctioneer
* Buying & Selling components
  * **ActionHouseImp**: controlls the relationship between inputs & outputs, and the functionality of the system
  * **Lot**: stores information and functionality relevant to the Lots  
  * **CatalogueEntry**: contains the information about a lot that is only relevant to Buyers
* Services
  * **MessagingService**: responsible for sending notifications to all the Actors
  * **BankingService**: responsible for handling financial transactions

### Description of Files

* **Requirements capture.pdf** - captures the requirements of the project and outlines the main use cases
* **Class documentation.pdf** - contains low level descriptions of each class, UML diagrams and Sequence models
* **High level design.pdf** - contains a high level description of the system, along with implementation decisions
* **bin folder** - contains the code of all the classes developed for the project

## Built With

* [Java JDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - OOP Programming language
* [Overleaf](https://www.overleaf.com/) - online LaTeX editor

## Authors
**Valentine Dragan** and **Diana Tanase**
