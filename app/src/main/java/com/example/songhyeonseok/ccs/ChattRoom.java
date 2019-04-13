package com.example.songhyeonseok.ccs;//만들어진 채팅방을 나타내는 액티비티. 채팅방 내 모든 기능이 여기서 작성되었다.

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewGroupCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class ChattRoom extends BaseActivity {
    final int SET_PEN_MODE = 345;
    final int SET_ERASER_MODE = 456;
    final int REQ_CODE_SELECT_IMAGE = 100;
    final protected int BORDER_SIZE_BIG = 21;
    final protected int BORDER_SIZE_MID = 22;
    final protected int BORDER_SIZE_SMALL = 23;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private StorageReference mStorageRef;
    String MyUID,MyName;
    float width_coeffi, height_coeffi;
    boolean noBody;
    LinearLayout grandParent,coverArea;
    FrameLayout parent,rootCover;
    View chattView;
    ListView chattDatas;
    Button chattSend;
    EditText chattInput;
    ArrayAdapter adapter;
    ImageView l1;
    TextView title;
    MyView mv;
    Bitmap bitmap;
    String back_key = "no1";
    ToggleButton er_btn;
    Button colorbtn,clearbtn,chattwinbtn,bordersetbtn,picturebtn;
    Animation animAlpha,animAlpha_gone;
    Animation animToTop;
    String roomKey;
    PopupMenu p,p2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paintingroom);
        setGlobalFont((ViewGroup)findViewById(android.R.id.content),Global_FontSize);
        //--------------------------------------------------------------------------------------------------
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Rooms");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        MyUID = mFirebaseUser.getUid();
        MyName = mFirebaseUser.getDisplayName();

        WindowManager winM = (WindowManager) getSystemService(WINDOW_SERVICE);
        title = (TextView)findViewById(R.id.title_chattroom);
        er_btn = (ToggleButton)findViewById(R.id.eraserModeBtn);
        colorbtn = (Button)findViewById(R.id.choose_color);
        clearbtn = (Button)findViewById(R.id.clean_all);
        chattwinbtn = (Button)findViewById(R.id.chatting_window);
        bordersetbtn = (Button)findViewById(R.id.border_size);
        picturebtn = (Button)findViewById(R.id.picture_set);

        animAlpha= AnimationUtils.loadAnimation(this,R.anim.anim_alpha);
        animAlpha_gone = AnimationUtils.loadAnimation(this,R.anim.anim_alpha_gone);
        animToTop = AnimationUtils.loadAnimation(this,R.anim.anim_translate_to_top);

        title.setText(""+getIntent().getStringExtra("titlename"));
        roomKey = getIntent().getStringExtra("roomkey");
        Log.d("keys",""+roomKey+" - "+getIntent().getStringExtra("titlename"));
        Display display = winM.getDefaultDisplay();
        Point point = new Point();

        display.getSize(point);

        width_coeffi = (float) (point.x / 720.0); //화면 계수(기본 가로화면크기 720, 이 기기의 화면크기를 기본화면크기로 나눈것)생성
        height_coeffi = (float) (point.y / 1280.0);// 기본 세로화면크기 1280
        //--------------------------------------------------------------------------------------------------
        noBody = true;//테스트용 라인

        rootCover = (FrameLayout)findViewById(R.id.rootCover);
        grandParent = (LinearLayout)findViewById(R.id.grandparent);
        parent = (FrameLayout) findViewById(R.id.back_canv);
        coverArea = (LinearLayout)findViewById(R.id.loading_cover_paintRoom);
        l1 = (ImageView) findViewById(R.id.canvas1);



        mv = new MyView(this, width_coeffi, height_coeffi);
        parent.addView(mv);
        createChattWin();
        setBtns();
        runListener();
        startPermissionReq();


    }//onCreate의 끝


    public void createChattWin(){
        chattView = (View)getLayoutInflater().inflate(R.layout.chattwindow,null);
        chattDatas = (ListView)chattView.findViewById(R.id.chatt_datas);
        chattSend = (Button)chattView.findViewById(R.id.chatt_send);
        chattInput = (EditText)chattView.findViewById(R.id.chatt_input);
        parent.addView(chattView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        chattDatas.setAdapter(adapter);


        chattSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chattInput.getText().toString().equals("COM_CLEAN")){
                    databaseReference.child(roomKey).child("Chatting").removeValue();
                    adapter.clear();
                    chattDatas.setAdapter(adapter);
                    chattInput.setText("");
                    return;
                }
                ChatData cd = new ChatData(MyName,chattInput.getText().toString());
                databaseReference.child(roomKey).child("Chatting").push().setValue(cd);
                chattInput.setText("");
            }
        });

        databaseReference.child(roomKey).child("Chatting").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatData tmp = dataSnapshot.getValue(ChatData.class);
                adapter.add(tmp.getUserName()+ " : "+tmp.getMessage());
                chattDatas.smoothScrollByOffset(adapter.getCount());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {            }
            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });


        chattDatas.smoothScrollByOffset(adapter.getCount());
        chattView.setVisibility(View.GONE);

    }





    public void setBtns(){
        View.OnTouchListener btn_touch_listen = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN) {
                    switch (v.getId()) {
                        case R.id.picture_set:
                            v.setBackgroundResource(R.drawable.photo_pressed);
                            break;
                        case R.id.chatting_window:
                            v.setBackgroundResource(R.drawable.chatting_pressed);
                            break;
                        case R.id.clean_all:
                            v.setBackgroundResource(R.drawable.delete_pressed);
                            break;
                        case R.id.border_size:
                            v.setBackgroundResource(R.drawable.pencil_pressed);
                            break;
                        case R.id.choose_color:
                            v.setBackgroundResource(R.drawable.color2_pressed);
                            break;
                    }
                }else if(event.getAction()==MotionEvent.ACTION_UP) {
                    switch (v.getId()) {
                        case R.id.picture_set:
                            v.setBackgroundResource(R.drawable.photo);

                            Intent intent = new Intent(Intent.ACTION_PICK);

                            intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);

                            intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
                            break;
                        case R.id.chatting_window:
                            v.setBackgroundResource(R.drawable.chatting);

                            if (chattView.getVisibility() == View.VISIBLE) {
                                chattView.startAnimation(animAlpha_gone);
                                chattView.setVisibility(View.GONE);
                            } else {
                                chattDatas.smoothScrollByOffset(adapter.getCount());
                                chattView.setVisibility(View.VISIBLE);
                                chattView.startAnimation(animAlpha);
                            }
                            break;
                        case R.id.clean_all:
                            v.setBackgroundResource(R.drawable.delete);

                            mv.setScreenClear();
                            databaseReference.child(roomKey).child("cleanSignal").push().setValue(MyUID);
                            databaseReference.child(roomKey).child("Borders").removeValue();
                            databaseReference.child(roomKey).child("cleanSignal").removeValue();
                            break;
                        case R.id.border_size:
                            v.setBackgroundResource(R.drawable.pencil);

                            p2 = new PopupMenu(getApplicationContext(), v);
                            getMenuInflater().inflate(R.menu.menu_border, p2.getMenu());
                            p2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getTitle().toString()) {
                                        case "굵게":
                                            mv.setBorder(BORDER_SIZE_BIG);
                                            break;
                                        case "중간":
                                            mv.setBorder(BORDER_SIZE_MID);
                                            break;
                                        case "가늘게":
                                            mv.setBorder(BORDER_SIZE_SMALL);
                                            break;
                                    }
                                    return false;
                                }

                            });
                            p2.show();
                            break;
                        case R.id.choose_color:
                            v.setBackgroundResource(R.drawable.color2);
                            p = new PopupMenu(getApplicationContext(), v);
                            getMenuInflater().inflate(R.menu.menu_color, p.getMenu());
                            p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getTitle().toString()) {
                                        case "검은색":
                                            mv.setColor("#FFFFFF");
                                            break;
                                        case "흰색":
                                            mv.setColor("WHITE");
                                            break;
                                        case "빨강색":
                                            mv.setColor("#9A0023");
                                            break;
                                        case "파랑색":
                                            mv.setColor("#0003C4");
                                            break;
                                        case "초록색":
                                            mv.setColor("#46875E");
                                            break;
                                    }
                                    return false;

                                }
                            });
                            p.show();
                            break;
                    }
                }

                return false;
            }
        };

        bordersetbtn.setOnTouchListener(btn_touch_listen);
        chattwinbtn.setOnTouchListener(btn_touch_listen);
        clearbtn.setOnTouchListener(btn_touch_listen);;
        colorbtn.setOnTouchListener(btn_touch_listen);
        picturebtn.setOnTouchListener(btn_touch_listen);

        databaseReference.child(roomKey).child("cleanSignal").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String tmp = dataSnapshot.getValue().toString();
                if(!tmp.equals(MyUID)) {
                    mv.setScreenClear();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(chattView.getVisibility() == View.VISIBLE){
            chattView.startAnimation(animAlpha_gone);
            chattView.setVisibility(View.GONE);
        }else{
            super.onBackPressed();
        }
    }

    public void startPermissionReq() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //Manifest.permission.READ_CALENDAR이 접근 승낙 상태 일때
        } else {
            //Manifest.permission.READ_CALENDAR이 접근 거절 상태 일때
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //사용자가 다시 보지 않기에 체크를 하지 않고, 권한 설정을 거절한 이력이 있는 경우
            } else {
                //사용자가 다시 보지 않기에 체크하고, 권한 설정을 거절한 이력이 있는 경우
            }

            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult){
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        //위 예시에서 requestPermission 메서드를 썼을시 , 마지막 매개변수에 0을 넣어 줬으므로, 매칭
        if(requestCode == 0){
            // requestPermission의 두번째 매개변수는 배열이므로 아이템이 여러개 있을 수 있기 때문에 결과를 배열로 받는다.
            // 해당 예시는 요청 퍼미션이 한개 이므로 i=0 만 호출한다.
            if(grantResult[0] == 0){
                //해당 권한이 승낙된 경우.
            }else{
                Toast.makeText(getApplicationContext(),"권한 거절로 인해 앱이 다운 될 수 있습니다.",Toast.LENGTH_SHORT).show();
                //해당 권한이 거절된 경우.
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_nav:
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        bitmap = null;
        Toast.makeText(getBaseContext(), "resultCode : " + resultCode, Toast.LENGTH_SHORT).show();
        if (requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                Uri uri = data.getData();
                String imagePath = getRealPathFromURI(uri);
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(imagePath);
                    BitmapFactory.Options bo = new BitmapFactory.Options();
                    bo.inSampleSize = 4;
                    bitmap = BitmapFactory.decodeFile(imagePath, bo);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
                int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                int exifDegree = exifOrientationToDegrees(exifOrientation);
                Matrix matrix = new Matrix();
                matrix.postRotate(exifDegree);

                Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                l1.setImageBitmap(resizedBitmap);
                sendImage(resizedBitmap);
            }

        }

    }

    void runListener() {
        er_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(er_btn.isChecked()){
                    mv.setPenOrEraser(SET_ERASER_MODE);
                    Log.d("지우개 모드 : ",er_btn.isChecked()+"");
                }else{
                    mv.setPenOrEraser(SET_PEN_MODE);
                    Log.d("지우개 모드 : ",er_btn.isChecked()+"");
                }
            }
        });

        databaseReference.child(roomKey).child("DrawRoomBacks").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getValue(String.class);
                setImage(key);
                Log.d("key : ", key);
                databaseReference.child(roomKey).child("DrawRoomBacks").removeValue();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void setImage(String key) {

        mStorageRef.child(roomKey).child("DrawRoomBacks").child("no1.jpg").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Use the bytes to display the image
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                l1.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("실패됨. : ", "BytesDownload");
            }
        });
        Log.d("셋 이미지 key : ", key);
    }

    void sendImage(Bitmap bitmap) {
        StorageReference riversRef = mStorageRef.child(roomKey).child("DrawRoomBacks").child(back_key + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = riversRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("업로드실패", "!");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressWarnings("VisibleForTests")
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d("URL : ", String.valueOf(downloadUrl));
                databaseReference.child(roomKey).child("DrawRoomBacks").push().setValue(back_key);
            }
        });
    }


    class MyView extends View {
        private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        private DatabaseReference databaseReference = firebaseDatabase.getReference().child("Rooms");
        private PaintData pd = new PaintData();
        private float wc, hc;
        private boolean eraseMode,other_eraseMode;

        float border = 10f;
        int paintColor = Color.BLACK;
        Paint paint = new Paint();
        Paint other_paint = new Paint();//자동으로 그려지는 선
        Paint canvasPaint;
        Canvas mcanvas;
        Bitmap bitmap;
        boolean init_flag;

        PorterDuffXfermode clear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);

        Path path = new Path();    // 자취를 저장할 객체, 내가 그리는 선
        Path other_path = new Path();//자동으로 그려지는 선

        public MyView(Context context, float w_coef, float h_coef) {
            super(context);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE); // 선이 그려지도록
            paint.setStrokeWidth(border);// 선의 굵기 지정
            paint.setColor(paintColor);
            paint.setStrokeJoin(Paint.Join.ROUND);

            other_paint.setAntiAlias(true);
            other_paint.setStyle(Paint.Style.STROKE); // 선이 그려지도록
            other_paint.setStrokeWidth(border);// 선의 굵기 지정
            other_paint.setColor(paintColor);
            other_paint.setStrokeJoin(Paint.Join.ROUND);

            eraseMode = false;
            other_eraseMode = false;

            wc = w_coef;
            hc = h_coef;

        }


        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
            mcanvas = new Canvas(bitmap);
            canvasPaint = new Paint(Paint.DITHER_FLAG);
            init_flag = false;

            databaseReference.child(roomKey).child("Borders").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    PaintData pd2 = dataSnapshot.getValue(PaintData.class);
                    float x1 = pd2.getX() * wc;
                    float y1 = pd2.getY() * hc;

                    if(!pd2.getAuthorUid().equals(MyUID)) {

                        other_paint.setStrokeWidth(pd2.getBorder());
                        other_paint.setColor(pd2.getColor());
                        if(pd2.isEraseMode()) {
                            setPenOrEraser_other(SET_ERASER_MODE);
                        }else{
                            setPenOrEraser_other(SET_PEN_MODE);
                        }

                        switch (pd2.getState()){
                            case "start":
                                other_path.moveTo(x1, y1); // 자취에 그리지 말고 위치만 이동해라
                                break;
                            case "moving":
                                if(other_eraseMode){
                                    mcanvas.drawPath(other_path,other_paint);
                                }
                                other_path.lineTo(x1, y1);
                                break;
                            case "end":
                                mcanvas.drawPath(other_path,other_paint);//mcanvas가 null인경우가 간헐적으로 생김. onSizeChanged 호출 문제인것으로 추정됨.
                                other_path.reset();
                                break;
                        }
                        invalidate();

                    }else if(!init_flag){
                        paint.setStrokeWidth(pd2.getBorder());
                        paint.setColor(pd2.getColor());
                        if (pd2.isEraseMode()) {
                            setPenOrEraser(SET_ERASER_MODE);
                        } else {
                            setPenOrEraser(SET_PEN_MODE);
                        }

                        switch (pd2.getState()) {
                            case "start":
                                path.moveTo(x1, y1); // 자취에 그리지 말고 위치만 이동해라
                                break;
                            case "moving":
                                if (eraseMode) {
                                    mcanvas.drawPath(path, paint);
                                }
                                path.lineTo(x1, y1);
                                break;
                            case "end":
                                mcanvas.drawPath(path, paint);//mcanvas가 null인경우가 간헐적으로 생김. onSizeChanged 호출 문제인것으로 추정됨.
                                path.reset();
                                break;
                        }
                        invalidate();


                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            databaseReference.child(roomKey).child("Borders").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    init_flag = true;//Loading Done.
                    coverArea.setVisibility(GONE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


        public void setPenOrEraser(int id){
            if(id == SET_PEN_MODE){
                eraseMode = false;
                paint.setXfermode(null);
                invalidate();
            }else if(id == SET_ERASER_MODE){
                eraseMode = true;
                paint.setXfermode(clear);
                Log.d("지우개 기능","작동");
                invalidate();
            }
        }

        public void setPenOrEraser_other(int id){
            if(id == SET_PEN_MODE){
                other_eraseMode = false;
                other_paint.setXfermode(null);
                invalidate();
            }else if(id == SET_ERASER_MODE){
                other_eraseMode = true;
                other_paint.setXfermode(clear);
                Log.d("지우개 기능","작동");
                invalidate();
            }
        }

        public void setScreenClear(){
            mcanvas.drawColor(0,PorterDuff.Mode.CLEAR);
            invalidate();
        }

        public void setBorder(int size){
            switch (size) {
                case BORDER_SIZE_SMALL:
                    border = 10f;
                    paint.setStrokeWidth(border);
                    break;
                case BORDER_SIZE_MID:
                    border = 30f;
                    paint.setStrokeWidth(border);
                    break;
                case BORDER_SIZE_BIG:
                    border = 50f;
                    paint.setStrokeWidth(border);
                    break;
            }
        }

        public void setColor(String newColor){
            invalidate();
            paintColor = Color.parseColor(newColor);
            paint.setColor(paintColor);
        }



        @Override
        protected void onDraw(Canvas canvas) { // 화면을 그려주는 메서드
            canvas.drawBitmap(bitmap,0,0,paint);
            canvas.drawBitmap(bitmap,0,0,other_paint);
            if(!eraseMode) {
                canvas.drawPath(path, paint);
            }
            if(!other_eraseMode){
                canvas.drawPath(other_path, other_paint);
            }
            // 저장된 path 를 그려라
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(x, y); // 자취에 그리지 말고 위치만 이동해라
                    pd.setState("start");
                    pd.setX(x / wc);
                    pd.setY(y / hc);
                    pd.setBorder(border);
                    pd.setEraseMode(eraseMode);
                    pd.setColor(paintColor);
                    pd.setAuthorUid(MyUID);
                    setPaint();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(eraseMode){
                        mcanvas.drawPath(path,paint);
                    }
                    path.lineTo(x, y); // 자취에 선을 그려라
                    pd.setX(x / wc);
                    pd.setY(y / hc);
                    pd.setBorder(border);
                    pd.setState("moving");
                    pd.setEraseMode(eraseMode);
                    pd.setColor(paintColor);
                    pd.setAuthorUid(MyUID);
                    setPaint();
                    break;
                case MotionEvent.ACTION_UP:
                    mcanvas.drawPath(path,paint);
                    path.reset();
                    pd.setX(x / wc);
                    pd.setY(y / hc);
                    pd.setBorder(border);
                    pd.setState("end");
                    pd.setEraseMode(eraseMode);
                    pd.setColor(paintColor);
                    pd.setAuthorUid(MyUID);
                    setPaint();
                    //databaseReference.child("DrawRoom").child("Borders").removeValue();
                    break;
            }

            invalidate(); // 화면을 다시그려라

            return true;
        }


        void setPaint() {
            databaseReference.child(roomKey).child("Borders").push().setValue(pd);
        }


    }

}
