package at.jku.assistivetechnology.myapplication.coreModules;

import android.content.Context;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Map;

import at.jku.assistivetechnology.domain.objects.RestaurantObject;
import at.jku.assistivetechnology.myapplication.R;
public class MoreInfoActivity extends WearableActivity  {

    TextView txtvw_title;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moreinfo);
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void init() {

        linearLayout=findViewById(R.id.dynamic_layout);
        txtvw_title=findViewById(R.id.txtvw_restauranttitle);

        //getting the selected object from previous activity
        RestaurantObject objects = (RestaurantObject) getIntent().getSerializableExtra("extra");

        //setting the title
        txtvw_title.setText(objects.getRestaurantName());

        //prepare the more info details
        prepareDynamicData(objects);

    }

    private void prepareDynamicData(RestaurantObject objects) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View _itemRow = null;
        TextView label, value;

        StringBuilder str=new StringBuilder();
        Iterator it=objects.getRestaurantFullData().entrySet().iterator();
        while (it.hasNext()) {
            _itemRow = inflater.inflate(R.layout.activity_inforowitem, null);
            label=_itemRow.findViewById(R.id.txtvw_label);
            value=_itemRow.findViewById(R.id.txtvw_value);
            Map.Entry pair = (Map.Entry)it.next();
            //str.append(pair.getKey()+"==>"+pair.getValue()).append("\n");
            label.setText(pair.getKey().toString());
            value.setText(pair.getValue().toString());
            linearLayout.addView(_itemRow);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
