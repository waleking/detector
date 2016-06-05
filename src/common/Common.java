package common;

import java.io.File;

import preprocess.DefaultGlobalValue;
import waleking.util.MyFile;

public class Common {

	/**
	 * e.g. model/tmpResult.model->model
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName) {
		String[] fileNameParts = fileName.split("\\.");
		if (fileNameParts.length == 1) {
			return null;
		} else {
			return fileNameParts[fileNameParts.length - 1];
		}
	}

	/**
	 * remove all files with specific extension e.g. model
	 */
	public static void removeAllFiles(String folder, String extension) {
		File file = new File(folder);
		if (file.isFile()) {
			return;
		} else if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				String wholename = folder + "/" + children[i];
				if (getFileExtension(wholename).equals(extension)) {
					File fileToDel = new File(wholename);
					fileToDel.delete();
				}
			}
		}

	}

	/**
	 * get lineNum of the file
	 */
	public static int getLineNum(String filename) {
		MyFile reader = new MyFile(filename, "r");
		String line = reader.readln();
		int i = 0;
		while (line != null) {
			i = i + 1;
			line = reader.readln();
		}
		return i;
	}

	/**
	 * add line number for lines in a specific file
	 */
	public static void addlinenumForFile(String filename) {
		StringBuffer sb = new StringBuffer();

		MyFile reader = new MyFile(filename, "r");
		String line = reader.readln();
		int i = 1;
		while (line != null) {
			sb.append(i + "\t" + line + "\r\n");
			i = i + 1;
			line = reader.readln();
		}
		reader.close();

		MyFile writer = new MyFile(filename, "w");
		writer.print(sb.toString().trim());
		writer.close();
	}

	/**
	 * check if there exist line numbers in the given file
	 */
	public static boolean ifExsitLineNumber(String filename) {
		MyFile reader = new MyFile(filename, "r");
		String line = reader.readln();

		// check first 10 lines
		for (int i = 0; i < 10; i++) {
			String[] strParts = line.split("\t");
			try {
				int num = Integer.parseInt(strParts[0]);
				int realnum = i + 1;
				if (num != realnum) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
			line = reader.readln();
		}
		reader.close();
		return true;
	}

	/**
	 * get line's element number 1 \t 2 return 2
	 */
	public static int getCollumnNum(String line) {
		String[] parts = line.trim().split("\t");
		return parts.length;
	}

	public static void main(String[] args) {
		Common.removeAllFiles(DefaultGlobalValue.modelFolder, "model");
	}
}
