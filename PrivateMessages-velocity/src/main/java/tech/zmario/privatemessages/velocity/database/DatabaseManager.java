package tech.zmario.privatemessages.velocity.database;

import com.google.common.collect.Lists;
import com.velocitypowered.api.proxy.Player;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.byteflux.libby.Library;
import tech.zmario.privatemessages.velocity.PrivateMessagesVelocity;
import tech.zmario.privatemessages.velocity.enums.SettingsConfiguration;
import tech.zmario.privatemessages.velocity.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DatabaseManager {

    private final PrivateMessagesVelocity plugin;
    private HikariDataSource dataSource;
    private Connection connection;

    public DatabaseManager(PrivateMessagesVelocity plugin, boolean mysql) {
        this.plugin = plugin;

        setup(mysql);
        makeTables();
    }

    private void setup(boolean mysql) {
        if (mysql) {
            Library hikariCp = Library.builder().groupId("com{}zaxxer").artifactId("HikariCP").version("4.0.3").build();

            Library mysqlConnector = Library.builder().groupId("mysql").artifactId("mysql-connector-java").version("8.0.19").build();

            plugin.getLibraryManager().loadLibrary(hikariCp);
            plugin.getLibraryManager().loadLibrary(mysqlConnector);

            HikariConfig config = new HikariConfig();

            config.setJdbcUrl("jdbc:mysql://" + SettingsConfiguration.MYSQL_HOST.getString() + ":" + SettingsConfiguration.MYSQL_PORT.getString() + "/" + SettingsConfiguration.MYSQL_DATABASE.getString());
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setUsername(SettingsConfiguration.MYSQL_USERNAME.getString());
            config.setPassword(SettingsConfiguration.MYSQL_PASSWORD.getString());
            config.setPoolName("PrivateMessages");

            dataSource = new HikariDataSource(config);
        } else {
            Library sqLite = Library.builder().groupId("org{}xerial").artifactId("sqlite-jdbc").version("3.36.0.3").build();

            plugin.getLibraryManager().loadLibrary(sqLite);

            connection = getConnection();
        }
    }

    private void makeTables() {
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `players_data` (uuid varchar(36) NOT NULL, ignores longtext, social_spy boolean NOT NULL, toggled_messages boolean NOT NULL)")) {

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().error("Failed to create tables. Error message: " + e.getMessage());
        }
    }

    public boolean isPresent(Player player) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players_data WHERE uuid = ?")) {

                preparedStatement.setString(1, player.getUniqueId().toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().error("Failed to check if player is present. Error message: " + e.getMessage());
            }
            return false;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ignored) {
        }

        return false;
    }

    public void createPlayer(Player player) {
        plugin.getProxyServer().getScheduler().buildTask(plugin, () -> {
            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO players_data (uuid, ignores, social_spy, toggled_messages) VALUES (?, ?, ?, ?);")) {

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, null);
                statement.setBoolean(3, false);
                statement.setBoolean(4, false);

                statement.executeUpdate();
                
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().error("Failed to add player to database. Error message: " + e.getMessage());
            }
        }).schedule();
    }

    public Connection getConnection() {
        try {
            if (dataSource != null) {

                return dataSource.getConnection();

            } else {
                File databaseFile = new File(plugin.getPluginFolder(), "data.db");

                if (!databaseFile.exists()) {
                    try {
                        databaseFile.createNewFile();

                    } catch (IOException e) {
                        e.printStackTrace();
                        plugin.getLogger().error("Failed to create database file. Error message: " + e.getMessage());

                    }
                }

                try {
                    if (connection != null && !connection.isClosed()) {
                        return connection;
                    }

                    Class.forName("org.sqlite.JDBC");
                    connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());

                    return connection;
                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                    plugin.getLogger().error("Failed to connect to database. Error message: " + e.getMessage());
                }

                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().error("Failed to get connection. Error message: " + e.getMessage());
        }

        return null;
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        } else {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().error("Failed to close connection. Error message: " + e.getMessage());
            }
        }
    }

    public List<String> getIgnoredPlayers(Player player) {
        List<String> ignoredPlayers = Lists.newArrayList();
        CompletableFuture.runAsync(() -> {

            try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT ignores FROM players_data WHERE uuid = ?")) {

                preparedStatement.setString(1, player.getUniqueId().toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    if (resultSet.next() && resultSet.getString("ignores") != null) {
                        ignoredPlayers.addAll(Arrays.asList(resultSet.getString("ignores").split(";")));

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().error("Failed to get ignored players. Error message: " + e.getMessage());
            }
        });

        return ignoredPlayers;
    }

    public boolean getToggledStatus(Player player) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
            try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT toggled_messages FROM players_data WHERE uuid = ?")) {

                preparedStatement.setString(1, player.getUniqueId().toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {

                    if (resultSet.next()) {
                        return resultSet.getBoolean("toggled_messages");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().error("Failed to get toggled status. Error message: " + e.getMessage());
            }
            return false;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ignored) {
        }

        return false;
    }

    public boolean getSocialSpyStatus(Player player) {
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {

            try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT social_spy FROM players_data WHERE uuid = ?")) {

                preparedStatement.setString(1, player.getUniqueId().toString());

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getBoolean("social_spy");
                    }

                }
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().error("Failed to get social spy status. Error message: " + e.getMessage());
            }

            return false;
        });

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ignored) {
        }

        return false;
    }

    public void updateIgnores(Player player, List<String> ignoresList) {
        plugin.getProxyServer().getScheduler().buildTask(plugin, () -> {

            try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players_data SET ignores = ? WHERE uuid = ?")) {

                preparedStatement.setString(1, Utils.listToString(ignoresList));
                preparedStatement.setString(2, player.getUniqueId().toString());

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().error("Failed to update ignored players. Error message: " + e.getMessage());
            }

        }).schedule();
    }

    public void updateSocialSpy(Player player, boolean status) {
        plugin.getProxyServer().getScheduler().buildTask(plugin, () -> {

            try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players_data SET social_spy = ? WHERE uuid = ?")) {

                preparedStatement.setBoolean(1, status);
                preparedStatement.setString(2, player.getUniqueId().toString());

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().error("Failed to update social spy. Error message: " + e.getMessage());
            }
        }).schedule();
    }

    public void updateMessagesToggled(Player player, boolean status) {
        plugin.getProxyServer().getScheduler().buildTask(plugin, () -> {
            try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players_data SET toggled_messages = ? WHERE uuid = ?")) {

                preparedStatement.setBoolean(1, status);
                preparedStatement.setString(2, player.getUniqueId().toString());

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().error("Failed to update toggled messages. Error message: " + e.getMessage());
            }
        }).schedule();
    }
}
