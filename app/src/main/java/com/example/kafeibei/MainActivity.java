package com.example.kafeibei;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.example.kafeibei.bean.BuyerNum;
import com.example.kafeibei.service.KeepAliveService;
import com.example.kafeibei.util.CommonUtils;
import com.example.kafeibei.util.HttpClient;
import com.example.kafeibei.util.NotificationSetUtil;
import com.example.kafeibei.util.UpdateApk;
import com.example.kafeibei.util.WindowPermissionCheck;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * ????????????????????????
 * ????????????????????????????????????
 * ????????????????????????
 * try catch
 * ???????????????????????????????????????????????????
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etUname,etPaw,etYj1;
    private TextView tvStart,tvStop,tvLog,tvAppDown,tvAppOpen,tvBrow,tvGetTitle;

    private Handler mHandler;
    private String tbName;
    private String tbId;
    private String buyerId;
    private int tbIndex;
    /*
    ???????????????????????????????????????3??????
    ??????????????????count+1???
     */
    private int count;
    private SharedPreferences userInfo;
    private int minPl;
    private Double minYj;
    private String token;
    private String yqToken;  //??????token
    private List<BuyerNum> buyerNumList;
    private boolean isAuth = false;

    private AlertDialog alertDialog2;
    private String[] tbNameArr;
    private Dialog dialog;

    private String jingDu;
    private String weiDu;
    private String ipAddress;
    private String uuid = "";
    private String imel = "";
    private String wifiName = "";
    private int tbTotal;
    private Calendar startDate;

    private static String APP_VERSION = "";
    private static String LOGIN_URL = "";
    private static String BROW_OPEN = "";
    private static String DOWNLOAD = "";
    private static String APP_VERSION_DETAIL = "";
    private static String PACKAGE = "com.senyu.kafeibei";
    /*
        2????????????0??????
         */
    private static int TASK_TYPE;
    private static int JIEDAN_DATE;
    private static int PACKAGE_COUNT = 192;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //???????????????
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, KeepAliveService.class);
        startService(intent);//??????????????????
        ignoreBatteryOptimization();//??????????????????
        if(!checkFloatPermission(this)){
            //??????????????????
            requestSettingCanDrawOverlays();
        }
        //????????????????????????????????????????????????????????????????????????true
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//?????????????????????
            //??????????????????,200????????????
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
        initView();
    }

    private void initView(){
        //????????????
        UpdateApk.update(MainActivity.this);
        //????????????????????????
        openNotification();
        //???????????????????????????
        WindowPermissionCheck.checkPermission(this);
        //??????????????????
        getPtAddress();
        mHandler = new Handler();
        etYj1 = findViewById(R.id.et_yj1);
        tvAppDown = findViewById(R.id.tv_appDown);
        tvAppOpen = findViewById(R.id.tv_appOpen);
        tvBrow = findViewById(R.id.tv_brow);
        etUname = findViewById(R.id.et_username);
        etPaw = findViewById(R.id.et_password);
        tvStart = findViewById(R.id.tv_start);
        tvStop = findViewById(R.id.tv_stop);
        tvLog = findViewById(R.id.tv_log);
        getUserInfo();//??????????????????

        //??????textView??????????????????
        tvLog.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvLog.setTextIsSelectable(true);
        tvStart.setOnClickListener(this);
        tvStop.setOnClickListener(this);
        tvBrow.setOnClickListener(this);
        tvAppOpen.setOnClickListener(this);
        tvAppDown.setOnClickListener(this);

        tvGetTitle = findViewById(R.id.tv_getTitle);
        tvGetTitle.setOnClickListener(this);

        tvLog.setText("??????600????????????~"+"\n");
        buyerNumList = new ArrayList<>();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_start:
                if("".equals(etYj1.getText().toString().trim())){
                    etYj1.setText("0");
                }
                if(Double.parseDouble(etYj1.getText().toString().trim()) > 4.0){
                    etYj1.setText("4");
                }
                minYj = Double.parseDouble(etYj1.getText().toString().trim());

                tbName = null;
                /*
                ?????????????????????Handler??????Runnable????????????????????????????????????????????????
                 */
                mHandler.removeCallbacksAndMessages(null);

                if(LOGIN_URL == ""){
                    tvLog.setText("?????????????????????,???3????????????...");
                }else {
                    userLogin(etUname.getText().toString().trim(),etPaw.getText().toString().trim(),"login");
                }
                break;
            case R.id.tv_stop:
                stop();
                break;
            case R.id.tv_appDown:

                if(DOWNLOAD == ""){
                    tvLog.setText("?????????????????????,???3????????????...");
                }else {
                    Uri uri = Uri.parse(DOWNLOAD);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
                break;
            case R.id.tv_appOpen:
                openApp(PACKAGE);
                break;
            case R.id.tv_brow:

                if(BROW_OPEN == ""){
                    tvLog.setText("?????????????????????,???3????????????...");
                }else {
                    browOpen();
                }
                break;
            case R.id.tv_getTitle:
                if(LOGIN_URL == ""){
                    tvLog.setText("?????????????????????,???3????????????...");
                }else {
                    userLogin(etUname.getText().toString().trim(),etPaw.getText().toString().trim(),"title");
                }
                break;
        }

    }




    /**
     * ????????????
     */
    public void announcementDialog(String[] lesson){
        
        dialog = new AlertDialog
                .Builder(this)
                .setTitle("??????")
                .setCancelable(false) //??????????????????????????????????????????????????? false
                .setPositiveButton("????????????", null)
                //.setMessage("")
                .setItems(lesson,null)
                .create();
        dialog.show();
    }



    private void openApp(String packName){
        PackageManager packageManager = this.getPackageManager();
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packName);
        List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);
        if (apps.size() == 0) {
            Toast.makeText(this, "????????????App????????????", Toast.LENGTH_LONG).show();
            return;
        }
        ResolveInfo resolveInfo = apps.iterator().next();
        if (resolveInfo != null) {
            String className = resolveInfo.activityInfo.name;
            Intent intent2 = new Intent(Intent.ACTION_MAIN);
            intent2.addCategory(Intent.CATEGORY_LAUNCHER);
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = new ComponentName(packName, className);
            intent2.setComponent(cn);
            this.startActivity(intent2);
        }
    }


    private void browOpen(){
        Uri uri = Uri.parse(BROW_OPEN);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /**
     * ??????activity???onKeyDown????????????????????????????????????activity
     * ????????????https://blog.csdn.net/qq_36713816/article/details/71511860
     * ?????????????????????????????????onBackPressed??????????????????????????????????????????????????????
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




    /**
     * ????????????
     * @param username
     * @param password
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void userLogin(String username, String password,String mark){

        //???????????????????????? ??????10??????
        startDate = Calendar.getInstance();
        startDate.add(Calendar.MINUTE,JIEDAN_DATE);

        //10-200  ??????5???
        jingDu = new BigDecimal(10+Math.random()*(200-10)).setScale(5,BigDecimal.ROUND_HALF_UP).toString();
        weiDu = new BigDecimal(10+Math.random()*(200-10)).setScale(5,BigDecimal.ROUND_HALF_UP).toString();
        ipAddress = CommonUtils.getClientIp(this);
        if(uuid == "" || uuid == null){
            uuid = UUID.randomUUID().toString();
            imel = UUID.randomUUID().toString();
        }

        wifiName = "";
        String mac = getRandomMac();
        String netWorkType = CommonUtils.isWifiOrGPRS(this);
        if ("wifi".equals(netWorkType)) {
            //??????wifiName
            wifiName = CommonUtils.getWIFIName(this);
            mac = "";
        }

        String androidId = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        tvLog.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()) + ": ???????????????..."+"\n");

        JSONObject params = new JSONObject();
        params.put("cellphoneNum", username);
        params.put("password", password);
        params.put("uuid", uuid);     //??????app???????????????????????????
        params.put("oaid", androidId);      //5866f70c5d3d9aa
        params.put("idfa", "");     //????????????????????????
        params.put("imeis", imel);     //??????app???????????????????????????
        params.put("mac", mac);      //????????????????????????????????????mac???????????????
        params.put("packageCount", PACKAGE_COUNT);   //????????????????????????????????????????????????
        params.put("root", false);
        params.put("real", true);
        params.put("appVersion", APP_VERSION_DETAIL);  //???????????????????????????
        params.put("deviceBrand", Build.BRAND);  //????????????
        params.put("deviceModel", Build.MODEL);  //????????????
        params.put("deviceSystem", Build.VERSION.RELEASE);  //??????????????????
        params.put("longitude", jingDu);
        params.put("latitude", weiDu);
        params.put("networkType", netWorkType);  //?????????mobile???wifiName??????
        params.put("wifiName", wifiName);


        HttpClient.getInstance().post("/sso/login/buyerLogin", LOGIN_URL)
                .upJson(params.toString())
                .headers("app-system","android")
                .headers("app-site","2")
                .headers("app-version",APP_VERSION)
//                .headers("user-agent","Dart/2.13 (dart:io)")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject obj = JSONObject.parseObject(response.body());
                            //????????????
                            if("0".equals(obj.getString("code"))){
                                sendLog("????????????");
                                token = obj.getString("data");
                                saveUserInfo(username,password, etYj1.getText().toString().trim(),uuid,imel);
                                getBuyerId(mark);
                                return;
                            }
                            sendLog(obj.getString("message"));
                        }catch (Exception e){
                            sendLog("?????????"+e.getMessage());
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendLog("??????ERR???"+response.getException());
                    }
                });
    }



    private void getBuyerId(String mark){
        HttpClient.getInstance().get("/sso/login/getLoginStatus", LOGIN_URL)
                .headers("app-system","android")
                .headers("app-site","2")
                .headers("app-version",APP_VERSION)
//                .headers("user-agent","Dart/2.13 (dart:io)")
                .headers("token",token)
                .params("token",token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject obj = JSONObject.parseObject(response.body());
                            buyerId = obj.getJSONObject("data").getString("buyerId");
                            if("login".equals(mark)){
                                getJieDanTotal();
                            }else {
                                getShopTitle();
                            }
                        }catch (Exception e){
                            sendLog("getBuyerId-ERR???"+e.getMessage());
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendLog("getBuyerId-ERR???"+response.getException());
                    }
                });
    }



    private void getJieDanTotal() {
        HttpClient.getInstance().get("/app/order/getAllowNum", LOGIN_URL)
                .headers("app-system","android")
                .headers("app-site","2")
                .headers("app-version",APP_VERSION)   //?????????????????????
//                .headers("user-agent","Dart/2.13 (dart:io)")
                .headers("x-token",token)
                .params("buyerId",buyerId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject obj = JSONObject.parseObject(response.body());
                            if("0".equals(obj.getString("code"))){
                                //???????????????????????????
                                JSONObject j = obj.getJSONObject("data");
                                String tbNum = j.getString("1");
                                tbTotal = Integer.parseInt(tbNum.substring(0,1)) + Integer.parseInt(tbNum.substring(2));
                                getTbInfo2();
                            }else {
//                                sendLog(obj.getString("message"));
                                sendLog(obj.toJSONString());
                            }
                        }catch (Exception e){
                            sendLog("getTbInfo:"+e.getMessage());
                        }
                    }
                });
    }




    /**
     * ??????????????????
     */
    private void getTbInfo2() {

        Date date = new Date();
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        JSONObject p = new JSONObject();
        p.put("buyerId", buyerId);
        p.put("platType", 1);
        JSONObject params = new JSONObject();
        params.put("pageSize", 16);
        params.put("time", dtf.format(date));
        params.put("queryString", p);

        HttpClient.getInstance().post("/app/buyer/getBindIdList", LOGIN_URL)
                .upJson(params.toString())
                .headers("app-system","android")
                .headers("app-site","2")
                .headers("app-version",APP_VERSION)
//                .headers("user-agent","Dart/2.13 (dart:io)")
                .headers("x-token",token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            /**
                             * ????????????    verifySatisfy   true?????????false?????????
                             * ????????????
                             * ????????????????????????
                             * ??????????????????  todayOrderNum
                             * ??????????????????  checkStatus  1?????? 0?????????
                             * ???????????????  defaultFlag  0??????  1????????????
                             */
                            JSONObject tbObj = JSONObject.parseObject(response.body());
                            //???????????????????????????
                            JSONArray jsonArray = tbObj.getJSONArray("data");
                            buyerNumList.clear();

                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject tbInfo = jsonArray.getJSONObject(i);
                                int tbStatus = tbInfo.getInteger("checkStatus");
                                if(tbStatus == 1){
                                    String tbId = tbInfo.getString("id");  //????????????????????????????????????
//                                    boolean isYanHao = tbInfo.getBoolean("verifySatisfy");
                                    String tbName = tbInfo.getString("thirdPlatId");
//                                    if (0 == tbInfo.getInteger("defaultFlag")){
//                                        sendLog(tbName+" ??????????????????");
//                                    }
                                    boolean foxTag = tbInfo.getBoolean("foxTag");
                                    int todayNum = tbInfo.getIntValue("todayOrderNum");

//                                if (isYanHao){
//                                    sendLog("???"+tbName+"????????????");
//                                }else {
//                                    sendLog("???"+tbName+"????????????");
//                                }

                                    if (foxTag){
                                        sendLog("???"+tbName+"???????????????,?????????????????????");
                                    }else {
                                        if(todayNum == tbTotal){
                                            sendLog("???"+tbName+"????????????,???????????????");
                                        }else {
                                            buyerNumList.add(new BuyerNum(tbId,tbName));
                                        }
                                    }
                                }

                            }

                            if(buyerNumList.size() == 0){
                                sendLog("????????????????????????");
                                return;
                            }
                            sendLog("?????????"+buyerNumList.size()+"??????????????????");

                            tbNameArr = new String[buyerNumList.size()+1];
                            tbNameArr[0] = "??????????????????";

                            for (int i = 0; i < buyerNumList.size(); i++){
                                tbNameArr[i+1] = buyerNumList.get(i).getName();
                            }
                            showSingleAlertDialog();
                        }catch (Exception e){
                            sendLog("getTbInfo:"+e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendLog(response.getException().toString());
                    }
                });
    }



    private void setDefaultFlag() {
        HttpClient.getInstance().post("/app/buyer/setDefaultID", LOGIN_URL)
                .headers("app-system","android")
                .headers("app-site","2")
                .headers("app-version",APP_VERSION)   //?????????????????????
//                .headers("user-agent","Dart/2.13 (dart:io)")
                .headers("x-token",token)
                .params("id",tbId)
                .params("buyerId",buyerId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject obj = JSONObject.parseObject(response.body());
                            if("0".equals(obj.getString("code"))){
                                startTask();
                                return;
                            }
                            sendLog(obj.getString("message"));
                        }catch (Exception e){
                            sendLog("setDefaultFlag:"+e.getMessage());
                        }
                    }
                });
    }



    public void showSingleAlertDialog(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("????????????????????????");
        alertBuilder.setCancelable(false); //??????????????????????????????????????????????????? false
        alertBuilder.setSingleChoiceItems( tbNameArr, -1, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface arg0, int index) {
                if("??????????????????".equals(tbNameArr[index])){
                    isAuth = true;
                    sendLog("????????? "+tbNameArr[index]+" ????????????");
                }else {
                    isAuth = false;
                    //????????????????????????????????????id
                    List<BuyerNum> buyerNum = buyerNumList.stream().
                            filter(p -> p.getName().equals(tbNameArr[index])).collect(Collectors.toList());
                    tbName = buyerNum.get(0).getName();
                    tbId = buyerNum.get(0).getId();
                    sendLog("????????? "+tbName+" ????????????");
                }
            }
        });
        alertBuilder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                //TODO ??????????????????
                if(!isAuth && tbName == null){
                    sendLog("?????????????????????");
                    return;
                }
                start();
                // ???????????????
                alertDialog2.dismiss();
            }
        });
        alertDialog2 = alertBuilder.create();
        alertDialog2.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void start(){

        if(isAuth){
            tbIndex = 0;
            tbName = buyerNumList.get(tbIndex).getName();
            tbId = buyerNumList.get(tbIndex).getId();
            tbIndex++;  //++?????????????????????3??????????????????????????????????????????????????????
        }
        setDefaultFlag();
    }



    private void startTask(){

        JSONObject p = new JSONObject();
        p.put("platformType", 1);
        p.put("thirdPlatId", tbName);
        p.put("thirdPlatName", "");

        JSONArray array = new JSONArray();
        array.add(p);

        JSONObject params = new JSONObject();
        params.put("buyerId", buyerId);
        params.put("taskType", TASK_TYPE);
        params.put("IpAddr", ipAddress);
        params.put("WifiName", wifiName);
        params.put("onPickInfoList", array);

        HttpClient.getInstance().post("/app/order/startReceiveOrder", LOGIN_URL)
                .upJson(params.toString())
                .headers("app-system","android")
                .headers("app-site","2")
                .headers("app-version",APP_VERSION)
//                .headers("user-agent","Dart/2.13 (dart:io)")
                .headers("x-token",token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject obj = JSONObject.parseObject(response.body());
                            /**
                            {"code":"2","message":"????????????????????????","data":null}
                            {"code":"1","message":"??????????????????????????????","data":null}
                             */
//                            sendLog("start???"+obj.toJSONString());
                            if("0".equals(obj.getString("code"))){
                                sendLog(tbName+" ??????????????????...");
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getTask();
                                    }
                                }, 5000);
                            }else if("1".equals(obj.getString("code"))){
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startTask();
                                    }
                                }, 5000);
                            }else {
                                playMusic(R.raw.fail,3000,0);
                                sendLog(obj.getString("message"));
                            }
                        }catch (Exception e){
                            sendLog("startTask???"+e.getMessage());
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendLog("startTask-ERR???"+response.getException());
                    }
                });
    }



    private void getTask(){

        JSONObject p = new JSONObject();
        p.put("platformType", 1);
        p.put("thirdPlatId", tbName);
        p.put("thirdPlatName", "");

        JSONArray array = new JSONArray();
        array.add(p);


        JSONObject params = new JSONObject();
        params.put("buyerId", buyerId);
        params.put("taskType", TASK_TYPE);
        params.put("IpAddr", ipAddress);
        params.put("WifiName", wifiName);
        params.put("onPickInfoList", array);

        HttpClient.getInstance().post("/app/order/getBuyerLockOrder", LOGIN_URL)
                .upJson(params.toString())
                .headers("app-system","android")
                .headers("app-site","2")
                .headers("app-version",APP_VERSION)
//                .headers("user-agent","Dart/2.13 (dart:io)")
                .headers("x-token",token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject obj = JSONObject.parseObject(response.body());
                            /**
                             * ??????????????????{"code":"0","data":{"brokeragePrice":"0.53","id":2113413,"orderType":0,"platformId":1,"rebateFee":"-1.00","tasktype":1,"tbTask":{}}}
                             * ??????????????????{"code":"0","data":{"brokeragePrice":"3.00","checkNum":"2","id":2148769,"orderType":0,"platformId":1,"rebateFee":"-2.90","tasktype":0,"tbTask":{"leaveMessage":"?????????????????????????????????????????????????????????????????????????????????30????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????"}}}
                             * ??????????????????{"code":"0","data":{"brokeragePrice":"3.00","checkNum":"1","id":2152874,"orderType":0,"platformId":1,"rebateFee":"-2.00","tasktype":0,"tbTask":{"leaveMessage":"????????????????????????????????????????????????????????????????????????????????????30????????????????????????????????????"}}}
                             */
//                            sendLog(obj.toJSONString());
                            if("0".equals(obj.getString("code"))){
                                JSONObject taskObj = obj.getJSONObject("data");
                                sendLog("???????????????...");
                                if(taskObj != null){
                                    //????????????id
                                    String orderId = taskObj.getString("id");
                                    String comm = taskObj.getString("brokeragePrice");
                                    if (minYj > Double.parseDouble(comm)){

                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                sendLog("?????????"+comm+"?????????????????????,?????????~");
                                                cancelTask(orderId);
                                            }
                                        }, 3000);

                                        return;
                                    }
                                    sendLog("????????????????????????"+comm);
                                    receiveSuccess(comm);
                                    lqTask(orderId);
                                    return;
                                }
                                jieDan();
                            }else {
                                sendLog(obj.getString("message"));
                                playMusic(R.raw.fail,3000,0);
                            }

                        }catch (Exception e){
                            sendLog("getTask???"+e.getMessage());
                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendLog("getTask-err???"+response.getException());

                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getTask();
                            }
                        }, 5000);
                    }
                });
    }





    private void cancelTask(String taskId){

        JSONObject p = new JSONObject();
        p.put("platformType", 1);
        p.put("thirdPlatId", tbName);
        p.put("thirdPlatName", "");

        JSONArray array = new JSONArray();
        array.add(p);

        JSONObject params = new JSONObject();
        params.put("buyerId", buyerId);
        params.put("taskType", TASK_TYPE);
        params.put("IpAddr", ipAddress);
        params.put("WifiName", wifiName);
        params.put("onPickInfoList", array);

        HttpClient.getInstance().post("/app/order/refuseOrder", LOGIN_URL)
                .upJson(params.toString())
                .headers("app-system","android")
                .headers("app-site","2")
                .headers("app-version",APP_VERSION)
//                .headers("user-agent","Dart/2.13 (dart:io)")
                .headers("x-token",token)
                .params("id",taskId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {

                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startTask();
                                }
                            }, 3000);

                        }catch (Exception e){

                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendLog("????????????ERR???"+response.getException());
                    }
                });
    }





    private void lqTask(String taskId){

        JSONObject p = new JSONObject();
        p.put("platformType", 1);
        p.put("thirdPlatId", tbName);
        p.put("thirdPlatName", "");

        JSONArray array = new JSONArray();
        array.add(p);

        JSONObject params = new JSONObject();
        params.put("buyerId", buyerId);
        params.put("taskType", TASK_TYPE);
        params.put("IpAddr", ipAddress);
        params.put("WifiName", wifiName);
        params.put("onPickInfoList", array);

        HttpClient.getInstance().post("/app/order/confirmOrder", LOGIN_URL)
                .upJson(params.toString())
                .headers("app-system","android")
                .headers("app-site","2")
                .headers("app-version",APP_VERSION)
//                .headers("user-agent","Dart/2.13 (dart:io)")
                .headers("x-token",token)
                .params("id",taskId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            playMusic(R.raw.success,3000,2);
                            getShopTitle();
                        }catch (Exception e){

                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendLog("????????????ERR???"+response.getException());
                    }
                });
    }



    private void getShopTitle(){

        JSONObject p = new JSONObject();
        p.put("buyerId", buyerId);
        p.put("status", "?????????");

        JSONObject params = new JSONObject();
        params.put("pageSize", 16);
        params.put("time", null);
        params.put("queryString", p);

        HttpClient.getInstance().post("/app/order/getAllOrderByStatus", LOGIN_URL)
                .upJson(params.toString())
                .headers("app-system","android")
                .headers("app-site","2")
                .headers("app-version",APP_VERSION)
//                .headers("user-agent","Dart/2.13 (dart:io)")
                .headers("x-token",token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject obj = JSONObject.parseObject(response.body());
                            JSONArray arr = obj.getJSONArray("data");
                            if(arr.size() == 0){
                                sendLog("??????????????????~");
                            }else {
                                String title = arr.getJSONObject(0).getString("keyword");
                                sendLog("????????????"+title);
                            }
                        }catch (Exception e){

                        }
                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendLog("????????????ERR???"+response.getException());
                    }
                });
    }




    /**
     * ???????????????????????????????????????????????????????????????????????????
     */
    public void jieDan(){

        //??????????????????10?????????????????????????????????????????????10??????
        Calendar calendar = Calendar.getInstance();
        if (calendar.after(startDate)){
            //??????????????????
            startDate = calendar;
            startDate.add(Calendar.MINUTE,JIEDAN_DATE);

            if(isAuth){  //????????????
                if(tbIndex >= buyerNumList.size()){
                    tbIndex = 0;
                }
                tbName = buyerNumList.get(tbIndex).getName();
                tbId = buyerNumList.get(tbIndex).getId();
                tbIndex++;
                setDefaultFlag();
            }else {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startTask();
                    }
                }, 4000);
            }
            return;
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getTask();
            }
        }, minPl);

    }





    /**
     * ????????????
     */
    public void stop(){
        OkGo.getInstance().cancelAll();
        //Handler????????????????????????removeCallbacksAndMessages?????????Message???Runnable
        mHandler.removeCallbacksAndMessages(null);
        sendLog("???????????????");
    }



    public void getPtAddress(){

        HttpClient.getInstance().get("/ptVersion/checkUpdate","http://47.94.255.103")
                .params("ptName","kaFeiBei")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject ptAddrObj = JSONObject.parseObject(response.body());

                            if(ptAddrObj == null){
                                Toast.makeText(MainActivity.this, "????????????????????????????????????", Toast.LENGTH_LONG).show();
                                return;
                            }

                            LOGIN_URL = ptAddrObj.getString("ptUrl");
                            DOWNLOAD = ptAddrObj.getString("apkDownload");
                            BROW_OPEN = ptAddrObj.getString("openUrl");

                            String[] version = ptAddrObj.getString("apkVersion").split(":");
                            APP_VERSION = version[0];
                            APP_VERSION_DETAIL = version[1];
                            JIEDAN_DATE = Integer.valueOf(version[2]);
                            TASK_TYPE = Integer.valueOf(version[3]);

                            minPl = Integer.parseInt(ptAddrObj.getString("pinLv"));

                            //????????????
                            String[] gongGao = ptAddrObj.getString("ptAnnoun").split(";");
                            announcementDialog(gongGao);

                        }catch (Exception e){
                            sendLog("???????????????"+e.getMessage());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        sendLog("????????????????????????~");
                    }
                });
    }


    /**
     * ???????????????????????????
     * @param voiceResId ????????????
     * @param milliseconds ????????????????????????
     */
    private void playMusic(int voiceResId, long milliseconds,int total){
        count = total;//?????????????????????
        //????????????
        MediaPlayer player = MediaPlayer.create(MainActivity.this, voiceResId);
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //??????????????????
                if(count != 0){
                    player.start();
                    count --;
                }
            }
        });

        //??????
        Vibrator vib = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
        //??????????????????
        vib.vibrate(milliseconds);
    }



    /**
     * ????????????
     * @param log
     */
    public void sendLog(String log){
        scrollToTvLog();
        if(tvLog.getLineCount() > 40){
            tvLog.setText("");
        }
        tvLog.append(new SimpleDateFormat("HH:mm:ss").format(new Date()) + ": "+log+"\n");
    }


    /**
     * ??????????????????
     */

    public void ignoreBatteryOptimization() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        boolean hasIgnored = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hasIgnored = powerManager.isIgnoringBatteryOptimizations(getPackageName());
            //  ????????????APP??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if(!hasIgnored) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:"+getPackageName()));
                startActivity(intent);
            }
        }


    }


    private void openNotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //???????????????????????????????????????
            NotificationSetUtil.OpenNotificationSetting(this);
        }
    }



    //????????????
    private void requestSettingCanDrawOverlays() {
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.O) {//8.0??????
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivityForResult(intent, 1);
        } else if (sdkInt >= Build.VERSION_CODES.M) {//6.0-8.0
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        } else {//4.4-6.0??????
            //???????????????
        }
    }




    //?????????????????????????????????   context???????????????Activity.??????tiis
    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getRandomMac(){

        String SEPARATOR_OF_MAC = ":";

        Random random = new Random();
        String[] mac = {
                String.format("%02x", 0x52),
                String.format("%02x", 0x54),
                String.format("%02x", 0x00),
                String.format("%02x", random.nextInt(0xff)),
                String.format("%02x", random.nextInt(0xff)),
                String.format("%02x", random.nextInt(0xff))
        };

        return String.join(SEPARATOR_OF_MAC, mac);
    }




    /**
     * ??????????????????
     */
    private void saveUserInfo(String username,String password,String yj1,String uuid,String imel){

        userInfo = getSharedPreferences("userData", MODE_PRIVATE);
        SharedPreferences.Editor editor = userInfo.edit();//??????Editor
        //??????Editor?????????????????????????????????
        editor.putString("username",username);
        editor.putString("password", password);
        editor.putString("yj1", yj1);
        editor.putString("uuid", uuid);
        editor.putString("imel", imel);
        editor.commit();//????????????

    }



    /**
     * ????????????????????????
     */
    @SuppressLint("WrongConstant")
    protected void receiveSuccess(String yj){
        //???????????????id????????????
        String channelId = "kfbSuccess";
        //??????????????????????????????
        String channelName = "???????????????????????????";
        //???????????????????????????????????????????????????????????????
        int importance = NotificationManager.IMPORTANCE_HIGH;

        // 2. ??????????????????????????????
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // 3. ??????NotificationChannel(???????????????channelId?????????????????????channelId????????????????????????????????????????????????)
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId,channelName, importance);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);
        }
        //???????????????????????????Activity
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        // 1. ??????????????????(????????????channelId)
        Notification notification = new NotificationCompat.Builder(this,channelId)
                .setContentTitle("????????????????????????")
                .setContentText("??????:"+yj)
                .setSmallIcon(R.mipmap.index)
                .setContentIntent(pendingIntent)//??????????????????Activity
                .setPriority(NotificationCompat.PRIORITY_MAX) //?????????????????????????????????
                .setCategory(Notification.CATEGORY_TRANSPORT) //??????????????????
                .setVisibility(Notification.VISIBILITY_PUBLIC)  //????????????????????????????????????????????????
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.index))   //???????????????
                .build();

        // 4. ????????????
        notificationManager.notify(2, notification);
    }


    public void onResume() {
        super.onResume();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //???????????????id????????? (??????????????????Context????????????Notification)
        notificationManager.cancel(2);
        //??????????????????
        //notificationManager.cancelAll();

    }




    /**
     * ??????????????????
     */
    private void getUserInfo(){
        userInfo = getSharedPreferences("userData", MODE_PRIVATE);
        String username = userInfo.getString("username", null);//??????username
        String passwrod = userInfo.getString("password", null);//??????password
        String yj1 = userInfo.getString("yj1",null);
        uuid = userInfo.getString("uuid",null);
        imel = userInfo.getString("imel",null);

        if(username!=null && passwrod!=null){
            etUname.setText(username);
            etPaw.setText(passwrod);
            etYj1.setText(yj1);
        }
    }


    public void scrollToTvLog(){
        int tvHeight = tvLog.getHeight();
        int tvHeight2 = getTextViewHeight(tvLog);
        if(tvHeight2>tvHeight){
            tvLog.scrollTo(0,tvHeight2-tvLog.getHeight());
        }
    }

    private int getTextViewHeight(TextView textView) {
        Layout layout = textView.getLayout();
        int desired = layout.getLineTop(textView.getLineCount());
        int padding = textView.getCompoundPaddingTop() +
                textView.getCompoundPaddingBottom();
        return desired + padding;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //???????????????????????? ?????????????????????????????????
        dialog.dismiss();

    }

}