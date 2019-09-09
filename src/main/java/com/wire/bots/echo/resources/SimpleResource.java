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
package com.wire.bots.echo.resources;

import com.codahale.metrics.annotation.Timed;
import com.wire.bots.echo.Service;
import com.wire.bots.echo.model.Simple;
import com.wire.bots.echo.Database;
import com.wire.bots.echo.Config;
import com.wire.bots.sdk.ClientRepo;
import com.wire.bots.sdk.WireClient;
import com.wire.bots.sdk.exceptions.MissingStateException;
import com.wire.bots.sdk.tools.Logger;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.UUID;
import java.util.Objects;

@Api
@Path("/alert/{username}")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SimpleResource {
    private final ClientRepo repo;
    private final Database db;

    public SimpleResource(ClientRepo repo) {
        this.repo = repo;
        db = new Database(Service.instance.getConfig());
    }

    @POST
    @Timed
    @ApiOperation(value = "Post message on Wire")
    public Response webhook(@ApiParam("Bearer token") @NotNull @Valid @HeaderParam("Authorization") String token,
                            @ApiParam @PathParam("username") String username,
                            @ApiParam @NotNull @Valid Simple payload) {
        try {
            String challenge = String.format("Bearer %s", Service.instance.getConfig().getAlertToken());
            if (!Objects.equals(token, challenge)) {
              Logger.warning("SimpleResource: Wrong Authorization: %s", token);
              return Response.
                      status(401).
                      build();
            }

            try {
                UUID botId = UUID.fromString(username);
                WireClient client = repo.getClient(botId);
                client.sendText(payload.message);
            } catch (Exception ex) {
              for (String botId : db.getBotsByUsername(username)) {
                Logger.info("Following bots for alert: %s", botId);
                WireClient client = repo.getClient(UUID.fromString(botId));
                client.sendText(payload.message);
              }
            }

            Logger.info("%s SimpleResource: New payload texted", username);

            return Response.
                    accepted().
                    build();
        } catch (MissingStateException e) {
            Logger.info("%s SimpleResource: %s", username, e);
            return Response.
                    status(404).
                    build();
        } catch (Exception e) {
            Logger.error("%s SimpleResource: %s", username, e);
            return Response.
                    serverError().
                    build();
        }
    }
}
