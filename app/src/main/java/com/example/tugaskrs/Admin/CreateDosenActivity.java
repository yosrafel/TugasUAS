package com.example.tugaskrs.Admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tugaskrs.Admin.Model.Dosen;
import com.example.tugaskrs.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.tugaskrs.DataDosenService;
import com.example.tugaskrs.RetrofitClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CreateDosenActivity extends AppCompatActivity {

    EditText edtNama, edtNidn, edtAlamat, edtEmail, edtGelar;
    Button btnSave;
    DataDosenService service;
    String idDosen = "", Img;
    ProgressDialog progressDialog;
    private Boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_dosen);
        this.setTitle("SI KRS - Hai Admin");
        edtNama = (EditText) findViewById(R.id.edtNamaDsn);
        edtNidn = (EditText) findViewById(R.id.edtNidn);
        edtAlamat = (EditText) findViewById(R.id.edtAlamatDsn);
        edtEmail = (EditText) findViewById(R.id.edtEmailDsn);
        edtGelar = (EditText) findViewById(R.id.edtGelar);

        checkUpdate();

        btnSave = (Button) findViewById(R.id.btnSimpanDosen);
        if (isUpdate) {
            btnSave.setText("Update");
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean isValid = true;

                if (edtNama.getText().toString().matches("")) {
                    edtNama.setError("Silahkan mengisi Nama Dosen");
                    isValid = false;
                }

                if (edtNidn.getText().toString().matches("")) {
                    edtNidn.setError("Silahkan mengisi NIM Dosen");
                    isValid = false;
                }

                if (edtAlamat.getText().toString().matches("")) {
                    edtAlamat.setError("Silahkan mengisi Alamat Dosen");
                    isValid = false;
                }

                if (edtEmail.getText().toString().matches("")) {
                    edtEmail.setError("Silahkan mengisi Email Dosen");
                    isValid = false;
                }

                if (edtGelar.getText().toString().matches("")) {
                    edtGelar.setError("Silahkan mengisi Gelar Dosen");
                }

                if (!isUpdate) {
                    if (isValid) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateDosenActivity.this);

                        builder.setMessage("Apakah anda yakin untuk menyimpan?")
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(CreateDosenActivity.this, "Batal Simpan", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestInsertDosen();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Toast.makeText(CreateDosenActivity.this, "Isi Data", Toast.LENGTH_LONG).show();
                    }
                } else if (isUpdate) {
                    if (isValid) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateDosenActivity.this);

                        builder.setMessage("Apakah anda yakin untuk menyimpan?")
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(CreateDosenActivity.this, "Batal Simpan", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestUpdateDosen();

                                    }
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Toast.makeText(CreateDosenActivity.this, "Silahkan Isi Data", Toast.LENGTH_LONG).show();
                    }
                }


            }
        });
    }

        private void requestInsertDosen() {
        edtNama = (EditText) findViewById(R.id.edtNamaDsn);
        edtNidn = (EditText) findViewById(R.id.edtNidn);
        edtAlamat = (EditText) findViewById(R.id.edtAlamatDsn);
        edtEmail = (EditText) findViewById(R.id.edtEmailDsn);
        edtGelar = (EditText) findViewById(R.id.edtGelar);
        service = RetrofitClient.getRetrofitInstance().create(DataDosenService.class);
        progressDialog = ProgressDialog.show(this, null, "Harap Tunggu...", true, false);

        Call<Dosen> call = service.insert_dosen(edtNama.getText().toString(), edtNidn.getText().toString(),
                edtAlamat.getText().toString(), edtEmail.getText().toString(), edtGelar.getText().toString(), "https://picsum.photos/200",
                "72170092");
        call.enqueue(new Callback<Dosen>() {
            @Override
            public void onResponse(Call<Dosen> call, Response<Dosen> response) {
                progressDialog.dismiss();
                Toast.makeText(CreateDosenActivity.this, "Berhasil Insert", Toast.LENGTH_LONG).show();
                Intent refresh = new Intent(CreateDosenActivity.this, RecyclerViewDaftarDosen.class);
                startActivity(refresh);
                finish();

            }

            @Override
            public void onFailure(Call<Dosen> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CreateDosenActivity.this, "Error..", Toast.LENGTH_SHORT);
            }
        });
    }

    private void requestUpdateDosen() {
        service = RetrofitClient.getRetrofitInstance().create(DataDosenService.class);
        progressDialog = ProgressDialog.show(CreateDosenActivity.this, null, "Harap Tunggu...", true, false);

        Call<Dosen> call = service.update_dosen(edtNama.getText().toString(),edtNidn.getText().toString(),
                edtAlamat.getText().toString(),edtEmail.getText().toString(),edtGelar.getText().toString(),"https://picsum.photos/200",
                "72170092");
        call.enqueue(new Callback<Dosen>() {
            @Override
            public void onResponse(Call<Dosen> call, Response<Dosen> response) {
                progressDialog.dismiss();
                Toast.makeText(CreateDosenActivity.this, "Berhasil Update", Toast.LENGTH_LONG).show();
                Intent refresh = new Intent(CreateDosenActivity.this, RecyclerViewDaftarDosen.class);
                startActivity(refresh);
                finish();

            }

            @Override
            public void onFailure(Call<Dosen> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(CreateDosenActivity.this, "Error cuy", Toast.LENGTH_SHORT);
            }
        });
    }

    void checkUpdate() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        isUpdate = extras.getBoolean("is_update");
        idDosen = extras.getString("id_dosen");
        edtNama.setText(extras.getString("nama"));
        edtNidn.setText(extras.getString("nidn"));
        edtAlamat.setText(extras.getString("alamat"));
        edtEmail.setText(extras.getString("email"));
        edtGelar.setText(extras.getString("gelar"));
    }
}