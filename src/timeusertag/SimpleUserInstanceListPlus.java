package timeusertag;

import java.util.ArrayList;

import usertagmodel.UserInstancePlus;
import cc.mallet.types.Alphabet;

public class SimpleUserInstanceListPlus {

	/**
	 * raw list doesn't need to be serialized
	 */
	public ArrayList<UserInstancePlus> userlist = new ArrayList<UserInstancePlus>();

	public Alphabet tagAlphabet;// point to TimeWindowsList's tagAlphabet
	public Alphabet tweetAlphabet;// point to TimeWindowsList's tweetAlphabet
	public Alphabet userAlphabet;// point to TimeWindowsList's userAlphabet

	public SimpleUserInstanceListPlus() {
		this.tagAlphabet = new Alphabet();
		this.tweetAlphabet = new Alphabet();
		this.userAlphabet = new Alphabet();
	}

	/**
	 * add userInstance, and build up tagAlphabet tweetAlphabet and userAlphabet
	 * 
	 * @param user
	 *            and three alphabet
	 */
	public void add(UserInstancePlus user, Alphabet tagAlphabet,
			Alphabet tweetAlphabet, Alphabet userAlphabet) {
		userlist.add(user);

		// add user tweet word to tweetAlphabet
		if (tweetAlphabet == null) {
			System.err.println("tweetAlphabet is null");
		} else {
			for (int i = 0; i < user.rawTweets.size(); i++) {
				String tweet = (user.rawTweets.get(i)).tweetContent;
				String[] words = tweet.trim().split(" ");
				for (int j = 0; j < words.length; j++) {
					tweetAlphabet.lookupIndex(words[j]);
				}
			}
		}

		// add userId to userAlphabet
		if (userAlphabet == null) {
			System.err.println("userAlphabet is null");
		} else {
			userAlphabet.lookupIndex(user.rawUserId);
		}
	}

	/**
	 * turn to vector format, which can shrink the size of data
	 */
	public void turnToVectorFormat(Alphabet tagAlphabet,
			Alphabet tweetAlphabet, Alphabet userAlphabet) {
		for (int i = 0; i < userlist.size(); i++) {
			UserInstancePlus user = userlist.get(i);
			if (user.rawTweets != null && !user.rawTweets.isEmpty()) {
				user.turnRawTweetsToTweets(tweetAlphabet);
			}
			// check if filtering out redundant words can enhance the
			// performance, and the result shows the performance is bad.
			// user.turnRawTweetsToTweetsSet(tweetAlphabet);
			user.turnRawUserIdToUserId(userAlphabet);
		}
		this.tagAlphabet = tagAlphabet;
		this.tweetAlphabet = tweetAlphabet;
		this.userAlphabet = userAlphabet;
	}

	public int size() {
		return userlist.size();
	}

	public UserInstancePlus get(int i) {
		return userlist.get(i);
	}

	/**
	 * @return |D|
	 */
	public int getTotalTweetsNum() {
		int rawTweetsNum = 0;
		for (int i = 0; i < userlist.size(); i++) {
			UserInstancePlus user = userlist.get(i);
			rawTweetsNum = rawTweetsNum + user.rawTweets.size();
		}
		return rawTweetsNum;
	}

	/**
	 * @return |W|
	 */
	public int getTweetsTokenNum() {
		int tweetsTokenNum = 0;
		for (int i = 0; i < userlist.size(); i++) {
			UserInstancePlus user = userlist.get(i);
			for (int j = 0; j < user.tweets.size(); j++) {
				ArrayList<Integer> tweet = (user.tweets.get(j)).tweetContent;
				tweetsTokenNum += tweet.size();
			}
		}
		return tweetsTokenNum;
	}

	/**
	 * @return |U|
	 */
	public int getUserNum() {
		return userlist.size();
	}
}
