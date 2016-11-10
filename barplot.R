library("ggplot2")
library("dplyr")
library("gridExtra")

df<-read.csv(file="/Users/huangwaleking/git/detector/OnBenchmark1.csv",header=TRUE)

df2 <- df %>% group_by(group,method) %>% 
            summarise(n_detect=sum(detected),n_total=n(),recall=n_detect/n_total)

#order the method
df2$group<-factor(df2$group,levels=c(">200","(100,200]","(50,100]","[0,50]"))
df2$method<-factor(df2$method,levels=c("TimeUserLDA","BurstyBTM","EDCoW","LSH","Twevent","TransDetector"))

p <- ggplot(df2, aes(fill=method, y=recall, x=group))+
      geom_bar(position="dodge",stat = "identity") +
      #scale_fill_manual(values=c("#E5E5E5", "#F1EEF6","#BDC9E1","#74A9CF","#2B8CBE","#045A8D"))+ #dark
      scale_fill_manual(values=c("#EE9336", "#EED757","#D4EE3D","#B9EE91","#79DCD1","#4CB3EE"))+
      theme_bw()+
      labs(y="Recall@Benchmark1",x="Group with different event size (number of tweets)",size=9)+ #set y and x labels
      theme(panel.grid.major.x = element_blank(), panel.grid.minor = element_blank(), legend.title=element_blank())

#print(p)  
pdf("barchartOnBenchmark1.pdf",height=2, width=8)
grid.arrange(p, nrow=1)
dev.off()