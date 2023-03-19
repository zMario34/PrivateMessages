package tech.zmario.privatemessages.common.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import tech.zmario.privatemessages.common.configuration.enums.SettingsConfiguration;
import tech.zmario.privatemessages.common.plugin.PrivateMessagesPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class ConnectionPoolManager {

    private final PrivateMessagesPlugin plugin;

    private HikariDataSource dataSource;

    private String hostname, port, database, username, password;

    public ConnectionPoolManager(PrivateMessagesPlugin plugin) {
        this.plugin = plugin;

        init();
        setupPool(SettingsConfiguration.MYSQL_ENABLED.getBoolean(plugin));
        makeTables();
    }

    private void init() {
        hostname = SettingsConfiguration.MYSQL_HOST.getString(plugin);
        port = SettingsConfiguration.MYSQL_PORT.getString(plugin);
        username = SettingsConfiguration.MYSQL_USERNAME.getString(plugin);
        password = SettingsConfiguration.MYSQL_PASSWORD.getString(plugin);
        database = SettingsConfiguration.MYSQL_DATABASE.getString(plugin);
    }

    private void setupPool(boolean useMySql) {
        HikariConfig config = new HikariConfig();

        if (useMySql) {

            config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);
            config.setUsername(username);
            config.setPassword(password);

            config.addDataSourceProperty("cachePrepStmts", true);
            config.addDataSourceProperty("prepStmtCacheSize", 250);
            config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
            config.addDataSourceProperty("useSSL", SettingsConfiguration.MYSQL_SSL.getBoolean(plugin));
            config.addDataSourceProperty("useUnicode", true);
            config.addDataSourceProperty("characterEncoding", "utf8");
            config.addDataSourceProperty("useLegacyDatetimeCode", false);
            config.addDataSourceProperty("serverTimezone", "UTC");

        } else {
            File file = new File(plugin.getBootstrap().getPluginFolder(), "database.db");

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    plugin.getBootstrap().getPluginLogger().log(Level.SEVERE, "Failed to create database file", e);
                    return;
                }
            }

            config.setJdbcUrl("jdbc:sqlite:" + file.getAbsolutePath());
            config.setDriverClassName("org.sqlite.JDBC");
        }

        config.setPoolName("PrivateMessages");

        dataSource = new HikariDataSource(config);
    }

    private void makeTables() {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `players_data` (uuid varchar(36) NOT NULL, social_spy boolean NOT NULL, toggled_messages boolean NOT NULL)");
             PreparedStatement preparedStatement1 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `ignored_players` (uuid varchar(36) NOT NULL, ignored varchar(36) NOT NULL)")) {

            preparedStatement.executeUpdate();
            preparedStatement1.executeUpdate();
        } catch (SQLException e) {
            plugin.getBootstrap().getPluginLogger().log(Level.SEVERE, "Failed to create tables", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}