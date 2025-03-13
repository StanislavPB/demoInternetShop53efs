package org.demointernetshop53efs.controller;

import lombok.RequiredArgsConstructor;
import org.demointernetshop53efs.controller.api.AdminApi;
import org.demointernetshop53efs.dto.UserResponseDto;
import org.demointernetshop53efs.entity.ConfirmationCode;
import org.demointernetshop53efs.entity.User;
import org.demointernetshop53efs.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminApi {
    private final UserService service;


    @Override
    public ResponseEntity<List<UserResponseDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Override
    public ResponseEntity<List<User>> findAllFull() {
        return ResponseEntity.ok(service.findAllFullDetails());
    }

    @Override
    public ResponseEntity<List<ConfirmationCode>> findAllCodes(String email) {
        return ResponseEntity.ok(service.findCodesByEmail(email));
    }
}
