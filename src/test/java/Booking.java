import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author : Ibrahim Sadigov
 * @mailto : isadigov4638@ada.edu.az
 * @created : 01 July, 2024
 **/
public class Booking extends BaseService{

    private static final String URL = "https://restful-booker.herokuapp.com/booking/";

    @Test
    public void getAllBookings(){

        Response response = given()
                .when()
                .get(URL);
        response
                .then()
                //.log().all()
                .statusCode(200);

        response.prettyPrint();

        JsonPath jsonPath = response.jsonPath();

        assertThat("Booking list is empty", jsonPath.getList("$"), notNullValue());
        assertThat("Booking list size is less than expected", jsonPath.getList("$").size(), greaterThan(0));

        if (!jsonPath.getList("$").isEmpty()) {
            assertThat("First booking ID is null", jsonPath.get("bookingid[0]"), notNullValue());
        }

        System.out.println("GetAllBookings Assertions passed successfully!");

    }

    @Test
    public void getBookingId(){

        Response resp = createBookingService();

        int id = resp.jsonPath().getInt("bookingid");

        Response response = given()
                .when()
                .get(URL + id);
        response
                .then()
                .statusCode(200);
        response.prettyPrint();

        // Extract the response body as JsonPath
        JsonPath jsonPath = response.jsonPath();

        // Assert the response body with descriptive messages
        assertThat("First name is incorrect", jsonPath.getString("firstname"), equalTo("Ibrahim"));
        assertThat("Last name is incorrect", jsonPath.getString("lastname"), equalTo("Sadigov"));
        assertThat("Total price is incorrect", jsonPath.getInt("totalprice"), equalTo(111));
        assertThat("Deposit paid status is incorrect", jsonPath.getBoolean("depositpaid"), equalTo(true));
        assertThat("Check-in date is incorrect", jsonPath.getString("bookingdates.checkin"), equalTo("2018-01-01"));
        assertThat("Check-out date is incorrect", jsonPath.getString("bookingdates.checkout"), equalTo("2019-01-01"));
        assertThat("Additional needs are incorrect", jsonPath.getString("additionalneeds"), equalTo("Breakfast"));

        // Print confirmation message
        System.out.println("GetBookingId Assertions passed successfully!");

    }

    @Test
    public void createBooking(){
        createBookingService();
    }

    @Test
    public void updateBooking(){

        Response resp = createBookingService();

        int id = resp.jsonPath().getInt("bookingid");

        ObjectNode requestBody = createRequestBody();

        Response response = requestSpecification()
                .body(requestBody)
                .when().put(URL + id)
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();

        assertThat("First name is incorrect", jsonPath.getString("firstname"), equalTo("Ibrahim"));
        assertThat("Last name is incorrect", jsonPath.getString("lastname"), equalTo("Sadigov"));
        assertThat("Total price is incorrect", jsonPath.getInt("totalprice"), equalTo(111));
        assertThat("Deposit paid status is incorrect", jsonPath.getBoolean("depositpaid"), equalTo(true));
        assertThat("Check-in date is incorrect", jsonPath.getString("bookingdates.checkin"), equalTo("2018-01-01"));
        assertThat("Check-out date is incorrect", jsonPath.getString("bookingdates.checkout"), equalTo("2019-01-01"));
        assertThat("Additional needs are incorrect", jsonPath.getString("additionalneeds"), equalTo("Breakfast"));

        response.prettyPrint();

        System.out.println("UpdateBooking Assertions passed successfully!");

    }

    @Test
    public void partialUpdateBooking(){

        Response resp = createBookingService();

        int id = resp.jsonPath().getInt("bookingid");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode requestBody = mapper.createObjectNode();

        requestBody.put("firstname", "Ibrahim");
        requestBody.put("lastname", "Sadigov");

        Response response = requestSpecification()
                .body(requestBody)
                .when().patch(URL + id)
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();

        assertThat("First name is incorrect", jsonPath.getString("firstname"), equalTo("Ibrahim"));
        assertThat("Last name is incorrect", jsonPath.getString("lastname"), equalTo("Sadigov"));

        response.prettyPrint();

        System.out.println("PartialUpdateBooking Assertions passed successfully!");
    }

    @Test
    public void deleteBooking(){

        Response resp = createBookingService();

        int id = resp.jsonPath().getInt("bookingid");

        Response response = requestSpecification()
                .when()
                .delete(URL + id)
                .then()
                .statusCode(201)
                .extract().response();

        response.prettyPrint();

        Response getResponse = given()
                .when()
                .get(URL + id)
                .then()
                .statusCode(404) // Assuming the API returns 404 for a deleted resource
                .extract()
                .response();

        System.out.println("DELETE request assertions passed successfully!");
    }


}
