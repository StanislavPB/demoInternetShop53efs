package org.demointernetshop53efs.controller;

import lombok.RequiredArgsConstructor;
import org.demointernetshop53efs.controller.api.PublicApi;
import org.demointernetshop53efs.dto.UserRequestDto;
import org.demointernetshop53efs.dto.UserResponseDto;
import org.demointernetshop53efs.service.ConfirmationCodeService;
import org.demointernetshop53efs.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublicController implements PublicApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponseDto> userRegistration(UserRequestDto request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registration(request));
    }

    @Override
    public ResponseEntity<UserResponseDto> confirmRegistration(String code) {
        return ResponseEntity.ok(userService.confirmation(code));
    }
}
