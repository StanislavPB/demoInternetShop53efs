package org.demointernetshop53efs.service;

import lombok.RequiredArgsConstructor;
import org.demointernetshop53efs.entity.ConfirmationCode;
import org.demointernetshop53efs.entity.User;
import org.demointernetshop53efs.repository.ConfirmationCodeRepository;
import org.demointernetshop53efs.service.exception.NotFoundException;
import org.demointernetshop53efs.service.mail.MailUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConfirmationCodeService {
    @Value("${confirmation.expiration.period}")
    private int EXPIRATION_PERIOD;

    private final ConfirmationCodeRepository repository;
    private final MailUtil mailUtil;

    @Value("${confirmation.storage.path}")
    private String LINK_PATH;


    private String generateConfirmationCode() {
        String code = UUID.randomUUID().toString();
        return code;
    }

    public void saveConfirmationCode(User newUser) {

        ConfirmationCode confirmationCode = createConfirmationCode(newUser);

        repository.save(confirmationCode);

        sendCodeByEmail(newUser, confirmationCode.getCode()); //- отправка кода по почте

    }

    private void sendCodeByEmail(User user, String code) {
        String link = LINK_PATH + code;
        String subject = "Code confirmation email";

        System.out.println("Что у нас в USER " + user);

        mailUtil.send(
                user.getFirstName(),
                user.getLastName(),
                link,
                subject,
                user.getEmail()
        );
    }

    private ConfirmationCode createConfirmationCode(User newUser) {
        String code = generateConfirmationCode();

        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code(code)
                .user(newUser)
                .expiredDataTime(LocalDateTime.now().plusDays(EXPIRATION_PERIOD))
                .build();

        return confirmationCode;
    }

    public User findCodeInDatabase(String code) {
        ConfirmationCode confirmationCode =
                repository.findByCodeAndExpiredDataTimeAfter(code, LocalDateTime.now())
                        .orElseThrow(() -> new NotFoundException("Код подтверждения не найден или его срок действия истек"));

        return confirmationCode.getUser();

    }

    public List<ConfirmationCode> findCodesByUser(User user){
        return repository.findByUser(user);
    }

    public void confirmStatus(String codeString){
        ConfirmationCode code = repository.findByCode(codeString)
                .orElseThrow(()-> {throw new NotFoundException("Code : " + codeString + " not found");});

        code.setConfirmed(true);
        repository.save(code);
    }
}
