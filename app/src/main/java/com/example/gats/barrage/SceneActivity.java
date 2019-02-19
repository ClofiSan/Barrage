package com.example.gats.barrage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.gats.barrage.R;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
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
    Intent intentToInput = new Intent(SceneActivity.this,InputActivity.class);

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

        //直接调用SceneForm中的现成的方法来监听扫描到的平面是否有被触碰
        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult,Plane plane,MotionEvent motionEvent)->{
                    if (testRenderable==null){
                        return;
                    }

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
            Log.d(TAG, "onClick: InputActivity Ready!");

            startActivity(intentToInput);
            Log.d(TAG, "onClickEnd: Back to main from input!");
        }else if (v==ViewButton1){

        }else if (v==ViewButton2){
            
        }
    }
}
