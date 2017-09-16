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


public final class WordPairCount {
    
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: JavaWordCount <input> <output>");
            System.exit(1);
        }
        JavaSparkContext sc = new JavaSparkContext();
        JavaRDD<String> lines = sc.textFile(args[0]);
        JavaPairRDD<String,Integer> wordsPair = lines.flatMapToPair(new TokenPairCount());
        JavaPairRDD<String, Integer> counts = wordsPair.reduceByKey(
                            (count1, count2) ->count1 + count2);
        
        JavaPairRDD<Integer,String> swap = counts.mapToPair(item -> item.swap());
        swap.sortByKey(false,1).coalesce(1,true).saveAsTextFile(args[1]);
        //       counts.sortBy(_2,false).coalesce(1,true).saveAsTextFile(args[1]);
        sc.stop();
    }
    
    static class TokenPairCount implements PairFlatMapFunction<String, String,Integer > {
        private  String endword = "";
        private  String word1 = new String();
        private  String word2 = new String();
        @Override
        public Iterator<Tuple2<String,Integer>> call(String s) {
            List<Tuple2<String, Integer>> results
            = new ArrayList<Tuple2<String, Integer>>();
            String temp ;
            String content = s.toString();
            String [] strs = content.split(" +");
            if (!endword.equals("")){
                word1 = normolizeWord(strs[0]);
                if (!word1.equals("")){
                    temp = setTupleString(endword,word1);
                    results.add(new Tuple2<>(temp,1));
                }
            }
            for (int i=0;i<strs.length-1;i++){
                word1 = normolizeWord(strs[i]);
                word2 = normolizeWord(strs[i+1]);
                if (word1.equals("")||word2.equals("")){
                    continue;
                }
                temp = setTupleString(word1,word2);
                results.add(new Tuple2<>(temp,1));
            }
            endword = normolizeWord(strs[strs.length-1]);
            return results.iterator();
        }
        
        public String setTupleString(String str1,String str2){
            if(str1.compareTo(str2) > 0){
                return str1+","+str2;
            }else{
                return str2+","+str1;
            }
        }
        
        public  String normolizeWord(String word){
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

}

