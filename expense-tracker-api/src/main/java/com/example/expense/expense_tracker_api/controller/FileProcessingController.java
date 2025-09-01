package com.example.expense.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;

@RestController
public class FileProcessingController {

    @PostMapping("/api/upload")
    public String handleFileUpload(@RequestParm("file") MultipartFile file) {
        if (file.isEmpty()){
            return "File is empty";
        }
        try {
            // Save the uploaded file temporarily to server
            String filePath = "uploads/" + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            // Execute Python script
            ProcessBuilder pb = new ProcessBuilder("python", "process_data.py", filePath);
            Process process = pb.start();

            // Read output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder reader = new StringBuilder();
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

            return "File uploaded and processed successfully.";
        } catch (Exception e) {
            return "Failed to upload file: " + e.getMessage();
        }
    }
}

