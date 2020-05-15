package com.github.johnsonmoon.commons;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.security.SecureRandom;

/**
 * Create by xuyh at 2020/5/14 22:55.
 */
public class CsvGenerator {
    private static SecureRandom random = new SecureRandom();
    private static final String BASE_NUMBER_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyz";

    public static String getRandomString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(BASE_NUMBER_CHARACTERS.length());
            stringBuilder.append(BASE_NUMBER_CHARACTERS.charAt(number));
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) throws Exception {
        String linesStr = System.getProperty("csv.lines");
        String columnsStr = System.getProperty("csv.columns");
        String filePathName = System.getProperty("csv.file.path.name");
        Integer lines = Integer.parseInt(linesStr);
        Integer columns = Integer.parseInt(columnsStr);

        File file = new File(filePathName);
        if (!file.exists()) {
            System.out.println("Target file not exist.");
            System.exit(-1);
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, true));
            for (int i = 0; i < lines; i++) {
                if (i % 1000 == 0) {
                    System.out.println("Line: " + i + ", Size: " + formatFileSize(file));
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (int j = 0; j < columns; j++) {
                    stringBuilder.append(getRandomString(6));
                    stringBuilder.append(",");
                }
                writer.write(stringBuilder.substring(0, stringBuilder.length() - 1) + "\r\n");
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private static String formatFileSize(File file) {
        long length = file.length();
        if (length >= 1024 && length < 1024 * 1024) {
            return String.format("%s KB", length / 1024);
        } else if (length >= 1024 * 1024 && length < 1024 * 1024 * 1024) {
            return String.format("%s MB", length / (1024 * 1024));
        } else if (length >= 1024 * 1024 * 1024) {
            return String.format("%s GB", length / (1024 * 1024 * 1024));
        } else {
            return String.format("%s B", length);
        }
    }
}
