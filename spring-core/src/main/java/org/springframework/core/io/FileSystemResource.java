/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.core.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link Resource} implementation for {@code java.io.File} and
 * {@code java.nio.file.Path} handles with a file system target.
 * Supports resolution as a {@code File} and also as a {@code URL}.
 * Implements the extended {@link WritableResource} interface.
 *
 * <p>Note: As of Spring Framework 5.0, this {@link Resource} implementation uses
 * NIO.2 API for read/write interactions. As of 5.1, it may be constructed with a
 * {@link java.nio.file.Path} handle in which case it will perform all file system
 * interactions via NIO.2, only resorting to {@link File} on {@link #getFile()}.
 *
 * @author Juergen Hoeller
 * @since 28.12.2003
 * @see #FileSystemResource(File)
 * @see #FileSystemResource(Path)
 * @see java.io.File
 * @see java.nio.file.Files
 *
 * 继承自AbstractResource，并实现了写的接口WritableResource
 * 所有Resource实现类中，唯一一个实现了WritableResource接口的类。就是说，其他的类都不可写入操作，都只能读取
 *
 */
public class FileSystemResource extends AbstractResource implements WritableResource {

	// 2个不可变的属性 file 和 path ，本质上就是一个java.io.File 的包装。
	private final String path;

	@Nullable
	private final File file;

	private final Path filePath;


	/**
	 * 简单的构造方法
	 */
	public FileSystemResource(String path) {
		Assert.notNull(path, "Path must not be null");
		// path为file路径格式化后的样子
		this.path = StringUtils.cleanPath(path);
		this.file = new File(path);
		this.filePath = this.file.toPath();
	}

	/**
	 * 简单构造方法  path为 file的 path 格式化后的样子
	 */
	public FileSystemResource(File file) {
		Assert.notNull(file, "File must not be null");
		this.path = StringUtils.cleanPath(file.getPath());
		this.file = file;
		this.filePath = file.toPath();
	}

	/**
	 * 不初始化 file
	 * 初始化 path 为格式化后的 path
	 * 初始化 filePath 为 原path
	 */
	public FileSystemResource(Path filePath) {
		Assert.notNull(filePath, "Path must not be null");
		this.path = StringUtils.cleanPath(filePath.toString());
		this.file = null;
		this.filePath = filePath;
	}

	/**
	 * Create a new {@code FileSystemResource} from a {@link FileSystem} handle,
	 * locating the specified path.
	 * <p>This is an alternative to {@link #FileSystemResource(String)},
	 * performing all file system interactions via NIO.2 instead of {@link File}.
	 * @param fileSystem the FileSystem to locate the path within
	 * @param path a file path
	 * @since 5.1.1
	 * @see #FileSystemResource(File)
	 */
	public FileSystemResource(FileSystem fileSystem, String path) {
		Assert.notNull(fileSystem, "FileSystem must not be null");
		Assert.notNull(path, "Path must not be null");
		this.path = StringUtils.cleanPath(path);
		this.file = null;
		this.filePath = fileSystem.getPath(this.path).normalize();
	}


	/**
	 * 返回 文件路径
	 */
	public final String getPath() {
		return this.path;
	}

	/**
	 * 重写父类方法  判断文件是否存在
	 */
	@Override
	public boolean exists() {
		// 文件不为空 调用 File的 exist 方法
		// 文件为空 调用 Files.exists 方法
		return (this.file != null ? this.file.exists() : Files.exists(this.filePath));
	}

	/**
	 * 检查文件是否可读 且不是 Directory
	 */
	@Override
	public boolean isReadable() {
		return (this.file != null ? this.file.canRead() && !this.file.isDirectory() :
				Files.isReadable(this.filePath) && !Files.isDirectory(this.filePath));
	}

	/**
	 * 获取输入流
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		try {
			return Files.newInputStream(this.filePath);
		}
		catch (NoSuchFileException ex) {
			throw new FileNotFoundException(ex.getMessage());
		}
	}

	/**
	 * 判断文件是否可写 且 不是 Directory
	 */
	@Override
	public boolean isWritable() {
		return (this.file != null ? this.file.canWrite() && !this.file.isDirectory() :
				Files.isWritable(this.filePath) && !Files.isDirectory(this.filePath));
	}

	/**
	 * 获取输出流
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		return Files.newOutputStream(this.filePath);
	}

	/**
	 * 通过 File 的 URI  获取 URL
	 */
	@Override
	public URL getURL() throws IOException {
		return (this.file != null ? this.file.toURI().toURL() : this.filePath.toUri().toURL());
	}

	/**
	 * 获取文件的URI
	 */
	@Override
	public URI getURI() throws IOException {
		return (this.file != null ? this.file.toURI() : this.filePath.toUri());
	}

	/**
	 * 是否为文件 永久为true
	 */
	@Override
	public boolean isFile() {
		return true;
	}

	/**
	 * 获取文件的引用
	 */
	@Override
	public File getFile() {
		return (this.file != null ? this.file : this.filePath.toFile());
	}

	/**
	 * 获取文件的 读字节channel
	 */
	@Override
	public ReadableByteChannel readableChannel() throws IOException {
		try {
			return FileChannel.open(this.filePath, StandardOpenOption.READ);
		}
		catch (NoSuchFileException ex) {
			throw new FileNotFoundException(ex.getMessage());
		}
	}

	/**
	 * 获取文件的 写字节channel
	 */
	@Override
	public WritableByteChannel writableChannel() throws IOException {
		return FileChannel.open(this.filePath, StandardOpenOption.WRITE);
	}

	/**
	 * 获取文件的 长度
	 */
	@Override
	public long contentLength() throws IOException {
		if (this.file != null) {
			long length = this.file.length();
			if (length == 0L && !this.file.exists()) {
				throw new FileNotFoundException(getDescription() +
						" cannot be resolved in the file system for checking its content length");
			}
			return length;
		}
		else {
			try {
				return Files.size(this.filePath);
			}
			catch (NoSuchFileException ex) {
				throw new FileNotFoundException(ex.getMessage());
			}
		}
	}

	/**
	 * This implementation returns the underlying File/Path last-modified time.
	 */
	@Override
	public long lastModified() throws IOException {
		if (this.file != null) {
			return super.lastModified();
		}
		else {
			try {
				return Files.getLastModifiedTime(this.filePath).toMillis();
			}
			catch (NoSuchFileException ex) {
				throw new FileNotFoundException(ex.getMessage());
			}
		}
	}

	/**
	 * This implementation creates a FileSystemResource, applying the given path
	 * relative to the path of the underlying file of this resource descriptor.
	 * @see org.springframework.util.StringUtils#applyRelativePath(String, String)
	 */
	@Override
	public Resource createRelative(String relativePath) {
		String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
		return (this.file != null ? new FileSystemResource(pathToUse) :
				new FileSystemResource(this.filePath.getFileSystem(), pathToUse));
	}

	/**
	 * This implementation returns the name of the file.
	 * @see java.io.File#getName()
	 */
	@Override
	public String getFilename() {
		return (this.file != null ? this.file.getName() : this.filePath.getFileName().toString());
	}

	/**
	 * This implementation returns a description that includes the absolute
	 * path of the file.
	 * @see java.io.File#getAbsolutePath()
	 */
	@Override
	public String getDescription() {
		return "file [" + (this.file != null ? this.file.getAbsolutePath() : this.filePath.toAbsolutePath()) + "]";
	}


	/**
	 * 通过path 来判定 文件与目标对象是否 equals
	 */
	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof FileSystemResource &&
				this.path.equals(((FileSystemResource) other).path)));
	}

	/**
	 *  path的 哈希code 作为文件的哈希code
	 */
	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

}
