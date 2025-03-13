package org.demointernetshop53efs.controller;

import lombok.RequiredArgsConstructor;
import org.demointernetshop53efs.dto.MessageResponseDto;
import org.demointernetshop53efs.service.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class FilesController {

    private final FileService service;

    @PostMapping("/files")
    public MessageResponseDto upload(@RequestParam("uploadFile")MultipartFile file){
        return service.uploadDigitalOceanStorage(file);
    }
}
