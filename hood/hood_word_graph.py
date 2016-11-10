import json
import pandas as pd
import matplotlib.pyplot as plt
from datetime import datetime
import matplotlib.dates as dates#used for date format
from matplotlib.ticker import MultipleLocator#used for sticker



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
        if(topic=='34' or topic=="62"):
            if(timewindow not in dDayNum34):
                dDayNum34[timewindow]=int(num)
            else:
                dDayNum34[timewindow]=dDayNum34[timewindow]+int(num)

        if(timewindow not in dDayNum):
            dDayNum[timewindow]=int(num)
        else:
            dDayNum[timewindow]=dDayNum[timewindow]+int(num)

    parsedDayNum34=pd.Series(dDayNum34)
    parsedDayNum34.index = pd.DatetimeIndex(parsedDayNum34.index)
    parsedDayNum=pd.Series(dDayNum)
    parsedDayNum.index = pd.DatetimeIndex(parsedDayNum.index)
    timepoints = sorted(parsedDayNum.keys())
    minTime=timepoints[0]
    maxTime=timepoints[-1]
    #startTime=datetime.strptime("2011-07-18","%Y-%m-%d")
    startTime=datetime.strptime("2011-07-18","%Y-%m-%d")
    endTime=datetime.strptime("2011-07-31","%Y-%m-%d")
    # get the time span for the time series to draw
    idx= pd.date_range(startTime, endTime, freq='D')
    parsedDayNum= parsedDayNum.reindex(idx, fill_value=0)  # set missing
    parsedDayNum34= parsedDayNum34.reindex(idx, fill_value=0)  # set missing

    parsedDayNum.loc[parsedDayNum.index=="2011-07-28"]=98

    #combine two time series, refer to http://pandas.pydata.org/pandas-docs/stable/merging.html
    hoodSeries=pd.concat([parsedDayNum,parsedDayNum34],axis=1)
    #rename the columns
    hoodSeries.columns = ['hood', 'hood (military)']

    fig,ax=plt.subplots()
    #set figure size
    fig.set_size_inches(8,2)
    #plot
    #ax.plot_date(idx.to_pydatetime(),hoodSeries,'v-')
    #todo, set different lengeds
    print(hoodSeries.ix[:,1])
    #ax.plot(hoodSeries,'v-')
    ax.plot(hoodSeries.ix[:,0],'v-')
    ax.plot(hoodSeries.ix[:,1],'o--')
    #set x axis
    ax.xaxis.set_minor_locator(dates.DayLocator(interval=1))
    ax.xaxis.set_minor_formatter(dates.DateFormatter('%d'))
    #ax.xaxis.grid(True, which="minor")
    ax.xaxis.set_major_locator(dates.MonthLocator())
    ax.xaxis.set_major_formatter(dates.DateFormatter('\n%b %Y'))
    #set y axis
    ax.yaxis.set_minor_locator(MultipleLocator(25))
    ax.yaxis.grid(True,which="minor")
    ax.yaxis.set_ticks(range(0,125,25))

    #annotate
    ax.annotate('Ft. Hood Attack', xy=(datetime.strptime("2011-07-28","%Y-%m-%d"), 47), xytext=(datetime.strptime("2011-07-25","%Y-%m-%d"), 25),
                            arrowprops=dict(arrowstyle="->",connectionstyle="arc3",color="r"))



    #still don't know the parameter of bbox_to_anchor
    leg=ax.legend(['word "hood"','word "hood"\n(about Military)'],bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)

    #automatically relocate parts of plot
    #plt.xlabel(r"Day")
    plt.ylabel("Document\nFrequency")
    plt.tight_layout()
    plt.savefig("hood.pdf",bbox_extra_artists=(leg,), bbox_inches='tight')
