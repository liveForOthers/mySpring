package org.springframework.tests.resource;

import org.junit.Test;
import org.springframework.core.io.FileSystemResource;

import java.io.File;

public class TestFileSystemResource {

	@Test
	public void fileSystemResourceTest() {
		String path = "/Users/wangzuojun/userIds.txt";
		FileSystemResource fileSystemResource1 = new FileSystemResource(path);
		System.out.println(fileSystemResource1.getFilename());

		File f = new File("text.txt");
		FileSystemResource fileSystemResource = new FileSystemResource(f);
		System.out.println(fileSystemResource.getFilename());
	}
}
