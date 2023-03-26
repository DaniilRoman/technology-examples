package ru.uskov.dmitry.transferapp.rest.controller;

import ru.uskov.dmitry.http.rest.server.api.HttpMethod;
import ru.uskov.dmitry.http.rest.server.api.annotation.Path;

@Path("/healthcheck")
public class HealthcheckController {

    @Path(value = "", method = HttpMethod.GET)
    public String healthcheck() {
        return "OK";
    }
}
