package br.com.alura.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class CreateUserService {

    private final Connection connection;

    CreateUserService() throws SQLException {
        String url = "jdbc:sqlite:target/users_database.db";
        this.connection = DriverManager.getConnection(url);
        connection.createStatement().execute("CREATE TABLE Users (" +
                "uuid VARCHAR(200) PRIMARY KEY," +
                "email VARCHAR(200))");
    }

    public static void main(String[] args) throws SQLException {
        var createUserService = new CreateUserService();
        try (var service = new KafkaService<>(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER", createUserService::parse, Order.class,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Order> record) throws SQLException {
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.value());
        Order order = record.value();

        if (isNewUser((String) order.getEmail())) {
            insetNewUser((String) order.getEmail());
        }
    }

    private void insetNewUser(String email) throws SQLException {
        var insert = connection.prepareStatement("INSERT INTO users(uuid, email) VALUES(?, ?)");
        insert.setString(1, "uuid");
        insert.setString(2, email);
        insert.execute();

        System.out.println("User uuid and " + email + " added");
    }

    private boolean isNewUser(String email) throws SQLException {
        var exists = connection.prepareStatement("SELECT * FROM Users WHERE email = ? LIMIT 1;");
        exists.setString(1, email);

        var results = exists.executeQuery();
        return !results.next();
    }
}
