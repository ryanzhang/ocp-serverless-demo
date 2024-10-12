package functions;

public class Output {
    private String message;
    private String secret;

    public Output() {}

    public Output(String message, String secret) {
        this.message = message;
        this.secret = secret;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
