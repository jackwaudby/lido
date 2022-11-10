library(ggplot2)
library(readr)
library(patchwork)

dat_file = "../results.csv"
df = read_csv(file = dat_file)
df$period = as.factor(df$period)

(g = ggplot(data = df, aes(x = subset, y = fpr, group=period, colour=period)) +
    geom_line() +
    theme_bw() + 
    theme(legend.position="top",text = element_text(size = 18)) + 
  labs(title=paste0("members=",df$members,
                    ", t=",df$timeout,"ms",
                    ", avMessageDelay=",1/df$messageRate,"ms"), 
       x = "subset (k)",
       y = "false postive rate (%)",
       color="T' (ms):")) 

                                                                 
ggsave(paste0("example.pdf"), g, width = 6, height = 4,device = "pdf")

