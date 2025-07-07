package com.example.utils.controller;


import com.example.utils.controller.dto.response.FileServiceResponse;
import com.example.utils.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import static com.example.utils.UtilsMessageProvider.*;

@RestController
@RequestMapping("api/v1/utils/fileService")
@RequiredArgsConstructor
@Log4j
public class FileServiceController {

    private final FileService fileService;

    @PostMapping("/changeFileExtension")
    public ResponseEntity<FileServiceResponse> changeFileExtension(@RequestParam("fileName") String fileName,
                                              @RequestParam("newExtension") String newExtension) {

        String result = fileService.changeExtension(fileName, newExtension);

        FileServiceResponse response = new FileServiceResponse().success(result, FILE_NAME_CHANGE_MSG, HttpStatus.OK);

        return ResponseEntity.status(response.getHttpStatus()).body(response);

    }

}
