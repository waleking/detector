import pandas

#old dataframe
df=pandas.read_csv("/Users/huangwaleking/git/detector/TransDetecotrResult_CSV.csv")

methodColIdx=df.columns.tolist().index("LSH") #the pivot to look for columns that before methods

columnsName=df.columns.tolist()#the column names
methodsName=columnsName[methodColIdx:]#LSH, TimeUserLDA, ...

columnsNum=len(df.columns)
rowsNum=len(df)

#new dataframe
df2=pandas.DataFrame(columns=('eventName', 'date', '#tweets', 'group','detected','method'))
for row in range(0,rowsNum):
    for methodName in methodsName:
        r=df.ix[row, 0:methodColIdx].tolist()
        r.append(df.ix[row][methodName]) #be careful
        r.append(methodName)  # be careful
        df2.loc[len(df2)]=r

print(df2)
df2.to_csv("/Users/huangwaleking/git/detector/OnBenchmark1.csv",header=True)
#save csv

