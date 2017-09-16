import scala.Tuple2;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.SparkSession;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public final class VotersCount {
    private static final Pattern SPACE = Pattern.compile(",");
    
    public static void main(String[] args) throws Exception {
        
        SparkConf conf = new SparkConf().setAppName("VoterCountAndMean");
        JavaSparkContext sc = new JavaSparkContext(conf);
        if (args.length < 1) {
            System.err.println("Usage: Count <file>");
            System.exit(1);
        }
        
        SparkSession spark = SparkSession
        .builder()
        .appName("VotersCount")
        .getOrCreate();
        
        JavaRDD<String> lines = spark.read().textFile(args[0]).javaRDD();
    
        JavaPairRDD<String, Integer> ones = lines.mapToPair(
                new PairFunction<String, String, Integer>() {
                @Override
                public Tuple2<String, Integer> call(String s) {
                        String []  strs  = SPACE.split(s);
                        if(strs[0].equals("Code for district")){
                             return new Tuple2<>("Code",0);
                        }
                        int voters = Integer.parseInt(strs[3]);
                        return new Tuple2<>(strs[0], voters);
                    }
                });
        
        JavaPairRDD<String,Integer> filterOnes = ones.filter( item ->
                        item._1().equals("Code")?false:true);
        
        JavaPairRDD<String, Integer> counts = filterOnes.reduceByKey(
                new Function2<Integer, Integer, Integer>() {
                        @Override
                    public Integer call(Integer i1, Integer i2) {
                    return i1 + i2;
                }
        });
        counts.saveAsTextFile(args[1]+"/a");
        JavaRDD<Integer>  values = counts.values();
        double meanValue = (double)values.reduce((a,b)->a+b) / values.count();
        List<Double> data = Arrays.asList(meanValue);
        JavaRDD<Double> mean = sc.parallelize(data);
        mean.coalesce(1,true).saveAsTextFile(args[1]+"/b");
        spark.stop();
        sc.stop();
    }
    

}
