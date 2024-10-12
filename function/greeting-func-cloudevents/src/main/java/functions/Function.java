package functions;

import io.quarkus.funqy.Funq;
import io.quarkus.funqy.knative.events.CloudEvent;
import io.quarkus.funqy.knative.events.CloudEventBuilder;
import jakarta.inject.Inject;

/**
 * Your Function class
 */
public class Function {
    @Inject
    GreetingService service;

    /**
     * Use the Quarkus Funq extension for the function. This example
     * function simply echoes its input data.
     * @param input a CloudEvent
     * @return a CloudEvent
     */
    @Funq
    public CloudEvent<Output> function(CloudEvent<Input> input) {

        // Add your business logic here

        System.out.println(input);
        Output output = new Output(service.greet(input.data().getMessage()));
        return CloudEventBuilder.create().build(output);
    }

}
