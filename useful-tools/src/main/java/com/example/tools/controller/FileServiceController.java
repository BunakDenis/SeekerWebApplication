package com.example.tools.controller;


import com.example.tools.controller.dto.response.FileServiceResponse;
import com.example.utils.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.example.tools.ToolsMessageProvider.*;


@RestController
@RequestMapping("api/v1/tools/fileService")
@RequiredArgsConstructor
@Log4j2
public class FileServiceController {

    private final FileService fileService;

    //private final VoskAudioDecoder decoder;

    @GetMapping("/")
    public ResponseEntity<String> greeding() {
        return ResponseEntity.ok().body("Hello! I am Utils Service");
    }

    @PostMapping("/changeFileExtension")
    public ResponseEntity<FileServiceResponse> changeFileExtension
            (@RequestParam("fileName") String fileName,
             @RequestParam("newExtension") String newExtension) {

        log.debug("Входящий запрос с параметрами - fileName=" + fileName + ", newExtension=" + newExtension);

        String result = fileService.changeExtension(fileName, newExtension);

        log.debug("Новое расширение - " + result);

        FileServiceResponse response = new FileServiceResponse().success(result, FILE_NAME_CHANGE_MSG, HttpStatus.OK);

        log.debug("Исходящий запрос - " + response);

        return ResponseEntity.status(response.getHttpStatus()).body(response);

    }
/*
    @PostMapping("/decode")
    public ResponseEntity<FileServiceResponse> decodeMediaFile() {
        String audioPath = "./app/resources/temp/AUD-20230511-WA0001.mp3";

        String decodedText = decoder.decode(audioPath);

        FileServiceResponse response = new FileServiceResponse();

        if (!decodedText.isEmpty()) {
            response = new FileServiceResponse().success(decodedText, MULTIMEDIA_FILE_DECODE_MSG, HttpStatus.OK);
        } else {
            response = new FileServiceResponse().failed("Decoding failed", HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.status(response.getHttpStatus()).body(response);

    }

 */

}
