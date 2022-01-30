package dev.dotspace.url.response.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Storage system Malfunction")
public class StorageException extends RuntimeException {

}
