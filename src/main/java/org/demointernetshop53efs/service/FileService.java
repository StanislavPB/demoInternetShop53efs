package org.demointernetshop53efs.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.demointernetshop53efs.config.S3ConfigurationProperties;
import org.demointernetshop53efs.dto.MessageResponseDto;
import org.demointernetshop53efs.entity.FileInfo;
import org.demointernetshop53efs.entity.User;
import org.demointernetshop53efs.repository.FileInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileInfoRepository repository;
    private final UserService userService;
    private final AmazonS3 amazonS3;

    private final String LOCAL_STORAGE_PATH = "src/main/resources/static/upload";

    @SneakyThrows
    @Transactional
    public MessageResponseDto uploadLocalStorage(MultipartFile uploadFile) {

        Path fileStorageLocation = Paths.get(LOCAL_STORAGE_PATH);

        String newFileName = createFileName(uploadFile);

        // создаем targetLocation который будет содержать полный путь до места хранения и имя файла
        Path targetLocation = fileStorageLocation.resolve(newFileName);

        // копируем данные из файла upload, который хранится во временном хранилище сервера
        // в папку и под именем, которое мы создали

        Files.copy(uploadFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        String link = targetLocation.toString();


        User currentUser = userService.getCurrentUser();

        FileInfo fileInfo = new FileInfo();
        fileInfo.setLink(link);
        fileInfo.setUser(currentUser);
        repository.save(fileInfo);

        userService.setPhotoLink(link);

        return new MessageResponseDto("Файл " + link + " успешно сохранен");

    }

    @Transactional
    @SneakyThrows
    public MessageResponseDto uploadDigitalOceanStorage(MultipartFile uploadFile){

        String newFileName = createFileName(uploadFile);
        // формат названия файла будет в формате UUID + "." + расширение исходного файла

        // загрузка в хранилище digital Ocean

        InputStream inputStream = uploadFile.getInputStream();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(uploadFile.getContentType());

        // создаем запрос на отправку файла

        /*
        варианты что может вернуть getContentType():

        Изображения:
        - image/jpeg
        - image/png
        - image/gif
        ...

        Не изображения:
        text/plain - txt
        text/html - html
        ...

        Документы:
        application/pdf
        application/msword
        application/vnd.ms-excel
        application/vnd.ms-powerpoint

        Архивы:
        application/zip
        ...

        Аудио
        audio/mp3
        audio/wav
        ...

        Видео
        video/mp4
        video/x-matroska (MKV)

         */

        String fileLink = "";

        if (uploadFile.getContentType().startsWith("image")){
            fileLink = "image/" + newFileName;
        } else if (uploadFile.getContentType().startsWith("text")) {
            fileLink = "data/" + newFileName;
        }

        String bucketName = "demo-shop-files";

        PutObjectRequest request = new PutObjectRequest(
                bucketName,
                fileLink,
                inputStream,
                metadata
        ).withCannedAcl(CannedAccessControlList.PublicRead);

        amazonS3.putObject(request);

        String digitalOceanLink = amazonS3.getUrl(bucketName, fileLink).toString();

        User currentUser = userService.getCurrentUser();

        FileInfo fileInfo = new FileInfo();
        fileInfo.setLink(digitalOceanLink);
        fileInfo.setUser(currentUser);
        repository.save(fileInfo);

        userService.setPhotoLink(digitalOceanLink);

        return new MessageResponseDto("Файл " + digitalOceanLink + " успешно сохранен");

    }

    private String createFileName(MultipartFile uploadFile){
        String originalFileName = uploadFile.getOriginalFilename();
        // получаем исходное имя файла

        String extension = "";
        if (originalFileName != null) {
            int indexExtension = originalFileName.lastIndexOf(".") + 1;
            //получаем индекс начала расширения полученного файла (следующий символ за '.')
            extension = originalFileName.substring(indexExtension);
        } else {
            throw new NullPointerException("Null original file name");
        }

        // генерируем случайное имя для файла с помощью UUID
        String uuidFileName = UUID.randomUUID().toString();
        String newFileName = uuidFileName + "." + extension;

        return newFileName;
    }


}
