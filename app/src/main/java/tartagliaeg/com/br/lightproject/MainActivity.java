package tartagliaeg.com.br.lightproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    DownDetectorGrid grid = findViewById(R.id.downdetectorGridLayout);
    grid.setDataSet(
      "", "10:00", "10:15", "10:30", "10:45", "11:00", "11:15", "11:30",
      "Santander 2x", "21", "06", "01", "03", "19", "56", "45",
      "Bradesco", "21", "06", "9", "04", "55", "77", "64",
      "Santander", "31", "46", "59", "51", "54", "32", "30",
      "Itaú", "21", "0", "69", "06", "23", "12", "44",
      "Caixa Economica Federal", "10", "5", "14", "26", "56", "34", "32");
    grid.setColumnsWidth(3, 1, 1, 1, 1, 1, 1, 1);
    grid.notifyDataSetUpdated();
  }
}
