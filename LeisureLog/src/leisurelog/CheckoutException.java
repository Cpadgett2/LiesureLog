
package leisurelog;

/**
 * Exception thrown when attempting illegal check-out
 * @author TeamLeisure
 */

public class CheckoutException extends Exception {
    
    private final Marine m;    

    CheckoutException(String message, Marine m){
        super(message);
        this.m = m;
    }
    
    public Marine getMarine(){
        return m;
    }
    
}
