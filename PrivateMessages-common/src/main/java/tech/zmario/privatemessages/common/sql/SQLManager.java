package tech.zmario.privatemessages.common.sql;

import com.google.common.collect.Lists;
import tech.zmario.privatemessages.common.plugin.PrivateMessagesPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class SQLManager {

    private final PrivateMessagesPlugin plugin;
    private final ConnectionPoolManager pool;

    private final Executor executor = Executors.newFixedThreadPool(1);

    public SQLManager(PrivateMessagesPlugin plugin) {
        this.plugin = plugin;
        this.pool = new ConnectionPoolManager(plugin);
    }

    public CompletableFuture<Boolean> isPresent(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = pool.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT uuid FROM players_data WHERE uuid = ?")) {
                statement.setString(1, uuid.toString());

                return statement.execute();
            } catch (SQLException e) {
                plugin.getBootstrap().getPluginLogger().log(Level.SEVERE, "Failed to check if player is present in database.", e);
            }

            return false;
        }, executor);
    }

    public void createPlayer(UUID uuid) {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = pool.getConnection();
                 PreparedStatement statement = connection.prepareStatement("INSERT INTO players_data (uuid, social_spy, toggled_messages, toggled_sound) VALUES (?, ?, ?, ?);")) {
                statement.setString(1, uuid.toString());
                statement.setBoolean(2, false);
                statement.setBoolean(3, false);
                statement.setBoolean(4, true);

                statement.executeUpdate();
            } catch (SQLException e) {
                plugin.getBootstrap().getPluginLogger().log(Level.SEVERE, "Failed to add player to database.", e);
            }
        }, executor);
    }

    public CompletableFuture<List<String>> getIgnoredPlayers(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> ignoredPlayers = Lists.newArrayList();

            try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT ignored FROM ignored_players INNER JOIN players_data ON players_data.uuid = ignored_players.uuid WHERE players_data.uuid = ?")) {
                preparedStatement.setString(1, uuid.toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        ignoredPlayers.add(resultSet.getString("ignored"));
                    }
                }
            } catch (SQLException e) {
                plugin.getBootstrap().getPluginLogger().log(Level.SEVERE, "Failed to get ignored players", e);
            }

            return ignoredPlayers;
        }, executor);
    }

    public CompletableFuture<Boolean> getToggledStatus(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = pool.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT toggled_messages FROM players_data WHERE uuid = ?")) {
                preparedStatement.setString(1, uuid.toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    if (resultSet.next()) {
                        return resultSet.getBoolean("toggled_messages");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getBootstrap().getPluginLogger().log(Level.SEVERE, "Failed to get toggled status", e);
            }
            return false;
        }, executor);
    }

    public CompletableFuture<Boolean> getSoundStatus(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = pool.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT toggled_sound FROM players_data WHERE uuid = ?")) {
                preparedStatement.setString(1, uuid.toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getBoolean("toggled_sound");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getBootstrap().getPluginLogger().log(Level.SEVERE, "Failed to get toggled sound status", e);
            }

            return false;
        }, executor);
    }

    public CompletableFuture<Boolean> getSocialSpyStatus(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = pool.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT social_spy FROM players_data WHERE uuid = ?")) {
                preparedStatement.setString(1, uuid.toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getBoolean("social_spy");
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getBootstrap().getPluginLogger().log(Level.SEVERE, "Failed to get social spy status", e);
            }

            return false;
        }, executor);
    }

    public void removeIgnore(UUID uuid, String ignored) {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = pool.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ignored_players WHERE uuid = ? AND ignored = ?")) {
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(2, ignored);

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                plugin.getBootstrap().getPluginLogger().log(Level.SEVERE, "Failed to remove ignore", e);
            }
        }, executor);
    }

    public void addIgnore(UUID uuid, String ignored) {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ignored_players (uuid, ignored) VALUES (?, ?);")) {
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(2, ignored);

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getBootstrap().getPluginLogger().log(Level.SEVERE, "Failed to add ignore", e);
            }
        }, executor);
    }

    public void updateSocialSpy(UUID uuid, boolean status) {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players_data SET social_spy = ? WHERE uuid = ?")) {
                preparedStatement.setBoolean(1, status);
                preparedStatement.setString(2, uuid.toString());

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                plugin.getBootstrap().getPluginLogger().log(Level.SEVERE, "Failed to update social spy", e);
            }
        }, executor);
    }

    public void updateMessagesToggled(UUID uuid, boolean status) {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = pool.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players_data SET toggled_messages = ? WHERE uuid = ?")) {
                preparedStatement.setBoolean(1, status);
                preparedStatement.setString(2, uuid.toString());

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getBootstrap().getPluginLogger().log(Level.SEVERE, "Failed to update toggled messages", e);
            }
        }, executor);
    }

    public void updateSoundToggled(UUID uuid, boolean status) {
        CompletableFuture.runAsync(() -> {
            try (Connection connection = pool.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players_data SET toggled_sound = ? WHERE uuid = ?")) {
                preparedStatement.setBoolean(1, status);
                preparedStatement.setString(2, uuid.toString());

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getBootstrap().getPluginLogger().log(Level.SEVERE, "Failed to update toggled sound", e);
            }
        }, executor);
    }

    public void disable() {
        pool.closePool();
    }
}
