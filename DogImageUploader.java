import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DogImageUploader {

    private static final String DOG_API_URL = "https://dog.ceo/api/";

    // Функция для получения случайной картинки собаки
    public static String getRandomDogImage(String breed) throws IOException {
        URL url = new URL(DOG_API_URL + "breed/" + breed + "/images");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Парсинг JSON
        String jsonResponse = response.toString();
        List<String> images = parseJsonImages(jsonResponse);
        return images.isEmpty() ? null : images.get(new Random().nextInt(images.size()));
    }

    // Функция для загрузки изображения на Яндекс Диск
    public static void uploadToYandexDisk(String imageUrl, String yandexToken, String path) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
        connection.setRequestMethod("GET");

        InputStream inputStream = connection.getInputStream();
        Files.copy(inputStream, Paths.get(path));
        inputStream.close();

        System.out.println("Uploaded to " + path);
    }

    // Основная функция
    public static void uploadDogImages(String breed, String yandexToken) throws IOException {
        String imageUrl = getRandomDogImage(breed);
        if (imageUrl != null) {
            uploadToYandexDisk(imageUrl, yandexToken, breed + ".jpg");
        }

        // Получение подпород
        URL url = new URL(DOG_API_URL + "breed/" + breed + "/list");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        List<String> subBreeds = parseJsonSubBreeds(response.toString());

        for (String subBreed : subBreeds) {
            String subImageUrl = getRandomDogImage(breed + "/" + subBreed);
            if (subImageUrl != null) {
                uploadToYandexDisk(subImageUrl, yandexToken, breed + "_" + subBreed + ".jpg");
            }
        }
    }

    // Метод для парсинга изображений из JSON
    private static List<String> parseJsonImages(String jsonResponse) {
        String[] parts = jsonResponse.split("\"message\":");
        if (parts.length > 1) {
            String imagePart = parts[1].trim();
            imagePart = imagePart.substring(1, imagePart.length() - 1); // Убираем кавычки
            return Collections.singletonList(imagePart); // Возвращаем URL изображения
        }
        return null;
    }

    // Метод для парсинга подпород из JSON
    private static List<String> parseJsonSubBreeds(String jsonResponse) {
        List<String> subBreeds = new ArrayList<>();
        String[] parts = jsonResponse.split("\"message\":");
        if (parts.length > 1) {
            String breedsPart = parts[1].trim();
            breedsPart = breedsPart.substring(1, breedsPart.length() - 1); // Убираем скобки
            String[] breedsArray = breedsPart.split(",");

            for (String breed : breedsArray) {
                subBreeds.add(breed.replaceAll("\"", "").trim()); // Убираем кавычки
            }
        }
        return subBreeds;
    }

    public static void main(String[] args) {
        String yandexToken = "y0_AgAAAAARdPVwAADLWwAAAAEUlQ33AABJz4YVUENLoYTXZx360pg2-o8lvw";
        try {
            uploadDogImages("spaniel", yandexToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}