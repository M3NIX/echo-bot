package com.wire.bots.echo;

import com.wire.bots.echo.Config;
import com.wire.bots.sdk.Configuration;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Database {
    private final Config conf;

    public Database(Config conf) {
        this.conf = conf;
    }

    boolean insertBot(UUID botId, UUID convId) throws Exception {
        try (Connection c = newConnection()) {
            PreparedStatement stmt = c.prepareStatement("INSERT INTO Alert (botId, convId) VALUES (?, ?)");
            stmt.setObject(1, botId);
            stmt.setObject(2, convId);
            return stmt.executeUpdate() == 1;
        }
    }

    public ArrayList<String> getBots() throws Exception {
        ArrayList<String> ret = new ArrayList<>();
        try (Connection c = newConnection()) {
            PreparedStatement stmt = c.prepareStatement("SELECT botId FROM Alert");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                ret.add(resultSet.getString("botId"));
            }
        }
        return ret;
    }

    boolean deleteBot(UUID botId) throws SQLException {
        try (Connection c = newConnection()) {
            PreparedStatement stmt = c.prepareStatement("DELETE FROM Alert WHERE botId = ?");
            stmt.setObject(1, botId);
            return stmt.executeUpdate() == 1;
        }
    }

    public ArrayList<String> getBotsByConversation(String convId) throws SQLException {
        ArrayList<String> ret = new ArrayList<>();
        try (Connection c = newConnection()) {
            PreparedStatement stmt = c.prepareStatement("SELECT botId FROM Alert WHERE convId = ?");
            stmt.setString(1, convId);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                ret.add(resultSet.getString("botId"));
            }
        }
        return ret;
    }

    private Connection newConnection() throws SQLException {
        String url = String.format("jdbc:sqlite:%s", conf.getSQLiteFile());
        return DriverManager.getConnection(url);
    }

}
