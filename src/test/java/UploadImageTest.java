import com.fasterxml.jackson.databind.ObjectMapper;
import com.napnap.NapNapApplication;
import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = NapNapApplication.class)
public class UploadImageTest {

    private static final String GITHUB_API_URL = "https://gitee.com/api/v5";
    private static final String REPO_OWNER = "zengmark";
    private static final String REPO_NAME = "napnapimages";
//    private static final String BRANCH = "main";
    private static final String TOKEN = "7b92890f758c98312becb7f9c368af6d";

    @Test
    public void test1() {
        String imagePath = "C:\\Users\\13123\\Desktop\\安卓实训\\Snipaste_2024-06-12_15-15-44.png";
        String fileName = "image1.png";
        try {
            String imageUrl = uploadImageToGitHub(imagePath, fileName);
            System.out.println("Uploaded Image URL: " + imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String uploadImageToGitHub(String imagePath, String fileName) throws IOException {
        // Read image file and encode it to Base64
        File imageFile = new File(imagePath);
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

        // Create JSON payload
        Map<String, String> payload = new HashMap<>();
        payload.put("message", "Upload image " + fileName);
        payload.put("content", encodedImage);
        payload.put("token", TOKEN);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(payload);

        // Create request
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(GITHUB_API_URL + "/repos/" + REPO_OWNER + "/" + REPO_NAME + "/contents/" + fileName)
                .header("Authorization", "token " + TOKEN)
                .put(body)
                .build();

        // Execute request
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        // Parse response to get image URL
        String responseBody = response.body().string();
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        Map<String, String> contentMap = (Map<String, String>) responseMap.get("content");
        return contentMap.get("download_url");
    }
}