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
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.springframework.lang.Nullable;

/**
 * Interface for a resource descriptor that abstracts from the actual
 * type of underlying resource, such as a file or class path resource.
 *
 * <p>An InputStream can be opened for every resource if it exists in
 * physical form, but a URL or File handle can just be returned for
 * certain resources. The actual behavior is implementation-specific.
 *
 * 本接口是用于资源描述，从潜在资源的实际类型（如文件 或 类路径资源）抽象出来
 *
 * 一个输入流 能被每个已经存在的物理形式的资源打开，但是对于 URL 或文件 仅能 返回 确定的资源
 * 实际的行为 基于 特定的实现
 *
 * Resource接口 是 Spring 框架所有资源的抽象和访问接口，它继承 InputStreamSource
 *
 * 作为所有资源的统一抽象，Resource 定义了一些通用的方法，由子类 AbstractResource 提供统一的默认实现。
 *
 * @author Juergen Hoeller
 * @since 28.12.2003
 * @see #getInputStream()
 * @see #getURL()
 * @see #getURI()
 * @see #getFile()
 * @see WritableResource      可写资源
 * @see ContextResource       上下文资源
 * @see UrlResource           对 java.net.URL类型资源的封装。内部委派 URL 进行具体的资源操作
 * @see FileUrlResource       文件url资源
 * @see FileSystemResource    对 java.io.File 类型资源的封装，
 * 							  	只要是跟 File 打交道的，基本上与 FileSystemResource 也可以打交道。
 * 							  	支持文件和 URL 的形式，实现 WritableResource 接口，
 * 							  	且从 Spring Framework 5.0 开始，FileSystemResource 使用 NIO2 API进行读/写交互。
 * @see ClassPathResource     class path 类型资源的实现。使用给定的 ClassLoader 或者给定的 Class 来加载资源。
 * @see ByteArrayResource     对字节数组提供的数据的封装。
 * 							  	如果通过 InputStream 形式访问该类型的资源，
 * 							  	该实现会根据字节数组的数据构造一个相应的 ByteArrayInputStream。
 * @see InputStreamResource   将给定的 InputStream 作为一种资源的 Resource 的实现类。
 */
public interface Resource extends InputStreamSource {

	/**
	 * 确定资源以物理的形式 是否存在
	 */
	boolean exists();

	/**
	 * 确定资源是否可以通过 getInputStream 进行读取  默认实现直接调用 资源是否存在
	 */
	default boolean isReadable() {
		return exists();
	}

	/**
	 * 资源所代表的句柄是否被一个 stream 打开了
	 */
	default boolean isOpen() {
		return false;
	}

	/**
	 * 是否为 File
	 */
	default boolean isFile() {
		return false;
	}

	/**
	 * 返回资源的 URL 的句柄
	 */
	URL getURL() throws IOException;

	/**
	 * 返回资源的 URI 的句柄
	 */
	URI getURI() throws IOException;

	/**
	 * 返回资源的 File 的句柄
	 */
	File getFile() throws IOException;

	/**
	 * 返回 ReadableByteChannel
	 */
	default ReadableByteChannel readableChannel() throws IOException {
		return Channels.newChannel(getInputStream());
	}

	/**
	 * 资源的长度
	 */
	long contentLength() throws IOException;

	/**
	 * 资源最后修改时间
	 */
	long lastModified() throws IOException;

	/**
	 * 根据资源相对路径创建新资源
	 */
	Resource createRelative(String relativePath) throws IOException;

	/**
	 * 资源文件名
	 */
	@Nullable
	String getFilename();

	/**
	 * 资源描述
	 */
	String getDescription();

}
