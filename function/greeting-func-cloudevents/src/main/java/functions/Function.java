package functions;

import io.quarkus.funqy.Funq;
import io.quarkus.funqy.knative.events.CloudEvent;
import io.quarkus.funqy.knative.events.CloudEventBuilder;
import jakarta.inject.Inject;

/**
 * Your Function class
 */
public class Function {

    /**
     * Use the Quarkus Funq extension for the function. This example
     * function simply echoes its input data.
     * @param input a CloudEvent
     * @return a CloudEvent
     */
    @Funq
    public String function(String cloudEventJson) {

        // Add your business logic here

        System.out.println(cloudEventJson);
        return cloudEventJson;
    }

}
