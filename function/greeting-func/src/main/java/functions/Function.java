package functions;

import io.quarkus.funqy.Funq;
import jakarta.inject.Inject;

/**
 * Your Function class
 */
public class Function {

    @Inject
    GreetingService service;

    /**
     * Use the Quarkus Funqy extension for our function. This function simply echoes its input
     * @param input a Java bean
     * @return a Java bean
     */
    @Funq
    public Output function(Input input) {

        // Add business logic here

        return new Output(service.greet(input.getMessage()));
    }

}
