//
// Wire
// Copyright (C) 2016 Wire Swiss GmbH
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see http://www.gnu.org/licenses/.
//

package com.wire.bots.echo;

import com.wire.bots.sdk.MessageHandlerBase;
import com.wire.bots.sdk.Server;
import com.wire.bots.sdk.tools.Logger;
import com.wire.bots.sdk.tools.Util;
import io.dropwizard.setup.Environment;
import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.net.URI;

public class Service extends Server<Config> {
    static Service instance;

    public static void main(String[] args) throws Exception {
        instance = new Service();
        instance.run(args);
    }

    @Override
    protected void initialize(Config config, Environment env) {
    }

    @Override
    protected MessageHandlerBase createHandler(Config config, Environment env) {
        return new MessageHandler();
    }

    @Override
    protected void onRun(Config config, Environment env) {
        addResource(new InboundResource(getClient()), env);

        try {
            ClientManager client = ClientManager.createClient();

            client.getProperties().put(ClientProperties.RECONNECT_HANDLER, new ClientManager.ReconnectHandler() {
                @Override
                public boolean onDisconnect(CloseReason closeReason) {
                    Logger.info("Websocket onDisconnect: reason: %s", closeReason.getCloseCode());
                    return true;
                }

                @Override
                public boolean onConnectFailure(Exception e) {
                    Logger.warning("Websocket onConnectFailure: reason: %s", e);
                    return true;
                }
            });

            String wss = String.format("wss://services.%s/roman/await/%s", Util.getDomain(), config.proxyToken);
            Endpoint endpoint = new Endpoint(getClient());

            Session session = client.connectToServer(endpoint, new URI(wss));
            Logger.info("Websocket connected: %s", session.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}