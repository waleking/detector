package preprocess;

import common.IniReader;

public class DefaultGlobalValueTweets {
	/**
	 * ========================================================================
	 * IO part
	 * ========================================================================
	 */

	// filter out meaningless tweet less than 3 words
	// if strs.length<checkRuleLowerbound, then filter out
	public static int checkRuleLowerbound = 4;
	public static String rawData = "data/20131220/final_user_tag_messages_3k";
	// only containing tweets content
	public static String contentData = "data/20131220/tweets.txt";

	// tweets.vectors file which is correspond to tweets.txt
	public static String contentMalletFormat = "data/20131220/tweets.vectors";
	public static String trainSetMalletFormat = "data/20131220/train.vectors";
	public static String testSetMalletFormat = "data/20131220/test.vectors";

	public static String dataFolder = "data/20131220";

	// stop words
	public static String simpleStopwords = "stoplists/zh.txt";
	public static String extraStopwords = "stoplists/lowfreqwords.txt";

	// cross validation
	public static int crossValidationFoldNum = 10;
	public static String trainSetIds = "data/20131220/train.ids";
	public static String testSetIds = "data/20131220/test.ids";

	/**
	 * ========================================================================
	 * model part
	 * ========================================================================
	 */
	// model
	public static String modelFile = "model/lda.model";
	// public static String modelFile="model/tmpResult30.model";
	public static String modelFolder = "model";

	// topic number
	// public static int topicNum=200;
	// public static int topicNum = 90;
	public static int topicNum = Integer.parseInt(IniReader.getValue("input",
			"topicNum", "config.ini"));
	public static int eventNum = Integer.parseInt(IniReader.getValue("input",
			"eventNum", "config.ini"));
	// public static int eventNum = 20;

	// we train model in Gibbs Sampling in 200 sweeps
	public static int trainIterationNum = Integer.parseInt(IniReader.getValue(
			"input", "trainIterationNum", "config.ini"));
	public static int testInterationNum = Integer.parseInt(IniReader.getValue(
			"input", "testInterationNum", "config.ini"));
	public static int wikiWeight = Integer.parseInt(IniReader.getValue("input",
			"wikiWeight", "config.ini"));
	public static double eventProportion = Double.parseDouble(IniReader
			.getValue("input", "eventProportion", "config.ini"));
	public static double chatterProportion = Double.parseDouble(IniReader
			.getValue("input", "chatterProportion", "config.ini"));
	public static double stopwordProportion = Double.parseDouble(IniReader
			.getValue("input", "stopwordProportion", "config.ini"));

	// every 10 sweeps, we output the model into the tmpResult
	public static int whenOutputTempResult = 10;
	public static String whereOutputTempResult = "model/tmpResult";

	// top 20 words in each topic are going to be shown
	public static int topKWordsToShow = 40;

	/**
	 * ========================================================================
	 * pipeline part
	 * ========================================================================
	 */
	public static int evaluateThreadCheckPoint = 30;// every 30 seconds check
													// the model/*.model, use
													// these file to compute the
													// perplexity

	/**
	 * ========================================================================
	 * evaluate part
	 * ========================================================================
	 */
	/**
	 * .pmi file restores the pmi score
	 */
	public static String externalFile = "data/20140103/2011-10_final";

	/**
	 * how many top words should be used to calculate average pmi
	 */
	public static int avaragePmiTopK = 20;// how manyaverage pmi
	public static double pmiSmoothingFactor = 0.001;

	/**
	 * data for userTagLDA
	 */
	// public static String userFile = "data/20140103/2011-10_final";
	public static String userFile = "data/20140114/2012-16_final_final";
	// public static String userFile = "data/20140103/2010-07_final";
	// public static String userFile="data/20131220/final_user_tag_messages_3k";

	public static String timeWindowFolder = IniReader.getValue("input",
			"timeWindowFolder", "config.ini");
	public static String[] trainingFiles = IniReader
			.getValue("input", "trainingFiles", "config.ini").trim().split(",");

	// public static String[] trainingFiles = { "tmp.json" };

	public static String[] testFiles = IniReader
			.getValue("input", "testingFiles", "config.ini").trim().split(",");

	public static String fileFinaltag = "data/finaltagfile.txt";
	public static String userList = "data/user.list";
	public static String tagCounts = "data/tagStatistics.txt";
	public static String tagFreqfreq = "data/tagFreqfreq.txt";
	public static String tagNumPerUser = "data/tagNumPerUser.txt";

	public static String tagStopwords = "data/tagStopwords.txt";
	public static String cleanUserTag = "data/cleanUserTag.txt";
	public static String malletTmpFile = "data/text.vectors";
}
