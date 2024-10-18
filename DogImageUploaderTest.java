import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DogImageUploaderTest {

    private DogImageUploader dogImageUploader;

    @BeforeEach
    public void setUp() {
        dogImageUploader = new DogImageUploader();
    }

    @Test
    public void testGetRandomDogImage() throws Exception {
        String breed = "spaniel";
        String jsonResponse = "{\"message\":[\"image1.jpg\", \"image2.jpg\"], \"status\":\"success\"}";

        // Мокаем URL и HttpURLConnection
        URL mockUrl = mock(URL.class);
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockUrl.openConnection()).thenReturn(mockConnection);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(jsonResponse.getBytes()));
        whenNew(URL.class).withArguments(any(String.class)).thenReturn(mockUrl);

        List<String> images = dogImageUploader.parseJsonImages(jsonResponse);
        assertNotNull(images);
        assertEquals(2, images.size());
        assertTrue(images.contains("image1.jpg") || images.contains("image2.jpg"));
    }

    @Test
    public void testUploadToYandexDisk() throws Exception {
        String imageUrl = "http://example.com/image.jpg";
        String yandexToken = "mockToken";
        String path = "test.jpg";

        // Мокаем URL и HttpURLConnection
        URL mockUrl = mock(URL.class);
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockUrl.openConnection()).thenReturn(mockConnection);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
        whenNew(URL.class).withArguments(any(String.class)).thenReturn(mockUrl);

        // Выполнение загрузки
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