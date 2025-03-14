package org.demointernetshop53efs.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.demointernetshop53efs.dto.ErrorResponseDto;
import org.demointernetshop53efs.dto.UserRequestDto;
import org.demointernetshop53efs.dto.UserResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/public")
public interface PublicApi {


    @Operation(summary = "Регистрация пользователя", description = "Операция доступна всем, роль по умолчанию - USER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> userRegistration(@Valid @RequestBody UserRequestDto request);


    @GetMapping("/confirm")
    public ResponseEntity<UserResponseDto> confirmRegistration(@RequestParam String code);

}
