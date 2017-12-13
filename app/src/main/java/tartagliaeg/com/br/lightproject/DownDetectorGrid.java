package tartagliaeg.com.br.lightproject;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tartaglia on 12/12/17.
 * ...
 */

public class DownDetectorGrid extends LinearLayout {
  private int mRowSpacing = 0;
  private Integer mColumnBgColor;
  private Integer mColumnTextColor;
  private Integer mOddColor;
  private Integer mEvenColor;
  private boolean mEnableHeaderRow;
  private boolean mEnableHeaderCol;


  private List<String> mDataSet = new ArrayList<>();
  private List<Float> mSizes = new ArrayList<>();


  private ColumnViewAdapter mColumnViewAdapter;

  public DownDetectorGrid(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);

    TypedArray attrs = context.getTheme().obtainStyledAttributes(
      attributeSet,
      R.styleable.DownDetectorGrid,
      0, 0
    );

    mColumnViewAdapter = new DefaultViewAdapter(context);

    try {
      mOddColor = attrs.hasValue(R.styleable.DownDetectorGrid_odd_lines_color)
        ? attrs.getColor(R.styleable.DownDetectorGrid_odd_lines_color, 0)
        : null;

      mEvenColor = attrs.hasValue(R.styleable.DownDetectorGrid_even_lines_color)
        ? attrs.getColor(R.styleable.DownDetectorGrid_even_lines_color, 0)
        : null;

      mColumnBgColor = attrs.hasValue(R.styleable.DownDetectorGrid_columns_background_color)
        ? attrs.getColor(R.styleable.DownDetectorGrid_columns_background_color, 0)
        : null;

      mColumnTextColor = attrs.hasValue(R.styleable.DownDetectorGrid_columns_text_color)
        ? attrs.getColor(R.styleable.DownDetectorGrid_columns_text_color, 0)
        : null;

      mRowSpacing = (int) attrs.getDimension(R.styleable.DownDetectorGrid_row_vertical_padding, 0);

      mEnableHeaderCol = attrs.getBoolean(R.styleable.DownDetectorGrid_enable_header_col, false);
      mEnableHeaderRow = attrs.getBoolean(R.styleable.DownDetectorGrid_enable_header_row, false);

    } finally {
      attrs.recycle();
    }
  }

  public void notifyDataSetUpdated() {
    int columnCount = mSizes.size();
    int rowCount = mDataSet.size() / columnCount;

    for (int i = 0; i < rowCount; i++) {
      LinearLayout row = (LinearLayout) getChildAt(i);

      if (row == null) {
        row = new LinearLayout(getContext());
        row.setOrientation(HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(params);

        if (i % 2 == 0 && mOddColor != null)
          row.setBackgroundColor(mOddColor);
        else if (i % 2 == 1 && mEvenColor != null)
          row.setBackgroundColor(mEvenColor);

        row.setPadding(0, mRowSpacing, 0, mRowSpacing);
        row.setGravity(Gravity.CENTER_VERTICAL);
        this.addView(row);
      }

      updateRow(row, mDataSet.subList(i * columnCount, i * columnCount + columnCount));

    }

    this.invalidate();
  }

  private void updateRow(LinearLayout row, List<String> dataSet) {
    int rowNumber = this.indexOfChild(row);
    boolean isRowHeader = mEnableHeaderRow && rowNumber == 0;

    for (int i = 0; i < dataSet.size(); i++) {
      View column = row.getChildAt(i);

      ColumnType columnType = isRowHeader || mEnableHeaderCol && i == 0
        ? ColumnType.HEADER
        : ColumnType.BODY;


      if (column == null) {
        column = mColumnViewAdapter.inflateColumnView(columnType, row, false);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
          0, // Will be calculated via weight
          LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.weight = mSizes.get(i);
        column.setLayoutParams(layoutParams);
        row.addView(column, i);
      }

      mColumnViewAdapter.configureColumnView(columnType, column, dataSet.get(i), rowNumber, i);
      column.invalidate();
    }

    row.invalidate();
  }


  public void setEnableHeaderRow(boolean enableHeaderRow) {
    mEnableHeaderRow = enableHeaderRow;
  }

  public void setEnableHeaderCol(boolean enableHeaderCol) {
    mEnableHeaderCol = enableHeaderCol;
  }

  public void setDataSet(List<String> dataset) {
    this.mDataSet = dataset;
    if (this.mSizes.size() == 0)
      setColumnsWidthByCount(dataset.size());
  }

  public void setDataSet(String... dataset) {
    setDataSet(Arrays.asList(dataset));
  }

  public void setColumnsWidth(Float... percentage) {
    this.mSizes = Arrays.asList(percentage);
  }

  public void setColumnsWidth(int... percentage) {
    Float intArr[] = new Float[percentage.length];

    for (int i = 0; i < percentage.length; i++)
      intArr[i] = percentage[i] * 1f;

    this.mSizes = Arrays.asList(intArr);
  }

  private void setColumnsWidthByCount(int count) {
    Float[] sizes = new Float[count];
    Arrays.fill(sizes, 100.0f / count);
    setColumnsWidth(sizes);
  }

  public void setColumnViewAdapter(ColumnViewAdapter viewAdapter) {
    this.mColumnViewAdapter = viewAdapter;
  }

  public enum ColumnType {
    HEADER,
    BODY
  }


  public interface ColumnViewAdapter {
    View inflateColumnView(ColumnType type, ViewGroup parent, boolean attachToRoot);

    void configureColumnView(ColumnType type, View view, String content, int row, int col);
  }


  public static class DefaultViewAdapter implements ColumnViewAdapter {
    private Context mContext;

    public DefaultViewAdapter(Context context) {
      this.mContext = context;
    }

    @Override
    public View inflateColumnView(ColumnType type, ViewGroup parent, boolean attachToRoot) {
      return type == ColumnType.BODY
        ? LayoutInflater.from(mContext).inflate(R.layout.downdetector_grid_column_body, parent, attachToRoot)
        : LayoutInflater.from(mContext).inflate(R.layout.downdetector_grid_column_header, parent, attachToRoot);

    }

    @Override
    public void configureColumnView(ColumnType type, View view, String content, int row, int col) {
      TextView txt = view.findViewById(R.id.downdetectorGridItem);
      txt.setText(content);
    }
  }

}
