package com.example.expense.expense_tracker_api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class FileProcessingController {

    @PostMapping("/api/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()){
            return "File is empty";
        }
        try {
            String homeDirectory = System.getProperty("user.home");
            String uploadDirectory = homeDirectory + File.separator + "uploads";

            Files.createDirectories(Paths.get(uploadDirectory));

            // Save the uploaded file temporarily to server
            String filePath = uploadDirectory + "/" + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            // Execute Python script
            ProcessBuilder pb = new ProcessBuilder("python", "process_data.py", filePath);
            pb.directory(new File(System.getProperty("user.dir") + File.separator + ".."));

            Process process = pb.start();

            // Read output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return output.toString();
            } else {
                return "Python script failed with exit code: " + exitCode;
            }

        } catch (Exception e) {
            return "Failed to upload file: " + e.getMessage();
        }
    }
}

