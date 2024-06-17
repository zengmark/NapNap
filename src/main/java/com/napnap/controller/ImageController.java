package com.napnap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.napnap.annotation.LoginCheck;
import com.napnap.common.BaseResponse;
import com.napnap.common.ErrorCode;
import com.napnap.common.ResultUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/image")
@Api(tags = "图片管理")
public class ImageController {

    @ApiOperation("测试")
    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @Value("${gitee.api.url}")
    private String giteeApiUrl;

    @Value("${gitee.repo.owner}")
    private String repoOwner;

    @Value("${gitee.repo.name}")
    private String repoName;

    @Value("${gitee.token}")
    private String token;

    @PostMapping("/upload")
    public BaseResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = uploadImageToGitee(file);
            return ResultUtils.success(imageUrl);
        } catch (IOException e) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "上传图片失败");
        }
    }

    private String uploadImageToGitee(MultipartFile file) throws IOException {
        // Read image file and encode it to Base64
        byte[] imageBytes = file.getBytes();
        String encodedImage = Base64.getEncoder().encodeToString(imageBytes);

        // Create JSON payload
        Map<String, String> payload = new HashMap<>();
        payload.put("message", "Upload image " + file.getOriginalFilename());
        payload.put("content", encodedImage);
        payload.put("access_token", token);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPayload = objectMapper.writeValueAsString(payload);

        // Create request
        OkHttpClient client = new OkHttpClient();
        okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonPayload, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(giteeApiUrl + "/repos/" + repoOwner + "/" + repoName + "/contents/" + file.getOriginalFilename())
                .post(body)
                .build();

        // Execute request
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        // Parse response to get image URL
        String responseBody = response.body().string();
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        Map<String, Object> contentMap = (Map<String, Object>) responseMap.get("content");
        return (String) contentMap.get("download_url");
    }

//    @Value("${gitee.api.url}")
//    private String githubApiUrl;
//
//    @Value("${gitee.repo.owner}")
//    private String repoOwner;
//
//    @Value("${gitee.repo.name}")
//    private String repoName;
//
////    @Value("${github.branch}")
////    private String branch;
//
//    @Value("${gitee.token}")
//    private String token;
//
//    @ApiOperation("上传图片")
//    @LoginCheck
//    @PostMapping("/upload")
//    public BaseResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
//        try {
//            String imageUrl = uploadImageToGitHub(file);
//            return ResultUtils.success(imageUrl);
//        } catch (IOException e) {
//            return ResultUtils.error(ErrorCode.OPERATION_ERROR, "上传图片失败");
//        }
//    }
//
//    private String uploadImageToGitHub(MultipartFile file) throws IOException {
//        // Read image file and encode it to Base64
//        byte[] imageBytes = file.getBytes();
//        String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
//
//        // Create JSON payload
//        Map<String, String> payload = new HashMap<>();
//        payload.put("message", "Upload image " + file.getOriginalFilename());
//        payload.put("content", encodedImage);
//        payload.put("access_token", token);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonPayload = objectMapper.writeValueAsString(payload);
//
//        // Create request
//        OkHttpClient client = new OkHttpClient();
//        okhttp3.RequestBody body = okhttp3.RequestBody.create(jsonPayload, MediaType.get("application/json"));
//        Request request = new Request.Builder()
//                .url(githubApiUrl + "/repos/" + repoOwner + "/" + repoName + "/contents/" + file.getOriginalFilename())
//                .header("Authorization", "token " + token)
//                .put(body)
//                .build();
//
//        // Execute request
//        Response response = client.newCall(request).execute();
//        if (!response.isSuccessful()) {
//            throw new IOException("Unexpected code " + response);
//        }
//
//        // Parse response to get image URL
//        String responseBody = response.body().string();
//        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
//        Map<String, String> contentMap = (Map<String, String>) responseMap.get("content");
//        return contentMap.get("download_url");
//    }

}
