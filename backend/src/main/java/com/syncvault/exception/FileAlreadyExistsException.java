package com.syncvault.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class FileAlreadyExistsException extends RuntimeException {

	public FileAlreadyExistsException(String path) {
		super("File already exists at path: " + path);
	}
}
