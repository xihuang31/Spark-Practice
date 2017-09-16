import scala.Tuple2;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.*;
import org.apache.spark.sql.SparkSession;
import java.util.Arrays;
import java.util.Iterator;
import java.util.*;
import java.util.regex.Pattern;
import java.util.StringTokenizer;


public final class TaxReturn {
    
    private static final Pattern Comma = Pattern.compile(",");

    
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: TaxCount <input> <output>");
            System.exit(1);
        }
        JavaSparkContext sc = new JavaSparkContext();
        JavaRDD<String> lines = sc.textFile(args[0]);
        JavaPairRDD<String,Double> record = lines.mapToPair(
            new PairFunction<String,String,Double>(){
            @Override
                public Tuple2<String,Double> call(String s){
                    String [] strs = Comma.split(s);
                    if (strs[1].equals("STATE")){
                        return new Tuple2<String,Double>("STATE",0.0);
                    }
                   return new Tuple2<String,Double>(strs[1]+" "+strs[3],Double.parseDouble(strs[4]));
                }
        });
        JavaPairRDD<String,Double> recordfilter = record.filter( item ->
                    item._1().equals("STATE")?false:true);
        JavaPairRDD<String, Double> counts = recordfilter.reduceByKey(
                                                                    (count1, count2) ->count1 + count2);
        
        counts.sortByKey(true,1).coalesce(1,true).saveAsTextFile(args[1]);
        //       counts.sortBy(_2,false).coalesce(1,true).saveAsTextFile(args[1]);
        sc.stop();
    }
    
}

