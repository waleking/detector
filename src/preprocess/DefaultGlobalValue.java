package preprocess;

public class DefaultGlobalValue {
	/**
	 * ========================================================================
	 * IO part
	 * ========================================================================
	 */

	// filter out meaningless tweet less than 3 words
	// if strs.length<checkRuleLowerbound, then filter out
	public static int checkRuleLowerbound = 1;
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
	public static int topicNum = 90;
	public static int eventNum = 30;

	// we train model in Gibbs Sampling in 200 sweeps
	public static int trainIterationNum = 200;
	// every 10 sweeps, we output the model into the tmpResult
	public static int whenOutputTempResult = 10;
	public static String whereOutputTempResult = "model/tmpResult";

	// top 20 words in each topic are going to be shown
	public static int topKWordsToShow = 20;

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

	public static String timeWindowFolder = "data/20140114";
	// public static String[]
	// trainingFiles={"2012-1_final_final","2012-2_final_final","2012-3_final_final"};
	// public static String[]
	// public static String[] trainingFiles = { "2012-1_final_final",
	// "2012-2_final_final", "2012-3_final_final" };
	// public static String[] trainingFiles = { "2012-1_final_final",
	// "2012-2_final_final", "2012-3_final_final", "2012-4_final_final" };
	// public static String[] trainingFiles = {
	// "2012-1_final_final","2012-2_final_final","2012-3_final_final"};
	// public static String[] trainingFiles = { "2012-1_final_final",
	// "2012-2_final_final", "2012-3_final_final" };
	public static String[] trainingFiles = { "2012-1_final_final",
			"2012-2_final_final", "2012-3_final_final", "2012-4_final_final" };

	public static String[] testFiles = { "2012-1_final_final",
			"2012-2_final_final", "2012-3_final_final", "2012-4_final_final",
			"2012-5_final_final", "2012-6_final_final", "2012-7_final_final",
			"2012-8_final_final", "2012-9_final_final" };
	// public static String[] testFiles = { "2012-2_final_final",
	// "2012-3_final_final", "2012-4_final_final", "2012-5_final_final",
	// "2012-6_final_final", "2012-7_final_final", "2012-8_final_final",
	// "2012-9_final_final", "2012-10_final_final", "2012-11_final_final",
	// "2012-12_final_final", "2012-13_final_final",
	// "2012-14_final_final", "2012-15_final_final",
	// "2012-16_final_final", "2012-17_final_final",
	// "2012-18_final_final", "2012-19_final_final",
	// "2012-20_final_final", "2012-21_final_final",
	// "2012-22_final_final", "2012-23_final_final",
	// "2012-24_final_final", "2012-25_final_final",
	// "2012-26_final_final", "2012-27_final_final",
	// "2012-28_final_final", "2012-29_final_final",
	// "2012-30_final_final", "2012-31_final_final",
	// "2012-32_final_final", "2012-33_final_final",
	// "2012-34_final_final", "2012-35_final_final",
	// "2012-36_final_final", "2012-37_final_final",
	// "2012-38_final_final", "2012-39_final_final",
	// "2012-40_final_final", "2012-41_final_final",
	// "2012-42_final_final", "2012-43_final_final",
	// "2012-44_final_final", "2012-45_final_final",
	// "2012-46_final_final", "2012-47_final_final",
	// "2012-48_final_final", "2012-49_final_final" };
	// public static String[] testFiles = { "2012-12_final_final",
	// "2012-13_final_final", "2012-14_final_final",
	// "2012-15_final_final", "2012-16_final_final",
	// "2012-17_final_final", "2012-18_final_final",
	// "2012-19_final_final", "2012-20_final_final",
	// "2012-21_final_final", "2012-22_final_final",
	// "2012-23_final_final", "2012-24_final_final",
	// "2012-25_final_final", "2012-26_final_final",
	// "2012-27_final_final", "2012-28_final_final",
	// "2012-29_final_final", "2012-30_final_final",
	// "2012-31_final_final", "2012-32_final_final",
	// "2012-33_final_final", "2012-34_final_final",
	// "2012-35_final_final", "2012-36_final_final",
	// "2012-37_final_final", "2012-38_final_final",
	// "2012-39_final_final", "2012-40_final_final",
	// "2012-41_final_final", "2012-42_final_final",
	// "2012-43_final_final", "2012-44_final_final",
	// "2012-45_final_final", "2012-46_final_final",
	// "2012-47_final_final", "2012-48_final_final", "2012-49_final_final" };

	public static String fileFinaltag = "data/finaltagfile.txt";
	public static String userList = "data/user.list";
	public static String tagCounts = "data/tagStatistics.txt";
	public static String tagFreqfreq = "data/tagFreqfreq.txt";
	public static String tagNumPerUser = "data/tagNumPerUser.txt";

	public static String tagStopwords = "data/tagStopwords.txt";
	public static String cleanUserTag = "data/cleanUserTag.txt";
	public static String malletTmpFile = "data/text.vectors";
}