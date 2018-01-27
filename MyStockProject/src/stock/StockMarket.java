package stock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 
 * @author arun.chougule This Class is intended to execute trading for different
 *         stocks and generate reports like: 
 *         1. Amount in USD settled incoming everyday 
 *         2. Amount in USD settled outgoing everyday
 * 
 *         To achieve this we have to initialize stock market and setup user
 *         portfolio with certain stocks. Special consideration is given to
 *         certain currencies to decide trade settlement date. As no UI and
 *         database involved, reports are generated on console output.
 */

public class StockMarket extends Thread {
	/**
	 * Collection to hold all stocks 
	 */
	List<Stock> stockList;
	
	/**
	 * Collection to hold portfolio details
	 */
	Map<String, Integer> portfolio = new HashMap<String, Integer>();
	
	/**
	 * Collection to hold price details per stock
	 */
	Map<String, Double> priceHolder;
	
	/**
	 * Collection to hold outgoing amount per day
	 */
	Map<String, Double> outgoingPerDay = new HashMap<String, Double>();
	
	/**
	 * Collection to hold incoming amount per day
	 */
	Map<String, Double> incomingPerDay = new HashMap<String, Double>();
	
	/**
	 * Collection to determine ranking for outgoing entities
	 */
	Map<String, Double> outgoingRank = new HashMap<String, Double>();
	
	/**
	 * Collection to determine ranking for incoming entities
	 */
	Map<String, Double> incomingRank = new HashMap<String, Double>();

	public void run() {
		System.out.println("Started execution of StockMarket thread...");

		// Initialize stock market with Stock name, Currency and AgreedFx
		initializeStockMarket();

		// Set stock price per unit
		setupStockPrice();

		// Setup user portfolio
		setupPortfolio();

		System.out.println("Portfolio before trading....");
		showPortfolio();

		try {

			// Buy 200 units of "foo"
			// 2nd Jan 2016 is Saturday, settlement to be done on next working day 4th Jan for SGD
			System.out.println(
					"^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ Trade execution starts ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
			executeTrade(TradeType.BUY, stockList.get(0), 200,
					new GregorianCalendar(2016, Calendar.JANUARY, 2).getTime());
			
			// Sell 450 units of "bar"
			// 7th Jan is Thursday working day for AED (Friday and Saturday non working days)
			executeTrade(TradeType.SELL, stockList.get(1), 450,
					new GregorianCalendar(2016, Calendar.JANUARY, 7).getTime());
			executeTrade(TradeType.SELL, stockList.get(1), 100,
					new GregorianCalendar(2016, Calendar.JUNE, 20).getTime());
			executeTrade(TradeType.BUY, stockList.get(2), 15,
					new GregorianCalendar(2017, Calendar.MARCH, 11).getTime());
			executeTrade(TradeType.SELL, stockList.get(2), 600,
					new GregorianCalendar(2014, Calendar.APRIL, 24).getTime());
			executeTrade(TradeType.BUY, stockList.get(0), 300,
					new GregorianCalendar(2018, Calendar.DECEMBER, 25).getTime());
			executeTrade(TradeType.SELL, stockList.get(3), 300,
					new GregorianCalendar(2015, Calendar.MAY, 31).getTime());
			
			System.out.println(
					"^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ Trade execution ends ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
		} catch (IndexOutOfBoundsException ex) {
			System.out.println("Exception occured, trying to access stock which does not exist!");
		}
		System.out.println("Portfolio after trading....");
		showPortfolio();
		printReport();
	}

	/**
	 * Method to initialize stock market with Stock name, Currency and AgreedFx each stock
	 */
	private void initializeStockMarket() {
		System.out.println("Initializing Stock Market....");
		stockList = new ArrayList<Stock>();
		Stock c1 = new Stock("foo", Currency.getInstance("SGD"), 0.50);
		stockList.add(c1);
		Stock c2 = new Stock("bar", Currency.getInstance("AED"), 0.22);
		stockList.add(c2);
		Stock c3 = new Stock("myStock1", Currency.getInstance("USD"), 0.67);
		stockList.add(c3);
		Stock c4 = new Stock("myStock2", Currency.getInstance("AUD"), 0.38);
		stockList.add(c4);
		for (Stock stock : stockList) {
			System.out.println(stock);
		}
		System.out.println(
				"--------------------------------------------------------------------------------------------------");
	}
	
	/**
	 * Method o=to setup stock price per unit
	 */
	private void setupStockPrice() {
		System.out.println("Setting up stock price per unit..."); // This can be dynamically retrieved from HOST systems
		priceHolder = new Hashtable<String, Double>();
		priceHolder.put(stockList.get(0).getStockName(), 100.25);
		priceHolder.put(stockList.get(1).getStockName(), 150.5);
		priceHolder.put(stockList.get(2).getStockName(), 367.66);
		priceHolder.put(stockList.get(3).getStockName(), 234.06);

		for (Map.Entry<String, Double> entry : priceHolder.entrySet()) {
			System.out.println("Stock: " + entry.getKey() + " | " + " Current price: " + entry.getValue());
		}
		System.out.println(
				"--------------------------------------------------------------------------------------------------");
	}

	/**
	 * Method to setup initial portfolio
	 */
	private void setupPortfolio() {
		portfolio.put(stockList.get(0).getStockName(), 500);
		portfolio.put(stockList.get(1).getStockName(), 500);
		portfolio.put(stockList.get(2).getStockName(), 350);
		portfolio.put(stockList.get(3).getStockName(), 400);
	}
	/**
	 * Method to print portfolio details with stock name and current holdings
	 */
	private void showPortfolio() {

		for (Map.Entry<String, Integer> entry : portfolio.entrySet()) {
			System.out.println("Stock: " + entry.getKey() + " | " + " Current holdings: " + entry.getValue());
		}
		System.out.println(
				"--------------------------------------------------------------------------------------------------");
	}

	/**
	 * Method to execute trade (given instruction - BUY or SELL)
	 * @param type
	 * 			Buy or Sell
	 * @param stock
	 * 			Stock object
	 * @param numShares
	 * 			number of shares to be bought or sold
	 * @param date
	 * 		instructed settlement date
	 */
	private void executeTrade(TradeType type, Stock stock, int numShares, Date date) {

		int currentShares = portfolio.get(stock.getStockName());
		switch (type) {
		case BUY:
			System.out.println("Buying stock: " + stock.getStockName() + " | Quantity: " + numShares);

			settleTrade(stock, numShares, date, currentShares);
			break;
		case SELL: {
			System.out.println("Selling stock: " + stock.getStockName() + " | Quantity: " + numShares);
			if (numShares > currentShares) {
				System.out.println("Ooops, you cannot sell more than what you have. You have " + currentShares
						+ " shares and you are trying sell " + numShares);
				System.out.println("------------------------------------------------------------------------------------");
				break;
			}
			
			numShares = numShares * -1;
			settleTrade(stock, numShares, date, currentShares);
		}
			break;
		}

	}

	/**
	 * Method to settle Trade and update portfolio
	 * @param stock
	 * 		The stock object
	 * @param numShares
	 * 		number of shares to be bought or sold
	 * @param date
	 * 		instructed settlement date
	 * @param currentShares
	 * 		current holdings
	 */
	private void settleTrade(Stock stock, int numShares, Date date, int currentShares) {
		// Check if currency code is AED or SAR to get next working day
		if (stock.getCurrency().getCurrencyCode().equalsIgnoreCase("AED")
				|| stock.getCurrency().getCurrencyCode().equalsIgnoreCase("SAR")) {
			while (HostCalendarUtil.isDayFallsOnSpecialWeekend(date)) {
				date = HostCalendarUtil.addDays(date, 1);
			}
			System.out.println("Instructed settlement date :" + date + " | Currency: "
					+ stock.getCurrency().getCurrencyCode() + " | Actual settlement date for this currency:" + date);
			int totalShares = currentShares + numShares;
			portfolio.put(stock.getStockName(), totalShares);

			calculateTotalTradedValue(stock, numShares, date);

		} else {
			// For currencies other than AED or SAR, check if day falls on weekend or not
			while (HostCalendarUtil.isDayFallsOnWeekend(date)) {
				date = HostCalendarUtil.addDays(date, 1);
			}
			System.out.println("Instructed settlement date :" + date + " | Currency: "
					+ stock.getCurrency().getCurrencyCode() + " | Actual settlement date for this currency:" + date);
			int totalShares = currentShares + numShares;
			portfolio.put(stock.getStockName(), totalShares);

			calculateTotalTradedValue(stock, numShares, date);

		}
		System.out.println("------------------------------------------------------------------------------------");
	}

	/**
	 * Method to calculate total traded value in USD by using given formula
	 * 		formula: tradedValueInUSD = price * numShares * agreedFx;
	 * @param stock
	 * 		The stock to be acted upon
	 * @param numShares
	 * 		total no of shares
	 * @param date
	 * 		calculated settlement date (final)
	 */
	private void calculateTotalTradedValue(Stock stock, int numShares, Date date) {
		double price = lookupStockPrice(stock.getStockName());
		double tradedValueInUSD = price * numShares * stock.getAgreedFx();

		System.out.println("Total traded value in USD: " + tradedValueInUSD);

		double tradedValueInUSDperDay = 0;

		if (numShares > 0) {

			if (outgoingPerDay.containsKey(date.toString())) {
				tradedValueInUSDperDay = outgoingPerDay.get(date.toString()) + tradedValueInUSD;
			} else {
				tradedValueInUSDperDay = tradedValueInUSD;
			}
			outgoingPerDay.put(date.toString(), tradedValueInUSDperDay);
			if (outgoingRank.containsKey(stock.getStockName())) {
				tradedValueInUSD = outgoingRank.get(stock.getStockName()) + tradedValueInUSD;
			}
			outgoingRank.put(stock.getStockName(), tradedValueInUSD);
		} else {

			if (incomingPerDay.containsKey(date.toString())) {
				tradedValueInUSDperDay = incomingPerDay.get(date.toString()) + tradedValueInUSD;
			} else {
				tradedValueInUSDperDay = tradedValueInUSD;
			}
			tradedValueInUSD = tradedValueInUSD * -1;
			incomingPerDay.put(date.toString(), tradedValueInUSDperDay);
			if (incomingRank.containsKey(stock.getStockName())) {
				tradedValueInUSD = incomingRank.get(stock.getStockName()) + tradedValueInUSD;
			}
			incomingRank.put(stock.getStockName(), tradedValueInUSD);
		}

	}

	/**
	 * Method to determine stock price dynamically
	 * @param stock
	 * @return stock price
	 */
	private double lookupStockPrice(String stock) {

		return priceHolder.get(stock);
	}
	
	/**
	 * Method to generate reports:
	 * 	1. Amount in USD settled incoming everyday 
	 *  2. Amount in USD settled outgoing everyday
	 */
	private void printReport() {
		System.out.println(
				"********************************************* REPORTING *******************************************");

		System.out.println("Amount in USD settled outgoing everyday");
		System.out.println(
				"--------------------------------------------------------------------------------------------------");
		if (outgoingPerDay.isEmpty()) {
			System.out.println("**No trading done in this category**");
		}
		for (Map.Entry<String, Double> dateEntry : outgoingPerDay.entrySet()) {
			System.out.println("Date trade settled: " + dateEntry.getKey() + " | " + "Total amount outflown in USD: "
					+ dateEntry.getValue() + "(+)");
		}
		System.out.println(
				"--------------------------------------------------------------------------------------------------");
		System.out.println(
				"//////////////////////////////////////////////////////////////////////////////////////////////////\n");
		System.out.println("Amount in USD settled incoming everyday");
		System.out.println(
				"--------------------------------------------------------------------------------------------------");
		if (incomingPerDay.isEmpty()) {
			System.out.println("**No trading done in this category**");
		}
		for (Map.Entry<String, Double> dateEntry : incomingPerDay.entrySet()) {
			System.out.println("Date trade settled: " + dateEntry.getKey() + " | " + "Total amount incoming in USD: "
					+ dateEntry.getValue() * -1 + "(-)");
		}
		System.out.println(
				"--------------------------------------------------------------------------------------------------");
		System.out.println(
				"//////////////////////////////////////////////////////////////////////////////////////////////////\n");
		System.out.println("Entity Ranking outgoing (BUY):");
		System.out.println(
				"--------------------------------------------------------------------------------------------------");
		gernerateRankingReport(outgoingRank);
		System.out.println(
				"//////////////////////////////////////////////////////////////////////////////////////////////////\n");
		System.out.println("Entity Ranking incoming (SELL):");
		System.out.println(
				"--------------------------------------------------------------------------------------------------");
		gernerateRankingReport(incomingRank);
		System.out.println(
				"--------------------------------------------------------------------------------------------------");
		System.out.println(
				"********************************************* END OF REPORT ****************************************");
	}

	/**
	 * Method to generate ranking report based on incoming and outgoing amount
	 * @param rankMap
	 */
	private void gernerateRankingReport(Map<String, Double> rankMap) {
		Set<Entry<String, Double>> set = rankMap.entrySet();
		List<Entry<String, Double>> list = new ArrayList<Entry<String, Double>>(set);
		if (list.isEmpty()) {
			System.out.println("**No trading done in this category**");
		} else if (list.size() == 1) {
			System.out.println("Only one entity traded in this category, so defaulted to top rating..");
		}
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});
		int rank = 1;
		for (Map.Entry<String, Double> entry : list) {
			System.out.println("Rank : " + rank + " | " + "Entity :" + entry.getKey()
					+ " | Total traded amount in USD: " + entry.getValue());
			rank++;
		}
	}


}
