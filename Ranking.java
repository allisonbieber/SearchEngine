import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A ranking function class that assigns a rank to a given text review
 */
public class Ranking {

  private HashMap<String, String> reviews;
  private String query;
  private HashMap<String, Integer> scores;
  private HashMap<String, String> results;
  private HashMap<String, String> addresses;
  private List<String> resultLst;


  public Ranking(String query) {
    this.reviews = new HashMap<>();
    this.query = query;
    this.results = new HashMap<>();
    this.scores = new HashMap<>();
    this.addresses = new HashMap<>();
    this.resultLst = new ArrayList<>();
  }


  public HashMap<String, String> getRes() {
    return this.results;
  }
  public HashMap<String, String> getRev() {
    return this.reviews;
  }
  public HashMap<String, String> getAddress() {
    return this.addresses;
  }

  public HashMap<String, Integer> getScores() {
    return this.scores;
  }
  public List<String> getResultLst() {
    return this.resultLst;
  }

  public void getTop() {

    //Set up SOLR connection
    String urlString = "http://localhost:8983/solr/reviews";
    HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
    solr.setParser(new XMLResponseParser());

    String newQuery = "";

    if (this.query.charAt(0) == '"' && this.query.charAt(this.query.length() - 1) == '"') {


      newQuery = "text:" + this.query;
      newQuery = newQuery + " AND nlpScore:[1 TO *]";

    } else {

      String[] queryWords = query.split(" ");
      for (int i = 0; i < queryWords.length; i++) {
        newQuery = newQuery + "text:" + queryWords[i] + " AND ";
      }
      newQuery = newQuery.substring(0, newQuery.length() - 5);
      newQuery = newQuery + " AND nlpScore:[1 TO *]";


    }
    //CREATE THE QUERY FOR SOLR
    SolrQuery query = new SolrQuery();
    query.setRows(100);
    query.set("q", newQuery);
    query.addSort("nlpScore", org.apache.solr.client.solrj.SolrQuery.ORDER.desc);
    query.setStart(0);

    try {
      QueryResponse response = solr.query(query);
      SolrDocumentList list = response.getResults();
      System.out.println("result size: " + response.getResults().size());


      for (SolrDocument solrDocument : list) {

        String restId = (String) solrDocument.getFieldValue("id");
        String restName = (String) solrDocument.getFieldValue("name");
        String review = (String) solrDocument.getFieldValue("text");
        String add = (String) solrDocument.getFieldValue("address");

        int score = (Integer) solrDocument.getFieldValue("nlpScore");

          this.resultLst.add(restId);
          this.results.put(restId, restName);
          this.scores.put(restId, score);
          this.reviews.put(restId, review);
          this.addresses.put(restId, add);
          System.out.println(score + ": " + restName);


      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
