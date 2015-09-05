package projects.jeancarlos.com.audiorecorder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private MediaRecorder mediaRecorder;
    private TextView statusTextView;
    private boolean isRecording;

    private ImageView imageView;
    final static int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusTextView = (TextView)findViewById(R.id.main_activity_textview_status);
        isRecording = false;

        imageView = (ImageView)findViewById(R.id.activity_main_imageview_photo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.action_switch_record:

                if(isRecording){
                    item.setTitle(R.string.rec);
                    stopRecording();
                    releaseRecording();
                    isRecording = false;
                    statusTextView.setText(R.string.ready);
                }
                else{
                    initMediaRecorder();
                    isRecording = true;
                    item.setTitle(R.string.stop);
                    statusTextView.setText(R.string.rec);
                    try {
                        startRecording();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.action_take_photo:

                final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(isActivityAvailable(intent)) {
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO);
                }
                else{
                    Toast.makeText(this,R.string.take_photo_error,Toast.LENGTH_SHORT).show();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initMediaRecorder(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); //Contenedor
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB); //
        final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        final String fileName = "voice_recording_" + System.currentTimeMillis()+".3gp";
        mediaRecorder.setOutputFile(path + "/" + fileName);
    }

    private void startRecording() throws IOException {
        mediaRecorder.prepare();
        mediaRecorder.start();
    }

    private void stopRecording(){
        mediaRecorder.stop();

    }
    private void releaseRecording(){
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
    }


    ////////////////////


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_TAKE_PHOTO && RESULT_OK == resultCode){
            final Bitmap photoBitmap = (Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(photoBitmap);
        }
    }

    private boolean isActivityAvailable(final Intent intent){
       return  intent.resolveActivity(getPackageManager())!= null;
    }
}
