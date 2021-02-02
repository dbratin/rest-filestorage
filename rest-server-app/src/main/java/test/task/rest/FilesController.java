package test.task.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import test.task.FileContentsWrapper;
import test.task.PageableListWrapper;
import test.task.FileRecord;
import test.task.exceptions.StorageFileNotFoundException;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
@RequestMapping("/")
public class FilesController {

    private final StorageService storageService;

    public FilesController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(method = RequestMethod.GET, path="/files",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody PageableListWrapper<FileRecord> filesList(@RequestParam(name = "p", defaultValue = "0") int page) {
        return storageService.listFiles(page);
    }

    @RequestMapping(method = RequestMethod.PUT, path="/file",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public @ResponseBody
    FileRecord storeFile(@RequestParam(name = "fileName") String fileName, @RequestParam(name = "contents") MultipartFile contents) throws IOException {
        return storageService.storeFile(fileName, contents.getInputStream());
    }

    @RequestMapping(method = RequestMethod.PUT, path="/file/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public @ResponseBody FileRecord modifyFile(
            @PathVariable(name = "id") Long fileId,
            @RequestParam(name = "version") Integer version,
            @RequestParam(name = "contents") MultipartFile contents) throws IOException {
        return storageService.modifyFile(fileId, version, contents.getInputStream());
    }

    @RequestMapping(method = RequestMethod.GET, path="/file/{id}")
    public ResponseEntity<InputStreamReader> downloadFile(@PathVariable(name = "id") Long fileId) {
        FileContentsWrapper contents = storageService.readFile(fileId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"" + contents.getFileName() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(contents.getLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamReader(contents.getInputStream()));
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/file/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable(name = "id") Long fileId, @RequestParam(name = "version") Integer version) {
        storageService.deleteFile(fileId, version);

        return ResponseEntity.ok("File " + fileId + " successfully deleted");
    }



    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
