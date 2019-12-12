import java.util.*;
import java.io.*;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

/**
 * This class performs the pre-processing of the Yelp Data
 */
public class Data {

  private HashMap<String, String> businessName;
  private HashMap<String, String> businessAddress;

  public Data() {
    this.businessName = new HashMap<>(); //maps business ids to
    this.businessAddress = new HashMap<>();

  }


  /**
   * Parses the business.json file and creates a business to business id map
   */
  private void parseBusiness() {

    File file = new File("business.json");
    File newFile = new File("glendale.txt");
    JSONParser jsonParser = new JSONParser();

    try {
      Scanner scan = new Scanner(file);

      while (scan.hasNextLine()) {

        try {

          JSONObject obj = (JSONObject) jsonParser.parse(scan.nextLine());

          if (obj.get("city").equals("Glendale")) {
            businessName.put(obj.get("business_id").toString(), obj.get("name").toString());
            businessAddress.put(obj.get("business_id").toString(), obj.get("address").toString());

          }

        } catch (Exception e) {
          System.out.println("Scanner error");
          e.printStackTrace();
        }
      }

      System.out.println("Businesses: " + businessName.size());
      System.out.println("Businesses: " + businessAddress.size());

    } catch (Exception e) {
      System.out.println("JSON Parser Error");
      e.printStackTrace();
    }
  }

  /**
   * This function parses the review.json file
   */
  private void parseReviewFile() {
    int count = 0;

    File revJson = new File("review.json");
    JSONParser jsonParser = new JSONParser();

    try {
      FileWriter newFile = new FileWriter("glendale.txt", true);
      Scanner scan = new Scanner(revJson);

      while (scan.hasNextLine()) {

        try {

          JSONObject obj = (JSONObject) jsonParser.parse(scan.nextLine());
          String id = obj.get("business_id").toString();

          if (businessName.containsKey(id)) {
            // String rev = obj.get("text").toString();

            obj.put("address", businessAddress.get(id));
            obj.put("bus_name", businessName.get(id));
            newFile.write(obj.toJSONString());
            newFile.write("\n");
            count++;
            System.out.println(count);
          }

        } catch (Exception e) {
          System.out.println("Scanner error");
          e.printStackTrace();
        }
      }

      scan.close();
      newFile.flush();
      newFile.close();


    } catch (Exception e) {
      System.out.println("JSON Parser Error");
      e.printStackTrace();
    }
    System.out.println(count);

  }

  /**
   * This function calculates the NLP score for the review
   */
  private void findNLPScore() {
    int count = 0;

    File glen = new File("glendale.txt");
    JSONParser jsonParser = new JSONParser();


    Properties props = new Properties();
    props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    try {
      Scanner scan = new Scanner(glen);
      FileWriter newFile = new FileWriter("glendaleScores.json", true);

      while (scan.hasNextLine()) {
        try {

          int result = 0;

          JSONObject obj = (JSONObject) jsonParser.parse(scan.nextLine());
          String rev = obj.get("text").toString();

          String[] linesArr = rev.split("\\.");
          for (int i = 0; i < linesArr.length; i++) {
            if (linesArr[i] != null) {
              Annotation annotation = pipeline.process(linesArr[i]);
              for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                int score = RNNCoreAnnotations.getPredictedClass(tree);
                result = result + (score - 2);
              }
            }
          }

          obj.put("score", result);

          newFile.write(obj.toJSONString());
          newFile.write("\n");
          count++;
          //started at 1:57

          System.out.println(count);


        } catch (Exception e) {
          System.out.println("Scanner error");
          e.printStackTrace();
        }
      }

    } catch (Exception e) {
      System.out.println("File writer error");
      e.printStackTrace();
    }

  }

  /**
   * Parses glendale.txt and parses into json object
   * Calculates score
   * Adds to solr
   */
  public void uploadReviewsSolr() {
    int count = 0;

    String urlString = "http://localhost:8983/solr/reviews";
    SolrClient solr = new HttpSolrClient.Builder(urlString).build();

    File glen = new File("glendaleScores.json");
    JSONParser jsonParser = new JSONParser();

    try {
      Scanner scan = new Scanner(glen);

      while (scan.hasNextLine()) {
        try {

          JSONObject obj = (JSONObject) jsonParser.parse(scan.nextLine());

          String id = obj.get("business_id").toString();
          String rev = obj.get("text").toString();
          String name = obj.get("bus_name").toString();
          String address = obj.get("address").toString();
          int score = Integer.parseInt(obj.get("score").toString());
          count++;

          SolrInputDocument document = new SolrInputDocument();

          document.addField("id", count);
          document.addField("text", rev);
          document.addField("bus_id", id);
          document.addField("name", name);
          document.addField("nlpScore", score);
          document.addField("address", address);

          UpdateResponse response = solr.add(document);
          if (count % 1000 == 0) {
            solr.commit();
          }

          System.out.println(count);

        } catch (Exception e) {
          System.out.println("Scanner error");
          e.printStackTrace();
        }
      }

      solr.commit();
      scan.close();

    } catch (Exception e) {
      System.out.println("File writer error");
      e.printStackTrace();
    }

  }



}
