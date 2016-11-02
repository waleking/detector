library("dplyr")
library("ggplot2")
library("splines")
library("gridExtra")

npmis<-read.csv(file="/Users/huangwaleking/git/detector/npmi.csv",header=TRUE)

scores<-npmis %>% filter(group<=50)

scores$model<-factor(scores$model,levels=c("Category-Level Topics","Topics learned by LightLDA"))

p<-ggplot(data=scores,aes(x=group, y=npmiscore,fill=model,color=model)) +
  geom_point(alpha = 0.7,size=1,aes(shape = model)) +
  stat_smooth(method = "lm", alpha=0.5,formula= y ~ ns(x,49))+ 
  labs(y="NPMI",x="Group ID",size=9)+ #set y and x labels
  coord_cartesian(xlim=c(2, 48))+ #hardcode, don't why these values, but they work
  theme_bw()+ #remove the grid
  theme(legend.position=c(0.73,0.59), legend.justification=c(0,0),legend.text=element_text(size=8))+
  theme(panel.grid.major = element_blank(), panel.grid.minor = element_blank(), legend.title=element_blank())
  


#print(p)
pdf("npmi.pdf",height=2.5, width=8)
grid.arrange(p, nrow=1)
dev.off()