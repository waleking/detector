# detector
## Dataset
We use reddit and twitter, wikipedia.

There are some settings of the KB-prior-LDA model.
(1) There are 100 topics, including 47 carefully refined topics. *TODO* We also should include ice hockey topic, boxing, baby sitting topic, and astrology topic etc. 
(2) We do 200 iterations, in which we do prior optimization every 8 iterations when after 20 burin iterations.
(3) We set KB prior weights to 6.4.
(4) We set the topic weights for words initially as tau (words vector defined by refined topics) when other oov words as [0, .., 0, 1/(100-47),.., 1/(100-47)] 

*TODO* we should check why perplexity varies when applying on 2011-07-02.json (perplexity=2050) and 2011-07-23.json (perplexity=1750).
