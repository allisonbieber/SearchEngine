

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

/**
 *
 */
public class QueryDriver {

  public static void main(String[] args) {


    //Pre-processing stage, do not uncomment:

 //   Data token = new Data();
   // token.parseBusiness();  //11,216 business, 290,097 rev
   // token.parseReviewFile();
   // token.findNLPScore();
  //  token.uploadReviewsSolr();


    //Initialize the view
    View view = new View();
    view.showGUI();


  }


  public static String parseQuery(String query) {

    String[] lineLst = query.split(" ");
    String id = lineLst[0];
    id = id.substring(0, id.length() - 1);

    return query;

  }

  public static void count() {

    //cities
    HashSet<String> charlotteBus = new HashSet<>();
    HashSet<String> phoenixBus = new HashSet<>();
    HashSet<String> lasvegasBus = new HashSet<>();
    HashSet<String> torBus = new HashSet<>();
    HashSet<String> glenBus = new HashSet<>();
    HashSet<String> scotBus = new HashSet<>();
    HashSet<String> temBus = new HashSet<>();


    File original= new File("business.json");
    JSONParser jsonParser = new JSONParser();

    try {
      Scanner scan = new Scanner(original);

      while (scan.hasNextLine()) {
        try {
          System.out.println("bus lines");

          JSONObject obj = (JSONObject) jsonParser.parse(scan.nextLine());
          String id = obj.get("business_id").toString();
          String state = obj.get("state").toString();
          String city = obj.get("city").toString();


        if (city.equals("Glendale")) {
          glenBus.add(id);
        }


        } catch (Exception e) {
          System.out.println("Scanner error");
          e.printStackTrace();
        }
      }

    } catch (Exception e) {
      System.out.println("File writer error");
      e.printStackTrace();
    }

    //PARSE REVIEWS

    File reviews = new File("review.json");

    int lasVegas = 0;
    int phoenix = 0;
    int charlotte = 0;
    int toronto = 0;
    int glendale = 0;
    int scottsdale = 0;
    int tempe = 0;

    try {
      Scanner scan = new Scanner(reviews);

      while (scan.hasNextLine()) {
        try {
          System.out.println("review lines");

          JSONObject obj = (JSONObject) jsonParser.parse(scan.nextLine());
          String id = obj.get("business_id").toString();

          /*if (lasvegasBus.contains(id)) {
            lasVegas++;
          }

          if (torBus.contains(id)) {
            toronto++;
          }

          if (phoenixBus.contains(id)) {
            phoenix++;
          }

          if (charlotteBus.contains(id)) {
            charlotte++;
          }*/
          if (glenBus.contains(id)) {
            glendale++;
          }
         /* if (scotBus.contains(id)) {
            scottsdale++;
          }
          if (temBus.contains(id)) {
            tempe++;
          }*/


        } catch (Exception e) {
          System.out.println("Scanner error");
          e.printStackTrace();
        }
      }

    } catch (Exception e) {
      System.out.println("File writer error");
      e.printStackTrace();
    }

    System.out.println("Glen Business: " + glenBus.size() + " Reviews: " + glendale);
  }

}

