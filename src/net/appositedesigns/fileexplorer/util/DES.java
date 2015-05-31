package net.appositedesigns.fileexplorer.util;

/*
 * Course: Cryptography and Network Security - HCMUT
 * Project: Simple DES Encryption and Decryption
 * Student Name: Nguyen Thanh Tien
 * Student ID: 51103600
 * Date create: Mar - 06, 2014
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DES {

	/**
	 * Convert string bit array to integer
	 * 
	 * @param input
	 *            a String type with '0' and '1' character
	 * @return an integer number
	 */
	public DES() {
		// TODO Auto-generated constructor stub
	}

	static int sToint(String input) {
		int tmp = input.length();
		int result = 0;
		for (int i = tmp - 1; i >= 0; i--) {
			char ctmp = input.charAt(i);
			result += ((ctmp == '0') ? 0 : 1)
					* (int) (Math.pow(2, (tmp - 1 - i)));
		}
		return result;
	}

	/**
	 * Convert a character array to integer
	 * 
	 * @param input
	 * @return
	 */
	static int charaToint(char[] input) {
		int iSize = input.length;
		int output = 0;
		for (int i = 0; i < iSize; i++) {
			output += ((input[i] == '0') ? 0 : 1)
					* (int) Math.pow(2, iSize - i - 1);
		}
		return output;
	}

	/**
	 * Convert a small integer to char array (2)
	 * 
	 * @param input
	 * @return
	 */
	static char[] intTochar2(int input) {
		char[] result = new char[2];
		for (int i = 0; i < 2; i++)
			result[i] = '0';
		int tmp = 1;
		while (input / 2 != 0) {
			if (input % 2 == 1)
				result[tmp] = '1';
			else
				result[tmp] = '0';
			tmp--;
			input = input / 2;
		}
		if (input % 2 == 1)
			result[tmp] = '1';
		else
			result[tmp] = '0';
		return result;
	}

	/**
	 * This function convert an integer number to char array (10) represent each
	 * bit.
	 * 
	 * @param input
	 * @return
	 */
	static char[] intTochar10(int input) {
		char[] result = new char[10];
		for (int i = 0; i < 10; i++)
			result[i] = '0';
		int tmp = 9;
		while (input / 2 != 0) {
			if (input % 2 == 1)
				result[tmp] = '1';
			else
				result[tmp] = '0';
			tmp--;
			input = input / 2;
		}
		if (input % 2 == 1)
			result[tmp] = '1';
		else
			result[tmp] = '0';
		return result;
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	static char[] intTochar8(int input) {
		char[] result = new char[8];
		for (int i = 0; i < 8; i++)
			result[i] = '0';
		int tmp = 7;
		while (input / 2 != 0) {
			if (input % 2 == 1)
				result[tmp] = '1';
			else
				result[tmp] = '0';
			tmp--;
			input = input / 2;
		}
		if (input % 2 == 1)
			result[tmp] = '1';
		else
			result[tmp] = '0';
		return result;
	}

	/**
	 * circular left shift
	 * 
	 * @param input
	 * @param nunberBit
	 * @return
	 */
	static char[] leftShip(char[] input, int nunberBit) {
		int iSize = input.length;
		int tmp = nunberBit % iSize;
		char[] result = new char[iSize];
		if (tmp == 0)
			return input;
		for (int i = 0; i < iSize; i++) {
			result[i] = input[(i + tmp) % iSize];
		}

		return result;
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	static char[][] p10(char[] input) {
		char[][] result = new char[2][5];
		result[0][0] = input[2];
		result[0][1] = input[4];
		result[0][2] = input[1];
		result[0][3] = input[6];
		result[0][4] = input[3];
		result[1][0] = input[9];
		result[1][1] = input[0];
		result[1][2] = input[8];
		result[1][3] = input[7];
		result[1][4] = input[5];
		return result;
	}

	/**
	 * 
	 * @param input
	 * @param keyId
	 * @return
	 */
	static char[] p8(char[][] input, int keyId) {
		int bitShip = (keyId == 1) ? 1 : 3;
		char[][] temp = new char[2][];
		temp[0] = leftShip(input[0], bitShip);
		temp[1] = leftShip(input[1], bitShip);
		char[] result = new char[8];
		result[0] = temp[1][0];
		result[1] = temp[0][2];
		result[2] = temp[1][1];
		result[3] = temp[0][3];
		result[4] = temp[1][2];
		result[5] = temp[0][4];
		result[6] = temp[1][4];
		result[7] = temp[1][3];
		return result;
	}

	/**
	 * 
	 * @param K
	 * @return
	 */
	static char[][] SDESKeySchedule(int K) {
		char[] a;
		char[][] result = new char[2][];
		a = intTochar10(K);

		char[][] p10c = p10(a);
		result[0] = p8(p10c, 1);
		result[1] = p8(p10c, 2);
		return result;
	}

	static char[] initPermutation(int input) {
		char[] a = intTochar8(input);
		char[] result = { a[1], a[5], a[2], a[0], a[3], a[7], a[4], a[6] };
		return result;
	}

	static int finalPermutation(char[] input) {
		char[] result = { input[3], input[0], input[2], input[4], input[6],
				input[1], input[7], input[5] };
		return charaToint(result);
	}

	static char excluseOr(char i1, char i2) {
		return ((i1 == i2) ? '0' : '1');
	}

	static char[] excluseOrArray(char[] i1, char[] i2) {
		if (i1.length == i2.length) {
			char[] result = new char[i1.length];
			for (int i = 0; i < i1.length; i++) {
				result[i] = excluseOr(i1[i], i2[i]);
			}
			return result;
		} else
			return null;
	}

	static char[] expanPermute(char[] input) {
		char[] result = { input[3], input[0], input[1], input[2], input[1],
				input[2], input[3], input[0] };
		return result;
	}

	static char[] fK(char[] input, char[] key) {
		char[][] temp = { { input[0], input[1], input[2], input[3] },
				{ input[4], input[5], input[6], input[7] } };
		char[] rep = expanPermute(temp[1]);

		char[] repXor = excluseOrArray(rep, key);

		int[][] S0 = { { 1, 0, 3, 2 }, { 3, 2, 1, 0 }, { 0, 2, 1, 3 },
				{ 3, 1, 3, 2 } };
		int[][] S1 = { { 0, 1, 2, 3 }, { 2, 0, 1, 3 }, { 3, 0, 1, 0 },
				{ 2, 1, 0, 3 } };

		char[] temp1 = { repXor[0], repXor[3] };
		char[] temp2 = { repXor[1], repXor[2] };
		char[] rightS0 = intTochar2(S0[charaToint(temp1)][charaToint(temp2)]);

		char[] temp3 = { repXor[4], repXor[7] };
		char[] temp4 = { repXor[5], repXor[6] };
		char[] rightS1 = intTochar2(S1[charaToint(temp3)][charaToint(temp4)]);

		char[] rightS = { rightS0[1], rightS1[1], rightS1[0], rightS0[0] };

		char[] left = excluseOrArray(rightS, temp[0]);
		char[] result = { left[0], left[1], left[2], left[3], temp[1][0],
				temp[1][1], temp[1][2], temp[1][3] };
		return result;
	}

	static char[] sWitch(char[] input) {
		char[] result = { input[4], input[5], input[6], input[7], input[0],
				input[1], input[2], input[3] };
		return result;
	}

	static int cipher(byte input, char[][] key) {
		int tmp = (int) input & 0x000000FF;
		return finalPermutation(fK(sWitch(fK(initPermutation(tmp), key[0])),
				key[1]));
	}

	static int decypt(byte input, char[][] key) {
		int tmp = (int) input & 0x000000FF;
		return finalPermutation(fK(sWitch(fK(initPermutation(tmp), key[1])),
				key[0]));
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] readBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			throw new IOException("Could not completely read file "
					+ file.getName() + "as is too long (" + length
					+ " bytes, max supported " + Integer.MAX_VALUE + ")");
		}
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) > 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}
		is.close();
		return bytes;
	}

	/**
	 * @param args
	 */
	public static void process(String args, String url) throws IOException {
		// TODO Auto-generated method stub

		/*
		 * if(args.length != 1){ System.out.println("Usage: lab1 key(10bits)");
		 * System.exit(1); }
		 */

		int key10Num = sToint(args);
		/**
		 * can't get path frome url here;
		 */
		// Path path = Paths.get(url);
		// byte[] data = Files.readAllBytes(path);
		File input = new File(url);
		byte[] data = readBytesFromFile(input);

		byte[] data1 = new byte[data.length];
		char[] key10 = { '1', '0', '1', '0', '0', '0', '0', '0', '1', '0' };
		char[][] key = SDESKeySchedule(key10Num);

		int tmp;
		for (int i = 0; i < data.length; i++) {
			tmp = cipher(data[i], key);
			data1[i] = (byte) tmp;
		}

		FileOutputStream oFile = new FileOutputStream(url);
		oFile.write(data1);
		oFile.close();

		// path = Paths.get(url);
		// data = Files.readAllBytes(path);
		/*
		 * data = readBytesFromFile(input);
		 * 
		 * byte[] data2 = new byte[data.length]; for (int i = 0; i <
		 * data.length; i++) { tmp = decypt(data[i], key); data2[i] = (byte)
		 * tmp; }
		 * 
		 * oFile = new FileOutputStream(url); oFile.write(data2); oFile.close();
		 */
	}

}
