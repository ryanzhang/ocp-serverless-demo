package functions;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SecretService {

    public String getSecret(){
        return System.getenv("secret_token");
    }
}
