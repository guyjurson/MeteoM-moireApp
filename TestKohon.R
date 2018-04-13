library(kohonen)
Mars <- read.table("C:/Users/Guy Ju/Documents/Fac/M1/MémoireApp/marseille_1416.data",sep=",")
Nanc <- read.table("C:/Users/Guy Ju/Documents/Fac/M1/MémoireApp/nancy_1416.data",sep=",")
Inco <- read.table("C:/Users/Guy Ju/Documents/Fac/M1/MémoireApp/inconnu_16.data",sep=",")
Mars <- subset(Mars, V4>=0)
Nanc <- subset(Nanc, V4>=0)
Inco <- subset(Inco, V4>=0)
length(Mars$V1)
length(Nanc$V1)
length(Inco$V1)
D <- rbind(Nanc, Mars)
D <- rbind(D, Inco)
length(D$V1)
E <- data.matrix(D)
Z <- scale(E, center=T, scale=T)
set.seed(100)

carte <- som(Z)

carte <- som(Z, grid = somgrid(3, 487, "rectangular"))

nb <- table(carte$unit.classif)
table(carte$unit.classif)

dc <- dist(carte$code[[1]])
#Construit un arbre à partir des observations
cah <- hclust(dc)
#Découpe l'arbre en k partie
groupes <- cutree(cah,k=10)
print(groupes)
