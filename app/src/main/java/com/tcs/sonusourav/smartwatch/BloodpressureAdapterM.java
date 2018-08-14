package com.tcs.sonusourav.smartwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class BloodpressureAdapterM extends BaseAdapter{

    private Context bpcontext;
    private ArrayList<BloodpressureHistoryList> BPList ;

    BloodpressureAdapterM(Context context, ArrayList<BloodpressureHistoryList> bloodpressureList) {
            this.bpcontext = context;
            this.BPList = bloodpressureList;
        }

        @Override
        public int getCount() {
            return BPList.size();
        }

        @Override
        public BloodpressureHistoryList getItem(int position) {
            return BPList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {


            if (convertView == null) {
                convertView = LayoutInflater.from(bpcontext).inflate(R.layout.history_listview, parent, false);
            }

            BloodpressureHistoryList hb = BPList.get(position);

            final TextView bpDate = convertView.findViewById(R.id.hb_date_tv);
            final TextView bpTime = convertView.findViewById(R.id.hb_time_tv);
            final TextView bpDesc = convertView.findViewById(R.id.hb_description_tv);
            final TextView bpValue = convertView.findViewById(R.id.hb_value_tv);
            final ImageButton bpShareBtn = convertView.findViewById(R.id.hb_share_btn);

            bpDate.setText(hb.getHBhistoryDate());
            bpTime.setText(hb.getHBhistoryTime());
            bpDesc.setText(hb.getHBdescription());
            bpValue.setText(Integer.toString(hb.getHBvalue()));
            bpShareBtn.setImageResource(R.drawable.icon_share);


            bpShareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    TextToPdf.createandDisplayPdf("testing");
                }
            });




            return convertView;

        }
    }



