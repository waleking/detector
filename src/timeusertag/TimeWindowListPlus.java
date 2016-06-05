package timeusertag;

import java.io.Serializable;
import java.util.ArrayList;

import cc.mallet.types.Alphabet;

import common.LogUtil;

public class TimeWindowListPlus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6153048748092257124L;

	public transient ArrayList<SimpleUserInstanceListPlus> timeWindowlist = new ArrayList<SimpleUserInstanceListPlus>();

	public Alphabet tagAlphabet = new Alphabet();
	public Alphabet tweetAlphabet = new Alphabet();
	public Alphabet userAlphabet = new Alphabet();

	/**
	 * reusing the alphabet, particularly used for test data set
	 * 
	 * @param tagAlphabet
	 * @param tweetAlphabet
	 * @param userAlphabet
	 */
	public TimeWindowListPlus(Alphabet tagAlphabet, Alphabet tweetAlphabet,
			Alphabet userAlphabet) {
		this.tagAlphabet = tagAlphabet;
		this.tweetAlphabet = tweetAlphabet;
		this.userAlphabet = userAlphabet;
	}

	public TimeWindowListPlus() {
	}

	public Alphabet getTagAlphabet() {
		return tagAlphabet;
	}

	public Alphabet getTweetAlphabet() {
		return tweetAlphabet;
	}

	public Alphabet getUserAlphabet() {
		return userAlphabet;
	}

	/**
	 * build up tagAlphabet, tweetAlphabet and userAlphabet
	 */
	public void add(SimpleUserInstanceListPlus userList) {
		timeWindowlist.add(userList);
	}

	public int size() {
		return this.timeWindowlist.size();
	}

	public SimpleUserInstanceListPlus get(int index) {
		return this.timeWindowlist.get(index);
	}

	/**
	 * turn to vector format, which can shrink the size of data
	 */
	public void turnToVectorFormat() {
		for (int i = 0; i < timeWindowlist.size(); i++) {
			SimpleUserInstanceListPlus userList = timeWindowlist.get(i);
			userList.turnToVectorFormat(this.tagAlphabet, this.tweetAlphabet,
					this.userAlphabet);
		}
	}

	/**
	 * @return |W|
	 */
	public int getTweetsTokenNum() {
		int tweetsTokenNum = 0;
		for (int i = 0; i < timeWindowlist.size(); i++) {
			SimpleUserInstanceListPlus userList = timeWindowlist.get(i);
			tweetsTokenNum += userList.getTweetsTokenNum();
		}
		return tweetsTokenNum;
	}

	/**
	 * @return |W|
	 */
	public int getTotalTweetsNum() {
		int totalTweetsNum = 0;
		for (int i = 0; i < timeWindowlist.size(); i++) {
			SimpleUserInstanceListPlus userList = timeWindowlist.get(i);
			LogUtil.logger().info(
					"getTotalTweetsNum: " + userList.getTotalTweetsNum());
			totalTweetsNum += userList.getTotalTweetsNum();
		}
		return totalTweetsNum;
	}

}
