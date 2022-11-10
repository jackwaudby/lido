#!/bin/bash

for p in 6 8 10
do
  for k in 1 2 3 4 5 6 7 8
  do
    java -cp target/swim-sim-1.0-SNAPSHOT.jar Main --members 10 --timeout 2 --period $p --messageRate 1000 --subset $k --duration "$1"
  done
done  
