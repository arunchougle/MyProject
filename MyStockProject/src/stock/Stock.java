package stock;

import java.io.Serializable;
import java.util.Currency;

/**
 * 
 * @author arun.chougule
 * Entity class representing Stock with stockName, currency and AgreedFx
 */
public class Stock implements Serializable {

	/**
	 * Generated serial version id
	 */
	private static final long serialVersionUID = -8964581069658726451L;

	private String stockName;
	private Currency currency;
	private Double agreedFx;

	Stock(String stockName, Currency currency, Double agreedFx) {
		this.stockName = stockName;
		this.currency = currency;
		this.agreedFx = agreedFx;
	}

	public Double getAgreedFx() {
		return agreedFx;
	}

	public void setAgreedFx(Double agreedFx) {
		this.agreedFx = agreedFx;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Stockname: ").append(this.stockName).append(" | ").append("Currency: ")
				.append(this.currency.getCurrencyCode()).append(" | ").append("AgreedFx: ").append(this.agreedFx.toString());
		return builder.toString();
	}

}
