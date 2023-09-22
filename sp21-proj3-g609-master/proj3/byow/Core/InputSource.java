package byow.Core;

/**
 * Created by hug.
 */
public interface InputSource {
    /** Always returns lowercase inputs */
    char getNextKey();
    boolean possibleNextInput();
}
