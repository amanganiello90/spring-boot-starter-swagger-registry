package com.example.demo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipOutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;

import com.example.demo.storage.StorageProperties;

public class Utility {

	private final static String YAML_FILE_TEXT = "yaml-filelist.txt";

	public static String getRootPath() {

		return "file:" + getRootPathWithoutFile();

	}

	public static String getRootPathWithoutFile() {
		StorageProperties properties = new StorageProperties();
		Path rootLocation = Paths.get(properties.getLocation());
		return rootLocation.toAbsolutePath().toString() + "/";
	}

	public static void writeTextYAML(String yamlName) {

		PrintWriter out = null;
		String stringFile = getRootPathWithoutFile() + YAML_FILE_TEXT;
		File f = new File(stringFile);

		try {
			if (f.exists() && !f.isDirectory()) {
				out = new PrintWriter(new FileOutputStream(new File(stringFile), true));
			} else {
				out = new PrintWriter(stringFile);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} finally {
			out.println(yamlName);
			out.close();
		}

	}

	/**
	 * method to create a zip
	 * 
	 * @param srcFolder
	 * @param destZipFile
	 * @throws Exception
	 */
	public static void zipFolder(String srcFolder, String destZipFile) throws Exception {
		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;

		fileWriter = new FileOutputStream(destZipFile);
		zip = new ZipOutputStream(fileWriter);

		addFolderToZip("", srcFolder, zip, true);
		zip.flush();
		zip.close();
	}

	/**
	 * method to add file in a zip
	 * 
	 * @param path
	 * @param srcFile
	 * @param zip
	 * @throws Exception
	 */
	private static void addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {

		File folder = new File(srcFile);
		if (folder.isDirectory()) {
			addFolderToZip(path, srcFile, zip, false);
		} else {
			byte[] buf = new byte[1024];
			int len;
			@SuppressWarnings("resource")
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}

	/**
	 * method to add folder in a zip
	 * 
	 * @param path
	 * @param srcFile
	 * @param zip
	 * @param first
	 * @throws Exception
	 */
	private static void addFolderToZip(String path, String srcFolder, ZipOutputStream zip, boolean first)
			throws Exception {

		File folder = new File(srcFolder);

		for (String fileName : folder.list()) {
			if (path.equals("") && first) {
				addFileToZip("", srcFolder + "/" + fileName, zip);
			} else {
				addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
			}
		}
	}

	/**
	 * method to extract jar file
	 * 
	 * @param jarFile
	 * @param destDir
	 * @throws IOException
	 */
	public static void unJarResources(String jarFile, String destDir, String templateFolder) throws IOException {
		@SuppressWarnings("resource")
		java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile);
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			java.util.jar.JarEntry file = (java.util.jar.JarEntry) entries.nextElement();

			if (file.toString().contains(templateFolder)) {

				// create another folders
				String folders[] = file.toString().split("/");
				if (folders.length > 1) {
					String folder = "";
					for (int i = 0; i < folders.length - 1; i++) {
						folder = folder + "/" + folders[i];
					}
					java.io.File f = new java.io.File(destDir + java.io.File.separator + folder);
					f.mkdirs();
				}

				java.io.File f = new java.io.File(destDir + java.io.File.separator + file.getName());

				if (file.isDirectory()) { // if its a directory, create it
					f.mkdir();

					continue;
				}
				java.io.InputStream is = jar.getInputStream(file); // get the
																	// input
																	// stream
				java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
				while (is.available() > 0) { // write contents of 'is' to 'fos'
					fos.write(is.read());
				}
				fos.close();
				is.close();
			}

		}
	}

}
