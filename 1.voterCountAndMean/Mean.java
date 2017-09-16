import scala.Tuple2;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.SparkSession;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.FSDataOutputStream;
//import org.apache.hadoop.fs.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public final class Mean {
    private static final Pattern SPACE = Pattern.compile(" ");
    
    public static void main(String[] args) throws Exception {
        
        if (args.length < 1) {
            System.err.println("Usage: Mean <file>");
            System.exit(1);
        }
        
        SparkSession spark = SparkSession
        .builder()
        .appName("VotersMean")
        .getOrCreate();
        
        JavaRDD<String> lines = spark.read().textFile(args[0]).javaRDD();
        
        JavaRDD<Integer> voters = lines.flatMap(line ->
                        Integer.parseInt(SPACE.split(line)[1]));
        JavaRDD<Double> values = voters.reduce((a,b)->a+b) / voters.count();
        mean.saveAsTextFile(args[1]);
        spark.stop();
    }
    
}
