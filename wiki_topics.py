#generate wiki.topics from ~/git/wiki_processor/data/features/
import os

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
files=os.listdir(featureFileFolder)
wikiTopicsFile=open("wiki.topics","w")
idx=0
for f in files:
    print(f)
    d=loadFeatures(featureFileFolder+"/"+f)
    for word in d:
        wikiTopicsFile.write("topic%s\t%s\t%s\n" % (idx,word,d[word]))
    idx=idx+1
wikiTopicsFile.close()
