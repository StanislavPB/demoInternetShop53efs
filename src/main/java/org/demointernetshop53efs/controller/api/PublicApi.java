package org.demointernetshop53efs.controller.api;

import jakarta.validation.Valid;
import org.demointernetshop53efs.dto.UserRequestDto;
import org.demointernetshop53efs.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/public")
public interface PublicApi {

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> userRegistration(@Valid @RequestBody UserRequestDto request);


    @GetMapping("/confirm")
    public ResponseEntity<UserResponseDto> confirmRegistration(@RequestParam String code);

}
