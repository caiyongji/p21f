package com.caiyongji;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Assemble {
	private String projectUri;
	private BufferedWriter writer;
	private StringBuffer buffer = new StringBuffer();

	public Assemble(String projectUri, String outPutFile) {
		this.projectUri = Paths.get(projectUri).toString();

		Path outPath = Paths.get(outPutFile);
		try {
			Files.createDirectories(Paths.get(outPath.toFile().getParent()));
			if (outPath.toFile().isFile()) {
				Files.write(outPath, new byte[0], StandardOpenOption.DELETE_ON_CLOSE);
			}
			writer = Files.newBufferedWriter(outPath, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void assemble() {
		assemble(projectUri);
		try {
			writer.write(buffer.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void assemble(String uri) {
		try {
			Files.list(Paths.get(uri)).filter(path -> !path.toString().endsWith(".class"))
					.filter(path -> !path.toString().endsWith("target"))
					.filter(path -> !path.toString().endsWith("bin")).forEach(path -> {
						if (path.toFile().isDirectory()) {
							assemble(path.toString());
						} else if (path.toFile().isFile()) {
							try {
								// System.out.println("path:" + path);
								// System.out.println(path.toString().replace(projectUri,
								// "") + Constants.specialStr + "\n");
								buffer.append(DESUtil.encrypt(
										path.toString().replace(projectUri, "") + Constants.SPECIAL_SIGNATURE,
										Constants.DES_KEY) + "\n");
								Files.lines(path).forEach(line -> {
									try {
										buffer.append(DESUtil.encrypt(line, Constants.DES_KEY) + "\n");
									} catch (Exception e) {
										e.printStackTrace();
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Assemble assemble = new Assemble("C:\\Users\\workspace\\SpringBoot", "C:/Users/Desktop/SpringBoot.txt");
		assemble.assemble();
	}
}