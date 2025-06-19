package finance_manager.exception.custom_exception;

public class ConflictException extends RuntimeException{
    public ConflictException(String message){
        super(message);
    }
}
