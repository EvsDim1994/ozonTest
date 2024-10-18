import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class DogImageIntegrationTest {

    private DogImageUploader dogImageUploader;

    @BeforeEach
    public void setUp() {
        dogImageUploader = new DogImageUploader();
        RestAssured.baseURI = "https://dog.ceo/api"; // Пример API
    }

    @Test
    public void testGetRandomDogImage() {
        // Выполняем GET-запрос для получения случайного изображения
        given()
                .when()
                .get("/breeds/image/random")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", notNullValue());
    }

    @Test
    public void testUploadToYandexDisk() {
        String imageUrl = "http://example.com/image.jpg"; // Укажите правильный URL
        String yandexToken = "mockToken";
        String path = "test.jpg";

        dogImageUploader.uploadToYandexDisk(imageUrl, yandexToken, path);

        // Проверяем, что файл создается
        assertTrue(Files.exists(Paths.get(path)));
        Files.deleteIfExists(Paths.get(path)); // Удаляем файл после теста
    }

    @Test
    public void testParseJsonImages() {
        String jsonResponse = "{\"message\":[\"image1.jpg\", \"image2.jpg\"], \"status\":\"success\"}";
        List<String> images = dogImageUploader.parseJsonImages(jsonResponse);

        assertNotNull(images);
        assertEquals(2, images.size());
        assertTrue(images.contains("image1.jpg"));
        assertTrue(images.contains("image2.jpg"));
    }

    @Test
    public void testParseJsonSubBreeds() {
        String jsonResponse = "{\"message\":[\"subBreed1\", \"subBreed2\"], \"status\":\"success\"}";
        List<String> subBreeds = dogImageUploader.parseJsonSubBreeds(jsonResponse);

        assertNotNull(subBreeds);
        assertEquals(2, subBreeds.size());
        assertTrue(subBreeds.contains("subBreed1"));
        assertTrue(subBreeds.contains("subBreed2"));
    }
}