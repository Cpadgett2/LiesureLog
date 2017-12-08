
package leisurelog;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Defines log date objects used for log entries
 *
 * @author TeamLeisure
 */

public class LogDateTime implements Serializable {
    
    private static final long serialVersionUID = -1302152153289478928L;
    private final LocalDateTime ldt;
    private static final DateTimeFormatter dtf = 
            DateTimeFormatter.ofPattern("MM'/'dd'/'yy' 'HH':'mm':'ss");
    
    // construct with current date time
    LogDateTime(){
        ldt = LocalDateTime.now();
    }
    
    // construct with string specifying date and time
    LogDateTime(String dateTime){
        ldt = LocalDateTime.parse(dateTime, dtf);
    }
    
    // construct with date string and time string
    LogDateTime(String date, String time){
        ldt = LocalDateTime.parse(date + ' ' + time, dtf);
    }
    
    LogDateTime(LocalDateTime ldt){
        this.ldt = ldt;
    }
    
    //getters
    public String getDate(){
        return toString().split(" ")[0];
    }
    
    public String getTime(){
        return toString().split(" ")[1];
    }
    
    // returns date time string minus year
    public String exYear(){
        return getDate().substring(0, 4) + " " + getTime();
    }
    
    public LocalDateTime getLocalDateTime(){
        return ldt;
    }
    
    // returns the 2 curfew times for this logdatetime
    public LogDateTime[] getCurfews(){
        LogDateTime tomorrow = new LogDateTime(ldt.plusDays(1));
        LogDateTime[] curfews = new LogDateTime[2];
        curfews[0] = new LogDateTime(tomorrow.getDate(),"00:00:00");
        curfews[1] = new LogDateTime(tomorrow.getDate(),"01:00:00");
        return curfews;
    }
    
    // returns string of todays date
    static String dateToday(){
       return (new LogDateTime()).getDate();
    }
    
    // is this log time after specified log time
    public boolean isAfter(LogDateTime specLogTime){
        return this.ldt.isAfter(specLogTime.getLocalDateTime());
    }
    
    @Override
    public String toString () {
        return ldt.format(dtf);
    }
    
    
}
