package com.wire.bots.echo;

import com.wire.bots.echo.model.MessageIn;
import com.wire.bots.echo.model.MessageOut;
import com.wire.bots.sdk.tools.Util;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/webhook")
@Produces(MediaType.APPLICATION_JSON)
public class InboundResource {
    private final WebTarget target;

    InboundResource(Client client) {
        String uri = String.format("https://services.%s/roman/conversation", Util.getDomain());
        target = client.target(uri);
    }

    @POST
    public Response webhook(@NotNull @HeaderParam("Authorization") String auth,
                            @Valid MessageIn payload) {

        switch (payload.type) {
            case "conversation.init": {
                MessageOut messageOut = new MessageOut();
                messageOut.type = "text";
                messageOut.text = "Hi there!";
                target.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", payload.token)
                        .post(Entity.entity(messageOut, MediaType.APPLICATION_JSON));
            }
            break;
            case "conversation.new_text": {
                MessageOut messageOut = new MessageOut();
                messageOut.type = "text";
                messageOut.text = "You wrote: " + payload.text;
                target.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", payload.token)
                        .post(Entity.entity(messageOut, MediaType.APPLICATION_JSON));
            }
            break;
            case "conversation.new_image": {
                MessageOut messageOut = new MessageOut();
                messageOut.type = "image";
                messageOut.image = payload.image;
                target.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", payload.token)
                        .post(Entity.entity(messageOut, MediaType.APPLICATION_JSON));
            }
            break;
            case "conversation.user_joined": {
                MessageOut messageOut = new MessageOut();
                messageOut.type = "text";
                messageOut.text = "Hello!";
                target.request(MediaType.APPLICATION_JSON)
                        .header("Authorization", payload.token)
                        .post(Entity.entity(messageOut, MediaType.APPLICATION_JSON));
            }
            break;
        }

        return Response.
                ok().
                build();
    }
}
