#generate background.topics from ~/git/wiki_processor/data/features/other_features.txt
import os
from operator import itemgetter

def loadBackgroundFeatures(filename):
    d=dict()
    sum=0
    for line in open(filename,"r"):
        if("\t" in line):
            word,chi2=line.strip().split("\t")
        else:
            word,chi2=line.strip().split(" ")

        chi2=float(chi2)
        sum=sum+chi2
        d[word]=chi2

    for word in d:
        d[word]=d[word]/sum
    return d



backgroundTopicsFile=open("background.topics","w")
d=loadBackgroundFeatures("../wiki_processor/data/features/other_features.txt")
sortedWords=sorted(d.items(),key=itemgetter(1),reverse=True)
for t in sortedWords:
    word,score=t
    backgroundTopicsFile.write("%s\t%s\n" % (word,score))
backgroundTopicsFile.close()
