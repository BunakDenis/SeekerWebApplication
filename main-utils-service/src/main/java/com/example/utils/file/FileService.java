package com.example.utils.file;

import com.example.utils.file.changer.FileExtensionChanger;
import com.example.utils.file.changer.FileNameChanger;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

@Component
@Data
@RequiredArgsConstructor
@Log4j
public class FileService {

    public String changeExtension(String filename, String newExtension) {
        return FileExtensionChanger.change(filename, newExtension);
    }

    public String addSuffixToFileName(String fileName, String suffix) {
        return FileNameChanger.addSuffix(fileName, suffix);
    }

}
