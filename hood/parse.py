import json
import pandas as pd
import matplotlib.pyplot as plt
from datetime import datetime


if __name__ == '__main__':
    hood={}
    for line in open("hood.json","r"):
        line=line.strip()
        hood=json.loads(line)

    dDayNum={}#{"2011-07-20":23,...} for all topics
    dDayNum34={} # for topic 34 (military)
    for ele in hood:
        topic=ele['topic']
        timewindow=ele['timewindow']
        num=ele['num']
        if(topic=='34'):
            dDayNum34[timewindow]=int(num)

        if(timewindow not in dDayNum):
            dDayNum[timewindow]=int(num)
        else:
            dDayNum[timewindow]=dDayNum[timewindow]+int(num)

    parsedDayNum34=pd.Series(dDayNum34)
    parsedDayNum34.index = pd.DatetimeIndex(parsedDayNum34.index)
    parsedDayNum=pd.Series(dDayNum)
    parsedDayNum.index = pd.DatetimeIndex(parsedDayNum.index)
    print(type(parsedDayNum))
    timepoints = sorted(parsedDayNum.keys())
    minTime=timepoints[0]
    maxTime=timepoints[-1]
    startTime=datetime.strptime("2011-07-18","%Y-%m-%d")
    endTime=datetime.strptime("2011-07-31","%Y-%m-%d")
    # get the time span for the time series to draw
    #toDraw = pd.date_range(minTime, maxTime, freq='D')
    toDraw = pd.date_range(startTime, endTime, freq='D')
    parsedDayNum= parsedDayNum.reindex(toDraw, fill_value=0)  # set missing
    parsedDayNum34= parsedDayNum34.reindex(toDraw, fill_value=0)  # set missing

    parsedDayNum.loc[parsedDayNum.index=="2011-07-28"]=86
    print(parsedDayNum)

    #combine two time series, refer to http://pandas.pydata.org/pandas-docs/stable/merging.html
    hoodSeries=pd.concat([parsedDayNum,parsedDayNum34],axis=1)
    #rename the columns
    hoodSeries.columns = ['hood', 'hood (military)']


    fig=plt.figure(figsize=(20,2),dpi=600)
    hoodSeries.plot(style="--")
    plt.legend()
    plt.savefig("hood.pdf")
