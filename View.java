import org.apache.solr.client.solrj.SolrQuery;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class View extends JFrame {

  private JScrollPane resultsPane;
  private JPanel buttons;
  private JTextArea res;


  public View() {

    this.createView();

  }

  private void createView() {

    this.setSize(900, 800);
    this.setLocation(200, 20);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(true);
    this.setTitle("Context-Based Yelp Reviews");

    // JPanel area = new JPanel();

    BoxLayout layout = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
    this.setLayout(layout);

    buttons = new JPanel();
    GridBagLayout grid = new GridBagLayout();
    buttons.setLayout(grid);
   // buttons.setPreferredSize(new Dimension(800, 100));
    buttons.setSize(new Dimension(800, 100));
    buttons.setOpaque(true);
    buttons.setBackground(new Color(145, 195, 163));
    //Add text field
    final JTextField input = new JTextField();
    input.setText("");
    input.setColumns(40);
    //Add buttons
    JButton searchButton = new JButton("Search");
    searchButton.setActionCommand("Run Search");
    searchButton.setBounds(0, 0, 50, 20);
    searchButton.addActionListener(new Action() {
      @Override
      public Object getValue(String key) {
        return null;
      }

      @Override
      public void putValue(String key, Object value) {

      }

      @Override
      public void setEnabled(boolean b) {

      }

      @Override
      public boolean isEnabled() {
        return false;
      }

      @Override
      public void addPropertyChangeListener(PropertyChangeListener listener) {

      }

      @Override
      public void removePropertyChangeListener(PropertyChangeListener listener) {

      }

      @Override
      public void actionPerformed(ActionEvent e) {
        //create ranking object that runs solr query and re-ranks, returning list
        Ranking results = new Ranking(input.getText());
        System.out.println("getting top results");
        results.getTop();
        System.out.println("Search done");
        display(results);
        System.out.println("Top is displayed");

        //print results to result screen

      }
    });
    JButton clearButton = new JButton("Clear");
    clearButton.setBounds(100, 100, 50, 20);
    clearButton.addActionListener(new Action() {
      @Override
      public Object getValue(String key) {
        return null;
      }

      @Override
      public void putValue(String key, Object value) {

      }

      @Override
      public void setEnabled(boolean b) {

      }

      @Override
      public boolean isEnabled() {
        return false;
      }

      @Override
      public void addPropertyChangeListener(PropertyChangeListener listener) {

      }

      @Override
      public void removePropertyChangeListener(PropertyChangeListener listener) {

      }

      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println("clear clicked");
        input.setText("");

      }
    });
    JButton searchAllButton = new JButton("Search All");
    searchAllButton.setBounds(150, 150, 50, 20);
    searchAllButton.addActionListener(new Action() {
      @Override
      public Object getValue(String key) {
        return null;
      }

      @Override
      public void putValue(String key, Object value) {

      }

      @Override
      public void setEnabled(boolean b) {

      }

      @Override
      public boolean isEnabled() {
        return false;
      }

      @Override
      public void addPropertyChangeListener(PropertyChangeListener listener) {

      }

      @Override
      public void removePropertyChangeListener(PropertyChangeListener listener) {

      }

      @Override
      public void actionPerformed(ActionEvent e) {//create ranking object that runs solr query and re-ranks, returning list
        Ranking results = new Ranking(input.getText());
        System.out.println("getting top results");
        results.getTop();
        System.out.println("Search done");
        displayAll(results);
        System.out.println("Top is displayed");


      }
    });

    buttons.add(input);
    buttons.add(searchButton);
    buttons.add(searchAllButton);
    buttons.add(clearButton);

    getContentPane().add(buttons, BorderLayout.PAGE_START);

    res = new JTextArea(100, 100);
    res.setEditable(false);
    resultsPane = new JScrollPane(res, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    getContentPane().add(resultsPane, BorderLayout.PAGE_END);


  }

  public void showGUI() {
    this.setVisible(true);
  }

  private void display(Ranking result) {

    HashSet<String> displayed = new HashSet<>();
    HashMap<String, Integer> displayedMap = new HashMap<>(); //restaurant name to highest score

    HashMap<String, String> resultList = result.getRes();
    HashMap<String, String> reviews = result.getRev();
    HashMap<String, String> addresses = result.getAddress();
    List<String> orderedResults = result.getResultLst();

    HashMap<String, Integer> scores = result.getScores();
    String show = "";

    for (String id : orderedResults) {
      if (!displayed.contains(resultList.get(id))) {
        show = show + resultList.get(id) + "  -  " + addresses.get(id) +
                "\n" + "\t" + formatReview(reviews.get(id)) + "\n" + "\n";
      }
      displayed.add(resultList.get(id));

    }

    res.setText(show);

  }

  private void displayAll(Ranking result) {
    HashMap<String, String> resultList = result.getRes();
    HashMap<String, String> reviews = result.getRev();
    HashMap<String, String> addresses = result.getAddress();
    List<String> orderedResults = result.getResultLst();

    String show = "";

    for (String id : orderedResults) {

        show = show + resultList.get(id) + "  -  " + addresses.get(id) +
                "\n" + "\t" + formatReview(reviews.get(id)) + "\n" + "\n";

    }

    res.setText(show);

  }

  private String formatReview(String review) {

    String result = "";
    review = review.replace("\n", "").replace("\r", "");
    String[] words = review.split(" ");

    int count = 0;
    for (String word : words) {
      result = result + " " + word;
      count++;
      if (count == 18) {
        result = result + "\n" + "\t";
        count = 0;
      }
    }

    return result;
  }


}
