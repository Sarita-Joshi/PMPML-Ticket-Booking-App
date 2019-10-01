package com.example.myApplication.payment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

//import android.support.v7.app.AppCompatActivity;

public class checksum extends AppCompatActivity  {
    String custid = "", orderId = "", mid = "", amount = "";
        int temp = 0;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_1);
            //initOrderId();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            Intent intent = getIntent();
            //orderId = intent.getExtras().getString("orderid");
            orderId = uuid_gen();
            custid = intent.getExtras().getString("custid");
            temp = intent.getIntExtra("final_amount", 10);
            mid = "YJIkal71410751507130"; /// your merchant id
            sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
            //noinspection unchecked
            dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

    @SuppressLint("StaticFieldLeak")
    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(checksum.this);

        String url ="https://much-factors.000webhostapp.com/generateChecksum.php";
        String verifyurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderId;
        String CHECKSUMHASH ="";
        @Override
        protected void onPreExecute() {
//            this.dialog.setMessage("Please wait");
//            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {

            JSONparser jsonParser = new JSONparser(getApplicationContext());
            String param=
                    "MID="+mid+
                            "&ORDER_ID=" + orderId+
                            "&CUST_ID="+custid+
                            "&CHANNEL_ID=WAP&TXN_AMOUNT=" + temp + "&WEBSITE=PRODUCTION" +
                            "&CALLBACK_URL=" + verifyurl + "&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {
                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {
         Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
            dialog.dismiss();
        }
        PaytmPGService Service = PaytmPGService.getProductionService();
        // when app is ready to publish use production service
        // PaytmPGService  Service = PaytmPGService.getProductionService();
        // now call paytm service here
        //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
        HashMap<String, String> paramMap = new HashMap<String, String>();
        //these are mandatory parameters
            paramMap.put("MID", mid); //MID provided by paytm
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custid);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", String.valueOf(temp));
            paramMap.put("WEBSITE", "PRODUCTION");
            paramMap.put("CALLBACK_URL", verifyurl);
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.i("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);

        // start payment service call here
            Service.startPaymentTransaction(checksum.this, true, true, new PaytmPaymentTransactionCallback() {
            /*Call Backs*/
            public void someUIErrorOccurred(String inErrorMessage) {
                //Toast.makeText(checksum.this, "hahahahaha", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(checksum.this, PaymentActivity.class);
                i.putExtra("METHOD","PAYTM");
                i.putExtra("TXN_RESPONSE","TXN_FAILURE");
                setResult(RESULT_OK);
                finish();
            }

            public void onTransactionResponse(Bundle inResponse) {
//                    Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                Log.e("Response", inResponse.toString());
                if(inResponse.toString().contains("STATUS=PENDING"))
                {
                    Intent i = new Intent(getApplicationContext(),PaymentActivity.class);
                    i.putExtra("TXN_RESPONSE","TXN_FAILURE");
                    i.putExtra("METHOD","PAYTM");
                    setResult(RESULT_OK,i);
                    finish();
                }
                else{
                    Intent i = new Intent(getApplicationContext(),PaymentActivity.class);
                    i.putExtra("TXN_RESPONSE","TXN_SUCCESS");
                    i.putExtra("METHOD","PAYTM");
                    setResult(RESULT_OK,i);
                    finish();
                }

            }

            public void networkNotAvailable() {
                Toast.makeText(checksum.this, "Check your Network", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),PaymentActivity.class);
                i.putExtra("TXN_RESPONSE","TXN_FAILURE");
                i.putExtra("METHOD","PAYTM");
                setResult(RESULT_OK,i);
                finish();
            }

            public void clientAuthenticationFailed(String inErrorMessage) {
//                    Intent i = new Intent(getApplicationContext(), PaymentActivity.class);
//                    startActivity(i);
//                    finish();
                Intent i = new Intent(getApplicationContext(),PaymentActivity.class);
                i.putExtra("TXN_RESPONSE","TXN_FAILURE");
                i.putExtra("METHOD","PAYTM");
                setResult(RESULT_OK,i);
                finish();
            }

            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                Intent i = new Intent(getApplicationContext(),PaymentActivity.class);
                i.putExtra("TXN_RESPONSE","TXN_FAILURE");
                i.putExtra("METHOD","PAYTM");
                setResult(RESULT_OK,i);
                finish();
            }

            public void onBackPressedCancelTransaction() {
                Log.e("checksum ", " cancel call back respon  ");
                Toast.makeText(getApplicationContext(),"Payment Cancelled", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(), PaymentActivity.class);
                i.putExtra("TXN_RESPONSE","TXN_FAILURE");
                i.putExtra("METHOD","PAYTM");
                setResult(RESULT_OK,i);
                finish();

            }

            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

                Toast.makeText(getApplicationContext(),"Payment Cancelled", Toast.LENGTH_LONG).show();
                Intent i = new Intent(getApplicationContext(),PaymentActivity.class);
                i.putExtra("TXN_RESPONSE","TXN_FAILURE");
                i.putExtra("METHOD","PAYTM");
                setResult(RESULT_OK,i);
                finish();
            }
        });

    }
}


    public String uuid_gen()
    {
        UUID uuid=UUID.randomUUID();
        String orderID=uuid.toString();
        return orderID;
    }


}