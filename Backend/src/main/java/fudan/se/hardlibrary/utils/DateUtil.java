package fudan.se.hardlibrary.utils;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Util class for change Date to String
 */
@Component
public class DateUtil {

    /**
     * @param date      Date object need to change
     * @param format    Format, such as "yy-mm-dd", "yy-MM-dd HH:mm:ss"
     */
    public String toDateString(Date date, String format) {
        String dateStr = null;
        if (date != null && format != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            dateStr = formatter.format(date);
        }
        return dateStr;
    }

    /**
     * string to date
     */
    public Date stringToDate(String dateString, String format) {
        Date date = null;
        if (dateString != null && format != null){
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            try {
                date = formatter.parse(dateString);
            } catch (ParseException e) {
                date = null;
            }
        }
        return date;
    }

    public String sumDate(String oldDate, int sec) {
        Date date = stringToDate(oldDate, "yyyy-MM-dd HH:mm:ss");
        Date newDate =  new Date(date.getTime() + ((long)sec) * 1000);
        return toDateString(newDate, "yyyy-MM-dd HH:mm:ss");
    }
}
