import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author : Ibrahim Sadigov
 * @mailto : isadigov4638@ada.edu.az
 * @created : 06 July, 2024
 **/
public class BaseService {

    private static final String CONTENT_TYPE = "application/json";
    private static final String APPLICATION_JSON = "application/json";
    private static final String COOKIE = "token=abc123";
    private static final String AUTH = "Basic YWRtaW46cGFzc3dvcmQxMjM=";
    private static final String URL = "https://restful-booker.herokuapp.com/booking/";

    public BaseService() {
        // Set the base URI for Rest Assured
        RestAssured.baseURI = URL;
        // Optionally, configure other settings
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    protected RequestSpecification requestSpecification() {
        return RestAssured.given()
                .header("Content-Type", CONTENT_TYPE)
                .header("Accept", APPLICATION_JSON)
                .header("Cookie", COOKIE)
                .header("Authorization", AUTH);
    }

    public ObjectNode createRequestBody(){
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode requestBody = mapper.createObjectNode();

        requestBody.put("firstname", "Ibrahim");
        requestBody.put("lastname", "Sadigov");
        requestBody.put("totalprice", 111);
        requestBody.put("depositpaid", true);

        ObjectNode bookingDates = mapper.createObjectNode();
        bookingDates.put("checkin", "2018-01-01");
        bookingDates.put("checkout", "2019-01-01");

        requestBody.set("bookingdates", bookingDates);
        requestBody.put("additionalneeds", "Breakfast");
        return requestBody;
    }

    protected Response createBookingService(){
        ObjectNode requestBody = createRequestBody();

        Response response = requestSpecification()
                .body(requestBody)
                .when()
                .post(URL)
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();

        assertThat("First name is incorrect", jsonPath.getString("booking.firstname"), equalTo("Ibrahim"));
        assertThat("Last name is incorrect", jsonPath.getString("booking.lastname"), equalTo("Sadigov"));
        assertThat("Total price is incorrect", jsonPath.getInt("booking.totalprice"), equalTo(111));
        assertThat("Deposit paid status is incorrect", jsonPath.getBoolean("booking.depositpaid"), equalTo(true));
        assertThat("Check-in date is incorrect", jsonPath.getString("booking.bookingdates.checkin"), equalTo("2018-01-01"));
        assertThat("Check-out date is incorrect", jsonPath.getString("booking.bookingdates.checkout"), equalTo("2019-01-01"));
        assertThat("Additional needs are incorrect", jsonPath.getString("booking.additionalneeds"), equalTo("Breakfast"));

        response.prettyPrint();

        System.out.println("CreateBookingService Assertions passed successfully!");

        return response;
    }

}
