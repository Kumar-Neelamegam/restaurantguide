package at.jku.assistivetechnology.myapplication.coreModules.coreModules;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Iterator;
import java.util.Map;

import at.jku.assistivetechnology.domain.objects.RestaurantObject;
import at.jku.assistivetechnology.myapplication.R;
import at.jku.assistivetechnology.myapplication.coreModules.utilities.SharedPrefUtils;

public class MoreInfoActivity extends AppCompatActivity {

    TextView txtvw_title;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moreinfo);
        try {
            setTheme();
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setTheme() {
        SharedPrefUtils sharedPrefUtils = SharedPrefUtils.getInstance(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("More Information");
        LinearLayout parent=findViewById(R.id.parent);
        if (sharedPrefUtils.isDarkMode()) {
            setTheme(R.style.AppTheme);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkcommonaccent)));
            parent.setBackground(new ColorDrawable(getResources().getColor(R.color.darkcommonaccent)));
        } else {
            setTheme(R.style.AppTheme_Light);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.lightcommonaccent)));
            parent.setBackground(new ColorDrawable(getResources().getColor(R.color.lightcommonaccent)));
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
