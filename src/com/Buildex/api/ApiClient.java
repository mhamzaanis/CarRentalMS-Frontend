package com.Buildex.api;

import com.Buildex.auth.*;
import com.Buildex.model.Booking;
import com.Buildex.model.BookingModel;
import com.Buildex.model.Car;
import com.Buildex.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient client;
    private final ObjectMapper mapper;
    private volatile String authHeader = null;
    private final String instanceId = UUID.randomUUID().toString();

    public ApiClient() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.mapper.findAndRegisterModules(); // For LocalDate support
        System.out.println("Created ApiClient instance: " + instanceId);
    }

    public void setCredentials(String email, String password) {
        String credentials = email + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        this.authHeader = "Basic " + encodedCredentials;
        System.out.println("Set auth header for ApiClient " + instanceId + ": " + authHeader);
    }

    public String getInstanceId() {
        return instanceId;
    }

    private synchronized HttpRequest.Builder addAuthHeader(HttpRequest.Builder builder) {
        if (authHeader != null) {
            System.out.println("Adding auth header: " + authHeader);
            builder.header("Authorization", authHeader);
        } else {
            System.out.println("No auth header set for ApiClient " + instanceId + "!");
        }
        return builder;
    }

    public User login(String email, String password) throws Exception {
        String credentials = email + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        String tempAuthHeader = "Basic " + encodedCredentials;
        String json = mapper.writeValueAsString(new LoginRequest(email, password));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users/login"))
                .header("Content-Type", "application/json")
                .header("Authorization", tempAuthHeader)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Login response status: " + response.statusCode());
        System.out.println("Login response body: " + response.body());
        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), User.class);
        }
        return null;
    }

    // User APIs
    public List<User> getAllUsers() throws Exception {
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/users"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return Arrays.asList(mapper.readValue(response.body(), User[].class));
    }

    public User getUserById(Long id) throws Exception {
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/users/" + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 404) {
            return null;
        }
        return mapper.readValue(response.body(), User.class);
    }

    public User createUser(User user) throws Exception {
        String json = mapper.writeValueAsString(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 201) {
            return mapper.readValue(response.body(), User.class);
        }
        return null;
    }

    public void updateCar(Car car) throws Exception {
        String json = mapper.writeValueAsString(car);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/cars/" + car.getId()))
                .header("Content-Type", "application/json")
                .header("Authorization", authHeader != null ? authHeader : "")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to update car: HTTP " + response.statusCode());
        }
    }

    public void deleteUser(Long id) throws Exception {
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/users/" + id))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("deleteUser response status for user " + id + ": " + response.statusCode());
        System.out.println("deleteUser response body for user " + id + ": " + response.body());
        if (response.statusCode() != 204 && response.statusCode() != 200) {
            throw new Exception("Failed to delete user " + id + ": HTTP " + response.statusCode() + " - " + response.body());
        }
    }

    // Car APIs
    public List<Car> getAllCars() throws Exception {
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/cars"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return Arrays.asList(mapper.readValue(response.body(), Car[].class));
    }

    public List<Car> getAvailableCars() throws Exception {
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/cars/available"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("getAvailableCars response status: " + response.statusCode());
        System.out.println("getAvailableCars response body: " + response.body());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to fetch available cars: HTTP " + response.statusCode());
        }
        String responseBody = response.body();
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(mapper.readValue(responseBody, Car[].class));
    }

    public Car getCarByNoPlate(String noPlate) throws Exception {
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/cars/noPlate/" + noPlate))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), Car.class);
    }

    public Car addCar(Car car) throws Exception {
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/cars"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(car)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new Exception("Failed to add car: " + response.body());
        }
        return mapper.readValue(response.body(), Car.class);
    }

    public void deleteCar(Long id) throws Exception {
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/cars/" + id))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    // Booking APIs
    public List<Booking> getAllBookings() throws Exception {
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/bookings"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("getAllBookings response status: " + response.statusCode());
        System.out.println("getAllBookings response body: " + response.body());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to fetch bookings: HTTP " + response.statusCode());
        }
        String responseBody = response.body();
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(mapper.readValue(responseBody, Booking[].class));
    }

    public List<Booking> getBookingsByUserId(Long userId) throws Exception {
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/bookings/user/" + userId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("getBookingsByUserId response status for user " + userId + ": " + response.statusCode());
        System.out.println("getBookingsByUserId response body for user " + userId + ": " + response.body());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to fetch bookings for user " + userId + ": HTTP " + response.statusCode());
        }
        String responseBody = response.body();
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(mapper.readValue(responseBody, Booking[].class));
    }

    public Booking getBookingById(Long id) throws Exception {
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/bookings/" + id))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("getBookingById response status for booking " + id + ": " + response.statusCode());
        System.out.println("getBookingById response body for booking " + id + ": " + response.body());
        if (response.statusCode() == 404) {
            return null;
        }
        return mapper.readValue(response.body(), Booking.class);
    }

    public void updateBooking(Booking booking) throws Exception {
        String json = mapper.writeValueAsString(booking);
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/bookings/" + booking.getId() + "/" + booking.isPaid()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to update booking: HTTP " + response.statusCode());
        }
    }

    public void clearCredentials() {
        this.authHeader = null;
        System.out.println("Cleared auth header for ApiClient: " + getInstanceId());
    }

    public Booking createBooking(BookingModel booking) throws Exception {
        String json = mapper.writeValueAsString(booking);
        System.out.println("Sending booking JSON: " + json);
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/bookings"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Create booking response: " + response.statusCode() + " - " + response.body());
        if (response.statusCode() != 201) {
            throw new Exception("Failed to create booking: HTTP " + response.statusCode() + " - " + response.body());
        }
        return mapper.readValue(response.body(), Booking.class);
    }

    public void deleteBooking(Long id) throws Exception {
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/bookings/" + id))
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("deleteBooking response status for booking " + id + ": " + response.statusCode());
        System.out.println("deleteBooking response body for booking " + id + ": " + response.body());
        if (response.statusCode() != 204 && response.statusCode() != 200) {
            throw new Exception("Failed to delete booking " + id + ": HTTP " + response.statusCode() + " - " + response.body());
        }
    }

    public void payBooking(Long bookingId) throws Exception {
        Booking booking = getBookingById(bookingId);
        if (booking == null) {
            throw new Exception("Booking with ID " + bookingId + " not found");
        }
        booking.setPaid(true);
        updateBooking(booking);
    }

    public void updateCarAvailability(Long carId, boolean isAvailable) throws Exception {
        Map<String, Boolean> payload = new HashMap<>();
        payload.put("isAvailable", isAvailable);
        String json = mapper.writeValueAsString(payload);
        HttpRequest request = addAuthHeader(HttpRequest.newBuilder())
                .uri(URI.create(BASE_URL + "/cars/" + carId + "/availability"))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("updateCarAvailability response status for car " + carId + ": " + response.statusCode());
        System.out.println("updateCarAvailability response body for car " + carId + ": " + response.body());
        if (response.statusCode() != 200) {
            throw new Exception("Failed to update car availability: HTTP " + response.statusCode() + " - " + response.body());
        }
    }
}