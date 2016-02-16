package com.qcast.tower.form;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qcast.tower.R;
import com.qcast.tower.business.Logic;
import com.slfuture.pluto.communication.Host;
import com.slfuture.pluto.communication.response.CommonResponse;
import com.slfuture.pluto.communication.response.Response;
import com.qcast.tower.model.MyWalletModel;
import com.slfuture.carrie.base.json.JSONArray;
import com.slfuture.carrie.base.json.JSONNumber;
import com.slfuture.carrie.base.json.JSONObject;
import com.slfuture.carrie.base.json.JSONString;
import com.slfuture.carrie.base.json.core.IJSON;

import java.util.ArrayList;

/**
 * Created by zhengningchuan on 15/9/16.
 */
public class MyWalletActivity extends Activity {
    private ArrayList<MyWalletModel> dataList;
    private MyWalletAdapter adapter;
    private ListView walletListView;
    private Button return_btn;
    private Button bell_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_my_wallet);
        walletListView = (ListView) this.findViewById(R.id.my_wallet_list);
        return_btn = (Button) this.findViewById(R.id.mywallet_return_btn);
        bell_btn = (Button) this.findViewById(R.id.mywallet_bell_btn);
        dataList = new ArrayList<MyWalletModel>();
        adapter = new MyWalletAdapter(this,dataList);
        walletListView.setAdapter(adapter);
        loadData();
        return_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyWalletActivity.this.finish();
            }
        });
    }

    private void loadData() {
        Host.doCommand("myWallet", new CommonResponse<String>() {
            @Override
            public void onFinished(String content) {
                if (Response.CODE_SUCCESS != code()) {
                    Toast.makeText(MyWalletActivity.this, "网络问题", Toast.LENGTH_LONG).show();
                    return;
                }
                JSONObject resultObject = JSONObject.convert(content);
                if (((JSONNumber) resultObject.get("code")).intValue() <= 0) {
                    Toast.makeText(MyWalletActivity.this, ((JSONString) resultObject.get("msg")).getValue(), Toast.LENGTH_LONG).show();
                    return;
                }
                JSONArray result = (JSONArray) resultObject.get("data");
                if (null == result || 0 == result.size()) {
                    Toast.makeText(MyWalletActivity.this, "您暂时没有红包~~~", Toast.LENGTH_LONG).show();
                    return;
                }
                for (IJSON item : result) {
                    JSONObject newJSONObject = (JSONObject) item;
                    MyWalletModel myWalletModel = new MyWalletModel();

                    myWalletModel.title = ((JSONString) newJSONObject.get("bank")).getValue();
                    myWalletModel.bindBankCard = ((JSONString) newJSONObject.get("bank")).getValue();
                    myWalletModel.amount = ((JSONNumber) newJSONObject.get("amount")).intValue();
                    myWalletModel.id = ((JSONNumber) newJSONObject.get("id")).intValue();
                    myWalletModel.sendTime = ((JSONString) newJSONObject.get("time")).getValue();

                    dataList.add(myWalletModel);
                }
                adapter.notifyDataSetChanged();
            }
        }, Logic.token);
    }

    class MyWalletAdapter extends BaseAdapter{
        private Context context;
        private ArrayList<MyWalletModel> data;

        public MyWalletAdapter(Context context,ArrayList<MyWalletModel> data){
            this.context=context;
            this.data=data;
        }

        @Override
        public int getCount() {
            if(data!= null && data.size()>0){
                return data.size();
            }else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            if(data!= null && data.size()>position) {
                return data.get(position);
            }else{
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyWalletModel model = data.get(position);
            ViewHolder viewHolder;
            if(convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.my_wallet_list_item,null);
                viewHolder = new ViewHolder();
                viewHolder.my_wallet_amount_tv = (TextView)convertView.findViewById(R.id.my_wallet_amount_tv);
                viewHolder.my_wallet_title_tv = (TextView)convertView.findViewById(R.id.my_wallet_title_tv);
                viewHolder.my_wallet_bankcard_tv = (TextView)convertView.findViewById(R.id.my_wallet_bankcard_tv);
                viewHolder.my_wallet_sendtime_tv = (TextView)convertView.findViewById(R.id.my_wallet_sendtime_tv);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.my_wallet_amount_tv.setText(model.amount+"");
            viewHolder.my_wallet_title_tv.setText(model.title);
            viewHolder.my_wallet_bankcard_tv.setText(model.bindBankCard);
            viewHolder.my_wallet_sendtime_tv.setText("发送时间："+model.sendTime);
            return convertView;

        }

        class ViewHolder{
            public TextView my_wallet_amount_tv;
            public TextView my_wallet_title_tv;
            public TextView my_wallet_bankcard_tv;
            public TextView my_wallet_sendtime_tv;

        }
    }
}
