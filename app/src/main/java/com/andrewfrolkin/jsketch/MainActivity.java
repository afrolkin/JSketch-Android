package com.andrewfrolkin.jsketch;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.andrewfrolkin.jsketch.models.Model;
import com.andrewfrolkin.jsketch.models.ShapeModel;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_STORAGE = 5;
    // Tools
    private ImageButton selectionButton;
    private ImageButton fillButton;
    private ImageButton lineButton;
    private ImageButton rectButton;
    private ImageButton circButton;
    private ImageButton eraserButton;

    // Line weight
    private ImageButton lineWeightThinButton;
    private ImageButton lineWeightMedButton;
    private ImageButton lineWeightLrgButton;

    // Colors
    private Button redButton;
    private Button blueButton;
    private Button greenButton;
    private int selectedButtonColor;
    private int defaultButtonColor;

    // selected tools
    private Model.SideBarTool sideBarTool;
    private Model.SideBarLineWidth sideBarLineWidth;
    private Model.SideBarColor sideBarColor;

    // getBitmap button
    private ImageButton shareButton;

    // canvas
    private CanvasView canvasView;

    // shapes on the canvas
    private List<ShapeModel> shapes;
    private int selectedShapeIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getReferences();
        setUpClickListeners();

        selectSelection();
        selectRed();
        selectThin();

        shapes = new ArrayList<>();

        canvasView.setShapes(shapes);
        canvasView.setActivity(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        shapes = canvasView.getShapes();

        savedInstanceState.putSerializable("color", sideBarColor);
        savedInstanceState.putSerializable("width", sideBarLineWidth);
        savedInstanceState.putSerializable("tool", sideBarTool);
        savedInstanceState.putInt("selected", selectedShapeIndex);
        savedInstanceState.putParcelableArrayList("shapes", ((ArrayList<ShapeModel>) shapes));
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        sideBarTool = (Model.SideBarTool) savedInstanceState.getSerializable("tool");
        sideBarLineWidth = (Model.SideBarLineWidth) savedInstanceState.getSerializable("width");
        sideBarColor = (Model.SideBarColor) savedInstanceState.getSerializable("color");
        shapes = savedInstanceState.getParcelableArrayList("shapes");
        selectedShapeIndex = savedInstanceState.getInt("selected");
        canvasView.setShapes(shapes);
        canvasView.setSelectedShape(selectedShapeIndex);

        switch (sideBarColor) {
            case RED:
                selectRed();
                break;
            case BLUE:
                selectBlue();
                break;
            case GREEN:
                selectGreen();
                break;
            default:
                selectRed();
                break;
        }

        switch (sideBarLineWidth) {
            case THICK:
                selectLarge();
                break;
            case THIN:
                selectThin();
                break;
            case MEDIUM:
                selectMed();
                break;
            default:
                selectThin();
                break;
        }

        switch (sideBarTool) {
            case SELECTION:
                selectSelection();
                break;
            case ERASER:
                selectEraser();
                break;
            case RECT:
                selectRect();
                break;
            case CIRCLE:
                selectCirc();
                break;
            case LINE:
                selectLine();
                break;
            case FILL:
                selectFill();
                break;
            default:
                selectSelection();
                break;
        }
    }

    private void setUpClickListeners() {
        selectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSelection();
            }
        });
        fillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFill();
            }
        });
        lineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLine();
            }
        });
        rectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRect();
            }
        });
        circButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCirc();
            }
        });
        eraserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectEraser();
            }
        });
        eraserButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                canvasView.clearCanvas();
                selectEraser();
                return true;
            }
        });

        lineWeightThinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectThin();
            }
        });
        lineWeightLrgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLarge();
            }
        });
        lineWeightMedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMed();
            }
        });

        redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRed();
            }
        });
        blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBlue();
            }
        });
        greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGreen();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareCanvas();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case WRITE_EXTERNAL_STORAGE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    shareCanvas();
                }
                break;

            default:
                break;
        }
    }

    private void shareCanvas() {


        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
        } else {
            Bitmap image = canvasView.getBitmap();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "canvas");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values);

            OutputStream outstream;
            try {
                outstream = getContentResolver().openOutputStream(uri);
                image.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                outstream.close();
            } catch (Exception e) {
                System.err.println(e.toString());
            }

            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share Image"));        }

    }

    private void getReferences() {
        selectionButton = (ImageButton) findViewById(R.id.selection_button);
        fillButton = (ImageButton) findViewById(R.id.fill_button);
        lineButton = (ImageButton) findViewById(R.id.line_button);
        rectButton = (ImageButton) findViewById(R.id.rect_button);
        circButton = (ImageButton) findViewById(R.id.circle_button);
        eraserButton = (ImageButton) findViewById(R.id.eraser_button);

        lineWeightThinButton = (ImageButton) findViewById(R.id.line_thin);
        lineWeightMedButton = (ImageButton) findViewById(R.id.line_med);
        lineWeightLrgButton = (ImageButton) findViewById(R.id.line_lrg);

        redButton = (Button) findViewById(R.id.red_button);
        blueButton = (Button) findViewById(R.id.blue_button);
        greenButton = (Button) findViewById(R.id.green_button);

        selectedButtonColor = ContextCompat.getColor(this, R.color.buttonSelectedColor);
        defaultButtonColor = ContextCompat.getColor(this, R.color.buttonDefaultColor);

        canvasView = (CanvasView) findViewById(R.id.canvasView);

        shareButton = (ImageButton) findViewById(R.id.share_button);
    }

    private void selectRed() {
        redButton.setBackground(getResources().getDrawable(R.drawable.red_border, null));
        blueButton.setBackground(getResources().getDrawable(R.drawable.blue_button, null));
        greenButton.setBackground(getResources().getDrawable(R.drawable.green_button, null));
        sideBarColor = Model.SideBarColor.RED;
        updateCanvasViewColor();
    }

    private void selectGreen() {
        redButton.setBackground(getResources().getDrawable(R.drawable.red_button, null));
        blueButton.setBackground(getResources().getDrawable(R.drawable.blue_button, null));
        greenButton.setBackground(getResources().getDrawable(R.drawable.green_border, null));
        sideBarColor = Model.SideBarColor.GREEN;
        updateCanvasViewColor();
    }

    private void selectBlue() {
        redButton.setBackground(getResources().getDrawable(R.drawable.red_button, null));
        blueButton.setBackground(getResources().getDrawable(R.drawable.blue_border, null));
        greenButton.setBackground(getResources().getDrawable(R.drawable.green_button, null));
        sideBarColor = Model.SideBarColor.BLUE;
        updateCanvasViewColor();
    }

    private void selectSelection() {
        selectionButton.setBackgroundColor(selectedButtonColor);
        fillButton.setBackgroundColor(defaultButtonColor);
        lineButton.setBackgroundColor(defaultButtonColor);
        rectButton.setBackgroundColor(defaultButtonColor);
        circButton.setBackgroundColor(defaultButtonColor);
        eraserButton.setBackgroundColor(defaultButtonColor);
        sideBarTool = Model.SideBarTool.SELECTION;
        updateCanvasViewTool();
    }

    private void selectLine() {
        selectionButton.setBackgroundColor(defaultButtonColor);
        fillButton.setBackgroundColor(defaultButtonColor);
        lineButton.setBackgroundColor(selectedButtonColor);
        rectButton.setBackgroundColor(defaultButtonColor);
        circButton.setBackgroundColor(defaultButtonColor);
        eraserButton.setBackgroundColor(defaultButtonColor);
        sideBarTool = Model.SideBarTool.LINE;
        updateCanvasViewTool();
    }

    private void selectRect() {
        selectionButton.setBackgroundColor(defaultButtonColor);
        fillButton.setBackgroundColor(defaultButtonColor);
        lineButton.setBackgroundColor(defaultButtonColor);
        rectButton.setBackgroundColor(selectedButtonColor);
        circButton.setBackgroundColor(defaultButtonColor);
        eraserButton.setBackgroundColor(defaultButtonColor);
        sideBarTool = Model.SideBarTool.RECT;
        updateCanvasViewTool();
    }

    private void selectCirc() {
        selectionButton.setBackgroundColor(defaultButtonColor);
        fillButton.setBackgroundColor(defaultButtonColor);
        lineButton.setBackgroundColor(defaultButtonColor);
        rectButton.setBackgroundColor(defaultButtonColor);
        circButton.setBackgroundColor(selectedButtonColor);
        eraserButton.setBackgroundColor(defaultButtonColor);
        sideBarTool = Model.SideBarTool.CIRCLE;
        updateCanvasViewTool();
    }

    private void selectEraser() {
        selectionButton.setBackgroundColor(defaultButtonColor);
        fillButton.setBackgroundColor(defaultButtonColor);
        lineButton.setBackgroundColor(defaultButtonColor);
        rectButton.setBackgroundColor(defaultButtonColor);
        circButton.setBackgroundColor(defaultButtonColor);
        eraserButton.setBackgroundColor(selectedButtonColor);
        sideBarTool = Model.SideBarTool.ERASER;
        updateCanvasViewTool();
    }

    private void selectFill() {
        selectionButton.setBackgroundColor(defaultButtonColor);
        fillButton.setBackgroundColor(selectedButtonColor);
        lineButton.setBackgroundColor(defaultButtonColor);
        rectButton.setBackgroundColor(defaultButtonColor);
        circButton.setBackgroundColor(defaultButtonColor);
        eraserButton.setBackgroundColor(defaultButtonColor);
        sideBarTool = Model.SideBarTool.FILL;
        updateCanvasViewTool();
    }

    private void selectThin() {
        lineWeightThinButton.setBackgroundColor(selectedButtonColor);
        lineWeightLrgButton.setBackgroundColor(defaultButtonColor);
        lineWeightMedButton.setBackgroundColor(defaultButtonColor);
        sideBarLineWidth = Model.SideBarLineWidth.THIN;
        updateCanvasViewLineWidth();
    }

    private void selectMed() {
        lineWeightThinButton.setBackgroundColor(defaultButtonColor);
        lineWeightLrgButton.setBackgroundColor(defaultButtonColor);
        lineWeightMedButton.setBackgroundColor(selectedButtonColor);
        sideBarLineWidth = Model.SideBarLineWidth.MEDIUM;
        updateCanvasViewLineWidth();
    }

    private void selectLarge() {
        lineWeightThinButton.setBackgroundColor(defaultButtonColor);
        lineWeightLrgButton.setBackgroundColor(selectedButtonColor);
        lineWeightMedButton.setBackgroundColor(defaultButtonColor);
        sideBarLineWidth = Model.SideBarLineWidth.THICK;
        updateCanvasViewLineWidth();
    }

    public void selectLineWidth(Model.SideBarLineWidth width) {
        switch (width) {
            case THIN:
                selectThin();
                break;
            case MEDIUM:
                selectMed();
                break;
            case THICK:
                selectLarge();
                break;
            default:
                selectThin();
                break;
        }
    }

    public void selectColor(Model.SideBarColor col) {
        switch (col) {
            case RED:
                selectRed();
                break;
            case GREEN:
                selectGreen();
                break;
            case BLUE:
                selectBlue();
                break;
            default:
                selectRed();
                break;
        }
    }

    private void updateCanvasViewLineWidth() {
        canvasView.setSideBarLineWidth(sideBarLineWidth);
    }

    private void updateCanvasViewColor() {
        canvasView.setSideBarColor(sideBarColor);
    }

    private void updateCanvasViewTool() {
        canvasView.setSideBarTool(sideBarTool);
    }

    public void setSelectedShapeIndex(int selectedShapeIndex) {
        this.selectedShapeIndex = selectedShapeIndex;
    }
}
