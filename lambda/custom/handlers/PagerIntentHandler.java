package handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.AutoPageCommand;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.ExecuteCommandsDirective;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.RenderDocumentDirective;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class PagerIntentHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("PagerIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        String speechText = "This is the pager template!";
        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<HashMap<String, Object>> documentMapType = new TypeReference<HashMap<String, Object>>() { };
            Map<String, Object> document = mapper.readValue(new File("pager.json"), documentMapType);

            TypeReference<HashMap<String, Object>> dataSourceMapType = new TypeReference<HashMap<String, Object>>() { };
            Map<String, Object> dataSource = mapper.readValue(new File("pagerTemplateData.json"), dataSourceMapType);

            RenderDocumentDirective documentDirective = RenderDocumentDirective.builder()
                    .withToken("pagerToken")
                    .withDocument(document)
                    .withDatasources(dataSource)
                    .build();

            ExecuteCommandsDirective commandsDirective = ExecuteCommandsDirective.builder()
                    .withToken("pagerToken")
                    .withCommands(Collections.singletonList(AutoPageCommand.builder()
                            .withComponentId("pagerComponentId")
                            .withDuration(5000)
                            .build()))
                    .build();

            return input.getResponseBuilder()
                    .withSpeech(speechText)
                    .addDirective(documentDirective)
                    .addDirective(commandsDirective)
                    .build();
        } catch (IOException e) {
            throw new AskSdkException("Unable to read or deserialize pager data", e);
        }
    }
}