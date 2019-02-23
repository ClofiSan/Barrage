package com.example.gats.barrage;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.gats.barrage.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SceneActivity extends AppCompatActivity implements View.OnClickListener{

    private ArFragment arFragment;
    private ViewRenderable testRenderable;
    private final String TAG = "SceneActivity";
    private Button InputActivityButton,ViewButton1,ViewButton2;
    private boolean hasFinishedLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);

        //初始化View
        InputActivityButton = (Button)findViewById(R.id.word_button);
        ViewButton1 = (Button)findViewById(R.id.view1);
        ViewButton2 = (Button)findViewById(R.id.view2);

        InputActivityButton.setOnClickListener(this);
        ViewButton1.setOnClickListener(this);
        ViewButton2.setOnClickListener(this);



        arFragment = (ArFragment) getSupportFragmentManager()
                .findFragmentById(R.id.ux_fragment);
        Log.d(TAG, "onCreate: init fragment");
        //实时更新
        arFragment.getArSceneView().getScene()
                .addOnUpdateListener((frameTime -> {
                    arFragment.onUpdate(frameTime);
                    // OnUpdate();
                }));

        //利用CompletableFuture 创造一个渲染线程
        CompletableFuture<ViewRenderable> wordRender =
                ViewRenderable
                        .builder()
                        .setView(this,R.layout.hello_sceneform)
                        .build();

        //Future的返回
        // When you build a Renderable, Sceneform loads its resources in the background while
        // returning a CompletableFuture. Call handle(), thenAccept(), or check isDone()
        // before calling get().
        CompletableFuture.allOf(
                wordRender
        ).handle(
                (notUsed,throwable) -> {

                    if (throwable != null){
                        Log.d(TAG, "onCreate: Unable to get Completable");
                        return null;
                    }
                    try{
                        testRenderable = wordRender.get();
                    }catch(InterruptedException|ExecutionException e){
                        Log.d(TAG, "onCreate: Unable to load render");
                    }
                    return null;
                });

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult,Plane plane,MotionEvent motionEvent)->{
                    //这个方法采用的是平面检测
                    //只能在检测到平面才能触发事件
                    if (testRenderable==null){
                        return;
                    }
                    Log.d(TAG, "onCreate: set tap on plane!");
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    TransformableNode render = new TransformableNode(arFragment.getTransformationSystem());
                    render.setParent(anchorNode);
                    render.setRenderable(testRenderable);
                    render.select();
                }
        );

    }

    @Override
    public void onClick(View v) {
        if (v==InputActivityButton){
            //这里还传递关键字
            Intent intentToInput = new Intent(SceneActivity.this,InputActivity.class);
            Log.d(TAG, "onClick: InputActivity Ready!");
            //startActivity(intentToInput);
            startActivityForResult(intentToInput,1);
            Log.d(TAG, "onClickEnd: Back to main from input!");
        }else if (v==ViewButton1){
            //开启新的渲染线程，默认状态下是第一种选择
        }else if (v==ViewButton2){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case 1://返回输入的字符串
                if (resultCode==RESULT_OK){
                    //将字符串塞入新的View然后进行渲染
                    String inputString = data.getStringExtra("input_word");
                    Log.d(TAG, "onActivityResult: Get input_word");


                }
        }

    }


    //让Scene上不断地更新frame

    private void onSingleTap(MotionEvent tap){
        //首先要判定模式
        //如果是自定义弹幕的话就需要等待装载

        //判定是否渲染完成
        if (!hasFinishedLoading){
            return;
        }
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame != null){
            Log.d(TAG, "onSingleTap: tap correct");
            BarrageShot(tap,frame);
        }
    }

    private void BarrageShot(MotionEvent tap, Frame frame) {
        //不需要放置在平面上，而是直接放在点击的地方就好
        if (tap != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING){
            Log.d(TAG, "BarrageShot: has set barrage,ready to shot");
            for (HitResult hit : frame.hitTest(tap)){
                //Trackable trackable = hit.getTrackable();
                Anchor anchor = hit.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());
                Node newBarrage = CreateNewBarrage();
                anchorNode.addChild(newBarrage);
            }
        }
    }

    private Node CreateNewBarrage(){
        //调取View文件然后改变其中的文字，并且载入全新的渲染线程
        //将渲染线程做Node返回
        Node newBarrage = new Node();
        TextView textView = createNewTextView(inputString);



        return newBarrage;
    }
    private TextView createNewTextView(String inputString){
        //动态创建一个新的TextView并且返回
        TextView textView = new TextView(this);
        ViewGroup.LayoutParams layoutParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        textView.setBackgroundColor(Color.WHITE);
        textView.setText(inputString);

        return textView;
    }
}
