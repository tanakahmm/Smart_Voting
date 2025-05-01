package com.smartvote.service;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
public class FaceRecognitionService {
    private final CascadeClassifier faceDetector;
    private static final double FACE_MATCH_THRESHOLD = 0.6;

    public FaceRecognitionService() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        this.faceDetector = new CascadeClassifier();
        this.faceDetector.load("haarcascade_frontalface_default.xml");
    }

    public Mono<String> verifyFace(String base64Image, String storedFaceData) {
        return Mono.fromCallable(() -> {
            try {
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                Mat image = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_COLOR);
                
                if (image.empty()) {
                    throw new RuntimeException("Invalid image data");
                }

                MatOfRect faceDetections = new MatOfRect();
                faceDetector.detectMultiScale(image, faceDetections);

                if (faceDetections.toArray().length == 0) {
                    throw new RuntimeException("No face detected in the image");
                }

                if (faceDetections.toArray().length > 1) {
                    throw new RuntimeException("Multiple faces detected in the image");
                }

                // Compare with stored face data
                double similarity = compareFaces(image, storedFaceData);
                
                if (similarity >= FACE_MATCH_THRESHOLD) {
                    return UUID.randomUUID().toString();
                } else {
                    throw new RuntimeException("Face verification failed");
                }
            } catch (Exception e) {
                log.error("Face verification error: ", e);
                throw new RuntimeException("Face verification failed: " + e.getMessage());
            }
        });
    }

    private double compareFaces(Mat image, String storedFaceData) {
        // Implement face comparison logic here
        // This is a simplified version - in production, you would use a more sophisticated
        // face recognition algorithm like FaceNet or DeepFace
        return 0.8; // Placeholder implementation
    }

    public String extractFaceFeatures(String base64Image) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            Mat image = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_COLOR);
            
            if (image.empty()) {
                throw new RuntimeException("Invalid image data");
            }

            MatOfRect faceDetections = new MatOfRect();
            faceDetector.detectMultiScale(image, faceDetections);

            if (faceDetections.toArray().length == 0) {
                throw new RuntimeException("No face detected in the image");
            }

            if (faceDetections.toArray().length > 1) {
                throw new RuntimeException("Multiple faces detected in the image");
            }

            // Extract face features and convert to base64
            Rect faceRect = faceDetections.toArray()[0];
            Mat face = new Mat(image, faceRect);
            MatOfByte faceBytes = new MatOfByte();
            Imgcodecs.imencode(".jpg", face, faceBytes);
            
            return Base64.getEncoder().encodeToString(faceBytes.toArray());
        } catch (Exception e) {
            log.error("Face feature extraction error: ", e);
            throw new RuntimeException("Face feature extraction failed: " + e.getMessage());
        }
    }
} 