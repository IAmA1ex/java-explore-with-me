package ru.racticum.statsclient;

import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.statsdto.NoteDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class StatusClient {

    protected final RestTemplate rest;

    public StatusClient(RestTemplate rest) {
        this.rest = rest;
    }

    public ResponseEntity<Object> get(LocalDateTime start, LocalDateTime end,
                                      List<String> uris,
                                      boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "yyyy-MM-dd HH:mm:ss");
        String startTime = start.format(formatter);
        String endTime = end.format(formatter);
        Map<String, Object> parameters = Map.of(
                "start", startTime,
                "end", endTime,
                "uris", uris,
                "unique", unique
        );
        return makeAndSendRequest(HttpMethod.GET, "/stats", parameters, null);
    }

    protected ResponseEntity<Object> post(NoteDto body) {
        return makeAndSendRequest(HttpMethod.POST, "/hit", null, body);
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable NoteDto body) {
        HttpEntity<NoteDto> requestEntity = new HttpEntity<>(body, defaultHeaders());
        ResponseEntity<Object> shareitServerResponse;
        try {
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAs(Object.class));
        }
        return prepareGatewayResponse(shareitServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }

    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
