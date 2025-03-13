package org.demointernetshop53efs.service;

import lombok.RequiredArgsConstructor;
import org.demointernetshop53efs.dto.MessageResponseDto;
import org.demointernetshop53efs.dto.UserRequestDto;
import org.demointernetshop53efs.dto.UserResponseDto;
import org.demointernetshop53efs.entity.ConfirmationCode;
import org.demointernetshop53efs.entity.User;
import org.demointernetshop53efs.repository.UserRepository;
import org.demointernetshop53efs.service.exception.AlreadyExistException;
import org.demointernetshop53efs.service.exception.NotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ConfirmationCodeService confirmationCodeService;
    private final Converter converter;


    @Transactional
    public UserResponseDto registration(UserRequestDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistException("Пользователь с email: " + request.getEmail() + " уже зарегистрирован");
        }

//        User user = User.builder()
//                .email(request.getEmail())
//                .firstName(request.getFirstName())
//                .secondName(request.getSecondName())
//                .hashPassword(request.getHashPassword())
//                .role(User.Role.USER)
//                .state(User.State.NOT_CONFIRMED)
//                .build();

        User user = converter.fromDto(request);
        user.setRole(User.Role.USER);
        user.setState(User.State.NOT_CONFIRMED);

        userRepository.save(user);

        confirmationCodeService.saveConfirmationCode(user);

        return converter.fromUser(user);
    }

    public List<UserResponseDto> findAll() {
        return converter.fromUser(userRepository.findAll());
    }

    public UserResponseDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));
        return converter.fromUser(user);
    }

    public List<User> findAllFullDetails() {
        return userRepository.findAll();
    }

    @Transactional
    public UserResponseDto confirmation(String confirmationCode) {

        User user = confirmationCodeService.findCodeInDatabase(confirmationCode);

        user.setState(User.State.CONFIRMED);

        userRepository.save(user);

        confirmationCodeService.confirmStatus(confirmationCode);

        return converter.fromUser(user);
    }

    public User findByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new NotFoundException("User with email " + email + " not found");
        }
    }

    public MessageResponseDto setPhotoLink(String fileLink) {
        User currentUser = getCurrentUser();
        currentUser.setPhotoLink(fileLink);
        userRepository.save(currentUser);
        return new MessageResponseDto("Ссылка на файл успешно обновлена");
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String email;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        return findByEmail(email);
    }

    public List<ConfirmationCode> findCodesByEmail(String email){
        User user = findByEmail(email);
        return confirmationCodeService.findCodesByUser(user);
    }

}
