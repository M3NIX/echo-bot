package com.wire.bots.echo.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageOut {
    public String type;
    public String text;
    public String image;
}
