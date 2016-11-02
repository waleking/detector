import matplotlib.pyplot as plt
import pandas
import os


def getSeries(filename,topicName,modelName,df):
    '''
    :param filename: "/Users/huangwaleking/Downloads/baseline_lda/97_npmi.txt"
    :param modelName: "LightLDA" or "TransDetector"
    :param topicName: "46" or "Aviation"
    :param df: the dataframe to be updated
    :return: df
    '''
    idx = 0
    for line in open(filename, "r").readlines()[1:]:
        if line != "\n":
            splitted = line.split("\t")
            npmi = float(splitted[1])
            if(idx<=100):
                df.loc[len(df)]=[topicName, modelName, idx, npmi]
            idx += 1
    return df



def getDataframe():
    # dataframe: topic, model, group, score
    df = pandas.DataFrame(columns=('topic', 'model', 'group', 'npmiscore'))

    #part 1, read in the npmi scores of baseline LDA

    baselineTopicNames=[str(e) for e in [58,86,16,7,46,
                                         71,31,81,38,29,
                                         98,21,5,44,28,
                                         79,97,1,19,22]]
    for bTopicName in baselineTopicNames:
        baselineTopicNPMIfile="/Users/huangwaleking/Downloads/baseline_lda/"+bTopicName+"_npmi.txt"
        df=getSeries(baselineTopicNPMIfile,bTopicName,"Topics learned by LightLDA",df)

    #part 2, read in the npmi scores of topics extracted by TransDetector
    transDetectorTopicNames = ["Film", "Tennis", "Chemistry", "Golf","Aviation",
                               "Medicine","Animals","Mobile","Military","Taxation",
                               "Christians","Harry","Beverages","Foods","Law",
                               "Mathematics","Photography","Middle","Video","Basketball"]
    #transDetectorTopicNames = [e.split("_")[0] for e in os.listdir("/Users/huangwaleking/Downloads/nt/")]
    for transDetectorTopicName in transDetectorTopicNames:
        transDetectorNPMIfile = "/Users/huangwaleking/Downloads/nt/" + transDetectorTopicName + "_npmi.txt"
        df = getSeries(transDetectorNPMIfile, transDetectorTopicName, "Category-Level Topics", df)

    return df


df=getDataframe()
print(df)
df.to_csv("/Users/huangwaleking/git/detector/npmi.csv",header=True,index=True)
