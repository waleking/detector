package usertagmodel;

import java.util.ArrayList;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import preprocess.DefaultGlobalValue;
import cc.mallet.types.Alphabet;

public class UserInstancePlus {
	public class Tweet {
		public Tweet(String tweetId, ArrayList<Integer> tweetContent) {
			this.tweetId = Long.parseLong(tweetId);
			this.tweetContent = tweetContent;
		}

		public long tweetId = -1L;
		public ArrayList<Integer> tweetContent = null;
	}

	public class RawTweet {
		public RawTweet(String tweetId, String tweetContent) {
			this.tweetId = tweetId;
			this.tweetContent = tweetContent;
		}

		public String tweetId = null;
		public String tweetContent = null;
	}

	public String rawUserId = "";

	public int userId = 0;

	public int getUserId() {
		return userId;
	}

	/**
	 * rawTweets does not need to be serialized
	 */
	public transient ArrayList<RawTweet> rawTweets = new ArrayList<RawTweet>();

	/**
	 * tweets is represented by tweet content feature ids
	 */
	public ArrayList<Tweet> tweets = new ArrayList<Tweet>();

	public ArrayList<Tweet> getTweets() {
		return tweets;
	}

	/**
	 * convert rawTweets to tweets
	 */
	public void turnRawTweetsToTweets(Alphabet alphabet) {
		for (int i = 0; i < this.rawTweets.size(); i++) {
			String rawTweet = this.rawTweets.get(i).tweetContent.trim();
			String tweetId = this.rawTweets.get(i).tweetId.trim();

			ArrayList<Integer> tweetContent = new ArrayList<Integer>();
			String[] tweetWords = rawTweet.split(" ");
			for (int j = 0; j < tweetWords.length; j++) {
				int wordId = alphabet.lookupIndex(tweetWords[j]);
				tweetContent.add(wordId);
			}
			this.tweets.add(new Tweet(tweetId, tweetContent));
		}
	}

	/**
	 * convert rawUserId to userId
	 */
	public void turnRawUserIdToUserId(Alphabet alphabet) {
		this.userId = alphabet.lookupIndex(this.rawUserId);
	}

	/**
	 * e.g. input: JSONArray ["训","唔","醒","小心","饮","\u0000"] output: 训 唔 醒 小心 饮
	 * and remove out @username
	 * 
	 * @param jsonMessageContent
	 * @return
	 */
	public static String getMessageContent(JSONArray jsonMessageContent) {
		String s = "";
		try {
			for (int i = 0; i < jsonMessageContent.length(); i++) {
				String item;
				item = (String) jsonMessageContent.get(i);
				// if (!item.equals("\u0000") && !item.substring(0,
				// 1).equals("@") ) {
				if (!item.equals("\u0000")) {
					s = s + " " + item;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return s.trim();
	}

	/**
	 * Filter out meaningless tweet containing less than 3 words e.g. 谢 记 ->
	 * flase 买 保时捷 昨晚 爽 跑 -> true
	 * 
	 * @param line
	 * @return
	 */
	public static boolean checkRule(String line) {
		String[] strs = line.trim().split(" ");
		if (strs.length < DefaultGlobalValue.checkRuleLowerbound) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * input:
	 * {"rawTags":["室内设计","创业","汽车","股票","数码","财经","手机"],"userId":"1000298872",
	 * "messages"
	 * :[{"crawlerTime":1339546080000,"messages":["训","唔","醒","小心","饮",
	 * "\u0000"]}]} output: add to userInstace.userProfiles and
	 * userInstance.rawTweets, and userInstance.tweetsIds (add on April 1st
	 * 2014)
	 * 
	 * @param line
	 */
	public void processJson(String line) {
		try {
			JSONObject jsonUser = new JSONObject(line);
			this.rawUserId = jsonUser.getString("userId");
			JSONArray messagesArray = jsonUser.getJSONArray("messages");

			HashSet<String> set = new HashSet<String>();
			for (int i = 0; i < messagesArray.length(); i++) {
				JSONObject jsonMessage = (JSONObject) messagesArray.get(i);
				String tweetId = jsonMessage.getString("tweetId");
				JSONArray jsonMessageContent = jsonMessage
						.getJSONArray("messages");
				String messageContent = getMessageContent(jsonMessageContent);
				if (!set.contains(messageContent) && !messageContent.equals("")) {
					set.add(messageContent);
					if (checkRule(messageContent) == true) {
						this.rawTweets
								.add(new RawTweet(tweetId, messageContent));
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
