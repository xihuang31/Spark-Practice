import scala.Tuple2;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.*;
import org.apache.spark.sql.SparkSession;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.StringTokenizer;


public final class WordCount {
    
    private static final Pattern SPACE = Pattern.compile(" +");
    
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: JavaWordCount <input> <output>");
            System.exit(1);
        }
        JavaSparkContext sc = new JavaSparkContext();
        JavaRDD<String> lines = sc.textFile(args[0]);
        JavaRDD<String> words = lines.flatMap(line -> Arrays.asList(SPACE.split(line)).iterator());
        JavaRDD<String> normWords = words.map(word -> normolizeWord(word) );
 
         JavaPairRDD<String, Integer> ones = normWords.mapToPair(word -> new Tuple2<>(word, 1));
        JavaPairRDD<String, Integer> counts = ones.reduceByKey( (count1, count2) ->
                                                               count1 + count2);
        JavaPairRDD<Integer,String> swap = counts.mapToPair(item -> item.swap());
       swap.sortByKey(false,1).coalesce(1,true).saveAsTextFile(args[1]);
 //       counts.sortBy(_2,false).coalesce(1,true).saveAsTextFile(args[1]);
        sc.stop();
    }
    
    public static String normolizeWord(String word){
        if(word.endsWith("'")){
            word = word.substring(0,word.length()-1);
        }
        if(word.endsWith(")")){
            word = word.substring(0,word.length()-1);
        }
        if(word.endsWith("_")){
            word = word.substring(0,word.length()-1);
        }
        if(word.endsWith(";")){
            word = word.substring(0,word.length()-1);
        }
        if(word.endsWith("!")){
            word = word.substring(0,word.length()-1);
        }
        if(word.endsWith("?")){
            word = word.substring(0,word.length()-1);
        }
        if(word.endsWith(",")){
            word = word.substring(0,word.length()-1);
        }
        if(word.endsWith(":")){
            word = word.substring(0,word.length()-1);
        }
        if(word.endsWith(";")){
            word = word.substring(0,word.length()-1);
        }
        if(word.endsWith("--")){
            word = word.substring(0,word.length()-2);
        }
        if(word.endsWith("'s")){
            word = word.substring(0,word.length()-2);
        }
        if(word.endsWith("ly")){
            word = word.substring(0,word.length()-2);
        }
        if(word.endsWith("ed")){
            word = word.substring(0,word.length()-2);
        }
        if(word.endsWith("ing")){
            word = word.substring(0,word.length()-3);
        }
        if(word.endsWith("ness")){
            word = word.substring(0,word.length()-4);
        }
        if(word.startsWith("'")){
            word = word.substring(0,word.length()-1);
        }
        
        if(word.startsWith("(")){
            word = word.substring(0,word.length()-1);
        }
        if(word.startsWith("_")){
            word = word.substring(0,word.length()-1);
        }
        if(word.startsWith("\"")){
            word = word.substring(0,word.length()-1);
        }
        word = word.toLowerCase();
        return word;
    }
}
