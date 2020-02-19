/**
 * LongShortStoNTest.java
 *
 * @author Created by Omnicore CodeGuide
 */

package edu.sc.seis.sod.bag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.io.DataInputStream;
import java.io.EOFException;
import java.time.Duration;
import java.util.LinkedList;

import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedRecord;
import edu.sc.seis.sod.mock.seismogram.MockSeismogram;
import edu.sc.seis.sod.model.common.FissuresException;
import edu.sc.seis.sod.model.common.SamplingImpl;
import edu.sc.seis.sod.model.seismogram.LocalSeismogramImpl;
import edu.sc.seis.sod.util.convert.mseed.FissuresConvert;

public class LongShortStoNTest  {

    @Test
    public void teXstSimple() throws FissuresException {
        LongShortStoN ston = new LongShortStoN(Duration.ofSeconds(4),
                                               Duration.ofSeconds(1),
                                               2);
        int[] datadata = { 1, 2, 1, 2, 1, 1, 19, -6, 6, -2, 1, 3, 5, -3, -1, 1 };
        int[] data = new int[1000];
        System.arraycopy(datadata, 0, data, 800, datadata.length);
        LocalSeismogramImpl seis = MockSeismogram.createTestData("est", data);
        seis.sampling_info = new SamplingImpl(1, TimeUtils.ONE_SECOND);
        LongShortTrigger[] triggers = ston.calcTriggers(seis);
        //  System.out.println("Found "+triggers.length+" triggers");
        //  for (int i = 0; i < triggers.length; i++) {
        //      System.out.println(triggers[i].getIndex()+"  "+triggers[i].getWhen()+"  "+triggers[i].getValue());
        //  }
    }

    @Test
    public void tesXtConstant() throws Exception {
        LongShortStoN ston = new LongShortStoN(Duration.ofSeconds(100),
                                               Duration.ofSeconds(6),
                                               .2f);
        int[] data = new int[1200];
        for (int i = 0; i < data.length; i++) {
            if (i%2000 > 1000 && i%2000<1010) {
                data[i] = 5*(i%2);
            } else {
                data[i] = i%2;
            }
        }
        LocalSeismogramImpl seis = MockSeismogram.createTestData("est", data);
        seis.sampling_info = SamplingImpl.ofSamplesSeconds(1, 0.1);
        LongShortTrigger[] triggers = ston.calcTriggers(seis);
        //System.out.println("Found "+triggers.length+" triggers");
        for (int i = 0; i < triggers.length; i++) {
        //    System.out.println(triggers[i].getIndex()+"  "+triggers[i].getWhen()+"  "+triggers[i].getValue());
        }
    }

    @Test
    public void testVsRefTrigMseed() throws Exception {
        DataInputStream in =
            new DataInputStream(this.getClass().getClassLoader().getResourceAsStream("edu/sc/seis/sod/bag/03.141.18.40.32.0551.6.m"));
        LinkedList<DataRecord> drList = new LinkedList<DataRecord>();
        try {
            while (true) {
                SeedRecord sr = SeedRecord.read(in);
                if (sr instanceof DataRecord) {
                    drList.add((DataRecord)sr);
                }
            }
        } catch (EOFException e) {
        }
        DataRecord[] dr = new DataRecord[drList.size()];
        dr = (DataRecord[])drList.toArray(dr);
        LocalSeismogramImpl seis = FissuresConvert.toFissures(dr);

        LongShortStoN ston = new LongShortStoN(Duration.ofSeconds(100),
                                               Duration.ofSeconds(6),
                                               7f);
        LongShortTrigger[] triggers = ston.calcTriggers(seis);
        //System.out.println("Found "+triggers.length+" triggers: cols are index, when, lta, sta, ratio");
        for (int i = 0; i < triggers.length; i++) {
        //    System.out.println(triggers[i].getIndex()+"  "+triggers[i].getWhen()+"  "+triggers[i].getLTA()+"  "+triggers[i].getSTA()+"  "+triggers[i].getValue());
        }
        assertTrue(true); // do we really need an assert???
    }


    /*
     pooh 4>reftrig -l 100 -s 6 -i 03.141.18.40.32.0551.6.m
     reftrig:  Version Number  97.329
     03.141.18.40.32.0551.6.m        2003:141:18:49:21.412   200     8854
     03.141.18.40.32.0551.6.m        2003:141:18:49:43.562   1478    10211

     pooh 8>reftrig -t 7 -l 100 -s 6 -i 03.141.18.40.32.0551.6.m
     reftrig:  Version Number  97.329
     03.141.18.40.32.0551.6.m        2003:141:18:49:22.662   277     2791
     03.141.18.40.32.0551.6.m        2003:141:18:49:28.412   498     3912
     03.141.18.40.32.0551.6.m        2003:141:18:49:31.512   671     5221
     03.141.18.40.32.0551.6.m        2003:141:18:49:34.062   871     8854

    pooh 10>/seis/raid1/local/External/passcal1.9/src/reftrig/sun4/reftrig -t 7 -l 100 -s 6 -i 03.141.18.40.32.0551.6.m
/seis/raid1/local/External/passcal1.9/src/reftrig/sun4/reftrig:  Version Number  97.329
10606   277.077148      1941.475708     7.006986
10607   280.794861      1989.200684     7.084177
10608   284.584717      2037.755127     7.160452
10609   288.447601      2087.147461     7.235794
10610   292.378937      2137.295166     7.310018
10611   296.367615      2188.009277     7.382754
10612   300.398560      2239.036377     7.453552
10613   304.448761      2289.993408     7.521770
10614   308.487701      2340.376709     7.586613
10615   312.480469      2389.613525     7.647241
10616   316.390686      2437.111084     7.702853
10617   320.187012      2482.365234     7.752861
10618   323.849152      2525.057617     7.797018
10619   327.360321      2564.930908     7.835192
10620   330.708282      2601.804443     7.867370
10621   333.886261      2635.590088     7.893676
10622   336.887543      2666.200439     7.914215
10623   339.706879      2693.573975     7.929112
10624   342.342560      2717.707275     7.938561
10625   344.790863      2738.564209     7.942682
10626   347.046082      2756.075684     7.941526
10627   349.104004      2770.198242     7.935166
10628   350.960419      2780.888916     7.923654
10629   352.611145      2788.104980     7.907024
10630   354.052979      2791.820801     7.885319
10631   355.281250      2791.986084     7.858523
10632   356.291290      2788.551270     7.826605
10633   357.082428      2781.532959     7.789610
10634   357.661987      2771.080322     7.747763
10635   358.038239      2757.356934     7.701292
10636   358.229004      2740.682373     7.650643
10637   358.257996      2721.472168     7.596403
10638   358.145416      2700.080078     7.539061
10639   358.022369      2678.690674     7.481909
10640   358.008728      2659.285889     7.427992
10641   358.092804      2641.657715     7.377020
10642   358.263489      2625.608154     7.328707
10643   358.513672      2611.007080     7.282866
10644   358.836243      2597.725586     7.239306
10645   359.227570      2585.693848     7.197927
10646   359.685089      2574.859131     7.158648
10647   360.209656      2565.227051     7.121483
10648   360.799713      2556.761719     7.086374
10649   361.447662      2549.328369     7.053105
10650   362.146912      2542.810303     7.021488
10721   498.227417      3503.517334     7.031964
10722   503.163544      3560.310059     7.075850
10723   508.018066      3615.324463     7.116528
10724   512.739197      3667.718018     7.153184
10725   517.277161      3716.688965     7.185102
10726   521.589294      3761.558350     7.211725
10727   525.631042      3801.620850     7.232489
10728   529.355347      3836.136230     7.246808
10729   532.727234      3864.569336     7.254312
10730   535.720825      3886.538574     7.254784
10731   538.319641      3901.822998     7.248153
10732   540.521790      3910.442871     7.234571
10733   542.331848      3912.525879     7.214265
10734   543.761902      3908.322510     7.187562
10735   544.829529      3898.171631     7.154847
10736   545.555725      3882.466797     7.116536
10737   545.972412      3861.779785     7.073214
10738   546.116516      3836.759521     7.025533
10783   671.744019      4710.138672     7.011806
10784   677.860596      4777.914551     7.048521
10785   683.850891      4843.092773     7.082089
10786   689.674683      4905.027832     7.112089
10787   695.295227      4963.138184     7.138174
10788   700.676392      5016.856445     7.160019
10789   705.774597      5065.495605     7.177215
10790   710.545349      5108.359375     7.189350
10791   714.946167      5144.791016     7.196054
10792   718.942200      5174.263672     7.197051
10793   722.515015      5196.527344     7.192276
10794   725.660278      5211.566406     7.181827
10795   728.390625      5219.645508     7.165998
10796   730.734619      5221.291504     7.145264
10797   732.731323      5217.199219     7.120208
10798   734.437622      5208.354980     7.091623
10799   735.920898      5195.909180     7.060418
10800   737.249817      5181.026367     7.027504
10834   871.601807      6123.992676     7.026136
10835   879.487732      6210.995605     7.062060
10836   887.513672      6299.660156     7.098099
10837   895.654480      6389.557129     7.133953
10838   903.893433      6480.402344     7.169432
10839   912.213928      6571.913574     7.204356
10840   920.591370      6663.679199     7.238477
10841   928.993225      6755.159180     7.271484
10842   937.372559      6845.579590     7.302944
10843   945.676941      6934.082520     7.332401
10844   953.855042      7019.834473     7.359435
10845   961.855652      7102.009766     7.383655
10846   969.634583      7179.906250     7.404755
10847   977.162720      7253.075195     7.422587
10848   984.435364      7321.477539     7.437235
10849   991.465454      7385.363281     7.448936
10850   998.296753      7445.492188     7.458195
10851   1004.994507     7502.971191     7.465684
10852   1011.622253     7558.873047     7.472031
10853   1018.247986     7614.334961     7.477879
10854   1024.936646     7670.433594     7.483812
10855   1031.741577     7728.045898     7.490292
10856   1038.692505     7787.652832     7.497554
10857   1045.802124     7849.450195     7.505674
10858   1053.061768     7913.276855     7.514542
10859   1060.435791     7978.526855     7.523819
10860   1067.890625     8044.633789     7.533200
10861   1075.397095     8111.109863     7.542432
10862   1082.926636     8177.480957     7.551279
10863   1090.454224     8243.333984     7.559542
10864   1097.956055     8308.277344     7.567040
10865   1105.412476     8371.997070     7.573641
10866   1112.800293     8434.115234     7.579181
10867   1120.090942     8494.176758     7.583471
10868   1127.249268     8551.612305     7.586266
10869   1134.224976     8605.613281     7.587219
10870   1140.959961     8655.244141     7.585931
10871   1147.392700     8699.521484     7.581991
10872   1153.478149     8737.743164     7.575127
10873   1159.199341     8769.674805     7.565286
10874   1164.561279     8795.450195     7.552587
10875   1169.595459     8815.636719     7.537338
10876   1174.339966     8830.906250     7.519889
10877   1178.822998     8841.765625     7.500503
10878   1183.079956     8848.833984     7.479489
10880   1191.073364     8854.315430     7.433896
10881   1194.850098     8853.440430     7.409666
10882   1198.505249     8850.591797     7.384692
10883   1202.057373     8846.092773     7.359127
10884   1205.522095     8840.214844     7.333101
10885   1208.909058     8833.127930     7.306694
10886   1212.210815     8824.719727     7.279856
10887   1215.433594     8815.101562     7.252639
10888   1218.596924     8804.606445     7.225200
10889   1221.709351     8793.382812     7.197606
10890   1224.795898     8781.849609     7.170052
10891   1227.908081     8770.861328     7.142930
10892   1231.085815     8761.073242     7.116541
10893   1234.347900     8752.788086     7.091022
10894   1237.714722     8746.330078     7.066515
10895   1241.201050     8741.929688     7.043121
10896   1244.808228     8739.593750     7.020835
03.141.18.40.32.0551.6.m        2003:141:18:49:22.662   277     2791
03.141.18.40.32.0551.6.m        2003:141:18:49:28.412   498     3912
03.141.18.40.32.0551.6.m        2003:141:18:49:31.512   671     5221
03.141.18.40.32.0551.6.m        2003:141:18:49:34.062   871     8854

     */
}



