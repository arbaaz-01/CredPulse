package com.ofss.project.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ofss.project.exception.DocumentStorageException;

@Service
public class FileStorageService {

    private final Path rootLocation;
    private final long maxFileSize;

    public FileStorageService(
            @Value("${app.file-storage.root}")
            String rootLocation,

            @Value("${app.file-storage.max-file-size}")
            long maxFileSize) throws IOException {

        this.rootLocation =
                Paths.get(rootLocation)
                        .toAbsolutePath()
                        .normalize();

        this.maxFileSize = maxFileSize;

        Files.createDirectories(this.rootLocation);
    }

    public StoredFile store(
            MultipartFile file,
            Long applicationId,
            String documentType) {

        validatePdf(file);

        String extension = ".pdf";

        String generatedFileName = UUID.randomUUID()+ extension;

        Path applicationDirectory =
                rootLocation
                        .resolve("credit-card-applications")
                        .resolve(String.valueOf(applicationId))
                        .normalize();

        ensureInsideRoot(applicationDirectory);

        try {
            Files.createDirectories(applicationDirectory);

            Path targetPath =
                    applicationDirectory
                            .resolve(
                                    documentType
                                            + "_"
                                            + generatedFileName
                            )
                            .normalize();

            ensureInsideRoot(targetPath);

            try (InputStream inputStream =
                         file.getInputStream()) {

                Files.copy(
                        inputStream,
                        targetPath,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            return new StoredFile(
                    generatedFileName,
                    targetPath.toString(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize()
            );

        } catch (IOException ex) {

            throw new DocumentStorageException(
                    "Failed to store uploaded document",
                    ex
            );
        }
    }

    public void delete(String storagePath) {

        if (storagePath == null ||
                storagePath.isBlank()) {
            return;
        }

        Path path =
                Paths.get(storagePath)
                        .toAbsolutePath()
                        .normalize();

        ensureInsideRoot(path);

        try {
            Files.deleteIfExists(path);
        } catch (IOException ex) {
            throw new DocumentStorageException(
                    "Failed to delete stored document",
                    ex
            );
        }
    }

    private void validatePdf(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Document file is required"
            );
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                    "Document exceeds maximum allowed size"
            );
        }

        String originalFilename =
                file.getOriginalFilename();

        String extension =
                originalFilename == null
                        ? ""
                        : StringUtils
                                .getFilenameExtension(
                                        originalFilename
                                );

        if (extension == null ||
                !extension.equalsIgnoreCase("pdf")) {

            throw new IllegalArgumentException(
                    "Only PDF documents are allowed"
            );
        }

        if (!hasPdfSignature(file)) {
            throw new IllegalArgumentException(
                    "Uploaded file is not a valid PDF"
            );
        }
    }

    private boolean hasPdfSignature(
            MultipartFile file) {

        try (InputStream inputStream =
                     file.getInputStream()) {

            byte[] header = new byte[5];

            int bytesRead =
                    inputStream.read(header);

            if (bytesRead != 5) {
                return false;
            }

            return header[0] == '%'
                    && header[1] == 'P'
                    && header[2] == 'D'
                    && header[3] == 'F'
                    && header[4] == '-';

        } catch (IOException ex) {
            throw new IllegalArgumentException(
                    "Unable to inspect uploaded PDF",
                    ex
            );
        }
    }

    private void ensureInsideRoot(Path path) {

        if (!path.startsWith(rootLocation)) {
            throw new IllegalStateException(
                    "Invalid file storage path"
            );
        }
    }

    public record StoredFile(
            String storedFileName,
            String storagePath,
            String originalFileName,
            String contentType,
            long fileSize
    ) {
    }
    
    public Resource loadAsResource(String storagePath) {

        Path filePath = resolveStoredFile(storagePath);

        return new FileSystemResource(filePath);
    }
    
    private Path resolveStoredFile(
            String storagePath) {

        Path path =
                Paths.get(storagePath)
                        .toAbsolutePath()
                        .normalize();

        ensureInsideRoot(path);

        try {
            Path realRoot =
                    rootLocation.toRealPath();

            Path realPath =
                    path.toRealPath();

            if (!realPath.startsWith(realRoot)) {
                throw new DocumentStorageException(
                        "Invalid document storage location"
                );
            }

            return realPath;

        } catch (java.io.IOException ex) {

            throw new DocumentStorageException(
                    "Stored document could not be resolved",
                    ex
            );
        }
    }
}