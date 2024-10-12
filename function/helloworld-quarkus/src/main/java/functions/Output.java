package functions;

public class Output {
    private String message;

    public Output() {}

    public Output(String message) {
        this.message = "hiya, " + message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
