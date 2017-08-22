package com.caiyongji;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Disassemble {
	private String fileUri;
	private Path projectFolderPath;
	private BufferedWriter writer;
	private StringBuffer tempFileContents;
	private boolean flag = true;

	public Disassemble(String fileUri, String projcetFolderUri) {
		this.fileUri = fileUri;
		this.projectFolderPath = Paths.get(projcetFolderUri);

	}

	public void disassemble() {
		try {
			Files.lines(Paths.get(fileUri)).forEach(line -> {
				line = DESUtil.decrypt(line, Constants.DES_KEY);
				if (line.contains(Constants.SPECIAL_SIGNATURE)) {
					Path outPath = Paths
							.get(projectFolderPath + line.substring(0, line.indexOf(Constants.SPECIAL_SIGNATURE)));
					// System.out.println(outPath);
					if (flag) {
						flag = false;
					} else {
						try {
							writer.write(tempFileContents.toString());
							writer.close();
						} catch (Exception e) {
						}
					}
					tempFileContents = new StringBuffer();
					try {
						Files.createDirectories(Paths.get(outPath.toFile().getParent()));
						if (outPath.toFile().isFile()) {
							Files.write(outPath, new byte[0], StandardOpenOption.DELETE_ON_CLOSE);
						}
						writer = Files.newBufferedWriter(outPath, StandardOpenOption.CREATE_NEW);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					tempFileContents.append(line + "\n");
				}
			});
			writer.write(tempFileContents.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Disassemble disassemble = new Disassemble("C:\\Users\\i\\Desktop\\SpringBoot.txt", "C:/Users/i/Desktop/springboot");
		disassemble.disassemble();
	}
}
