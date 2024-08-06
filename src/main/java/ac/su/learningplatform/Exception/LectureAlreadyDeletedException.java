package ac.su.learningplatform.Exception;

public class LectureAlreadyDeletedException extends RuntimeException {
    public LectureAlreadyDeletedException(String message) {
        super(message);
    }
}