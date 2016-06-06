#generate wiki.topics from ~/git/wiki_processor/data/features/
import os
from operator import itemgetter

def loadFeatures(filename):
    d=dict()
    sum=0
    for line in open(filename,"r"):
        word,chi2=line.strip().split("\t")
        chi2=float(chi2)
        sum=sum+chi2
        d[word]=chi2

    for word in d:
        d[word]=d[word]/sum
    return d



featureFileFolder="../wiki_processor/data/features"
#todo, replace it by ../wiki_processor/additional_scripts/topicnames.txt
files=os.listdir(featureFileFolder)
wikiTopicsFile=open("wiki.topics","w")
idx=0
for f in files:
    print(f)
    d=loadFeatures(featureFileFolder+"/"+f)
    sortedWords=sorted(d.items(),key=itemgetter(1),reverse=True)
    for t in sortedWords:
        word,score=t
        wikiTopicsFile.write("topic%s\t%s\t%s\n" % (idx,word,score))
    idx=idx+1
wikiTopicsFile.close()
