package usertagmodel;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import tool.MathUtil;
import usertagmodel.UserInstancePlus.Tweet;
import cc.mallet.types.Alphabet;

/**
 * print topic's top k words of
 * 
 * @author Waleking
 * 
 */
public class PrintUtil {
	/**
	 * print top 20 words in the topic prefix can be "Tag" or "Event" or
	 * "Interest", than the result may be "Tag Topic 3's top 20 words",
	 * "Event Topic 3's top 20 words" or "Interest Topic 3's top 20 words"
	 */
	public static String printTopWords(int topK, Alphabet alphabet,
			int[][] topic, String prefix) {
		class WordProb implements Comparable<Object> {
			public double prob = 0.0;
			public int word = 0;

			public WordProb(int word, double prob) {
				this.word = word;
				this.prob = prob;
			}

			/*
			 * this.means xx.compareTo(y)<0 means x<y and x.compareTo(y)=0 means
			 * x=y whilex.compareTo(y)>0 means x>y
			 * 
			 * @see java.lang.Comparable#compareTo(java.lang.Object)
			 */
			@Override
			public int compareTo(Object y) {
				if (this.prob > ((WordProb) y).prob)
					return -1;
				else if (this.prob == ((WordProb) y).prob) {
					return 0;
				} else {
					return 1;
				}
			}
		}
		int topicNum = topic.length;

		StringBuffer sb = new StringBuffer();
		for (int k = 0; k < topicNum; k++) {
			WordProb[] wordProbs = new WordProb[alphabet.size()];
			for (int v = 0; v < alphabet.size(); v++) {
				wordProbs[v] = new WordProb(v, topic[k][v]);
			}
			Arrays.sort(wordProbs);
			sb.append(prefix + " Topic " + k + "'s top " + topK + " words"
					+ "\r\n");
			for (int i = 0; i < topK; i++) {
				sb.append((alphabet.lookupObject(wordProbs[i].word)+":"+topic[k][wordProbs[i].word]).toString()
						+ "\t");
			}
			sb.append("\r\n");
		}
		return sb.toString();
	}

	/**
	 * print top words of topic distributions into json format
	 */
	public static JSONObject[] printTopWordsToJson(int topK, Alphabet alphabet,
			int[][] topic) {
		class WordProb implements Comparable<Object> {
			public double prob = 0.0;
			public int word = 0;

			public WordProb(int word, double prob) {
				this.word = word;
				this.prob = prob;
			}

			/*
			 * this.means xx.compareTo(y)<0 means x<y and x.compareTo(y)=0 means
			 * x=y whilex.compareTo(y)>0 means x>y
			 * 
			 * @see java.lang.Comparable#compareTo(java.lang.Object)
			 */
			@Override
			public int compareTo(Object y) {
				if (this.prob > ((WordProb) y).prob)
					return -1;
				else if (this.prob == ((WordProb) y).prob) {
					return 0;
				} else {
					return 1;
				}
			}
		}

		int topicNum = topic.length;
		JSONObject[] jsonsToReturn = new JSONObject[topicNum];
		try {
			for (int k = 0; k < topicNum; k++) {
				// {"topicId":"topic0","list":["java":"2002","code":"2000",...],"totalWords":"54000"}
				// "topicId":"topic*"
				JSONObject topicRoot = new JSONObject();
				topicRoot.put("topicId", "topic" + Integer.toString(k));
				// sort
				WordProb[] wordProbs = new WordProb[alphabet.size()];
				for (int v = 0; v < alphabet.size(); v++) {
					wordProbs[v] = new WordProb(v, topic[k][v]);
				}
				Arrays.sort(wordProbs);

				// "list":[*]
				JSONArray array = new JSONArray();
				for (int i = 0; i < topK; i++) {
					// "java":"2002"
					JSONObject jsonWordProbPair = new JSONObject();
					String word = (alphabet.lookupObject(wordProbs[i].word))
							.toString();
					jsonWordProbPair.put(word, wordProbs[i].prob);
					array.put(jsonWordProbPair);
				}
				topicRoot.put("list", array);
				// "totalWords":54000
				topicRoot.put("totalWords", MathUtil.sum(topic[k]));

				// return the topic
				jsonsToReturn[k] = topicRoot;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonsToReturn;
	}

	/**
	 * screenName\tmessage
	 */
	public static String printScreenNameAndMessage(Integer userId,
			Alphabet userAlphabet, long tweetId,
			ArrayList<Integer> tweetTokenList, Alphabet tweetAlphabet) {

		String screenName = printRawUsers(userId, userAlphabet);
		String message = printRawContents(tweetTokenList, tweetAlphabet);
		return Long.toString(tweetId) + "\t" + screenName + "\t" + message;
	}

	/**
	 * turn user id into screenname
	 */
	public static String printRawUsers(Integer userId, Alphabet alphabet) {
		return (alphabet.lookupObject(userId)).toString();
	}

	/**
	 * turn list of word ids into raw content
	 * 
	 * @param list
	 * @param alphabet
	 * @return
	 */
	public static String printRawContents(ArrayList<Integer> list,
			Alphabet alphabet) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			sb.append((alphabet.lookupObject(list.get(i))).toString() + "\t");
		}
		sb.append("\r\n");
		return sb.toString();
	}

	/**
	 * print top 20 words in the topic prefix can be "Tag" or "Event" or
	 * "Interest", than the result may be "Tag Topic 3's top 20 words",
	 * "Event Topic 3's top 20 words" or "Interest Topic 3's top 20 words"
	 */
	public static String printTopWords(int topK, Alphabet alphabet,
			int[] topic, String prefix) {
		class WordProb implements Comparable<Object> {
			public double prob = 0.0;
			public int word = 0;

			public WordProb(int word, double prob) {
				this.word = word;
				this.prob = prob;
			}

			/*
			 * this.means xx.compareTo(y)<0 means x<y and x.compareTo(y)=0 means
			 * x=y whilex.compareTo(y)>0 means x>y
			 * 
			 * @see java.lang.Comparable#compareTo(java.lang.Object)
			 */
			@Override
			public int compareTo(Object y) {
				if (this.prob > ((WordProb) y).prob)
					return -1;
				else if (this.prob == ((WordProb) y).prob) {
					return 0;
				} else {
					return 1;
				}
			}
		}

		StringBuffer sb = new StringBuffer();
		WordProb[] wordProbs = new WordProb[alphabet.size()];
		for (int v = 0; v < alphabet.size(); v++) {
			wordProbs[v] = new WordProb(v, topic[v]);
		}
		Arrays.sort(wordProbs);
		sb.append(prefix + " Topic's top " + topK + " words" + "\r\n");
		for (int i = 0; i < topK; i++) {
			sb.append((alphabet.lookupObject(wordProbs[i].word)).toString()
					+ "\t");
		}
		sb.append("\r\n");
		return sb.toString();
	}
}
