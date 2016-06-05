package timeusertag;

import java.io.File;

import preprocess.DefaultGlobalValue;
import usertagmodel.UserInstancePlus;
import waleking.util.MyFile;
import cc.mallet.types.Alphabet;

import common.LogUtil;

public class PreprocessorPlus {

	public SimpleUserInstanceListPlus getUserInstanceList(String file,
			Alphabet tagAlphabet, Alphabet tweetAlphabet, Alphabet userAlphabet) {
		MyFile reader = new MyFile(file, "r");
		String line = reader.readln();
		SimpleUserInstanceListPlus userlist = new SimpleUserInstanceListPlus();
		while (line != null) {
			UserInstancePlus userInstance = new UserInstancePlus();
			userInstance.processJson(line);
			// add userInstance to the list, and build up tagAlphabet
			// tweetAlphabet and userAlphabet
			userlist.add(userInstance, tagAlphabet, tweetAlphabet, userAlphabet);
			line = reader.readln();
		}
		return userlist;
	}

	/**
	 * read in a single file
	 * 
	 * @param fileFolder
	 * @param file
	 * @return
	 */
	public TimeWindowListPlus getTimeWindowList(String fileFolder, String file) {
		long start = System.currentTimeMillis();

		TimeWindowListPlus timeWindowList = new TimeWindowListPlus();

		File folder = new File(fileFolder);
		if (!folder.isDirectory()) {
			System.err.println("please set the fileFolder");
			return null;
		} else {
			String wholename = fileFolder + "/" + file;
			timeWindowList.add(this.getUserInstanceList(wholename,
					timeWindowList.tagAlphabet, timeWindowList.tweetAlphabet,
					timeWindowList.userAlphabet));
			timeWindowList.turnToVectorFormat();

			LogUtil.logger()
					.info("tagAlphabet's size is"
							+ timeWindowList.tagAlphabet.size());
			LogUtil.logger().info(
					"tweetAlphabet's size is"
							+ timeWindowList.tweetAlphabet.size());
			LogUtil.logger().info(
					"userAlphabet's size is"
							+ timeWindowList.userAlphabet.size());
			LogUtil.logger().info(
					"tweets number is" + timeWindowList.getTotalTweetsNum());
			LogUtil.logger().info(
					"token number is" + timeWindowList.getTweetsTokenNum());
			// LogUtil.logger().info("tag token number is" +
			// ilist.getTagsTokenNum());
			long end = System.currentTimeMillis();
			LogUtil.logger().info(
					"reading files cost " + (end - start) + " milliseconds");

			return timeWindowList;
		}
	}

	/**
	 * readin a single file
	 * 
	 * @param fileFolder
	 * @param file
	 * @param tagAlphabet
	 * @param tweetAlphabet
	 * @param userAlphabet
	 * @return
	 */
	public TimeWindowListPlus getTimeWindowList(String fileFolder, String file,
			Alphabet tagAlphabet, Alphabet tweetAlphabet, Alphabet userAlphabet) {
		long start = System.currentTimeMillis();

		TimeWindowListPlus timeWindowList = new TimeWindowListPlus(tagAlphabet,
				tweetAlphabet, userAlphabet);

		File folder = new File(fileFolder);
		if (!folder.isDirectory()) {
			System.err.println("please set the fileFolder");
			return null;
		} else {
			String wholename = fileFolder + "/" + file;
			timeWindowList.add(this.getUserInstanceList(wholename,
					timeWindowList.tagAlphabet, timeWindowList.tweetAlphabet,
					timeWindowList.userAlphabet));
			timeWindowList.turnToVectorFormat();

			LogUtil.logger()
					.info("tagAlphabet's size is"
							+ timeWindowList.tagAlphabet.size());
			LogUtil.logger().info(
					"tweetAlphabet's size is"
							+ timeWindowList.tweetAlphabet.size());
			LogUtil.logger().info(
					"userAlphabet's size is"
							+ timeWindowList.userAlphabet.size());
			LogUtil.logger().info(
					"tweets number is" + timeWindowList.getTotalTweetsNum());
			LogUtil.logger().info(
					"token number is" + timeWindowList.getTweetsTokenNum());
			// LogUtil.logger().info("tag token number is" +
			// ilist.getTagsTokenNum());
			long end = System.currentTimeMillis();
			LogUtil.logger().info(
					"reading file " + file + " cost " + (end - start)
							+ " milliseconds");

			return timeWindowList;
		}
	}

	public TimeWindowListPlus getTimeWindowList(String fileFolder,
			String[] files, Alphabet tagAlphabet, Alphabet tweetAlphabet,
			Alphabet userAlphabet) {
		long start = System.currentTimeMillis();

		TimeWindowListPlus timeWindowList = new TimeWindowListPlus(tagAlphabet,
				tweetAlphabet, userAlphabet);

		File folder = new File(fileFolder);
		if (!folder.isDirectory()) {
			System.err.println("please set the fileFolder");
			return null;
		} else {
			for (int i = 0; i < files.length; i++) {
				String wholename = fileFolder + "/" + files[i];
				timeWindowList.add(this.getUserInstanceList(wholename,
						timeWindowList.tagAlphabet,
						timeWindowList.tweetAlphabet,
						timeWindowList.userAlphabet));
			}
			timeWindowList.turnToVectorFormat();

			LogUtil.logger()
					.info("tagAlphabet's size is"
							+ timeWindowList.tagAlphabet.size());
			LogUtil.logger().info(
					"tweetAlphabet's size is"
							+ timeWindowList.tweetAlphabet.size());
			LogUtil.logger().info(
					"userAlphabet's size is"
							+ timeWindowList.userAlphabet.size());
			LogUtil.logger().info(
					"tweets number is" + timeWindowList.getTotalTweetsNum());
			LogUtil.logger().info(
					"token number is" + timeWindowList.getTweetsTokenNum());
			// LogUtil.logger().info("tag token number is" +
			// ilist.getTagsTokenNum());
			long end = System.currentTimeMillis();
			LogUtil.logger().info(
					"reading files cost " + (end - start) + " milliseconds");

			return timeWindowList;
		}
	}

	public TimeWindowListPlus getTimeWindowList(String fileFolder,
			String[] files) {
		long start = System.currentTimeMillis();

		TimeWindowListPlus timeWindowList = new TimeWindowListPlus();

		File folder = new File(fileFolder);
		if (!folder.isDirectory()) {
			System.err.println("please set the fileFolder");
			return null;
		} else {
			for (int i = 0; i < files.length; i++) {
				String wholename = fileFolder + "/" + files[i];
				timeWindowList.add(this.getUserInstanceList(wholename,
						timeWindowList.tagAlphabet,
						timeWindowList.tweetAlphabet,
						timeWindowList.userAlphabet));
			}
			timeWindowList.turnToVectorFormat();

			LogUtil.logger()
					.info("tagAlphabet's size is"
							+ timeWindowList.tagAlphabet.size());
			LogUtil.logger().info(
					"tweetAlphabet's size is"
							+ timeWindowList.tweetAlphabet.size());
			LogUtil.logger().info(
					"userAlphabet's size is"
							+ timeWindowList.userAlphabet.size());
			LogUtil.logger().info(
					"tweets number is" + timeWindowList.getTotalTweetsNum());
			LogUtil.logger().info(
					"token number is" + timeWindowList.getTweetsTokenNum());
			// LogUtil.logger().info("tag token number is" +
			// ilist.getTagsTokenNum());
			long end = System.currentTimeMillis();
			LogUtil.logger().info(
					"reading files cost " + (end - start) + " milliseconds");

			return timeWindowList;
		}
	}

	public TimeWindowListPlus getTimeWindowList(String fileFolder) {
		long start = System.currentTimeMillis();

		TimeWindowListPlus timeWindowList = new TimeWindowListPlus();

		File folder = new File(fileFolder);
		if (!folder.isDirectory()) {
			System.err.println("please set the fileFolder");
			return null;
		} else {
			String[] files = folder.list();
			for (int i = 0; i < files.length; i++) {
				String wholename = fileFolder + "/" + files[i];
				timeWindowList.add(this.getUserInstanceList(wholename,
						timeWindowList.tagAlphabet,
						timeWindowList.tweetAlphabet,
						timeWindowList.userAlphabet));
			}
			timeWindowList.turnToVectorFormat();

			LogUtil.logger()
					.info("tagAlphabet's size is"
							+ timeWindowList.tagAlphabet.size());
			LogUtil.logger().info(
					"tweetAlphabet's size is"
							+ timeWindowList.tweetAlphabet.size());
			LogUtil.logger().info(
					"userAlphabet's size is"
							+ timeWindowList.userAlphabet.size());
			LogUtil.logger().info(
					"tweets number is" + timeWindowList.getTotalTweetsNum());
			LogUtil.logger().info(
					"token number is" + timeWindowList.getTweetsTokenNum());

			// LogUtil.logger().info("tag token number is" +
			// ilist.getTagsTokenNum());
			long end = System.currentTimeMillis();
			LogUtil.logger().info(
					"reading files cost " + (end - start) + " milliseconds");

			return timeWindowList;
		}
	}

	public static void main(String[] args) {
		// Pre.getUserInstanceList(DefaultGlobalValue.userFile);
		// Preprocessor pre = new Preprocessor();
		// TimeWindowList timeWindowList = pre
		// .getTimeWindowList(DefaultGlobalValue.timeWindowFolder);
		// UserProfileList userProfileList = new UserProfileList(timeWindowList,
		// timeWindowList.userAlphabet);

		PreprocessorPlus pre = new PreprocessorPlus();
		pre.getTimeWindowList(DefaultGlobalValue.timeWindowFolder);
	}
}
