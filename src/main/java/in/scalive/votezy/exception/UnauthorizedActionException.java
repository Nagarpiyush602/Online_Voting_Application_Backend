package in.scalive.votezy.exception;

public class UnauthorizedActionException extends RuntimeException {
	
	public UnauthorizedActionException(String message) {
		super(message);
	}
}
