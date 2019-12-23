package org.springframework.core.io.loader;

import org.junit.Test;
import org.springframework.core.io.*;

import java.io.IOException;

public class ClassRelativeResourceLoaderTest {

	@Test
	public void classpathFirst() throws Exception {
		// 获取获取文件系统中的某个资源
		ResourceLoader resourceLoader = new ClassRelativeResourceLoader(this.getClass());
		Resource resource = resourceLoader.getResource("DefaultResourceLoaderTests.class");
		describe(resource);
	}

	private void describe(Resource resource) throws IOException {
		System.out.println(resource.toString());
		System.out.println(resource.contentLength());
		System.out.println(resource.exists());
		System.out.println(resource.getDescription());
		System.out.println(resource.isReadable());
		System.out.println(resource.isOpen());
		System.out.println(resource.getFilename());
		System.out.println(resource.isFile());
		if (resource.isFile()) {
// getFile()仅针对文件类型Resource有效,可以是文件系统文件或者classpath上的文件
			System.out.println(resource.getFile());
		}
		if (!((resource instanceof ByteArrayResource) || (resource instanceof InputStreamResource))) {
// 以下三个属性针对 ByteArrayResource/InputStreamResource 类型资源无效，调用的话会抛出异常
			System.out.println(resource.lastModified());
			System.out.println(resource.getURI());
			System.out.println(resource.getURL());
		}
	}
}
