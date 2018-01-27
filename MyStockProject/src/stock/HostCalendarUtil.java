package stock;

import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author arun.chougule
 * Utility class to check if instructed settlement date falls on Weekend and get next working day
 */
public class HostCalendarUtil {
	
	static Calendar calendar = Calendar.getInstance();
	/**
	 * Method to check if instructed settlement date falls on Weekend for currency other than AED and SAR
	 * @param date
	 * @return true if date falls on weekend (Saturday or Sunday), false otherwise
	 */
	public static boolean isDayFallsOnWeekend(Date date) {
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
			return true;
		} else {
			return false;
		}

	}
	/**
	 * Method to check if instructed settlement date falls on non-working day for currency AED and SAR
	 * @param date
	 * @return true if date falls on weekend (Friday or Saturday), false otherwise
	 */
	public static boolean isDayFallsOnSpecialWeekend(Date date) {
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		if (day == Calendar.FRIDAY || day == Calendar.SATURDAY) {
			return true;
		} else {
			return false;
		}

	}
	/**
	 * Method to check add days to get next working day
	 * @param date
	 * @return next working day
	 */
	public static Date addDays(Date date, int days) {
	    calendar.setTime(date);
	    calendar.add(Calendar.DATE, days);
	    return calendar.getTime();
	}
}
