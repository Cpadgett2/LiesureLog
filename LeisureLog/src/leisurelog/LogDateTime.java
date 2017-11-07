
package leisurelog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class LogDateTime {
    
    private final LocalDateTime ldt;
    private static final DateTimeFormatter dtf = 
            DateTimeFormatter.ofPattern("MM'/'dd'/'yyyy' 'HH':'mm':'ss");
    
    // construct with current date time
    LogDateTime(){
        ldt = LocalDateTime.parse(LocalDateTime.now().format(dtf), dtf);
    }
    
    // construct with string specifying date and time
    LogDateTime(String dateTime){
        ldt = LocalDateTime.parse(dateTime, dtf);
    }
    
    // construct with date string and time string
    LogDateTime(String date, String time){
        ldt = LocalDateTime.parse(date + ' ' + time, dtf);
    }
    
    //getters
    public String getDate(){
        return toString().split(" ")[0];
    }
    
    public String getTime(){
        return toString().split(" ")[1];
    }
    
    public LocalDateTime getLocalDateTime(){
        return ldt;
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
