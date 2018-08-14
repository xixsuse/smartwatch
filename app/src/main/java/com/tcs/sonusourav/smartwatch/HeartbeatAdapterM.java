package com.tcs.sonusourav.smartwatch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HeartbeatAdapterM extends BaseAdapter{

        private Context hcontext;
        private ArrayList<HeartbeatHistoryList> HBList;

        HeartbeatAdapterM(Context context, ArrayList<HeartbeatHistoryList> ecList) {
            this.hcontext = context;
            this.HBList = ecList;
        }

        @Override
        public int getCount() {
            return HBList.size();
        }

        @Override
        public HeartbeatHistoryList getItem(int position) {
            return HBList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


            if (convertView == null) {
                convertView = LayoutInflater.from(hcontext).inflate(R.layout.history_listview, parent, false);
            }

            HeartbeatHistoryList hb = HBList.get(position);

            final TextView hbDate = convertView.findViewById(R.id.hb_date_tv);
            final TextView hbTime = convertView.findViewById(R.id.hb_time_tv);
            final TextView hbDesc = convertView.findViewById(R.id.hb_description_tv);
            final TextView hbValue = convertView.findViewById(R.id.hb_value_tv);
            final ImageButton hbShareBtn = convertView.findViewById(R.id.hb_share_btn);

            hbDate.setText(hb.getHBhistoryDate());
            hbTime.setText(hb.getHBhistoryTime());
            hbDesc.setText(hb.getHBdescription());
            hbValue.setText(Integer.toString(hb.getHBvalue()));
            hbShareBtn.setImageResource(R.drawable.icon_share);


            hbShareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextToPdf.createandDisplayPdf("testing");
                }
            });


            return convertView;

        }

}
