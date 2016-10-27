./fasttext supervised -input "data/20newsgroup.train" -output "result_fastText/20newsgroup" -dim 10 -lr 0.1 -wordNgrams 2 -minCount 1 -bucket 10000000 -epoch 320 -thread 4
./fasttext test result_fastText/20newsgroup.bin data/20newsgroup.test
./fasttext predict result_fastText/20newsgroup.bin data/20newsgroup.test > result_fastText/20newsgroup.test.predict

awk {'print $1'} result_fastText/20newsgroup.test.predict | sort | uniq -c
