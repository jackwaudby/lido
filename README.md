# Simulating SWIM 

## False positive rate 

Assumptions:
* No failures 
* Static membership group

## Time to detect

Pick a random member to failure then measure time until the first member detects it as failed.

```
mvn clean install

java -cp target/swim-sim-1.0-SNAPSHOT.jar Main --members 3 --messageRate 200 --subset 1 --period 50 --timeout 10 --seed false --seedValue 0 --duration 10
```

