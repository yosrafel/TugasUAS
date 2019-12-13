package com.example.tugaskrs.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tugaskrs.Admin.Adapter.DosenAdapter;
import com.example.tugaskrs.Admin.Model.Dosen;
import com.example.tugaskrs.Dosen.DataDiri;
import com.example.tugaskrs.MainActivity;
import com.example.tugaskrs.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response; //menghasilkan data JSON
import com.example.tugaskrs.DataDosenService;
import com.example.tugaskrs.RetrofitClient;

public class RecyclerViewDaftarDosen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DosenAdapter dosenAdapter;
    private ArrayList<Dosen> dosenList;
    ProgressDialog progressDialog;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menucreate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu1){
            Intent intent = new Intent(RecyclerViewDaftarDosen.this,CreateDosenActivity.class);
            startActivity(intent);
        }
        return  true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_daftar_dosen);
        this.setTitle("SI KRS - Hai Admin");

        recyclerView = (RecyclerView)findViewById(R.id.rvDosen);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        DataDosenService service = RetrofitClient.getRetrofitInstance().create(DataDosenService.class);
        Call<ArrayList<Dosen>> call = service.getDosenAll("72170092");
        call.enqueue(new Callback<ArrayList<Dosen>>() {
            @Override
            public void onResponse(Call<ArrayList<Dosen>> call, Response<ArrayList<Dosen>> response) {
                progressDialog.dismiss();

                dosenList = response.body();
                dosenAdapter = new DosenAdapter(response.body());

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RecyclerViewDaftarDosen.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(dosenAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Dosen>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(RecyclerViewDaftarDosen.this,"Error",Toast.LENGTH_SHORT);
            }
        });

        registerForContextMenu(recyclerView);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Dosen dosen = dosenList.get(item.getGroupId());
        if (item.getTitle()== "Ubah Data Dosen"){
            Intent intent = new Intent(RecyclerViewDaftarDosen.this, CreateDosenActivity.class);
            intent.putExtra("id_dosen",dosen.getId()); //(key, value) -> ketika manggil Dosen harus sama
            intent.putExtra("nama",dosen.getNamaDosen());
            intent.putExtra("nidn",dosen.getNidn());
            intent.putExtra("alamat",dosen.getAlamat());
            intent.putExtra("email",dosen.getEmail());
            intent.putExtra("gelar",dosen.getGelar());
            intent.putExtra("foto",dosen.getFoto());
            intent.putExtra("is_update",true);
            startActivity(intent);

        }else if(item.getTitle() == "Hapus Data Dosen"){

            progressDialog = new ProgressDialog(RecyclerViewDaftarDosen.this);
            progressDialog.show();

            DataDosenService service = RetrofitClient.getRetrofitInstance().create(DataDosenService.class);
            Call<Dosen> call = service.delete_dosen(
                    dosen.getId(), "72170092");
            call.enqueue(new Callback<Dosen>() {
                @Override
                public void onResponse(Call<Dosen> call, Response<Dosen> response) {
                    progressDialog.dismiss();
                    Toast.makeText(RecyclerViewDaftarDosen.this,"Berhasil Menghapus",Toast.LENGTH_SHORT).show();
                    recreate();
                }

                @Override
                public void onFailure(Call<Dosen> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(RecyclerViewDaftarDosen.this,"Gagal Hapus",Toast.LENGTH_SHORT).show();
                }
            });
        }

        return super.onContextItemSelected(item);
    }
}
