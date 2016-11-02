library("ggplot2")

df<-read.csv(file="/Users/huangwaleking/git/detector/npmi.csv",header=TRUE)
npmis<-df %>% filter(group<=50)

npmis$model<-factor(npmis$model,levels=c("Category-Level Topics","Topics learned by LightLDA"))
npmis$group<-factor(npmis$group)

p <- ggplot(data = npmis, aes(x=group, y=npmiscore)) + 
  geom_boxplot(aes(fill = model))
  #geom_jitter(aes(colour = model))

print(p)