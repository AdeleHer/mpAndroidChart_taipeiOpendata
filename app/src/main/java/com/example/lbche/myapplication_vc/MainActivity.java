package com.example.lbche.myapplication_vc;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> list1 = new ArrayList<String>();
    private TextView mTxtDisplay;
    private LineChart lineChart;
    private BarChart barChart;
    private Description description =new Description();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //GUI components
        mTxtDisplay = (TextView) findViewById(R.id.txv1);
        lineChart= (LineChart) findViewById(R.id.lineChart);
        barChart=(BarChart) findViewById(R.id.barChart);
        //創建描述信息
        description.setText("測試圖表");
        description.setTextColor(Color.RED);
        description.setTextSize(20);
        lineChart.setDescription(description);//設置圖表描述信息
        lineChart.setNoDataText("沒有數據");//沒有數據時顯示的文字
        lineChart.setNoDataTextColor(Color.BLUE);//沒有數據時顯示文字的顏色
        lineChart.setDrawGridBackground(false);//chart 繪圖區后面的背景矩形將繪制
        lineChart.setDrawBorders(false);//禁止繪制圖表邊框的線

        barChart.setDescription(description);//設置圖表描述信息
        barChart.setNoDataText("沒有數據");//沒有數據時顯示的文字
        barChart.setNoDataTextColor(Color.BLUE);//沒有數據時顯示文字的顏色
        barChart.setDrawGridBackground(false);//chart 繪圖區后面的背景矩形將繪制
        barChart.setDrawBorders(false);//禁止繪制圖表邊框的線
    }

    //On-Click event listener
    public void onClick(View view) {
        //queue
        RequestQueue queue = Volley.newRequestQueue(this);

        //Open Data 網址: 停車場
        //String urlParkingArea = "http://data.taipei/opendata/datalist/apiAccess?scope=resourceAquire&rid=a880adf3-d574-430a-8e29-3192a41897a5";

        //Open Data 網址: 溫度 (A)
        String urlParkingArea = "http://data.taipei/opendata/datalist/apiAccess?scope=resourceAquire&rid=2ba6d87e-d27a-458b-85a0-44eac0110df4";

        //發出 Volley 請求
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, urlParkingArea, null, new Response.Listener<JSONObject>() {

                    // 請求成功
                    @Override
                    public void onResponse(JSONObject response) {

                        // 顯示資料 並 繪圖
                        parserJson(response);
                    }
                }, new Response.ErrorListener() {

                    // 請求失敗
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.e("2", "error : " + error.toString());
                    }
                });

        // Access the RequestQueue
        queue.add(jsObjRequest);
    }

    // 顯示資料 並 繪圖
    private void parserJson(JSONObject jsonObject) {
        try {

            /* Entry 坐標點對象  構造函數 第一個參數為x點坐標 第二個為y點*/
            ArrayList<Entry> values1 = new ArrayList<>();
            ArrayList<BarEntry> values2 = new ArrayList<>();

            //分析JSON資料: 溫度 (B)
            JSONArray data = jsonObject.getJSONObject("result").getJSONArray("results");

            //取前10筆
            for (int i = 0; i < data.length(); i++) {
                JSONObject o = data.getJSONObject(i);
                if(o.get("營運日").toString().length()>=1) {
                    int date = Integer.parseInt(o.getString("營運日").split("/")[2]);
                    int people = Integer.parseInt(o.getString("總 運 量 (單位：人次)").replaceAll(",","").trim());
                    values1.add(new Entry(date,people));
                    values2.add(new BarEntry(date,people));
                }
            }

            //繪圖
            mTxtDisplay.setText("臺北捷運全系統旅運量統計_201710");

            //LineDataSet每一個對象就是一條連接線
            LineDataSet set1;
            BarDataSet set2;

            //判斷圖表中原來是否有數據
           if (lineChart.getData() != null &&
                    lineChart.getData().getDataSetCount() > 0) {
                //獲取數據1
                set1 = (LineDataSet) lineChart.getData().getDataSetByIndex(0);
                set1.setValues(values1);
                set2= (BarDataSet) barChart.getData().getDataSetByIndex(0);
                set2.setValues(values2);
                //刷新數據
                lineChart.getData().notifyDataChanged();
                lineChart.notifyDataSetChanged();
               barChart.getData().notifyDataChanged();
               barChart.notifyDataSetChanged();
            } else {
                //設置數據1  參數1：數據源 參數2：圖例名稱
                set1 = new LineDataSet(values1, "測試數據1");
                set1.setColor(Color.RED);
                set1.setCircleColor(Color.BLACK);
                set1.setLineWidth(1f);//設置線寬
                set1.setCircleRadius(3f);//設置焦點圓心的大小
                set1.enableDashedHighlightLine(10f, 5f, 0f);//點擊后的高亮線的顯示樣式
                set1.setHighlightLineWidth(2f);//設置點擊交點后顯示高亮線寬
                set1.setHighlightEnabled(true);//是否禁用點擊高亮線
                set1.setHighLightColor(Color.RED);//設置點擊交點后顯示交高亮線的顏色
                set1.setValueTextSize(9f);//設置顯示值的文字大小
                set1.setDrawFilled(false);//設置禁用范圍背景填充

                //格式化顯示數據
                final DecimalFormat mFormat = new DecimalFormat("###,###,##0");
                set1.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        return mFormat.format(value);
                    }
                });
                //if (Utils.getSDKInt() >= 18) {
                    // fill drawable only supported on api level 18 and above
                //    Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                //    set1.setFillDrawable(drawable);//設置范圍背景填充
                //} else
                {
                    set1.setFillColor(Color.BLACK);
                }

                //設置數據2
                 set2=new BarDataSet(values2,"測試數據2");

                set2.setColors(ColorTemplate.MATERIAL_COLORS);
               ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
               dataSets.add(set2);

               BarData barData = new BarData(dataSets);
               barData.setValueTextSize(10f);
               barData.setBarWidth(0.9f);

                //保存LineDataSet集合
                ArrayList<ILineDataSet> dataSets2 = new ArrayList<>();
                dataSets2.add(set1); // add the datasets
                //創建LineData對象 屬于LineChart折線圖的數據集合
                LineData line_data = new LineData(dataSets2);
                // 添加到圖表中
                lineChart.setData(line_data);
                barChart.setData(barData);
                //繪制圖表
                lineChart.invalidate();
                barChart.invalidate();
           }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

