package org.example.videoserver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Controller
public class VideoController {


    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam String videoId, @RequestPart("file") MultipartFile file) {
        try {
            // 예제 비디오 파일 저장 경로. 실제로는 videoId를 사용하여 동적 경로를 설정할 수 있습니다.
            Path videoPath = Paths.get("videos", videoId + ".mp4");
            Files.createDirectories(videoPath.getParent());
            Files.write(videoPath, file.getBytes());

            // 비디오를 DASH 형식으로 변환
            String outputDir = "videos/" + videoId;
            Files.createDirectories(Paths.get(outputDir));
            String command = String.format("ffmpeg -i %s -map 0 -seg_duration 4 -f dash %s/output.mpd",
                    videoPath.toString(), outputDir);

            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            process.waitFor();

            return "Upload and conversion successful";
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "Upload failed";
        }
    }
}