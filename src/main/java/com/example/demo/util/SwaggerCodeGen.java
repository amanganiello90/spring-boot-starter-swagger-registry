package com.example.demo.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import io.swagger.codegen.ClientOptInput;

import io.swagger.codegen.DefaultGenerator;
import io.swagger.codegen.config.CodegenConfigurator;
import org.apache.commons.io.FileUtils;

public class SwaggerCodeGen {

	private final static String TEMP_OUTPUT = "tempOutput";
	private final static String TEMPLATE_FOLDER = "typescript-templates";
	private static String OUTPUT_DIR;
	private static String EXTRACTED_TEMPLATES;
	private final static String JAR_SUFFIX = "BOOT-INF/classes/";
	private final static String EXTRACTED_TEMPLATES_NOT_ROOT = JAR_SUFFIX + TEMPLATE_FOLDER;

	private static void generateClient(String yamlPath, String zipPath, String language) {

		OUTPUT_DIR = Utility.getRootPathWithoutFile() + "/" + TEMP_OUTPUT;

		CodegenConfigurator configurator = new CodegenConfigurator();

		configurator.setInputSpec(yamlPath);

		configurator.setVerbose(false);

		configurator.setLang(language);

		configurator.setOutputDir(OUTPUT_DIR);

		if (language.equals("typescript-angular")) {
			EXTRACTED_TEMPLATES = Utility.getRootPathWithoutFile() + EXTRACTED_TEMPLATES_NOT_ROOT;

			if (!(new File(EXTRACTED_TEMPLATES).exists())) {

				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				URL url = loader.getResource(TEMPLATE_FOLDER);
				String path = url.getPath();
				path = path.replace("file:/", "/");
				if (!path.contains(".jar")) {
					EXTRACTED_TEMPLATES = path;
				} else {
					String jarFile[] = path.split("!/");
					System.out.println(jarFile[0]);

					try {

						Utility.unJarResources(jarFile[0], Utility.getRootPathWithoutFile(), TEMPLATE_FOLDER);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

			configurator.setTemplateDir(EXTRACTED_TEMPLATES);

		}

		final ClientOptInput input = configurator.toClientOptInput();

		new DefaultGenerator().opts(input).generate();

		try {
			Utility.zipFolder(OUTPUT_DIR, zipPath);
			FileUtils.deleteDirectory(new File(OUTPUT_DIR));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void DownloadClient(String filename, String language) {

		String stringFilePath = Utility.getRootPathWithoutFile() + filename;
		File filePath = new File(stringFilePath);
		String stringZipFile = stringFilePath;
		if (language.equals("java")) {
			stringFilePath = stringFilePath.replace("-java.zip", ".yaml");
		} else if (language.equals("typescript-angular")) {
			stringFilePath = stringFilePath.replace("-typescript.zip", ".yaml");
		} else {
			throw new IllegalArgumentException("swagger cli language '" + language + "' not allowed");
		}

		if (!filePath.exists()) {
			SwaggerCodeGen.generateClient(stringFilePath, stringZipFile, language);

		}
	}

}
