package wordle;

public enum LetterCode {
    GREEN(0),   // Correct letter in correct position
    YELLOW(1),  // Letter exists in answer but wrong position
    GREY(2);    // Letter not in answer

    public final int value;

    LetterCode(int value) {
        this.value = value;
    }
}
