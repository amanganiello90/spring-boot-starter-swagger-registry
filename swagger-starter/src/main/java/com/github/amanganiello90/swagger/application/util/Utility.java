package com.github.amanganiello90.swagger.application.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.amanganiello90.swagger.application.storage.StorageProperties;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;

public class Utility {

	private final static String YAML_FILE_TEXT = "yaml-filelist.txt";
	private final static String SWAGGER_FOLDER = "swagger";
	private final static String SPRING_BOOT_JAR_FOLDER = "BOOT-INF/";
	private final static String SPRING_BOOT_LIB_FOLDER = "lib";
	private final static String EXTRACT_DEPENDENCY_FOLDER = "target/api-dependency";
	private final static Logger log = LoggerFactory.getLogger("");

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
		boolean contained=false;

		try {
			if (f.exists() && !f.isDirectory()) {
				out = new PrintWriter(new FileOutputStream(new File(stringFile), true));
			} else {
				out = new PrintWriter(stringFile);
			}

			contained = checkIfAlreadyContainsTheString(stringFile, yamlName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} finally {
			if (!contained) {
				out.println(yamlName);
			}
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
	public static void unJarResources(String jarFile, String destDir, String templateFolder,
			String optionalJarNameContains) throws IOException {
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
				java.io.FileOutputStream fos = null; // input
				// stream
				if (optionalJarNameContains != null) {
					if (file.getName().contains(optionalJarNameContains)) {
						fos = new java.io.FileOutputStream(f);

						while (is.available() > 0) { // write contents of 'is'
														// to
							// 'fos'
							fos.write(is.read());
						}

					}

					else {

					}

				}

				else {

					fos = new java.io.FileOutputStream(f);

					while (is.available() > 0) { // write contents of 'is' to
													// 'fos'
						fos.write(is.read());
					}

				}

				if (fos != null) {
					fos.close();
				}
				is.close();
			}

		}
	}

	/**
	 * method to extract yaml from jar file
	 * 
	 * @param swaggerYAMLArtifacts
	 */
	public static void extractYAMLFromJar(String swaggerYAMLArtifacts) {
		File rootFileExecutePath = new File("");
		String rootPath = rootFileExecutePath.getAbsolutePath();
		File locationFilePom = new File(rootPath + "/pom.xml");
		File jarFilesLocation = new File(rootPath + "/" + EXTRACT_DEPENDENCY_FOLDER);
		File swaggerDestFolderFiles = new File(Utility.getRootPathWithoutFile() + SWAGGER_FOLDER);

		if (locationFilePom.exists()) {
			String mavenHome = System.getenv("MAVEN_HOME");

			if (mavenHome == null) {
				mavenHome = System.getenv("M2_HOME");
			}

			if (mavenHome == null) {
				throw new IllegalStateException("You have to set MAVEN_HOME or M2_HOME variable");
			}

			InvocationRequest request = new DefaultInvocationRequest();

			request.setPomFile(locationFilePom);
			request.setGoals(Arrays.asList("dependency:copy-dependencies -DoutputDirectory=" + EXTRACT_DEPENDENCY_FOLDER
					+ " -DincludeArtifactIds=" + swaggerYAMLArtifacts));
			Invoker invoker = new DefaultInvoker();
			invoker.setMavenHome(new File(mavenHome));
			invoker.setOutputHandler(null);

			InvocationResult resultStart = null;
			try {
				resultStart = invoker.execute(request);
			} catch (MavenInvocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (resultStart.getExitCode() != 0) {
				throw new IllegalStateException(
						"Impossible to get jar yaml dependencies with maven dependency:copy plugin");

			}

			if (!jarFilesLocation.exists()) {
				/*
				 * throw new IllegalStateException(
				 * "The maven dependency:copy plugin has not created the jar folder. Maybe the property 'outputDirectory' is overwritten by your pom."
				 * );
				 */
				log.warn(
						"The maven dependency:copy plugin has not created the jar folder. Maybe the property 'outputDirectory' is overwritten by your pom or not exist artifact declared in your swagger property");
			}

			extractSingleJar(jarFilesLocation, swaggerDestFolderFiles);

		}

		else {

			String jar = System.getProperty("java.class.path");
			String jarFiles[] = swaggerYAMLArtifacts.split(";");

			try {
				for (String jarEx : jarFiles) {
					Utility.unJarResources(rootPath + "/" + jar, Utility.getRootPathWithoutFile(),
							SPRING_BOOT_LIB_FOLDER, jarEx);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			File jarExtractedFilesLocation = new File(
					Utility.getRootPathWithoutFile() + SPRING_BOOT_JAR_FOLDER + SPRING_BOOT_LIB_FOLDER + "/");

			extractSingleJar(jarExtractedFilesLocation, swaggerDestFolderFiles);

			try {
				FileUtils.deleteDirectory(jarExtractedFilesLocation);
				FileUtils.deleteDirectory(jarExtractedFilesLocation.getParentFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private static void moveYAML(File swaggerDestFolderFiles) {

		File[] yamlFiles = swaggerDestFolderFiles.listFiles();

		if (yamlFiles != null) {
			for (File yaml : yamlFiles) {
				Utility.writeTextYAML(yaml.getName());
				yaml.renameTo(new File(Utility.getRootPathWithoutFile() + yaml.getName()));
			}

			try {
				FileUtils.deleteDirectory(swaggerDestFolderFiles);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private static void extractSingleJar(File jarLocation, File swaggerDestFolderFiles) {

		File[] jarFiles = jarLocation.listFiles();

		if (jarFiles != null) {

			for (File jar : jarFiles) {
				try {
					Utility.unJarResources(jar.getAbsolutePath(), Utility.getRootPathWithoutFile(), SWAGGER_FOLDER,
							null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Utility.moveYAML(swaggerDestFolderFiles);

			}
		}

	}

	private static boolean checkIfAlreadyContainsTheString(String fileName, String stringContained) {
		try {
			FileReader textFileReader = new FileReader(fileName);
			@SuppressWarnings("resource")
			BufferedReader bufReader = new BufferedReader(textFileReader);

			char[] buffer = new char[8096];

			int numberOfCharsRead = bufReader.read(buffer); // read will be from
			// memory
			while (numberOfCharsRead != -1) {
				// System.out.println(String.valueOf(buffer, 0,
				// numberOfCharsRead));
				if (String.valueOf(buffer, 0, numberOfCharsRead).contains(stringContained)) {
					return true;
				}
				numberOfCharsRead = textFileReader.read(buffer);
			}

			bufReader.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
