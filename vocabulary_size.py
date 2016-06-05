#!/usr/bin/env python
# coding=utf-8

# 2015 Feb. 3rd

import ConfigParser
import json

def get():
    '''get the set of words 
    '''
    s = set()
    
    config = ConfigParser.ConfigParser()
    config.read(['config.ini'])
    dataFolder = config.get("input", "timeWindowFolder")
    trainingFiles = config.get("input", "trainingFiles").split(",")
    for filename in trainingFiles:
        f = open(dataFolder + "/" + filename, 'r')
        for line in f:
            user = json.loads(line.strip())
            tweets = user['messages']
            for tweet in tweets:
                for word in tweet['messages']:
                    s.add(word)
        f.close()
        
    return s

    
if __name__ == '__main__':
    s = get()
    print(len(s))