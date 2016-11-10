package com.daydays.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author dingpc
 *
 */
public final class FileUtils {
	/**
	 * log.
	 */
	private static final Log log = LogFactory.getLog(FileUtils.class);

	/**
	 * default constructor
	 */
	private FileUtils() {
	}

	public static File createFile(String fileName) throws IOException {
		File file = new File(fileName);

		// Create file if it does not exist
		try {
			boolean success = file.createNewFile();
			if (success) {
				// File did not exist and was created
			} else {
				throw new IllegalArgumentException("error in creating file : " + fileName);
			}
		} catch (IOException e) {
			throw e;
		}
		return file;
	}

	public static File getFile(String fileName) throws IOException {
		File file = null;
		try {
			file = new File(fileName);
			if (!file.exists()) {
				throw new IOException();
			}
		} catch (IOException e) {
			throw e;
		}
		return file;
	}

	public static boolean renameFile(String oldName, String newName) {
		// File (or directory) with old name
		File file = new File(oldName);

		// File (or directory) with new name
		File file2 = new File(newName);

		// Rename file (or directory)
		boolean success = file.renameTo(file2);
		if (!success) {
			log.error("Can not rename file: from " + oldName + ", to  " + newName);
		}
		return success;

	}

	public static boolean moveFile(String fileName, String dirName) {
		// File (or directory) to be moved
		File file = new File(fileName);

		// Destination directory
		File dir = new File(dirName);

		// Move file to new directory
		boolean success = file.renameTo(new File(dir, file.getName()));
		if (!success) {
			log.error("Can not move file :from " + fileName + ", to  " + dirName);
		}
		return success;

	}

	public static void deleteFile(String fileName) {
		boolean success = (new File(fileName)).delete();
		if (!success) {
			log.warn("Can not delete file:" + fileName);
		}
	}

	public static long getFileSize(String fileNmae) {
		File file = new File(fileNmae);

		// Get the number of bytes in the file
		long length = file.length();
		return length;
	}

	public static String readFromStandardInput() throws IOException {
		String str = "";
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(System.in));

			while (str != null) {
				System.out.print("> prompt ");
				str = in.readLine();
				// process(str);
			}
		} catch (IOException e) {
			if (in != null) {
				in.close();
			}
			throw e;
		}
		return str;
	}

	public static String readFromFile(String fileName) {
		try {
			StringBuffer content = new StringBuffer();
			FileInputStream is = new FileInputStream(fileName);
			int n = 1024;
			byte[] buffer = new byte[n];
			while (true) {
				int length = is.read(buffer, 0, n);
				if (length > 0) {
					content.append(new String(buffer, 0, length).replaceAll("\r", ""));
				} else {
					break;
				}
			}
			is.close();
			return content.toString();
		} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}

	public static String readFromFile2(String fileName) {
		try {
			StringBuffer content = new StringBuffer();
			File file = new File(fileName);
			FileReader sr = new FileReader(file);
			// InputStreamReader sr = new InputStreamReader(new
			// FileInputStream(fileName));
			BufferedReader bufReader = new BufferedReader(sr);
			int n = 1024;
			char[] buffer = new char[n];
			while (true) {
				int length = bufReader.read(buffer, 0, n);
				if (length > 0) {
					content.append(new String(buffer, 0, length).replaceAll("\r", ""));
				} else {
					break;
				}
			}
			bufReader.close();
			return content.toString();
		} catch (Exception e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
	}

	public static List<String> readLineFromFile(String fileName, IExecutable executable) throws IOException {

		List<String> result = new ArrayList<>();

		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(fileName));
			String temp;
			while ((temp = in.readLine()) != null) {
				result.add(temp);
				if (result.size() >= 1000) {
					executable.execute(result);
					result = new ArrayList<>();
				}
			}
			if (result.size() > 0) {
				executable.execute(result);
			}
		} catch (IOException e) {
			log.error("", e);
			throw e;
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return result;

	}

	public static List<String> readLineFromFile(String fileName) throws IOException {
		List<String> result = new ArrayList<>();

		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(fileName));
			String temp;
			while ((temp = in.readLine()) != null) {
				result.add(temp);
			}
			in.close();
		} catch (IOException e) {
			if (in != null) {
				in.close();
			}
			throw e;
		}
		return result;
	}

	@SuppressWarnings("resource")
	public static String readLineFromFile(String fileName, int lineNumber) throws IOException {
		if (lineNumber < 0) {
			throw new IllegalArgumentException("line number is :" + lineNumber);
		}
		String str = null;
		LineNumberReader in = null;
		try {
			in = new LineNumberReader(new FileReader(fileName));
			String temp;
			for (int i = 0; i <= lineNumber; i++) {
				log.debug("current line number is " + in.getLineNumber());
				if ((temp = in.readLine()) != null) {
					str = temp;
				} else {
					str = null;
					throw new IOException("exceed the max line number");
				}
			}
			in.close();
		} catch (IOException e) {
			if (in != null) {
				in.close();
			}
			throw e;
		}
		return str;
	}

	// Reading a File into a Byte Array
	// This example implements a method that reads the entire contents of a file
	// into a byte array.
	// See also e35 Reading Text from a File.
	// Returns the contents of the file in a byte array.
	@SuppressWarnings("resource")
	public static byte[] readBytesFromFile(File file) throws IOException {
		byte[] bytes = null;
		InputStream is = null;
		try {
			is = new FileInputStream(file);

			// Get the size of the file
			long length = file.length();

			// You cannot create an array using a long type.
			// It needs to be an int type.
			// Before converting to an int type, check
			// to ensure that file is not larger than Integer.MAX_VALUE.
			if (length > Integer.MAX_VALUE) {
				// File is too large
			}

			// Create the byte array to hold the data
			bytes = new byte[(int) length];

			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			// Ensure all the bytes have been read in
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file " + file.getName());
			}

			// Close the input stream and return bytes
			is.close();
		} catch (IOException e) {
			if (is != null) {
				is.close();
			}
			throw e;
		}
		return bytes;
	}

	// Writing to a File
	public static void writeToFile(String str, String fileName) throws IOException {
		// If the file does not already exist, it is automatically created.
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(fileName));
			out.write(str);
			out.close();
		} catch (IOException e) {
			if (out != null) {
				out.close();
			}
			throw e;
		}
	}

	// Appending to a File
	public static void appendToFile(String str, String fileName) throws IOException {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(fileName, true));
			out.write(str);

			out.close();
		} catch (IOException e) {
			if (out != null) {
				out.close();
			}
			throw e;
		}
	}

	// Using a Random Access File
	public static void writeToRandomAccessFile(String str, String fileName) throws IOException {
		RandomAccessFile raf = null;
		try {
			File f = new File(fileName);
			raf = new RandomAccessFile(f, "rw");

			// Read a character
			// char ch = raf.readChar();

			// Seek to end of file
			raf.seek(f.length());

			// Append to the end
			raf.writeChars(str);
			raf.close();
		} catch (IOException e) {
			if (raf != null) {
				raf.close();
			}
			throw e;
		}
	}

	// Copies src file to dst file.
	// If the dst file does not exist, it is created
	public void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static boolean checkDir(String absolutePath, boolean create) {
		File destfile = new File(absolutePath);
		if (!destfile.exists()) {
			if (create) {
				if (!destfile.mkdirs()) {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}

	public static File[] listSubFile(String dir) {
		if (checkDir(dir, false)) {
			return new File(dir).listFiles();
		}
		return null;
	}
}
