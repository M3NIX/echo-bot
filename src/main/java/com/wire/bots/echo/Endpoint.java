package com.wire.bots.echo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wire.bots.echo.model.MessageIn;
import com.wire.bots.echo.model.MessageOut;
import com.wire.bots.sdk.tools.Logger;
import com.wire.bots.sdk.tools.Util;

import javax.websocket.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@ClientEndpoint(decoders = Endpoint._Decoder.class)
public class Endpoint {
    private final WebTarget target;
    private Session session;

    Endpoint(Client jersey) {
        String uri = String.format("https://services.%s/roman/conversation", Util.getDomain());
        target = jersey.target(uri);
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        Logger.debug("Websocket open: %s", session.getId());
    }

    @OnMessage
    public void onMessage(MessageIn payload) throws Exception {
        Logger.info("onMessage: `%s`", payload.type);

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
    }

    @OnClose
    public void onClose(Session closed, CloseReason reason) throws Exception {
        Logger.debug("Websocket closed: %s: reason: %s", closed.getId(), reason.getCloseCode());
    }

    public static class _Decoder implements Decoder.Text<MessageIn> {
        private final static ObjectMapper mapper = new ObjectMapper();

        @Override
        public MessageIn decode(String s) throws DecodeException {
            try {
                return mapper.readValue(s, MessageIn.class);
            } catch (IOException e) {
                throw new DecodeException(s, "oops", e);
            }
        }

        @Override
        public boolean willDecode(String s) {
            return s.startsWith("{") && s.endsWith("}");
        }

        @Override
        public void init(EndpointConfig config) {

        }

        @Override
        public void destroy() {

        }
    }
}
