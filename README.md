## preparation

```
% ./gradlew shadowJar
% ./prepare-wikipedia-data.sh
% java -cp bench/build/libs/bench-1.0-SNAPSHOT-all.jar com.mayreh.bench.GenerateData data/uuid.dat 10000000
```

## result

### environment
- MacBook Air (M2, 2022), Memory 24GB, macOS Monterey
- OpenJDK Runtime Environment Temurin-11.0.15+10 (build 11.0.15+10)

### hash benchmark

```
% java -cp bench/build/libs/bench-1.0-SNAPSHOT-all.jar com.mayreh.bench.HashBenchmark data/uuid.dat
...
Benchmark                     (path)   Mode  Cnt          Score         Error  Units
HashBenchmark.murmur3  data/uuid.dat  thrpt    5  112307073.290 ± 1272439.598  ops/s
HashBenchmark.wy3      data/uuid.dat  thrpt    5  123382813.229 ± 1965823.606  ops/s
HashBenchmark.xx3      data/uuid.dat  thrpt    5  130807110.431 ± 1057020.236  ops/s
```

xx3 is the fastest slightly.

### counter benchmark

```
% java -cp bench/build/libs/bench-1.0-SNAPSHOT-all.jar com.mayreh.bench.CounterBenchmark data/uuid.dat 0.00001 0.00001
...
Benchmark                                    (cmDelta)  (cmEpsilon)         (path)   Mode  Cnt        Score        Error  Units
CounterBenchmark.conservativeUpdateCountMin    0.00001      0.00001  data/uuid.dat  thrpt    5  3653331.228 ±  79397.245  ops/s
CounterBenchmark.countMin                      0.00001      0.00001  data/uuid.dat  thrpt    5  3865337.479 ±  69198.954  ops/s
CounterBenchmark.exact                         0.00001      0.00001  data/uuid.dat  thrpt    5  2958263.113 ± 261876.781  ops/s
```

With `ε = 0.00001, δ = 0.00001`, faster than HashMap

### counter accuracy

```
% java -cp bench-accuracy/build/libs/bench-accuracy-1.0-SNAPSHOT-all.jar com.mayreh.bench.accuracy.AccuracyBenchmark data/simplewiki.xml 0.00001 0.00001                                       master
 Total item count : 112327701
Unique item count : 11757391
   CountMin    Nε : 1123.277010
   CountMin   1-δ : 0.999990
   CountMin depth : 12
   CountMin width : 524288
================================
   frequency | num keys  | max error | min error | rmse
<=    413262 |  11757368 |        70 |         0 |22.306859
<=    826524 |        14 |        26 |        14 |19.789969
<=   1239786 |         3 |        27 |        25 |25.683977
<=   1653048 |         2 |        26 |        13 |20.554805
<=   2066310 |         1 |        31 |        31 |31.000000
<=   2479572 |         1 |        15 |        15 |15.000000
<=   2892834 |         1 |        22 |        22 |22.000000
<=   4132628 |         1 |        18 |        18 |18.000000
================================
   Keys with error > Nε : 0
           Conform rate : 1.000000
```

- conservative update:

```
% java -cp bench-accuracy/build/libs/bench-accuracy-1.0-SNAPSHOT-all.jar com.mayreh.bench.accuracy.AccuracyBenchmark data/simplewiki.xml 0.00001 0.00001 --conservative                        master
 Total item count : 112327701
Unique item count : 11757391
   CountMin    Nε : 1123.277010
   CountMin   1-δ : 0.999990
   CountMin depth : 12
   CountMin width : 524288
================================
   frequency | num keys  | max error | min error | rmse
<=    413262 |  11757368 |        30 |         0 |9.681790
<=    826524 |        14 |         0 |         0 |0.000000
<=   1239786 |         3 |         0 |         0 |0.000000
<=   1653048 |         2 |         0 |         0 |0.000000
<=   2066310 |         1 |         0 |         0 |0.000000
<=   2479572 |         1 |         0 |         0 |0.000000
<=   2892834 |         1 |         0 |         0 |0.000000
<=   4132628 |         1 |         0 |         0 |0.000000
================================
   Keys with error > Nε : 0
           Conform rate : 1.000000
```
