#!/bin/bash
# run an example for PK-LDA  
set -e

#generate wiki.topics from ~/git/wiki_processor/data/features/
echo "Generate wiki.topics."
python wiki_topics.py 

#compile
cd $PWD
lib="lib"
src="src"
classes="classes"
#mkdir ${classes}
IETMLibPath=${lib}/org.json.jar:${lib}/huangweijing-tool.jar:${lib}/mallet.jar:${lib}/mallet.jar:${lib}/mallet-deps.jar:${lib}/commons-math3-3.1.1.jar:${lib}/log4j-1.2.15.jar
javac -classpath ${classes}:$IETMLibPath -sourcepath ${src} -d ${classes} ${src}/ietm/LDAWithPrior.java


vocabulary_size=`python vocabulary_size.py`
echo "vocabulary size is $vocabulary_size"

#vocabulary_size=272472
#wikiPortions="0 0.1 0.2 0.4 0.8 1.6 3.2 6.4 12.8 25.6 51.2"
wikiPortions="6.4"
roundArray="0"
for portion in ${wikiPortions}
do 
	for round in ${roundArray}
	do
		wikiWeight=`echo "scale=0;$portion*$vocabulary_size*0.01"|bc`
		echo "start to train LDA with wikiWeight=$wikiWeight"
	
		#output: ${portion}-${round}-lda-learned.topics
		fileTopics=${portion}-${round}-lda-learned.topics
		echo "output learned topics: ${fileTopics}"
		java -classpath ${classes}:$IETMLibPath ietm/LDAWithPrior -train $wikiWeight $fileTopics
	done #end round
done # end portion

#compute pmi
#input: ${portion}-lda-learned.topics, output: lda${portion}-wiki.pmi, lda${portion}-sasa.pmi
#python computePMIRedis.py lda 

#draw pmi score curve
#input: *.pmi output: pmi score (todo)
#python drawPMIGraph.py
