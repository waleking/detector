package ietm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import preprocess.DefaultGlobalValueTweets;
import timeusertag.PreprocessorPlus;
import timeusertag.SimpleUserInstanceListPlus;
import timeusertag.TimeWindowListPlus;
import tool.MemoryTest;
import usertagmodel.PrintUtil;
import usertagmodel.UserInstancePlus;
import waleking.util.MyFile;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Dirichlet;
import cc.mallet.util.Randoms;

import common.LogUtil;

/**
 * LDA
 * 
 * @author Waleking
 * 
 */
public class LDAWithPrior implements Serializable {
	private static final long serialVersionUID = -3335514051631979791L;
	// prior parameters
	public double[] alpha;
    public double alphaSum=0;
	public double beta = 0.005;

	// dataset related
	public int tweetVSize = 0;
	public int topicNumber = 0;
    public int wikiTopicNumber=0;
	public int userNumber = 0;
	public int timeWindowNum = 0;

	/**
	 * seven statistics
	 */
	// public int[][] cu;// include cu[u][0], cu[u][1]

	// user interest related
	public int[][] iCkv;// c^{0}_{k,v}
    //word vector
    public double[][] wordVecs;

	// additional prior knowledge
	public double[][] tau;
	public double[] tauK;

	// user interest or event related words
	public int[] interestTokenPerTopic;// c_{k,.}

	private Randoms rs = new Randoms();

	/**
	 * hidden topics
	 */
	public transient Zu[][][] z;// \vec{z}_{t,u,d}
	public transient Ndk[][][] ndk;// \vec{n}_{t,u,d}

    /**
     * statistics for hyper parmaters' estimation
     */
    private int maxTokenNums=0;
    private transient int[] oneDocTopicCounts;//temporal histogram of topic related token's number
    private transient int[] docLengthCounts; // histogram of document sizes
    private transient int[][] topicDocCounts; // histogram of document/topic counts


	/**
	 * hidden topic of user's tweets
	 */
	public class Zu implements Serializable {
		private static final long serialVersionUID = 5637209082555554246L;
		public int numofTokens = 0;
		public int[] val = null;

		public Zu(int numofTokens) {
			this.numofTokens = numofTokens;
			val = new int[numofTokens];
			Arrays.fill(val, 0);
		}
	}

	/**
	 * remember what happened to document d by user's daily bubbles.
	 */
	public class Ndk implements Serializable {
		private static final long serialVersionUID = 634392189800448078L;
		public int numofTokens = 0;
		public int[] val = null;

		public Ndk(int numofTokens) {
			this.numofTokens = numofTokens;
			val = new int[this.numofTokens];
			Arrays.fill(val, 0);
		}
	}

	/**
	 * initialize the model
	 */
	public void init(TimeWindowListPlus timeWindows) {
		init(timeWindows, this.topicNumber);
	}

	/**
	 * check if the process is valid
	 */
	public void checkIfValid() {
		int interestTokenNum = 0;
		for (int k = 0; k < this.topicNumber; k++) {
			interestTokenNum += this.interestTokenPerTopic[k];
		}
		LogUtil.logger().info("interestTokenNum=" + interestTokenNum);
	}

	public LDAWithPrior() {

	}

	/**
	 * init toInitIETM by wiki.topics
	 * 
	 * @param toInitIETM
	 * @param alphabet
	 */
	public LDAWithPrior initByWikipedia(LDAWithPrior toInitIETM,
			TimeWindowListPlus trainingData, float wikiWeight) {

		// Step1, update alphabet by wiki
		int oldTweetVSize = trainingData.tweetAlphabet.size();
		LogUtil.logger().info(
				"Before adding wikipedia's alphabet. size(alphabet)="
						+ trainingData.tweetAlphabet.size());

        //the alphabet of background.topics includes the alphabet of wiki.topics
        String[] extraFilesForTopics=new String[]{"wiki.topics","background.topics"};
		buildExtraAlphabet(extraFilesForTopics,trainingData.tweetAlphabet);

		LogUtil.logger().info(
				"After adding wikipedia's alphabet. size(alphabet)="
						+ trainingData.tweetAlphabet.size());

		// Step2, update iCkv part
		toInitIETM.tweetVSize = trainingData.tweetAlphabet.size();

		// reuse the trained model, interest topic-token distribution
		int[][] iCkv = new int[toInitIETM.topicNumber][];
		int[] interestTokenPerTopic = new int[toInitIETM.tweetVSize];
		double[][] tau = new double[toInitIETM.topicNumber][];
		for (int k = 0; k < toInitIETM.topicNumber; k++) {
			iCkv[k] = new int[toInitIETM.tweetVSize];
			tau[k] = new double[toInitIETM.tweetVSize];
			Arrays.fill(tau[k], 0);
			for (int v = 0; v < oldTweetVSize; v++) {
				iCkv[k][v] = toInitIETM.iCkv[k][v];// IMPORTANT
				tau[k][v] = toInitIETM.tau[k][v];// IMPORTANT
			}
			interestTokenPerTopic[k] = toInitIETM.interestTokenPerTopic[k];
		}
        //update prior knowledge from wiki.topics
		String filename = "wiki.topics";
		MyFile reader = new MyFile(filename, "r");
		String line = reader.readln();
		int lineNum = 1;

		LogUtil.logger().info("oldTweetVSize=" + oldTweetVSize);

		while (line != null) {
			line = line.trim();
			if (!line.equals("")) {
				String[] array = line.split("\t");
				int topicId = Integer.parseInt(array[0].split("topic")[1]);
                if(topicId+1>this.wikiTopicNumber){
                    this.wikiTopicNumber=topicId+1;
                }
				String word = array[1];
				float prob = Float.parseFloat(array[2]);

				int v = trainingData.tweetAlphabet.lookupIndex(word);
				float wikiPart = (new Float(prob * wikiWeight));
				if (wikiPart < 0) {
					LogUtil.logger().error("NEGATIVE v: " + v + " " + wikiPart);
				}
				try {
					tau[topicId][v] = tau[topicId][v] + wikiPart;// IMPORTANT
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.logger().error(line);
					LogUtil.logger().error("topicId:" + topicId + " " + "world" + v);
					LogUtil.logger().error("trainingData.tweetAlphabet.size"+trainingData.tweetAlphabet.size());
					System.exit(0);
				}
				tauK[topicId] += wikiPart;
			}
			line = reader.readln();
			lineNum++;
			if (lineNum % 1000000 == 0) {
				LogUtil.logger().info(
						"processed " + lineNum + " lines in wiki.topics");
			}
		}
		toInitIETM.tau = tau;
		toInitIETM.tauK = tauK;
		toInitIETM.iCkv = iCkv;
		toInitIETM.interestTokenPerTopic = interestTokenPerTopic;

		LogUtil.logger().info("finish init by wikipedia");
		return toInitIETM;
	}


	/**
	 * init toInitIETM by background.topics
	 * 
	 * @param toInitIETM
	 * @param alphabet
	 */
	public LDAWithPrior initByWikiBackgroundTopics(LDAWithPrior toInitIETM,TimeWindowListPlus trainingData,float wikiWeight) {

        //update prior knowledge from wiki.topics
		String filename = "background.topics";
		MyFile reader = new MyFile(filename, "r");
		String line = reader.readln();
		int lineNum = 1;

		while (line != null) {
			line = line.trim();
			if (!line.equals("")) {
				String[] array = line.split("\t");
				String word = array[0];
				float prob = Float.parseFloat(array[1]);

				int v = trainingData.tweetAlphabet.lookupIndex(word);
				float wikiPart = (new Float(prob * wikiWeight));
				if (wikiPart < 0) {
					LogUtil.logger().error("NEGATIVE v: " + v + " " + wikiPart);
				}
				try {
		            for (int k = this.wikiTopicNumber; k < toInitIETM.topicNumber; k++) {
					    toInitIETM.tau[k][v] = toInitIETM.tau[k][v] + wikiPart;// IMPORTANT
                        toInitIETM.tauK[k]=toInitIETM.tauK[k]+wikiPart;
		            }
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.logger().error(line);
					LogUtil.logger().error("error happened in initByWikiBackgroundTopics on world" + v);
					LogUtil.logger().error("trainingData.tweetAlphabet.size"+trainingData.tweetAlphabet.size());
					System.exit(0);
				}
			}
			line = reader.readln();
			lineNum++;
			if (lineNum % 1000000 == 0) {
				LogUtil.logger().info(
						"processed " + lineNum + " lines in wiki.topics");
			}
		}

		LogUtil.logger().info("finish init by wikipedia background topic");
		return toInitIETM;
	}

	public void randomAssignHiddenTopics(TimeWindowListPlus timeWindows) {
		// random assign tweet's topic
		for (int t = 0; t < timeWindows.timeWindowlist.size(); t++) {
			SimpleUserInstanceListPlus ilist = timeWindows.timeWindowlist
					.get(t);
			for (int user = 0; user < ilist.size(); user++) {
				UserInstancePlus userInstance = ilist.get(user);
				int u = userInstance.getUserId();
				int tweetNum = userInstance.getTweets().size();
				this.z[t][u] = new Zu[tweetNum];
				this.ndk[t][u] = new Ndk[tweetNum];
				for (int d = 0; d < tweetNum; d++) {
					ArrayList<Integer> tweet = userInstance.getTweets().get(d).tweetContent;
					int tweetLength = tweet.size();
                    if(tweetLength>maxTokenNums){
                        maxTokenNums=tweetLength;
                    }
					this.z[t][u][d] = new Zu(tweetLength);
					this.ndk[t][u][d] = new Ndk(this.topicNumber);

					for (int n = 0; n < tweetLength; n++) {
						int w = tweet.get(n);
                        double[] vec=this.wordVecs[w];
						int k = rs.nextDiscrete(vec);
						// user interest
						this.z[t][u][d].val[n] = k;
						this.ndk[t][u][d].val[k]++;

						this.iCkv[k][w]++;
						this.interestTokenPerTopic[k]++;// iCkv[k]=\sum_{v=1}^{V}iCkv[k][v];
					}// end of n (tweetLength)
				}// end of d (tweetNum)
			}// end of user

		}// end of t (timeWindowList.size())
        
	}// end of randomAssighHiddenTopics


    public void normalizeVec(double[] vec){
        double sum=0;
        for(int i=0;i<vec.length;i++){
            sum=sum+vec[i];
        }
        if(sum!=0){
            for(int i=0;i<vec.length;i++){
                vec[i]=vec[i]/sum;
            }
        }else{
            for(int k=this.wikiTopicNumber;k<this.topicNumber;k++){
                vec[k]=1.0/(this.topicNumber-this.wikiTopicNumber);
            }
        }
    }

    public void updateWordVecs(boolean isInit,TimeWindowListPlus trainingData){
       if(isInit==true){
           int alphabetSize=trainingData.tweetAlphabet.size();
           this.wordVecs=new double[alphabetSize][];
           for(int v=0;v<alphabetSize;v++){
               this.wordVecs[v]=new double[this.topicNumber];
               Arrays.fill(this.wordVecs[v],0);
           }
           for(int k=0;k<this.topicNumber;k++){
               for(int v=0;v<alphabetSize;v++){
                   this.wordVecs[v][k]=this.tau[k][v];
               }
           }
           for(int v=0;v<alphabetSize;v++){
               normalizeVec(this.wordVecs[v]);
           }
       } else{

       }
    }

    /**
     * update hyper parameters' related statistics
     */
    public void updateHyperParameterStat(TimeWindowListPlus timeWindows){
        //init docLengthCounts and topicDocCounts
        if(this.docLengthCounts==null){
            this.docLengthCounts=new int[maxTokenNums+1];
        }else{
            Arrays.fill(this.docLengthCounts,0);
        }
        if(this.topicDocCounts==null){
            this.topicDocCounts=new int[this.topicNumber][maxTokenNums+1];
        }
        else{
            for(int k=0;k<this.topicNumber;k++){
                Arrays.fill(this.topicDocCounts[k],0);
            }
        }
        for (int t = 0; t < timeWindows.timeWindowlist.size(); t++) {
            SimpleUserInstanceListPlus ilist = timeWindows.timeWindowlist
                .get(t);
            for (int user = 0; user < ilist.size(); user++) {
                UserInstancePlus userInstance = ilist.get(user);
                int u = userInstance.getUserId();
                int tweetNum = userInstance.getTweets().size();
                for (int d = 0; d < tweetNum; d++) {
                    ArrayList<Integer> tweet = userInstance.getTweets().get(d).tweetContent;
                    int tweetLength = tweet.size();
                    this.docLengthCounts[tweetLength]++;
                    Arrays.fill(this.oneDocTopicCounts,0);
                    for (int n = 0; n < tweetLength; n++) {
                        int k=this.z[t][u][d].val[n];
                        this.oneDocTopicCounts[k]++;
                    }
                    for(int k=0;k<this.topicNumber;k++){
                        this.topicDocCounts[k][this.oneDocTopicCounts[k]]++;
                    }
                }
            }
        }
    }

    /**
     * call it after updateHyperParameterStat(**)
     */
    public void updateAlpha(){
        for(int k=0;k<this.topicNumber;k++){
            this.alpha[k]=this.alpha[k]/this.alphaSum;
        }
    }

    public String traceICkv(TimeWindowListPlus timeWindowList) {
        int topic = 0;
        StringBuffer sb = new StringBuffer();
        for (int v = 0; v < 200; v++) {
            sb.append(this.iCkv[topic][v]).append(" ");
        }
        for (int v=0;v<200;v++) {
            if(this.iCkv[topic][v]>0){
               sb.append(timeWindowList.tweetAlphabet.lookupObject(v)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    public LDAWithPrior(LDAWithPrior trainedIETM,
            TimeWindowListPlus timeWindowList) {

        // prior parameters
        this.topicNumber = trainedIETM.topicNumber;
        this.alpha = new double[this.topicNumber];
        Arrays.fill(this.alpha,0.1);
        for(int k=0;k<this.topicNumber;k++){
            this.alphaSum=this.alphaSum+this.alpha[k];
        }

        this.beta = trainedIETM.beta;

        // dataset related
        this.tweetVSize = timeWindowList.tweetAlphabet.size();// specific
        this.userNumber = timeWindowList.userAlphabet.size();// specific
        this.timeWindowNum = timeWindowList.size();

        /**
         * seven statistics
         */
        // reuse the trained model, interest topic-token distribution
        this.iCkv = new int[this.topicNumber][];
        for (int k = 0; k < this.topicNumber; k++) {
            this.iCkv[k] = new int[this.tweetVSize];
            int v = 0;
            for (v = 0; v < trainedIETM.tweetVSize; v++) {
                this.iCkv[k][v] = trainedIETM.iCkv[k][v];
            }
            for (; v < this.tweetVSize; v++) {
                this.iCkv[k][v] = 0;
            }
        }
        // reuse the trained model, prior knowledge distribution
        this.tau = new double[this.topicNumber][];
        for (int k = 0; k < this.topicNumber; k++) {
            this.tau[k] = new double[this.tweetVSize];
            int v = 0;
            for (v = 0; v < trainedIETM.tweetVSize; v++) {
                this.tau[k][v] = trainedIETM.tau[k][v];
            }
            for (; v < this.tweetVSize; v++) {
                this.tau[k][v] = 0;
            }
        }
        this.interestTokenPerTopic = new int[this.topicNumber];
        for (int k = 0; k < this.topicNumber; k++) {
            this.interestTokenPerTopic[k] = trainedIETM.interestTokenPerTopic[k];
        }
        this.tauK = new double[this.topicNumber];
        for (int k = 0; k < this.topicNumber; k++) {
            this.tauK[k] = trainedIETM.tauK[k];
        }
        // init z
        this.z = new Zu[this.timeWindowNum][this.userNumber][];
        this.ndk = new Ndk[this.timeWindowNum][this.userNumber][];

        // random assign tag's topic
        double[] topicWeights = new double[this.topicNumber];
        Arrays.fill(topicWeights, (double) 1 / (double) this.topicNumber);

    }

    /**
     * initialize the model
     */
    public void init(TimeWindowListPlus timeWindows, int topicNum) {
        this.topicNumber = topicNum;// K
        //alpha
        this.alpha = new double[this.topicNumber];
        Arrays.fill(this.alpha,0.1);
        for(int k=0;k<this.topicNumber;k++){
            this.alphaSum=this.alphaSum+this.alpha[k];
        }
        this.timeWindowNum = timeWindows.timeWindowlist.size();// T
        this.userNumber = timeWindows.userAlphabet.size();// U

        this.tweetVSize = timeWindows.tweetAlphabet.size();

        // init iCkv
        this.iCkv = new int[this.topicNumber][];
        for (int k = 0; k < this.topicNumber; k++) {
            this.iCkv[k] = new int[this.tweetVSize];
            Arrays.fill(this.iCkv[k], 0);
        }
        // init tau
        this.tau = new double[this.topicNumber][];
        this.tauK = new double[this.topicNumber];
        for (int k = 0; k < this.topicNumber; k++) {
            this.tau[k] = new double[this.tweetVSize];
            Arrays.fill(this.tau[k], 0);
        }
        Arrays.fill(this.tauK, 0);

        // init interestTokenPerTopic
        this.interestTokenPerTopic = new int[this.topicNumber];
        // init temporal topic related words histogram
        this.oneDocTopicCounts=new int[this.topicNumber];

        // init zu
        this.z = new Zu[this.timeWindowNum][this.userNumber][];
        this.ndk = new Ndk[this.timeWindowNum][this.userNumber][];
    }

    /**
     * gibbs sweep for uer tweets
     */
    public void gibbsSweepII(TimeWindowListPlus timeWindowList) {
        for (int t = 0; t < timeWindowList.size(); t++) {
            SimpleUserInstanceListPlus userList = timeWindowList.get(t);
            for (int user = 0; user < userList.size(); user++) {

                UserInstancePlus userInstance = userList.get(user);
                int u = userInstance.getUserId();
                /**
                 * ------------------------------------------------------------
                 * --- sample for tweets (joint sample for z)
                 */
                double[] topicWeights = new double[this.topicNumber];

                double topicWeightSum = 0.0;

                for (int d = 0; d < userInstance.getTweets().size(); d++) {
                    ArrayList<Integer> tweet = userInstance.getTweets().get(d).tweetContent;
                    int tweetLength = tweet.size();
                    for (int n = 0; n < tweetLength; n++) {
                        int w = tweet.get(n);
                        int oldTopic = this.z[t][u][d].val[n];
                        // reset statistics
                        this.ndk[t][u][d].val[oldTopic]--;
                        this.iCkv[oldTopic][w]--;
                        this.interestTokenPerTopic[oldTopic]--;
                        assert this.iCkv[oldTopic][w] >= 0;

                        // compute sampling distribution
                        topicWeightSum = 0.0;
                        for (int k = 0; k < this.topicNumber; k++) {
                            double numerator1 = this.ndk[t][u][d].val[k]
                            //    + this.alpha[k];
                                +0.01;
                            double numerator2 = this.iCkv[k][w] + this.beta
                                + this.tau[k][w];
                            double denumerator2 = this.interestTokenPerTopic[k]
                                + this.tweetVSize * this.beta
                                + this.tauK[k];
                            topicWeights[k] = numerator1
                                * (numerator2 / denumerator2);
                            topicWeightSum += topicWeights[k];
                        }
                        int sampleResult = rs.nextDiscrete(topicWeights,
                                topicWeightSum);
                        int k = sampleResult;
                        this.z[t][u][d].val[n] = k;
                        this.iCkv[k][w]++;
                        this.interestTokenPerTopic[k]++;
                        this.ndk[t][u][d].val[k]++;
                    }// end of sampling z_{t,u,d}

                }// end of sampling tweet's topic
            }// end of sampling user u's information
        }// end of sampling time window t's information
        LogUtil.logger().error(traceICkv(timeWindowList));
    }

    /**
     * todo: compute perplexity
     * 
     * @param timeWindowList
     * @return
     */
    public double computePerplexity(TimeWindowListPlus timeWindowList) {
        double loglikelihood = 0.0;
        double tokenNum = 0;

        double[][] phi = new double[this.topicNumber][this.tweetVSize];
        for (int k = 0; k < this.topicNumber; k++) {
            for (int v = 0; v < this.tweetVSize; v++) {
                phi[k][v] = (double) (this.iCkv[k][v] + this.beta + this.tau[k][v])
                    / ((double) this.interestTokenPerTopic[k]
                            + this.tweetVSize * this.beta + this.tauK[k]);
            }
        }

        int[] theta_freq = new int[this.topicNumber];
        double[] theta = new double[this.topicNumber];
        for (int t = 0; t < timeWindowList.size(); t++) {
            // generate necessary statistics

            SimpleUserInstanceListPlus userList = timeWindowList.get(t);
            for (int user = 0; user < userList.size(); user++) {

                UserInstancePlus userInstance = userList.get(user);
                int u = userInstance.getUserId();

                for (int d = 0; d < userInstance.getTweets().size(); d++) {
                    ArrayList<Integer> tweet = userInstance.getTweets().get(d).tweetContent;
                    int tweetLength = tweet.size();
                    Arrays.fill(theta_freq, 0);
                    for (int n = 0; n < this.z[t][u][d].val.length; n++) {
                        theta_freq[this.z[t][u][d].val[n]]++;
                    }
                    for (int k = 0; k < this.topicNumber; k++) {
                        theta[k] = ((double) theta_freq[k] + this.alpha[k])
                            / ((double) tweetLength + this.alphaSum);
                    }
                    double likelihood = likelihoodPerTweet(phi, theta, tweet);
                    loglikelihood += Math.log(likelihood);
                    tokenNum += tweet.size();
                }
            }

        }

        // compute perplexity
        return Math.exp(-loglikelihood / tokenNum);
    }

    /**
     * It is called by computePerplexity()
     * 
     * @param pi_u
     * @param theta_u
     * @param phi
     * @param rho
     * @param eta
     * @param phiStopword
     * @param tweet
     * @return
     */
    public double likelihoodPerTweet(double[][] phi, double[] theta,
            ArrayList<Integer> tweet) {
        double likelihood = 1.0;

        int tweetLength = tweet.size();
        for (int n = 0; n < tweetLength; n++) {
            double partn = 0.0;
            int w = tweet.get(n);
            for (int k = 0; k < this.topicNumber; k++) {
                partn += (phi[k][w] * theta[k]);
            }
            likelihood *= partn;
        }
        return likelihood;
    }

    public void output(TimeWindowListPlus timeWindowList) {
        // output
        String interestTopics = PrintUtil.printTopWords(
                DefaultGlobalValueTweets.topKWordsToShow,
                timeWindowList.getTweetAlphabet(), this.iCkv, "Interest");
        for(int k=0;k<this.topicNumber;k++){
            System.out.println("topic"+k+" "+this.interestTokenPerTopic[k]);
        }
        LogUtil.logger().info(interestTopics);
    }

    public void output(TimeWindowListPlus timeWindowList, String filename) {
        MyFile writer = new MyFile(filename, "w");
        String interestTopics = PrintUtil.printTopWords(
                DefaultGlobalValueTweets.topKWordsToShow,
                timeWindowList.getTweetAlphabet(), this.iCkv, "Interest");
        writer.print(interestTopics);
        for(int k=0;k<this.topicNumber;k++){
            System.out.println("topic"+k+" "+this.interestTokenPerTopic[k]);
        }
        writer.close();
    }

    /**
     * get alphabet by wiki.topics
     * 
     * @return
     */
    public static Alphabet buildExtraAlphabet(String[] filenames, Alphabet alphabet) {
        for(int i=0;i<filenames.length;i++){
            String filename=filenames[i];
            MyFile reader = new MyFile(filename, "r");
            String line = reader.readln();
            int lineNum = 1;

        
            while (line != null ) {
                line = line.trim();
                if (!line.equals("")) {
                    String[] array = line.split("\t");
                    if(filename=="wiki.topics"){
                        String word = array[1];
                        alphabet.lookupIndex(word);
                    }
                    if(filename=="background.topics"){
                        String word = array[0];
                        alphabet.lookupIndex(word);
                    }
                    line = reader.readln();
                    lineNum++;
                }
            }
            reader.close();
        }

        return alphabet;
    }

    /**
     * train the model on training files, output:
     * ${portion}-${round}-lda-learned.topics
     */
    public static void trainOnTweets(float wikiWeight, String fLearnedTopics) {
        PreprocessorPlus pre = new PreprocessorPlus();
        TimeWindowListPlus trainingData = pre.getTimeWindowList(
                DefaultGlobalValueTweets.timeWindowFolder,
                DefaultGlobalValueTweets.trainingFiles);

        LDAWithPrior unInitedUserEventLDA = new LDAWithPrior();
        unInitedUserEventLDA.init(trainingData,
                DefaultGlobalValueTweets.topicNum);
        LogUtil.logger().error(unInitedUserEventLDA.traceICkv(trainingData));

        LDAWithPrior userEventLDA = unInitedUserEventLDA.initByWikipedia(
                unInitedUserEventLDA, trainingData, wikiWeight);
        //userEventLDA = unInitedUserEventLDA.initByWikiBackgroundTopics(
        //        userEventLDA, trainingData, wikiWeight);
        boolean isInit=true;
        userEventLDA.updateWordVecs(isInit,trainingData);
        userEventLDA.randomAssignHiddenTopics(trainingData);

        LogUtil.logger().error(userEventLDA.traceICkv(trainingData));

        long start = System.currentTimeMillis();
        for (int i = 0; i < DefaultGlobalValueTweets.trainIterationNum; i++) {
            // userEventLDA.checkIfValid();
            // userEventLDA.output(trainingData);
            userEventLDA.gibbsSweepII(trainingData);
            if(i>=20 && i%8==0){
                userEventLDA.updateHyperParameterStat(trainingData);
                userEventLDA.alphaSum=Dirichlet.learnParameters(userEventLDA.alpha, 
                        userEventLDA.topicDocCounts, userEventLDA.docLengthCounts);
                LogUtil.logger().info("alpha="+Arrays.toString(userEventLDA.alpha));
                userEventLDA.updateAlpha();
            }

            double perplexity = userEventLDA.computePerplexity(trainingData);
            LogUtil.logger()
                .info("after " + i + " round training, perplexity ="
                        + perplexity);
        }

        long end = System.currentTimeMillis();
        LogUtil.logger().info(
                "gibbs sampling cost " + (end - start) + " seconds");
        // serializeModel(userEventLDA, trainingData);
        userEventLDA.output(trainingData, fLearnedTopics);
    }

    public static void serializeModel(LDAWithPrior userEventLDA,
            TimeWindowListPlus trainingData) {
        // output the trained model
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("ietm2.model"));
            oos.writeObject(userEventLDA);
            oos.close();
            LogUtil.logger().info("serialization model done!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("trainedData.alphabet"));
            oos.writeObject(trainingData);
            oos.close();
            LogUtil.logger()
                .info("serialization trained data's alphebet done!");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * test the model on testing files
     */
    public static void testOnTweets() {
        PreprocessorPlus pre = new PreprocessorPlus();
        // read in the model
        LDAWithPrior trainedIETM4 = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(
                        "ietm2.model"));
            trainedIETM4 = (LDAWithPrior) in.readObject();
            // do something here
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // online learning
        TimeWindowListPlus lastProcessedData = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(
                        "trainedData.alphabet"));
            lastProcessedData = (TimeWindowListPlus) in.readObject();
            // do something here
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (int f = 0; f < DefaultGlobalValueTweets.testFiles.length; f++) {
            String file = DefaultGlobalValueTweets.testFiles[f];
            long start = System.currentTimeMillis();
            LogUtil.logger().info(
                    "online learning for file " + file + " start at "
                    + System.currentTimeMillis());
            TimeWindowListPlus testData = pre.getTimeWindowList(
                    DefaultGlobalValueTweets.timeWindowFolder, file,
                    lastProcessedData.getTagAlphabet(),
                    lastProcessedData.getTweetAlphabet(),
                    lastProcessedData.getUserAlphabet());
            LogUtil.logger()
                .info("loading online file costs "
                        + (System.currentTimeMillis() - start) + " seconds");
            // OnlineTimeUserTagLDAIV onlineIV = new OnlineTimeUserTagLDAIV(iv,
            // testData, allUsers);
            LDAWithPrior onlineIETM = new LDAWithPrior(trainedIETM4, testData);
            // onlineIETM.init(testData);
            for (int i = 0; i < DefaultGlobalValueTweets.testInterationNum; i++) {
                // sample for user tweets
                onlineIETM.gibbsSweepII(testData);

                double memoryUsed = MemoryTest.getUsedMemory();
                LogUtil.logger().info(
                        "f=" + file + " costs " + memoryUsed + "KB memory");
                // String windowName = file;
                onlineIETM.output(testData);
            }

            onlineIETM.output(testData);
            double perplexity = onlineIETM.computePerplexity(testData);
            long end = System.currentTimeMillis();
            LogUtil.logger().info(
                    "f=" + file + "[testing] gibbs sampling cost "
                    + (end - start) + " seconds");
            LogUtil.logger().info("perplexity =" + perplexity);

            // finally, update the lastProcessedData
            lastProcessedData = testData;
            trainedIETM4 = onlineIETM;
        }
        serializeModel(trainedIETM4, lastProcessedData);
    }

    public static void main(String[] args) {
        // args = new String[] { "-test" };
        if (args.length < 3) {
            System.out
                .println("usage: [-test] [-train] [-trainAndTest] wikiWeight fLearnedTopics");
            return;
        }
        float wikiWeight = Float.parseFloat(args[1]);
        String fLearnedTopics = args[2];

        if (args[0].equals("-test")) {
            testOnTweets();
        } else if (args[0].equals("-train")) {
            trainOnTweets(wikiWeight, fLearnedTopics);
        } else if (args[0].equals("-trainAndTest")) {
            trainOnTweets(wikiWeight, fLearnedTopics);
            testOnTweets();
        } else {
            System.out.println("usage: [-test] [-train] [-trainAndTest]");
        }
    }
}
