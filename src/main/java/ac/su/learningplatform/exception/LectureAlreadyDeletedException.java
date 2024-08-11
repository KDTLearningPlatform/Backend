package ac.su.learningplatform.exception;

public class LectureAlreadyDeletedException extends RuntimeException {
    public LectureAlreadyDeletedException(String message) {
        super(message);
    }
}