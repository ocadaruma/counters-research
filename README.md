## preparation

```
% ./gradlew shadowJar
% ./prepare-wikipedia-data.sh
% java -cp bench/build/libs/bench-1.0-SNAPSHOT-all.jar com.mayreh.bench.GenerateData data/uuid.dat 10000000
```

## resul

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
% java -cp bench-accuracy/build/libs/bench-accuracy-1.0-SNAPSHOT-all.jar com.mayreh.bench.accuracy.AccuracyBenchmark data/simplewiki.xml 0.00005 0.00001
 Total item count : 112327701
Unique item count : 11757391
   CountMin    Nε : 5616.385050
   CountMin   1-δ : 0.999990
   CountMin depth : 12
   CountMin width : 65536
================================
   frequency | num keys  | max error | min error | rmse
<=    413262 |  11757368 |       791 |       169 |318.188000
<=    826524 |        14 |       492 |       258 |331.670447
<=   1239786 |         3 |       370 |       299 |345.830884
<=   1653048 |         2 |       315 |       267 |291.988013
<=   2066310 |         1 |       325 |       325 |325.000000
<=   2479572 |         1 |       256 |       256 |256.000000
<=   2892834 |         1 |       367 |       367 |367.000000
<=   4132628 |         1 |       329 |       329 |329.000000
================================
   Keys with error > Nε : 0
           Conform rate : 1.000000
```

- conservative update:

```
% java -cp bench-accuracy/build/libs/bench-accuracy-1.0-SNAPSHOT-all.jar com.mayreh.bench.accuracy.AccuracyBenchmark data/simplewiki.xml 0.00005 0.00001 --conservative
 Total item count : 112327701
Unique item count : 11757391
   CountMin    Nε : 5616.385050
   CountMin   1-δ : 0.999990
   CountMin depth : 12
   CountMin width : 65536
================================
   frequency | num keys  | max error | min error | rmse
<=    413262 |  11757368 |       338 |         0 |144.003644
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
