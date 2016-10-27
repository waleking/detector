#encoding=utf8
'''generate label dictionary class-label.dict, label-class.dict, 
20newsgroup.all.txt
20newsgroup.train
20newsgroup.test
in classifier/data
'''
import os
import os.path
import json
from sklearn.datasets.twenty_newsgroups import *
import random

sourceFolder="20news-18828"
globaldClassLabel=dict()
globaldLabelClass=dict()

def writeDict(filename,d):
    f=open(filename,"w")
    f.write(json.dumps(d))
    f.close()


def getClassAndLabels():
    files=os.listdir(sourceFolder)
    files=[f for f in files if os.path.isfile(f)==False]#filter out non-folder file
    idx=0
    for f in files:
        globaldClassLabel[f]=idx
        globaldLabelClass[idx]=f
        idx=idx+1
    writeDict("classifier/data/class-label.dict",globaldClassLabel)
    writeDict("classifier/data/label-class.dict",globaldLabelClass)


def compressFileIntoALine(filename,label,fileId):
    '''compress the 20newsgroup's article into a single line
    '''
    f=open(filename,"r")
    data=[]
    for line in f.readlines():
        data.extend(line)
    content="".join(data) 
    s=strip_newsgroup_header(content)
    #s=strip_newsgroup_footer(s)
    s=strip_newsgroup_quoting(s)
    s="\n".join(s.strip().split("\n")[:-1])

    line=(" ".join([line.strip() for line in s.split("\n")]))
    result="__label__"+str(label)+" , "+fileId+" , "+line
    return result


def portal():
    getClassAndLabels() 
    fWriter=open("classifier/data/20newsgroup.all.txt","w")
    fTrain=open("classifier/data/20newsgroup.train","w")
    fTest=open("classifier/data/20newsgroup.test","w")
    #line=compressFileIntoALine("20news-18828/alt.atheism/52909","alt.atheism","52909")
    #fWriter.write(line+"\n")
    #fWriter.close()
    #return
    for classname in globaldClassLabel.keys():
        files=[sourceFolder+"/"+classname+"/"+f for f in os.listdir(sourceFolder+"/"+classname)]
        for f in files:
            print(f)
            fileId=f.split("/")[-1]
            label=globaldClassLabel[classname]
            line=compressFileIntoALine(f,label,fileId)
            #if(fileId in ["49960","52033","51060","52499"]):
            #    continue
            fWriter.write(line+"\n")
            sample=random.randint(0,9)
            if(sample>8):
                fTest.write(line+"\n")
            else:
                fTrain.write(line+"\n")
            
    fWriter.close()
    fTrain.close()
    fTest.close()



if __name__=="__main__":
    portal()
